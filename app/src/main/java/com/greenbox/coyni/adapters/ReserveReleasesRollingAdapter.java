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
import com.greenbox.coyni.model.reserverolling.RollingItem;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.List;

public class ReserveReleasesRollingAdapter extends BaseRecyclerViewAdapter<ReserveReleasesRollingAdapter.MyViewHolder> {

    private Context context;
    private OnItemClickListener listener;
    List<RollingItem> items;
    MyApplication myApplication;

    public ReserveReleasesRollingAdapter(Context context, List<RollingItem> items) {
        this.context = context;
        this.items = items;
        this.myApplication =(MyApplication)context.getApplicationContext();
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReserveReleasesRollingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rollingDetails = inflater.inflate(R.layout.rolling_list, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(rollingDetails);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReserveReleasesRollingAdapter.MyViewHolder holder, int position) {

//        if (position % 2 == 0) {
//            holder.statusType.setText("On Hold");
//            holder.amount.setTextColor(context.getColor(R.color.active_black));
//            holder.statusType.setTextColor(context.getColor(R.color.pending_color));
//            holder.statusType.setBackgroundResource(R.drawable.txn_pending_bg);
//        } else {
//
//            if (position % 3 == 0) {
//                holder.statusType.setText("Open");
//                holder.statusType.setTextColor(context.getColor(R.color.inprogress_status));
//                holder.statusType.setBackgroundResource(R.drawable.txn_inprogress_bg);
//            }
//            else {
//                holder.statusType.setText("Released");
//                holder.amount.setText("6435.21");
//                holder.amount.setTextColor(context.getColor(R.color.active_green));
//                holder.statusType.setTextColor(context.getColor(R.color.completed_status));
//                holder.statusType.setBackgroundResource(R.drawable.txn_completed_bg);
//            }
//        }

        RollingItem objData = items.get(position);
        if (objData.getSentTo() != null && !objData.getSentTo().equals("")){
            holder.name.setText(objData.getSentTo());
        }
        if (String.valueOf(objData.getReserveAmount()) != null && !String.valueOf(objData.getSentTo()).equals("")){
            holder.amount.setText(Utils.convertTwoDecimal(String.valueOf(objData.getReserveAmount())));
        }
        if (objData.getStatus() != null && !objData.getStatus().equals("")){
            holder.statusType.setText(objData.getStatus());
            switch (objData.getStatus()) {
                case Utils.open :

                    holder.statusType.setTextColor(context.getColor(R.color.inprogress_status));
                    holder.statusType.setBackgroundResource(R.drawable.txn_inprogress_bg);
                    break;

                case  Utils.onhold :

                    holder.amount.setTextColor(context.getColor(R.color.active_black));
                    holder.statusType.setTextColor(context.getColor(R.color.pending_color));
                    holder.statusType.setBackgroundResource(R.drawable.txn_pending_bg);
                    break;

                case Utils.released :

                    holder.amount.setTextColor(context.getColor(R.color.active_green));
                    holder.statusType.setTextColor(context.getColor(R.color.completed_status));
                    holder.statusType.setBackgroundResource(R.drawable.txn_completed_bg);
                    break;

                case Utils.canceled :

                    holder.amount.setTextColor(context.getColor(R.color.active_black));
                    holder.statusType.setTextColor(context.getColor(R.color.failed_status));
                    holder.statusType.setBackgroundResource(R.drawable.txn_failed_bg);
                    break;
            }
        }
        if (objData.getCreatedAt() != null && !objData.getCreatedAt().equals("")){
            if(objData.getStatus().equalsIgnoreCase(Utils.released)){
                holder.dateTime.setText(Utils.convertPayoutDate(objData.getCreatedAt()));

            } else {
                holder.dateTime.setText("Release: " + Utils.convertPayoutDate(objData.getCreatedAt()));


            }
        }

        holder.rlBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition(), null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rlBase;
        public TextView amount, statusType, dateTime, name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTV);
            amount = itemView.findViewById(R.id.ammont_TV);
            statusType = itemView.findViewById(R.id.status_TV);
            dateTime = itemView.findViewById(R.id.dateTime_TV);
            rlBase = itemView.findViewById(R.id.rl_base);
        }
    }
}
