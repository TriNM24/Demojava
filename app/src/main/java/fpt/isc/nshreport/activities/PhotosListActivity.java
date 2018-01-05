package fpt.isc.nshreport.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.PhotosListAdapter;
import fpt.isc.nshreport.models.ReportPhotos;

/**
 * Created by PhamTruong on 05/06/2017.
 */

public class PhotosListActivity extends BaseActivity {
    //Declares variables
    @BindView(R.id.lvPhotosList)
    ListView lvPhoto;
    private PhotosListAdapter adapter = null;
    private ArrayList<ReportPhotos> photosArrayList = null;
    private int pumpId = 0;

    /**
     * Constructor
     */
    public PhotosListActivity(){ super(R.layout.activity_photos_list);}

    /**
     * Create a intent to call this activity from another activity
     * @param context
     * @return
     */
    public static Intent buildIntent(Context context){
        Intent intent = new Intent(context, PhotosListActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get data intent
        Bundle bundle = getIntent().getExtras();
        if(bundle == null) { return; }
        pumpId = bundle.getInt("PUMP_LINE_ID", 0);
        if(pumpId <= 0) { return; }
        //Create widgets
        createWidgets();
        //Create events
        createEvents();
        //Get data
        getData();
        //Show Data
        showData();
    }


    /**
     * Create Widgets for App
     */
    private void createWidgets(){
        photosArrayList = new ArrayList<ReportPhotos>();
    }

    /**
     * Create Events for App
     */
    private void createEvents(){

    }

    /**
     * Get data for App
     */
    private void getData(){
        //photosArrayList = db.retrievePhotosListByPumpLineId(pumpId);
    }

    /**
     * Show data for App
     */
    private void showData(){
        if(photosArrayList != null && photosArrayList.size() > 0){
            adapter = new PhotosListAdapter(PhotosListActivity.this, R.layout.activity_photos_list_item, photosArrayList);
            lvPhoto.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_cancel_photos_list:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finish();
        //super.onBackPressed();
    }

}
