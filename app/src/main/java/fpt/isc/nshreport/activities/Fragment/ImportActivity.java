package fpt.isc.nshreport.activities.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.ImportDetailActivity;
import fpt.isc.nshreport.adapters.ImportAdapter;
import fpt.isc.nshreport.models.SpinnerObject;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.objectParse.ImportList;
import fpt.isc.nshreport.models.objectParse.OptionsList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.EndlessScrollListener;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class ImportActivity extends BaseFragment {
    //Declares Variables
    @BindView(R.id.spOrderSalesReport)
    Spinner spOrder;
    @BindView(R.id.txtLitreTotalSalesReport)
    TextView txtLitre;
    @BindView(R.id.txtTotalSalesReport)
    TextView txtTotal;
    @BindView(R.id.lvSalesReport)
    ListView lvImport;
    //ListView lvDetailPrice;
    @BindView(R.id.swipe_layout_sales_report)
    SwipeRefreshLayout swipeLayout;
    private List<ImportList.data> resultList = new ArrayList<>();
    private ImportAdapter adapter;
    private int spPosition = 0;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    //new code
    OptionsList options;

    //private BottomSheetDialog bottomSheetDialog;
    @BindView(R.id.layout_bottom)
     RelativeLayout layoutBottom;

    User user;
    boolean isLoadedFirst = false;

    //variables to save total money and litre
    long sl = 0,total=0;

    private  final class onLoadingMoreDataTask implements EndlessScrollListener.onActionListViewScroll {

        @Override
        public void onApiLoadMoreTask(int page) {
            reLoadData(page);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_sales_report, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.navigation_title_import);
        setHasOptionsMenu(true);
        user  = NSHSharedPreferences.getInstance(getContext()).getUserLogin();
        //Create widgets
        createWidgets();
        //Create events
        createEvents();
        //getData(1);

    }

    public void showComponentClickable(boolean isEnable)
    {

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(isEnable);
        }
        if(isEnable) radioGroup.setAlpha(1f); else radioGroup.setAlpha(0.5f);

        spOrder.setEnabled(isEnable);
        if(isEnable) spOrder.setAlpha(1f); else spOrder.setAlpha(0.5f);

        swipeLayout.setEnabled(isEnable);
    }

    private void getDataFirst(int num)
    {
        showComponentClickable(false);
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeLayout.setRefreshing(true);
            }
        });
        //get statusoption first
        mCompositeDisposable.add(
                NSHServer.getServer().statusListImport(user.getApi_token())
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .flatMap(optionsList -> {
                    this.options = optionsList;
                    return NSHServer.getServer().importList(user.getApi_token(),num,"date","");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,this::handleError)
        );
    }
    private void reLoadData(int page)
    {
        String order_by = "date";
        String status = "";
        int id = radioGroup.getCheckedRadioButtonId();
        if(id == R.id.radio_date)
        {
            order_by = "date";
        }else
        {
            order_by = "liter";
        }
        SpinnerObject tmp = (SpinnerObject)spOrder.getSelectedItem();
        if(!tmp.getKey().equals("-1"))
        {
            status = tmp.getKey();
        }
        //Toast.makeText(getContext(), order_by+" " + status, Toast.LENGTH_SHORT).show();
        getData(page,order_by,status);
    }
    private void getData(int num,String order_by,String status)
    {
        showComponentClickable(false);
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeLayout.setRefreshing(true);
            }
        });
        //get statusoption first
        mCompositeDisposable.add(
                NSHServer.getServer().importList(user.getApi_token(),num,order_by,status)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseReload,this::handleError)
        );

    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        swipeLayout.setRefreshing(false);
        showComponentClickable(true);
    }

    private void handleResponseReload(ImportList rs) {

        //set data for list
        //long sl = 0,total=0;
        for(int i=0;i<rs.data.size();i++)
        {
            sl += Long.valueOf(rs.data.get(i).total);
            total += Long.valueOf(rs.data.get(i).total_money);
        }
        //set total number litre and money
        txtLitre.setText(AppUtilities.longTypeTextFormat(sl) + " lít");
        txtTotal.setText(AppUtilities.longTypeTextFormat(total) + " đ");

        resultList.addAll(rs.data);

        if(resultList != null)
        {
            if(adapter == null)
            {
                adapter = new ImportAdapter(getContext(), resultList,options);
                lvImport.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
        }

        lvImport.setOnScrollListener(new EndlessScrollListener(rs.current_page,rs.last_page,new onLoadingMoreDataTask()));

        swipeLayout.setRefreshing(false);
        showComponentClickable(true);
    }

    private void handleResponse(ImportList rs) {

        //set data for spinner
        List<SpinnerObject> dataspinner = new ArrayList<>();
        dataspinner.add(new SpinnerObject("-1","Tất cả"));
        for(int i=0;i<options.data.size();i++)
        {
            dataspinner.add(new SpinnerObject(options.data.get(i).id,options.data.get(i).name));
        }
        ArrayAdapter<SpinnerObject> adapterSpinner = new ArrayAdapter<>(getContext(), R.layout.textview_spinner, dataspinner);
        adapterSpinner.setDropDownViewResource(R.layout.row_one_spinner_item);
        spOrder.setAdapter(adapterSpinner);

        /*//set data for detail bottom dialog
        List<Dashboard> list = new ArrayList<>();
        list.add(new Dashboard("A95","11.000 lít","$ 11.000.000","#009900"));
        list.add(new Dashboard("A92","11.000 lít","$ 11.000.000","#000099"));
        list.add(new Dashboard("DO","11.000 lít","$ 11.000.000","#FF0000"));

        DashboardAdapter adapterDetail = new DashboardAdapter(getContext(),list);
        lvDetailPrice.setAdapter(adapterDetail);*/

        //set data for list
        long sl = 0,total=0;
       for(int i=0;i<rs.data.size();i++)
        {
            sl += Long.valueOf(rs.data.get(i).total);
            total += Long.valueOf(rs.data.get(i).total_money);
        }
        //set total number litre and money
        txtLitre.setText(AppUtilities.longTypeTextFormat(sl) + " lít");
        txtTotal.setText(AppUtilities.longTypeTextFormat(total) + " đ");




        resultList.addAll(rs.data);

        if(resultList != null && resultList.size() > 0)
        {
            if(adapter == null)
            {
                adapter = new ImportAdapter(getContext(), resultList,options);
                lvImport.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
        }

        swipeLayout.setRefreshing(false);
        showComponentClickable(true);
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * Create Widgets for App
     */
    private void createWidgets(){
        swipeLayout.setColorSchemeResources(R.color.colorOrange, R.color.colorGreen, R.color.colorBlue);

        /*//bottom dialog
        View bottomSheetView = getLayoutInflater(savedInstanceState).inflate(R.layout.bottomsheetdialog_layout, null);
        lvDetailPrice = (ListView) bottomSheetView.findViewById(R.id.lv_detail);
        bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(bottomSheetView);*/
    }

    /**
     * Create Events for App
     */
    private void createEvents(){
        //Select a order type
        spOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(isLoadedFirst) {
                    resultList.clear();
                    sl = 0;
                    total = 0;
                    reLoadData(1);
                }else
                {
                    isLoadedFirst = true;
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Select a item list
        lvImport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImportList.data tmp = resultList.get(position);
                if(tmp != null) {
                    Intent intent = ImportDetailActivity.buildIntent(getContext());
                    Bundle bundle = new Bundle();
                    bundle.putInt("objectID", tmp.filling_station_inventory_workflow_header_id);
                    bundle.putBoolean("isApproved",tmp.status.equals("success")?true:false);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        //Refresh data
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    resultList.clear();
                    sl = 0;
                    total = 0;
                    reLoadData(1);
                }catch (Exception e){}
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                resultList.clear();
                sl = 0;
                total = 0;
                reLoadData(1);
            }
        });
        /*layoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.show();
            }
        });*/
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        /*menu.clear();
        inflater.inflate(R.menu.menu_sales_report, menu);
        inflater.inflate(R.menu.menu_notify, menu);*/
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Add new sales report
        switch(id)
        {
            case R.id.action_add_sales_report:
            {
                //startActivity(SalesReportAddActivity.buildIntent(getContext()));
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        resultList.clear();
        getDataFirst(1);
    }
}
