package fpt.isc.nshreport.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.refactor.library.SmoothCheckBox;
import fpt.isc.nshreport.R;
import fpt.isc.nshreport.models.DebtReport;

/**
 * Created by Chick on 12/18/2017.
 */

public class DebtReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public int STYLE_HEADER = 0;
    public int STYLE_DETAIL = 1;

    private Context context;
    private List<DebtReport> data;

    public DebtReportAdapter(Context context, List<DebtReport> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == STYLE_HEADER)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_debt_header, parent, false);
            return new ItemHeaderHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_debt_detail, parent, false);
            return new ItemDetailHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        boolean isHeader = data.get(position).isHeader();
        if(isHeader) return STYLE_HEADER; else return STYLE_DETAIL;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemHeaderHolder)
        {
            ((ItemHeaderHolder)holder).txtDate.setText(data.get(position).getDate());
            ((ItemHeaderHolder)holder).txtTotalMoney.setText(data.get(position).getTotalMoney());
        }
        else
        {
            ((ItemDetailHolder)holder).smoothCheckBox.setEnabled(false);
            ((ItemDetailHolder)holder).smoothCheckBox.setChecked(data.get(position).isApproved());
            ((ItemDetailHolder)holder).txtTime.setText(data.get(position).getDateHour());
            ((ItemDetailHolder)holder).txtMoney.setText(data.get(position).getMoneyPerDebt());
            ((ItemDetailHolder)holder).txtNote.setText(data.get(position).getNote());

            //make image blinking
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.tween);
            ((ItemDetailHolder)holder).img_detail.startAnimation(myFadeInAnimation);

            //check to show or hide top line
            if((position-1)>= 0 && data.get(position-1).isHeader())
            {
                ((ItemDetailHolder)holder).lineTop.setVisibility(View.GONE);
            }else
            {
                ((ItemDetailHolder)holder).lineTop.setVisibility(View.VISIBLE);
            }
            //check to show or hide bottom line
            if(position < (data.size()-1))
            {
                if(data.get(position+1).isHeader())
                {
                    ((ItemDetailHolder)holder).lineBot.setVisibility(View.GONE);
                    ((ItemDetailHolder)holder).lineSeperate.setVisibility(View.VISIBLE);
                }else
                {
                    ((ItemDetailHolder)holder).lineBot.setVisibility(View.VISIBLE);
                    ((ItemDetailHolder)holder).lineSeperate.setVisibility(View.GONE);
                }
            }else
            {
                ((ItemDetailHolder)holder).lineBot.setVisibility(View.GONE);
                ((ItemDetailHolder)holder).lineSeperate.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemHeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txtDate)
        TextView txtDate;
        @BindView(R.id.txtTotalMoney)
        TextView txtTotalMoney;


        public ItemHeaderHolder(View parent) {
            super(parent);
            ButterKnife.bind(this, parent);
        }
    }

    class ItemDetailHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.smoothCheckBox)
        SmoothCheckBox smoothCheckBox;
        @BindView(R.id.txtTime)
        TextView txtTime;
        @BindView(R.id.txtMoney)
        TextView txtMoney;
        @BindView(R.id.txtNote)
        TextView txtNote;
        @BindView(R.id.lineTop)
        View lineTop;
        @BindView(R.id.lineBot)
        View lineBot;
        @BindView(R.id.lineSeperate)
        View lineSeperate;
        @BindView(R.id.img_detail)
        ImageView img_detail;



        public ItemDetailHolder(View parent) {
            super(parent);
            ButterKnife.bind(this, parent);
        }
    }
}
