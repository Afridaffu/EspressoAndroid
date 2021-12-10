package com.greenbox.coyni.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TransactionListPendingAdapter extends RecyclerView.Adapter<TransactionListPendingAdapter.MyViewHolder> {
    List<TransactionListPending> transactionListItemspending;
    Context mecontext;
    MyApplication objMyApplication;
    TransactionList transactionList;

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

        String strType = "", strPrev = "", strCurr = "", strCurDate = "";
//            holder.tvDate.setText(Utils.convertDate(objData.getCreatedAt()));
//        holder.date.setText(objMyApplication.convertZoneDate(objData.getUpdatedAt()));
//        if (position != 0) {
////                strPrev = Utils.convertDate(listTransactionsItem.get(position - 1).getCreatedAt());
////                strCurr = Utils.convertDate(objData.getCreatedAt());
//            strPrev = objMyApplication.convertZoneDate(transactionListItemspending.get(position - 1).getUpdatedAt());
//            strCurr = objMyApplication.convertZoneDate(objData.getUpdatedAt());
//            if (strPrev.equals(strCurr)) {
//                holder.date.setVisibility(View.GONE);
//            } else {
//                holder.date.setVisibility(View.VISIBLE);
//            }
//        }
//        else if (position == 0) {
//            holder.date.setVisibility(View.VISIBLE);
//            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            strCurDate = spf.format(Calendar.getInstance().getTime());
//            if (Utils.convertDate(objData.getUpdatedAt()).equals(Utils.convertDate(strCurDate))) {
//                holder.date.setText("Today");
//            }
//        }
        holder.txnStatus.setText(objData.getTxnStatusDn());
        holder.txnStatus.setBackgroundResource(R.drawable.txn_pending_bg);
        holder.txnDescrip.setText(objData.getTxnDescription());
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
            holder.amount.setTextColor(R.color.active_green);
        }
//        if (position == transactionListItemspending.size() - 1 && transactionListItemspending.size() < trans) {
//            Log.e("size", transactionListItemspending.size() + "");
//            tokenFragment.currentPage = tokenFragment.currentPage + 1;
//
//            if (tokenFragment.currentPage <= (tokenFragment.total - 1)) {
//                tokenFragment.pbLoader.setVisibility(View.VISIBLE);
//
//                tokenFragment.objMap = new HashMap<>();
//                tokenFragment.objMap.put("walletCategory", Utils.walletCategory);
//                tokenFragment.objMap.put("pageSize", String.valueOf(Utils.pageSize));
//                tokenFragment.objMap.put("pageNo", String.valueOf(tokenFragment.currentPage));
//                tokenFragment.dashboardViewModel.meTransactions(tokenFragment.objMap);
//            } else {
//                tokenFragment.pbLoader.setVisibility(View.GONE);
//            }
//        }
        if (position==transactionListItemspending.size()-1){
            holder.viewLine.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return transactionListItemspending.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, txnDescrip, amount, txnStatus, walletBal;
        LinearLayout viewLine;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateTV);
            txnDescrip = itemView.findViewById(R.id.messageTV);
            amount = itemView.findViewById(R.id.amountTV);
            txnStatus = itemView.findViewById(R.id.statusTV);
            walletBal = itemView.findViewById(R.id.balanceTV);
            viewLine = itemView.findViewById(R.id.viewV);
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

}
