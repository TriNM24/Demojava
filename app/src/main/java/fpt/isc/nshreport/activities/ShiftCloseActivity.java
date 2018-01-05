package fpt.isc.nshreport.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.Fragment.BaseFragment;
import fpt.isc.nshreport.activities.Fragment.ShiftCloseTab1Activity;
import fpt.isc.nshreport.activities.Fragment.ShiftCloseTab2Activity;
import fpt.isc.nshreport.adapters.CustomViewPagerAdapter;
import fpt.isc.nshreport.adapters.PumpLinesCloseAdapter;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.models.ShiftCloseOtherDetail;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackConfirmDialog;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.BaseResponse;
import fpt.isc.nshreport.models.objectParse.ShiftList;
import fpt.isc.nshreport.models.objectParse.ShiftOpen;
import fpt.isc.nshreport.models.objectParse.UserList;
import fpt.isc.nshreport.models.posts.product;
import fpt.isc.nshreport.models.posts.pumpUdateReport;
import fpt.isc.nshreport.models.posts.shiftUpdate;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.FileUtils;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by PhamTruong on 25/05/2017.
 */

public class ShiftCloseActivity extends BaseActivity implements
        OnRequestPermissionsResultCallback, View.OnClickListener {
    //Declares variables
    @BindView(R.id.txtReportDateSalesReportAdd)
    TextView txtDate;
    @BindView(R.id.txt_shift)
    TextView txtShift;
    @BindView(R.id.txt_staff)
    TextView txtStaff;
    @BindView(R.id.btn_close)
    LinearLayout btnClose;

    private User user;
    private ShiftList shiftlist;
    private UserList userList;
    private ShiftOpen shiftOpen;

    @BindView(R.id.fragment_content_action_layout)
    ViewPager pager;
    private List<BaseFragment> listBaseFragments;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    private List<String> menu = new ArrayList<>();
    private CustomViewPagerAdapter adapter;

    ShiftCloseTab1Activity fragmentTab1;
    ShiftCloseTab2Activity fragmentTab2;

    String optionUpdate;

    private boolean isLoaded = false;


    /**
     * Constructor
     */
    public ShiftCloseActivity() {
        super(R.layout.activity_shift_close_new);
    }

    /**
     * Create a intent to call this activity from another activity
     *
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, ShiftCloseActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSupportForApp();
        initView();
        getDefaultDate();
        getData();
    }

    private void initView() {
        //txtDate = (TextView) findViewById(R.id.txtReportDateSalesReportAdd);
        //txtStaff = (TextView) findViewById(R.id.txt_staff);
        //txtShift = (TextView) findViewById(R.id.txt_shift);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer_report_add);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setEnabled(false);

        //btnClose = (LinearLayout) findViewById(R.id.btn_close);
        //btnClose.setOnClickListener(this);


        btnDetail = (FloatingActionButton) findViewById(R.id.btn_detail);
        //btnDetail.setOnClickListener(this);
    }

    @OnClick({R.id.btn_detail, R.id.btn_close})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_detail: {

                tabLayout.setScrollPosition(0, 0f, true);
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
            case R.id.btn_close: {
                if (checkValidate()) {
                    String msg = "Đóng ca báng hàng";
                    if (!checkAtLeastChane()) {
                        msg = "Đóng ca hàng với chỉ số không thay đổi!";
                    }
                    AppUtilities.showConfirm(this, "Xác nhận", msg, "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE, new CallBackConfirmDialog() {
                        @Override
                        public void DiaglogPositive() {
                            updataReport();
                        }

                        @Override
                        public void DiaglogNegative() {

                        }
                    });
                }

                break;
            }
        }
    }

    private void updataReport() {

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        shiftUpdate data = new shiftUpdate();
        data.setFillingStationId(Integer.valueOf(user.getStation_id()));
        //đóng ca isUpdate là 0
        data.setIsUpdate(0);
        //int staffID = Integer.valueOf(((SpinnerObject) spin_staff.getSelectedItem()).getKey());
        data.setSalseId(shiftOpen.data.salse_id);

        List<pumpUdateReport> pumps = new ArrayList<>();
        for (int i = 0; i < fragmentTab1.headerDataList.size(); i++) {
            for (int j = 0; j < fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).size(); j++) {
                pumpUdateReport tmp = new pumpUdateReport();
                tmp.setDailyReportDetailId(fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getId());
                tmp.setDailyReportImageId(fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getImageID());
                tmp.setReportNum(fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getSalesLitre());
                tmp.setImage(fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getPhotos());
                pumps.add(tmp);
            }
        }

        List<product> products = new ArrayList<>();
        product tmp;
        for (int i = 0; i < fragmentTab2.listData.size(); i++) {
            tmp = new product();
            tmp.setId(fragmentTab2.listData.get(i).getId());
            tmp.setReportNum(Long.valueOf(fragmentTab2.listData.get(i).getNewLitre()));
            products.add(tmp);
        }

        data.setPumps(pumps);
        data.setProducts(products);

        mCompositeDisposable.add(
                NSHServer.getServer().shiftUpdate(user.getApi_token(), shiftOpen.data.daily_report_header_id, data)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponseUpdate, this::handleError));

    }

    private void handleResponseUpdate(BaseResponse baseResponse) {
        swipeRefreshLayout.setRefreshing(false);
        if (baseResponse.status != null && baseResponse.status.equals("success")) {

            AppUtilities.showInform(this, "Thông báo", baseResponse.message, "OK", SweetAlertDialog.SUCCESS_TYPE, new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {

                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });

        } else {
            AppUtilities.showInform(this, "Thông báo", baseResponse.message, "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {
                }
            });
        }
    }

    private boolean checkValidate() {
        for (int i = 0; i < fragmentTab1.headerDataList.size(); i++) {
            for (int j = 0; j < fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).size(); j++) {

                if(fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getSalesAmount() < 0)
                {
                    AppUtilities.showInform(this, "Thông báo", "Chỉ số mới nhỏ hơn chỉ số cũ tại " + fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getLineName(), "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                        @Override
                        public void DiaglogPositive() {
                        }
                    });
                    return false;
                }

                if (fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getSalesLitre() <= 0) {
                    AppUtilities.showInform(this, "Thông báo", "Nhập số lít cho " + fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getLineName(), "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                        @Override
                        public void DiaglogPositive() {
                        }
                    });
                    return false;
                }
                if (fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getImageID() != 1 ||
                        fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getPhotos().length() < 1) {
                    AppUtilities.showInform(this, "Thông báo", "Chọn ảnh cho " + fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getLineName(), "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                        @Override
                        public void DiaglogPositive() {
                        }
                    });
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkAtLeastChane() {
        for (int i = 0; i < fragmentTab1.headerDataList.size(); i++) {
            for (int j = 0; j < fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).size(); j++) {
                if (fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i)).get(j).getSalesAmount() > 0) {
                    return true;
                }
            }
        }
        for(int i=0;i<fragmentTab2.listData.size();i++)
        {
            if(Long.valueOf(fragmentTab2.listData.get(i).getNewLitre()) > 0)
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Get default date
     */
    private void getDefaultDate() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        String strDate = df.format(calendar.getTime());
        txtDate.setText(strDate);

        shiftOpen = (ShiftOpen) getIntent().getSerializableExtra("shift_data");

        optionUpdate = getIntent().getStringExtra("option");
        if (optionUpdate != null && optionUpdate.equals("change")) {
            getSupportActionBar().setTitle("Chuyển giá - B1.Đóng ca");
        }

        int notifi_id = getIntent().getIntExtra("notifi_id", 0);
        if (notifi_id != 0) {
            mCompositeDisposable.add(
                    NSHServer.getServer().updateReadNotification(NSHSharedPreferences.getToken(this), notifi_id)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(this::handleResponseUpdateNotifi, this::handleError));
        }

    }

    private void handleResponseUpdateNotifi(BaseResponse baseResponse) {
    }

    private void handleError(Throwable throwable) {
        showMessage(throwable.getMessage(), "Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        swipeRefreshLayout.setRefreshing(false);
        showComponentClickable(true);
    }

    public void showComponentClickable(boolean isEnable)
    {
        btnDetail.setEnabled(isEnable);
        if(isEnable) btnDetail.setAlpha(1f); else btnDetail.setAlpha(0.5f);

        btnClose.setEnabled(isEnable);
        if(isEnable) btnClose.setAlpha(1f); else btnClose.setAlpha(0.5f);
    }

    private void getData() {
        showComponentClickable(false);
        isLoaded = false;
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
        loadData();
    }

    private void loadData() {

        //set shift and staff
        for (int i = 0; i < shiftlist.data.size(); i++) {
            if (shiftlist.data.get(i).shift_id == shiftOpen.data.shift_id) {
                txtShift.setText(shiftlist.data.get(i).shift_name);
                break;
            }
        }
        for (int i = 0; i < userList.data.size(); i++) {
            if (userList.data.get(i).user_id == shiftOpen.data.salse_id) {
                txtStaff.setText(userList.data.get(i).full_name);
                break;
            }
        }

        initViewPager();

        showComponentClickable(true);
        isLoaded = true;
    }

    private void initViewPager() {
        //pager = (ViewPager) findViewById(R.id.fragment_content_action_layout);
        //tabLayout = (TabLayout) findViewById(R.id.tabs);
        listBaseFragments = new ArrayList<>();

        fragmentTab1 = ShiftCloseTab1Activity.newInstance(shiftOpen);
        listBaseFragments.add(fragmentTab1);
        menu.add("Xăng dầu");

        //new code
        List<ShiftCloseOtherDetail> listData = new ArrayList<>();
        ShiftCloseOtherDetail tmp;
        for (int j = 0; j < shiftOpen.data.daily_report_details.products.size(); j++) {
            tmp = new ShiftCloseOtherDetail();
            tmp.setId(shiftOpen.data.daily_report_details.products.get(j).daily_report_detail_id);
            tmp.setTitle(shiftOpen.data.daily_report_details.products.get(j).product_name);
            tmp.setOldLitre(String.valueOf(shiftOpen.data.daily_report_details.products.get(j).number_shift_open));
            //tmp.setNewLitre(String.valueOf(shiftOpen.data.daily_report_details.products.get(j).number_shift_open));
            //tmp.setNum("0");
            tmp.setImage(shiftOpen.data.daily_report_details.products.get(j).product_image);
            tmp.setUnit(shiftOpen.data.daily_report_details.products.get(j).unit);
            listData.add(tmp);
        }

        fragmentTab2 = ShiftCloseTab2Activity.newInstance(listData);
        listBaseFragments.add(fragmentTab2);
        menu.add("Sản phẩm khác");

        adapter = new CustomViewPagerAdapter(getSupportFragmentManager(), listBaseFragments, menu, null);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) btnDetail.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppUtilities.ALBUM_PHOTOS) { //Get picture from gallery
                if (null != data) {
                    getPhotoResultFromGallery(data);
                }
            } else if (requestCode == AppUtilities.REQUEST_CAMERA) { //Get picture from camera
                File tempFile = new File(FileUtils.mCurrentPhotoPath);
                FileUtils.mCurrentPhotoPath = "";
                new UploadImageAsyncTask().execute(tempFile);
            }
        }
    }



    private void getPhotoResultFromGallery(Intent data) {
        Uri selectedImageUri = data.getData();
        String selectedImagePath = getAppUtilities().getRealPathUrlImage(this, selectedImageUri);
        File file = new File(selectedImagePath);

        //SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);

        Date createImg = new Date(file.lastModified());
        Date before1Hourse = cal.getTime();

        if (createImg.after(before1Hourse)) {
            new UploadImageAsyncTask().execute(file);
        } else {
            //Toast.makeText(this, "Ảnh chụp quá 1 giờ trước! Chọn ảnh gần đây hơn", Toast.LENGTH_LONG).show();
            AppUtilities.showInform(ShiftCloseActivity.this, "Thông báo", "Ảnh chụp quá 1 giờ trước! Chọn ảnh gần đây hơn", "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {

                }
            });
        }


    }


    private class UploadImageAsyncTask extends AsyncTask<File, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(File... params) {

            File file = params[0];
            //test code
            File file2 = FileUtils.resizeImage(file, 200);
            UploadImage(file2);
            return null;
        }
    }

    private void UploadImage(File file) {

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        file
                );
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file_upload", file.getName(), requestFile);

        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        MultipartBody.FORM, descriptionString);

        User user = getNSHSharedPreferences().getUserLogin();
        Call<BaseResponse> call = NSHServer.getServer().uploadDocument3(user.getApi_token(), description, body);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().link_image != null && response.body().link_image.length() > 1) {
                        try {
                            int groupItem = PumpLinesCloseAdapter.group_pos;
                            int childItem = PumpLinesCloseAdapter.child_pos;
                            PumpLinesAdd pumpLine = (PumpLinesAdd) fragmentTab1.adapter.getChild(groupItem, childItem);
                            pumpLine.setPhotos(response.body().link_image);
                            pumpLine.setImageID(1);
                            fragmentTab1.adapter.notifyDataSetChanged();
                        }catch(Exception e){}
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                //Toast.makeText(ShiftAddActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                showMessage(t.getMessage(), "Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
                swipeRefreshLayout.setRefreshing(false);
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
        getMenuInflater().inflate(R.menu.menu_sales_report_add, menu);
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
            case R.id.action_add_new_sales_report:
                if(isLoaded) {
                    if (checkValidate()) {
                        String msg = "Đóng ca báng hàng";
                        if (!checkAtLeastChane()) {
                            msg = "Đóng ca hàng với chỉ số không thay đổi!";
                        }
                        AppUtilities.showConfirm(this, "Xác nhận", msg, "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE, new CallBackConfirmDialog() {
                            @Override
                            public void DiaglogPositive() {
                                updataReport();
                            }

                            @Override
                            public void DiaglogNegative() {

                            }
                        });
                    }
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
