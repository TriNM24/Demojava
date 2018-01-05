package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.ReportPhotos;

/**
 * Created by PhamTruong on 05/06/2017.
 */

public class PhotosListAdapter extends ArrayAdapter<ReportPhotos> {
    //Declares variables
    private Activity context = null;
    private ArrayList<ReportPhotos> photosArray= null;
    private int layoutId;

    //Constructor
    public PhotosListAdapter(Activity context, int layoutId, ArrayList<ReportPhotos> arr){
        super(context, layoutId, arr);
        this.context = context;
        this.layoutId = layoutId;
        this.photosArray = arr;
    }

    //Set View
    public View getView(int position, View convertView, ViewGroup parrent){
        //Set layout
        PhotosListHolder holder;
        if(convertView == null){
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(layoutId, null);
            holder = new PhotosListHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (PhotosListHolder) convertView.getTag();
        }
        //Set data
        if(photosArray.size() > 0 && position >= 0){
            final ReportPhotos photoItem = photosArray.get(position);
            holder.txtUpdateTime.setText(photoItem.getUpdateTime());
            if(photoItem.getPhotoPath() != null && photoItem.getPhotoPath().length() > 0) {
                Picasso.with(context).load(photoItem.getPhotoPath()).fit().centerInside().into(holder.imgView);
            }
        }
        //Return
        return convertView;
    }

    //Set Widgets
    class PhotosListHolder{
        @BindView(R.id.txtUpdateTimeItemPhotosList)
        TextView txtUpdateTime;
        @BindView(R.id.imgViewItemPhotosList)
        ImageView imgView;

        public PhotosListHolder(View parent) {
            ButterKnife.bind(this,parent);
        }
    }
}
