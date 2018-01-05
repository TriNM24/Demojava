package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import fpt.isc.nshreport.models.PumpLinesAdd;

/**
 * Created by PhamTruong on 31/05/2017.
 */

public class SalesReportDetailAdapter02 extends BaseExpandableListAdapter {
    //Declares variables
    private Activity context = null;
    private ArrayList<GasPumps> headerDataList = null;
    private HashMap<GasPumps, ArrayList<PumpLinesAdd>> childDataList = null;
    private int saleReportId = 0;

    //Constructor
    public SalesReportDetailAdapter02(Activity context, ArrayList<GasPumps> headerList, HashMap<GasPumps, ArrayList<PumpLinesAdd>> childList, int saleReportId){
        this.context = context;
        this.headerDataList = headerList;
        this.childDataList = childList;
        this.saleReportId = saleReportId;
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
            convertView = inflater.inflate(R.layout.activity_sales_report_detail_child_item_02, null);
            childHolder = new ChildViewHolder();
            //Child item display with horizontal view
            childHolder.horizontalListView = (RecyclerView) convertView.findViewById(R.id.pumplineslayout);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false);
            childHolder.horizontalListView.setLayoutManager(layoutManager);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildViewHolder) convertView.getTag();
        }

        //Set data
        try {
            ArrayList<PumpLinesAdd> resultList = this.childDataList.get(this.headerDataList.get(groupPosition));
            PumpLinesAdapter pumpLinesAdapter = new PumpLinesAdapter(this.context, resultList, this.saleReportId);
            childHolder.horizontalListView.setAdapter(pumpLinesAdapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Return
        return convertView;
    }

    //Set widgets for Child view
    private class ChildViewHolder{
        RecyclerView horizontalListView;
    }

    /**
     * Set up Header view
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        //return this.childDataList.get(this.headerDataList.get(groupPosition)).size();
        //Since the parent element going to have only one RecyclerView
        return 1;
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
            headerHolder = new HeaderViewHolder(convertView);
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
    class HeaderViewHolder{
        @BindView(R.id.txtNameParentItemSalesReportDetail)
        TextView txtName;
        @BindView(R.id.imgGroupIndicatorItemSalesReportDetail)
        ImageView imgIndicator;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this,view);
        }
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
