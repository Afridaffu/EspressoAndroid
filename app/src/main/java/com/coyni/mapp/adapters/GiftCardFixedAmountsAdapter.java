package com.coyni.mapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.giftcard.Items;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.view.GiftCardDetails;

import java.util.List;

public class GiftCardFixedAmountsAdapter extends RecyclerView.Adapter<GiftCardFixedAmountsAdapter.MyViewHolder> {
    List<Items> listAmounts;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView amount;
        ImageView tickIcon;
        View viewLine;

        public MyViewHolder(View view) {
            super(view);
            amount = (TextView) view.findViewById(R.id.tvPreference);
            tickIcon = (ImageView) view.findViewById(R.id.tickIcon);
            viewLine = (View) view.findViewById(R.id.viewLine);
        }
    }


    public GiftCardFixedAmountsAdapter(List<Items> list, Context context) {
        this.mContext = context;
        this.listAmounts = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timezones_listitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            holder.amount.setText("$ " + listAmounts.get(position).getFaceValue().toString());
            if (listAmounts.get(position).isSelected()) {
                holder.tickIcon.setVisibility(View.VISIBLE);
            } else {
                holder.tickIcon.setVisibility(View.GONE);
            }
            holder.amount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GiftCardDetails.giftCardDetails.selectedFixedAmount = listAmounts.get(position).getFaceValue().toString();

                    for (int i = 0; i < listAmounts.size(); i++) {
                        if (position == i) {
                            listAmounts.get(i).setSelected(true);
                        } else {
                            listAmounts.get(i).setSelected(false);
                        }
                    }

                    notifyDataSetChanged();
                }
            });

            if (position == listAmounts.size() - 1) {
                holder.viewLine.setVisibility(View.GONE);
            } else {
                holder.viewLine.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listAmounts.size();
    }

    public void updateList(List<Items> list) {
        listAmounts = list;
        notifyDataSetChanged();
    }

}
