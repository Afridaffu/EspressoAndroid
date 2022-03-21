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

public class ReserveReleasesRollingAdapter extends BaseRecyclerViewAdapter<ReserveReleasesRollingAdapter.MyViewHolder> {

    private Context context;
    private OnItemClickListener listener;

    public ReserveReleasesRollingAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReserveReleasesRollingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rollingDetails = inflater.inflate(R.layout.rolling_details, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(rollingDetails);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReserveReleasesRollingAdapter.MyViewHolder holder, int position) {

        if (position % 2 == 0) {
            holder.statusType.setText("On Hold");
            holder.amount.setTextColor(context.getColor(R.color.active_black));
            holder.statusType.setTextColor(context.getColor(R.color.pending_color));
            holder.statusType.setBackgroundResource(R.drawable.txn_pending_bg);
        } else {

            if (position % 3 == 0) {
                holder.statusType.setText("Open");
                holder.statusType.setTextColor(context.getColor(R.color.inprogress_status));
                holder.statusType.setBackgroundResource(R.drawable.txn_inprogress_bg);
            }
            else {
                holder.statusType.setText("Released");
                holder.amount.setText("6435.21");
                holder.amount.setTextColor(context.getColor(R.color.active_green));
                holder.statusType.setTextColor(context.getColor(R.color.completed_status));
                holder.statusType.setBackgroundResource(R.drawable.txn_completed_bg);
            }
        }
        holder.rlBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(null, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return 21;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlBase;
        public TextView amount, statusType, dateTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.ammont_TV);
            statusType = itemView.findViewById(R.id.status_TV);
            dateTime = itemView.findViewById(R.id.dateTime_TV);
            rlBase = itemView.findViewById(R.id.rl_base);
        }
    }
}
