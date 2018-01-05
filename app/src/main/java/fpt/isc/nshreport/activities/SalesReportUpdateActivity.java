package fpt.isc.nshreport.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.PumpLinesUpdateAdapter;
import fpt.isc.nshreport.adapters.SalesReportUpdateAdapter;
import fpt.isc.nshreport.models.GasPumps;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.models.SalesReport;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.customView.CallBackConfirmDialog;
import fpt.isc.nshreport.models.customView.CallBackInformDialog;
import fpt.isc.nshreport.models.objectParse.BaseResponse;
import fpt.isc.nshreport.models.posts.pumpUdateReport;
import fpt.isc.nshreport.models.posts.reportUpdate;
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

public class SalesReportUpdateActivity extends BaseActivity {
    //Declares variables
    private TextView txtReportDate;
    public TextView txtLitreTotal, txtTotal;
    private ImageView imgDate;
    private ExpandableListView lvAdd = null;
    private int lastExpandedPosition = -1;
    private SalesReport mSalesReport = null;
    private ArrayList<GasPumps> gasPumpsList = null;
    private SalesReportUpdateAdapter adapter = null;
    private ArrayList<GasPumps> headerDataList = null;
    private HashMap<GasPumps, ArrayList<PumpLinesAdd>> childDataList = null;
    private int year, month, day;
    private String selectedImagePath;
    private long litreTotal = 0;
    private long salesTotal = 0;
    private SimpleDateFormat df;

    private SwipeRefreshLayout swipeContainer;

    /**
     * Constructor
     */
    public SalesReportUpdateActivity() {
        super(R.layout.activity_sales_report_add);
    }

    /**
     * Create a intent to call this activity from another activity
     *
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, SalesReportUpdateActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get data intent
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            return;
        }
        //Sales report object
        mSalesReport = (SalesReport) bundle.getSerializable("SALES_REPORT_UPDATE");
        headerDataList = (ArrayList<GasPumps>) bundle.getSerializable("SALES_REPORT_HEADER");
        //childDataList = (HashMap<GasPumps, ArrayList<PumpLinesAdd>>) bundle.getSerializable("SALES_REPORT_CHILD");
        HashMap<String, ArrayList<PumpLinesAdd>> childDataList2;
        childDataList2 = (HashMap<String, ArrayList<PumpLinesAdd>>) bundle.getSerializable("SALES_REPORT_CHILD");
        if(mSalesReport == null){
            return;
        }
        GasPumps a1 = headerDataList.get(0);
        ArrayList<PumpLinesAdd> b1 = childDataList2.get("a");
        //create childDataList haha
        childDataList = new HashMap<>();
        for(int i=0;i<headerDataList.size();i++)
        {
            childDataList.put(headerDataList.get(i),childDataList2.get(headerDataList.get(i).getName()));
        }
        //Set up support tool for App
        setupSupportForApp();
        //Create widgets
        createWidgets();
        //Create events
        createEvents();
        //Get default date
        //getDefaultDate();
        //Get Gas pumps data
        //getGasPumpData();
        showDataList();
        showDataTotal();
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
        txtReportDate = (TextView) findViewById(R.id.txtReportDateSalesReportAdd);
        txtLitreTotal = (TextView) findViewById(R.id.txtLitreTotalSalesReportAdd);
        txtTotal = (TextView) findViewById(R.id.txtTotalSalesReportAdd);
        imgDate = (ImageView) findViewById(R.id.imgDateSalesReportAdd);
        lvAdd = (ExpandableListView) findViewById(R.id.lvSalesReportAdd);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer_report_add);
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
        //Set text view
        txtReportDate.setText(mSalesReport.getSalesDate());
        //Convert string date to calendar
        Calendar calendar = Calendar.getInstance();
        df = new SimpleDateFormat("dd/MM/yyy");
        try {
            calendar.setTime(df.parse(mSalesReport.getSalesDate()));
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }catch (ParseException ex) {
            ex.printStackTrace();
        }
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
            }
        };
        DatePickerDialog dPic = new DatePickerDialog(SalesReportUpdateActivity.this, callback, year, month, day);
        dPic.setTitle("Chọn ngày báo cáo");
        dPic.show();
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
                saveData();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == getAppUtilities().ALBUM_PHOTOS) { //Get picture from gallery
                if (null != data) {
                    getPhotoResultFromGallery(data);
                }
            } else if (requestCode == getAppUtilities().REQUEST_CAMERA) { //Get picture from camera
                File tempFile = new File(FileUtils.mCurrentPhotoPath);
                //Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "fpt.isc.nshreport.provider", tempFile);
                //Picasso.with(getApplicationContext()).load(photoURI).fit().centerInside().into(imgDate);
                new UploadImageAsyncTask().execute(tempFile);
            }
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
    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }
    private void UploadImage(File file)
    {
        //File file = FileUtils.getFile(this, fileUri);

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
                    Log.d("chick","____________ok__________");
                    if(response.body().link_image != null && response.body().link_image.length() > 1)
                    {
                        int groupItem = PumpLinesUpdateAdapter.group_pos;
                        int childItem = PumpLinesUpdateAdapter.child_pos;
                        PumpLinesAdd pumpLine = (PumpLinesAdd) adapter.getChild(groupItem, childItem);
                        pumpLine.setPhotos(response.body().link_image);
                        adapter.notifyDataSetChanged();
                    }
                    swipeContainer.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT).show();
                showMessage(t.getMessage(),"Lỗi không xác định",SweetAlertDialog.ERROR_TYPE);
                swipeContainer.setRefreshing(false);
                Log.d("chick",t.toString());
            }
        });

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
            showMessage("Ảnh chụp quá 1 giờ trước! Chọn ảnh gần đây hơn", SweetAlertDialog.WARNING_TYPE);
        }
    }

    /**
     * Get Photo result from Camera
     * @param data
     */
    private void getPhotoResultFromCamera(Intent data) {
        //Create a picture file
        File pictureFile = getAppUtilities().getOutputMediaFile(SalesReportUpdateActivity.this, getAppUtilities().MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            Toast.makeText(getApplicationContext(), "Kiểm tra lại bộ nhớ máy!", Toast.LENGTH_LONG).show();
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
                int groupItem = PumpLinesUpdateAdapter.group_pos;
                int childItem = PumpLinesUpdateAdapter.child_pos;
                //update photo
                PumpLinesAdd pumpLine = (PumpLinesAdd) adapter.getChild(groupItem, childItem);
                pumpLine.setPhotos(path);
                adapter.notifyDataSetChanged();
                //save photo
                //saveChangedPhotos(pumpLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save data
     */
    private void saveData() {
        if (txtReportDate.toString().trim().length() <= 0) {
            //Toast.makeText(getBaseContext(), "Vui lòng chọn ngày báo cáo!", Toast.LENGTH_SHORT).show();
            showMessage("Vui lòng chọn ngày báo cáo!",SweetAlertDialog.WARNING_TYPE);
        } else {
            if (headerDataList != null && childDataList != null) {
                AppUtilities.showConfirm(SalesReportUpdateActivity.this, "Xác nhận", "Cập nhật báo cáo!", "Đồng ý", "Hủy", SweetAlertDialog.NORMAL_TYPE,new CallBackConfirmDialog() {
                    @Override
                    public void DiaglogPositive() {
                        updateReport();
                    }

                    @Override
                    public void DiaglogNegative() {}
                });
            }
        }
    }

    private void updateReport()
    {
        swipeContainer.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeContainer.setRefreshing(true);
            }
        });

        User user = getNSHSharedPreferences().getUserLogin();
        reportUpdate data = new reportUpdate();
        data.setFillingStationId(Integer.valueOf(user.getStation_id()));
        List<pumpUdateReport> pumps = new ArrayList<>();
        //childDataList.get(headerDataList.get(0)).get(0).getSalesLitre();
        for(int i=0;i<headerDataList.size();i++)
        {
            for(int j=0;j<childDataList.get(headerDataList.get(i)).size();j++)
            {
                pumpUdateReport tmp = new pumpUdateReport();
                tmp.setDailyReportDetailId(childDataList.get(headerDataList.get(i)).get(j).getPumpId());
                tmp.setDailyReportImageId(childDataList.get(headerDataList.get(i)).get(j).getImageID());
                tmp.setReportNum(childDataList.get(headerDataList.get(i)).get(j).getSalesLitre());
                tmp.setImage(childDataList.get(headerDataList.get(i)).get(j).getPhotos());
                pumps.add(tmp);
            }
        }
        data.setPumps(pumps);

        mCompositeDisposable.add(
                NSHServer.getServer().reportUpdate(user.getApi_token(),mSalesReport.getId(),data)
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

    private void handleResponse(BaseResponse baseResponse) {
        if(baseResponse.status.equals("success"));
        {
            swipeContainer.setRefreshing(false);
            //showSuccessDialog();
            AppUtilities.showInform(SalesReportUpdateActivity.this, "Thông báo", "Lưu dữ liệu thành công!", "OK", SweetAlertDialog.WARNING_TYPE,new CallBackInformDialog() {
                @Override
                public void DiaglogPositive() {
                    finish();
                }
            });
        }
    }




    /**
     * Show alert dialog when saved successfully
     */
    /*private void showSuccessDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(SalesReportUpdateActivity.this);
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
     * Show data list for App
     */
    private void showDataList() {
        if (headerDataList != null && childDataList != null) {
            adapter = new SalesReportUpdateAdapter(SalesReportUpdateActivity.this, headerDataList, childDataList);
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
        txtTotal.setText(AppUtilities.longTypeTextFormat(salesTotal));
    }



}
