package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.Tank;
import fpt.isc.nshreport.utilities.AppUtilities;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by Chick on 1/3/2018.
 */

public class TankAdapterNew extends BaseExpandableListAdapter {
    private Activity mcontext = null;
    private List<String> headerDataList;
    private List<List<Tank>> childDataList;
    private LayoutInflater layoutInflater;

    public TankAdapterNew(Activity mcontext, List<String> headerDataList, List<List<Tank>> childDataList) {
        this.mcontext = mcontext;
        this.headerDataList = headerDataList;
        this.childDataList = childDataList;
        this.layoutInflater = LayoutInflater.from(mcontext);
    }


    @Override
    public int getGroupCount() {
        return headerDataList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childDataList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headerDataList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childDataList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HeaderViewHolder headerHolder;
        if (convertView == null) {
            LayoutInflater inflater = this.mcontext.getLayoutInflater();
            convertView = inflater.inflate(R.layout.activity_sales_report_detail_parent_item, null);
            headerHolder = new HeaderViewHolder(convertView);
            convertView.setTag(headerHolder);
        } else {
            headerHolder = (HeaderViewHolder) convertView.getTag();
        }
        //Set data
        headerHolder.imgIndicator.setSelected(isExpanded);
        String header = (String) getGroup(groupPosition);
        if (header != null) {
            headerHolder.txtName.setText(header);
        }
        //Return
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        //tanks
        if (groupPosition == 0) {
            ViewHolder_Tank holder = null;
            convertView = layoutInflater.inflate(R.layout.item_tank, parent, false);
            holder = new ViewHolder_Tank(convertView);
            convertView.setTag(holder);
            /*if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_tank, parent, false);
                holder = new ViewHolder_Tank(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder_Tank) convertView.getTag();
            }*/

            Tank Object = childDataList.get(groupPosition).get(childPosition);
            holder.tv_title.setText(Object.getTitle());
            holder.pump_name.setText(Object.getName());
            holder.num_litre.setText(AppUtilities.longTypeTextFormat(Object.getLitre()) + " lít");
            holder.waveLoadingView.setCenterTitle(String.valueOf(Object.getPercent()) + " %");
            holder.waveLoadingView.setProgressValue(Math.round(Object.getPercent()));

            if (Math.round(Object.getPercent()) < 35) {
                holder.waveLoadingView.setBorderColor(ContextCompat.getColor(mcontext, R.color.colorRed));
                holder.waveLoadingView.setWaveColor(ContextCompat.getColor(mcontext, R.color.colorRed));
            } else if (Math.round(Object.getPercent()) < 70) {
                holder.waveLoadingView.setBorderColor(ContextCompat.getColor(mcontext, R.color.colorWarning));
                holder.waveLoadingView.setWaveColor(ContextCompat.getColor(mcontext, R.color.colorWarning));
            } else {
                holder.waveLoadingView.setBorderColor(ContextCompat.getColor(mcontext, R.color.colorGreen));
                holder.waveLoadingView.setWaveColor(ContextCompat.getColor(mcontext, R.color.colorGreen));
            }
        } else {//product_other
            ViewHolder_Product holder = null;
            convertView = layoutInflater.inflate(R.layout.item_shift_add_other, parent, false);
            holder = new ViewHolder_Product(convertView);
            convertView.setTag(holder);

            /*if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.item_shift_add_other, parent, false);
                holder = new ViewHolder_Product(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder_Product) convertView.getTag();
            }*/

            Tank Object = childDataList.get(groupPosition).get(childPosition);
            holder.txt_title.setText(Object.getProduct_name());
            holder.txt_unit.setText(Object.getUnit());
            holder.txt_num.setText(AppUtilities.longTypeTextFormat(Object.getInventory()));

            if(Object.getImage() != null && Object.getImage().length() > 3)
            {
                Picasso.with(mcontext).load(Object.getImage()).fit().centerInside().into(holder.img_detail);
            }
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    final class ViewHolder_Tank {
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.pump_name)
        TextView pump_name;
        @BindView(R.id.num_litre)
        TextView num_litre;
        @BindView(R.id.waveLoadingView)
        WaveLoadingView waveLoadingView;

        private ViewHolder_Tank(View parent) {
            ButterKnife.bind(this, parent);
        }
    }

    final class ViewHolder_Product {
        @BindView(R.id.txt_title)
        TextView txt_title;
        @BindView(R.id.txt_unit)
        TextView txt_unit;
        @BindView(R.id.txt_num)
        TextView txt_num;
        @BindView(R.id.img_detail)
        RoundedImageView img_detail;

        private ViewHolder_Product(View parent) {
            ButterKnife.bind(this, parent);
        }
    }

    class HeaderViewHolder {
        @BindView(R.id.txtNameParentItemSalesReportDetail)
        TextView txtName;
        @BindView(R.id.imgGroupIndicatorItemSalesReportDetail)
        ImageView imgIndicator;

        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
