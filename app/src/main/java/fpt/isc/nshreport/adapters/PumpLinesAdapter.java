package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.Dialog.ListImageFragment;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.utilities.AppUtilities;

/**
 * Created by PhamTruong on 01/06/2017.
 */

public class PumpLinesAdapter extends RecyclerView.Adapter<PumpLinesAdapter.ViewHolder> {
    //Declares variables
    private Context context;
    private ArrayList<PumpLinesAdd> pumpLinesList = null;
    private FragmentManager fm;
    private int salesReportId = 0;
    private int itemWidth;

    //Constructor
    public PumpLinesAdapter(Context context, ArrayList<PumpLinesAdd> arr, int salesReportId) {
        this.context = context;
        this.pumpLinesList = arr;
        this.salesReportId = salesReportId;

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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View cardView = inflater.inflate(R.layout.activity_sales_report_detail_child_item, null, false);
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
        final PumpLinesAdd pumpLine = pumpLinesList.get(position);

        //set width for item
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        holder.layout_root.setLayoutParams(params);

        holder.txtName.setText(pumpLine.getLineName());
        holder.txtReportLitre.setText(AppUtilities.longTypeTextFormat(pumpLine.getReportNum()) + " lít");
        holder.txtCheckedLitre.setText(AppUtilities.longTypeTextFormat(pumpLine.getApproveNum()) + " lít");
        holder.txtTotal.setText(AppUtilities.longTypeTextFormat(pumpLine.getSalesPrice() * pumpLine.getSalesLitre()) + " đ");

        //set image
        if (pumpLine.getPhotos()!= null && pumpLine.getPhotos() != "") {
            Picasso.with(context).load(pumpLine.getPhotos()).fit().centerInside().into(holder.imgView);
        }


        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pumpLine != null && pumpLine.getImageList().size() > 0) {

                    //Set DialogFragment
                    ListImageFragment listImagesFragment = new ListImageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", (Serializable) pumpLine.getImageList());
                    listImagesFragment.setArguments(bundle);
                    listImagesFragment.setCancelable(true);
                    // Show DialogFragment
                    listImagesFragment.show(fm, "list_images_fragment");
                }
            }
        });
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
        @BindView(R.id.layout_root)
        LinearLayout layout_root;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
