package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.SalesReport;
import fpt.isc.nshreport.utilities.AppUtilities;

/**
 * Created by PhamTruong on 30/05/2017.
 */

public class SalesReportAdapter extends ArrayAdapter<SalesReport> {
    //Declares variables
    private Activity context = null;
    private ArrayList<SalesReport> salesReportsList = null;
    private int layoutId;

    //Constructor
    public SalesReportAdapter(Activity context, int layoutId, ArrayList<SalesReport> arr){
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.salesReportsList = arr;
    }

    //Set View
    public View getView(int position, View convertView, ViewGroup parrent){
        //Set layout
        SalesReportHolder holder;
        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            holder = new SalesReportHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SalesReportHolder) convertView.getTag();
        }
        //Set data
        if(salesReportsList.size() > 0 && position >= 0){
            final SalesReport salesReport = salesReportsList.get(position);
            holder.salesDate.setText(salesReport.getSalesDate());
            holder.salesLitre.setText(AppUtilities.longTypeTextFormat(salesReport.getSalesLitre()) + " lít");
            holder.salesAmount.setText(AppUtilities.longTypeTextFormat(salesReport.getSalesAmount()) + " đ");
            holder.salesStatus.setText(salesReport.getSalesStatus());
            holder.salesMan.setText(salesReport.getCheckedMan());
        }
        //Return
        return convertView;
    }

    //Set Widgets
    class SalesReportHolder{
        @BindView(R.id.txtSalesDateItemSalesReport)
        TextView salesDate;
        @BindView(R.id.txtSalesLitreItemSalesReport)
        TextView salesLitre;
        @BindView(R.id.txtSalesAmountItemSalesReport)
        TextView salesAmount;
        @BindView(R.id.txtSalesStatusItemSalesReport)
        TextView salesStatus;
        @BindView(R.id.txtSalesManItemSalesReport)
        TextView salesMan;

        public SalesReportHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }
}
