package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.GasPumps;
import fpt.isc.nshreport.models.PumpLines;
import fpt.isc.nshreport.utilities.AppUtilities;

/**
 * Created by PhamTruong on 31/05/2017.
 */

public class SalesReportDetailAdapter extends BaseExpandableListAdapter {
    //Declares variables
    private Activity context = null;
    private ArrayList<GasPumps> headerDataList = null;
    private HashMap<GasPumps, ArrayList<PumpLines>> childDataList = null;

    //Constructor
    public SalesReportDetailAdapter(Activity context, ArrayList<GasPumps> headerList, HashMap<GasPumps, ArrayList<PumpLines>> childList){
        this.context = context;
        this.headerDataList = headerList;
        this.childDataList = childList;
    }

    /**
     * Set up Child view
     */
    @Override
    public Object getChild(int groupPosition, int childPosition){
        return this.childDataList.get(this.headerDataList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Set layout
        ChildViewHolder childHolder;
        if (convertView == null) {
            LayoutInflater inflater = this.context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.activity_sales_report_detail_child_item, null);
            childHolder = new ChildViewHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildViewHolder) convertView.getTag();
        }
        //Set data
        final PumpLines pumpLine = (PumpLines) getChild(groupPosition, childPosition);
        if (pumpLine != null) {
            childHolder.txtName.setText(pumpLine.getLineName());
            childHolder.txtReportLitre.setText(AppUtilities.longTypeTextFormat(pumpLine.getSalesLitre()) + " lít");
            childHolder.txtCheckedLitre.setText(AppUtilities.longTypeTextFormat(pumpLine.getCheckedLitre()) + " lít");
            childHolder.txtTotal.setText(AppUtilities.longTypeTextFormat(pumpLine.getSalesAmount()) + " đ");
        }
        //Return
        return convertView;
    }
    //Set widgets for Child view
    class ChildViewHolder{
        @BindView(R.id.txtNameChildItemSalesReportDetail)
        TextView txtName;
        @BindView(R.id.txtReportLitreChildItemSalesReportDetail)
        TextView txtReportLitre;
        @BindView(R.id.txtCheckedLitreChildItemSalesReportDetail)
        TextView txtCheckedLitre;
        @BindView(R.id.txtTotalChildItemSalesReportDetail)
        TextView txtTotal;
        @BindView(R.id.imgChildItemSalesReportDetail)
        ImageView imgView;

        public ChildViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
    }

    /**
     * Set up Header view
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childDataList.get(this.headerDataList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerDataList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headerDataList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Set layout
        HeaderViewHolder headerHolder;
        if (convertView == null) {
            LayoutInflater inflater = this.context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.activity_sales_report_detail_parent_item, null);
            headerHolder = new HeaderViewHolder();
            headerHolder.txtName = (TextView) convertView.findViewById(R.id.txtNameParentItemSalesReportDetail);
            headerHolder.imgIndicator = (ImageView) convertView.findViewById(R.id.imgGroupIndicatorItemSalesReportDetail);
            convertView.setTag(headerHolder);
        } else {
            headerHolder = (HeaderViewHolder) convertView.getTag();
        }
        //Set data
        headerHolder.imgIndicator.setSelected(isExpanded);
        GasPumps gasPump = (GasPumps) getGroup(groupPosition);
        if (gasPump != null) {
            headerHolder.txtName.setText(gasPump.getName());
        }
        //Return
        return convertView;
    }

    //Set widgets for Header view
    private class HeaderViewHolder{
        TextView txtName;
        ImageView imgIndicator;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
