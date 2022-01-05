package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AgreeListAdapter;
import com.greenbox.coyni.adapters.NotificationsAdapter;
import com.greenbox.coyni.model.notification.Notifications;
import com.greenbox.coyni.model.notification.NotificationsData;
import com.greenbox.coyni.model.notification.NotificationsDataItems;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.viewmodel.GiftCardsViewModel;
import com.greenbox.coyni.viewmodel.NotificationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    public NotificationsViewModel notificationsViewModel;
    public List<NotificationsDataItems> globalNotifications = new ArrayList<>();
    public List<NotificationsDataItems> globalSentNotifications = new ArrayList<>();
    public List<NotificationsDataItems> globalReceivedNotifications = new ArrayList<>();
    RecyclerView notificationsRV;
    LinearLayout notifBackbtn;
    TextView notificationsTV, requestsTV;
    public static NotificationsActivity notificationsActivity;
    MyApplication objMyApplication;
    String selectedTab = "NOTIFICATIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_notifications);

        initFields();
        initObservers();
    }

    public void initFields() {
        notificationsActivity = this;
        objMyApplication = (MyApplication) getApplicationContext();
        notificationsRV = findViewById(R.id.notificationsRV);
        notifBackbtn = findViewById(R.id.notifBackbtn);
        notificationsTV = findViewById(R.id.notificationsTV);
        requestsTV = findViewById(R.id.requestsTV);

        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        try {
            notificationsViewModel.getNotifications();
            notificationsViewModel.getSentNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        notificationsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab.equals("REQUESTS")) {
                    selectedTab = "NOTIFICATIONS";
                    notificationsTV.setTextColor(getResources().getColor(R.color.white));
                    notificationsTV.setBackgroundResource(R.drawable.bg_core_colorfill);
                    requestsTV.setBackgroundColor(getResources().getColor(R.color.white));
                    requestsTV.setTextColor(getResources().getColor(R.color.primary_black));

                    LinearLayoutManager nLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
                    NotificationsAdapter notificationsAdapter = new NotificationsAdapter(globalNotifications, NotificationsActivity.this);
                    notificationsRV.setLayoutManager(nLayoutManager);
                    notificationsRV.setItemAnimator(new DefaultItemAnimator());
                    notificationsRV.setAdapter(notificationsAdapter);
                }
            }
        });

        requestsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab.equals("NOTIFICATIONS")) {
                    selectedTab = "REQUESTS";
                    requestsTV.setTextColor(getResources().getColor(R.color.white));
                    requestsTV.setBackgroundResource(R.drawable.bg_core_colorfill);
                    notificationsTV.setBackgroundColor(getResources().getColor(R.color.white));
                    notificationsTV.setTextColor(getResources().getColor(R.color.primary_black));

                    List<NotificationsDataItems> payReqNotifications = new ArrayList<>();
                    payReqNotifications.addAll(globalReceivedNotifications);
                    payReqNotifications.addAll(globalSentNotifications);

                    LinearLayoutManager nLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
                    NotificationsAdapter notificationsAdapter = new NotificationsAdapter(payReqNotifications, NotificationsActivity.this);
                    notificationsRV.setLayoutManager(nLayoutManager);
                    notificationsRV.setItemAnimator(new DefaultItemAnimator());
                    notificationsRV.setAdapter(notificationsAdapter);
                }
            }
        });


    }

    public void initObservers() {

        try {
            notificationsViewModel.getNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
                @Override
                public void onChanged(Notifications notifications) {
//
                    if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
                        globalNotifications.clear();
                        globalNotifications = notifications.getData().getItems();

                        for (int i = 0; i < globalNotifications.size(); i++) {
                            globalNotifications.get(i).setType("Notification");
                            globalNotifications.get(i).setTimeAgo(objMyApplication.convertNotificationTime(globalNotifications.get(i).getCreatedAt()));
                        }
                        notificationsViewModel.getReceivedNotifications();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            notificationsViewModel.getReceivedNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
                @Override
                public void onChanged(Notifications notifications) {
                    if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
                        notificationsViewModel.getSentNotifications();
                        globalReceivedNotifications.clear();
                        globalReceivedNotifications = notifications.getData().getItems();
                        for (int i = 0; i < globalReceivedNotifications.size(); i++) {
                            globalReceivedNotifications.get(i).setType("Received");
                            globalReceivedNotifications.get(i).setTimeAgo(objMyApplication.convertNotificationTime(globalReceivedNotifications.get(i).getRequestedDate()));
                        }
                        globalNotifications.addAll(globalReceivedNotifications);
                        LinearLayoutManager nLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
                        NotificationsAdapter notificationsAdapter = new NotificationsAdapter(globalNotifications, NotificationsActivity.this);
                        notificationsRV.setLayoutManager(nLayoutManager);
                        notificationsRV.setItemAnimator(new DefaultItemAnimator());
                        notificationsRV.setAdapter(notificationsAdapter);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            notificationsViewModel.getSentNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
                @Override
                public void onChanged(Notifications notifications) {
                    if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
                        globalSentNotifications.addAll(notifications.getData().getItems());
                        for (int i = 0; i < globalSentNotifications.size(); i++) {
                            globalSentNotifications.get(i).setType("Sent");
                            globalSentNotifications.get(i).setTimeAgo(objMyApplication.convertNotificationTime(globalSentNotifications.get(i).getRequestedDate()));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}