package fpt.isc.nshreport.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import fpt.isc.nshreport.utilities.NSHSharedPreferences;

/**
 * Created by Chick on 7/26/2017.
 */

public class FirebaseInstance extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //send to service
        NSHSharedPreferences.saveDeviceToken(refreshedToken,this);
    }
}
