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
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.List;

public class ReserveReleasesRollingAdapter extends BaseRecyclerViewAdapter<ReserveReleasesRollingAdapter.MyViewHolder> {

    private Context context;
    private OnItemClickListener listener;
    List<BatchPayoutListItems> items;
    MyApplication myApplication;
    private String date;

    public ReserveReleasesRollingAdapter(Context context, List<BatchPayoutListItems> items) {
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
        return new MyViewHolder(rollingDetails);
    }

    @Override
    public void onBindViewHolder(@NonNull ReserveReleasesRollingAdapter.MyViewHolder holder, int position) {

        BatchPayoutListItems objData = items.get(position);
        if (objData.getSentTo() != null && !objData.getSentTo().equals("")){
            holder.name.setText(objData.getSentTo());
        }

        if (objData.getReserveAmount() != null && !objData.getReserveAmount().equals("")){
            holder.amount.setText(Utils.convertTwoDecimal(objData.getReserveAmount()));
        }else {
            holder.amount.setText("");
        }
        if (objData.getStatus() != null && !objData.getStatus().equals("")){
            holder.statusType.setText(objData.getStatus());
            if(objData.getStatus().equalsIgnoreCase(Utils.ROLLING_LIST_STATUS.OPEN.getStatus())) {
                holder.statusType.setTextColor(context.getColor(R.color.inprogress_status));
                holder.statusType.setBackgroundResource(R.drawable.txn_inprogress_bg);
            } else if(objData.getStatus().equalsIgnoreCase(Utils.ROLLING_LIST_STATUS.ON_HOLD.getStatus())) {
                holder.amount.setTextColor(context.getColor(R.color.active_black));
                holder.statusType.setTextColor(context.getColor(R.color.pending_color));
                holder.statusType.setBackgroundResource(R.drawable.txn_pending_bg);
            } else if(objData.getStatus().equalsIgnoreCase(Utils.ROLLING_LIST_STATUS.RELEASED.getStatus())) {
                holder.amount.setTextColor(context.getColor(R.color.active_green));
                holder.statusType.setTextColor(context.getColor(R.color.completed_status));
                holder.statusType.setBackgroundResource(R.drawable.txn_completed_bg);
            } else if(objData.getStatus().equalsIgnoreCase(Utils.ROLLING_LIST_STATUS.CANCELED.getStatus())) {
                holder.amount.setTextColor(context.getColor(R.color.active_black));
                holder.statusType.setTextColor(context.getColor(R.color.failed_status));
                holder.statusType.setBackgroundResource(R.drawable.txn_failed_bg);
            }
        }
        if (objData.getCreatedAt() != null && !objData.getCreatedAt().equals("")){
            date = objData.getCreatedAt();
            if (date.contains(".")) {
                date = date.substring(0, date.lastIndexOf("."));
            }
            date = myApplication.convertZoneDateTime(date, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma");
            if(objData.getStatus().equalsIgnoreCase(String.valueOf(Utils.ROLLING_LIST_STATUS.RELEASED))){
                holder.dateTime.setText(date.toLowerCase());

            } else {
                holder.dateTime.setText("Release: " + date.toLowerCase());
            }
        }

        if (!objData.getStatus().equalsIgnoreCase(Utils.OPEN)) {
            holder.rlBase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(holder.getAdapterPosition(), items.get(holder.getAbsoluteAdapterPosition()));
                }
            });
        }
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
