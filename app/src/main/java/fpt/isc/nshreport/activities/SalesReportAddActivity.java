package fpt.isc.nshreport.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.PumpLinesAddAdapter;
import fpt.isc.nshreport.adapters.SalesReportAddAdapter;
import fpt.isc.nshreport.models.GasPumps;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.models.SalesReport;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackConfirmDialog;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.BaseResponse;
import fpt.isc.nshreport.models.objectParse.stationDetail;
import fpt.isc.nshreport.models.posts.pump;
import fpt.isc.nshreport.models.posts.reportAdd;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.FileUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by PhamTruong on 31/05/2017.
 */

public class SalesReportAddActivity extends BaseActivity {
    //Declares variables
    @BindView(R.id.txtReportDateSalesReportAdd)
    TextView txtReportDate;
    @BindView(R.id.txtLitreTotalSalesReportAdd)
    TextView txtLitreTotal;
    @BindView(R.id.txtTotalSalesReportAdd)
    TextView txtTotal;
    @BindView(R.id.imgDateSalesReportAdd)
    ImageView imgDate;
    @BindView(R.id.lvSalesReportAdd)
    ExpandableListView lvAdd = null;
    private int lastExpandedPosition = -1;
    private SalesReport mSalesReport = null;
    private ArrayList<GasPumps> gasPumpsList = null;
    private SalesReportAddAdapter adapter = null;
    private ArrayList<GasPumps> headerDataList = null;
    private HashMap<GasPumps, ArrayList<PumpLinesAdd>> childDataList = null;
    private int year, month, day;
    private String selectedImagePath;
    private long litreTotal = 0;
    private long salesTotal = 0;
    private SimpleDateFormat df;

    @BindView(R.id.swipeContainer_report_add)
    SwipeRefreshLayout swipeContainer;

    /**
     * Constructor
     */
    public SalesReportAddActivity() {
        super(R.layout.activity_sales_report_add);
    }

    /**
     * Create a intent to call this activity from another activity
     *
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, SalesReportAddActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up support tool for App
        setupSupportForApp();
        //Create widgets
        createWidgets();
        //Create events
        createEvents();
        //Get default date
        getDefaultDate();
        //Show Data
        showData();
        //Get Gas pumps data
        getData();
        //getGasPumpData();
        super.setupUI(findViewById(R.id.layout_root));
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

    }

    /**
     * Create Widgets for App
     */
    private void createWidgets() {
        //txtReportDate = (TextView) findViewById(R.id.txtReportDateSalesReportAdd);
        //txtLitreTotal = (TextView) findViewById(R.id.txtLitreTotalSalesReportAdd);
        //txtTotal = (TextView) findViewById(R.id.txtTotalSalesReportAdd);
        //imgDate = (ImageView) findViewById(R.id.imgDateSalesReportAdd);
        //lvAdd = (ExpandableListView) findViewById(R.id.lvSalesReportAdd);

        //swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer_report_add);
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeContainer.setEnabled(false);
    }

    /**
     * Create Events for App
     */
    private void createEvents() {
        imgDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        lvAdd.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    lvAdd.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    /**
     * Get default date
     */
    private void getDefaultDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        df = new SimpleDateFormat("dd/MM/yyy");
        String strDate = df.format(calendar.getTime());
        txtReportDate.setText(strDate);
        //txtReportDate.setText(day + "/" + (month + 1) + "/" + year);
    }

    /**
     * Show date picker dialog
     */
    public void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1;
                day = i2;
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                df = new SimpleDateFormat("dd/MM/yyy");
                String strDate = df.format(calendar.getTime());
                txtReportDate.setText(strDate);
                //txtReportDate.setText(i2 + "/" + (i1 + 1) + "/" + i);
            }
        };
        DatePickerDialog dPic = new DatePickerDialog(SalesReportAddActivity.this, callback, year, month, day);
        dPic.setTitle("Chọn ngày báo cáo");
        dPic.show();
    }

    /**
     * Show data for App
     */
    private void showData() {
        //Text view
        txtLitreTotal.setText("0");
        txtTotal.setText("0");
    }

    /**
     * Show data list for App
     */
    private void showDataList() {
        if (headerDataList != null && childDataList != null) {
            adapter = new SalesReportAddAdapter(SalesReportAddActivity.this, headerDataList, childDataList);
            lvAdd.setAdapter(adapter);
        }
    }

    /**
     * Set litre and amount total
     */
    public void showDataTotal() {
        litreTotal = 0;
        salesTotal = 0;
        for (int i = 0; i < headerDataList.size(); i++) {
            ArrayList<PumpLinesAdd> resultList = childDataList.get(headerDataList.get(i));
            for (int j = 0; j < resultList.size(); j++) {
                PumpLinesAdd pumpLinesAdd = resultList.get(j);
                litreTotal += pumpLinesAdd.getSalesLitre();
                salesTotal += pumpLinesAdd.getSalesAmount();
            }
        }
        txtLitreTotal.setText(AppUtilities.longTypeTextFormat(litreTotal));
        txtTotal.setText(getAppUtilities().longTypeTextFormat(salesTotal));
    }
    private void getData()
    {
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeContainer.setRefreshing(true);
            }
        });

        User user = getNSHSharedPreferences().getUserLogin();
        mCompositeDisposable.add(
                NSHServer.getServer().stationDetail(user.getApi_token(),user.getStation_id())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError));
    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(this, throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định",SweetAlertDialog.ERROR_TYPE);
        swipeContainer.setRefreshing(false);
    }

    private void handleResponse(stationDetail data) {

        headerDataList = new ArrayList<GasPumps>();
        childDataList = new HashMap<GasPumps, ArrayList<PumpLinesAdd>>();
        try {
            //set headerDataList
            for(int i=0;i<data.fuel_filling_columns.size();i++)
            {
                GasPumps tmp = new GasPumps();
                tmp.setId(data.fuel_filling_columns.get(i).fuel_filling_column_id);
                tmp.setName(data.fuel_filling_columns.get(i).fuel_filling_column_code);
                headerDataList.add(tmp);
                List<stationDetail.fuel_filling_columns.pumps> pump = data.fuel_filling_columns.get(i).pumps;

                ArrayList<PumpLinesAdd> pumplist = new ArrayList<>();
                for(int j=0;j<pump.size();j++)
                {
                    PumpLinesAdd a = new PumpLinesAdd();
                    a.setPumpId(pump.get(j).pump_id);
                    a.setLineName(pump.get(j).pump_name);
                    a.setSalesLitre(0);
                    a.setSalesPrice(100);
                    a.setPhotos("test.jpg");
                    pumplist.add(a);
                }
                childDataList.put(tmp,pumplist);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Show data list
        showDataList();
        swipeContainer.setRefreshing(false);
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
                //saveData();
                AppUtilities.showConfirm(SalesReportAddActivity.this, "Xác nhận", "Thêm báo cáo", "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE,new CallBackConfirmDialog() {
                    @Override
                    public void DiaglogPositive() {
                        addReport();
                    }

                    @Override
                    public void DiaglogNegative() {

                    }
                });

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    /**
     * Activity Result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == getAppUtilities().ALBUM_PHOTOS) { //Get picture from gallery
                if (null != data) {
                    getPhotoResultFromGallery(data);
                }
            } else if (requestCode == getAppUtilities().REQUEST_CAMERA) { //Get picture from camera
                /*if (null != data) {
                    new UploadImageAsyncTask().execute(data.getData());
                }*/
                File tempFile = new File(FileUtils.mCurrentPhotoPath);
                //Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "fpt.isc.nshreport.provider", tempFile);
                //Picasso.with(getApplicationContext()).load(photoURI).fit().centerInside().into(imgDate);
                new UploadImageAsyncTask().execute(tempFile);

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

        //SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR,-1);

        Date createImg = new Date(file.lastModified());
        Date before1Hourse = cal.getTime();

        if(createImg.after(before1Hourse))
        {
            new UploadImageAsyncTask().execute(file);
        }
        else
        {
            //Toast.makeText(this, "Ảnh chụp quá 1 giờ trước! Chọn ảnh gần đây hơn", Toast.LENGTH_LONG).show();
            showMessage("Ảnh chụp quá 1 giờ trước! Chọn ảnh gần đây hơn",SweetAlertDialog.WARNING_TYPE);
        }
    }

    /**
     * Get Photo result from Camera
     * @param data
     */
    private void getPhotoResultFromCamera(Intent data) {
        //Create a picture file
        File pictureFile = getAppUtilities().getOutputMediaFile(SalesReportAddActivity.this, getAppUtilities().MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            //Toast.makeText(getApplicationContext(), "Kiểm tra lại bộ nhớ máy!", Toast.LENGTH_LONG).show();
            showMessage("Kiểm tra lại bộ nhớ máy", SweetAlertDialog.WARNING_TYPE);
            return;
        }
        //Save picture file
        try {
            //Save Picture to SDCard
            Bitmap rotatedBitmap = null;
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            //Save picture to gallery
            rotatedBitmap = (Bitmap) data.getExtras().get("data");
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(ostream.toByteArray());
            fos.close();
            //Get image path for data list
            String path = pictureFile.getPath();
            if (path.length() > 0) {
                int groupItem = PumpLinesAddAdapter.group_pos;
                int childItem = PumpLinesAddAdapter.child_pos;
                PumpLinesAdd pumpLine = (PumpLinesAdd) adapter.getChild(groupItem, childItem);
                pumpLine.setPhotos(path);
                adapter.notifyDataSetChanged();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class UploadImageAsyncTask extends AsyncTask<File,Void,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeContainer.post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    swipeContainer.setRefreshing(true);
                }
            });
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(File... params) {

            File filePath = params[0];
            //test code
            File file2 = FileUtils.resizeImage(filePath,200);
            UploadImage(file2);
            return null;
        }
    }

    private void UploadImage(File file)
    {

        /*RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );*/
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
                        okhttp3.MultipartBody.FORM, descriptionString);

        User user = getNSHSharedPreferences().getUserLogin();
        Call<BaseResponse> call = NSHServer.getServer().uploadDocument3(user.getApi_token(),description,body);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if(response.isSuccessful())
                {
                    if(response.body().link_image != null && response.body().link_image.length() > 1)
                    {
                        int groupItem = PumpLinesAddAdapter.group_pos;
                        int childItem = PumpLinesAddAdapter.child_pos;
                        PumpLinesAdd pumpLine = (PumpLinesAdd) adapter.getChild(groupItem, childItem);
                        pumpLine.setPhotos(response.body().link_image);
                        adapter.notifyDataSetChanged();
                    }
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                //Toast.makeText(SalesReportAddActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                showMessage(t.getMessage(),"Lỗi không xác định",SweetAlertDialog.ERROR_TYPE);
                swipeContainer.setRefreshing(false);
            }
        });

    }

    private void addReport()
    {
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeContainer.setRefreshing(true);
            }
        });

        User user = getNSHSharedPreferences().getUserLogin();
        //new code
        reportAdd data = new reportAdd();
        String inputDate = AppUtilities.ConvertStringDateToStringDate(txtReportDate.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd");
        data.setSelldate(inputDate);
        data.setUserid(user.getUserID());
        data.setNote("note demo");
        data.setFillingStationId(user.getStation_id());
        List<pump> pumps = new ArrayList<>();
        for(int i=0;i<headerDataList.size();i++)
        {
            ArrayList<PumpLinesAdd> resultList = childDataList.get(headerDataList.get(i));
            for(int j=0;j<resultList.size();j++)
            {
                pump tmp = new pump();
                tmp.setFuelFillingColumnId(headerDataList.get(i).getId());
                tmp.setPumpId(resultList.get(j).getPumpId());
                tmp.setReportNum(resultList.get(j).getSalesLitre());
                tmp.setImage(resultList.get(j).getPhotos());
                //tmp.setImage("test.jpg");
                pumps.add(tmp);
            }
        }
        data.setPumps(pumps);

        //call api
        mCompositeDisposable.add(
                NSHServer.getServer().reportAdd(user.getApi_token(),data)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse2,this::handleError)
        );

    }

    private void handleResponse2(BaseResponse res) {
        //Toast.makeText(this, res.status, Toast.LENGTH_SHORT).show();
        //showSuccessDialog();
        AppUtilities.showInform(SalesReportAddActivity.this, "Thông báo", "Lưu dữ liệu thành công!", "OK", SweetAlertDialog.SUCCESS_TYPE,new CallBackInformDialog() {
            @Override
            public void DiaglogPositive() {
                finish();
            }
        });
        swipeContainer.setRefreshing(false);
    }

    /*private void showSuccessDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        *//*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SalesReportUpdateActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SalesReportUpdateActivity.this);
        }*//*
        builder.setMessage("Lưu dữ liệu thành công!");
        builder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog,int id){
                dialog.cancel();
                finish();
            }
        });
        builder.create().show();
    }*/

    /**
     * Show alert dialog when saved successfully
     */
    /*private void showSuccessDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(SalesReportAddActivity.this);
        *//*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SalesReportAddActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SalesReportAddActivity.this);
        }*//*
        builder.setMessage("Lưu dữ liệu thành công!");
        builder.setNegativeButton("OK",new DialogInterface.OnClickListener() {
            public void onClick (DialogInterface dialog,int id){
                dialog.cancel();
                finish();
            }
        });
        builder.create().show();
    }*/


}
