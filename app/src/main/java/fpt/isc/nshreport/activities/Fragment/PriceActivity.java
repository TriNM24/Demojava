package fpt.isc.nshreport.activities.Fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.PriceAdapter;
import fpt.isc.nshreport.models.objectParse.PriceList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class PriceActivity extends BaseFragment {

    @BindView(R.id.lv_data)
    ListView lvData;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe;
    private PriceAdapter adapter;
    private List<PriceList.data> dataObject = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.activity_prices, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.activity_title_price);
        initView(view);
        initEvent();
        getData();
    }
    private void initView(View view)
    {
        swipe.setColorSchemeResources(R.color.colorOrange, R.color.colorGreen, R.color.colorBlue);
    }
    private void initEvent()
    {
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    dataObject.clear();
                    getData();
                }catch(Exception ex){}
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
                NSHServer.getServer().priceList(NSHSharedPreferences.getToken(getContext()))
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

    private void handleResponse(PriceList priceList) {
        if(priceList.status.equals("success") && priceList.data.size() > 0)
        {
            dataObject.addAll(priceList.data);
            if(adapter == null)
            {
                adapter = new PriceAdapter(getContext(),dataObject);
                lvData.setAdapter(adapter);
            }else
            {
                adapter.notifyDataSetChanged();
            }
        }
        swipe.setRefreshing(false);
    }
}
