package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.NotificationsAdapter;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.notification.Notifications;
import com.greenbox.coyni.model.notification.NotificationsDataItems;
import com.greenbox.coyni.model.notification.StatusRequest;
import com.greenbox.coyni.model.notification.UnReadDelResponse;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.userrequest.UserRequest;
import com.greenbox.coyni.model.userrequest.UserRequestResponse;
import com.greenbox.coyni.model.withdraw.GiftCardWithDrawInfo;
import com.greenbox.coyni.model.withdraw.RecipientDetail;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.utils.CustomeTextView.AnimatedGradientTextView;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.NotificationsViewModel;
import com.greenbox.coyni.viewmodel.PayViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

import org.w3c.dom.Text;

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
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NotificationsActivity extends AppCompatActivity {
    public NotificationsViewModel notificationsViewModel;
    CoyniViewModel coyniViewModel;
    public List<NotificationsDataItems> globalNotifications = new ArrayList<>();
    public List<NotificationsDataItems> globalSentNotifications = new ArrayList<>();
    public List<NotificationsDataItems> globalReceivedNotifications = new ArrayList<>();
    public List<NotificationsDataItems> globalRequests = new ArrayList<>();
    public RecyclerView notificationsRV;
    LinearLayout notifBackbtn;
    DatabaseHandler dbHandler;
    public TextView notificationsTV, requestsTV, noDataTV;
    public static NotificationsActivity notificationsActivity;
    MyApplication objMyApplication;
    String selectedTab = "NOTIFICATIONS";
    public String selectedRow = "";
    public String updatedStatus = "";
    public ProgressDialog progressDialog;
    public NotificationsAdapter notificationsAdapter;

    SQLiteDatabase mydatabase;
    Cursor dsFacePin, dsTouchID;
    boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    int CODE_AUTHENTICATION_VERIFICATION = 251;
    public int FOR_RESULT = 235, previousItemPos = -1;
    Long mLastClickTime = 0L;
    boolean isAuthenticationCalled = false;
    public TransferPayRequest userPayRequest = new TransferPayRequest();
    PayViewModel payViewModel;
    StatusRequest statusRequest = new StatusRequest();
    CardView viewSideBarCV;

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
        dbHandler = DatabaseHandler.getInstance(NotificationsActivity.this);

        notifBackbtn = findViewById(R.id.notifBackbtn);
        notificationsTV = findViewById(R.id.notificationsTV);
        requestsTV = findViewById(R.id.requestsTV);
        noDataTV = findViewById(R.id.noDataTV);
        viewSideBarCV = findViewById(R.id.viewSideBarCV);

        try {
            if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT)
                viewSideBarCV.setVisibility(View.GONE);
            else if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT)
                viewSideBarCV.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }


        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        payViewModel = new ViewModelProvider(this).get(PayViewModel.class);
        coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
        try {
            progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
            progressDialog.setIndeterminate(false);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            notificationsViewModel.getNotifications();
            notificationsViewModel.getSentNotifications();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        SetFaceLock();
//        SetTouchId();
        setFaceLock();
        setTouchId();

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
                    notificationsTV.setTextColor(getResources().getColor(R.color.white));
                    notificationsTV.setBackgroundResource(R.drawable.bg_core_colorfill);
                    requestsTV.setBackgroundColor(getResources().getColor(R.color.white));
                    requestsTV.setTextColor(getResources().getColor(R.color.primary_black));
                    if (globalNotifications.size() > 0) {
                        notificationsRV.setVisibility(View.VISIBLE);
                        noDataTV.setVisibility(View.GONE);

                        LinearLayoutManager nLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
                        notificationsAdapter = new NotificationsAdapter(globalNotifications, NotificationsActivity.this);
                        notificationsRV.setLayoutManager(nLayoutManager);
                        notificationsRV.setItemAnimator(new DefaultItemAnimator());
                        notificationsRV.setAdapter(notificationsAdapter);
                    } else {
                        notificationsRV.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                        noDataTV.setText("You have no notifications");
                    }
                    selectedTab = "NOTIFICATIONS";
                }
            }
        });

        requestsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab.equals("NOTIFICATIONS")) {
                    requestsTV.setTextColor(getResources().getColor(R.color.white));
                    requestsTV.setBackgroundResource(R.drawable.bg_core_colorfill);
                    notificationsTV.setBackgroundColor(getResources().getColor(R.color.white));
                    notificationsTV.setTextColor(getResources().getColor(R.color.primary_black));

                    globalRequests.clear();
                    globalRequests = new ArrayList<>();
                    globalRequests.addAll(globalReceivedNotifications);
                    globalRequests.addAll(globalSentNotifications);

                    if (globalRequests.size() > 0) {
                        notificationsRV.setVisibility(View.VISIBLE);
                        noDataTV.setVisibility(View.GONE);
                        Collections.sort(globalRequests, Comparator.comparing(NotificationsDataItems::getIsToday, Comparator.reverseOrder())
                                .thenComparing(NotificationsDataItems::getLongTime, Comparator.reverseOrder()));

                        LinearLayoutManager nLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
                        notificationsAdapter = new NotificationsAdapter(globalRequests, NotificationsActivity.this);
                        notificationsRV.setLayoutManager(nLayoutManager);
                        notificationsRV.setItemAnimator(new DefaultItemAnimator());
                        notificationsRV.setAdapter(notificationsAdapter);
                    } else {
                        notificationsRV.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                        noDataTV.setText("You have no requests");
                    }
                    selectedTab = "REQUESTS";
                }
            }
        });

    }

    public void initObservers() {
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

        notificationsViewModel.getReceivedNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (notifications != null) {
                    if (notifications.getStatus().equalsIgnoreCase("success")) {
                        globalReceivedNotifications.clear();

                        List<NotificationsDataItems> localData = notifications.getData().getItems();
                        for (int i = 0; i < localData.size(); i++) {
                            if (localData.get(i).getStatus().equalsIgnoreCase("Requested") ||
                                    localData.get(i).getStatus().equalsIgnoreCase("Remind")) {
                                globalReceivedNotifications.add(localData.get(i));
                            }
                        }

                        for (int i = 0; i < globalReceivedNotifications.size(); i++) {
                            globalReceivedNotifications.get(i).setType("Received");
                            globalReceivedNotifications.get(i).setTimeAgo(convertNotificationTime(globalReceivedNotifications.get(i).getRequestedDate(), i,
                                    "Receive"));
                        }
                        globalNotifications.addAll(globalReceivedNotifications);

                        if (globalNotifications.size() > 0) {
                            notificationsRV.setVisibility(View.VISIBLE);
                            noDataTV.setVisibility(View.GONE);

                            Collections.sort(globalNotifications, Comparator.comparing(NotificationsDataItems::getIsToday, Comparator.reverseOrder())
                                    .thenComparing(NotificationsDataItems::getLongTime, Comparator.reverseOrder()));

                            LinearLayoutManager nLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
                            notificationsAdapter = new NotificationsAdapter(globalNotifications, NotificationsActivity.this);
                            notificationsRV.setLayoutManager(nLayoutManager);
                            notificationsRV.setItemAnimator(new DefaultItemAnimator());
                            notificationsRV.setAdapter(notificationsAdapter);
                        } else {
                            notificationsRV.setVisibility(View.GONE);
                            noDataTV.setVisibility(View.VISIBLE);
                            noDataTV.setText("You have no notifications");
                        }
                    } else {

                        if (!notifications.getError().getErrorDescription().equals("") && notifications.getError().getErrorDescription().equals("User request data not found.")) {
                            if (globalNotifications.size() > 0) {
                                notificationsRV.setVisibility(View.VISIBLE);
                                noDataTV.setVisibility(View.GONE);

                                Collections.sort(globalNotifications, Comparator.comparing(NotificationsDataItems::getIsToday, Comparator.reverseOrder())
                                        .thenComparing(NotificationsDataItems::getLongTime, Comparator.reverseOrder()));

                                LinearLayoutManager nLayoutManager = new LinearLayoutManager(NotificationsActivity.this);
                                notificationsAdapter = new NotificationsAdapter(globalNotifications, NotificationsActivity.this);
                                notificationsRV.setLayoutManager(nLayoutManager);
                                notificationsRV.setItemAnimator(new DefaultItemAnimator());
                                notificationsRV.setAdapter(notificationsAdapter);
                            } else {
                                notificationsRV.setVisibility(View.GONE);
                                noDataTV.setVisibility(View.VISIBLE);
                                noDataTV.setText("You have no notifications");
                            }
                        } else {
                            Utils.displayAlert(notifications.getError().getErrorDescription(), NotificationsActivity.this, "",
                                    notifications.getError().getFieldErrors().get(0));
                        }
                    }
                }

            }
        });

        notificationsViewModel.getSentNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
                if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
//                        globalSentNotifications.addAll(notifications.getData().getItems());

                    List<NotificationsDataItems> localData = notifications.getData().getItems();
                    for (int i = 0; i < localData.size(); i++) {
                        if (localData.get(i).getStatus().equalsIgnoreCase("Requested") ||
                                localData.get(i).getStatus().equalsIgnoreCase("Remind")) {
                            globalSentNotifications.add(localData.get(i));
                        }
                    }

                    for (int i = 0; i < globalSentNotifications.size(); i++) {
                        globalSentNotifications.get(i).setType("Sent");
                        globalSentNotifications.get(i).setTimeAgo(convertNotificationTime(globalSentNotifications.get(i).getRequestedDate(), i,
                                "Sent"));
                    }
                }
            }
        });

        notificationsViewModel.getMarkReadResponse().observe(this, new Observer<UnReadDelResponse>() {
            @Override
            public void onChanged(UnReadDelResponse unReadDelResponse) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (unReadDelResponse != null && unReadDelResponse.getStatus().equalsIgnoreCase("success")) {
                    globalNotifications.get(Integer.parseInt(selectedRow)).setRead(true);
                    if (globalNotifications.size() > 0) {
                        notificationsRV.setVisibility(View.VISIBLE);
                        noDataTV.setVisibility(View.GONE);
                        notificationsAdapter.updateList(globalNotifications, Integer.parseInt(selectedRow));
                    } else {
                        notificationsRV.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                    }
                } else {
                    Utils.displayAlert(unReadDelResponse.getError().getErrorDescription(), NotificationsActivity.this, "", unReadDelResponse.getError().getFieldErrors().get(0));
                }
            }
        });

        notificationsViewModel.getMarkUnReadResponse().observe(this, new Observer<UnReadDelResponse>() {
            @Override
            public void onChanged(UnReadDelResponse unReadDelResponse) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (unReadDelResponse != null && unReadDelResponse.getStatus().equalsIgnoreCase("success")) {
                    globalNotifications.get(Integer.parseInt(selectedRow)).setRead(false);
                    if (globalNotifications.size() > 0) {
                        notificationsRV.setVisibility(View.VISIBLE);
                        noDataTV.setVisibility(View.GONE);
                        notificationsAdapter.updateList(globalNotifications, Integer.parseInt(selectedRow));
                    } else {
                        notificationsRV.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                    }
                } else {
                    Utils.displayAlert(unReadDelResponse.getError().getErrorDescription(), NotificationsActivity.this, "", unReadDelResponse.getError().getFieldErrors().get(0));
                }
            }
        });

        notificationsViewModel.getDeleteNotifResponse().observe(this, new Observer<UnReadDelResponse>() {
            @Override
            public void onChanged(UnReadDelResponse unReadDelResponse) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (unReadDelResponse != null && unReadDelResponse.getStatus().equalsIgnoreCase("success")) {
                    globalNotifications.remove(Integer.parseInt(selectedRow));
                    if (globalNotifications.size() > 0) {
                        notificationsRV.setVisibility(View.VISIBLE);
                        noDataTV.setVisibility(View.GONE);
                        notificationsAdapter.updateList(globalNotifications, Integer.parseInt(selectedRow));
                    } else {
                        notificationsRV.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                    }
                } else {
                    Utils.displayAlert(unReadDelResponse.getError().getErrorDescription(), NotificationsActivity.this, "", unReadDelResponse.getError().getFieldErrors().get(0));
                }
            }
        });

        notificationsViewModel.getNotificationStatusUpdateResponse().observe(this, new Observer<UserRequestResponse>() {
            @Override
            public void onChanged(UserRequestResponse userRequestResponse) {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (userRequestResponse != null) {
                        if (userRequestResponse.getStatus().equalsIgnoreCase("success")) {
                            if (selectedTab.equals("NOTIFICATIONS")) {
                                if (!updatedStatus.equals("Completed")) {
                                    globalNotifications.get(Integer.parseInt(selectedRow)).setStatus(updatedStatus);
                                    if (globalNotifications.size() > 0) {
                                        notificationsRV.setVisibility(View.VISIBLE);
                                        noDataTV.setVisibility(View.GONE);
                                        notificationsAdapter.updateList(globalNotifications, Integer.parseInt(selectedRow));
                                    } else {
                                        notificationsRV.setVisibility(View.GONE);
                                        noDataTV.setVisibility(View.VISIBLE);
                                    }
                                    if (updatedStatus.equals("Declined")) {
                                        for (int i = 0; i < globalRequests.size(); i++) {
                                            if (globalRequests.get(i).getId() == globalNotifications.get(Integer.parseInt(selectedRow)).getId()) {
                                                globalRequests.get(i).setStatus(updatedStatus);
                                                break;
                                            }
                                        }
                                    }


                                }
                            } else {
                                if (!updatedStatus.equals("Completed")) {
                                    globalRequests.get(Integer.parseInt(selectedRow)).setStatus(updatedStatus);
                                    if (globalRequests.size() > 0) {
                                        notificationsRV.setVisibility(View.VISIBLE);
                                        noDataTV.setVisibility(View.GONE);
                                        notificationsAdapter.updateList(globalRequests, Integer.parseInt(selectedRow));
                                    } else {
                                        notificationsRV.setVisibility(View.GONE);
                                        noDataTV.setVisibility(View.VISIBLE);
                                    }
                                    if (updatedStatus.equals("Declined")) {
                                        for (int i = 0; i < globalNotifications.size(); i++) {
                                            if (globalNotifications.get(i).getId() == globalRequests.get(Integer.parseInt(selectedRow)).getId()) {
                                                globalNotifications.get(i).setStatus(updatedStatus);
                                                break;
                                            }
                                        }
                                    }

//                                    if (updatedStatus.equals("Cancelled")) {
//                                        for (int i = 0; i < globalNotifications.size(); i++) {
//                                            if (globalNotifications.get(i).getId() == globalRequests.get(Integer.parseInt(selectedRow)).getId()) {
//                                                globalNotifications.get(i).setStatus(updatedStatus);
//                                                break;
//                                            }
//                                        }
//                                    }

                                }
                            }
                        } else {
                            Utils.displayAlert(userRequestResponse.getError().getErrorDescription(), NotificationsActivity.this, "", userRequestResponse.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        payViewModel.getPayRequestResponseMutableLiveData().observe(this, new Observer<PayRequestResponse>() {
            @Override
            public void onChanged(PayRequestResponse payRequestResponse) {
                try {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    if (payRequestResponse != null) {
                        Utils.setStrToken("");
                        objMyApplication.setPayRequestResponse(payRequestResponse);
                        if (payRequestResponse.getStatus().toLowerCase().equals("success")) {
                            Utils.showCustomToast(NotificationsActivity.this, "Payment has successfully sent", R.drawable.ic_payment_successful, "");

//                            StatusRequest statusRequest = new StatusRequest();
                            if (selectedTab.equals("NOTIFICATIONS")) {
                                for (int i = 0; i < globalReceivedNotifications.size(); i++) {
                                    if (globalReceivedNotifications.get(i).getId() == globalNotifications.get(Integer.parseInt(selectedRow)).getId()) {
                                        globalReceivedNotifications.remove(i);
                                        break;
                                    }
                                }

                                for (int i = 0; i < globalRequests.size(); i++) {
                                    if (globalRequests.get(i).getId() == globalNotifications.get(Integer.parseInt(selectedRow)).getId()) {
                                        globalRequests.remove(i);
                                        break;
                                    }
                                }

                                statusRequest.setId(globalNotifications.get(Integer.parseInt(selectedRow)).getId());
                                globalNotifications.remove(Integer.parseInt(selectedRow));
                                if (globalNotifications.size() > 0) {
                                    notificationsRV.setVisibility(View.VISIBLE);
                                    noDataTV.setVisibility(View.GONE);
                                    notificationsAdapter.updateList(globalNotifications, Integer.parseInt(selectedRow));
                                } else {
                                    notificationsRV.setVisibility(View.GONE);
                                    noDataTV.setVisibility(View.VISIBLE);
                                }

                            } else {
                                for (int i = 0; i < globalNotifications.size(); i++) {
                                    if (globalNotifications.get(i).getId() == globalRequests.get(Integer.parseInt(selectedRow)).getId()) {
                                        globalNotifications.remove(i);
                                        break;
                                    }
                                }

                                for (int i = 0; i < globalReceivedNotifications.size(); i++) {
                                    if (globalReceivedNotifications.get(i).getId() == globalRequests.get(Integer.parseInt(selectedRow)).getId()) {
                                        globalReceivedNotifications.remove(i);
                                        break;
                                    }
                                }

                                statusRequest.setId(globalRequests.get(Integer.parseInt(selectedRow)).getId());
                                globalRequests.remove(Integer.parseInt(selectedRow));
                                if (globalRequests.size() > 0) {
                                    notificationsRV.setVisibility(View.VISIBLE);
                                    noDataTV.setVisibility(View.GONE);
                                    notificationsAdapter.updateList(globalRequests, Integer.parseInt(selectedRow));
                                } else {
                                    notificationsRV.setVisibility(View.GONE);
                                    noDataTV.setVisibility(View.VISIBLE);
                                }
                            }

                            statusRequest.setStatus("Completed");
                            statusRequest.setRemarks("");
                            updatedStatus = "Completed";
                            //userRequestStatusUpdateCall(statusRequest);
                            new FetchData(NotificationsActivity.this).execute();

                        } else {
                            Utils.displayAlert(payRequestResponse.getError().getErrorDescription(), NotificationsActivity.this, "", payRequestResponse.getError().getFieldErrors().get(0));
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.something_went_wrong), NotificationsActivity.this, "", "");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, new Observer<BiometricTokenResponse>() {
            @Override
            public void onChanged(BiometricTokenResponse biometricTokenResponse) {
                try {
                    if (biometricTokenResponse != null) {
                        if (biometricTokenResponse.getStatus().toLowerCase().equals("success")) {
                            if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
                                Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                            }
                            notificationPayCall();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
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
//            Log.e("now", now + "");

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


            if (type.equals("Notification")) {
                globalNotifications.get(position).setLongTime(past.getTime());
                globalNotifications.get(position).setIsToday(getIsToday(now.getTime(), past.getTime()));
            } else if (type.equals("Receive")) {
                globalReceivedNotifications.get(position).setLongTime(past.getTime());
                globalReceivedNotifications.get(position).setIsToday(getIsToday(now.getTime(), past.getTime()));
            } else {
                globalSentNotifications.get(position).setLongTime(past.getTime());
                globalSentNotifications.get(position).setIsToday(getIsToday(now.getTime(), past.getTime()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return timeAgo;
    }

    public void markReadAPICall(List<Integer> list) {
        try {
            notificationsViewModel.setReadNotification(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markUnReadAPICall(List<Integer> list) {
        try {
            notificationsViewModel.setUnReadNotification(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteNotificationCall(List<Integer> list) {
        try {
            notificationsViewModel.setDeleteNotification(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void userRequestStatusUpdateCall(StatusRequest request) {
        try {
            notificationsViewModel.notificationStatusUpdate(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPayRequestPreview(NotificationsDataItems dataItem, TransferPayRequest request) {
        try {
            Dialog prevDialog = new Dialog(NotificationsActivity.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.notification_pay_preview);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView payingToTV = prevDialog.findViewById(R.id.payingToTV);
            TextView myUserIDTV = prevDialog.findViewById(R.id.myUserIDTV);
            TextView messageTV = prevDialog.findViewById(R.id.messageTV);
            TextView requesterIDTV = prevDialog.findViewById(R.id.requesterIDTV);
            LinearLayout messageLL = prevDialog.findViewById(R.id.messageLL);
            LinearLayout requesterIDLL = prevDialog.findViewById(R.id.requesterIDLL);
            ImageView requesterIDIV = prevDialog.findViewById(R.id.requesterIDIV);
            TextView balanceTV = prevDialog.findViewById(R.id.balanceTV);

            TextView amountTV = prevDialog.findViewById(R.id.amountTV);
            AnimatedGradientTextView tv_lable = prevDialog.findViewById(R.id.tv_lable);
            TextView tv_lable_verify = prevDialog.findViewById(R.id.tv_lable_verify);

            CardView im_lock_ = prevDialog.findViewById(R.id.im_lock_);

            MotionLayout slideToConfirm = prevDialog.findViewById(R.id.slideToConfirm);

            amountTV.setText(Utils.convertTwoDecimal(dataItem.getAmount().toString()));
            payingToTV.setText("Paying " + dataItem.getFromUser());
            if (dataItem.getRequesterWalletId().length() > Integer.parseInt(getString(R.string.waddress_length))) {
                requesterIDTV.setText(dataItem.getRequesterWalletId().substring(0, Integer.parseInt(getString(R.string.waddress_length))) + "...");
            } else {
                requesterIDTV.setText(dataItem.getRequesterWalletId());
            }
            balanceTV.setText("Available: " + Utils.convertBigDecimalUSDC(String.valueOf(objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getExchangeAmount())) + " CYN");

            if (!dataItem.getRemarks().equals("")) {
                messageLL.setVisibility(View.VISIBLE);
                messageTV.setText("\""+dataItem.getRemarks()+"\"");
                myUserIDTV.setText(dataItem.getFromUser() + " Says:");
            } else {
                messageLL.setVisibility(View.GONE);
            }
            requesterIDLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.copyText(dataItem.getRequesterWalletId(), NotificationsActivity.this);
                }
            });

            isAuthenticationCalled = false;

            slideToConfirm.setTransitionListener(new MotionLayout.TransitionListener() {
                @Override
                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

                    if (progress > Utils.slidePercentage) {
                        im_lock_.setAlpha(1.0f);
                        motionLayout.setTransition(R.id.middle, R.id.end);
                        motionLayout.transitionToState(motionLayout.getEndState());
                        slideToConfirm.setInteractionEnabled(false);
//                        tv_lable.setText("Verifying");
//                        tv_lable.setVisibility(View.GONE);
//                        tv_lable_verify.setVisibility(View.VISIBLE);
                        userPayRequest = request;
                        if (!isAuthenticationCalled) {
                            tv_lable.setText("Verifying");
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(NotificationsActivity.this)) {
                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(NotificationsActivity.this)) || (isFaceLock))) {
                                    prevDialog.dismiss();
                                    isAuthenticationCalled = true;
                                    Utils.checkAuthentication(NotificationsActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    prevDialog.dismiss();
                                    isAuthenticationCalled = true;
                                    startActivityForResult(new Intent(NotificationsActivity.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("screen", "Notifications"), FOR_RESULT);
                                }
                            } else {
                                Log.e("elsee", "elssee");
                                prevDialog.dismiss();
                                isAuthenticationCalled = true;
                                startActivityForResult(new Intent(NotificationsActivity.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("screen", "Notifications"), FOR_RESULT);
                            }
                        }

                    }
                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                }

                @Override
                public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {
                }
            });

            Window window = prevDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            prevDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            prevDialog.setCanceledOnTouchOutside(true);
            prevDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                }
            });
            prevDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 251:
                if (resultCode == RESULT_OK) {
//                    notificationPayCall();
                    payRequestToken();
                } else if (resultCode == RESULT_CANCELED) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                    if (requestCode == CODE_AUTHENTICATION_VERIFICATION)
                        startActivityForResult(new Intent(NotificationsActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "Notifications"), FOR_RESULT);
                }
                break;
            case 235:
                if (resultCode == 235) {
                    notificationPayCall();
//                    payRequestToken();
                } else if (resultCode == RESULT_CANCELED) {
                    if (progressDialog != null)
                        progressDialog.dismiss();
                }
                break;
        }
    }

    private void payRequestToken() {
        try {
            progressDialog = Utils.showProgressDialog(NotificationsActivity.this);
            BiometricTokenRequest request = new BiometricTokenRequest();
            request.setDeviceId(Utils.getDeviceID());
            request.setMobileToken(objMyApplication.getStrMobileToken());
            request.setActionType(Utils.sendActionType);
            coyniViewModel.biometricToken(request);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void notificationPayCall() {
        try {
            if (Utils.checkInternet(NotificationsActivity.this)) {
                payViewModel.sendTokens(userPayRequest);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public void SetFaceLock() {
//        try {
//            isFaceLock = false;
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
//            dsFacePin.moveToFirst();
//            if (dsFacePin.getCount() > 0) {
//                String value = dsFacePin.getString(1);
//                if (value.equals("true")) {
//                    isFaceLock = true;
//                    objMyApplication.setLocalBiometric(true);
//                } else {
//                    isFaceLock = false;
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void SetTouchId() {
//        try {
//            isTouchId = false;
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
//            dsTouchID.moveToFirst();
//            if (dsTouchID.getCount() > 0) {
//                String value = dsTouchID.getString(1);
//                if (value.equals("true")) {
//                    isTouchId = true;
//                    objMyApplication.setLocalBiometric(true);
//                } else {
//                    isTouchId = false;
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }


    public void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                objMyApplication.setLocalBiometric(true);
            } else {
                isFaceLock = false;
                objMyApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTouchId() {
        try {
            isTouchId = false;
            String value = dbHandler.getThumbPinLock();
            if (value != null && value.equals("true")) {
                isTouchId = true;
                objMyApplication.setLocalBiometric(true);
            } else {
                isTouchId = false;
                objMyApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getIsToday(long now, long past) {
        int value = 0;
        try {
            Date dateNow = new Date(now);
            Date datePast = new Date(past);
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy");
            String nowStr = df2.format(dateNow);
            String pastStr = df2.format(datePast);
            Log.e("dates", nowStr + " " + pastStr);
            if (nowStr.equals(pastStr)) {
                value = 1;
            } else {
                value = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public class FetchData extends AsyncTask<Void, Void, Boolean> {

        public FetchData(Context context) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                userRequestStatusUpdateCall(statusRequest);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean list) {
            super.onPostExecute(list);

        }
    }

}