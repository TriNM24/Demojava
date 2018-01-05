package fpt.isc.nshreport.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.ShiftCloseOtherDetail;
import fpt.isc.nshreport.utilities.AppUtilities;

public class ShiftCloseDetailAdapter extends BaseAdapter {

	private Context mContext;
	private List<ShiftCloseOtherDetail> ObjectList;
	private LayoutInflater layoutInflater;

	public ShiftCloseDetailAdapter(Context context, List<ShiftCloseOtherDetail> ObjectList){
		this.mContext = context;
		this.ObjectList = ObjectList;
		this.layoutInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return ObjectList.size();
	}

	@Override
	public Object getItem(int position) {
		return ObjectList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	final class ViewHolder{
		@BindView(R.id.txt_title)
		TextView txt_title;
		@BindView(R.id.txt_title_detail)
		TextView txt_title_detail;
		@BindView(R.id.txt_old_litre)
		TextView txt_old_litre;
		@BindView(R.id.txt_new_litre)
		TextView txt_new_litre;
		@BindView(R.id.txt_num)
		TextView txt_num;
		@BindView(R.id.img_detail)
		ImageView img_detail;
		
		private ViewHolder(View parent){
			ButterKnife.bind(this,parent);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.item_shift_close_detail, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		ShiftCloseOtherDetail Object = ObjectList.get(position);

		holder.txt_title.setText(Object.getTitle());
		holder.txt_title_detail.setText(Object.getContent());
		holder.txt_old_litre.setText(AppUtilities.StringNumerTextFormat(Object.getOldLitre()));
		holder.txt_new_litre.setText(AppUtilities.StringNumerTextFormat(Object.getNewLitre()));
		holder.txt_num.setText(AppUtilities.StringNumerTextFormat(Object.getNum()));

		if (Object.getImage()!= null && Object.getImage().length() > 0) {
			Picasso.with(mContext).load(Object.getImage()).fit().centerInside().into(holder.img_detail);
		}

		return convertView;
	}

}
