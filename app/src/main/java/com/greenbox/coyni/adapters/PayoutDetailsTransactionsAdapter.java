package com.greenbox.coyni.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DateItem;
import com.greenbox.coyni.model.ListItem;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class PayoutDetailsTransactionsAdapter extends BaseRecyclerViewAdapter<RecyclerView.ViewHolder>{
    private OnItemClickListener listener;
    private Context mContext;
    private MyApplication objMyApplication;
    private List<ListItem> consolidatedListData = new ArrayList<>();
    private List<TransactionListPosted> transactionListItemsPosted;


    public PayoutDetailsTransactionsAdapter(List<TransactionListPosted> list, Context context) {
        this.transactionListItemsPosted = list;
        this.mContext = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
        Collections.sort(transactionListItemsPosted, Collections.reverseOrder());
        consolidatedListData = new ArrayList<>();

        for (int i = 0; i < transactionListItemsPosted.size(); i++) {
            String datee = objMyApplication.convertZoneDateLastYear(transactionListItemsPosted.get(i).getUpdatedAt().split("\\.")[0]);
            DateItem dateItem = new DateItem();
            dateItem.setDate(datee);


//            if (!consolidatedListData.contains(dateItem)) {
//                consolidatedListData.add(dateItem);
//            }
            consolidatedListData.add(transactionListItemsPosted.get(i));

        }
    }


    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener= listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListItem.TYPE_GENERAL:
                View v1 = inflater.inflate(R.layout.posted_inner_item, parent, false);
                viewHolder = new PayoutDetailsTransactionsAdapter.ItemViewHolder(v1);
                break;
//            case ListItem.TYPE_GROUP:
//                View v2 = inflater.inflate(R.layout.group_item, parent, false);
//                viewHolder = new PayoutDetailsTransactionsAdapter.GroupViewHolder(v2);
//                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedListData.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ListItem.TYPE_GENERAL:
                PayoutDetailsTransactionsAdapter.ItemViewHolder itemViewHolder = (PayoutDetailsTransactionsAdapter.ItemViewHolder) holder;
                TransactionListPosted objData = (TransactionListPosted) consolidatedListData.get(position);
                setItemViewData(objData, itemViewHolder);
                break;
//            case ListItem.TYPE_GROUP:
//                PayoutDetailsTransactionsAdapter.GroupViewHolder groupViewHolder = (PayoutDetailsTransactionsAdapter.GroupViewHolder) holder;
//                DateItem dateItem = (DateItem) consolidatedListData.get(position);
//                setGroupViewData(dateItem, groupViewHolder);
//                break;
        }

    }

    @Override
    public int getItemCount() {
        return consolidatedListData.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txnTypeDn, txnTypeDnExtention, amount, txnStatus, createdDate, businessTx;
        RelativeLayout itemRL;
        LinearLayout lineItem;
        LinearLayout ll_merchant_transaction_item;
        View blankView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            txnTypeDn = itemView.findViewById(R.id.messageTV);
            txnTypeDnExtention = itemView.findViewById(R.id.messagTV);
            ll_merchant_transaction_item = itemView.findViewById(R.id.ll_merchant_transaction_item);
            amount = itemView.findViewById(R.id.amountTV);
            txnStatus = itemView.findViewById(R.id.statusTV);
            createdDate = itemView.findViewById(R.id.balanceTV);
            itemRL = itemView.findViewById(R.id.layoutTopRadiusRL);
            lineItem = itemView.findViewById(R.id.viewV);
            blankView = itemView.findViewById(R.id.blankView);
            businessTx = itemView.findViewById(R.id.balTXT);
            businessTx.setVisibility(View.GONE);
            txnTypeDnExtention.setVisibility(View.GONE);
        }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        View viewBottomCorner;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_group_name);
            viewBottomCorner = itemView.findViewById(R.id.bottom_corners);
        }
    }

    private void setGroupViewData(DateItem date, MerchantTransactionListPostedNewAdapter.GroupViewHolder holder) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strCurDate = spf.format(Calendar.getInstance().getTime());
        if (holder.getAdapterPosition() == 0) {
            holder.viewBottomCorner.setVisibility(View.GONE);
        } else {
            holder.viewBottomCorner.setVisibility(View.VISIBLE);
        }
        if (date.getDate().equals(objMyApplication.convertZoneDateLastYear(strCurDate))) {
            holder.date.setText("Today");
        } else {
            if (currentYear == Integer.parseInt(date.getDate().split(",")[1].trim())) {
                holder.date.setText(date.getDate().split(",")[0].trim());
            } else {
                holder.date.setText(date.getDate());
            }
        }
    }

    private void setItemViewData(TransactionListPosted objData, PayoutDetailsTransactionsAdapter.ItemViewHolder holder) {

        if (objData.getCreatedAt() != null && !objData.getCreatedAt().equals("")){
            String date = objData.getCreatedAt();
            if (date.contains(".")) {
                date = date.substring(0, date.lastIndexOf("."));
            }
            date = objMyApplication.convertZoneDateTime(date, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy hh:mma");
            holder.createdDate.setText(date.toLowerCase());

        }        //type transaction
        if (objData.getTxnTypeDn().equalsIgnoreCase(Utils.SaleOrder)) {
            holder.txnTypeDn.setText(objData.getTxnTypeDn() + " - " + objData.getSenderName());
            holder.amount.setText(convertTwoDecimal(objData.getAmount()).replace("CYN"," "));
            holder.amount.setTextColor(Color.parseColor("#008a05"));
        }else if(objData.getTxnTypeDn().equalsIgnoreCase(Utils.Refund)){
            holder.txnTypeDn.setText(objData.getTxnTypeDn() + " to " + objData.getSenderName());
            holder.amount.setText(" - " + convertTwoDecimal(objData.getAmount()).replace("CYN"," "));
            holder.amount.setTextColor(Color.parseColor("#000000"));
        }else if(objData.getTxnTypeDn().equalsIgnoreCase(Utils.MerchantPayout)){
            holder.txnTypeDn.setText(objData.getTxnTypeDn() + " to " + objData.getReceiveName());
            holder.amount.setText(convertTwoDecimal(objData.getAmount()).replace("CYN"," "));
            holder.amount.setTextColor(Color.parseColor("#008a05"));
        }else if(objData.getTxnTypeDn().equalsIgnoreCase(Utils.MonthlyServiceFee)){
            holder.txnTypeDn.setText(objData.getTxnTypeDn());
            holder.amount.setText(" - " + convertTwoDecimal(objData.getAmount()).replace("CYN"," "));
            holder.amount.setTextColor(Color.parseColor("#000000"));
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

        holder.ll_merchant_transaction_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(0, objData);
                }
            }
        });
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
}
