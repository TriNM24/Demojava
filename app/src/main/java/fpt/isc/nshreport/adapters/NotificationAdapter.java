package fpt.isc.nshreport.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.objectParse.NotificationList;
import fpt.isc.nshreport.utilities.AppUtilities;

public class NotificationAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<NotificationList.data> notificationObjectList;
	private LayoutInflater layoutInflater;
	
	public NotificationAdapter(Context context, List<NotificationList.data> notificationObjectList){
		this.mContext = context;
		this.notificationObjectList = notificationObjectList;
		this.layoutInflater = LayoutInflater.from(mContext);
	}
	

	@Override
	public int getCount() {
		return notificationObjectList.size();
	}

	@Override
	public Object getItem(int position) {
		return notificationObjectList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	final class ViewHolder{
		@BindView(R.id.txt_title)
		TextView tv_notification_title;
		@BindView(R.id.txt_date)
		TextView tv_notification_date_created;
		@BindView(R.id.txt_content)
		TextView tv_notification_content;
		@BindView(R.id.roundedImageView)
		RoundedImageView roundedImageView;
		
		private ViewHolder(View parent){

			ButterKnife.bind(this,parent);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if (convertView == null){
			convertView = layoutInflater.inflate(R.layout.item_notify, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if (position % 2 == 0) {
			convertView.setBackgroundColor(Color.parseColor("#ffffff"));
		} else {
			convertView.setBackgroundColor(Color.parseColor("#e0f2f2"));
		}

		NotificationList.data notificationObject = notificationObjectList.get(position);
		
		holder.tv_notification_title.setText(notificationObject.object_display_name);
		holder.tv_notification_content.setText(notificationObject.object_content);

		String day = AppUtilities.ConvertStringDateToStringDate(notificationObject.date_created,"yyyy-MM-dd HH:mm:ss","dd/MM/yyyy HH:mm:ss");
		holder.tv_notification_date_created.setText(day);

		if(notificationObject.is_read == 0)
		{
			holder.roundedImageView.setVisibility(View.VISIBLE);
		}else
		{
			holder.roundedImageView.setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
