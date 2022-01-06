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
import com.greenbox.coyni.utils.swipelayout.RecyclerSwipeAdapter;
import com.greenbox.coyni.utils.swipelayout.SwipeLayout;

import java.util.List;

public class NotificationsAdapter extends RecyclerSwipeAdapter<NotificationsAdapter.MyViewHolder> {
    Context mContext;
    MyApplication objMyApplication;
    List<NotificationsDataItems> notifications;
//    boolean isToday = false, isPast = false;


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
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.deleteLL));
            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.readStatusLL));
            holder.tvNotifDate.setPadding(40, 30, 0, 0);

            if (notifications.get(position).getType().equals("Notification")) {

                holder.swipeLayout.setLeftSwipeEnabled(true);
                holder.swipeLayout.setRightSwipeEnabled(true);

                holder.fromRequesterLL.setVisibility(View.GONE);
                holder.meRequestLL.setVisibility(View.GONE);

                holder.messageTV.setVisibility(View.VISIBLE);

                holder.subject.setText(notifications.get(position).getMsgSubject());
                holder.messageTV.setText(notifications.get(position).getMsgContent());
                if (notifications.get(position).isRead()) {
                    holder.readStatusCV.setVisibility(View.GONE);
                    holder.readStatusTV.setText("UNREAD");
                } else {
                    holder.readStatusTV.setText("READ");
                    holder.readStatusCV.setVisibility(View.VISIBLE);
                }

                holder.readStatusLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("left", "left");
                    }
                });

                holder.deleteLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("delete", "delete");
                    }
                });
            } else if (notifications.get(position).getType().equals("Received")) {

                holder.swipeLayout.setLeftSwipeEnabled(false);
                holder.swipeLayout.setRightSwipeEnabled(false);

                holder.fromRequesterLL.setVisibility(View.VISIBLE);
                holder.subject.setText(notifications.get(position).getContent());
                holder.messageTV.setVisibility(View.GONE);
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
            } else {

                holder.swipeLayout.setLeftSwipeEnabled(false);
                holder.swipeLayout.setRightSwipeEnabled(false);

            }

            holder.timeTV.setText(notifications.get(position).getTimeAgo());

            if (position == 0) {
                if (notifications.get(position).getIsToday() == 0) {
                    holder.tvNotifDate.setVisibility(View.VISIBLE);
                    holder.tvNotifDate.setText("Past");

                } else if (notifications.get(position).getIsToday() == 1) {
                    holder.tvNotifDate.setVisibility(View.VISIBLE);
                    holder.tvNotifDate.setText("Today");
                } else {
                    holder.tvNotifDate.setVisibility(View.GONE);
                }
            } else {
                if (notifications.get(position - 1).getIsToday() == notifications.get(position).getIsToday()) {
                    holder.tvNotifDate.setVisibility(View.GONE);
                } else {
                    if (notifications.get(position).getIsToday() == 0) {
                        holder.tvNotifDate.setVisibility(View.VISIBLE);
                        holder.tvNotifDate.setText("Past");
                    } else if (notifications.get(position).getIsToday() == 1) {
                        holder.tvNotifDate.setVisibility(View.VISIBLE);
                        holder.tvNotifDate.setText("Today");
                    } else {
                        holder.tvNotifDate.setVisibility(View.GONE);
                    }
                }
            }
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
        TextView subject, timeTV, messageTV, tvNotifDate, readStatusTV;
        LinearLayout fromRequesterLL, meRequestLL, denyLL, payLL, remindLL, cancelLL, readStatusLL, deleteLL;
        SwipeLayout swipeLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            readStatusCV = itemView.findViewById(R.id.readStatusCV);
            subject = itemView.findViewById(R.id.subject);
            timeTV = itemView.findViewById(R.id.timeTV);
            messageTV = itemView.findViewById(R.id.messageTV);
            tvNotifDate = itemView.findViewById(R.id.tvNotifDate);

            fromRequesterLL = itemView.findViewById(R.id.fromRequesterLL);
            meRequestLL = itemView.findViewById(R.id.meRequestLL);
            denyLL = itemView.findViewById(R.id.denyLL);
            payLL = itemView.findViewById(R.id.payLL);
            cancelLL = itemView.findViewById(R.id.cancelLL);
            remindLL = itemView.findViewById(R.id.remindLL);

            swipeLayout = itemView.findViewById(R.id.swipeLayout);
            readStatusTV = itemView.findViewById(R.id.readStatusTV);
            readStatusLL = itemView.findViewById(R.id.readStatusLL);
            deleteLL = itemView.findViewById(R.id.deleteLL);

        }

    }

//    public void updateList(List<List<TransactionListPosted>> list) {
//        listedData = list;
//        notifyDataSetChanged();
//    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLL;
    }
}
