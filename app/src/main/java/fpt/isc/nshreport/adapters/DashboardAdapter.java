package fpt.isc.nshreport.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.Dashboard;

public class DashboardAdapter extends BaseAdapter {

	private Context mContext;
	private List<Dashboard> ObjectList;
	private LayoutInflater layoutInflater;

	public DashboardAdapter(Context context, List<Dashboard> ObjectList){
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
		@BindView(R.id.txt_litre)
		TextView txt_litre;
		@BindView(R.id.txt_money)
		TextView txt_money;
		@BindView(R.id.view_layout)
		View view_layout;

		private ViewHolder(View parent){
			ButterKnife.bind(this, parent);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.item_dashboard, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Dashboard Object = ObjectList.get(position);
		holder.txt_title.setText(Object.getTitle());
		holder.txt_litre.setText(Object.getLitre());
		holder.txt_money.setText(Object.getMoney());
		//set background for view
		holder.view_layout.setBackgroundColor(Color.parseColor(Object.getColor()));
		holder.txt_title.setTextColor(Color.parseColor(Object.getColor()));
		
		return convertView;
	}

}
