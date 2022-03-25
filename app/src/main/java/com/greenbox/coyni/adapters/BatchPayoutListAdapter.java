package com.greenbox.coyni.adapters;

import android.content.Context;
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
            detailsLL = view.findViewById(R.id.detailsLL);
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

        BatchPayoutListItems objData = listItems.get(position);

        if (objData.getSentTo() != null && !objData.getSentTo().equals("")) {
            holder.payoutSentTextTV.setText(objData.getSentTo());
        }
//        if (objData.getStatus() != null && !objData.getStatus().equals("")) {
//            holder.statusTV.setText(objData.getStatus());
//        }

        if (objData.getTotalAmount() != null && !objData.getTotalAmount().equals("")) {
            holder.payoutMoneyTV.setText(Utils.convertTwoDecimal(objData.getTotalAmount()));
        }

        if (objData.getCreatedAt() != null && !objData.getCreatedAt().equals("")) {
            holder.payoutDateTV.setText(Utils.convertPayoutDate(objData.getCreatedAt()));
        }

//        holder.payoutDateTV.setText(Utils.convertPayoutDate(objData.getCreatedAt().split("")[0]) + Utils.convertPayoutTime(objData.getCreatedAt().split("")[1]));
//        if (objData.getStatus() != null && !objData.getStatus().equals("")) {
//            holder.statusTV.setText(objData.getStatus());
//        }
//

        holder.statusTV.setText(objData.getStatus());
        switch (objData.getStatus().toLowerCase()) {
            case "completed":
                holder.statusTV.setTextColor(context.getResources().getColor(R.color.completed_status));
                holder.statusTV.setBackgroundResource(R.drawable.txn_completed_bg);
                break;
            case "in progress":
                holder.statusTV.setTextColor(context.getResources().getColor(R.color.inprogress_status));
                holder.statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg);
                break;
//            case "pending":
//                holder.statusTV.setTextColor(context.getResources().getColor(R.color.pending_status));
//                holder.statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
//                break;
            case "partialrefund":
                holder.statusTV.setTextColor(context.getResources().getColor(R.color.pending_status));
                holder.statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                break;
            case "open":
                holder.statusTV.setTextColor(context.getResources().getColor(R.color.pending_status));
                holder.statusTV.setBackgroundResource(R.drawable.txn_pending_bg);
                break;

        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }
}


//        holder.detailsLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.onItemClick(position, null);
//            }
//        });


