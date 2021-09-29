package com.coyni.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.model.notification.NotificationsDataItems;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.view.NotificationsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    List<NotificationsDataItems> listNotifications;
    Context mContext;
    MyApplication objContext;
    Boolean isLongPress = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvSubject, tvContent, tvNotiDate;
        public CardView cvNotification;
        public ImageView imgSelect, imgStatus;

        public MyViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvSubject = (TextView) view.findViewById(R.id.tvSubject);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            tvNotiDate = (TextView) view.findViewById(R.id.tvNotiDate);
            imgSelect = (ImageView) view.findViewById(R.id.imgSelect);
            imgStatus = (ImageView) view.findViewById(R.id.imgStatus);
            cvNotification = (CardView) view.findViewById(R.id.cvNotification);
        }
    }

    public NotificationsAdapter(List<NotificationsDataItems> list, Context context) {
        this.mContext = context;
        this.listNotifications = list;
        this.objContext = (MyApplication) context.getApplicationContext();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notificationlistitem, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            NotificationsDataItems objData = listNotifications.get(position);
            String strPrev = "", strCurr = "", strCurDate = "";
            int days1, days2;
            int days = Utils.numberOfDays(objData.getCreatedAt());
            holder.tvDate.setText(getDateString(days));
//            holder.tvNotiDate.setText(Utils.transactionDate(objData.getCreatedAt()).toUpperCase());
            holder.tvNotiDate.setText(objContext.transactionDate(objData.getCreatedAt()).toUpperCase());
            if (position != 0) {
                days1 = Utils.numberOfDays(listNotifications.get(position - 1).getCreatedAt());
                days2 = Utils.numberOfDays(objData.getCreatedAt());
                strPrev = getDateString(days1);
                strCurr = getDateString(days2);
                if (strPrev.equals(strCurr)) {
                    holder.tvDate.setVisibility(View.GONE);
                } else {
                    holder.tvDate.setVisibility(View.VISIBLE);
                }
            } else if (position == 0) {
                holder.tvDate.setVisibility(View.VISIBLE);
                SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                strCurDate = spf.format(Calendar.getInstance().getTime());
                if (Utils.convertDate(objData.getCreatedAt()).equals(Utils.convertDate(strCurDate))) {
                    holder.tvDate.setText("Today");
                }
            }
            holder.tvSubject.setText(objData.getMsgSubject());
            holder.tvContent.setText(objData.getMsgContent());
            if (objData.getRead()) {
                holder.imgStatus.setImageResource(R.drawable.ic_noti_read);
            } else {
                holder.imgStatus.setImageResource(R.drawable.ic_noti_unread);
            }
            if (objContext.getListSelNotifications().contains(String.valueOf(objData.getId()))) {
                holder.imgSelect.setVisibility(View.VISIBLE);
                holder.cvNotification.setRadius(Utils.convertPxtoDP(9));
                holder.cvNotification.setCardElevation(Utils.convertPxtoDP(6));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(Utils.convertPxtoDP(10), Utils.convertPxtoDP(10), Utils.convertPxtoDP(10), Utils.convertPxtoDP(10));
                holder.cvNotification.setLayoutParams(params);
                enableOptions();
            } else {
                holder.imgSelect.setVisibility(View.GONE);
                holder.cvNotification.setRadius(0);
                holder.cvNotification.setCardElevation(0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 0);
                holder.cvNotification.setLayoutParams(params);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (isLongPress && objContext.getListSelNotifications().size() > 0) {
                    String strId = String.valueOf(objData.getId());
//                    if (objContext.getListSelNotifications().contains(strId)) {
//                        objContext.getListSelNotifications().remove(strId);
//                    } else {
//                        objContext.getListSelNotifications().add(strId);
//                    }
                    if (!objContext.getListSelNotifications().contains(strId)) {
                        objContext.getListSelNotifications().clear();
                        objContext.getListSelNotifications().add(strId);
                        ((NotificationsActivity) mContext).notificationRead(objData.getRead(), objData.getId());
                    } else {
                        objContext.getListSelNotifications().clear();
                    }
                    enableOptions();
                    notifyDataSetChanged();
                }
//                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    try {
//                        String strId = String.valueOf(objData.getId());
//                        if (objContext.getListSelNotifications().contains(strId)) {
//                            objContext.getListSelNotifications().remove(strId);
//                        } else {
//                            objContext.getListSelNotifications().add(strId);
//                        }
//                        isLongPress = true;
//                        enableOptions();
//                        notifyDataSetChanged();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return listNotifications.size();
    }

    private void enableOptions() {
        try {
            if (objContext.getListSelNotifications().size() > 0) {
                ((NotificationsActivity) mContext).enableOptions(true);
            } else {
                ((NotificationsActivity) mContext).enableOptions(false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getDateString(int days) {
        String strDate = "";
        try {
            if (days == 1) {
                strDate = "Yesterday";
            } else if (days >= 2 && days <= 6) {
                strDate = "2 days ago";
            } else if (days >= 7 && days <= 14) {
                strDate = "7 days ago";
            } else {
                strDate = "Older";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }
}





