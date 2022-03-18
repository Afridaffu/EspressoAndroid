package com.greenbox.coyni.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutList;


public class BatchPayoutListAdapter extends BaseRecyclerViewAdapter<BatchPayoutListAdapter.MyViewHolder>{

    public BatchPayoutListAdapter(BatchPayoutList[] payoutList) {
        super();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) { }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView payoutSentTextTV,payoutMoneyTV,paidTV,payoutDateTV;

        public MyViewHolder(View view) {
            super(view);
            payoutSentTextTV = (TextView) view.findViewById(R.id.payoutSentTextTV);
            payoutMoneyTV = (TextView) view.findViewById(R.id.payoutMoneyTV);
            paidTV = (TextView) view.findViewById(R.id.paidTV);
            payoutDateTV = (TextView) view.findViewById(R.id.payoutDateTV);
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

    }

    @Override
    public int getItemCount() {
        return 8;
    }

}
