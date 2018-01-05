package fpt.isc.nshreport.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.Fragment.BaseFragment;
import fpt.isc.nshreport.activities.Fragment.ShiftAddTab1Activity;
import fpt.isc.nshreport.activities.Fragment.ShiftAddTab2Activity;
import fpt.isc.nshreport.adapters.CustomViewPagerAdapter;
import fpt.isc.nshreport.models.ShiftAddOtherDetail;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.objectParse.ShiftList;
import fpt.isc.nshreport.models.objectParse.ShiftOpen;
import fpt.isc.nshreport.models.objectParse.UserList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by PhamTruong on 25/05/2017.
 */

public class ShiftDetailActivity extends BaseActivity implements
        OnRequestPermissionsResultCallback,View.OnClickListener {

    private final int UPDATE_SHIFT = 123;
    private final int CHANGE_SHIFT = 124;
    private final int CREATE_SHIFT = 125;

    //Declares variables
    @BindView(R.id.txtReportDateSalesReportAdd)
    TextView txtDate;
    @BindView(R.id.txt_shift)
    TextView txtShift;
    @BindView(R.id.txt_staff)
    TextView txtStaff;
    @BindView(R.id.btn_change)
    LinearLayout btnChangePrice;
    @BindView(R.id.btn_close)
    LinearLayout btnClose;
    private User user;
    private ShiftList shiftlist;
    private UserList userList;

    @BindView(R.id.fragment_content_action_layout)
    ViewPager pager;
    private List<BaseFragment> listBaseFragments;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    private List<String> menu = new ArrayList<>();
    private CustomViewPagerAdapter adapter;

    ShiftAddTab1Activity fragmentTab1;
    ShiftAddTab2Activity fragmentTab2;

    //default data
    ShiftOpen shiftOpen;

    /**
     * Constructor
     */
    public ShiftDetailActivity(){ super(R.layout.activity_shift_detail_new);}

    /**
     * Create a intent to call this activity from another activity
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context){
        Intent intent = new Intent(context, ShiftDetailActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setupSupportForApp();
        initView();
        getData();
    }

    private void initView()
    {
        swipeRefreshLayout  = (SwipeRefreshLayout) findViewById(R.id.swipeContainer_report_add);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setEnabled(false);

        btnDetail = (FloatingActionButton) findViewById(R.id.btn_detail);
        btnDetail.setOnClickListener(this);
    }

    @OnClick({ R.id.btn_detail, R.id.btn_close, R.id.btn_change })
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.btn_detail:
            {
                //change to tab1
                tabLayout.setScrollPosition(0,0f,true);
                pager.setCurrentItem(0);

                if (fragmentTab1.lvAdd.getVisibility() == View.VISIBLE) {
                    fragmentTab1.layoutAddDetail.setVisibility(View.VISIBLE);
                    fragmentTab1.lvAdd.setVisibility(View.GONE);
                    fragmentTab1.getDataDetail();
                } else {
                    fragmentTab1.layoutAddDetail.setVisibility(View.GONE);
                    fragmentTab1.lvAdd.setVisibility(View.VISIBLE);
                }
                break;
            }
            case R.id.btn_close:
            {
                Intent intent = ShiftCloseActivity.buildIntent(getApplicationContext());
                intent.putExtra("option", "close");
                Bundle bundle = new Bundle();
                bundle.putSerializable("shift_data", shiftOpen);
                intent.putExtras(bundle);
                startActivityForResult(intent, UPDATE_SHIFT);

                break;
            }
            case R.id.btn_change:
            {
                Intent intent = ShiftCloseActivity.buildIntent(getApplicationContext());
                intent.putExtra("option", "change");
                Bundle bundle = new Bundle();
                bundle.putSerializable("shift_data", shiftOpen);
                intent.putExtras(bundle);
                startActivityForResult(intent, CHANGE_SHIFT);
                break;
            }
        }
    }

    private void handleError(Throwable throwable) {
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        swipeRefreshLayout.setRefreshing(false);
        showComponentClickable(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode)
            {
                case UPDATE_SHIFT:
                {
                    setResult(Activity.RESULT_OK);
                    finish();
                    break;
                }
                case CHANGE_SHIFT: {
                    //start stepp 2 - Open new shift
                    Intent intent = ShiftAddActivity.buildIntent(getApplicationContext());
                    intent.putExtra("option", "change");
                    startActivityForResult(intent, CREATE_SHIFT);
                    break;
                }
                case CREATE_SHIFT: {
                    setResult(Activity.RESULT_OK);
                    finish();
                    break;
                }
            }
        }
    }

    private void showComponentClickable(boolean isEnable)
    {
        btnDetail.setEnabled(isEnable);
        if(isEnable)btnDetail.setAlpha(1f); else btnDetail.setAlpha(0.5f);

        btnClose.setEnabled(isEnable);
        if(isEnable)btnClose.setAlpha(1f); else btnClose.setAlpha(0.5f);

        btnChangePrice.setEnabled(isEnable);
        if(isEnable)btnChangePrice.setAlpha(1f); else btnChangePrice.setAlpha(0.5f);
    }

    private void getData() {

        showComponentClickable(false);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        mCompositeDisposable.add(
                NSHServer.getServer().shiftList(user.getApi_token())
                        .subscribeOn(Schedulers.computation())
                        .flatMap(shiftList -> {
                            this.shiftlist = shiftList;
                            return NSHServer.getServer().userList(user.getApi_token());
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse, this::handleError)
        );

    }

    private void handleResponse(UserList userList) {
        this.userList = userList;
        shiftOpen = (ShiftOpen)getIntent().getSerializableExtra("shift_data");
        txtDate.setText(AppUtilities.ConvertStringDateToStringDate(shiftOpen.data.report_date,"yyyy-MM-dd","dd/MM/yyyy"));
        //AppUtilities.ConvertStringDateToStringDate()
        loadDataOpen(shiftOpen);
        initViewPager();

        showComponentClickable(true);

    }

    private void loadDataOpen(ShiftOpen rs) {
        txtDate.setText(AppUtilities.ConvertStringDateToStringDate(rs.data.report_date, "yyyy-MM-dd", "dd/MM/yyyy"));
        //set shift and staff
        for (int i = 0; i < shiftlist.data.size(); i++) {
            if (shiftlist.data.get(i).shift_id == rs.data.shift_id) {
                txtShift.setText(shiftlist.data.get(i).shift_name);
                break;
            }
        }
        for (int i = 0; i < userList.data.size(); i++) {
            if (userList.data.get(i).user_id == rs.data.salse_id) {
                txtStaff.setText(userList.data.get(i).full_name);
                break;
            }
        }
    }

    private void initViewPager()
    {
        listBaseFragments = new ArrayList<>();

        fragmentTab1 = ShiftAddTab1Activity.newInstance(shiftOpen,true);
        listBaseFragments.add(fragmentTab1);
        menu.add("Xăng dầu");

        //new code
        List<ShiftAddOtherDetail> data = new ArrayList<>();
        ShiftAddOtherDetail temp;
        for(int i=0;i<shiftOpen.data.daily_report_details.products.size();i++)
        {
            temp = new ShiftAddOtherDetail();
            temp.setImage(shiftOpen.data.daily_report_details.products.get(i).product_image);
            temp.setNum(String.valueOf(shiftOpen.data.daily_report_details.products.get(i).number_shift_open));
            temp.setTitle(shiftOpen.data.daily_report_details.products.get(i).product_name);
            temp.setUnit(shiftOpen.data.daily_report_details.products.get(i).unit);
            data.add(temp);
        }
        fragmentTab2 = ShiftAddTab2Activity.newInstance(data);

        listBaseFragments.add(fragmentTab2);
        menu.add("Sản phẩm khác");

        adapter = new CustomViewPagerAdapter(getSupportFragmentManager(), listBaseFragments,menu,null);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==1) btnDetail.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * Set up support tool for App
     */
    private void setupSupportForApp() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sales_report_add);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = getNSHSharedPreferences().getUserLogin();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_sales_report_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
