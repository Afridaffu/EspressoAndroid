package com.greenbox.coyni.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TransactionListPostedAdapter extends  RecyclerView.Adapter<TransactionListPostedAdapter.MyViewHolder> {
    Context mecontext;
    MyApplication objMyApplication;
    List<TransactionListPosted> transactionListItemsposted;
    public TransactionListPostedAdapter(List<TransactionListPosted> list, Context context){
        this.transactionListItemsposted=list;
        this.mecontext=context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @NonNull
    @Override
    public TransactionListPostedAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_posted_listitem, parent, false);

        return new TransactionListPostedAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionListPostedAdapter.MyViewHolder holder, int position) {
        TransactionListPosted objData=transactionListItemsposted.get(position);

        String strType = "", strPrev = "", strCurr = "", strCurDate = "";
//            holder.tvDate.setText(Utils.convertDate(objData.getCreatedAt()));
//        holder.date.setText(objMyApplication.convertZoneDate(objData.getUpdatedAt()));
        holder.date.setText(Utils.convertDate(objData.getUpdatedAt()));
        if (position == 0) {
            try {
                holder.date.setVisibility(View.VISIBLE);
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                strCurDate = spf.format(Calendar.getInstance().getTime());
                Log.e("dateTransaction",""+objMyApplication.convertZoneDate(objData.getUpdatedAt()));

                if (Utils.convertDate(objData.getUpdatedAt()).equals(Utils.convertDate(strCurDate))) {
                    holder.date.setText("Today");
                    holder.topRadius.setBackgroundResource(R.drawable.topradius_list_items);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        if (position != 0) {

            try {
                strPrev = objMyApplication.convertZoneDate(transactionListItemsposted.get(position - 1).getUpdatedAt());
                strCurr = objMyApplication.convertZoneDate(objData.getUpdatedAt());
                if (strPrev.equals(strCurr)) {
                    holder.date.setVisibility(View.GONE);
                } else {
                    holder.date.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //txn description
        if(objData.getTxnDescription().length()>25){
            holder.txnDescrip.setText(objData.getTxnDescription().substring(0,25)+"...");
        }
        else {
            holder.txnDescrip.setText(objData.getTxnDescription());
        }
        holder.walletBal.setText(convertTwoDecimal(objData.getWalletBalance()));


        //type transaction
        if (objData.getTxnTypeDn().toLowerCase().contains("withdraw")) {
            strType = "withdraw";
        }
        else if (objData.getTxnTypeDn().toLowerCase().contains("pay") || objData.getTxnTypeDn().toLowerCase().contains("request")) {
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
            holder.amount.setTextColor(Color.parseColor("#000000"));
        } else if (strType.contains("buy")||strType.equals("receive")){
            holder.amount.setText("+" + convertTwoDecimal(objData.getAmount()));
            holder.amount.setTextColor(Color.parseColor("#008a05"));

        }
        else {
            holder.amount.setText(convertTwoDecimal(objData.getAmount()));
        }
//        switch (strType) {
//            case "pay":
//                holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokensend));
//                holder.imgIcon.setImageResource(R.drawable.ic_token_send);
//                break;
//            case "receive":
//                holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokenrece));
//                holder.imgIcon.setImageResource(R.drawable.ic_token_receive);
//                break;
//            case "withdraw":
//                holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokenwith));
//                holder.imgIcon.setImageResource(R.drawable.ic_token_withdraw);
//                break;
//            case "buy":
//                holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokenpaid));
//                holder.imgIcon.setImageResource(R.drawable.ic_token_paid);
//                break;
//            case "deposit":
//                holder.cvType.setCardBackgroundColor(mContext.getResources().getColor(R.color.tokenadd));
//                holder.imgIcon.setImageResource(R.drawable.ic_buy_token);
//                break;
//        }
        holder.txnStatus.setText(objData.getTxnStatusDn());
        switch (objData.getTxnStatusDn().replace(" ", "").toLowerCase()) {
            case Utils.transInProgress:
                holder.txnStatus.setTextColor(Color.parseColor("#2196F3"));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_status_corner_radius);
                holder.txnStatus.setBackgroundColor(Color.parseColor("#E8F3F9"));
                break;
            case Utils.transPending:
                holder.txnStatus.setTextColor(Color.parseColor("#A5A5A5"));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_status_corner_radius);
                holder.txnStatus.setBackgroundColor(Color.parseColor("#FFF6E5"));
                break;
            case Utils.transCompleted:
                holder.txnStatus.setTextColor(Color.parseColor("#00CC6E"));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_status_corner_radius);
                holder.txnStatus.setBackgroundColor(Color.parseColor("#E5F3E5"));
                break;
            case Utils.transFailed:
                holder.txnStatus.setTextColor(Color.parseColor("#D45858"));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_status_corner_radius);
                holder.txnStatus.setBackgroundColor(Color.parseColor("#EFEFEF"));
                break;
        }

//
//        holder.amount.setText(transactionListItemsposted.get(position).getAmount());
//        holder.txnStatus.setText(transactionListItemsposted.get(position).getTxnStatusDn());
//
//        holder.walletBal.setText(transactionListItemsposted.get(position).getWalletBalance());




    }

    @Override
    public int getItemCount() {
        Log.e("size",""+transactionListItemsposted.size());
        return transactionListItemsposted.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date,txnDescrip,amount,txnStatus,walletBal;
//        CardView statusBackcolor;
        RelativeLayout topRadius;
        MyApplication myApplication;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.dateTV);
            txnDescrip=itemView.findViewById(R.id.messageTV);
            amount=itemView.findViewById(R.id.amountTV);
            txnStatus=itemView.findViewById(R.id.statusTV);
            walletBal=itemView.findViewById(R.id.balanceTV);
//            statusBackcolor=itemView.findViewById(R.id.statusCVbg);
            topRadius=itemView.findViewById(R.id.layoutTopRadiusRL);
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
                transactionListItemsposted.add(null);
                notifyItemInserted(transactionListItemsposted.size() - 1);
            }
        });
    }

    public void removeLoadingView() {
        //Remove loading item
        transactionListItemsposted.remove(transactionListItemsposted.size() - 1);
        notifyItemRemoved(transactionListItemsposted.size());
    }

    public void addData(List<TransactionListPosted> listItems) {
        this.transactionListItemsposted.addAll(listItems);
        notifyDataSetChanged();
    }

}
