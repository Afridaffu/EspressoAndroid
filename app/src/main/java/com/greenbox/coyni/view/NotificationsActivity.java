package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AgreeListAdapter;
import com.greenbox.coyni.adapters.NotificationsAdapter;
import com.greenbox.coyni.model.notification.Notifications;
import com.greenbox.coyni.model.notification.NotificationsData;
import com.greenbox.coyni.model.notification.NotificationsDataItems;
import com.greenbox.coyni.utils.ExpandableHeightRecyclerView;
import com.greenbox.coyni.viewmodel.GiftCardsViewModel;
import com.greenbox.coyni.viewmodel.NotificationsViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    NotificationsViewModel notificationsViewModel;
    public List<NotificationsDataItems> globalNotifications = new ArrayList<>();
    public List<NotificationsDataItems> globalSentNotifications = new ArrayList<>();
    public List<NotificationsDataItems> globalReceivedNotifications = new ArrayList<>();

    RecyclerView notificationsRV;

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
        notificationsRV = findViewById(R.id.notificationsRV);

        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        notificationsViewModel.getNotifications();

    }

    public void initObservers() {

        notificationsViewModel.getNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
//                notificationsViewModel.getReceivedNotifications();
                if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
                    globalNotifications.clear();
                    globalNotifications = notifications.getData().getItems();

                    LinearLayoutManager nLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
                    NotificationsAdapter notificationsAdapter = new NotificationsAdapter(notifications.getData().getItems(), NotificationsActivity.this);
                    notificationsRV.setLayoutManager(nLayoutManager);
                    notificationsRV.setItemAnimator(new DefaultItemAnimator());
                    notificationsRV.setAdapter(notificationsAdapter);
//                    notificationsRV.setExpanded(true);
                }
            }
        });

        notificationsViewModel.getReceivedNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
//                notificationsViewModel.getSentNotifications();
                if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
                    globalReceivedNotifications.clear();
                    globalReceivedNotifications = notifications.getData().getItems();
                }
            }
        });

        notificationsViewModel.getSentNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
                if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
                    globalSentNotifications.clear();
                    globalSentNotifications = notifications.getData().getItems();
                }
            }
        });
    }
}