package fpt.isc.nshreport.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.GasPumpManageDetailAdapter;
import fpt.isc.nshreport.models.GasPumps;
import fpt.isc.nshreport.models.PumpLinesAdd;

public class GasPumpManageDetailActivity extends BaseActivity {
    //Declares variables
    private GasPumpManageDetailAdapter adapter = null;
    private ArrayList<PumpLinesAdd> pumpLinesList = null;
    private GasPumps mGasPump = null;

    //Widgets
    @BindView(R.id.txtCodeGasPumpManageDetail)
    TextView txtCode;
    @BindView(R.id.txtDateGasPumpManageDetail)
    TextView txtDate;
    @BindView(R.id.txtStatusGasPumpManageDetail)
    TextView txtStatus;
    @BindView(R.id.txtLineNumberGasPumpManageDetail)
    TextView txtNumber;
    @BindView(R.id.lvGasPumpManageDetail)
    ListView lvPumpLine;

    /**
     * Constructor
     */
    public GasPumpManageDetailActivity() {
        super(R.layout.activity_gas_pump_manage_detail);
    }

    public static Intent buildIntent(Context context) {
        Intent intent = new Intent(context, GasPumpManageDetailActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get data bundle
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){ return; }
        mGasPump = (GasPumps) bundle.getSerializable("GAS_PUMP_DETAIL");
        if(mGasPump == null){ return; }
        //Set title for app
        setTitle(mGasPump.getName());
        //Set events
        createEvents();
        //Set support for app
        setupSupportForApp();
        //Show Data
        showData();
        //Retrieve data list
        getDataList();
        //Show Data list
        showDataList();
    }

    /**
     * Create events for app
     */
    private void createEvents() {
    }

    /**
     * Set up support for app
     */
    private void setupSupportForApp() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail_gas_pump_manage);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();
    }

    /**
     * Show data
     */
    private void showData(){
        txtCode.setText(mGasPump.getCode());
        txtDate.setText(mGasPump.getSetupDate());
        txtStatus.setText(mGasPump.getPumpStatus());
        txtNumber.setText(String.valueOf(mGasPump.getNumberLine()));
    }

    /**
     * Show data list
     */
    private void showDataList(){
        if(pumpLinesList != null && pumpLinesList.size() > 0){
            adapter = new GasPumpManageDetailAdapter(GasPumpManageDetailActivity.this, R.layout.activity_gas_pump_manage_detail_item, pumpLinesList);
            lvPumpLine.setAdapter(adapter);
        }
    }

    /**
     * Retrieve data list
     */
    private void getDataList(){
        pumpLinesList = mGasPump.getPumpList();
    }

}
