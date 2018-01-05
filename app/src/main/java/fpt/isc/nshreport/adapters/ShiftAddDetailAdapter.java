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
import fpt.isc.nshreport.models.ShiftAddOtherDetail;
import fpt.isc.nshreport.utilities.AppUtilities;

public class ShiftAddDetailAdapter extends BaseAdapter {

	private Context mContext;
	private List<ShiftAddOtherDetail> ObjectList;
	private LayoutInflater layoutInflater;

	public ShiftAddDetailAdapter(Context context, List<ShiftAddOtherDetail> ObjectList){
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
			convertView = layoutInflater.inflate(R.layout.item_shift_add_detail, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		/*if (position != 0) {
			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
		} else {
			convertView.setBackgroundColor(Color.parseColor("#e0f2f2"));
		}*/
		//List<ShiftAddOtherDetail> ObjectList
		ShiftAddOtherDetail Object = ObjectList.get(position);

		holder.txt_title.setText(Object.getTitle());
		holder.txt_title_detail.setText(Object.getContent());
		holder.txt_num.setText(AppUtilities.StringNumerTextFormat(Object.getNum()));

		if (Object.getImage()!= null && Object.getImage().length() > 0) {
			Picasso.with(mContext).load(Object.getImage()).fit().centerInside().into(holder.img_detail);
		}

		return convertView;
	}

}
