package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.retrieveemail.RetUserResData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.LoginActivity;
import com.greenbox.coyni.view.TransactionDetailsActivity;

import java.util.List;

public class LatestTxnAdapter extends RecyclerView.Adapter<LatestTxnAdapter.MyViewHolder> {
    LatestTxnResponse latestTxns;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txnDescrip,txnDescripExtention, amountTV, dateTV, statusTV, balanceTV;
        LinearLayout statusLL;

        public MyViewHolder(View view) {
            super(view);
            txnDescrip = (TextView) view.findViewById(R.id.latestmessageTV);
            txnDescripExtention=view.findViewById(R.id.latestmessagTV);
            amountTV = (TextView) view.findViewById(R.id.amountTV);
            dateTV = (TextView) view.findViewById(R.id.dateTV);
            statusTV = (TextView) view.findViewById(R.id.statusTV);
            balanceTV = (TextView) view.findViewById(R.id.balanceTV);
            statusLL = (LinearLayout) view.findViewById(R.id.statusLL);
        }
    }


    public LatestTxnAdapter(LatestTxnResponse list, Context context) {
        this.mContext = context;
        this.latestTxns = list;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.latest_txn_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            LatestTxnResponse.Daata objData = latestTxns.getData().get(position);

            String[] data = objData.getTxnDescription().replace("****","-").split("-");
            try {
                if (data.length > 1) {
                    holder.txnDescripExtention.setVisibility(View.VISIBLE);
                    holder.txnDescrip.setText(data[0]);
                    holder.txnDescripExtention.setText("**"+data[1]);
                    holder.txnDescrip.setVisibility(View.VISIBLE);
                } else {
                    holder.txnDescrip.setText(objData.getTxnDescription());
                    holder.txnDescripExtention.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


//            holder.txnDescTV.setText(latestTxns.getData().get(position).getTxnDescription());
//
            holder.amountTV.setText(latestTxns.getData().get(position).getAmount());
//            holder.dateTV.setText(latestTxns.getData().get(position).getUpdatedAt());
            holder.balanceTV.setText("Balance " + Utils.convertTwoDecimal(latestTxns.getData().get(position).getWalletBalance()).split(" ")[0]);
            String strType = "";

            if (latestTxns.getData().get(position).getTxnTypeDn().toLowerCase().contains("withdraw")) {
                strType = "withdraw";
            } else if (latestTxns.getData().get(position).getTxnTypeDn().toLowerCase().contains("pay") || latestTxns.getData().get(position).getTxnTypeDn().toLowerCase().contains("request")) {
                if (latestTxns.getData().get(position).getTxnSubTypeDn().toLowerCase().contains("send") || latestTxns.getData().get(position).getTxnSubTypeDn().toLowerCase().contains("sent")) {
                    strType = "pay";
                } else {
                    strType = "receive";
                }
            } else if (latestTxns.getData().get(position).getTxnTypeDn().toLowerCase().contains("buy")) {
                strType = "buy";
            } else {
                strType = latestTxns.getData().get(position).getTxnTypeDn().toLowerCase();
            }

            if (strType.contains("pay") || strType.equals("withdraw")) {
                holder.amountTV.setText("-" + Utils.convertTwoDecimal(latestTxns.getData().get(position).getAmount()).split(" ")[0]);
            } else {
                holder.amountTV.setText("+" + Utils.convertTwoDecimal(latestTxns.getData().get(position).getAmount()).split(" ")[0]);
                holder.amountTV.setTextColor(mContext.getResources().getColor(R.color.active_green));
            }

            if (latestTxns.getData().get(position).getTxnStatusDn().equalsIgnoreCase("Failed")) {
                holder.statusTV.setText(latestTxns.getData().get(position).getTxnStatusDn());
                holder.statusTV.setTextColor(mContext.getColor(R.color.error_red));
                holder.statusLL.setBackground(mContext.getDrawable(R.drawable.txn_failed_bg));
            } else if (latestTxns.getData().get(position).getTxnStatusDn().equalsIgnoreCase("Completed")) {
                holder.statusLL.setVisibility(View.GONE);
                holder.dateTV.setVisibility(View.VISIBLE);
                holder.dateTV.setText(objMyApplication.convertZoneLatestTxn(latestTxns.getData().get(position).getUpdatedAt()));
            } else if (latestTxns.getData().get(position).getTxnStatusDn().equalsIgnoreCase("In Progress")) {
                holder.statusTV.setText(latestTxns.getData().get(position).getTxnStatusDn());
                holder.statusTV.setTextColor(mContext.getColor(R.color.under_review_blue));
                holder.statusLL.setBackground(mContext.getDrawable(R.drawable.txn_inprogress_bg));

                // comment
//                holder.statusLL.setVisibility(View.GONE);
//                holder.dateTV.setVisibility(View.VISIBLE);
//                holder.dateTV.setText(objMyApplication.convertZoneLatestTxn(latestTxns.getData().get(position).getUpdatedAt()));
            } else if (latestTxns.getData().get(position).getTxnStatusDn().equalsIgnoreCase("Pending")) {
                holder.statusTV.setText(latestTxns.getData().get(position).getTxnStatusDn());
                holder.statusTV.setTextColor(mContext.getColor(R.color.orange));
                holder.statusLL.setBackground(mContext.getDrawable(R.drawable.txn_pending_bg));
            }

            holder.statusTV.setText(latestTxns.getData().get(position).getTxnStatusDn());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent i = new Intent(mContext, TransactionDetailsActivity.class);
                        i.putExtra("gbxTxnIdType", objData.getGbxTransactionId());
                        i.putExtra("txnType", objData.getTxnTypeDn());
                        i.putExtra("txnSubType", objData.getTxnSubTypeDn());
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
        return latestTxns.getData().size();
    }


}

