package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.utilities.AppUtilities;

/**
 * Created by PhamTruong on 01/06/2017.
 */

public class PumpLinesDetailAdapter extends RecyclerView.Adapter<PumpLinesDetailAdapter.ViewHolder> {
    //Declares variables
    private Context context;
    private ArrayList<PumpLinesAdd> pumpLinesList = null;
    private AppUtilities appUtilities = new AppUtilities();
    private int itemWidth = 0;
    private int group_item;
    public static int group_pos;
    public static int child_pos;

    //Constructor
    public PumpLinesDetailAdapter(Context context, ArrayList<PumpLinesAdd> arr, int position) {
        this.context = context;
        this.pumpLinesList = arr;
        this.group_item = position;
        //get width
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        itemWidth = dm.widthPixels/2;
    }

    /**
     * Create view holder for recycle view
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView = inflater.inflate(R.layout.item_child_shift_detail, null, false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    /**
     * Set data for recycle view
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PumpLinesAdd pumpLine = pumpLinesList.get(position);
        if(pumpLine != null) {
            holder.txtName.setText(pumpLine.getLineName());
            if(pumpLine.getSalesLitre() != 0){
                //holder.editLitre.setText(String.valueOf(pumpLine.getSalesLitre()));
                holder.editLitre.setText(AppUtilities.longTypeTextFormat(pumpLine.getSalesLitre()));
            }
            //set image
            if (pumpLine.getPhotos()!= null && pumpLine.getPhotos().length() > 0) {
                Picasso.with(context).load(pumpLine.getPhotos()).fit().centerInside().into(holder.imgPhoto);
            }
            //set width for item
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.layout_root.setLayoutParams(params);
        }
    }

    /**
     * Get number of item in List
     * @return
     */
    @Override
    public int getItemCount() {
        return pumpLinesList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Create widgets
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Declares variables
        @BindView(R.id.txtNameChildItemSalesReportAdd)
        TextView txtName;
        @BindView(R.id.txtLitreChildItemSalesReportAdd)
        TextView editLitre;
        @BindView(R.id.imgPhotoChildItemSalesReportAdd)
        ImageView imgPhoto;
        @BindView(R.id.layout_root)
        LinearLayout layout_root;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
