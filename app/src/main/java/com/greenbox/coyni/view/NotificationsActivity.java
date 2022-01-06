package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.NotificationsAdapter;
import com.greenbox.coyni.model.notification.Notifications;
import com.greenbox.coyni.model.notification.NotificationsDataItems;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.viewmodel.NotificationsViewModel;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
                            globalNotifications.get(i).setTimeAgo(convertNotificationTime(globalNotifications.get(i).getCreatedAt(),
                                    i, "Notification"));
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
                            globalReceivedNotifications.get(i).setTimeAgo(convertNotificationTime(globalReceivedNotifications.get(i).getRequestedDate(), i,
                                    "Receive"));
                        }
                        globalNotifications.addAll(globalReceivedNotifications);

                        Collections.sort(globalNotifications, Comparator.comparing(NotificationsDataItems::getIsToday, Comparator.reverseOrder())
                                .thenComparing(NotificationsDataItems::getLongTime, Comparator.reverseOrder()));

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
                            globalSentNotifications.get(i).setTimeAgo(convertNotificationTime(globalSentNotifications.get(i).getRequestedDate(), i,
                                    "Sent"));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertNotificationTime(String date, int position, String type) {
        String strDate = "";
        String timeAgo = "";
        try {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                    .toFormatter()
                    .withZone(ZoneOffset.UTC);
            ZonedDateTime zonedTime = ZonedDateTime.parse(date, dtf);
            DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(objMyApplication.getStrPreference(), ZoneId.SHORT_IDS));
            strDate = zonedTime.format(DATE_TIME_FORMATTER);

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date past = format.parse(strDate);

            Date now = new Date();

            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            String nowString = formatter.format(now);

            DateTimeFormatter dtfNow = new DateTimeFormatterBuilder().appendPattern("EEE MMM dd HH:mm:ss zzzz yyyy")
                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                    .toFormatter()
                    .withZone(ZoneOffset.UTC);
            ZonedDateTime zonedTimeNow = ZonedDateTime.parse(nowString, dtfNow);
            DateTimeFormatter DATE_TIME_FORMATTER_NOW = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
            zonedTime = zonedTimeNow.withZoneSameInstant(ZoneId.of(objMyApplication.getStrPreference(), ZoneId.SHORT_IDS));
            nowString = zonedTime.format(DATE_TIME_FORMATTER_NOW);

            SimpleDateFormat formatNow = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            now = formatNow.parse(nowString);
            Log.e("now", now + "");

            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
            int weeks = (int) days / 7;
            int months = (int) weeks / 4;
            int years = (int) months / 4;

            if (seconds < 60) {
                timeAgo = seconds + "s ago";
            } else if (minutes < 60) {
//                System.out.println(minutes + " minutes ago");
                timeAgo = minutes + "m ago";
            } else if (hours < 24) {
//                System.out.println(hours + " hours ago");
                timeAgo = hours + "h ago";
            } else if (days < 7) {
//                System.out.println(days + " days ago");
                timeAgo = days + "d ago";
            } else if (weeks < 4) {
                timeAgo = weeks + "w ago";
//                System.out.println(days + " weeks ago");
            } else if (months < 12) {
                timeAgo = months + "M ago";
//                System.out.println(days + " weeks ago");
            } else {
                timeAgo = years + "y ago";
            }


            try {
                System.out.println(days + " days ago");
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (type.equals("Notification")) {
                globalNotifications.get(position).setLongTime(past.getTime());
                if (days > 0)
                    globalNotifications.get(position).setIsToday(0);
                else
                    globalNotifications.get(position).setIsToday(1);

            } else if (type.equals("Receive")) {
                globalReceivedNotifications.get(position).setLongTime(past.getTime());
                if (days > 0)
                    globalReceivedNotifications.get(position).setIsToday(0);
                else
                    globalReceivedNotifications.get(position).setIsToday(1);
            } else {
                globalSentNotifications.get(position).setLongTime(past.getTime());
                if (days > 0)
                    globalSentNotifications.get(position).setIsToday(0);
                else
                    globalSentNotifications.get(position).setIsToday(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return timeAgo;
    }

}