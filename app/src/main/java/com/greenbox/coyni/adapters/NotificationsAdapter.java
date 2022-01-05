package com.greenbox.coyni.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.notification.NotificationsDataItems;
import com.greenbox.coyni.utils.MyApplication;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    Context mContext;
    MyApplication objMyApplication;
    List<NotificationsDataItems> notifications;


    public NotificationsAdapter(List<NotificationsDataItems> list, Context context) {
        this.notifications = list;
        this.mContext = context;
        this.objMyApplication = (MyApplication) context.getApplicationContext();
    }


    @NonNull
    @Override
    public NotificationsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_item, parent, false);

        return new NotificationsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.MyViewHolder holder, int position) {
        try {
            if (notifications.get(position).getType().equals("Notification")) {
                holder.receivedLL.setVisibility(View.GONE);
                holder.subject.setText(notifications.get(position).getMsgSubject());
                holder.messageTV.setText(notifications.get(position).getMsgContent());
                if (notifications.get(position).isRead()) {
                    holder.readStatusCV.setVisibility(View.GONE);
                } else {
                    holder.readStatusCV.setVisibility(View.VISIBLE);
                }
            } else if (notifications.get(position).getType().equals("Received")) {
                holder.receivedLL.setVisibility(View.VISIBLE);
                holder.subject.setText(notifications.get(position).getSubject());
                holder.messageTV.setText(notifications.get(position).getContent());
                holder.readStatusCV.setVisibility(View.GONE);

                holder.denyLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                holder.payLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }

            holder.timeTV.setText(notifications.get(position).getTimeAgo());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        Log.e("size", notifications.size() + "");
        return notifications.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView readStatusCV;
        TextView subject, timeTV, messageTV;
        LinearLayout receivedLL, denyLL, payLL;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            readStatusCV = itemView.findViewById(R.id.readStatusCV);
            subject = itemView.findViewById(R.id.subject);
            timeTV = itemView.findViewById(R.id.timeTV);
            messageTV = itemView.findViewById(R.id.messageTV);

            receivedLL = itemView.findViewById(R.id.receivedLL);
            denyLL = itemView.findViewById(R.id.denyLL);
            payLL = itemView.findViewById(R.id.payLL);

        }

    }

//    public void updateList(List<List<TransactionListPosted>> list) {
//        listedData = list;
//        notifyDataSetChanged();
//    }

}
