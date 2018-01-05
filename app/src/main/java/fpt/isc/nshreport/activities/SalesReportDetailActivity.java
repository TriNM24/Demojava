package fpt.isc.nshreport.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.Fragment.BaseFragment;
import fpt.isc.nshreport.activities.Fragment.ReportDetailTab1Activity;
import fpt.isc.nshreport.activities.Fragment.ReportDetailTab2Activity;
import fpt.isc.nshreport.adapters.CustomViewPagerAdapter;
import fpt.isc.nshreport.adapters.DashboardAdapter;
import fpt.isc.nshreport.models.Dashboard;
import fpt.isc.nshreport.models.GasPumps;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.models.SalesReport;
import fpt.isc.nshreport.models.ShiftCloseOtherDetail;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.objectParse.OptionsList;
import fpt.isc.nshreport.models.objectParse.ReportDetail;
import fpt.isc.nshreport.models.objectParse.UserList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 31/05/2017.
 */

public class SalesReportDetailActivity extends BaseActivity {
    //Declares variables
    @BindView(R.id.txtSalesDateSalesReportDetail)
    TextView txtSalesDate;
    @BindView(R.id.txtCheckedManSalesReportDetail)
    TextView txtCheckedMan;
    @BindView(R.id.txtSalesStatusSalesReportDetail)
    TextView txtSalesStatus;
    /*@BindView(R.id.txtNoteSalesReportDetail)
    TextView txtNote;*/
    @BindView(R.id.txtLitreTotalSalesReportDetail)
    TextView txtLitreTotal;
    @BindView(R.id.txtTotalSalesReportDetail)
    TextView txtTotal;

    @BindView(R.id.fragment_content_action_layout)
    ViewPager pager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    private SalesReport mSalesReport = null;
    private ArrayList<GasPumps> headerDataList = null;
    private HashMap<GasPumps, ArrayList<PumpLinesAdd>> childDataList = null;

    /*@BindView(R.id.swipeContainer_report_detail)
    SwipeRefreshLayout swipeContainer;*/
    @BindView(R.id.progressBar_main)
    ProgressBar progressBar_main;

    //new code
    OptionsList options;
    UserList users;

    private BottomSheetDialog bottomSheetDialog;

    @BindView(R.id.layout_bottom)
    RelativeLayout layoutBottom;
    ListView lvDetailPrice;


    List<String> colorList = new ArrayList<>(Arrays.asList("#009900","#000099","#FF0000"));

    private List<String> menu = new ArrayList<>();
    private CustomViewPagerAdapter adapter;

    private List<BaseFragment> listBaseFragments;
    ReportDetailTab1Activity fragmentTab1;
    ReportDetailTab2Activity fragmentTab2;

    private boolean isLoaded = false;

    /**
     * Constructor
     */
    public SalesReportDetailActivity(){ super(R.layout.activity_sales_report_detail);}

    /**
     * Create a intent to call this activity from another activity
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context){
        Intent intent = new Intent(context, SalesReportDetailActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get data intent
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        //Get model object
        mSalesReport = (SalesReport) bundle.getSerializable("SALES_REPORT");
        if(mSalesReport == null){
            return;
        }
        //Set up support tool for App
        setupSupportForApp();
        //Create widgets
        createWidgets();
        //Create events
        createEvents();

        getData(mSalesReport.getId());
    }

    private void getData(int report_id)
    {
        isLoaded = false;
        /*swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeContainer.setRefreshing(true);
            }
        });*/
        progressBar_main.setAlpha(1.0f);
        User user = getNSHSharedPreferences().getUserLogin();
        mCompositeDisposable.add(
                NSHServer.getServer().statusList(user.getApi_token())
                        .subscribeOn(Schedulers.computation())
                        //.observeOn(Schedulers.computation())
                        .flatMap(optionsList -> {
                            this.options = optionsList;
                            return NSHServer.getServer().userList(user.getApi_token());
                        })
                        .flatMap(userList -> {
                            this.users = userList;
                            return NSHServer.getServer().reportDetail(user.getApi_token(),report_id);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse,this::handleError)
        );
    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(this, throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        //swipeContainer.setRefreshing(false);
        progressBar_main.setAlpha(0);
        isLoaded = true;
    }

    private void handleResponse(ReportDetail rs) {

        //get user approve
        String userApprove = "";
        for(int g =0;g<users.data.size();g++)
        {
            if(users.data.get(g).user_id == rs.data.user_report)
            {
                userApprove = users.data.get(g).full_name;
                break;
            }
        }
        //set data
        txtSalesDate.setText(AppUtilities.ConvertStringDateToStringDate(rs.data.report_date,"yyyy-MM-dd","dd/MM/yyyy"));
        txtCheckedMan.setText(userApprove);
        txtSalesStatus.setText(AppUtilities.getStatus(options.data,rs.data.status));
        //txtNote.setText("Ghi chú");
        txtLitreTotal.setText(AppUtilities.longTypeTextFormat(rs.data.total_liter) + " lít");
        txtTotal.setText(AppUtilities.longTypeTextFormat(rs.data.total_money) + " đ");


        //set data for detail bottom dialog
        List<Dashboard> list = new ArrayList<>();

        try {
            String name_detail ="", litre_detail="", money_detail="";
            //set headerDataList
            for(int i=0;i<rs.data.daily_report_details.fuel_filling_columns.size();i++)
            {
                //add data for detail bottom
                litre_detail = AppUtilities.longTypeTextFormat(rs.data.daily_report_details.fuel_filling_columns.get(i).total_liter)+ " lít";
                money_detail = "$ "+AppUtilities.longTypeTextFormat(rs.data.daily_report_details.fuel_filling_columns.get(i).total_money);
                name_detail = rs.data.daily_report_details.fuel_filling_columns.get(i).fuel_filling_column_code;
                //List<ReportDetail.data.daily_report_details.fuel_filling_columns.pump> pump = rs.data.daily_report_details.fuel_filling_columns.get(i).pumps;
                /*for(int j=0;j<pump.size();j++)
                {
                    //set name for datail bottom A92, A95
                    name_detail = pump.get(j).pump_name;

                }*/
                list.add(new Dashboard(name_detail,litre_detail,money_detail,colorList.get(i%3)));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        DashboardAdapter adapterDetail = new DashboardAdapter(getApplicationContext(),list);
        lvDetailPrice.setAdapter(adapterDetail);
        //Show data list
        showDataList(rs);
        //swipeContainer.setRefreshing(false);
        progressBar_main.setAlpha(0);

        isLoaded = true;
    }


    @Override
    protected void onResume(){
        super.onResume();
    }

    /**
     * Set up support tool for App
     */
    private void setupSupportForApp() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sales_report_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Create Widgets for App
     */
    private void createWidgets(){
        /*swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setEnabled(false);*/

        //bottom dialog
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottomsheetdialog_layout, null);
        lvDetailPrice = (ListView) bottomSheetView.findViewById(R.id.lv_detail);
        bottomSheetDialog = new BottomSheetDialog(SalesReportDetailActivity.this);
        bottomSheetDialog.setContentView(bottomSheetView);
        //layoutBottom = (RelativeLayout) findViewById(R.id.layout_bottom);
    }

    /**
     * Create Events for App
     */
    private void createEvents(){
        layoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoaded) bottomSheetDialog.show();
            }
        });
    }

    /**
     * Show data list for App
     */
    private void showDataList(ReportDetail data){

        listBaseFragments = new ArrayList<>();
        fragmentTab1 = ReportDetailTab1Activity.newInstance(data,mSalesReport.getId());
        listBaseFragments.add(fragmentTab1);
        menu.add("Xăng dầu");

        //get data
        List<ShiftCloseOtherDetail> listData = new ArrayList<>();
        ShiftCloseOtherDetail tmp;
        for (int j = 0; j < data.data.daily_report_details.products.size(); j++) {
            tmp = new ShiftCloseOtherDetail();
            tmp.setId(data.data.daily_report_details.products.get(j).daily_report_detail_id);
            tmp.setTitle(data.data.daily_report_details.products.get(j).product_name);

            //ton kho
            tmp.setOldLitre(String.valueOf(data.data.daily_report_details.products.get(j).number_report));

            //so ban duoc
            tmp.setNewLitre(String.valueOf(data.data.daily_report_details.products.get(j).sell_number));

            //ton kho con lai
            tmp.setNum(String.valueOf(data.data.daily_report_details.products.get(j).number_approve));
            if(data.data.status.equals("new"))
                tmp.setNum(String.valueOf(data.data.daily_report_details.products.get(j).number_report));

            tmp.setImage(data.data.daily_report_details.products.get(j).product_image);
            listData.add(tmp);
        }



        fragmentTab2 = ReportDetailTab2Activity.newInstance(listData);
        listBaseFragments.add(fragmentTab2);
        menu.add("Sản phẩm khác");

        adapter = new CustomViewPagerAdapter(getSupportFragmentManager(), listBaseFragments,menu,null);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_sales_report_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
           /* case R.id.action_update_sales_report_detail:
                updateReport();
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }

    /**
     * Update sales report
     */
    private void updateReport(){
        //Set data model
        //SalesReportUpdates saleUpdate = new SalesReportUpdates();
        //saleUpdate.setObjSalesReport(mSalesReport);
       // saleUpdate.setObjHeaderDataList(headerDataList);
        //saleUpdate.setObjChildDataList(childDataList);

        //crate hasmap can tranfer haha :((
        HashMap<String, ArrayList<PumpLinesAdd>> childDataList2 = new HashMap<>();
        for(int i=0;i<headerDataList.size();i++) {
            childDataList2.put(headerDataList.get(i).getName(), childDataList.get(headerDataList.get(i)));
        }
        //Set data Intent
        Intent intent = SalesReportUpdateActivity.buildIntent(SalesReportDetailActivity.this);
        Bundle bundle = new Bundle();
        bundle.putSerializable("SALES_REPORT_UPDATE", mSalesReport);
        bundle.putSerializable("SALES_REPORT_HEADER", headerDataList);
        bundle.putSerializable("SALES_REPORT_CHILD", childDataList2);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
