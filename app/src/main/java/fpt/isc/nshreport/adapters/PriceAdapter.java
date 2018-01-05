package fpt.isc.nshreport.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.objectParse.PriceList;
import fpt.isc.nshreport.utilities.AppUtilities;

public class PriceAdapter extends BaseAdapter {

	private Context mContext;
	private List<PriceList.data> ObjectList;
	private LayoutInflater layoutInflater;

	public PriceAdapter(Context context, List<PriceList.data> ObjectList){
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
		@BindView(R.id.tv_title)
		TextView tv_title;
		@BindView(R.id.tv_dateUpdate)
		TextView tv_dateUpdate;
		@BindView(R.id.tv_price)
		TextView tv_price;
		@BindView(R.id.img_status)
		ImageView img_status;
		
		private ViewHolder(View parent){
			ButterKnife.bind(this,parent);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.item_price, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		/*if (position % 2 == 0) {
			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
		} else {
			convertView.setBackgroundColor(Color.parseColor("#e0f2f2"));
		}*/
		
		PriceList.data Object = ObjectList.get(position);

		holder.tv_title.setText(Object.product_name);
		holder.tv_dateUpdate.setText(Object.date_update_price);
		holder.tv_price.setText(AppUtilities.longTypeTextFormat(Object.new_price));

		if(Object.status_price == 0)
		{
			holder.img_status.setVisibility(View.GONE);
		}else if(Object.status_price == 1)
		{
			holder.img_status.setVisibility(View.VISIBLE);
			holder.img_status.setImageResource(R.drawable.ic_arrow_up_price);
		}else
		{
			holder.img_status.setVisibility(View.VISIBLE);
			holder.img_status.setImageResource(R.drawable.ic_arrow_down_price);
		}
		
		return convertView;
	}

}
