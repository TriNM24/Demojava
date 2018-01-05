package fpt.isc.nshreport.activities.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.DashboardAdapter;
import fpt.isc.nshreport.models.Dashboard;
import fpt.isc.nshreport.models.objectParse.ProductList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class DashboadActivity extends BaseFragment {
    @BindView(R.id.txt_totoal)
    TextView txt_total;
    @BindView(R.id.txt_no_data)
    TextView txt_no_data;
    @BindView(R.id.list_dashboard)
    ListView lv;
    private DashboardAdapter adapter;
    List<Dashboard> list = new ArrayList<>();
    List<String> colorList = new ArrayList<>(Arrays.asList("#009900","#000099","#FF0000"));
    //test
    private String date;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe;
    protected HomeActivity mainFragment;

    public DashboadActivity() {
    }

    @SuppressLint("ValidFragment")
    public DashboadActivity(HomeActivity mainFragment) {
        this.mainFragment = mainFragment;
    }

    public static DashboadActivity newInstance(String date, HomeActivity mainFragment_) {

        Bundle args = new Bundle();
        args.putString("date", date);
        DashboadActivity fragment = new DashboadActivity(mainFragment_);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        date = getArguments().getString("date");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        swipe.setColorSchemeResources(R.color.colorOrange, R.color.colorGreen, R.color.colorBlue);
        swipe.setEnabled(false);
    }

    private void getData() {

        mainFragment.showLoading();

        //String tmp = NSHSharedPreferences.getToken(getContext());

        mCompositeDisposable.add(
                NSHServer.getServer().productList(NSHSharedPreferences.getToken(getContext()), date)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));
    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(),"Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
        swipe.setRefreshing(false);
    }

    private void handleResponse(ProductList rs) {

        if(rs.status.equals("success"))
        {
            if(rs.data.products != null && rs.data.products.size() > 0) {
                if (rs.data.products.get(0).report != null && rs.data.products.get(0).report.size() > 0) {
                    txt_no_data.setVisibility(View.GONE);
                    for (int i = 0; i < rs.data.products.size(); i++) {
                        list.add(new Dashboard(rs.data.products.get(i).product_name,
                                AppUtilities.LongTypeTextFormat(rs.data.products.get(i).report.get(0).total_liter) + " lít",
                                "$ " + AppUtilities.StringNumerTextFormat(rs.data.products.get(i).report.get(0).total_money), colorList.get(i % 3)));
                    }
                    txt_total.setText("$ " + AppUtilities.longTypeTextFormat(rs.data.total_money));
                    adapter = new DashboardAdapter(getContext(), list);
                    lv.setAdapter(adapter);
                } else {
                    txt_no_data.setVisibility(View.VISIBLE);
                }
            }

        }
        mainFragment.hideLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        list.clear();
        getData();
    }
}
