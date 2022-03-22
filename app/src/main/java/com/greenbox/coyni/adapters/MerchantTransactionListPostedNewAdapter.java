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
import java.util.List;

public class MerchantTransactionListPostedNewAdapter extends BaseRecyclerViewAdapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private MyApplication objMyApplication;
    private List<ListItem> consolidatedListData = new ArrayList<>();
    private List<TransactionListPosted> transactionListItemsPosted;
    private OnItemClickListener listener;

    public MerchantTransactionListPostedNewAdapter(List<TransactionListPosted> list, Context context) {
        this.transactionListItemsPosted = list;
        this.mContext = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();

        consolidatedListData = new ArrayList<>();
        for (int i = 0; i < transactionListItemsPosted.size(); i++) {
            String datee = objMyApplication.convertZoneDateLastYear(transactionListItemsPosted.get(i).getUpdatedAt().split("\\.")[0]);
            DateItem dateItem = new DateItem();
            dateItem.setDate(datee);
            if (consolidatedListData.contains(dateItem)) {
                consolidatedListData.add(transactionListItemsPosted.get(i));
            } else {
                consolidatedListData.add(dateItem);
            }
        }


//        for (int i = 0; i < list.size(); i++) {
//            String datee = objMyApplication.convertZoneDateLastYear(list.get(i).getUpdatedAt().split("\\.")[0]);
//            if (!dates.contains(datee)) {
//                dates.add(datee);
//            }
//        }
//
//        for (int j = 0; j < dates.size(); j++) {
//            List<TransactionListPosted> individualDateData = new ArrayList<>();
//            for (int i = 0; i < list.size(); i++) {
//                String datee = objMyApplication.convertZoneDateLastYear(list.get(i).getUpdatedAt().split("\\.")[0]);
//                if (dates.get(j).equals(datee)) {
//                    individualDateData.add(list.get(i));
//                }
//            }
//            listedData.add(individualDateData);
//        }
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedListData.get(position).getType();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ListItem.TYPE_GENERAL:
                View v1 = inflater.inflate(R.layout.posted_inner_item, parent, false);
                viewHolder = new ItemViewHolder(v1);
                break;
            case ListItem.TYPE_GROUP:
                View v2 = inflater.inflate(R.layout.group_item, parent, false);
                viewHolder = new GroupViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ListItem.TYPE_GENERAL:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                TransactionListPosted objData = (TransactionListPosted) consolidatedListData.get(position);
                setItemViewData(objData, itemViewHolder);
                break;
            case ListItem.TYPE_GROUP:
                GroupViewHolder groupViewHolder = (GroupViewHolder) holder;
                DateItem dateItem = (DateItem) consolidatedListData.get(position);
                setGroupViewData(dateItem, groupViewHolder);
                break;
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
            //businessTx.setVisibility(View.GONE);
        }
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView date;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_group_name);
        }
    }

    public void updateList(List<List<TransactionListPosted>> list) {
        //listedData = list;
        notifyDataSetChanged();
    }

    private void setGroupViewData(DateItem date, GroupViewHolder holder) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strCurDate = spf.format(Calendar.getInstance().getTime());
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

    private void setItemViewData(TransactionListPosted objData, ItemViewHolder holder) {
        String strType = "";
        String[] data = objData.getTxnTypeDn().replace("****", "-").split("-");
        try {
            if (data.length > 1) {
                holder.txnTypeDnExtention.setVisibility(View.VISIBLE);
                holder.txnTypeDn.setText(data[0]);
                holder.txnTypeDnExtention.setText("**" + data[1]);
                holder.txnTypeDn.setVisibility(View.VISIBLE);
            } else {
                holder.txnTypeDn.setText(objData.getTxnTypeDn());
                // holder.txnTypeDnExtention.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.createdDate.setText(Utils.convertDate(objData.getCreatedAt()));

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
                if(listener != null) {
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
