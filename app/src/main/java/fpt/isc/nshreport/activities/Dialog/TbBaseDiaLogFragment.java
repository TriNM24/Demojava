package fpt.isc.nshreport.activities.Dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class TbBaseDiaLogFragment<C> extends DialogFragment {

	private Class<C> mCallbackInterface = null;

	@SuppressLint("ValidFragment")
	protected TbBaseDiaLogFragment(Class<C> callbackInterface) {
		this.mCallbackInterface = callbackInterface;
	}
	public TbBaseDiaLogFragment()
	{
	}

	public C getCallback() {
		if (getActivity() == null) {
			Log.e( "test","Returning null callback since the fragment is not attached to any activity now: ");
		}
		return (C) getActivity();
	}
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
