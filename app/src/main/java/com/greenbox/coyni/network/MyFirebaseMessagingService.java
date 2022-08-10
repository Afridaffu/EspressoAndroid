package com.greenbox.coyni.network;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static String REQUEST_ACCEPT = "111";
    PendingIntent resultPendingIntent;
    MyApplication objMyApplication;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            super.onMessageReceived(remoteMessage);
            objMyApplication = (MyApplication) getApplicationContext();
            if (remoteMessage.getData().size() > 0) {

            }
            if (remoteMessage.getNotification() != null) {
                Log.d("", "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(getBaseContext());
//
//            Intent intent = new Intent(REQUEST_ACCEPT);
//            intent.putExtra("screen", "Token");
//            broadcaster.sendBroadcast(intent);
                Intent dashboardIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                    dashboardIntent = new Intent(getApplicationContext(), BusinessDashboardActivity.class);
                }
                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, dashboardIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
            }
            if (Build.VERSION.SDK_INT >= 26) {
//            if (Utils.getStrAuth() == null || Utils.getStrAuth().equals("")) {
//                notificationDialog(remoteMessage);
//            } else {
//                Log.d("Token", "Notification Receive");
//            }

                notificationDialog(remoteMessage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationDialog(RemoteMessage remoteMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "coyni_01";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
            // Configure the notification channel.
            notificationChannel.setDescription("Test Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("coyni")
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentIntent(resultPendingIntent)
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentInfo("Information");
        notificationManager.notify(1, notificationBuilder.build());
    }

}

