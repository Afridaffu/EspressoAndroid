package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.recentusers.RecentUsersData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.PayRequestActivity;

import java.util.List;

public class RecentUsersAdapter extends RecyclerView.Adapter<RecentUsersAdapter.MyViewHolder> {
    List<RecentUsersData> listUsers;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNameHead, tvUserName, tvWalletAddress;
        public ImageView imgUser;

        public MyViewHolder(View view) {
            super(view);
            tvNameHead = view.findViewById(R.id.tvNameHead);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvWalletAddress = view.findViewById(R.id.tvWalletAddress);
            imgUser = view.findViewById(R.id.imgUser);
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
                holder.tvUserName.setText(Utils.capitalize(objData.getUserName()));
                holder.tvWalletAddress.setText("Account Address " + objData.getWalletAddress());
                if (objData.getImage() != null && !objData.getImage().trim().equals("")) {
                    holder.imgUser.setVisibility(View.VISIBLE);
                    holder.tvNameHead.setVisibility(View.GONE);
                    Glide.with(mContext)
                            .load(objData.getImage())
                            .placeholder(R.drawable.ic_profilelogo)
                            .into(holder.imgUser);
                } else {
                    holder.imgUser.setVisibility(View.GONE);
                    holder.tvNameHead.setVisibility(View.VISIBLE);
                }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(mContext, PayRequestActivity.class);
                        i.putExtra("walletId", objData.getWalletAddress());
                        i.putExtra("name", objData.getUserName());
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

    public void updateList(List<RecentUsersData> list) {
        listUsers = list;
        notifyDataSetChanged();
    }

}


