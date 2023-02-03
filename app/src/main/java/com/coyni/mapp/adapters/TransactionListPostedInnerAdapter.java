package com.coyni.mapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.transaction.TransactionList;
import com.coyni.mapp.model.transaction.TransactionListPosted;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.TransactionDetailsActivity;

import java.util.List;

public class TransactionListPostedInnerAdapter extends RecyclerView.Adapter<TransactionListPostedInnerAdapter.MyViewHolder> {
    Context mContext;
    MyApplication objMyApplication;
    List<TransactionListPosted> transactionListItemsposted;
    TransactionList transactionList;
    Long mLastClickTime = 0L;

    public TransactionListPostedInnerAdapter(List<TransactionListPosted> list, Context context) {
        this.transactionListItemsposted = list;
        this.mContext = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @NonNull
    @Override
    public TransactionListPostedInnerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.posted_inner_item, parent, false);

        return new TransactionListPostedInnerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListPostedInnerAdapter.MyViewHolder holder, int position) {
        TransactionListPosted objData = transactionListItemsposted.get(position);
        String strType = "", strSubtype = "";


        String[] data = objData.getTxnDescription().replace("****", "-").split("-");
        try {
            if (data.length > 1) {
                holder.txnDescripExtention.setVisibility(View.VISIBLE);
                holder.txnDescrip.setText(data[0]);
                holder.txnDescripExtention.setText("**" + data[1]);
                holder.txnDescrip.setVisibility(View.VISIBLE);
            } else {
                holder.txnDescrip.setText(objData.getTxnDescription());
                holder.txnDescripExtention.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (objData.getUserType().equalsIgnoreCase(Utils.BUSINESS) && objData.getTxnTypeDn().equalsIgnoreCase("Sale Order")) {
            holder.txnDescrip.setText(objData.getTxnTypeDn() + " - " + objData.getReceiveName());
        } else if (objData.getTxnTypeDn().equalsIgnoreCase("Sale Order")) {
//            holder.txnDescrip.setText(objData.getTxnTypeDn() + " - " + objData.getTxnSubTypeDn());
            holder.txnDescrip.setText(objData.getReceiveName());
        }


        holder.walletBal.setText("Balance " + Utils.convertTwoDecimal(objData.getWalletBalance()));

        if (position == transactionListItemsposted.size() - 1) {
            holder.blankView.setVisibility(View.VISIBLE);
        } else {
            holder.blankView.setVisibility(View.GONE);
        }

        holder.blankView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //type transaction
        strType = "withdraw";

        if (objData.getTxnTypeDn().toLowerCase().contains("withdraw")) {
        } else if (objData.getTxnTypeDn().toLowerCase().contains("pay") || objData.getTxnTypeDn().toLowerCase().contains("request")) {
            if (objData.getTxnSubTypeDn().toLowerCase().contains("send") || objData.getTxnSubTypeDn().toLowerCase().contains("sent")) {
                strType = "pay";
            } else {
                strType = "receive";
            }
        } else if (objData.getTxnTypeDn().toLowerCase().contains("buy")) {
            strType = "buy";
        } else if (objData.getTxnTypeDn().toLowerCase().contains("refund")) {
            strType = "refund";
        } else if (objData.getTxnTypeDn().toLowerCase().contains("sale order")) {
            strType = "sale order";
        } else {
            strType = objData.getTxnTypeDn().toLowerCase();
        }

        if (strType.contains("pay") || strType.equals("withdraw") || strType.equals("sale order") || strType.equals("monthly service fee")) {
            holder.amount.setText("-" + Utils.convertTwoDecimal(objData.getAmount()).replace("CYN", "").trim());
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            holder.amount.setText("+" + Utils.convertTwoDecimal(objData.getAmount()).replace("CYN", "").trim());
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.active_green));
        }

        strSubtype = objData.getTxnSubTypeDn().toLowerCase();

        if (strSubtype.equalsIgnoreCase("Sent")) {
            holder.amount.setText("-" + Utils.convertTwoDecimal(objData.getAmount()).split(" ")[0]);
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.black));
        } else if (strSubtype.equalsIgnoreCase("Received")) {
            holder.amount.setText("+" + Utils.convertTwoDecimal(objData.getAmount()).split(" ")[0]);
            holder.amount.setTextColor(mContext.getResources().getColor(R.color.active_green));
        }

        holder.txnStatus.setText(objData.getTxnStatusDn());
        switch (objData.getTxnStatusDn().replace(" ", "").toLowerCase()) {
            case Utils.transInProgress:
                holder.txnStatus.setTextColor(mContext.getResources().getColor(R.color.under_review_blue));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                break;
            case Utils.transPending:
                holder.txnStatus.setTextColor(mContext.getResources().getColor(R.color.orange));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                break;
            case Utils.transCompleted:
                holder.txnStatus.setTextColor(mContext.getResources().getColor(R.color.active_green));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case Utils.transFailed:
            case Utils.transCancelled: {
                holder.txnStatus.setTextColor(mContext.getResources().getColor(R.color.error_red));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                break;
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

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

    }

    @Override
    public int getItemCount() {
        Log.e("size", "" + transactionListItemsposted.size());
        return transactionListItemsposted.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txnDescrip, txnDescripExtention, amount, txnStatus, walletBal;
        RelativeLayout itemRL;
        LinearLayout lineItem;
        View blankView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txnDescrip = itemView.findViewById(R.id.messageTV);
            txnDescripExtention = itemView.findViewById(R.id.messagTV);

            amount = itemView.findViewById(R.id.amountTV);
            txnStatus = itemView.findViewById(R.id.statusTV);
            walletBal = itemView.findViewById(R.id.balanceTV);
            itemRL = itemView.findViewById(R.id.layoutTopRadiusRL);
            lineItem = itemView.findViewById(R.id.viewV);
            blankView = itemView.findViewById(R.id.blankView);
        }

    }

//    private String convertTwoDecimal(String strAmount) {
//        String strValue = "", strAmt = "";
//        try {
//            if (strAmount.contains(" ")) {
//                strAmt = Utils.convertBigDecimalUSDC(strAmount.split(" ")[0]);
//                strValue = Utils.USNumberFormat(Utils.doubleParsing(strAmt)) + " " + strAmount.split(" ")[1];
//            } else {
//                strAmt = Utils.convertBigDecimalUSDC(strAmount);
//                strValue = Utils.USNumberFormat(Utils.doubleParsing(strAmt));
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return strValue;
//    }

}
