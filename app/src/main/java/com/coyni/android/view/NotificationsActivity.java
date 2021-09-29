package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coyni.android.adapters.NotificationsAdapter;
import com.coyni.android.model.APIError;
import com.coyni.android.model.notification.Notifications;
import com.coyni.android.model.notification.NotificationsDataItems;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.NotificationsViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.coyni.android.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    NotificationsViewModel notificationsViewModel;
    MyApplication objMyApplication;
    ProgressDialog progressDialog;
    ImageView imgNotificationRead, imgClear;
    List<NotificationsDataItems> notificationsList;
    Boolean isRead = true, isDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notifications);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (objMyApplication.getListSelNotifications() != null) {
            objMyApplication.getListSelNotifications().clear();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(NotificationsActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(NotificationsActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(NotificationsActivity.this, this, false);
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            Utils.statusBar(NotificationsActivity.this);
            imgNotificationRead = (ImageView) findViewById(R.id.imgNotificationRead);
            imgClear = (ImageView) findViewById(R.id.imgClear);
            objMyApplication = (MyApplication) getApplicationContext();
            notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
            if (Utils.checkInternet(NotificationsActivity.this)) {
                progressDialog = new ProgressDialog(NotificationsActivity.this, R.style.MyAlertDialogStyle);
                progressDialog.setIndeterminate(false);
                progressDialog.setMessage("Please wait...");
                progressDialog.getWindow().setGravity(Gravity.CENTER);
                progressDialog.show();
                notificationsViewModel.meNotifications();
            } else {
                Toast.makeText(NotificationsActivity.this, getString(R.string.internet), Toast.LENGTH_LONG).show();
            }
            imgNotificationRead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (!isRead) {
                            if (objMyApplication.getListSelNotifications() != null && objMyApplication.getListSelNotifications().size() > 0) {
                                List<Integer> list = new ArrayList<>();
                                for (int i = 0; i < objMyApplication.getListSelNotifications().size(); i++) {
                                    list.add(Integer.parseInt(objMyApplication.getListSelNotifications().get(i)));
                                }
                                objMyApplication.getListSelNotifications().clear();
                                if (list != null && list.size() > 0) {
//                                    progressDialog = new ProgressDialog(NotificationsActivity.this, R.style.MyAlertDialogStyle);
//                                    progressDialog.setIndeterminate(false);
//                                    progressDialog.setMessage("Please wait...");
//                                    progressDialog.getWindow().setGravity(Gravity.CENTER);
//                                    progressDialog.show();
                                    notificationsViewModel.notificationsMarkRead(list);
                                }
                            }
                        } else {
                            Toast.makeText(NotificationsActivity.this, "Notifications are already in Read state.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            imgClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (objMyApplication.getListSelNotifications() != null && objMyApplication.getListSelNotifications().size() > 0) {
                            List<Integer> list = new ArrayList<>();
                            for (int i = 0; i < objMyApplication.getListSelNotifications().size(); i++) {
                                list.add(Integer.parseInt(objMyApplication.getListSelNotifications().get(i)));
                            }
                            objMyApplication.getListSelNotifications().clear();
                            if (list != null && list.size() > 0) {
                                isDelete = true;
//                                progressDialog = new ProgressDialog(NotificationsActivity.this, R.style.MyAlertDialogStyle);
//                                progressDialog.setIndeterminate(false);
//                                progressDialog.setMessage("Please wait...");
//                                progressDialog.getWindow().setGravity(Gravity.CENTER);
//                                progressDialog.show();
                                notificationsViewModel.notificationsMarkClear(list);
                            }
                        } else {
                            Context context = new ContextThemeWrapper(NotificationsActivity.this, R.style.Theme_QuickCard);
                            new MaterialAlertDialogBuilder(context)
                                    .setTitle(R.string.app_name)
                                    .setMessage("Do you want to delete all your notifications.")
                                    .setNegativeButton("Cancel", (dialog1, which) -> {
                                        dialog1.dismiss();
                                    })
                                    .setPositiveButton("OK", (dialog, which) -> {
                                                isDelete = true;
                                                progressDialog = new ProgressDialog(NotificationsActivity.this, R.style.MyAlertDialogStyle);
                                                progressDialog.setIndeterminate(false);
                                                progressDialog.setMessage("Please wait...");
                                                progressDialog.getWindow().setGravity(Gravity.CENTER);
                                                progressDialog.show();
                                                notificationsViewModel.notificationsClearAll();
                                            }
                                    ).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        notificationsViewModel.getNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
                progressDialog.dismiss();
                if (notifications != null && notifications.getStatus().toUpperCase().equals("SUCCESS")) {
                    bindNotifications(notifications.getData().getItems());
                    if (notifications.getData().getItems() != null && notifications.getData().getItems().size() > 0) {
                        for (int i = 0; i < notifications.getData().getItems().size(); i++) {
                            if (!notifications.getData().getItems().get(i).getRead()) {
                                objMyApplication.setNotiAvailable(true);
                                break;
                            } else {
                                objMyApplication.setNotiAvailable(false);
                            }
                        }
                    }
                }
            }
        });

        notificationsViewModel.getNotiMarkReadMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                //progressDialog.dismiss();
                if (apiError != null) {
                    if (apiError.getStatus().toUpperCase().equals("SUCCESS")) {
//                        progressDialog = new ProgressDialog(NotificationsActivity.this, R.style.MyAlertDialogStyle);
//                        progressDialog.setIndeterminate(false);
//                        progressDialog.setMessage("Please wait...");
//                        progressDialog.getWindow().setGravity(Gravity.CENTER);
//                        progressDialog.show();
                        notificationsViewModel.meNotifications();
                    }
                    if (isDelete) {
                        isDelete = false;
                        Utils.displayCloseAlert(apiError.getData().toString(), NotificationsActivity.this);
                    }
                }
                enableOptions(false);
            }
        });

        notificationsViewModel.getApiErrorMutableLiveData().observe(this, new Observer<APIError>() {
            @Override
            public void onChanged(APIError apiError) {
                //progressDialog.dismiss();
                if (apiError != null) {
                    if (!apiError.getError().getErrorDescription().equals("")) {
                        if (apiError.getError().getErrorDescription().toLowerCase().contains("expire") || apiError.getError().getErrorDescription().toLowerCase().contains("invalid token")) {
                            objMyApplication.displayAlert(NotificationsActivity.this, getString(R.string.session));
                        } else {
                            Utils.displayAlert(apiError.getError().getErrorDescription(), NotificationsActivity.this);
                        }
                    } else {
                        Utils.displayAlert(apiError.getError().getFieldErrors().get(0), NotificationsActivity.this);
                    }
                }
            }
        });
    }

    private void bindNotifications(List<NotificationsDataItems> listNotifications) {
        NotificationsAdapter notificationsAdapter;
        RecyclerView rvNotifications;
        TextView tvNoData;
        try {
            rvNotifications = (RecyclerView) findViewById(R.id.rvNotifications);
            tvNoData = (TextView) findViewById(R.id.tvNoData);
            if (listNotifications != null && listNotifications.size() > 0) {
                rvNotifications.setVisibility(View.VISIBLE);
                tvNoData.setVisibility(View.GONE);
                notificationsList = listNotifications;
                notificationsAdapter = new NotificationsAdapter(listNotifications, NotificationsActivity.this);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                rvNotifications.setLayoutManager(mLayoutManager);
                rvNotifications.setItemAnimator(new DefaultItemAnimator());
                rvNotifications.setAdapter(notificationsAdapter);
            } else {
                rvNotifications.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableOptions(Boolean value) {
        try {
            isRead = true;
            RelativeLayout layoutIcons = (RelativeLayout) findViewById(R.id.layoutIcons);
            if (value) {
                layoutIcons.setVisibility(View.VISIBLE);
            } else {
                layoutIcons.setVisibility(View.INVISIBLE);
            }
            if (objMyApplication.getListSelNotifications().size() > 0) {
                int id = -1;
                for (int i = 0; i < objMyApplication.getListSelNotifications().size(); i++) {
                    id = Integer.parseInt(objMyApplication.getListSelNotifications().get(i));
                    for (int j = 0; j < notificationsList.size(); j++) {
                        if (notificationsList.get(j).getId() == id && !notificationsList.get(j).getRead()) {
                            isRead = notificationsList.get(j).getRead();
                        }
                    }
                }
                if (isRead) {
                    imgNotificationRead.setImageResource(R.drawable.ic_noti_unread);
                } else {
                    imgNotificationRead.setImageResource(R.drawable.ic_noti_read);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void notificationRead(Boolean value, int Id) {
        try {
            if (!value) {
                List<Integer> list = new ArrayList<>();
                list.add(Id);
                if (list != null && list.size() > 0) {
//                    progressDialog = new ProgressDialog(NotificationsActivity.this, R.style.MyAlertDialogStyle);
//                    progressDialog.setIndeterminate(false);
//                    progressDialog.setMessage("Please wait...");
//                    progressDialog.getWindow().setGravity(Gravity.CENTER);
//                    progressDialog.show();
                    notificationsViewModel.notificationsMarkRead(list);
                }
            }
//            else {
//                Toast.makeText(NotificationsActivity.this, "Notification is already in Read state.", Toast.LENGTH_SHORT).show();
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}