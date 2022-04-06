package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
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
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.TransactionDetailsActivity;
import com.greenbox.coyni.view.business.BusinessBatchPayoutIdDetailsActivity;
import com.greenbox.coyni.view.business.BusinessBatchPayoutSearchActivity;

import java.util.Arrays;
import java.util.List;


public class BatchPayoutListAdapter extends BaseRecyclerViewAdapter<BatchPayoutListAdapter.MyViewHolder> {

    MyApplication objMyApplication;
    private OnItemClickListener listener;
    List<BatchPayoutListItems> listItems;
    Context context;


    public BatchPayoutListAdapter(Context context, List<BatchPayoutListItems> payoutList) {
        this.context = context;
        this.listItems = payoutList;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView payoutSentTextTV, payoutMoneyTV, statusTV, payoutDateTV;
        LinearLayout detailsLL;

        public MyViewHolder(View view) {
            super(view);
            detailsLL = view.findViewById(R.id.payoutLL);
            payoutSentTextTV = view.findViewById(R.id.payoutSentText);
            payoutMoneyTV = view.findViewById(R.id.payoutMoneyTV);
            statusTV = view.findViewById(R.id.payoutListState);
            payoutDateTV = view.findViewById(R.id.payoutDate);
        }
    }

    @Override
    public BatchPayoutListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View payoutsearch = layoutInflater.inflate(R.layout.business_batch_payout_search, parent, false);
        return new BatchPayoutListAdapter.MyViewHolder(payoutsearch);
    }

    @Override
    public void onBindViewHolder(@NonNull BatchPayoutListAdapter.MyViewHolder holder, int position) {
        holder.detailsLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position, listItems.get(position));
            }
        });
//        if (listItems.get().getStatus().equalsIgnoreCase("closed")) {

        BatchPayoutListItems objData = listItems.get(position);

        if (objData.getSentTo() != null && !objData.getSentTo().equals("")) {
            holder.payoutSentTextTV.setText(objData.getSentTo());
        }
        if (objData.getTotalAmount() != null && !objData.getTotalAmount().equals("")) {
            holder.payoutMoneyTV.setText(Utils.convertTwoDecimal(objData.getTotalAmount()));
        }
        if (objData.getCreatedAt() != null && !objData.getCreatedAt().equals("")) {
            String date = objData.getCreatedAt();
            if (date.contains(".")) {
                String resDate = date.substring(0, date.lastIndexOf("."));
                holder.payoutDateTV.setText(objMyApplication.convertZoneDateTime(resDate, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma"));
            } else {
            }
        }

//        if (objData.getStatus().equalsIgnoreCase("closed")) {
//            holder.statusTV.setText(objData.getStatus());
//            holder.statusTV.setTextColor(context.getColor(R.color.completed_status));
//            holder.statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
//        }
        holder.statusTV.setText(objData.getStatus());

        switch (objData.getStatus().toLowerCase()) {
            case "closed":
                holder.statusTV.setTextColor(context.getColor(R.color.completed_status));
                holder.statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case "in progress":
                holder.statusTV.setTextColor(context.getColor(R.color.inprogress_status));
                holder.statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                break;
            case "open":
                holder.statusTV.setTextColor(context.getColor(R.color.pending_status));
                holder.statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                break;
            case "failed":
                holder.statusTV.setTextColor(context.getColor(R.color.failed_status));
                holder.statusTV.setBackgroundResource(R.drawable.txn_failed_bg);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}


