package fpt.isc.nshreport.activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.BuildConfig;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.UserLogin;
import fpt.isc.nshreport.models.posts.UserPost;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.DeviceInfo;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by PhamTruong on 25/05/2017.
 */

public class LoginActivity extends BaseActivity implements OnRequestPermissionsResultCallback {
    //Declares variables
    @BindView(R.id.txtUsernameLogin)
    EditText txtUser;
    @BindView(R.id.txtPassLogin)
    EditText txtPass;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.layout_saved)
    LinearLayout layout_saved;
    @BindView(R.id.layout_not_save)
    TextInputLayout layout_not_save;
    @BindView(R.id.img_account)
    RoundedImageView img_account;
    @BindView(R.id.txt_user_saved)
    TextView txt_user_saved;
    @BindView(R.id.txt_login_other)
    TextView txt_login_other;
    @BindView(R.id.txt_imei_info)
    TextView txt_imei_info;

    //new code
    String strUser, strPass;
    boolean isSaved = false;

    /**
     * Constructor
     */
    public LoginActivity() {
        super(R.layout.activity_login_saved);
    }

    /**
     * Create a intent to call this activity from another activity
     *
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLayout();
        createEvents();
        super.setupUI(findViewById(R.id.layout_login));
    }

    private void checkLayout() {
        isSaved = getIntent().getBooleanExtra("saved", false);
        if (isSaved) {
            layout_saved.setVisibility(View.VISIBLE);
            layout_not_save.setVisibility(View.GONE);
            txt_login_other.setVisibility(View.VISIBLE);
            txt_imei_info.setVisibility(View.GONE);

            User user = getNSHSharedPreferences().getUserLogin();

            txt_user_saved.setText(user.getName());
            txtUser.setText(user.getUsername());

            if(user.getAvatar()!= null && user.getAvatar().length() > 3)
            {
                Picasso.with(this).load("http://inside.nshpetro.com.vn/"+user.getAvatar()).fit().centerInside().into(img_account);
            }

        } else {
            layout_saved.setVisibility(View.GONE);
            layout_not_save.setVisibility(View.VISIBLE);
            txt_login_other.setVisibility(View.GONE);
            txt_imei_info.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.btnLogin, R.id.txt_login_other,R.id.txt_imei_info})
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnLogin: {
                strUser = txtUser.getText().toString().trim();
                strPass = txtPass.getText().toString().trim();
                if (checkInputData(strUser, strPass)) {
                    callLogin(strUser, strPass);
                }
                break;
            }
            case R.id.txt_login_other: {
                txtUser.setText("");
                layout_saved.setVisibility(View.GONE);
                layout_not_save.setVisibility(View.VISIBLE);
                txt_login_other.setVisibility(View.GONE);
                txt_imei_info.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.txt_imei_info:{
                showAppInformation();
            }
        }
    }


    /**
     * Create events for App
     */
    private void createEvents() {

        Observable<CharSequence> Obuser = RxTextView.textChanges(txtUser);
        Observable<CharSequence> Obpass = RxTextView.textChanges(txtPass);

        btnLogin.setEnabled(false);

        Observable.combineLatest(Obuser, Obpass, (userObservable, passObservable) -> {
            boolean emailCheck = userObservable.length() > 0;

            boolean passwordCheck = passObservable.length() >= 6;
            return emailCheck && passwordCheck;
        }).subscribe(aBoolean -> {
            btnLogin.setEnabled(aBoolean);
        });
    }

    /**
     * Check Input Data
     *
     * @param inputUser
     * @param inputPass
     * @return
     */
    private boolean checkInputData(String inputUser, String inputPass) {
        if (inputUser.length() < 1 || inputPass.length() < 1) {
            showMessage("Vui lòng nhập thông tin!", SweetAlertDialog.WARNING_TYPE);
            return false;
        }
        return true;
    }

    /**
     * Login to system
     *
     * @param user
     * @param pass
     */
    private void callLogin(String user, String pass) {

        showProcessing("Đang đăng nhập...");

        String token = NSHSharedPreferences.getDeviceToken(this);


        UserPost data = new UserPost(user, pass, token, "android", DeviceInfo.DEVICEIMEI);

        mCompositeDisposable.add(
                NSHServer.getServer().callLogin(data)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
    }

    private void handleResponse(UserLogin data) {

        if (data.status.equals("success")) {
            showSuccessProcessing("Đăng nhập thành công!", true);
            //Save user
            User user = new User();
            user.setUsername(strUser);
            user.setPassword(strPass);
            user.setName(data.user.full_name);
            user.setEmail(data.user.email);
            user.setApi_token(data.api_token);
            user.setStation_id(data.user.filling_station_id);
            user.setUserID(data.user.user_id);
            user.setAvatar(data.user.avatar);
            user.setTitle_station(data.user.title);
            getNSHSharedPreferences().saveUserLogin(user);
            getNSHSharedPreferences().saveCheckUserLogin(strUser, strPass, true);
            NSHSharedPreferences.saveToken(data.api_token, this);
            //Go to MainActivity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            showFailProcessing("Thông tin đăng nhập không chính xác. Vui lòng nhập lại!", "Thông báo");
        }
    }

    private void handleError(Throwable error) {
        showFailProcessing(error.getMessage(), "Lỗi không xác định");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 2: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    AppUtilities.showInform(LoginActivity.this, "Thông báo", "Không chạy được chương trình do không đủ quyền!", "OK", SweetAlertDialog.ERROR_TYPE, new CallBackInformDialog() {
                        @Override
                        public void DiaglogPositive() {
                            finish();
                        }
                    });
                }
                return;
            }
        }
    }

    private void showAppInformation()
    {
        String version = BuildConfig.VERSION_NAME;
        String releaseDate = AppUtilities.ConvertStringDateToStringDate(BuildConfig.RELEASE_DATE,"yyyy-MM-dd","dd/MM/yyyy");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_app_info_imei, null);
        ((TextView)dialogView.findViewById(R.id.txt_version)).setText(version);
        ((TextView)dialogView.findViewById(R.id.txt_release_date)).setText(releaseDate);
        ((TextView)dialogView.findViewById(R.id.txt_imei)).setText(DeviceInfo.DEVICEIMEI);
        ((Button)dialogView.findViewById(R.id.btn_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("IMEI", "Android: " + DeviceInfo.DEVICEIMEI);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(LoginActivity.this, "Đã sao chép IMEI vào bộ nhớ tạm!", Toast.LENGTH_SHORT).show();
            }
        });


        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        //alertDialog.setCancelable(false);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
