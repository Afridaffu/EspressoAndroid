package com.greenbox.coyni.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.greenbox.coyni.R;
import com.greenbox.coyni.model.notification.NotificationsDataItems;
import com.greenbox.coyni.model.notification.StatusRequest;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.userrequest.UserRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.swipelayout.RecyclerSwipeAdapter;
import com.greenbox.coyni.utils.swipelayout.SwipeLayout;
import com.greenbox.coyni.view.NotificationsActivity;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerSwipeAdapter<NotificationsAdapter.MyViewHolder> {
    Context mContext;
    MyApplication objMyApplication;
    List<NotificationsDataItems> notifications;
    Long mLastClickTime = 0L;

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

//            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
//            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.deleteLL));
//            holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.readStatusLL));
//            holder.tvNotifDate.setPadding(40, 30, 0, 0);
//            holder.swipeLayout.close();

            if (notifications.get(position).getType().equals("Notification")) {
                holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.deleteLL));
                holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.swipeLayout.findViewById(R.id.readStatusLL));
                holder.tvNotifDate.setPadding(40, 30, 0, 0);
                holder.swipeLayout.setLeftSwipeEnabled(true);
                holder.swipeLayout.setRightSwipeEnabled(true);
                holder.messageTV.setTextColor(mContext.getResources().getColor(R.color.dark_grey));

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
                        try {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            ((NotificationsActivity) mContext).progressDialog = Utils.showProgressDialog(mContext);
                            ((NotificationsActivity) mContext).selectedRow = position + "";

                            List<Integer> list = new ArrayList<>();
                            list.add(notifications.get(position).getId());

                            if (notifications.get(position).isRead()) {
                                ((NotificationsActivity) mContext).markUnReadAPICall(list);
                            } else {
                                ((NotificationsActivity) mContext).markReadAPICall(list);
                            }
                            mItemManger.closeAllItems();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.deleteLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            ((NotificationsActivity) mContext).progressDialog = Utils.showProgressDialog(mContext);
                            ((NotificationsActivity) mContext).selectedRow = position + "";

                            List<Integer> list = new ArrayList<>();
                            list.add(notifications.get(position).getId());

                            ((NotificationsActivity) mContext).deleteNotificationCall(list);
                            mItemManger.closeAllItems();
//                            holder.swipeLayout.close(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        mItemManger.closeAllExcept(layout);
                    }

                    @Override
                    public void onOpen(SwipeLayout layout) {

                    }

                    @Override
                    public void onStartClose(SwipeLayout layout) {
                    }

                    @Override
                    public void onClose(SwipeLayout layout) {
                    }

                    @Override
                    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    }

                    @Override
                    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    }
                });
            } else if (notifications.get(position).getType().equals("Received")) {

                Log.e("Status", notifications.get(position).getContent() + "  " + notifications.get(position).getStatus());
                holder.swipeLayout.setLeftSwipeEnabled(false);
                holder.swipeLayout.setRightSwipeEnabled(false);
                holder.messageTV.setVisibility(View.GONE);
                holder.messageTV.setTextColor(mContext.getResources().getColor(R.color.primary_green));

                if (notifications.get(position).getStatus().equalsIgnoreCase("Requested")) {
                    holder.meRequestLL.setVisibility(View.GONE);
                    holder.fromRequesterLL.setVisibility(View.VISIBLE);
                    holder.payLL.setVisibility(View.VISIBLE);
                    holder.denyLL.setVisibility(View.VISIBLE);
                    if(!notifications.get(position).getRemarks().trim().equals("")) {
                        holder.messageTV.setText(notifications.get(position).getRemarks());
                        holder.messageTV.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
                        holder.messageTV.setVisibility(View.VISIBLE);
                    }else
                        holder.messageTV.setVisibility(View.GONE);
                } else if (notifications.get(position).getStatus().equalsIgnoreCase("Cancelled")) {
                    Log.e("cancelled", "cancelled");
                    holder.meRequestLL.setVisibility(View.GONE);
                    holder.fromRequesterLL.setVisibility(View.GONE);
                    holder.messageTV.setVisibility(View.VISIBLE);
                    holder.messageTV.setText(notifications.get(position).getFromUser() + " cancelled this request");
                } else if (notifications.get(position).getStatus().equalsIgnoreCase("Remind")) {
                    holder.meRequestLL.setVisibility(View.GONE);
                    holder.fromRequesterLL.setVisibility(View.VISIBLE);
                    holder.messageTV.setVisibility(View.VISIBLE);
                    holder.messageTV.setText(notifications.get(position).getFromUser() + " sent you a reminder");
                } else if (notifications.get(position).getStatus().equalsIgnoreCase("Declined")
                        && notifications.get(position).getRequesterWalletId().equalsIgnoreCase(objMyApplication.getCurrentUserData().getTokenWalletResponse()
                        .getWalletNames().get(0).getWalletId())) {
                    holder.meRequestLL.setVisibility(View.GONE);
                    holder.fromRequesterLL.setVisibility(View.GONE);
                    holder.messageTV.setVisibility(View.VISIBLE);
                    holder.messageTV.setText(notifications.get(position).getFromUser() + " declined this request");
                } else if (notifications.get(position).getStatus().equalsIgnoreCase("Declined")
                        && !notifications.get(position).getRequesterWalletId().equalsIgnoreCase(objMyApplication.getCurrentUserData().getTokenWalletResponse()
                        .getWalletNames().get(0).getWalletId())) {
                    holder.meRequestLL.setVisibility(View.GONE);
                    holder.fromRequesterLL.setVisibility(View.GONE);
                    holder.messageTV.setVisibility(View.VISIBLE);
                    holder.messageTV.setText("You declined this request");
                }

                holder.subject.setText(notifications.get(position).getContent());
                holder.readStatusCV.setVisibility(View.GONE);

                holder.denyLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        ((NotificationsActivity) mContext).progressDialog = Utils.showProgressDialog(mContext);
                        Log.e("denyLL", "denyLL");
                        ((NotificationsActivity) mContext).selectedRow = position + "";

                        StatusRequest statusRequest = new StatusRequest();
                        statusRequest.setId(notifications.get(position).getId());
                        statusRequest.setStatus("Declined");
                        statusRequest.setRemarks("");
                        ((NotificationsActivity) mContext).updatedStatus = "Declined";

                        ((NotificationsActivity) mContext).userRequestStatusUpdateCall(statusRequest);
                    }
                });

                holder.payLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Log.e("payLL", "payLL");

                            if (notifications.get(position).getAmount() <= objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getExchangeAmount()) {
                                ((NotificationsActivity) mContext).selectedRow = position + "";

                                TransferPayRequest request = new TransferPayRequest();
                                request.setTokens(Utils.convertTwoDecimal(notifications.get(position).getAmount().toString()));
                                request.setRemarks(notifications.get(position).getRemarks());
                                request.setRecipientWalletId(notifications.get(position).getRequesterWalletId());

                                ((NotificationsActivity) mContext).showPayRequestPreview(notifications.get(position), request);
                            } else {
                                Utils.displayAlert("Amount exceeds available balance\nAvailable: " + objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getExchangeAmount() + " CYN", (Activity) mContext, "", "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.cancelLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Log.e("cancelLL", "cancelLL");
                            ((NotificationsActivity) mContext).progressDialog = Utils.showProgressDialog(mContext);
                            ((NotificationsActivity) mContext).selectedRow = position + "";

                            StatusRequest statusRequest = new StatusRequest();
                            statusRequest.setId(notifications.get(position).getId());
                            statusRequest.setStatus("Cancelled");
                            statusRequest.setRemarks("");
                            ((NotificationsActivity) mContext).updatedStatus = "Cancelled";

                            ((NotificationsActivity) mContext).userRequestStatusUpdateCall(statusRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.remindLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Log.e("remindLL", "remindLL");
                            ((NotificationsActivity) mContext).selectedRow = position + "";

                            StatusRequest statusRequest = new StatusRequest();
                            statusRequest.setId(notifications.get(position).getId());
                            statusRequest.setStatus("Remind");
                            statusRequest.setRemarks("");
                            ((NotificationsActivity) mContext).updatedStatus = "Remind";

                            ((NotificationsActivity) mContext).userRequestStatusUpdateCall(statusRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else {
                Log.e("Status Sent ", notifications.get(position).getContent() + "  " + notifications.get(position).getStatus());
                holder.swipeLayout.setLeftSwipeEnabled(false);
                holder.swipeLayout.setRightSwipeEnabled(false);

                holder.subject.setText(notifications.get(position).getContent());
                holder.messageTV.setTextColor(mContext.getResources().getColor(R.color.primary_green));
                holder.messageTV.setVisibility(View.GONE);
                holder.readStatusCV.setVisibility(View.GONE);

                holder.fromRequesterLL.setVisibility(View.GONE);

                if (notifications.get(position).getStatus().equalsIgnoreCase("Requested")) {
                    holder.meRequestLL.setVisibility(View.VISIBLE);
                    holder.fromRequesterLL.setVisibility(View.GONE);
                    holder.remindLL.setVisibility(View.VISIBLE);
                    holder.cancelLL.setVisibility(View.VISIBLE);
                    if(!notifications.get(position).getRemarks().trim().equals("")) {
                        holder.messageTV.setVisibility(View.VISIBLE);
                        holder.messageTV.setText(notifications.get(position).getRemarks());
                        holder.messageTV.setTextColor(mContext.getResources().getColor(R.color.dark_grey));
                    }else
                        holder.messageTV.setVisibility(View.GONE);
                } else if (notifications.get(position).getStatus().equalsIgnoreCase("Cancelled")) {
                    holder.meRequestLL.setVisibility(View.GONE);
                    holder.messageTV.setVisibility(View.VISIBLE);
                    holder.messageTV.setText("You cancelled this request");
                } else if (notifications.get(position).getStatus().equalsIgnoreCase("Remind")) {
                    holder.meRequestLL.setVisibility(View.VISIBLE);
                    holder.remindLL.setVisibility(View.INVISIBLE);
                    holder.remindLL.setClickable(false);
                    holder.messageTV.setVisibility(View.VISIBLE);
                    holder.messageTV.setText("You sent a reminder to " + notifications.get(position).getToUser());
                } else if (notifications.get(position).getStatus().equalsIgnoreCase("Declined")) {
                    holder.meRequestLL.setVisibility(View.GONE);
                    holder.fromRequesterLL.setVisibility(View.GONE);
                    holder.messageTV.setVisibility(View.VISIBLE);
                    holder.messageTV.setText("You declined this request");
                }

                holder.cancelLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Log.e("cancelLL", "cancelLL");
                            ((NotificationsActivity) mContext).selectedRow = position + "";

                            StatusRequest statusRequest = new StatusRequest();
                            statusRequest.setId(notifications.get(position).getId());
                            statusRequest.setStatus("Cancelled");
                            statusRequest.setRemarks("");
                            ((NotificationsActivity) mContext).updatedStatus = "Cancelled";

                            ((NotificationsActivity) mContext).userRequestStatusUpdateCall(statusRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                holder.remindLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            Log.e("remindLL", "remindLL");
                            ((NotificationsActivity) mContext).selectedRow = position + "";

                            StatusRequest statusRequest = new StatusRequest();
                            statusRequest.setId(notifications.get(position).getId());
                            statusRequest.setStatus("Remind");
                            statusRequest.setRemarks("");
                            ((NotificationsActivity) mContext).updatedStatus = "Remind";

                            ((NotificationsActivity) mContext).userRequestStatusUpdateCall(statusRequest);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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

        mItemManger.bind(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        Log.e("size", notifications.size() + "");
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView readStatusCV, cvNotification;
        TextView subject, timeTV, messageTV, tvNotifDate, readStatusTV;
        LinearLayout fromRequesterLL, meRequestLL, denyLL, payLL,
                remindLL, cancelLL, readStatusLL, deleteLL, notificationItemLL;
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

            notificationItemLL = itemView.findViewById(R.id.notificationItemLL);
            cvNotification = itemView.findViewById(R.id.cvNotification);

        }

    }

    public void updateList(List<NotificationsDataItems> list, int pos) {
        try {
            Log.e("list", list.size() + "  " + pos);
            notifications = list;
            notifyDataSetChanged();
//            notifyItemChanged(pos);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (notifications.size() > 0) {
//            ((NotificationsActivity) mContext).notificationsRV.setVisibility(View.VISIBLE);
//            ((NotificationsActivity) mContext).noDataTV.setVisibility(View.GONE);
//
//            LinearLayoutManager nLayoutManager = new LinearLayoutManager(mContext);
//            ((NotificationsActivity) mContext).notificationsAdapter = new NotificationsAdapter(notifications, mContext);
//            ((NotificationsActivity) mContext).notificationsRV.setLayoutManager(nLayoutManager);
//            ((NotificationsActivity) mContext).notificationsRV.setItemAnimator(new DefaultItemAnimator());
//            ((NotificationsActivity) mContext).notificationsRV.setAdapter(((NotificationsActivity) mContext).notificationsAdapter);
//
//            ((NotificationsActivity) mContext).notificationsRV.scrollToPosition(pos);
//        } else {
//            ((NotificationsActivity) mContext).notificationsRV.setVisibility(View.GONE);
//            ((NotificationsActivity) mContext).noDataTV.setVisibility(View.VISIBLE);
//            ((NotificationsActivity) mContext).noDataTV.setText("You have no notifications");
//        }
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayout;
    }

}
