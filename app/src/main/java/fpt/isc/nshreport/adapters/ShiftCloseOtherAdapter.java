package fpt.isc.nshreport.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.activities.BaseActivity;
import fpt.isc.nshreport.models.ShiftCloseOtherDetail;
import fpt.isc.nshreport.utilities.AppUtilities;
import fpt.isc.nshreport.utilities.NumberTextWatcherForThousand;

public class ShiftCloseOtherAdapter extends RecyclerView.Adapter<ShiftCloseOtherAdapter.ViewHolder> {

	private static Activity mContext;
	private List<ShiftCloseOtherDetail> ObjectList;

	public ShiftCloseOtherAdapter(Activity mContext, List<ShiftCloseOtherDetail> objectList) {
		this.mContext = mContext;
		ObjectList = objectList;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		//mContext = parent.getContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cardView = inflater.inflate(R.layout.item_shift_close_other, null, false);
		ViewHolder viewHolder = new ViewHolder(cardView);
		return viewHolder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		ShiftCloseOtherDetail Object = ObjectList.get(position);



		holder.txt_title.setText(Object.getTitle());

		//số tồn kho
		holder.txt_inventory_litre.setText(AppUtilities.StringNumerTextFormat(Object.getOldLitre()));

		holder.txt_sold_litre.setText("0");
		holder.txt_sold_litre.addTextChangedListener(new NumberTextWatcherForThousand(holder.txt_sold_litre));

		//số tồn kho còn lại
		holder.txt_num.setText(AppUtilities.StringNumerTextFormat(Object.getOldLitre()));

		holder.txt_unit.setText(Object.getUnit());

		if (Object.getImage()!= null && Object.getImage().length() > 0) {
			Picasso.with(mContext).load(Object.getImage()).fit().centerInside().into(holder.img_detail);
		}

		holder.txt_sold_litre.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void afterTextChanged(Editable s) {
				String strAfter = NumberTextWatcherForThousand.trimCommaOfString(s.toString());
				if(strAfter.trim().length() > 0) {
					try {
						long value = Long.valueOf(strAfter.trim());
						long inventory_num = Long.valueOf(ObjectList.get(position).getOldLitre());
						long sold = inventory_num - value;
						if(sold > 0) {
							holder.txt_num.setText(AppUtilities.StringNumerTextFormat(String.valueOf(sold)));
							ObjectList.get(position).setNewLitre(String.valueOf(value));
						}else
						{
							holder.txt_sold_litre.setText("0");
						}

					}catch (NumberFormatException e)
					{
						((BaseActivity) mContext).showMessage("Không phải số", SweetAlertDialog.WARNING_TYPE);
					}
				}

			}
		});
	}

	@Override
	public int getItemCount() {
		return ObjectList.size();
	}


	static class ViewHolder extends RecyclerView.ViewHolder {
		//Declares variables
		@BindView(R.id.txt_title)
		TextView txt_title;
		@BindView(R.id.txt_inventory_litre)
		TextView txt_inventory_litre;
		@BindView(R.id.txt_num)
		TextView txt_num;
		@BindView(R.id.txt_sold_litre)
		EditText txt_sold_litre;
		@BindView(R.id.img_detail)
		ImageView img_detail;
		@BindView(R.id.txt_unit)
		TextView txt_unit;

		private ViewHolder(View parent){
			super(parent);
			setupUI(parent.findViewById(R.id.layout_root));
			ButterKnife.bind(this,parent);
		}

	}

	public static void hideSoftKeyboard(Activity activity) {
		((BaseActivity)activity).hideSoftKeyboard();
	}

	public static void setupUI(View view) {

		//Set up touch listener for non-text box views to hide keyboard.
		if(!(view instanceof EditText)) {

			view.setOnTouchListener(new View.OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					hideSoftKeyboard(mContext);
					return false;
				}

			});
		}

		//If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {

			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

				View innerView = ((ViewGroup) view).getChildAt(i);

				setupUI(innerView);
			}
		}
	}

}
