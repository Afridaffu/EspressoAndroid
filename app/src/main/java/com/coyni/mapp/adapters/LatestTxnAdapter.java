package com.coyni.mapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.identity_verification.LatestTxnResponse;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.TransactionDetailsActivity;

public class LatestTxnAdapter extends RecyclerView.Adapter<LatestTxnAdapter.MyViewHolder> {
    LatestTxnResponse latestTxns;
    Context mContext;
    MyApplication objMyApplication;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txnDescrip, txnDescripExtention, amountTV, dateTV, statusTV, balanceTV;
        LinearLayout statusLL;
        public View gapView, descriptionView;

        public MyViewHolder(View view) {
            super(view);
            txnDescrip = (TextView) view.findViewById(R.id.latestmessageTV);
            txnDescripExtention = view.findViewById(R.id.latestmessagTV);
            amountTV = (TextView) view.findViewById(R.id.amountTV);
            dateTV = (TextView) view.findViewById(R.id.dateTV);
            statusTV = (TextView) view.findViewById(R.id.statusTV);
            balanceTV = (TextView) view.findViewById(R.id.balanceTV);
            statusLL = (LinearLayout) view.findViewById(R.id.statusLL);
            gapView = (View) view.findViewById(R.id.gapView);
            descriptionView = (View) view.findViewById(R.id.description1);
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

            String[] data = objData.getTxnDescription().replace("****", "-").split("-");
            try {
                if (data.length > 1) {
                    holder.txnDescripExtention.setVisibility(View.VISIBLE);
                    if (data[0].length() > 21) {
                        holder.txnDescrip.setText(data[0].substring(0, 20) + "...");
                    } else {
                        holder.txnDescrip.setText(data[0]);
                    }
                    holder.txnDescripExtention.setText("**" + data[1]);
                    holder.txnDescrip.setVisibility(View.VISIBLE);
                    if (holder.gapView.getVisibility() == View.VISIBLE)
                        holder.gapView.setVisibility(View.GONE);
                } else {
                    if (objData.getTxnDescription().length() > 23)
                        holder.txnDescrip.setText(objData.getTxnDescription().substring(0, 22) + "...");
                    else {
                        holder.txnDescrip.setText(objData.getTxnDescription());
                    }
                    holder.txnDescripExtention.setVisibility(View.GONE);
                    holder.descriptionView.setVisibility(View.VISIBLE);
                    if (holder.gapView.getVisibility() == View.GONE)
                        holder.gapView.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


//            holder.txnDescTV.setText(latestTxns.getData().get(position).getTxnDescription());
//
            holder.amountTV.setText(Utils.convertTwoDecimal(latestTxns.getData().get(position).getAmount()));
//            holder.dateTV.setText(latestTxns.getData().get(position).getUpdatedAt());
            holder.balanceTV.setText("Balance " + Utils.convertTwoDecimal(latestTxns.getData().get(position).getWalletBalance()).split(" ")[0]);
            String strType = "", strSubtype = "";
            strSubtype = latestTxns.getData().get(position).getTxnSubTypeDn().toLowerCase();

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
            } else if (latestTxns.getData().get(position).getTxnTypeDn().toLowerCase().contains("paid")) {
                strType = "paid";
            } else {
                strType = latestTxns.getData().get(position).getTxnTypeDn().toLowerCase();
            }

            if (strType.contains("pay") || strType.equals("withdraw") || strType.equals("paid")) {
                holder.amountTV.setText("-" + Utils.convertTwoDecimal(latestTxns.getData().get(position).getAmount()).split(" ")[0]);
            } else {
                holder.amountTV.setText("+" + Utils.convertTwoDecimal(latestTxns.getData().get(position).getAmount()).split(" ")[0]);
                holder.amountTV.setTextColor(mContext.getResources().getColor(R.color.active_green));
            }

            if (strSubtype.equalsIgnoreCase("Sent")) {
                holder.amountTV.setText("-" + Utils.convertTwoDecimal(latestTxns.getData().get(position).getAmount()).split(" ")[0]);
                holder.amountTV.setTextColor(mContext.getResources().getColor(R.color.black));
            } else if (strSubtype.equalsIgnoreCase("Received")) {
                holder.amountTV.setText("+" + Utils.convertTwoDecimal(latestTxns.getData().get(position).getAmount()).split(" ")[0]);
                holder.amountTV.setTextColor(mContext.getResources().getColor(R.color.active_green));
            }

            if (latestTxns.getData().get(position).getTxnStatusDn().equalsIgnoreCase("Failed") || latestTxns.getData().get(position).getTxnStatusDn().toLowerCase().equals("cancelled")) {
                holder.statusTV.setText(latestTxns.getData().get(position).getTxnStatusDn());
                holder.statusTV.setTextColor(mContext.getColor(R.color.error_red));
                holder.statusLL.setBackground(mContext.getDrawable(R.drawable.txn_failed_bg));
            } else if (latestTxns.getData().get(position).getTxnStatusDn().equalsIgnoreCase("Completed")) {
                holder.statusLL.setVisibility(View.VISIBLE);
                holder.dateTV.setVisibility(View.GONE);
//                holder.dateTV.setText(objMyApplication.convertZoneLatestTxn(latestTxns.getData().get(position).getUpdatedAt()));
                holder.statusTV.setTextColor(mContext.getColor(R.color.completed_status));
                holder.statusLL.setBackground(mContext.getDrawable(R.drawable.txn_active_bg));
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
                        i.putExtra("txnId", objData.getTransactionId());
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
        if (latestTxns.getData().size() > 5)
            return 5;
        else
            return latestTxns.getData().size();
    }


}
