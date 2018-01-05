package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.BaseActivity;
import fpt.isc.nshreport.models.PumpLinesAdd;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.FileUtils;
import fpt.isc.nshreport.utilities.NumberTextWatcherForThousand;

/**
 * Created by PhamTruong on 01/06/2017.
 */

public class PumpLinesCloseAdapter extends RecyclerView.Adapter<PumpLinesCloseAdapter.ViewHolder> {
    //Declares variables
    private Context context;
    private ArrayList<PumpLinesAdd> pumpLinesList = null;
    private AppUtilities appUtilities = new AppUtilities();
    private int group_item;
    public static int group_pos;
    public static int child_pos;
    private int itemWidth = 0;

    //Constructor
    public PumpLinesCloseAdapter(Context context, ArrayList<PumpLinesAdd> arr, int position) {
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
        View cardView = inflater.inflate(R.layout.item_child_shift_close, null, false);
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

            holder.editLitre.addTextChangedListener(new NumberTextWatcherForThousand(holder.editLitre));

            //set width for item
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            holder.layout_root.setLayoutParams(params);

            //set data
            holder.txtName.setText(pumpLine.getLineName());

            holder.txtPreNum.setText(AppUtilities.longTypeTextFormat(pumpLine.getReportNumPre()));

            holder.editLitre.setText(String.valueOf(pumpLine.getSalesLitre()));

            holder.txtSaledNum.setText(AppUtilities.longTypeTextFormat(pumpLine.getSalesAmount()));
            //set image
            if (pumpLine.getPhotos()!= null && pumpLine.getPhotos().length() > 0) {
                Picasso.with(context).load(pumpLine.getPhotos()).fit().centerInside().into(holder.imgPhoto);
            }
            //Events text change
            holder.editLitre.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    String strAfter = NumberTextWatcherForThousand.trimCommaOfString(holder.editLitre.getText().toString());
                    if(strAfter.trim().length() > 0) {
                        try {
                            long value = Long.valueOf(strAfter.trim());
                            pumpLinesList.get(position).setSalesLitre(value);
                            long valuePre = Long.valueOf(holder.txtPreNum.getText().toString().replace(",",""));
                            long sold = value - valuePre;
                            holder.txtSaledNum.setText(AppUtilities.longTypeTextFormat(sold));
                            pumpLinesList.get(position).setSalesAmount(sold);
                            if(sold > 0) {
                                //nhap lai anh
                                pumpLinesList.get(position).setImageID(0);
                            }else
                            {
                                //khong nhap lai anh
                                pumpLinesList.get(position).setImageID(1);
                            }
                        }catch (NumberFormatException e)
                        {
                            ((BaseActivity) context).showMessage("Không phải số", SweetAlertDialog.WARNING_TYPE);
                        }
                    }

                }
            });
            //Get photo from gallery
            holder.imgAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Set value for group item
                    group_pos = group_item;
                    //Set value for child item
                    child_pos = position;
                    //Intent intent = new Intent();
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), appUtilities.ALBUM_PHOTOS);
                }
            });
            //Get photo from camera
            holder.imgCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Set value for group item
                    group_pos = group_item;
                    //Set value for child item
                    child_pos = position;
                    /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    ((Activity) context).startActivityForResult(intent, appUtilities.REQUEST_CAMERA);*/
                    try {
                        Uri uri = FileUtils.createImageFile(context);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        ((Activity) context).startActivityForResult(intent, appUtilities.REQUEST_CAMERA);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
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
        @BindView(R.id.txt_pre_num)
        TextView txtPreNum;
        @BindView(R.id.txt_saled_num)
        TextView txtSaledNum;
        @BindView(R.id.txtLitreChildItemSalesReportAdd)
        EditText editLitre;
        @BindView(R.id.imgPhotoChildItemSalesReportAdd)
        ImageView imgPhoto;
        @BindView(R.id.imgCameraChildItemSalesReportAdd)
        ImageView imgCamera;
        @BindView(R.id.imgAlbumChildItemSalesReportAdd)
        ImageView imgAlbum;
        @BindView(R.id.layout_root)
        LinearLayout layout_root;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
