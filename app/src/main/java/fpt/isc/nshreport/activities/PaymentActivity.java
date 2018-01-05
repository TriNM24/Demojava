package fpt.isc.nshreport.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackConfirmDialog;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.BaseResponse;
import fpt.isc.nshreport.models.objectParse.DebtNumber;
import fpt.isc.nshreport.models.posts.reportDebt;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.FileUtils;
import fpt.isc.nshreport.utilities.NumberTextWatcherForThousand;
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

public class PaymentActivity extends BaseActivity {
    //Declares variables
    @BindView(R.id.txt_date)
    TextView txtDate;
    @BindView(R.id.txt_debt)
    TextView txtDebt;
    @BindView(R.id.txt_note)
    EditText edtNote;
    @BindView(R.id.edit_had_pay)
    EditText edtMoney;
    @BindView(R.id.img_bill)
    ImageView imgBill;
    @BindView(R.id.img_noImage)
    ImageView imgNoImage;
    @BindView(R.id.layout_image)
    LinearLayout layoutImage;
    @BindView(R.id.btn_add)
    FloatingActionButton ButtonAdd;


    private User user;

    private File imageFile = null;

    private boolean isLoaded = false;

    /**
     * Constructor
     */
    public PaymentActivity() {
        super(R.layout.activity_payment);
    }

    /**
     * Create a intent to call this activity from another activity
     *
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, PaymentActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSupportForApp();
        initView();
        getData();
        super.setupUI(findViewById(R.id.layout_root));
    }

    public void showComponentClickable(boolean isEnable)
    {
        ButtonAdd.setEnabled(isEnable);
        if(isEnable) ButtonAdd.setAlpha(1f); else ButtonAdd.setAlpha(0.5f);
    }

    private void getData() {
        showComponentClickable(false);
        isLoaded = false;
        mCompositeDisposable.add(
                NSHServer.getServer().stationDebt(user.getApi_token())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
    }

    private void handleError(Throwable throwable) {
        showMessage(throwable.getMessage(), "Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
    }

    private void handleResponse(DebtNumber debtNumber) {
        if (debtNumber.status.equals("success")) {
            txtDebt.setText(AppUtilities.longTypeTextFormat(debtNumber.total_debt));
        }
        edtNote.setText("");
        edtMoney.setText("");
        imageFile = null;
        if (imgNoImage.getVisibility() == View.GONE) {
            imgNoImage.setVisibility(View.VISIBLE);
            imgBill.setVisibility(View.GONE);
        }
        layoutImage.setBackgroundResource(R.drawable.cus_edit_text_add_red);
        showComponentClickable(true);
        isLoaded = true;
    }

    private void initView() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy");
        String strDate = df.format(calendar.getTime());
        txtDate.setText(strDate);

        //edtMoney.setBackgroundResource(R.drawable.cus_edit_text_add);

        edtMoney.addTextChangedListener(new NumberTextWatcherForThousand(edtMoney));
        edtMoney.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    edtMoney.setBackgroundResource(R.drawable.cus_edit_text_add);
                } else {
                    edtMoney.setBackgroundResource(R.drawable.cus_edit_text_add_red);
                }
            }
        });
    }

    /**
     * Set up support tool for App
     */
    private void setupSupportForApp() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.action_add_new_sales_report: {
                if(isLoaded) {
                    if (checkValidate()) {
                        AppUtilities.showConfirm(this, "Xác nhận", "Thêm dữ liệu!", "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE, new CallBackConfirmDialog() {
                            @Override
                            public void DiaglogPositive() {
                                addDebtReport();
                            }

                            @Override
                            public void DiaglogNegative() {

                            }
                        });
                    }
                }
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.layout_image, R.id.btn_add})
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_image: {

                AppUtilities.showChooseCamera(this, new CallBackConfirmDialog() {
                    @Override
                    public void DiaglogPositive() {
                        try {
                            Uri uri = FileUtils.createImageFile(PaymentActivity.this);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            startActivityForResult(intent, AppUtilities.REQUEST_CAMERA);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void DiaglogNegative() {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), AppUtilities.ALBUM_PHOTOS);
                    }
                });

                break;
            }
            case R.id.btn_add: {
                if(checkValidate()) {
                    AppUtilities.showConfirm(this, "Xác nhận", "Thêm dữ liệu!", "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE, new CallBackConfirmDialog() {
                        @Override
                        public void DiaglogPositive() {
                            addDebtReport();
                        }

                        @Override
                        public void DiaglogNegative() {

                        }
                    });
                }
            }
        }
    }

    private void addDebtReport() {
        new UploadImageAsyncTask().execute(imageFile);
    }

    private boolean checkValidate() {
        if (edtMoney.getText().length() < 2) {
            showMessage("Nhập số tiền!", "Thông báo", SweetAlertDialog.WARNING_TYPE);
            return false;
        }
        if (imageFile == null) {
            showMessage("Chọn hình ảnh!", "Thông báo", SweetAlertDialog.WARNING_TYPE);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AppUtilities.ALBUM_PHOTOS) { //Get picture from gallery
                if (null != data) {
                    getPhotoResultFromGallery(data);
                }
            }else
            {
                getPhotoResultFromCamera();
            }
        }
    }

    /**
     * Get Photo result from Gallery
     *
     * @param data
     */
    private void getPhotoResultFromGallery(Intent data) {
        Uri selectedImageUri = data.getData();
        String selectedImagePath = getAppUtilities().getRealPathUrlImage(this, selectedImageUri);
        File file = new File(selectedImagePath);

        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -1);

        Date createImg = new Date(file.lastModified());
        Date before1Hourse = cal.getTime();

        if (createImg.after(before1Hourse)) {
            imageFile = file;
            if (imgNoImage.getVisibility() == View.VISIBLE) {
                imgNoImage.setVisibility(View.GONE);
                imgBill.setVisibility(View.VISIBLE);
            }
            Picasso.with(PaymentActivity.this).load(selectedImageUri).fit().centerInside().into(imgBill);
            layoutImage.setBackgroundResource(R.drawable.cus_edit_text_add);
        } else {
            AppUtilities.showInform(PaymentActivity.this, "Thông báo", "Ảnh chụp quá 1 giờ trước! Chọn ảnh gần đây hơn", "OK", SweetAlertDialog.WARNING_TYPE, new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {

                }
            });
        }
    }

    private void getPhotoResultFromCamera() {

        imageFile = new File(FileUtils.mCurrentPhotoPath);
        FileUtils.mCurrentPhotoPath = "";

        if (imgNoImage.getVisibility() == View.VISIBLE) {
            imgNoImage.setVisibility(View.GONE);
            imgBill.setVisibility(View.VISIBLE);
        }
        Picasso.with(PaymentActivity.this).load(imageFile).fit().centerInside().into(imgBill);
        layoutImage.setBackgroundResource(R.drawable.cus_edit_text_add);
    }

    private class UploadImageAsyncTask extends AsyncTask<File, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProcessing("Thêm dữ liệu nộp tiền");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(File... params) {

            File file = params[0];
            File file2 = FileUtils.resizeImage(file, 200);
            //test code
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

        Call<BaseResponse> call = NSHServer.getServer().uploadDocument3(user.getApi_token(), description, body);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().link_image != null && response.body().link_image.length() > 1) {
                        //add debt report
                        reportDebt data = new reportDebt();
                        data.setFillingStationId(user.getStation_id());
                        data.setUserId(user.getUserID());
                        data.setAmount(NumberTextWatcherForThousand.trimCommaOfString(edtMoney.getText().toString()));
                        data.setBillDate(AppUtilities.ConvertStringDateToStringDate(txtDate.getText().toString(), "dd/MM/yyyy", "yyyy-MM-dd"));
                        data.setImageBill(response.body().link_image);
                        data.setNotes(edtNote.getText().toString());
                        postData(data);
                    }
                    //showSuccessProcessing("Thành công");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                //Toast.makeText(ShiftAddActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                showFailProcessing(t.getMessage(), "Lỗi không xác định");
            }
        });
    }

    private void postData(reportDebt data) {
        mCompositeDisposable.add(
                NSHServer.getServer().debtReportAdd(user.getApi_token(), data)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponseAdd, this::handleErrorAdd));
    }

    private void handleResponseAdd(BaseResponse baseResponse) {
        if (baseResponse.status.equals("success")) {
            showSuccessProcessing("Thêm thành công");
        }
        getData();
    }

    private void handleErrorAdd(Throwable throwable) {
        showFailProcessing(throwable.getMessage(), "Lỗi không xác định");
    }

}
