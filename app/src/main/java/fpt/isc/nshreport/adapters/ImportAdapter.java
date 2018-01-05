package fpt.isc.nshreport.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.objectParse.ImportList;
import fpt.isc.nshreport.models.objectParse.OptionsList;
import fpt.isc.nshreport.utilities.AppUtilities;

public class ImportAdapter extends BaseAdapter {

	private Context mContext;
	private List<ImportList.data> ObjectList;
	private OptionsList options;
	private LayoutInflater layoutInflater;

	public ImportAdapter(Context context, List<ImportList.data> ObjectList,OptionsList options){
		this.mContext = context;
		this.ObjectList = ObjectList;
		this.options = options;
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
		@BindView(R.id.txtBill)
		TextView txtBill;
		@BindView(R.id.txtDate)
		TextView txtDate;
		@BindView(R.id.txtLitre)
		TextView txtLitre;
		@BindView(R.id.txtStatus)
		TextView txtStatus;
		@BindView(R.id.txtPrice)
		TextView txtPrice;
		@BindView(R.id.txtCreator)
		TextView txtCreator;
		
		private ViewHolder(View parent){
			ButterKnife.bind(this,parent);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.item_import, parent, false);
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

		ImportList.data Object = ObjectList.get(position);

		String day = AppUtilities.ConvertStringDateToStringDate(Object.date_created,"yyyy-MM-dd HH:mm:ss","dd/MM/yyyy");
		//get status name
		String statusName = "";
		for(int i=0;i<options.data.size();i++)
		{
			if(options.data.get(i).id.equals(Object.status))
			{
				statusName = options.data.get(i).name;
				break;
			}
		}

		holder.txtBill.setText(Object.voucher_code);
		holder.txtDate.setText(day);
		holder.txtLitre.setText(String.valueOf(Object.total));
		holder.txtStatus.setText(statusName);
		holder.txtPrice.setText(AppUtilities.longTypeTextFormat(Object.total_money));
		holder.txtCreator.setText(Object.user_created_name);
		
		return convertView;
	}

}
