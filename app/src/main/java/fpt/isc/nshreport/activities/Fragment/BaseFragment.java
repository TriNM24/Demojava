package fpt.isc.nshreport.activities.Fragment;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ADMIN on 7/29/2017.
 */

public class BaseFragment extends Fragment {
    protected CompositeDisposable mCompositeDisposable;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onStart() {
        super.onStart();
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
            mCompositeDisposable.dispose();
        }
        //mCompositeDisposable.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.clear();
            mCompositeDisposable.dispose();
        }
    }

    //show message
    public void showMessage(String msg, int messageType)
    {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(),messageType);
        sweetAlertDialog.setTitleText("Thông báo");
        sweetAlertDialog.setContentText(msg);
        sweetAlertDialog.setCanceledOnTouchOutside(true);
        sweetAlertDialog.show();
    }
    //show message
    public void showMessage(String msg,String title ,int messageType)
    {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(),messageType);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(msg);
        sweetAlertDialog.setCanceledOnTouchOutside(true);
        sweetAlertDialog.show();
    }
}
