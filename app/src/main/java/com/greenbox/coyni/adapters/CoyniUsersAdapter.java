package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.coyniusers.CoyniUsersData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.PayRequestTransactionActivity;

import java.util.List;

public class CoyniUsersAdapter extends RecyclerView.Adapter<CoyniUsersAdapter.MyViewHolder> {
    List<CoyniUsersData> listUsers;
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


    public CoyniUsersAdapter(List<CoyniUsersData> list, Context context) {
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
            CoyniUsersData objData = listUsers.get(position);
            if (objData.getFullName() != null && !objData.getFullName().equals("")) {
                if (objData.getFullName().contains(" ")) {
                    if (!objData.getFullName().split(" ")[0].equals("")) {
                        holder.tvNameHead.setText(objData.getFullName().split(" ")[0].substring(0, 1).toUpperCase() + objData.getFullName().split(" ")[1].substring(0, 1).toUpperCase());
                    } else {
                        holder.tvNameHead.setText(objData.getFullName().split(" ")[0].toUpperCase() + objData.getFullName().split(" ")[1].substring(0, 1).toUpperCase());
                    }
                } else {
                    holder.tvNameHead.setText(objData.getFullName().substring(0, 1).toUpperCase());
                }
                holder.tvUserName.setText(Utils.capitalize(objData.getFullName()));
                holder.tvWalletAddress.setText("Account Address " + objData.getWalletId());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(mContext, PayRequestTransactionActivity.class);
                        i.putExtra("walletId", objData.getWalletId());
                        i.putExtra("name", objData.getFullName());
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

    public void updateList(List<CoyniUsersData> list) {
        listUsers = list;
        notifyDataSetChanged();
    }

}

