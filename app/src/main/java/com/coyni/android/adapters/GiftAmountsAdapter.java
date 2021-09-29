package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.model.giftcard.Items;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.GiftCardDetailsActivity;

import java.util.List;

public class GiftAmountsAdapter extends RecyclerView.Adapter<GiftAmountsAdapter.MyViewHolder> {
    private List<Items> listAmounts;
    Context mContext;
    MyApplication objContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAmount;
        public RelativeLayout layoutItem;

        public MyViewHolder(View view) {
            super(view);
            tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            layoutItem = (RelativeLayout) view.findViewById(R.id.layoutItem);
        }
    }


    public GiftAmountsAdapter(List<Items> list, Context context) {
        this.mContext = context;
        this.listAmounts = list;
        this.objContext = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.giftamtlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            Items objData = listAmounts.get(position);
            holder.tvAmount.setText("$" + Utils.convertBigDecimalUSDC(String.valueOf(objData.getFaceValue())));
            holder.layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GiftCardDetailsActivity) mContext).getAmount(objData);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listAmounts.size();
    }
}