package com.greenbox.coyni.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.activtity_log.ActivityLogResp;
import com.greenbox.coyni.model.preferences.BaseProfile;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.List;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.MyViewHolder>{

    ActivityLogResp respList;
    Context mContext;
    MyApplication myApplication;
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_log_items, parent, false);
        return new MyViewHolder(itemView);
    }

    public ActivityLogAdapter(ActivityLogResp list, Context context) {
        this.mContext = context;
        this.respList = list;
        myApplication= (MyApplication) mContext.getApplicationContext();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.createdTime.setText(myApplication.convertZoneLatestTxn(respList.getData().get(position).getCreatedAt()));
        holder.messageTv.setText(respList.getData().get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return respList.getData().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView createdTime,messageTv,type;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            createdTime = itemView.findViewById(R.id.purchaseTime);
            messageTv = itemView.findViewById(R.id.message_tv);
            type = itemView.findViewById(R.id.purchase_tv);

        }
    }
}
