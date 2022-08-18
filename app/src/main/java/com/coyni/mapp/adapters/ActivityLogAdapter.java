package com.coyni.mapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.activtity_log.ActivityLogResp;
import com.coyni.mapp.utils.MyApplication;

public class ActivityLogAdapter extends RecyclerView.Adapter<ActivityLogAdapter.MyViewHolder> {

    ActivityLogResp respList;
    Context mContext;
    MyApplication myApplication;
    private static final String dateAndTime = "yyyy-MM-dd HH:mm:ss";
    private static final String requiredFormat = "MM/dd/yyyy h:mma";

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
        myApplication = (MyApplication) mContext.getApplicationContext();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.createdTime.setText(myApplication.convertZoneDateTime(respList.getData().get(position).getCreatedAt(), dateAndTime, requiredFormat).toLowerCase());
//        String str = " Chargeback case 124 created. <a href='api/v2/transactions/token/info/BUT38D6F83274A5F2C9C775B6AAE2C825/2?txnSubType=3'>Reference ID BUT38D6F83274A5F2C9C775B6AAE2C825</a>";
        String str = respList.getData().get(position).getMessage();

        if (str.contains("</a>")) {
            String firstText = str.split("<a")[0];
            String secondText = "Reference ID " + str.split("<a")[1].split("Reference ID")[1].replace("</a>", "");
            holder.messageTv.setText(firstText.trim() + " " + secondText.trim());
        } else {
            holder.messageTv.setText(str);
        }

        if (respList.getData().get(position).getTxnType() != null && respList.getData().get(position).getTxnSubType() != null) {
            holder.type.setText("Purchase");
        } else {
            holder.type.setText("ChargeBack");
        }
    }

    @Override
    public int getItemCount() {
        if (respList == null || respList.getData() == null) {
            return 0;
        }
        return respList.getData().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView createdTime, messageTv, type;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            createdTime = itemView.findViewById(R.id.purchaseTime);
            messageTv = itemView.findViewById(R.id.message_tv);
            type = itemView.findViewById(R.id.purchase_tv);

        }
    }
}
