package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;

public class ReserveDetailsListAdapter extends BaseRecyclerViewAdapter<ReserveDetailsListAdapter.MyViewHolder>{

    private Context context;
    private OnItemClickListener listener;

    public  ReserveDetailsListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View detailsList = inflater.inflate(R.layout.reserve_recent_transactions, parent, false);
        MyViewHolder myViewHolder =new MyViewHolder(detailsList);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

//        holder.statusType.setText("On Hold");
//        holder.amount.setTextColor(context.getColor(R.color.active_black));
//        holder.statusType.setTextColor(context.getColor(R.color.pending_color));
//        holder.statusType.setBackgroundResource(R.drawable.txn_pending_bg);
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlBase;
        public TextView amount, amountTV, dateTime, name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTv);
            amount = itemView.findViewById(R.id.ammont_TV);
            amountTV = itemView.findViewById(R.id.amountTV);
            dateTime = itemView.findViewById(R.id.dateTime_TV);
            rlBase = itemView.findViewById(R.id.rl_base);
        }
    }
}
