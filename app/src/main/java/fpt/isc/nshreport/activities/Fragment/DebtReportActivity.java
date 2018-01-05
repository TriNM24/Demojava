package fpt.isc.nshreport.activities.Fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.Dialog.ListImageFragment;
import fpt.isc.nshreport.adapters.DebtReportAdapter;
import fpt.isc.nshreport.models.DebtReport;
import fpt.isc.nshreport.models.ImageList;
import fpt.isc.nshreport.models.SpinnerObject;
import fpt.isc.nshreport.models.objectParse.DebtReportAPI;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.EndlessScrollListenerRecyclerView;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class DebtReportActivity extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView lvData;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe;

    @BindView(R.id.spOrderReport)
    Spinner spinShort;

    List<DebtReport> data = new ArrayList<>();
    DebtReportAdapter adapter;

    LinearLayoutManager linearLayoutManager;

    private boolean isFirstLoad = true;

    //detect is click or crolling
    GestureDetector mGestureDetector;

    private  final class onLoadingMoreDataTask implements EndlessScrollListenerRecyclerView.onActionListViewScroll {

        @Override
        public void onApiLoadMoreTask(int page) {

            SpinnerObject selectedItem = (SpinnerObject) spinShort.getSelectedItem();
            if(selectedItem != null)
            getData(page,selectedItem.getKey());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_debt_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.activity_title_debt_report);
        initView();
        initEvent();
        isFirstLoad = true;
        SpinnerObject selectedItem = (SpinnerObject) spinShort.getSelectedItem();
        if(selectedItem != null)
        getData(1,selectedItem.getKey());
    }
    private void initView()
    {
        swipe.setColorSchemeResources(R.color.colorOrange, R.color.colorGreen, R.color.colorBlue);

        List<SpinnerObject> dataspinner = new ArrayList<>();
        dataspinner.add(new SpinnerObject("bill_date","Ngày"));
        dataspinner.add(new SpinnerObject("amount","Tổng tiền"));

        ArrayAdapter<SpinnerObject> adapterSpinner = new ArrayAdapter<>(getContext(), R.layout.textview_spinner, dataspinner);
        adapterSpinner.setDropDownViewResource(R.layout.row_one_spinner_item);

        spinShort.setAdapter(adapterSpinner);

        linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        lvData.setLayoutManager(linearLayoutManager);

    }
    private void initEvent()
    {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    SpinnerObject selectedItem = (SpinnerObject) spinShort.getSelectedItem();
                    if (selectedItem != null) {
                        data.clear();
                        getData(1, selectedItem.getKey());
                    }
                }catch(Exception e){}
            }
        });

        spinShort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!isFirstLoad) {
                    SpinnerObject selectedItem = (SpinnerObject) spinShort.getSelectedItem();
                    if (selectedItem != null) {
                        data.clear();
                        getData(1, selectedItem.getKey());
                    }
                }else
                {
                    isFirstLoad = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        lvData.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View childView = rv.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && mGestureDetector.onTouchEvent(e))
                {
                    int position = rv.getChildAdapterPosition(childView);
                    if(position != -1)
                    {
                        if(!data.get(position).isHeader())
                        {
                            List<ImageList> dataImage = new ArrayList<>();
                            ImageList tmp = new ImageList();
                            tmp.setName(data.get(position).getDateHour());
                            tmp.setLink(data.get(position).getImageReport());
                            dataImage.add(tmp);

                            //get date of report
                            String dateString = "";
                            int vt = position;
                            while(vt >=0 )
                            {
                                if(data.get(vt).isHeader())
                                {
                                    dateString = data.get(vt).getDate();
                                    break;
                                }
                                vt--;
                            }

                            String title = "Báo cáo " + dateString + " " + data.get(position).getDateHour();

                            showImageDetail(dataImage,title);
                            return true;
                        }
                    }
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public void showImageDetail(List<ImageList> data, String title)
    {
        ListImageFragment listImagesFragment = new ListImageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) data);
        bundle.putString("title",title);
        listImagesFragment.setArguments(bundle);
        listImagesFragment.setCancelable(true);
        // Show DialogFragment
        listImagesFragment.show(getFragmentManager(), "list_images_fragment");
    }

    private void getData(int page,String order)
    {
        swipe.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipe.setRefreshing(true);
            }
        });

        mCompositeDisposable.add(
                NSHServer.getServer().debtReports(NSHSharedPreferences.getToken(getContext()),page,order)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError));
    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        swipe.setRefreshing(false);
    }

    private void handleResponse(DebtReportAPI rs) {
        if(rs.status.equals("success") && rs.data.size() > 0)
        {
            DebtReport tmp;
            String tmpString;
            for(int i=0;i<rs.data.size();i++)
            {
                //add header
                tmp = new DebtReport();
                tmp.setHeader(true);

                tmpString = AppUtilities.ConvertStringDateToStringDate(rs.data.get(i).dateReport,
                        "yyyy-MM-dd HH:mm:ss","dd/MM/yyyy");
                tmp.setDate(tmpString);

                tmpString = AppUtilities.StringNumerTextFormat(rs.data.get(i).totalMoney);
                tmp.setTotalMoney(tmpString);
                data.add(tmp);

                //add detail for header
                for(int j=0;j<rs.data.get(i).detail.size();j++)
                {
                    tmp = new DebtReport();
                    tmp.setHeader(false);
                    tmp.setApproved(rs.data.get(i).detail.get(j).approved);
                    tmp.setDateHour(rs.data.get(i).detail.get(j).time);
                    tmp.setImageReport(rs.data.get(i).detail.get(j).image_bill);

                    tmpString = AppUtilities.longTypeTextFormat(rs.data.get(i).detail.get(j).money);
                    tmp.setMoneyPerDebt(tmpString);

                    tmp.setNote(rs.data.get(i).detail.get(j).note);

                    data.add(tmp);
                }

            }
            if(adapter == null) {
                adapter = new DebtReportAdapter(getActivity(), data);
                lvData.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
            lvData.clearOnScrollListeners();
            lvData.addOnScrollListener(new EndlessScrollListenerRecyclerView(
                    rs.current_page,rs.last_page,new onLoadingMoreDataTask(),linearLayoutManager));
        }
        swipe.setRefreshing(false);
    }
}
