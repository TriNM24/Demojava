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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.SalesReportAddActivity;
import fpt.isc.nshreport.activities.SalesReportDetailActivity;
import fpt.isc.nshreport.adapters.SalesReportAdapter;
import fpt.isc.nshreport.models.SalesReport;
import fpt.isc.nshreport.models.SpinnerObject;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.objectParse.OptionsList;
import fpt.isc.nshreport.models.objectParse.ReportList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.EndlessScrollListener;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class SalesReportActivity extends BaseFragment {
    //Declares Variables
    @BindView(R.id.spOrderSalesReport)
    Spinner spOrder;
    @BindView(R.id.txtLitreTotalSalesReport)
    TextView txtLitre;
    @BindView(R.id.txtTotalSalesReport)
    TextView txtTotal;
    @BindView(R.id.lvSalesReport)
    ListView lvSalesReport;
    //ListView lvDetailPrice;
    @BindView(R.id.swipe_layout_sales_report)
    SwipeRefreshLayout swipeLayout;
    private ArrayList<SalesReport> salesReportsList;
    private ArrayList<SalesReport> resultList;
    private SalesReportAdapter adapter;
    private AppUtilities appUtilities = new AppUtilities();
    private int spPosition = 0;
    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    //new code
    OptionsList options;
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
        getActivity().setTitle(R.string.activity_title_sales_report);
        setHasOptionsMenu(true);
        user  = NSHSharedPreferences.getInstance(getContext()).getUserLogin();
        //Create widgets
        createWidgets();
        //Create events
        createEvents();
        getDataFist(1);
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

    private void getDataFist(int num)
    {
        showComponentClickable(false);

        lvSalesReport.setEnabled(false);
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeLayout.setRefreshing(true);
            }
        });
        //get statusoption first
        mCompositeDisposable.add(
                NSHServer.getServer().statusList(user.getApi_token())
                .subscribeOn(Schedulers.computation())
                .flatMap(optionsList -> {
                    this.options = optionsList;
                    return NSHServer.getServer().reportList(user.getApi_token(),num,"date","","close");
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse,this::handleError)
        );

    }
    private void getData(int num,String order_by,String status)
    {
        showComponentClickable(false);
        lvSalesReport.setEnabled(false);
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeLayout.setRefreshing(true);
            }
        });
        //get statusoption first
        mCompositeDisposable.add(
                NSHServer.getServer().reportList(user.getApi_token(),num,order_by,status,"close")
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseReload,this::handleError)
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
        getData(page,order_by,status);
    }



    private void handleError(Throwable throwable) {
        //Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        swipeLayout.setRefreshing(false);
        lvSalesReport.setEnabled(true);
        showComponentClickable(true);
    }

    private void handleResponseReload(ReportList reportList) {
        if(resultList == null) {
            resultList = new ArrayList<>();
        }
        //set data for list
        //long sl = 0,total=0;
        for(int i=0;i<reportList.data.size();i++)
        {
            sl += Long.valueOf(reportList.data.get(i).number_report_total);
            total += Long.valueOf(reportList.data.get(i).amount_report_total);

            SalesReport salesReport = new SalesReport();
            salesReport.setId(reportList.data.get(i).daily_report_header_id);
            salesReport.setSalesDate(AppUtilities.ConvertStringDateToStringDate(reportList.data.get(i).report_date,"yyyy-MM-dd","dd/MM/yyyy"));
            salesReport.setSalesLitre(Long.valueOf(reportList.data.get(i).number_report_total));
            salesReport.setSalesAmount(Long.valueOf(reportList.data.get(i).amount_report_total));
            salesReport.setSalesStatus(AppUtilities.getStatus(options.data,reportList.data.get(i).status));
            salesReport.setCheckedMan(reportList.data.get(i).user_report_name);
            salesReport.setSalesNote("note");

            resultList.add(salesReport);
        }
        //set total number litre and money
        txtLitre.setText(AppUtilities.longTypeTextFormat(sl) + " lít");
        txtTotal.setText(AppUtilities.longTypeTextFormat(total) + " đ");

        if(resultList != null)
        {
            if(adapter == null)
            {
                adapter = new SalesReportAdapter(getActivity(), R.layout.activity_sales_report_item, resultList);
                lvSalesReport.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
        }

        lvSalesReport.setOnScrollListener(new EndlessScrollListener(reportList.current_page,reportList.last_page,new onLoadingMoreDataTask()));
        swipeLayout.setRefreshing(false);
        lvSalesReport.setEnabled(true);
        showComponentClickable(true);
    }
    private void handleResponse(ReportList reportList) {
        if(resultList == null) {
            resultList = new ArrayList<>();
        }
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

        //set data for detail bottom dialog
        /*List<Dashboard> list = new ArrayList<>();
        list.add(new Dashboard("A95","11.000 lít","$ 11.000.000","#009900"));
        list.add(new Dashboard("A92","11.000 lít","$ 11.000.000","#000099"));
        list.add(new Dashboard("DO","11.000 lít","$ 11.000.000","#FF0000"));

        DashboardAdapter adapterDetail = new DashboardAdapter(getContext(),list);
        lvDetailPrice.setAdapter(adapterDetail);*/

        //set data for list
        for(int i=0;i<reportList.data.size();i++)
        {
            sl += Long.valueOf(reportList.data.get(i).number_report_total);
            total += Long.valueOf(reportList.data.get(i).amount_report_total);

            SalesReport salesReport = new SalesReport();
            salesReport.setId(reportList.data.get(i).daily_report_header_id);
            salesReport.setSalesDate(AppUtilities.ConvertStringDateToStringDate(reportList.data.get(i).report_date,"yyyy-MM-dd","dd/MM/yyyy"));
            salesReport.setSalesLitre(Long.valueOf(reportList.data.get(i).number_report_total));
            salesReport.setSalesAmount(Long.valueOf(reportList.data.get(i).amount_report_total));
            salesReport.setSalesStatus(AppUtilities.getStatus(options.data,reportList.data.get(i).status));
            salesReport.setCheckedMan(reportList.data.get(i).user_report_name);
            salesReport.setSalesNote("note");

            resultList.add(salesReport);
        }
        //set total number litre and money
        txtLitre.setText(AppUtilities.longTypeTextFormat(sl) + " lít");
        txtTotal.setText(AppUtilities.longTypeTextFormat(total) + " đ");

        if(resultList != null && resultList.size() > 0)
        {
            if(adapter == null)
            {
                adapter = new SalesReportAdapter(getActivity(), R.layout.activity_sales_report_item, resultList);
                lvSalesReport.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
        }

        lvSalesReport.setOnScrollListener(new EndlessScrollListener(reportList.current_page,reportList.last_page,new onLoadingMoreDataTask()));

        swipeLayout.setRefreshing(false);
        lvSalesReport.setEnabled(true);
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
    }

    /**
     * Create Events for App
     */
    private void createEvents(){
        //Select a order type
        spOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SpinnerObject tmp = (SpinnerObject)spOrder.getSelectedItem();
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
        lvSalesReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SalesReport salesReport = resultList.get(position);
                if(salesReport != null) {
                    Intent intent = SalesReportDetailActivity.buildIntent(getContext());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("SALES_REPORT", salesReport);
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
                }catch(Exception e){}
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
                startActivity(SalesReportAddActivity.buildIntent(getContext()));
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set event for spinner
     */
    private void spinnerEvents(){
        switch (spPosition){
            case 0:
                //resultList = sortBySalesDate();
                break;
            case 1:
                //resultList = sortBySalesLitre();
                break;
        }
    }

    /**
     * Set data fo App ArrayList<SalesReport> reportsList
     */
    private void setData(){
        if(resultList != null && resultList.size() > 0) {
            adapter = new SalesReportAdapter(getActivity(), R.layout.activity_sales_report_item, resultList);
            lvSalesReport.setAdapter(adapter);
        } else {
            adapter = null;
            lvSalesReport.setAdapter(adapter);
        }
    }

    /**
     * Set Litre and Amount Total for bottom text view
     * @param reportsList
     */
    /*private void setLitreAndMoneyTotal(ArrayList<SalesReport> reportsList){
        int litreTotal = 0;
        long amountTotal = 0;
        if(reportsList != null && reportsList.size() > 0){
            try {
                for (int i = 0; i < reportsList.size(); i++) {
                    SalesReport salesReport = reportsList.get(i);
                    litreTotal += salesReport.getSalesLitre();
                    amountTotal += salesReport.getSalesAmount();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        //Set text for text view
        txtLitre.setText(appUtilities.integerTypeTextFormat(litreTotal) + " lít");
        txtTotal.setText(appUtilities.longTypeTextFormat(amountTotal) + " đ");
    }*/

    /**
     * Sort data list by sales date - Descending
     * @return
     */
    private ArrayList<SalesReport> sortBySalesDate(){
        ArrayList<SalesReport> sortList = null;
        if(salesReportsList != null && salesReportsList.size() > 0) {
            sortList = salesReportsList;
            try {
                Collections.sort(sortList, new Comparator<SalesReport>() {
                    @Override
                    public int compare(SalesReport o1, SalesReport o2) {
                        Date a = appUtilities.convertStringToDate(o1.getSalesDate());
                        Date b = appUtilities.convertStringToDate(o2.getSalesDate());
                        if (a == null || b == null) {
                            return 0;
                        }
                        return b.compareTo(a);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return sortList;
    }

    /**
     * Sort data list by sales litre - Descending
     * @return
     */
    /*private ArrayList<SalesReport> sortBySalesLitre(){
        ArrayList<SalesReport> sortList = null;
        if(salesReportsList != null && salesReportsList.size() > 0) {
            sortList = salesReportsList;
            try {
                Collections.sort(sortList, new Comparator<SalesReport>() {
                    @Override
                    public int compare(SalesReport o1, SalesReport o2) {
                        Integer a = o1.getSalesLitre();
                        Integer b = o2.getSalesLitre();
                        if (a < 0 || b < 0) {
                            return 0;
                        }
                        return b.compareTo(a);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return sortList;
    }*/

}
