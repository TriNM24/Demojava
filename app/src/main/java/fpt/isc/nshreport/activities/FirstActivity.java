package fpt.isc.nshreport.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.BuildConfig;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.CheckVersion;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.DeviceInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class FirstActivity extends BaseActivity implements OnRequestPermissionsResultCallback {

    //private ProgressDialog dialog;
    private boolean responded = false;
    Call<CheckVersion> callAPI;

    /**
     * Constructor
     */
    public FirstActivity() {
        super(R.layout.activity_first);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check internet connection
        /*if (CheckInternetIsConnected()) {
            if (isPermissionGranted()) {
                //check internet ping web
                //inIt DeviceInfo
                new DeviceInfo(FirstActivity.this);
                new TestInternet().execute(5000);
            }
        } else {
            AppUtilities.showInform(FirstActivity.this, "Thông báo", "Không có mạng, không chạy được chương trình!", "OK", SweetAlertDialog.WARNING_TYPE,new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {
                    finish();
                }
            });

        }*/
    }

    @Override
    public void onResume(){
        super.onResume();

        if (CheckInternetIsConnected()) {
            if (isPermissionGranted()) {
                //check internet ping web
                //inIt DeviceInfo
                new DeviceInfo(FirstActivity.this);
                new TestInternet().execute(5000);
            }
        } else {
            AppUtilities.showInform(FirstActivity.this, "Thông báo", "Không có mạng, không chạy được chương trình!", "OK", SweetAlertDialog.WARNING_TYPE,new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {
                    finish();
                }
            });

        }
    }

    private boolean isPermissionGranted() {


        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> temp = new ArrayList<>();
            //GET_ACCOUNTS
            /*if(checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)
            {
                temp.add(Manifest.permission.GET_ACCOUNTS);
            }*/
            //ACCESS_FINE_LOCATION
            /*if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                temp.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }*/
            //READ_PHONE_STATE
            /*if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
            {
                temp.add(Manifest.permission.READ_PHONE_STATE);
            }*/
            //CALL_PHONE
            /*if(checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                temp.add(Manifest.permission.CALL_PHONE);
            }*/
            //READ_EXTERNAL_STORAGE
            /*if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                temp.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }*/
            //WRITE_EXTERNAL_STORAGE
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                temp.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                temp.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                temp.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (temp.size() > 0) {
                String permissions[] = new String[temp.size()];
                for (int i = 0; i < temp.size(); i++) {
                    permissions[i] = temp.get(i);
                }
                ActivityCompat.requestPermissions(this, permissions, 2);
                return false;
            }

        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 2: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new DeviceInfo(FirstActivity.this);
                    new TestInternet().execute(5000);

                } else {
                    AppUtilities.showInform(FirstActivity.this, "Thông báo", "Không chạy được chương trình do không đủ quyền!", "OK", SweetAlertDialog.WARNING_TYPE,new CallBackInformDialog() {
                        @Override
                        public void DiaglogPositive() {
                            finish();
                        }
                    });
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    class TestInternet extends AsyncTask<Integer, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Integer... params) {
            //new code
            new Thread() {
                @Override
                public void run() {
                    int TIMEOUT_VALUE = 3000;
                    try {
                        URL testUrl = new URL(NSHServer.BASE_URL);
                        //StringBuilder answer = new StringBuilder(100000);

                        URLConnection testConnection = testUrl.openConnection();
                        testConnection.setConnectTimeout(TIMEOUT_VALUE);
                        testConnection.setReadTimeout(TIMEOUT_VALUE);
                        BufferedReader in = new BufferedReader(new InputStreamReader(testConnection.getInputStream()));
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            responded = true;
                            break;
                        }
                        in.close();
                    } catch (SocketTimeoutException e) {

                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }.start();

            try {
                int timeout = params[0];
                int waited = 0;
                while (!responded && (waited < timeout)) {
                    sleep(100);
                    if (!responded) {
                        waited += 100;
                    }
                }
            } catch (InterruptedException e) {
                //show_Message("InterruptedException " + e.toString());
            } // do nothing
            finally {
                if (!responded) {
                    return false;
                } else {
                    return true;
                }
            }

        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { // code if not connected
                AppUtilities.showInform(FirstActivity.this, "Thông báo", "Kiểm tra lại kết nối mạng.", "Thử lại", SweetAlertDialog.WARNING_TYPE,new CallBackInformDialog() {
                    @Override
                    public void DiaglogPositive() {
                        new TestInternet().execute(3000);
                    }
                });
            } else { // code if connected
                //check version and do after check in handleResponse
                checkVersion();

            }
        }
    }


    private boolean checkLogin() {
        User user = getNSHSharedPreferences().getUserLogin();
        if (user.getUsername() == "") {
            return false;
        }
        return true;
    }

    public void checkVersion() {

        callAPI = NSHServer.getServer().checkVersion("android", DeviceInfo.DEVICEIMEI);
        callAPI.enqueue(new Callback<CheckVersion>() {
            @Override
            public void onResponse(Call<CheckVersion> call, Response<CheckVersion> response) {
                if (response.isSuccessful()) {
                    handleResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<CheckVersion> call, Throwable t) {
                AppUtilities.showInform(FirstActivity.this, "Thông báo", "Kiểm tra lại kết nối mạng.", "Thử lại", SweetAlertDialog.WARNING_TYPE,new CallBackInformDialog() {
                    @Override
                    public void DiaglogPositive() {
                        new TestInternet().execute(3000);
                    }
                });
            }
        });
    }

    private void handleResponse(CheckVersion rs) {

        if (!rs.status.equals("fail")) {
            String version = BuildConfig.VERSION_NAME;
            String releaseDate = BuildConfig.RELEASE_DATE;
            //dialog.dismiss();
            if (rs.data.flag == 1) {
                if (rs.data.version.equals(version) && rs.data.release_date.equals(releaseDate)) {
                    //start activity
                    if (checkLogin()) {
                        //startActivity(MainActivity.buildIntent(FirstActivity.this));
                        //new code
                        Intent intent = LoginActivity.buildIntent(FirstActivity.this);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("saved", true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        startActivity(LoginActivity.buildIntent(FirstActivity.this));
                    }
                } else {
                    //show form update
                    show_inform(rs.data.link_update);

                }
            }
        }else
        {
            //Toast.makeText(this, "Show imei to import db", Toast.LENGTH_LONG).show();
            showAppInformation();
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
                Toast.makeText(FirstActivity.this, "Đã sao chép IMEI vào bộ nhớ tạm!", Toast.LENGTH_SHORT).show();
            }
        });


        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();
    }

    public boolean CheckInternetIsConnected() {
        boolean isNetWork = true;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null) {
            isNetWork = false;
        } else {
            if (networkInfo.isConnected() == false) {
                isNetWork = false;
            }
            if (networkInfo.isAvailable() == false) {
                isNetWork = false;
            }
        }
        return isNetWork;
    }

    public void show_inform(String linkdownload) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Đã có phiên bản mới! Cập nhật?");

        builder.setPositiveButton("Cập nhật",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new DownloadAPKFromURL().execute(linkdownload);
                    }

                });
        builder.setCancelable(false);

        builder.setNegativeButton("Thoát",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing

                        finish();
                        // System.exit(0);
                        // dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.getWindow().setGravity(Gravity.TOP);
        alert.show();
    }

    public class DownloadAPKFromURL extends AsyncTask<String, String, String> {

        // "/sdcard/download/";
        String path_file = Environment.getExternalStorageDirectory()
                + "/download/";
        String file_name = "abc.apk";

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(FirstActivity.this);
            pDialog.setTitle("Cập nhật phiên bản mới");
            pDialog.setMessage("Đang tải bản cập nhật...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            int count;
            int progress = 0;

            try {

                URL url = new URL(params[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // Check folder
                File folder = new File(
                        Environment.getExternalStorageDirectory() + "/download");
                if (!folder.exists()) {

                    folder.mkdir();
                }

                // Get file name
                file_name = url.getFile().substring(
                        url.getFile().lastIndexOf('/') + 1,
                        url.getFile().length());

                // Check exists file
                File file = new File(path_file, file_name);

                if (file.exists()) {

                    file.delete();
                }

                try {
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(
                            url.openStream(), 8192);

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(path_file
                            + file_name);

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called

                        progress = (int) ((total * 100) / lenghtOfFile);

                        publishProgress("" + progress);

                        // writing data to file
                        output.write(data, 0, count);
                    }
                    // flushing output
                    output.flush();
                    // closing streams
                    output.close();
                    input.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                    return "DISCONNET_INTERNET";
                }

            } catch (Exception e) {
                return "DISCONNET_INTERNET";
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            // hide dialog
            pDialog.dismiss();

            if (result != null && result.equals("DISCONNET_INTERNET")) {
                /*Toast.makeText(getApplicationContext(), "Kiểm tra lại kết nối mạng",
                        Toast.LENGTH_LONG).show();*/
                showMessage("Kiểm tra lại kết nối mạng!", SweetAlertDialog.WARNING_TYPE);
                finish();

                return;
            }
            // Install app
            try {

                finish();

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(
                        Uri.fromFile(new File(path_file + file_name)),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                /*Toast.makeText(getApplicationContext(), "Tải bản cập nhật thành công",
                        Toast.LENGTH_LONG).show();*/
                showMessage("Tải bản cập nhật thành công",SweetAlertDialog.SUCCESS_TYPE);

            } catch (Exception ex) {
                /*Toast.makeText(getApplicationContext(), "Lỗi không thể cập nhật",
                        Toast.LENGTH_LONG).show();*/
                showMessage("Lỗi không thể cập nhật",SweetAlertDialog.ERROR_TYPE);
                finish();
            }
        }
    }

}
