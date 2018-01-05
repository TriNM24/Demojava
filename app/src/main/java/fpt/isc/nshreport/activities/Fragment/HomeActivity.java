package fpt.isc.nshreport.activities.Fragment;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.adapters.CustomViewPagerAdapter;
import fpt.isc.nshreport.adapters.TankDashBoardAdapter;
import fpt.isc.nshreport.models.EventBus.UpdateTextDetail;
import fpt.isc.nshreport.models.objectParse.ProductList;
import fpt.isc.nshreport.models.objectParse.TankList;
import fpt.isc.nshreport.services.NSHServer;
import fpt.isc.nshreport.utilities.NSHSharedPreferences;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class HomeActivity extends BaseFragment {

    @BindView(R.id.fragment_content_action_layout)
    ViewPager pager;
    private List<BaseFragment> listBaseFragments;
    private List<String> menu = new ArrayList<>();
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.txt_month)
    TextView txt_month;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe;
    /*@BindView(R.id.chart)
    LineChartView chartView;*/
    @BindView(R.id.layout_top)
    LinearLayout layout_top;
    private CustomViewPagerAdapter adapter;

    @BindView(R.id.tab_chart)
    TabLayout tabChart;
    @BindView(R.id.fragment_content_chart)
    ViewPager pagerChart;
    private CustomViewPagerAdapter adapterChart;

    private List<BaseFragment> listFragmentCharts;
    private List<TankList.data.tanks> listObject = new ArrayList<>();
    private TankDashBoardAdapter tankAdapter;
    @BindView(R.id.list_tank)
    RecyclerView tankList;

    @BindView(R.id.txt_detail)
    TextView txtDetail;


    private int itemWidth;

    private int heightScreem = 0;
    private int heightTxtMonth = 0;
    private int heightPagerChart = 0;

    //private Tooltip mTip;

    //new code
    private String startDate = "";
    private String endDate = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_home, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.activity_title_home);
        intitView(view);
        initViewPager();
        getData();

    }


    public void showLoading() {
        swipe.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                swipe.setRefreshing(true);
            }
        });
    }

    public void hideLoading() {
        swipe.setRefreshing(false);
    }

    public void getData() {
        //load month
        final Calendar cal = Calendar.getInstance();
        int month = (cal.get(Calendar.MONTH)) + 1;
        txt_month.setText("Tháng " + month);
        txt_month.setTag(String.valueOf(month));

        //get start and end date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE,-1);
        endDate = df.format(calendar.getTime());

        calendar.add(Calendar.DATE,-9);
        startDate = df.format(calendar.getTime());

        //test
        //startDate = "2017-09-15";
        //endDate = "2017-09-24";

        mCompositeDisposable.add(
                NSHServer.getServer().tankList(NSHSharedPreferences.getToken(getContext()))
                        .subscribeOn(Schedulers.computation())
                        .flatMap(tankList1 ->
                        {
                            listObject.addAll(tankList1.data.tanks);
                            return NSHServer.getServer().productListStartEnd(NSHSharedPreferences.getToken(getContext()),startDate,endDate);
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(this::handleResponse, this::handleError));

    }

    private void handleResponse(ProductList rs) {
        //listObject.addAll(rs.data);
        tankAdapter = new TankDashBoardAdapter(getContext(), listObject, itemWidth);
        tankList.setAdapter(tankAdapter);
        initChart(rs);
    }

    private void handleError(Throwable throwable) {
        //Toast.makeText(getContext(), throwable.toString(), Toast.LENGTH_SHORT).show();
        showMessage(throwable.getMessage(), "Lỗi không xác định", SweetAlertDialog.ERROR_TYPE);
    }

    private void intitView(View rs) {
        swipe.setColorSchemeResources(R.color.colorOrange, R.color.colorGreen, R.color.colorBlue);
        swipe.setEnabled(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        tankList.setLayoutManager(layoutManager);

        //get real width
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        itemWidth = displaymetrics.widthPixels / 3;
        heightScreem = displaymetrics.heightPixels;

        //get height of txt_month
        Rect bounds = new Rect();
        Paint textPaint = txt_month.getPaint();
        String text = "Thang 10";
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        heightTxtMonth = bounds.height() + (bounds.height() / 2);

        //set height for chart view
        layout_top.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightTop = layout_top.getHeight();
                if(heightTop >0) {
                    layout_top.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    heightPagerChart = heightScreem - heightTop - (heightTxtMonth * 7);
                    params.height = heightPagerChart;
                    //adding attributes to the imageview
                    pagerChart.setLayoutParams(params);
                    //EventBus.getDefault().postSticky("initChart");
                }
            }
        });

        //String styledText = "<font color='#009900'>DO</font> - <font color='#000099'>A92</font> - <font color='#FF0000'>A95</font>";
        //txtDetail.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);
    }

    public void setTextDetail(String text)
    {
        txtDetail.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }


    private int getPixelsToDP(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }


    private void initChart(ProductList rs) {

        List<String> dateList = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        for(int i=0;i<10;i++)
        {
            try
            {
                calendar.setTime(format.parse(startDate));
                calendar.add(Calendar.DATE,i);
                dateList.add(format.format(calendar.getTime()));
            }catch (Exception e){}
        }



        listFragmentCharts = new ArrayList<BaseFragment>();
        List<String> menu = new ArrayList<>();

        listFragmentCharts.add(ChartFragment.newInstance(true,dateList,rs));
        menu.add("Xăng dầu");
        listFragmentCharts.add(ChartFragment.newInstance(false,dateList,rs));
        menu.add("Doanh thu");

        adapterChart = new CustomViewPagerAdapter(getChildFragmentManager(), listFragmentCharts, menu, null);

        pagerChart.setAdapter(adapterChart);
        tabChart.setupWithViewPager(pagerChart);

    }

    private void initViewPager() {
        //set height for viewpager
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int height = (heightScreem / 2) - heightTxtMonth;
        params.height = height; //left, top, right, bottom
        //adding attributes to the imageview
        pager.setLayoutParams(params);


        listBaseFragments = new ArrayList<BaseFragment>();

        List<String> month = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            List<String> dayMonth = getPreviousDay(i);
            listBaseFragments.add(DashboadActivity.newInstance(dayMonth.get(2), this));
            menu.add(dayMonth.get(0));
            month.add(dayMonth.get(1));
        }


        adapter = new CustomViewPagerAdapter(getChildFragmentManager(), listBaseFragments, menu, month);

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String tmp = adapter.getMonth(position);
                String currentMonth = (String) txt_month.getTag();
                if (!tmp.equals(currentMonth)) {
                    txt_month.setText("Tháng " + tmp);
                    txt_month.setTag(tmp);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private List<String> getPreviousDay(int numBefore) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        List<String> rs = new ArrayList<>();
        int day, month;
        final Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, -numBefore);
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = (cal.get(Calendar.MONTH)) + 1;

        rs.add(day + "");
        rs.add(month + "");
        rs.add(format1.format(cal.getTime()));

        return rs;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UpdateTextDetail data) {
        //String styledText = "<font color='#009900'>DO</font> - <font color='#000099'>A92</font> - <font color='#FF0000'>A95</font>";
        txtDetail.setText(Html.fromHtml(data.getText()), TextView.BufferType.SPANNABLE);
    }
}
