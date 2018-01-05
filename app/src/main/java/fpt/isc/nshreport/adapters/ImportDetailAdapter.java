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
import fpt.isc.nshreport.models.objectParse.ImportDetail;
import fpt.isc.nshreport.utilities.AppUtilities;

public class ImportDetailAdapter extends BaseAdapter {

	private Context mContext;
	private List<ImportDetail.data.inventory_workflow_detail> ObjectList;
	private LayoutInflater layoutInflater;

	public ImportDetailAdapter(Context context, List<ImportDetail.data.inventory_workflow_detail> ObjectList){
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
		@BindView(R.id.txt_price)
		TextView txt_price;
		@BindView(R.id.txt_unit)
		TextView txt_unit;
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
			convertView = layoutInflater.inflate(R.layout.item_import_detail, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		ImportDetail.data.inventory_workflow_detail Object = ObjectList.get(position);

		holder.txt_title.setText(Object.product_code);
		holder.txt_num.setText(AppUtilities.StringNumerTextFormat(String.valueOf(Object.quantity_recived)));
		holder.txt_price.setText("$ " + AppUtilities.longTypeTextFormat(Object.total));
		holder.txt_title_detail.setText(Object.tank_name + " " + Object.tank_code);
		holder.txt_unit.setText(Object.unit);


		return convertView;
	}

}
