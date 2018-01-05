package fpt.isc.nshreport.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.fabric.sdk.android.Fabric;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by TruongPV5 on 24/05/2017.
 */

public class BaseActivity extends AppCompatActivity {
    //Declares variables
    private Integer mLayoutResId;
    protected CompositeDisposable mCompositeDisposable;
    SweetAlertDialog pDialog;

    //bien btnDetail su dung trong fragment shift detail
    public FloatingActionButton btnDetail;
    public SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Set common layout
     * @param layoutResId
     */
    protected BaseActivity(Integer layoutResId){
        this.mLayoutResId = layoutResId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutResId.intValue());
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        logUserFabric();
        mCompositeDisposable = new CompositeDisposable();
    }

    public void logUserFabric()
    {
        User user = NSHSharedPreferences.getInstance(this).getUserLogin();
        if(user.getUserID().length()>0) {
            Crashlytics.setUserIdentifier(user.getUserID());
            Crashlytics.setUserEmail(user.getEmail());
            Crashlytics.setUserName(user.getUsername());
        }
    }

    public void hideSoftKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) BaseActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(BaseActivity.this.getCurrentFocus().getWindowToken(), 0);
        }catch (Exception e)
        {

        }
    }

    public void showDataTotal(){}

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    v.clearFocus();
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }

    /**
     * Get NSHSharedPreferences
     * @return
     */
    protected NSHSharedPreferences getNSHSharedPreferences(){
        return NSHSharedPreferences.getInstance(getApplicationContext());
    }

    /**
     * Get AppUtilities
     * @return
     */
    protected AppUtilities getAppUtilities(){
        return new AppUtilities();
    }


    //cancel all request is existing
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable!= null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
            mCompositeDisposable.dispose();
        }
    }
    //cancel all request is existing
    @Override
    protected void onPause() {
        super.onPause();
        if(mCompositeDisposable!= null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
            mCompositeDisposable.dispose();
        }

    }
    //cancel all request is existing
    @Override
    protected void onStart() {
        super.onStart();
        mCompositeDisposable = new CompositeDisposable();
    }

    //show message
    public void showMessage(String msg, int messageType)
    {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this,messageType);
        sweetAlertDialog.setTitleText("Thông báo");
        sweetAlertDialog.setContentText(msg);
        sweetAlertDialog.setCanceledOnTouchOutside(true);
        sweetAlertDialog.show();
    }

    //show message
    public void showMessage(String msg,String title ,int messageType)
    {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this,messageType);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(msg);
        sweetAlertDialog.setCanceledOnTouchOutside(true);
        sweetAlertDialog.show();
    }

    //show processing
    public void showProcessing(String title)
    {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(title);
        pDialog.setCancelable(false);
        pDialog.show();
    }
    public void showSuccessProcessing(String title)
    {
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setTitleText(title)
                .setConfirmText("OK")
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    public void showSuccessProcessing(String title,boolean autoClose)
    {
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setTitleText(title)
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        if(autoClose) pDialog.dismiss();
    }

    public void showFailProcessing(String message,String title)
    {
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setTitleText(title);
        pDialog.setContentText(message);
        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }
}
