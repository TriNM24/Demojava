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
import fpt.isc.nshreport.models.PumpLinesAdd;

/**
 * Created by PhamTruong on 13/06/2017.
 */

public class GasPumpManageDetailAdapter extends ArrayAdapter<PumpLinesAdd> {
    //Declares variables
    private Activity context = null;
    private ArrayList<PumpLinesAdd> pumpLinesList = null;
    private int layoutId;

    //Constructor
    public GasPumpManageDetailAdapter(Activity context, int layoutId, ArrayList<PumpLinesAdd> arr){
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.pumpLinesList = arr;
    }

    //Set View
    public View getView(int position, View convertView, ViewGroup parrent){
        //Set layout
        GasPumpManageDetailHolder holder;
        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            holder = new GasPumpManageDetailHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GasPumpManageDetailHolder) convertView.getTag();
        }
        //Set data
        if(pumpLinesList.size() > 0 && position >= 0){
            final PumpLinesAdd pumpLine = pumpLinesList.get(position);
            holder.txtName.setText(pumpLine.getLineName());
            holder.txtStatus.setText(pumpLine.getPumpStatus());
        }
        //Return
        return convertView;
    }

    //Set Widgets
    class GasPumpManageDetailHolder{
        @BindView(R.id.txtNameItemGasPumpManageDetail)
        TextView txtName;
        @BindView(R.id.txtStatusItemGasPumpManageDetail)
        TextView txtStatus;

        public GasPumpManageDetailHolder(View parent) {
            ButterKnife.bind(this, parent);
        }
    }
}
