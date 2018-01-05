package fpt.isc.nshreport.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.ImportDetailAdapter;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackConfirmDialog;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.BaseResponse;
import fpt.isc.nshreport.models.objectParse.ImportDetail;
import fpt.isc.nshreport.models.objectParse.OptionsList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 31/05/2017.
 */

public class ImportDetailActivity extends BaseActivity {

    OptionsList options;
    private User user;
    private int objectID = 0;
    private boolean isApproved = false;


    @BindView(R.id.txt_bill)
    TextView txt_bill;
    @BindView(R.id.txt_date_create)
    TextView txt_date_create;
    @BindView(R.id.txt_status)
    TextView txt_status;
    @BindView(R.id.txt_date_import)
    TextView txt_date_import;
    @BindView(R.id.txtTotal)
    TextView txtTotal;
    @BindView(R.id.txtLitreTotal)
    TextView txtLitreTotal;

    @BindView(R.id.lvDetail)
    ListView lvDetail;
    @BindView(R.id.btn_approve)
    FloatingActionButton btn_approve;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeContainer;

    private ImportDetailAdapter adapter;
    private List<ImportDetail.data.inventory_workflow_detail> listObject = new ArrayList<>();

    private boolean isLoaded = false;

    /**
     * Constructor
     */
    public ImportDetailActivity(){ super(R.layout.activity_import_detail);}

    /**
     * Create a intent to call this activity from another activity
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context){
        Intent intent = new Intent(context, ImportDetailActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get data intent
        Bundle bundle = getIntent().getExtras();
        objectID = bundle.getInt("objectID");
        isApproved = bundle.getBoolean("isApproved",false);
        //Set up support tool for App
        setupSupportForApp();
        user = getNSHSharedPreferences().getUserLogin();
        //Create widgets
        createWidgets();
        //Create events
        createEvents();
        //update notification is read if start from notifcation
        updateIsRead();
        //get data
        getData();
        super.setupUI(findViewById(R.id.layout_root));
    }

    private void updateIsRead()
    {
        int notifi_id = getIntent().getIntExtra("notifi_id",0);
        if(notifi_id!=0)
        {
            mCompositeDisposable.add(
                    NSHServer.getServer().updateReadNotification(NSHSharedPreferences.getToken(this),notifi_id)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(this::handleResponseUpdateNotifi,this::handleError));
        }
    }

    private void handleResponseUpdateNotifi(BaseResponse baseResponse) {
    }

    public void showComponentClickable(boolean isEnable)
    {

        btn_approve.setEnabled(isEnable);
        if(isEnable) btn_approve.setAlpha(1f); else btn_approve.setAlpha(0.5f);
    }

    private void getData()
    {
        showComponentClickable(false);
        isLoaded = false;
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeContainer.setRefreshing(true);
            }
        });

        mCompositeDisposable.add(
                NSHServer.getServer().statusListImport(user.getApi_token())
                        .subscribeOn(Schedulers.computation())
                        .flatMap(optionsList -> {
                            this.options = optionsList;
                            return NSHServer.getServer().importDetail(user.getApi_token(),objectID);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse,this::handleError)
        );
    }

    private void approveImport()
    {
        mCompositeDisposable.add(
                NSHServer.getServer().approveImport(user.getApi_token(),objectID)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponseApprove,this::handleError)
        );
    }

    private void handleResponseApprove(BaseResponse baseResponse) {
        AppUtilities.showInform(ImportDetailActivity.this, "Thông báo",baseResponse.message + " " + baseResponse.error, "OK", SweetAlertDialog.SUCCESS_TYPE,new CallBackInformDialog() {
            @Override
            public void DiaglogPositive() {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }


    private void handleError(Throwable throwable) {
        //Toast.makeText(this, throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(), SweetAlertDialog.ERROR_TYPE);
        swipeContainer.setRefreshing(false);
        isLoaded = true;
        showComponentClickable(true);
    }

    private void handleResponse(ImportDetail rs) {


        txt_bill.setText(rs.data.voucher_code);
        txt_date_create.setText(AppUtilities.ConvertStringDateToStringDate(rs.data.date_created,"yyyy-MM-dd HH:mm:ss","dd/MM/yyyy"));
        txt_date_import.setText(AppUtilities.ConvertStringDateToStringDate(rs.data.date_recived,"yyyy-MM-dd HH:mm:ss","dd/MM/yyyy"));
        String status = "";
        for(int i=0;i<options.data.size();i++)
        {
            if(rs.data.status.equals(options.data.get(i).id))
            {
                status = options.data.get(i).name;
                break;
            }
        }
        txt_status.setText(status);
        txtTotal.setText(AppUtilities.longTypeTextFormat(rs.data.total_money));
        txtLitreTotal.setText(AppUtilities.longTypeTextFormat(rs.data.total_quantity));

        listObject.addAll(rs.data.inventory_workflow_detail);
        //Show data list
        if(listObject != null && listObject.size() > 0){
            if(adapter == null) {
                adapter = new ImportDetailAdapter(getApplicationContext(), listObject);
                lvDetail.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
        }
        swipeContainer.setRefreshing(false);
        isLoaded = true;
        showComponentClickable(true);
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

        if(!isApproved)
        {
            btn_approve.setVisibility(View.VISIBLE);
        }else
        {
            btn_approve.setVisibility(View.GONE);
        }

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setEnabled(false);
    }

    /**
     * Create Events for App
     */
    private void createEvents(){
        btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //approveImport();
                AppUtilities.showConfirm(ImportDetailActivity.this, "Xác nhận", "Duyệt phiếu nhập hàng", "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE,new CallBackConfirmDialog() {
                    @Override
                    public void DiaglogPositive() {
                        approveImport();
                    }

                    @Override
                    public void DiaglogNegative() {

                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!isApproved) {
            getMenuInflater().inflate(R.menu.menu_sales_report_add, menu);
        }
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
            case R.id.action_add_new_sales_report:
                //approveImport();
                if(isLoaded) {
                    AppUtilities.showConfirm(ImportDetailActivity.this, "Xác nhận", "Duyệt phiếu nhập hàng", "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE, new CallBackConfirmDialog() {
                        @Override
                        public void DiaglogPositive() {
                            approveImport();
                        }

                        @Override
                        public void DiaglogNegative() {

                        }
                    });
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }

}
