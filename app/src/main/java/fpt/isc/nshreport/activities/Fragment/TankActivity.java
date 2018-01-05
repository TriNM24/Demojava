package fpt.isc.nshreport.activities.Fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.TankAdapterNew;
import fpt.isc.nshreport.models.Tank;
import fpt.isc.nshreport.models.objectParse.TankList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class TankActivity extends BaseFragment {
    @BindView(R.id.lv_data)
    //ListView lvData;
    ExpandableListView lvData;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe;
    //private TankAdapter adapter;
    //private List<Tank> dataObject = new ArrayList<>();
    private TankAdapterNew adapter;
    private List<String> dataHeader;
    private List<List<Tank>> dataChild = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_tank, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.activity_title_inventory);
        initView();
        initEvent();
        getData();
    }
    private void initView()
    {
        swipe.setColorSchemeResources(R.color.colorOrange, R.color.colorGreen, R.color.colorBlue);
        dataHeader = new ArrayList<>();
        dataHeader.add("Xăng dầu");
        dataHeader.add("Sản phẩm khác");
    }
    private void initEvent()
    {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    dataChild.clear();
                    getData();
                }catch (Exception e){}
            }
        });
    }

    private void getData()
    {
        swipe.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipe.setRefreshing(true);
            }
        });

        mCompositeDisposable.add(
                NSHServer.getServer().tankList(NSHSharedPreferences.getToken(getContext()))
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse,this::handleError));
    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        swipe.setRefreshing(false);
    }

    private void handleResponse(TankList tankListList) {
        if(tankListList.status.equals("success") && tankListList.data.tanks.size() > 0)
        {
            Tank temp;
            List<Tank> dataTanks = new ArrayList<>();
            for(int i=0;i<tankListList.data.tanks.size();i++)
            {
                temp = new Tank();
                temp.setTank(true);
                temp.setTitle(tankListList.data.tanks.get(i).tank_code);
                temp.setName(tankListList.data.tanks.get(i).product_name);
                temp.setLitre(tankListList.data.tanks.get(i).quantity);
                temp.setPercent(tankListList.data.tanks.get(i).percent);
                dataTanks.add(temp);
            }
            dataChild.add(dataTanks);

            List<Tank> dataProduct = new ArrayList<>();
            if(tankListList.data.product_others.size() > 0)
            {
                for(int i=0;i<tankListList.data.product_others.size();i++) {
                    temp = new Tank();
                    temp.setTank(false);
                    temp.setProduct_name(tankListList.data.product_others.get(i).product_name);
                    temp.setUnit(tankListList.data.product_others.get(i).unit);
                    temp.setInventory(tankListList.data.product_others.get(i).inventory);
                    dataProduct.add(temp);
                }
            }
            dataChild.add(dataProduct);

            if(adapter == null)
            {
                //adapter = new TankAdapter(getContext(),dataObject);
                adapter = new TankAdapterNew(getActivity(),dataHeader,dataChild);
                lvData.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
            //set expend first
            lvData.expandGroup(0);
        }
        swipe.setRefreshing(false);
    }
}
