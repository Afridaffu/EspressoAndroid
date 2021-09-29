package com.coyni.android.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.model.recentuser.RecentUsersData;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.PayRequestActivity;

import java.util.List;

public class RecentUsersAdapter extends RecyclerView.Adapter<RecentUsersAdapter.MyViewHolder> {
    List<RecentUsersData> listUsers;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNameHead, tvName;
        public CardView cvNameHead;

        public MyViewHolder(View view) {
            super(view);
            tvNameHead = (TextView) view.findViewById(R.id.tvNameHead);
            tvName = (TextView) view.findViewById(R.id.tvName);
            cvNameHead = (CardView) view.findViewById(R.id.cvNameHead);
        }
    }


    public RecentUsersAdapter(List<RecentUsersData> list, Context context) {
        this.mContext = context;
        this.listUsers = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recentuserlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            RecentUsersData objData = listUsers.get(position);
            if (objData.getUserName() != null && !objData.getUserName().equals("")) {
                if (objData.getUserName().contains(" ")) {
                    if (!objData.getUserName().split(" ")[0].equals("")) {
                        holder.tvNameHead.setText(objData.getUserName().split(" ")[0].substring(0, 1).toUpperCase() + objData.getUserName().split(" ")[1].substring(0, 1).toUpperCase());
                    } else {
                        holder.tvNameHead.setText(objData.getUserName().split(" ")[0].toUpperCase() + objData.getUserName().split(" ")[1].substring(0, 1).toUpperCase());
                    }
                } else {
                    holder.tvNameHead.setText(objData.getUserName().substring(0, 1).toUpperCase());
                }
                if (objData.getUserName().length() > 7) {
                    holder.tvName.setText(Utils.capitalize(objData.getUserName().substring(0, 7) + "..."));
                } else {
                    holder.tvName.setText(Utils.capitalize(objData.getUserName()));
                }
            }
            if (position % 4 == 0) {
                holder.cvNameHead.setCardBackgroundColor(Color.parseColor("#26D45858"));
                holder.tvNameHead.setTextColor(Color.parseColor("#D45858"));
            } else if (position % 4 == 1) {
                holder.cvNameHead.setCardBackgroundColor(Color.parseColor("#2635BAB6"));
                holder.tvNameHead.setTextColor(Color.parseColor("#35BAB6"));
            } else if (position % 4 == 2) {
                holder.cvNameHead.setCardBackgroundColor(Color.parseColor("#262196F3"));
                holder.tvNameHead.setTextColor(Color.parseColor("#2196F3"));
            } else {
                holder.cvNameHead.setCardBackgroundColor(Color.parseColor("#26FFA701"));
                holder.tvNameHead.setTextColor(Color.parseColor("#FFA701"));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(mContext, PayRequestActivity.class);
                        i.putExtra("walletId", objData.getWalletAddress());
                        mContext.startActivity(i);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

}

