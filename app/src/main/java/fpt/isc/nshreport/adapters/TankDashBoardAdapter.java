package fpt.isc.nshreport.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.objectParse.TankList;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * Created by PhamTruong on 01/06/2017.
 */

public class TankDashBoardAdapter extends RecyclerView.Adapter<TankDashBoardAdapter.ViewHolder> {
    //Declares variables
    private Context context;
    private List<TankList.data.tanks> listObject = null;
    private FragmentManager fm;
    private int itemWidth = 0;

    //Constructor
    public TankDashBoardAdapter(Context context, List<TankList.data.tanks> arr,int itemWidth) {
        this.context = context;
        this.listObject = arr;
        this.itemWidth = itemWidth;
    }

    /**
     * Create view holder for recycle view
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView = inflater.inflate(R.layout.item_tank_home, null, false);
        fm = ((AppCompatActivity) context).getSupportFragmentManager();
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    /**
     * Set data for recycle view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TankList.data.tanks object = listObject.get(position);
        holder.txtName.setText(object.product_short_name);

        if(Math.round(object.percent) < 35) {
            holder.tank.setBorderColor(ContextCompat.getColor(context, R.color.colorRed));
            holder.tank.setWaveColor(ContextCompat.getColor(context, R.color.colorRed));
        }else if(Math.round(object.percent) < 70)
        {
            holder.tank.setBorderColor(ContextCompat.getColor(context, R.color.colorWarning));
            holder.tank.setWaveColor(ContextCompat.getColor(context, R.color.colorWarning));
        }else
        {
            holder.tank.setBorderColor(ContextCompat.getColor(context, R.color.colorGreen));
            holder.tank.setWaveColor(ContextCompat.getColor(context, R.color.colorGreen));
        }

        holder.tank.setCenterTitle(String.valueOf(object.percent) + " %");
        holder.tank.setProgressValue(Math.round(object.percent));

        //set width for item
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.root.setLayoutParams(params);
    }

    /**
     * Get number of item in List
     * @return
     */
    @Override
    public int getItemCount() {
        return listObject.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Create widgets
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        //Declares variables
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.waveLoadingView_tank)
        WaveLoadingView tank;
        @BindView(R.id.layout_root)
        LinearLayout root;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
