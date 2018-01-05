package fpt.isc.nshreport.activities.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.GasPumpManageDetailActivity;
import fpt.isc.nshreport.adapters.GasPumpManageAdapter;
import fpt.isc.nshreport.models.GasPumps;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.models.User;
import fpt.isc.nshreport.models.objectParse.stationDetail;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 12/06/2017.
 */

public class GasPumpManageActivity extends BaseFragment {
    //Declare variables
    private List<GasPumps> gasPumpsList = new ArrayList<>();
    private GasPumpManageAdapter adapter;
    @BindView(R.id.swipe_layout_gas_pump)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.lvGasPumpManage)
    ListView lvGasPump;
    @BindView(R.id.txtPumpTotalGasPumpManage)
    TextView txtNumberPump;
    @BindView(R.id.txtPumpLineTotalGasPumpManage)
    TextView txtNumberPumpLine;

    //new code
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_gas_pump_manage, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.activity_title_gas_pump_manage);
        user  = NSHSharedPreferences.getInstance(getContext()).getUserLogin();
        createWidgets();
        createEvents();
        //getdata
        getData();

    }

    private void getData()
    {
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipeLayout.setRefreshing(true);
            }
        });
        
        mCompositeDisposable.add(
                NSHServer.getServer().stationDetail(user.getApi_token(),user.getStation_id())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError));

    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(getActivity(), throwable.toString(), Toast.LENGTH_SHORT).show();
        swipeLayout.setRefreshing(false);
    }

    private void handleResponse(stationDetail stationDetail) {
        //get data set to list
        for(int i=0;i<stationDetail.fuel_filling_columns.size();i++)
        {
            GasPumps gasPump = new GasPumps();
            gasPump.setId(stationDetail.fuel_filling_columns.get(i).fuel_filling_column_id);
            gasPump.setCode(stationDetail.fuel_filling_columns.get(i).fuel_filling_column_code);
            gasPump.setName(stationDetail.fuel_filling_columns.get(i).fuel_filling_column_code);
            gasPump.setNumberLine(stationDetail.fuel_filling_columns.get(i).pumps.size());
            gasPump.setLitreTotal(12);
            gasPump.setAmountTotal(24);
            gasPump.setPumpStatus((stationDetail.fuel_filling_columns.get(i).is_active == 1)? "OK": "Not OK");
            gasPump.setSetupDate("01/01/2017");

            ArrayList<PumpLinesAdd> pumpList = new ArrayList<>();
            for(int j=0;j<stationDetail.fuel_filling_columns.get(i).pumps.size();j++)
            {
                PumpLinesAdd tmp = new PumpLinesAdd();
                String name = stationDetail.fuel_filling_columns.get(i).pumps.get(j).pump_name + " " + stationDetail.fuel_filling_columns.get(i).pumps.get(j).location;
                tmp.setLineName(name);
                tmp.setPumpStatus((stationDetail.fuel_filling_columns.get(i).pumps.get(j).is_actived == 1)?"OK":"Not OK");
                pumpList.add(tmp);
            }

            gasPump.setPumpList(pumpList);


            gasPumpsList.add(gasPump);
        }
        showData();
        showDataTotal();
        swipeLayout.setRefreshing(false);
    }

    /**
     * Create events for app
     */
    private void createEvents() {
        //Refresh data
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    gasPumpsList.clear();
                    getData();
                }catch (Exception e){}
            }
        });
        //List view
        lvGasPump.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = GasPumpManageDetailActivity.buildIntent(getActivity());
                Bundle bundle = new Bundle();
                bundle.putSerializable("GAS_PUMP_DETAIL", gasPumpsList.get(i));
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
    }

    /**
     * Create widgets for app
     */
    private void createWidgets() {
        swipeLayout.setColorSchemeResources(R.color.colorOrange, R.color.colorBlue, R.color.colorGreen);
    }

    /**
     * Show data
     */
    private void showData() {
        if(gasPumpsList != null && gasPumpsList.size() > 0) {
            if(adapter == null) {
                adapter = new GasPumpManageAdapter(getActivity(),gasPumpsList);
                lvGasPump.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Show data total
     */
    private void showDataTotal(){
        if(gasPumpsList != null && gasPumpsList.size() > 0) {
            //Show gas pump total
            txtNumberPump.setText(String.valueOf(gasPumpsList.size()));
            //Show gas pump line total
            int number = 0;
            for(int i = 0; i < gasPumpsList.size(); i++){
                number += gasPumpsList.get(i).getNumberLine();
            }
            txtNumberPumpLine.setText(String.valueOf(number));
        }
    }

}
