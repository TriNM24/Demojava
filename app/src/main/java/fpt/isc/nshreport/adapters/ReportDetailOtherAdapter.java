package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.ShiftCloseOtherDetail;
import fpt.isc.nshreport.utilities.AppUtilities;

public class ReportDetailOtherAdapter extends RecyclerView.Adapter<ReportDetailOtherAdapter.ViewHolder> {

	private static Activity mContext;
	private List<ShiftCloseOtherDetail> ObjectList;

	public ReportDetailOtherAdapter(Activity mContext, List<ShiftCloseOtherDetail> objectList) {
		this.mContext = mContext;
		ObjectList = objectList;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		//mContext = parent.getContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cardView = inflater.inflate(R.layout.item_shift_close_detail, null, false);
		ViewHolder viewHolder = new ViewHolder(cardView);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		ShiftCloseOtherDetail Object = ObjectList.get(position);

		holder.txt_title_detail.setVisibility(View.GONE);
		holder.txt_title.setText(Object.getTitle());
		holder.txt_old_litre.setText(AppUtilities.StringNumerTextFormat(Object.getOldLitre()));
		holder.txt_new_litre.setText(AppUtilities.StringNumerTextFormat(Object.getNewLitre()));
		holder.txt_num.setText(AppUtilities.StringNumerTextFormat(Object.getNum()));

		if (Object.getImage()!= null && Object.getImage().length() > 0) {
			Picasso.with(mContext).load(Object.getImage()).fit().centerInside().into(holder.img_detail);
		}
	}

	@Override
	public int getItemCount() {
		return ObjectList.size();
	}


	static class ViewHolder extends RecyclerView.ViewHolder {
		//Declares variables
		@BindView(R.id.txt_title)
		TextView txt_title;
		@BindView(R.id.txt_title_detail)
		TextView txt_title_detail;
		@BindView(R.id.txt_old_litre)
		TextView txt_old_litre;
		@BindView(R.id.txt_num)
		TextView txt_num;
		@BindView(R.id.txt_new_litre)
		TextView txt_new_litre;
		@BindView(R.id.img_detail)
		ImageView img_detail;

		private ViewHolder(View parent){
			super(parent);
			ButterKnife.bind(this,parent);
		}
	}
}
