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

public class ShiftAddOtherAdapter extends BaseAdapter {

	private Context mContext;
	private List<ShiftAddOtherDetail> ObjectList;
	private LayoutInflater layoutInflater;

	public ShiftAddOtherAdapter(Context context, List<ShiftAddOtherDetail> ObjectList){
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
		@BindView(R.id.txt_num)
		TextView txt_num;
		@BindView(R.id.img_detail)
		ImageView img_detail;
		@BindView(R.id.txt_unit)
		TextView txt_unit;

		
		private ViewHolder(View parent){
			ButterKnife.bind(this,parent);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.item_shift_add_other, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}

		ShiftAddOtherDetail Object = ObjectList.get(position);

		holder.txt_title.setText(Object.getTitle());
		holder.txt_num.setText(AppUtilities.StringNumerTextFormat(Object.getNum()));
		holder.txt_unit.setText(Object.getUnit());

		if (Object.getImage()!= null && Object.getImage().length() > 0) {
			Picasso.with(mContext).load(Object.getImage()).fit().centerInside().into(holder.img_detail);
		}

		return convertView;
	}

}
