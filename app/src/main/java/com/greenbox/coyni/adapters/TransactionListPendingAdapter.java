package com.greenbox.coyni.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.EditCardActivity;
import com.greenbox.coyni.view.TransactionDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TransactionListPendingAdapter extends RecyclerView.Adapter<TransactionListPendingAdapter.MyViewHolder> {
    List<TransactionListPending> transactionListItemspending;
    Context mecontext;
    MyApplication objMyApplication;
    TransactionList transactionList;
    Long mLastClickTime = 0L;

    public TransactionListPendingAdapter(List<TransactionListPending> list, Context context) {
        this.transactionListItemspending = list;
        this.mecontext = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_posted_listitem, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull TransactionListPendingAdapter.MyViewHolder holder, int position) {
        TransactionListPending objData = transactionListItemspending.get(position);

        String strType = "";

        holder.txnDescrip.setText(objData.getTxnDescription());
        holder.txnStatus.setText(objData.getTxnStatusDn());
        holder.txnStatus.setBackgroundResource(R.drawable.txn_pending_bg);
        holder.walletBal.setText(convertTwoDecimal(objData.getWalletBalance()));
        holder.date.setVisibility(View.GONE);
        if (objData.getTxnTypeDn().toLowerCase().contains("withdraw")) {
            strType = "withdraw";
        } else if (objData.getTxnTypeDn().toLowerCase().contains("pay") || objData.getTxnTypeDn().toLowerCase().contains("request")) {
            if (objData.getTxnSubTypeDn().toLowerCase().contains("send") || objData.getTxnSubTypeDn().toLowerCase().contains("sent")) {
                strType = "pay";
            } else {
                strType = "receive";
            }
        } else if (objData.getTxnTypeDn().toLowerCase().contains("buy")) {
            strType = "buy";
        } else {
            strType = objData.getTxnTypeDn().toLowerCase();
        }

        if (strType.contains("pay") || strType.equals("withdraw")) {
            holder.amount.setText("-" + convertTwoDecimal(objData.getAmount()));
        } else {
            holder.amount.setText("+" + convertTwoDecimal(objData.getAmount()));
            holder.amount.setTextColor(Color.parseColor("#008a05"));
        }

        if (position == transactionListItemspending.size() - 1) {
            holder.blankView.setVisibility(View.VISIBLE);
        } else {
            holder.blankView.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    Intent i = new Intent(mecontext, TransactionDetailsActivity.class);
                    i.putExtra("gbxTxnIdType", objData.getGbxTransactionId());
                    i.putExtra("txnType", objData.getTxnTypeDn());
                    i.putExtra("txnSubType", objData.getTxnSubTypeDn());
                    mecontext.startActivity(i);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        holder.date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return transactionListItemspending.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, txnDescrip, amount, txnStatus, walletBal;
        LinearLayout viewLine;
        View blankView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateTV);
            txnDescrip = itemView.findViewById(R.id.messageTV);
            amount = itemView.findViewById(R.id.amountTV);
            txnStatus = itemView.findViewById(R.id.statusTV);
            walletBal = itemView.findViewById(R.id.balanceTV);
            viewLine = itemView.findViewById(R.id.viewV);
            blankView = itemView.findViewById(R.id.blankView);
        }
    }

    private String convertTwoDecimal(String strAmount) {
        String strValue = "", strAmt = "";
        try {
            if (strAmount.contains(" ")) {
                strAmt = Utils.convertBigDecimalUSDC(strAmount.split(" ")[0]);
                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt)) + " " + strAmount.split(" ")[1];
            } else {
                strAmt = Utils.convertBigDecimalUSDC(strAmount);
                strValue = Utils.USNumberFormat(Double.parseDouble(strAmt));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strValue;
    }

    public void addLoadingView() {
        //add loading item
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                transactionListItemspending.add(null);
                notifyItemInserted(transactionListItemspending.size() - 1);
            }
        });
    }

    public void removeLoadingView() {
        //Remove loading item
        transactionListItemspending.remove(transactionListItemspending.size() - 1);
        notifyItemRemoved(transactionListItemspending.size());
    }

    public void addData(List<TransactionListPending> listItems) {
        this.transactionListItemspending.addAll(listItems);
        notifyDataSetChanged();
    }

    public void updateList(List<TransactionListPending> list) {
        transactionListItemspending = list;
        notifyDataSetChanged();
    }

}
