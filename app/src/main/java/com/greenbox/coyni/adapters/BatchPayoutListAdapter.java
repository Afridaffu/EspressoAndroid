package com.greenbox.coyni.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutList;
import com.greenbox.coyni.view.TransactionDetailsActivity;
import com.greenbox.coyni.view.business.BusinessBatchPayoutIdDetailsActivity;


public class BatchPayoutListAdapter extends BaseRecyclerViewAdapter<BatchPayoutListAdapter.MyViewHolder>{

    Context mContext;
    private OnItemClickListener listener;

    public BatchPayoutListAdapter(BatchPayoutList[] payoutList) { super(); }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView payoutSentTextTV,payoutMoneyTV,paidTV,payoutDateTV;
        public LinearLayout detailsLL;

        public MyViewHolder(View view) {
            super(view);
            detailsLL = view.findViewById(R.id.detailsLL);
            payoutSentTextTV = view.findViewById(R.id.payoutSentTextTV);
            payoutMoneyTV = view.findViewById(R.id.payoutMoneyTV);
            paidTV = view.findViewById(R.id.paidTV);
            payoutDateTV = view.findViewById(R.id.payoutDateTV);
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

                listener.onItemClick(position, null);

            }
        });

    }

    @Override
    public int getItemCount() {
        return 8;
    }

}
