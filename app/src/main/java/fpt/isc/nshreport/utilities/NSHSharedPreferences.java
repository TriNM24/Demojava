package fpt.isc.nshreport.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import fpt.isc.nshreport.models.User;

/**
 * Created by PhamTruong on 26/05/2017.
 */

public class NSHSharedPreferences {
    //Declares variables
    private static final String EXTRA_USER_ACCOUNT_KEY = "extra_user_account_key";
    private static final String EXTRA_USER_PASS_KEY = "extra_user_pass_key";
    private static final String EXTRA_USER_REMEMBER_KEY = "extra_user_remember_key";
    private static final String EXTRA_USER_NAME_KEY = "extra_user_name_key";
    private static final String EXTRA_USER_EMAIL_KEY = "extra_user_email_key";
    private static final String EXTRA_USER_TOKEN_KEY = "extra_user_token_key";
    private static final String EXTRA_USER_STATION_ID = "extra_user_station_id";
    private static final String EXTRA_USER_ID = "extra_user_id";
    private static final String EXTRA_USER_AVATAR = "extra_user_avatar";
    private static final String EXTRA_USER_STATION = "extra_user_station";

    private static final String EXTRA_TOKEN_KEY = "extra_token_key";

    private static final String EXTRA_DEVICE_TOKEN_KEY = "extra_device_token_key";
    private static final String EXTRA_NOTIFY_KEY = "extra_notify_key";

    private static NSHSharedPreferences mNSHPreferences;
    private SharedPreferences mPreferences;

    /**
     * NSHSharedPreferences Constructor
     * @param context
     */
    private NSHSharedPreferences (Context context){
        mPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public synchronized static NSHSharedPreferences getInstance(Context context){
        if (mNSHPreferences == null){
            mNSHPreferences = new NSHSharedPreferences(context);
        }
        return mNSHPreferences;
    }

    public void saveCheckUserLogin(String user, String pass, boolean checkRememberLogin){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(EXTRA_USER_ACCOUNT_KEY, user);
        editor.putString(EXTRA_USER_PASS_KEY, pass);
        editor.putBoolean(EXTRA_USER_REMEMBER_KEY, checkRememberLogin);
        editor.commit();
    }

    public void saveUserLogin(User user){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(EXTRA_USER_ACCOUNT_KEY, user.getUsername());
        editor.putString(EXTRA_USER_PASS_KEY, user.getPassword());
        editor.putString(EXTRA_USER_NAME_KEY, user.getName());
        editor.putString(EXTRA_USER_EMAIL_KEY, user.getEmail());
        editor.putString(EXTRA_USER_TOKEN_KEY, user.getApi_token());
        editor.putString(EXTRA_USER_STATION_ID, user.getStation_id());
        editor.putString(EXTRA_USER_ID, user.getUserID());
        editor.putString(EXTRA_USER_AVATAR,user.getAvatar());
        editor.putString(EXTRA_USER_STATION,user.getTitle_station());
        editor.commit();
    }

    public User getUserLogin(){
        User user = new User();
        user.setUsername(mPreferences.getString(EXTRA_USER_ACCOUNT_KEY,""));
        user.setPassword(mPreferences.getString(EXTRA_USER_PASS_KEY,""));
        user.setName(mPreferences.getString(EXTRA_USER_NAME_KEY,""));
        user.setEmail(mPreferences.getString(EXTRA_USER_EMAIL_KEY,""));
        user.setApi_token(mPreferences.getString(EXTRA_USER_TOKEN_KEY,""));
        user.setStation_id(mPreferences.getString(EXTRA_USER_STATION_ID,""));
        user.setUserID(mPreferences.getString(EXTRA_USER_ID,""));
        user.setAvatar(mPreferences.getString(EXTRA_USER_AVATAR,""));
        user.setTitle_station(mPreferences.getString(EXTRA_USER_STATION,""));
        return user;
    }

    public static void saveToken(String token,Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(EXTRA_TOKEN_KEY, token);
        editor.commit();
    }
    public static String getToken(Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        return mPreferences.getString(EXTRA_TOKEN_KEY,"");
    }

    public static void saveDeviceToken(String token,Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(EXTRA_DEVICE_TOKEN_KEY, token);
        editor.commit();
    }

    public static String getDeviceToken(Context context){
        SharedPreferences mPreferences = context.getSharedPreferences("tokenSave", Context.MODE_PRIVATE);
        return mPreferences.getString(EXTRA_DEVICE_TOKEN_KEY,"");
    }

    public static void saveNotifiCount(String count,Context context)
    {
        SharedPreferences mPreferences = context.getSharedPreferences("notifyCount", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(EXTRA_NOTIFY_KEY, count);
        editor.commit();
    }

    public static String getNotifiCount(Context context)
    {
        SharedPreferences mPreferences = context.getSharedPreferences("notifyCount", Context.MODE_PRIVATE);
        return mPreferences.getString(EXTRA_NOTIFY_KEY,"0");
    }


    public void clearData(){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear().commit();
    }

}
