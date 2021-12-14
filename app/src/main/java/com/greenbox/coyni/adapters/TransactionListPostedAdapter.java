package com.greenbox.coyni.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.TransactionListActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TransactionListPostedAdapter extends RecyclerView.Adapter<TransactionListPostedAdapter.MyViewHolder> {
    Context mecontext;
    MyApplication objMyApplication;
    List<TransactionListPosted> transactionListItemsposted;
    TransactionList transactionList;

    public TransactionListPostedAdapter(List<TransactionListPosted> list, Context context) {
        this.transactionListItemsposted = list;
        this.mecontext = context;
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
        TransactionListPosted objData = transactionListItemsposted.get(position);
        boolean isLastYear = false, isNextAvailabe = false, isNextSameDate = false;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        String strType = "", strPrev = "", strCurr = "", strCurDate = "", strNext = "", nextCardDate = "";
        String cardDate = objMyApplication.convertZoneDateLastYear(objData.getUpdatedAt().split("\\.")[0]);
        try {
            nextCardDate = objMyApplication.convertZoneDateLastYear(transactionListItemsposted.get(position + 1).getUpdatedAt().split("\\.")[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (nextCardDate.equals("")) {
            isNextAvailabe = false;
            isNextSameDate = false;
        } else if (cardDate.equals(nextCardDate)) {
            isNextAvailabe = true;
            isNextSameDate = true;
        } else if (!cardDate.equals(nextCardDate)) {
            isNextAvailabe = true;
            isNextSameDate = false;
        }
        try {
            if (currentYear == Integer.parseInt(cardDate.split(",")[1].trim())) {
                isLastYear = false;
                holder.date.setText(objMyApplication.convertNewZoneDate(objData.getUpdatedAt().split("\\.")[0]));
            } else {
                isLastYear = true;
                holder.date.setText(objMyApplication.convertZoneDateLastYear(objData.getUpdatedAt().split("\\.")[0]));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (position == 0) {
            try {
                holder.date.setVisibility(View.VISIBLE);
                if (isNextAvailabe && isNextSameDate) {
                    holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.topradius_list_items));
                    holder.lineItem.setVisibility(View.VISIBLE);
                } else if (isNextAvailabe && !isNextSameDate) {
                    holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.fullradius_list_items));
                    holder.lineItem.setVisibility(View.GONE);
                } else {
                    holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.fullradius_list_items));
                    holder.lineItem.setVisibility(View.GONE);
                }
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                strCurDate = spf.format(Calendar.getInstance().getTime());
                if (Utils.convertDate(objData.getUpdatedAt()).equals(Utils.convertDate(strCurDate))) {
                    holder.date.setText("Today");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (position != 0) {

            try {
                strPrev = objMyApplication.convertZoneDateLastYear(transactionListItemsposted.get(position - 1).getUpdatedAt().split("\\.")[0]);
                try {
                    strNext = objMyApplication.convertZoneDateLastYear(transactionListItemsposted.get(position + 1).getUpdatedAt().split("\\.")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                strCurr = objMyApplication.convertZoneDateLastYear(objData.getUpdatedAt().split("\\.")[0]);


                if (!strCurr.equals(strNext)) {
                    if (isLastYear) {
                        holder.date.setVisibility(View.VISIBLE);
                        if (isNextAvailabe && isNextSameDate) {
                            holder.lineItem.setVisibility(View.VISIBLE);
                        } else if (isNextAvailabe && !isNextSameDate) {
                            holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.card_bottomradius));
                            holder.lineItem.setVisibility(View.GONE);
                        } else {
                            holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.fullradius_list_items));
                            holder.lineItem.setVisibility(View.GONE);
                        }

//                        holder.lineItem.setVisibility(View.VISIBLE);
                    } else {
                        holder.date.setVisibility(View.GONE);
                        if (isNextAvailabe && isNextSameDate) {
                            holder.lineItem.setVisibility(View.VISIBLE);
                        } else if (isNextAvailabe && !isNextSameDate) {
                            holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.card_bottomradius));
                            holder.lineItem.setVisibility(View.GONE);
                        } else if (!isNextAvailabe && !isNextSameDate) {
                            holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.card_bottomradius));
                            holder.lineItem.setVisibility(View.GONE);
                        } else {
                            holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.fullradius_list_items));
                            holder.lineItem.setVisibility(View.GONE);
                        }
//                        holder.lineItem.setVisibility(View.GONE);
                    }
                } else {
                    if (strPrev.equals(strCurr)) {
                        holder.date.setVisibility(View.GONE);
                    } else {
                        if (isNextAvailabe && isNextSameDate) {
                            holder.lineItem.setVisibility(View.VISIBLE);
                            holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.topradius_list_items));
                        } else if (isNextAvailabe && !isNextSameDate) {
                            holder.lineItem.setVisibility(View.GONE);
                            holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.card_bottomradius));
                        } else {
                            holder.lineItem.setVisibility(View.GONE);
                            holder.itemRL.setBackground(mecontext.getDrawable(R.drawable.fullradius_list_items));
                        }
//                        holder.date.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //txn description
        if (objData.getTxnDescription().length() > 32) {
            holder.txnDescrip.setText(objData.getTxnDescription().substring(0, 32) + "...");
        } else {
            holder.txnDescrip.setText(objData.getTxnDescription());
        }
        holder.walletBal.setText(convertTwoDecimal(objData.getWalletBalance()));


        //type transaction
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
            holder.amount.setTextColor(Color.parseColor("#000000"));
        } else if (strType.contains("buy") || strType.equals("receive")) {
            holder.amount.setText("+" + convertTwoDecimal(objData.getAmount()));
            holder.amount.setTextColor(Color.parseColor("#008a05"));

        } else {
            holder.amount.setText(convertTwoDecimal(objData.getAmount()));
        }
        holder.txnStatus.setText(objData.getTxnStatusDn());
        switch (objData.getTxnStatusDn().replace(" ", "").toLowerCase()) {
            case Utils.transInProgress:
                holder.txnStatus.setTextColor(mecontext.getResources().getColor(R.color.under_review_blue));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_inprogress_bg);
                break;
            case Utils.transPending:
                holder.txnStatus.setTextColor(mecontext.getResources().getColor(R.color.orange));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_pending_bg);
                break;
            case Utils.transCompleted:
                holder.txnStatus.setTextColor(mecontext.getResources().getColor(R.color.active_green));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case Utils.transFailed:
                holder.txnStatus.setTextColor(mecontext.getResources().getColor(R.color.error_red));
                holder.txnStatus.setBackgroundResource(R.drawable.txn_failed_bg);
                break;
        }

//        if (position == transactionListItemsposted.size() - 1) {
//            if (TransactionListActivity.transactionListActivity.total > TransactionListActivity.transactionListActivity.currentPage) {
//                TransactionListActivity.transactionListActivity.progressBar.setVisibility(View.VISIBLE);
//                TransactionListActivity.transactionListActivity.currentPage = TransactionListActivity.transactionListActivity.currentPage + 1;
//                Log.e("CurrentPage", TransactionListActivity.transactionListActivity.currentPage + "");
//                TransactionListRequest transactionListRequest = new TransactionListRequest();
//                transactionListRequest.setPageNo(String.valueOf(TransactionListActivity.transactionListActivity.currentPage));
//                transactionListRequest.setWalletCategory(Utils.walletCategory);
//                transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
//                TransactionListActivity.transactionListActivity.dashboardViewModel.meTransactionList(transactionListRequest);
//
//            }
//        }
    }

    @Override
    public int getItemCount() {
        Log.e("size", "" + transactionListItemsposted.size());
        return transactionListItemsposted.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, txnDescrip, amount, txnStatus, walletBal;
        //        CardView statusBackcolor;
        RelativeLayout itemRL;
        MyApplication myApplication;
        LinearLayout lineItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateTV);
            txnDescrip = itemView.findViewById(R.id.messageTV);
            amount = itemView.findViewById(R.id.amountTV);
            txnStatus = itemView.findViewById(R.id.statusTV);
            walletBal = itemView.findViewById(R.id.balanceTV);
//            statusBackcolor=itemView.findViewById(R.id.statusCVbg);
            itemRL = itemView.findViewById(R.id.layoutTopRadiusRL);
            lineItem = itemView.findViewById(R.id.viewV);
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
    public void updateList(List<TransactionListPosted> list) {
        transactionListItemsposted = list;
        notifyDataSetChanged();
    }

}
