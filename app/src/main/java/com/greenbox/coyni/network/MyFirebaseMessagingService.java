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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    PendingIntent resultPendingIntent;
    MyApplication objMyApplication;

    @SuppressLint("WrongConstant")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            super.onMessageReceived(remoteMessage);
            objMyApplication = (MyApplication) getApplicationContext();
            if (remoteMessage.getNotification() != null) {
                LogUtils.d("", "Message Notification Body: " + remoteMessage.getNotification().getBody());
                Intent dashboardIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                    dashboardIntent = new Intent(getApplicationContext(), BusinessDashboardActivity.class);
                }
                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, dashboardIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
                } else {
                    resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, dashboardIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                }
            }
            if (Utils.getStrAuth() == null || Utils.getStrAuth().equals("")) {
                notificationDialog(remoteMessage);
            } else {
                Log.d("Token", "Notification Receive");
                sendBroadcast(new Intent().setAction(Utils.NOTIFICATION_ACTION));
            }

//            notificationDialog(remoteMessage);
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
        notificationManager.notify(getNotificationId(), notificationBuilder.build());
    }

    private static int getNotificationId() {
        Random rnd = new Random();
        return 100 + rnd.nextInt(9000);
    }

}

