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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import fpt.isc.nshreport.activities.Fragment.ShiftAddTab1Activity;
import fpt.isc.nshreport.activities.Fragment.ShiftAddTab2Activity;
import fpt.isc.nshreport.adapters.CustomViewPagerAdapter;
import fpt.isc.nshreport.adapters.PumpLinesAddAdapter;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.models.ShiftAddOtherDetail;
import fpt.isc.nshreport.models.SpinnerObject;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackConfirmDialog;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.BaseResponse;
import fpt.isc.nshreport.models.objectParse.ShiftList;
import fpt.isc.nshreport.models.objectParse.ShiftOpen;
import fpt.isc.nshreport.models.objectParse.UserList;
import fpt.isc.nshreport.models.posts.productAdd;
import fpt.isc.nshreport.models.posts.pump;
import fpt.isc.nshreport.models.posts.shiftCreate;
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

public class ShiftAddActivity extends BaseActivity implements
        OnRequestPermissionsResultCallback, View.OnClickListener {
    //Declares variables
    @BindView(R.id.txtReportDateSalesReportAdd)
    TextView txtDate;
    @BindView(R.id.spin_shift)
    Spinner spinnerShift;
    @BindView(R.id.spin_staff)
    Spinner spinnerStaff;
    @BindView(R.id.btn_up_data)
    LinearLayout btnCommit;

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
    ShiftOpen shiftBefore;
    String optionUpdate;

    private boolean isLoaded = false;

    /**
     * Constructor
     */
    public ShiftAddActivity() {
        super(R.layout.activity_shift_add_new);
    }

    /**
     * Create a intent to call this activity from another activity
     *
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, ShiftAddActivity.class);
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
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer_report_add);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setEnabled(false);

        btnDetail = (FloatingActionButton) findViewById(R.id.btn_detail);
        btnDetail.setOnClickListener(this);
    }

    @OnClick({R.id.btn_detail, R.id.btn_up_data})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_detail: {
                //change to tab1
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
            case R.id.btn_up_data: {
                if(checkValidate()) {
                    AppUtilities.showConfirm(this, "Xác nhận", "Mở ca báng hàng", "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE, new CallBackConfirmDialog() {
                        @Override
                        public void DiaglogPositive() {
                            addReport();
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

    /**
     * Get default date
     */
    private void getDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        String strDate = df.format(calendar.getTime());
        txtDate.setText(strDate);

        optionUpdate = getIntent().getStringExtra("option");
        if (optionUpdate != null && optionUpdate.equals("change")) {
            getSupportActionBar().setTitle("Chuyển giá - B2.Mở ca");
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

        btnCommit.setEnabled(isEnable);
        if(isEnable) btnCommit.setAlpha(1f); else btnCommit.setAlpha(0.5f);
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
                        .flatMap(userList1 ->
                                {
                                    this.userList = userList1;
                                    return NSHServer.getServer().reportBefore(user.getApi_token());
                                }
                        )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponse, this::handleError)
        );
    }

    private void handleResponse(ShiftOpen shiftOpen) {
        this.shiftBefore = shiftOpen;
        addDataSpinner();
        initViewPager();

        showComponentClickable(true);
        isLoaded = true;
    }

    private void addDataSpinner() {
        ArrayList<SpinnerObject> rsShift = new ArrayList<>();
        rsShift.add(0, new SpinnerObject("-1", "--Ca--"));
        for (int i = 0; i < shiftlist.data.size(); i++) {

            rsShift.add(i + 1, new SpinnerObject(String.valueOf(shiftlist.data.get(i).shift_id), shiftlist.data.get(i).shift_name));
        }

        ArrayAdapter<SpinnerObject> adapterShift = new ArrayAdapter<>(
                getApplicationContext(), R.layout.textview_spinner, rsShift);
        adapterShift.setDropDownViewResource(R.layout.row_one_spinner_item);
        spinnerShift.setAdapter(adapterShift);


        ArrayList<SpinnerObject> rsUser = new ArrayList<>();
        rsUser.add(0, new SpinnerObject("-1", "--Chọn nhân viên--"));
        for (int i = 0; i < userList.data.size(); i++) {

            rsUser.add(i + 1, new SpinnerObject(String.valueOf(userList.data.get(i).user_id), userList.data.get(i).full_name));
        }

        ArrayAdapter<SpinnerObject> adapterUser = new ArrayAdapter<>(
                getApplicationContext(), R.layout.textview_spinner, rsUser);
        adapterUser.setDropDownViewResource(R.layout.row_one_spinner_item);
        spinnerStaff.setAdapter(adapterUser);

    }

    private void initViewPager() {
        //pager = (ViewPager) findViewById(R.id.fragment_content_action_layout);
        //tabLayout = (TabLayout) findViewById(R.id.tabs);
        listBaseFragments = new ArrayList<>();

        fragmentTab1 = ShiftAddTab1Activity.newInstance(shiftBefore, false);
        listBaseFragments.add(fragmentTab1);
        menu.add("Xăng dầu");


        //new code
        List<ShiftAddOtherDetail> data = new ArrayList<>();
        ShiftAddOtherDetail temp;
        for (int i = 0; i < shiftBefore.data.daily_report_details.products.size(); i++) {
            temp = new ShiftAddOtherDetail();
            temp.setId(shiftBefore.data.daily_report_details.products.get(i).product_id);
            temp.setImage(shiftBefore.data.daily_report_details.products.get(i).product_image);
            temp.setNum(String.valueOf(shiftBefore.data.daily_report_details.products.get(i).number_report));
            temp.setTitle(shiftBefore.data.daily_report_details.products.get(i).product_name);
            temp.setUnit(shiftBefore.data.daily_report_details.products.get(i).unit);
            data.add(temp);
        }
        fragmentTab2 = ShiftAddTab2Activity.newInstance(data);


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
                if (position == 1) {
                    btnDetail.setVisibility(View.VISIBLE);
                }

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
            AppUtilities.showInform(ShiftAddActivity.this, "Thông báo", "Ảnh chụp quá 1 giờ trước! Chọn ảnh gần đây hơn", "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
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
                        int groupItem = PumpLinesAddAdapter.group_pos;
                        int childItem = PumpLinesAddAdapter.child_pos;
                        PumpLinesAdd pumpLine = (PumpLinesAdd) fragmentTab1.adapter.getChild(groupItem, childItem);
                        pumpLine.setPhotos(response.body().link_image);
                        pumpLine.setImageID(1);
                        fragmentTab1.adapter.notifyDataSetChanged();
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
                        AppUtilities.showConfirm(this, "Xác nhận", "Mở ca báng hàng", "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE, new CallBackConfirmDialog() {
                            @Override
                            public void DiaglogPositive() {
                                addReport();
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

    private void addReport() {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeRefreshLayout.setRefreshing(true);
            }
        });

        User user = getNSHSharedPreferences().getUserLogin();
        //new code
        shiftCreate data = new shiftCreate();
        String inputDate = AppUtilities.ConvertStringDateToStringDate(txtDate.getText().toString(), "dd/MM/yyyy", "yyyy-MM-dd");
        //get shift id
        SpinnerObject staff = (SpinnerObject) spinnerStaff.getSelectedItem();
        //get shift
        SpinnerObject shift = (SpinnerObject) spinnerShift.getSelectedItem();

        data.setSelldate(inputDate);
        data.setSalseId(Integer.valueOf(staff.getKey()));
        data.setShiftId(Integer.valueOf(shift.getKey()));
        data.setFillingStationId(user.getStation_id());
        List<pump> pumps = new ArrayList<>();
        for (int i = 0; i < fragmentTab1.headerDataList.size(); i++) {
            ArrayList<PumpLinesAdd> resultList = fragmentTab1.childDataList.get(fragmentTab1.headerDataList.get(i));
            for (int j = 0; j < resultList.size(); j++) {
                pump tmp = new pump();
                tmp.setFuelFillingColumnId(fragmentTab1.headerDataList.get(i).getId());
                tmp.setPumpId(resultList.get(j).getPumpId());
                tmp.setReportNum(resultList.get(j).getSalesLitre());
                tmp.setImage(resultList.get(j).getPhotos());
                //tmp.setImage("http://api.nshpetro.com.vn/uploads/images/2017/12/29/jpeg-nsh-1774526837-1514519962.jpg");
                tmp.setProductId(resultList.get(j).getProductId());
                pumps.add(tmp);
            }
        }
        data.setPumps(pumps);

        List<productAdd> products = new ArrayList<>();
        productAdd tmp;
        for (int i = 0; i < fragmentTab2.listData.size(); i++) {
            tmp = new productAdd();
            tmp.setId(fragmentTab2.listData.get(i).getId());
            tmp.setReportNum(Long.valueOf(fragmentTab2.listData.get(i).getNum()));
            products.add(tmp);
        }
        data.setProducts(products);

        //call api
        mCompositeDisposable.add(
                NSHServer.getServer().shiftAdd(user.getApi_token(), data)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponseCreate, this::handleError)
        );
    }

    private void handleResponseCreate(BaseResponse res) {
        if (res.status!=null && res.status.equals("fail")) {
            String message = res.message;
            if (optionUpdate.equals("change")) message = "Chuyển giá thất bại!";
            AppUtilities.showInform(this, "Thông báo", message + "\n" + res.error, "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {

                }
            });
        } else {
            String message = res.message;
            if (optionUpdate.equals("change")) message = "Chuyển giá thành công!";
            AppUtilities.showInform(this, "Thông báo", message, "OK", SweetAlertDialog.SUCCESS_TYPE, new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private boolean checkValidate() {
        if (((SpinnerObject) spinnerShift.getSelectedItem()).getKey().equals("-1")) {
            AppUtilities.showInform(this, "Thông báo", "Chọn ca bán hàng!", "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {
                }
            });
            AppUtilities.setErrorToSpinner(spinnerShift, "--Ca--", true);
            return false;
        }
        if (((SpinnerObject) spinnerStaff.getSelectedItem()).getKey().equals("-1")) {
            AppUtilities.showInform(this, "Thông báo", "Chọn nhân viên!", "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {
                }
            });
            AppUtilities.setErrorToSpinner(spinnerStaff, "--Chọn nhân viên--", true);
            return false;
        }
        return true;
    }
}
