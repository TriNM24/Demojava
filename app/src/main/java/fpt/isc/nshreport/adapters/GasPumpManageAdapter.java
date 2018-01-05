package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.GasPumps;

/**
 * Created by PhamTruong on 12/06/2017.
 */

public class GasPumpManageAdapter extends BaseAdapter {
    //Declares variables
    private Activity context = null;
    private List<GasPumps> gasPumpsList = null;
    private LayoutInflater layoutInflater;

    public GasPumpManageAdapter(Activity context, List<GasPumps> arr){

        this.context = context;
        this.gasPumpsList = arr;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return gasPumpsList.size();
    }

    @Override
    public Object getItem(int position) {
        return gasPumpsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    final class ViewHolder{
        @BindView(R.id.txtNameItemGasPumpManage)
        TextView txtName;
        @BindView(R.id.txtStatusItemGasPumpManage)
        TextView txtStatus;
        @BindView(R.id.txtPumpLineItemGasPumpManage)
        TextView txtPumpNumber;

        private ViewHolder(View parent){
            ButterKnife.bind(this, parent);
            /*txtName = (TextView) parent.findViewById(R.id.txtNameItemGasPumpManage);
            txtStatus = (TextView) parent.findViewById(R.id.txtStatusItemGasPumpManage);
            txtPumpNumber = (TextView) parent.findViewById(R.id.txtPumpLineItemGasPumpManage);*/
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.activity_gas_pump_manage_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        GasPumps gasPump = gasPumpsList.get(position);
        holder.txtName.setText(gasPump.getName());
        holder.txtStatus.setText(gasPump.getPumpStatus());
        holder.txtPumpNumber.setText(String.valueOf(gasPump.getNumberLine()));

        return convertView;
    }
}
