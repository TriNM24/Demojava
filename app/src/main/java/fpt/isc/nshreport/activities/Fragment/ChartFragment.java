package fpt.isc.nshreport.activities.Fragment;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.animation.Animation;
import com.db.chart.model.LineSet;
import com.db.chart.tooltip.Tooltip;
import com.db.chart.util.Tools;
import com.db.chart.view.LineChartView;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.EventBus.UpdateTextDetail;
import fpt.isc.nshreport.models.objectParse.ProductList;

/**
 * Created by PhamTruong on 25/05/2017.
 */

public class ChartFragment extends BaseFragment {

    private Tooltip mTip;

    @BindView(R.id.chart)
    LineChartView chartView;

    List<String> colorList = new ArrayList<>(Arrays.asList("#009900", "#000099", "#FF0000","#008080","#00ff00","#990000","#ffa500"));

    /*@BindView(R.id.layout_root)
    LinearLayout layoutRoot;*/


    boolean isLitre = false;
    List<String> dateList;
    ProductList data;

    public static ChartFragment newInstance(boolean isLitre, List<String> dateList, ProductList data) {

        Bundle args = new Bundle();
        args.putBoolean("isLitre", isLitre);
        args.putSerializable("dateList", (Serializable) dateList);
        args.putSerializable("data",data);
        ChartFragment fragment = new ChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.isLitre = getArguments().getBoolean("isLitre");
        this.dateList = (List<String>) getArguments().getSerializable("dateList");
        this.data = (ProductList) getArguments().getSerializable("data");
        initView();
        initChart();
    }

    private void initView() {
        /*ViewPager.LayoutParams params = new ViewPager.LayoutParams(
                ViewPager.LayoutParams.MATCH_PARENT,
                ViewPager.LayoutParams.MATCH_PARENT);
        params.height = heightChart;
        params.leftMargin = (int) Tools.fromDpToPx(10);
        params.rightMargin = (int) Tools.fromDpToPx(10);
        //adding attributes to the imageview
        layoutRoot.setLayoutParams(params);*/

        mTip = new Tooltip(getActivity(), R.layout.linechart_litre_tooltip, R.id.value);
        if(!isLitre) mTip = new Tooltip(getActivity(), R.layout.linechart_money_tooltip, R.id.value);
        mTip.setVerticalAlignment(Tooltip.Alignment.BOTTOM_TOP);
        mTip.setDimensions((int) Tools.fromDpToPx(150), (int) Tools.fromDpToPx(25));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            mTip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)).setDuration(200);

            mTip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f)).setDuration(200);

            mTip.setPivotX(Tools.fromDpToPx(65) / 2);
            mTip.setPivotY(Tools.fromDpToPx(25));
        }
    }

    private void initChart() {

        //----------------------get labels for line chart----------------------
        List<String> dateTmp = new ArrayList<>();
        SimpleDateFormat formatResult = new SimpleDateFormat("dd/MM");
        SimpleDateFormat formatInput = new SimpleDateFormat("yyyy-MM-dd");
        Date tmp;

        for(int i=0;i<dateList.size();i++)
        {
            try {
                tmp = formatInput.parse(dateList.get(i));
                dateTmp.add(formatResult.format(tmp));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String[] mLabels = dateTmp.toArray(new String[0]);
        //----------------------get labels for line chart----------------------

        //----------------------get data for each line----------------------
        List<float[]> mValues = new ArrayList<>();
        float minNumber = 0;
        float maxNumber = 0;
        float[] tmpValue;
        List<String> productNames = new ArrayList<>();
        for(int i=0;i<data.data.products.size();i++)
        {
            productNames.add(data.data.products.get(i).product_short_name);
            tmpValue = new float[]{0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
            for(int j=0;j<data.data.products.get(i).report.size();j++)
            {
                String date = data.data.products.get(i).report.get(j).date;

                float numberReport = data.data.products.get(i).report.get(j).total_liter;
                if(!isLitre) numberReport = Float.valueOf(data.data.products.get(i).report.get(j).total_money);

                if(numberReport < minNumber) minNumber = numberReport;
                if(numberReport > maxNumber) maxNumber = numberReport;
                for(int k=0;k<dateList.size();k++)
                {
                    if(dateList.get(k).equals(date))
                    {
                        tmpValue[k] = numberReport;
                    }
                }
            }
            mValues.add(tmpValue);
        }
        //----------------------get data for each line----------------------

        //decrease number if it lager than 1 billion
        if(maxNumber > 1000000000)
        {
            maxNumber = maxNumber/1000;
            minNumber = minNumber/1000;
            for(int i=0;i<mValues.size();i++)
            {
                for(int j=0;j<mValues.get(i).length;j++)
                {
                    mValues.get(i)[j] = mValues.get(i)[j]/ 1000;
                }
            }
        }

        //----------------------set chart line----------------------
        float stepNum =Math.round ((maxNumber - minNumber)/10);

        stepNum = roundDown(stepNum);

        minNumber = roundDown(minNumber);

        if(minNumber < maxNumber && stepNum > 0)
        chartView.setAxisBorderValues(minNumber, maxNumber, stepNum);

        //++++++++++++++++++++test++++++++++++++++++++
        String dataText = "";
        LineSet dataset;
        if(mValues.size() > 0) {
            for (int i = 0; i < mValues.size(); i++) {
                dataset = new LineSet(mLabels, mValues.get(i));
                int colorIndex = i % colorList.size();
                dataset.setColor(Color.parseColor(colorList.get(colorIndex)))
                        .setDotsColor(Color.parseColor("#D70900"));
                chartView.addData(dataset);

                /*String styledText = "<font color='#009900'>DO</font> - <font color='#000099'>A92</font> - <font color='#FF0000'>A95</font>";
                txtDetail.setText(Html.fromHtml(styledText), TextView.BufferType.SPANNABLE);*/
                //set text detail
                dataText += "<font color='" + colorList.get(colorIndex) + "'>" + productNames.get(i) + "</font> -";
            }
            dataText =  dataText.substring(0,dataText.length()-1);
            EventBus.getDefault().postSticky(new UpdateTextDetail(dataText));

        }else
        {
            //default line
            float[] defalutValue = new float[]{0f,0f,0f,0f,0f,0f,0f,0f,0f,0f};
            dataset = new LineSet(mLabels, defalutValue);
            dataset.setColor(Color.parseColor(colorList.get(0)))
                    .setDotsColor(Color.parseColor("#D70900"));
            chartView.addData(dataset);

            //test
            dataText += "<font color='" + colorList.get(0) + "'>" + "AA" + "</font> -";
            dataText += "<font color='" + colorList.get(1) + "'>" + "BB" + "</font> -";
            dataText.substring(0,dataText.length()-1);
            EventBus.getDefault().postSticky(new UpdateTextDetail(dataText));
        }

        chartView.setTooltips(mTip);
        chartView.show(new Animation().withEndAction(unlockAction));
    }

    private float roundDown(float number)
    {
        for(int i=1;i<20; i++){
            int tmp = (int) Math.round(number/Math.pow(10,i));
            if(Math.abs(tmp) < 10)
            {
                return (float) (tmp*Math.pow(10,i));
            }
        }
        return 0;
    }

    private final Runnable unlockAction = new Runnable() {
        @Override
        public void run() {

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //an hien button
                    //mTip.prepare(mChart.getEntriesArea(0).get(3), mValues[0][3]);
                    //mChart.showTooltip(mTip, true);
                }
            }, 500);
        }
    };
}
