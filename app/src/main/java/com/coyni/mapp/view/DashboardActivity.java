package com.coyni.mapp.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coyni.mapp.R;
import com.coyni.mapp.adapters.LatestTxnAdapter;
import com.coyni.mapp.model.appupdate.AppUpdateResp;
import com.coyni.mapp.model.bank.SignOn;
import com.coyni.mapp.model.businesswallet.BusinessWalletResponse;
import com.coyni.mapp.model.businesswallet.WalletInfo;
import com.coyni.mapp.model.businesswallet.WalletRequest;
import com.coyni.mapp.model.businesswallet.WalletResponseData;
import com.coyni.mapp.model.featurecontrols.FeatureControlByUser;
import com.coyni.mapp.model.featurecontrols.FeatureControlGlobalResp;
import com.coyni.mapp.model.featurecontrols.FeatureControlRespByUser;
import com.coyni.mapp.model.featurecontrols.FeatureData;
import com.coyni.mapp.model.featurecontrols.PermissionResponseList;
import com.coyni.mapp.model.identity_verification.LatestTransactionsRequest;
import com.coyni.mapp.model.identity_verification.LatestTxnResponse;
import com.coyni.mapp.model.notification.Notifications;
import com.coyni.mapp.model.notification.NotificationsDataItems;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.preferences.Preferences;
import com.coyni.mapp.model.profile.Profile;
import com.coyni.mapp.model.profile.TrackerResponse;
import com.coyni.mapp.model.websocket.WebSocketUrlResponse;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.business.BusinessCreateAccountsActivity;
import com.coyni.mapp.viewmodel.BusinessDashboardViewModel;
import com.coyni.mapp.viewmodel.CustomerProfileViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.IdentityVerificationViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;
import com.coyni.mapp.viewmodel.NotificationsViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends BaseActivity {
    public static final int REQUEST_READ_CONTACTS = 79;
    LinearLayout layoutProfile, layoutCrypto, layoutCard, layoutMainMenu;
    LinearLayout scanQr, viewMoreLL, notificationsSmallLL, ll_identity_verification_failed;
    RelativeLayout notificationsLL;
    DashboardViewModel dashboardViewModel;

    BusinessDashboardViewModel businessDashboardViewModel;
    CustomerProfileViewModel customerProfileViewModel;
    IdentityVerificationViewModel identityVerificationViewModel;
    public NotificationsViewModel notificationsViewModel;
    private LoginViewModel loginViewModel;
    TextView tvUserName, tvUserNameSmall, tvUserInfoSmall, tvUserInfo, noTxnTV, tvBalance, countTV,
            welcomeCoyniTV, buyTokenWelcomeCoyniTV, contactUSTV, idVeriStatus, actionRequiredMsgTV,
            actionTV, tvUserStatusUpdate;
    MyApplication objMyApplication;
    Dialog dialog;
    RelativeLayout cvHeaderRL, cvSmallHeaderRL, statusCardsRL;
    NestedScrollView transactionsNSV;
    CardView getStartedCV, welcomeCoyniCV, underReviewCV, additionalActionCV, buyTokensCV, newUserGetStartedCV, cvPayRequest, countCV;
    ImageView imgProfileSmall, imgProfile;
    Long mLastClickTime = 0L, mLastClickTimeQA = 0L;
    RecyclerView txnRV;
    SwipeRefreshLayout latestTxnRefresh;
    String strName = "", strFirstUser = "", strFCMToken = "";
    ConstraintLayout cvProfileSmall, cvProfile;
    DatabaseHandler databaseHandler;
    int globalCount = 0;
    private DisplayImageUtility displayImageUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_dashboard);
            initialization();
            initObserver();

            if (objMyApplication.getCheckOutModel() != null && objMyApplication.getCheckOutModel().isCheckOutFlag()) {
                showProgressDialog("connecting...");
            }
            firebaseToken();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        try {
            objMyApplication.setStrRetrEmail("");
            objMyApplication.clearUserData();
            displayImageUtility.clearCache();
            Utils.setStrAuth("");
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        try {
            switch (requestCode) {
                case REQUEST_READ_CONTACTS:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        objMyApplication.setContactPermission(true);
                    } else {
                        // permission denied,Disable the
                        // functionality that depends on this permission.
                        objMyApplication.setContactPermission(false);
                    }
                    startActivity(new Intent(DashboardActivity.this, AddRecipientActivity.class));
                    break;
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onNotificationUpdate() {
        super.onNotificationUpdate();
        latestTxnRefresh.setRefreshing(true);
        fetchTransactions();
    }

    private void initialization() {
        try {
            cvHeaderRL = findViewById(R.id.cvHeaderRL);
            cvSmallHeaderRL = findViewById(R.id.cvSmallHeaderRL);
            getStartedCV = findViewById(R.id.getStartedCV);
            ll_identity_verification_failed = findViewById(R.id.ll_identity_verification_failed);
            transactionsNSV = findViewById(R.id.transactionsNSV);
            imgProfileSmall = findViewById(R.id.imgProfileSmall);
            imgProfile = findViewById(R.id.imgProfile);
            contactUSTV = findViewById(R.id.contactUSTV);
            idVeriStatus = findViewById(R.id.idVeriStatus);
            tvUserStatusUpdate = findViewById(R.id.tv_user_status_update);

            newUserGetStartedCV = findViewById(R.id.newUserGetStartedCV);

            welcomeCoyniCV = findViewById(R.id.welcomeCoyniCV);
            welcomeCoyniTV = findViewById(R.id.welcomeCoyniTV);
            underReviewCV = findViewById(R.id.underReviewCV);
            additionalActionCV = findViewById(R.id.additionalActionCV);
            actionRequiredMsgTV = findViewById(R.id.actionRequiredMsgTV);
            actionTV = findViewById(R.id.actionTV);
            buyTokensCV = findViewById(R.id.buyTokensCV);
            buyTokenWelcomeCoyniTV = findViewById(R.id.buyTokenWelcomeCoyniTV);
            cvPayRequest = findViewById(R.id.cvPayRequest);

            layoutMainMenu = findViewById(R.id.layoutMainMenu);
            layoutProfile = findViewById(R.id.layoutProfile);
            layoutCrypto = findViewById(R.id.layoutCrypto);
            layoutCard = findViewById(R.id.layoutCard);
            tvUserName = findViewById(R.id.tvUserName);
            tvUserNameSmall = findViewById(R.id.tvUserNameSmall);
            tvUserInfo = findViewById(R.id.tvUserInfo);
            tvUserInfoSmall = findViewById(R.id.tvUserInfoSmall);
            scanQr = findViewById(R.id.scanQrLL);
            txnRV = findViewById(R.id.txnRV);
            noTxnTV = findViewById(R.id.noTxnTV);
            tvBalance = findViewById(R.id.tvBalance);
            viewMoreLL = findViewById(R.id.viewMoreLL);
            statusCardsRL = findViewById(R.id.statusCardsRL);
            latestTxnRefresh = findViewById(R.id.latestTxnRefresh);
            cvProfileSmall = findViewById(R.id.cvProfileSmall);
            cvProfile = findViewById(R.id.cvProfile);
            notificationsSmallLL = findViewById(R.id.notificationsSmallLL);
            notificationsLL = findViewById(R.id.notificationsLL);
            countTV = findViewById(R.id.countTV);
            countCV = findViewById(R.id.countCV);

            idVeriStatus.setText(getString(R.string.declined_text));
            objMyApplication = (MyApplication) getApplicationContext();
            displayImageUtility = DisplayImageUtility.getInstance(this);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
            notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            databaseHandler = DatabaseHandler.getInstance(DashboardActivity.this);
            SetDB();
            if (strFirstUser == null || strFirstUser.equals("")) {
                saveFirstUser();
            }

            layoutMainMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                            showQuickAction(DashboardActivity.this);
                        } else if (objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus()) ||
                                objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus()) ||
                                objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
                            Utils.showCustomToast(DashboardActivity.this, getString(R.string.complete_idve), 0, "");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            layoutProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Intent i = new Intent(DashboardActivity.this, CustomerProfileActivity.class);
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            layoutCrypto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    cryptoAssets();
//                    startActivity(new Intent(DashboardActivity.this, TransactionListActivity.class));
                }
            });

            layoutCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    issueCards();
                }
            });

            scanQr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    startActivity(new Intent(DashboardActivity.this, ScanActivity.class));
                }
            });

            newUserGetStartedCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(DashboardActivity.this, BindingLayoutActivity.class);
                    i.putExtra("screen", "profileGetStarted");
                    startActivity(i);
                }
            });

            welcomeCoyniCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(DashboardActivity.this, PaymentMethodsActivity.class)
                            .putExtra("screen", "dashboard"));
                }
            });

            underReviewCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                }
            });

            additionalActionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (objMyApplication.getMyProfile().getData().getStatusChangeReasonType().equals("ACTION_REQUIRED_ADDITIONAL_INFO"))
                        startActivity(new Intent(DashboardActivity.this, AdditionalActionUploadActivity.class));
                    else if (objMyApplication.getMyProfile().getData().getStatusChangeReasonType().equals("ACTION_REQUIRED_FULL_SSN"))
                        startActivity(new Intent(DashboardActivity.this, IdVeAdditionalActionActivity.class)
                                .putExtra("from", "DASHBOARD"));


                }
            });

            buyTokensCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(DashboardActivity.this, BuyTokenPaymentMethodsActivity.class);
                    i.putExtra("screen", "dashboard");
                    startActivity(i);
                }
            });

            cvPayRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    Intent i = new Intent(DashboardActivity.this, AddRecipientActivity.class);
//                    startActivity(i);
                        if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                            return;
                        }
                        mLastClickTimeQA = SystemClock.elapsedRealtime();
                        requestPermission();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            viewMoreLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(DashboardActivity.this, TransactionListActivity.class));
                }
            });

            latestTxnRefresh.setColorSchemeColors(getResources().getColor(R.color.primary_green));

            latestTxnRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    try {
//                        if (objMyApplication.getTrackerResponse().getData().isPersonIdentified()
//                                && objMyApplication.getTrackerResponse().getData().isPaymentModeAdded()) {
                        fetchTransactions();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tvUserName.getText().toString().contains("...")) {
                        if (strName.length() == 21 || strName.length() > 21) {
                            tvUserName.setText(getString(R.string.hi_text) + strName.substring(0, 20));
                        } else {
                            tvUserName.setText(getString(R.string.hi_text) + strName);
                        }
                    } else {
                        if (strName.length() == 21) {
                            tvUserName.setText(getString(R.string.hi_text) + strName.substring(0, 20) + "...");
                        } else if (strName.length() > 21) {
                            tvUserName.setText(getString(R.string.hi_text) + strName.substring(0, 21) + "...");
                        } else {
                            tvUserName.setText(getString(R.string.hi_text) + strName);
                        }
                    }
                }
            });

            tvUserNameSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tvUserNameSmall.getText().toString().contains("...")) {
                        if (strName.length() == 21 || strName.length() > 21) {
                            tvUserNameSmall.setText(getString(R.string.hi_text) + strName.substring(0, 20));
                        } else {
                            tvUserNameSmall.setText(getString(R.string.hi_text) + strName);
                        }
                    } else {
                        if (strName.length() == 21) {
                            tvUserNameSmall.setText(getString(R.string.hi_text) + strName.substring(0, 20) + "...");
                        } else if (strName.length() > 22) {
                            tvUserNameSmall.setText(getString(R.string.hi_text) + strName.substring(0, 22) + "...");
                        } else {
                            tvUserNameSmall.setText(getString(R.string.hi_text) + strName);
                        }
                    }
                }
            });

            cvProfileSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardActivity.this, BusinessCreateAccountsActivity.class));
                }
            });

            cvProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardActivity.this, BusinessCreateAccountsActivity.class));
                }
            });

            notificationsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardActivity.this, NotificationsActivity.class));
                }
            });

            notificationsSmallLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardActivity.this, NotificationsActivity.class));
                }
            });

            contactUSTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(Utils.mondayURL));
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fetchTransactions() {
        if (objMyApplication.getTrackerResponse().getData().isPersonIdentified()) {
            LatestTransactionsRequest request = new LatestTransactionsRequest();
            request.setTransactionType(getDefaultTransactionTypes());
            dashboardViewModel.getLatestTxns(request);
            WalletRequest walletRequest = new WalletRequest();
            walletRequest.setWalletType(Utils.TOKEN);
            businessDashboardViewModel.meMerchantWallet(walletRequest);
            notificationsViewModel.getNotifications();
            transactionsNSV.smoothScrollTo(0, 0);
        } else {
            latestTxnRefresh.setRefreshing(false);
        }
    }

    private void initObserver() {
        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile != null) {
                    objMyApplication.setMyProfile(profile);
                    identityVerificationViewModel.getStatusTracker();
                    objMyApplication.setStrUserName(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                    strName = Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName());
                    if (objMyApplication.getStrUserName().length() > 20) {
                        tvUserName.setText(getString(R.string.hi_text) + strName.substring(0, 20));
                        tvUserNameSmall.setText(getString(R.string.hi_text) + strName.substring(0, 20));
                    } else {
                        tvUserName.setText(getString(R.string.hi_text) + strName);
                        tvUserNameSmall.setText(getString(R.string.hi_text) + strName);
                    }
                    Utils.setUserEmail(DashboardActivity.this, profile.getData().getEmail());
                }
                new FetchData(DashboardActivity.this).execute();
            }
        });

        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(this, new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                if (businessWalletResponse != null) {
                    objMyApplication.setWalletResponseData(businessWalletResponse.getData());
                    getBalance(businessWalletResponse.getData());
                }
            }
        });

        identityVerificationViewModel.getGetStatusTracker().observe(this, new Observer<TrackerResponse>() {
            @Override
            public void onChanged(TrackerResponse trackerResponse) {

                if (trackerResponse != null && trackerResponse.getStatus().equalsIgnoreCase("success")) {
                    objMyApplication.setTrackerResponse(trackerResponse);

                    if (trackerResponse.getData().isPersonIdentified()) {
                        cvHeaderRL.setVisibility(View.VISIBLE);
                        cvSmallHeaderRL.setVisibility(View.GONE);
                        getStartedCV.setVisibility(View.GONE);
                        transactionsNSV.setVisibility(View.VISIBLE);

                        if (trackerResponse.getData().isPaymentModeAdded()) {
                            welcomeCoyniCV.setVisibility(View.GONE);
                            noTxnTV.setVisibility(View.GONE);
//                            dashboardViewModel.getLatestTxns();
                        } else {
                            welcomeCoyniCV.setVisibility(View.VISIBLE);
                            txnRV.setVisibility(View.GONE);
                            noTxnTV.setVisibility(View.VISIBLE);
                        }

                        if (underReviewCV.getVisibility() != View.VISIBLE)
                            underReviewCV.setVisibility(View.GONE);
                        additionalActionCV.setVisibility(View.GONE);
                        buyTokensCV.setVisibility(View.GONE);

                        LatestTransactionsRequest request = new LatestTransactionsRequest();
//                        if (objMyApplication.getMyProfile() != null && objMyApplication.getMyProfile().getData() != null) {
//                            request.setUserId(objMyApplication.getMyProfile().getData().getId());
//                        }
                        request.setTransactionType(getDefaultTransactionTypes());

                        dashboardViewModel.getLatestTxns(request);
                    } else {
                        if (objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus())) {
                            cvHeaderRL.setVisibility(View.GONE);
                            cvSmallHeaderRL.setVisibility(View.VISIBLE);
                            getStartedCV.setVisibility(View.VISIBLE);
                            transactionsNSV.setVisibility(View.GONE);
                            ll_identity_verification_failed.setVisibility(View.GONE);
                        } else if (objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DEACTIVE.getStatus()) ||
                                objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
//                            cvHeaderRL.setVisibility(View.GONE);
//                            cvSmallHeaderRL.setVisibility(View.VISIBLE);
//                            getStartedCV.setVisibility(View.GONE);
//                            transactionsNSV.setVisibility(View.GONE);
//                            ll_identity_verification_failed.setVisibility(View.VISIBLE);
                            startActivity(new Intent(DashboardActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "FAILED"));
                        } else if (objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
                            cvHeaderRL.setVisibility(View.VISIBLE);
                            cvSmallHeaderRL.setVisibility(View.GONE);
                            getStartedCV.setVisibility(View.GONE);
                            transactionsNSV.setVisibility(View.VISIBLE);

                            welcomeCoyniCV.setVisibility(View.GONE);
                            underReviewCV.setVisibility(View.VISIBLE);
                            additionalActionCV.setVisibility(View.GONE);
                            buyTokensCV.setVisibility(View.GONE);

                        } else if (objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
                            cvHeaderRL.setVisibility(View.VISIBLE);
                            cvSmallHeaderRL.setVisibility(View.GONE);
                            getStartedCV.setVisibility(View.GONE);
                            transactionsNSV.setVisibility(View.VISIBLE);

                            welcomeCoyniCV.setVisibility(View.GONE);
                            underReviewCV.setVisibility(View.GONE);
                            additionalActionCV.setVisibility(View.VISIBLE);
                            buyTokensCV.setVisibility(View.GONE);
                            if (objMyApplication.getMyProfile().getData().getStatusChangeReasonType().equals("ACTION_REQUIRED_ADDITIONAL_INFO")) {
                                actionRequiredMsgTV.setText(getString(R.string.action_required_msg_upload));
                                actionTV.setText("Upload Document");
                            } else if (objMyApplication.getMyProfile().getData().getStatusChangeReasonType().equals("ACTION_REQUIRED_FULL_SSN")) {
                                actionRequiredMsgTV.setText(getString(R.string.action_required_msg_SSN));
                                actionTV.setText("Continue");
                            }
                        }
                    }
                    bindImage();
                }

            }
        });

        dashboardViewModel.getGetUserLatestTxns().observe(this, new Observer<LatestTxnResponse>() {
            @Override
            public void onChanged(LatestTxnResponse latestTxnResponse) {
                try {
                    latestTxnRefresh.setRefreshing(false);
                    if (latestTxnResponse != null && latestTxnResponse.getStatus().equalsIgnoreCase("success")) {
                        cvHeaderRL.setVisibility(View.VISIBLE);
                        cvSmallHeaderRL.setVisibility(View.GONE);
                        getStartedCV.setVisibility(View.GONE);
                        transactionsNSV.setVisibility(View.VISIBLE);

                        welcomeCoyniCV.setVisibility(View.GONE);
                        if (underReviewCV.getVisibility() != View.VISIBLE)
                            underReviewCV.setVisibility(View.GONE);
                        additionalActionCV.setVisibility(View.GONE);

                        if (latestTxnResponse.getData().size() == 0) {
                            txnRV.setVisibility(View.GONE);
                            noTxnTV.setVisibility(View.VISIBLE);
//                            buyTokensCV.setVisibility(View.VISIBLE);
                        } else if (latestTxnResponse.getData().size() > 4) {
                            buyTokensCV.setVisibility(View.GONE);
                            txnRV.setVisibility(View.VISIBLE);
                            viewMoreLL.setVisibility(View.VISIBLE);
                            noTxnTV.setVisibility(View.GONE);
                            LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(latestTxnResponse, DashboardActivity.this);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(DashboardActivity.this);
                            txnRV.setLayoutManager(mLayoutManager);
                            txnRV.setItemAnimator(new DefaultItemAnimator());
                            txnRV.setAdapter(latestTxnAdapter);
                        } else if (latestTxnResponse.getData().size() <= 4) {
                            buyTokensCV.setVisibility(View.GONE);
                            txnRV.setVisibility(View.VISIBLE);
                            viewMoreLL.setVisibility(View.GONE);
                            noTxnTV.setVisibility(View.GONE);
                            LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(latestTxnResponse, DashboardActivity.this);
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(DashboardActivity.this);
                            txnRV.setLayoutManager(mLayoutManager);
                            txnRV.setItemAnimator(new DefaultItemAnimator());
                            txnRV.setAdapter(latestTxnAdapter);
                        }
                        if (objMyApplication.getTrackerResponse().getData().isPaymentModeAdded()) {
                            welcomeCoyniCV.setVisibility(View.GONE);
                            if (latestTxnResponse.getData().size() == 0) {
                                buyTokensCV.setVisibility(View.VISIBLE);
                            } else {
                                buyTokensCV.setVisibility(View.GONE);
                            }
                        } else {
                            welcomeCoyniCV.setVisibility(View.VISIBLE);
                            buyTokensCV.setVisibility(View.GONE);
                        }

                    }
                    showUnderReviewData();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                try {
                    if (signOn != null) {
                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {
                            objMyApplication.setSignOnData(signOn.getData());
                            objMyApplication.setStrSignOnError("");
                        } else {
                            objMyApplication.setSignOnData(null);
                            objMyApplication.setStrSignOnError(signOn.getError().getErrorDescription());
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dashboardViewModel.getPreferenceMutableLiveData().observe(this, new Observer<Preferences>() {
            @Override
            public void onChanged(Preferences preferences) {
                try {
                    if (preferences != null) {
                        if (preferences.getData().getTimeZone() == 0) {
                            objMyApplication.setTempTimezone(getString(R.string.PST));
                            objMyApplication.setTempTimezoneID(0);
                            objMyApplication.setStrPreference("PST");
                        } else if (preferences.getData().getTimeZone() == 1) {
                            objMyApplication.setTempTimezone(getString(R.string.MST));
                            objMyApplication.setTempTimezoneID(1);
                            objMyApplication.setStrPreference("America/Denver");
                        } else if (preferences.getData().getTimeZone() == 2) {
                            objMyApplication.setTempTimezone(getString(R.string.CST));
                            objMyApplication.setTempTimezoneID(2);
                            objMyApplication.setStrPreference("CST");
                        } else if (preferences.getData().getTimeZone() == 3) {
                            objMyApplication.setTempTimezone(getString(R.string.EST));
                            objMyApplication.setTempTimezoneID(3);
                            objMyApplication.setStrPreference("America/New_York");
                        } else if (preferences.getData().getTimeZone() == 4) {
                            objMyApplication.setTempTimezone(getString(R.string.HST));
                            objMyApplication.setTempTimezoneID(4);
                            objMyApplication.setStrPreference("HST");
                        } else if (preferences.getData().getTimeZone() == 5) {
                            objMyApplication.setTempTimezone(getString(R.string.AST));
                            objMyApplication.setTempTimezoneID(5);
                            objMyApplication.setStrPreference("AST");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        dashboardViewModel.getPaymentMethodsResponseMutableLiveData().observe(this, new Observer<PaymentMethodsResponse>() {
            @Override
            public void onChanged(PaymentMethodsResponse paymentMethodsResponse) {
                if (paymentMethodsResponse != null) {
                    PaymentMethodsResponse objResponse = objMyApplication.filterPaymentMethods(paymentMethodsResponse);
                    objMyApplication.setPaymentMethodsResponse(objResponse);
                }
            }
        });

        notificationsViewModel.getNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {
                if (notifications != null && notifications.getStatus().equalsIgnoreCase("success")) {
                    globalCount = 0;
                    for (int i = 0; i < notifications.getData().getItems().size(); i++) {
                        if (!notifications.getData().getItems().get(i).isRead()) {
                            globalCount++;
                        }
                    }
                    notificationsViewModel.getReceivedNotifications();
                    Log.e("count notif", globalCount + "");
                }
            }
        });

        notificationsViewModel.getReceivedNotificationsMutableLiveData().observe(this, new Observer<Notifications>() {
            @Override
            public void onChanged(Notifications notifications) {

                try {
                    if (notifications != null) {
                        if (notifications.getStatus().equalsIgnoreCase("success")) {
                            List<NotificationsDataItems> localData = notifications.getData().getItems();
                            for (int i = 0; i < localData.size(); i++) {
                                if (localData.get(i).getStatus().equalsIgnoreCase("Requested") ||
                                        localData.get(i).getStatus().equalsIgnoreCase("Remind")) {
                                    globalCount++;
                                }
                            }

                            if (globalCount > 0) {
                                countCV.setVisibility(View.VISIBLE);
                                countTV.setText(globalCount + "");
                            } else {
                                countCV.setVisibility(View.GONE);
                            }

                            Log.e("count total", globalCount + "");
                        } else {
                            if (globalCount > 0) {
                                countCV.setVisibility(View.VISIBLE);
                                countTV.setText(globalCount + "");
                            } else {
                                countCV.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.something_went_wrong), DashboardActivity.this, "", "");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dashboardViewModel.getFeatureControlRespByUserMutableLiveData().observe(this, new Observer<FeatureControlRespByUser>() {
            @Override
            public void onChanged(FeatureControlRespByUser featureControlRespByUser) {
                try {
                    FeatureData obj = new FeatureData();
                    if (featureControlRespByUser != null && featureControlRespByUser.getData() != null) {
                        obj = featureControlRespByUser.getData().getData();
                        if (obj != null && obj.getPermissionResponseList() != null && obj.getPermissionResponseList().size() > 0) {
                            featureControlsPermission(obj.getPermissionResponseList(), "user");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dashboardViewModel.getFeatureControlGlobalRespMutableLiveData().observe(this, new Observer<FeatureControlGlobalResp>() {
            @Override
            public void onChanged(FeatureControlGlobalResp featureControlGlobalResp) {
                try {
                    FeatureData obj = new FeatureData();
                    if (featureControlGlobalResp != null && featureControlGlobalResp.getData() != null) {
                        obj = featureControlGlobalResp.getData();
                        if (obj != null && obj.getPermissionResponseList() != null && obj.getPermissionResponseList().size() > 0) {
                            featureControlsPermission(obj.getPermissionResponseList(), "global");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        customerProfileViewModel.getWebSocketUrlResponseMutableLiveData().observe(this, new Observer<WebSocketUrlResponse>() {
            @Override
            public void onChanged(WebSocketUrlResponse webSocketUrlResponse) {
                if (webSocketUrlResponse != null) {
                    objMyApplication.setWebSocketUrlResponse(webSocketUrlResponse.getData());
                }
            }
        });

        dashboardViewModel.getAppUpdateRespMutableLiveData().observe(this, new Observer<AppUpdateResp>() {
            @Override
            public void onChanged(AppUpdateResp appUpdateResp) {
                try {
                    if (appUpdateResp == null){
                        return;
                    }
                    String version = getPackageManager().getPackageInfo(DashboardActivity.this.getPackageName(), 0).versionName;
                    int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                    int versionName = Integer.parseInt(version.replace(".", ""));
                    Context context = new ContextThemeWrapper(DashboardActivity.this, R.style.Theme_Coyni_Update);
                    if (versionName < Integer.parseInt(appUpdateResp.getData().getVersion().replace(".", ""))) {
                        showUpdateDialog(context);
                    } else if (versionName == Integer.parseInt(appUpdateResp.getData().getVersion().replace(".", ""))) {
                        if (versionCode < Integer.parseInt(appUpdateResp.getData().getBuildNum().replace(".", ""))) {
                            showUpdateDialog(context);
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void showUnderReviewData() {
        if (objMyApplication.getMyProfile() != null &&
                objMyApplication.getMyProfile().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
            cvHeaderRL.setVisibility(View.VISIBLE);
            cvSmallHeaderRL.setVisibility(View.GONE);
            getStartedCV.setVisibility(View.GONE);
            transactionsNSV.setVisibility(View.VISIBLE);

            welcomeCoyniCV.setVisibility(View.GONE);
            underReviewCV.setVisibility(View.VISIBLE);
            additionalActionCV.setVisibility(View.GONE);
            buyTokensCV.setVisibility(View.GONE);
            tvUserStatusUpdate.setText(R.string.customer_under_review);
            if (objMyApplication.getMyProfile().getData().getStatusChangeReasonType() != null
                    && objMyApplication.getMyProfile().getData().getStatusChangeReasonType().equalsIgnoreCase("UNDER_REVIEW_DISPUTE")) {
                tvUserStatusUpdate.setText(R.string.under_review_dispute);
            }
        }
    }

    private void SetDB() {
        strFirstUser = databaseHandler.getTableUserDetails();
    }

    private void saveFirstUser() {
        try {
            if (strFirstUser == null || strFirstUser.equals("")) {
                strFirstUser = objMyApplication.getStrEmail();
            }
            databaseHandler.clearTableUserDetails();
            databaseHandler.insertTableUserDetails(strFirstUser.toLowerCase());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void cryptoAssets() {
        try {
            LinearLayout layoutClose;
            dialog = new Dialog(DashboardActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_crypto_assets_cmng_sn);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.dimAmount = 0.7f;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            dialog.show();
            transactionsNSV.smoothScrollTo(0, 0);
            layoutClose = dialog.findViewById(R.id.layoutClose);
            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void issueCards() {
        try {
            LinearLayout layoutClose;
            dialog = new Dialog(DashboardActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_issuing_card_cmng_sn);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

//            WindowManager.LayoutParams lp = window.getAttributes();
//            lp.dimAmount = 0.7f;
//            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            dialog.show();
            transactionsNSV.smoothScrollTo(0, 0);
            layoutClose = dialog.findViewById(R.id.layoutClose);
            layoutClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class FetchData extends AsyncTask<Void, Void, Boolean> {

        public FetchData(Context context) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                customerProfileViewModel.meSignOn();
                dashboardViewModel.mePaymentMethods();
                WalletRequest walletRequest = new WalletRequest();
                walletRequest.setWalletType(Utils.TOKEN);
                businessDashboardViewModel.meMerchantWallet(walletRequest);
                notificationsViewModel.getNotifications();
                dashboardViewModel.getFeatureControlByUser(objMyApplication.getLoginUserId());
                dashboardViewModel.getFeatureControlGlobal(getString(R.string.portalType));
                if (!strFCMToken.equals("")) {
                    loginViewModel.initializeDevice(strFCMToken);
                }
                if (objMyApplication.getWebSocketUrlResponse() == null) {
                    customerProfileViewModel.webSocketUrl();
                }
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

    public void bindImage() {
        try {

//            if (objMyApplication.getMyProfile().getData().getAccountStatus().equals(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
//                cvHeaderRL.setVisibility(View.VISIBLE);
//                cvSmallHeaderRL.setVisibility(View.GONE);
//            } else {
//                cvHeaderRL.setVisibility(View.GONE);
//                cvSmallHeaderRL.setVisibility(View.VISIBLE);
//            }

            imgProfile.setVisibility(View.GONE);
            tvUserInfo.setVisibility(View.VISIBLE);

            imgProfileSmall.setVisibility(View.GONE);
            tvUserInfoSmall.setVisibility(View.VISIBLE);

            String imageString = objMyApplication.getMyProfile().getData().getImage();
            String imageTextNew = "";
            imageTextNew = imageTextNew + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                    objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
            tvUserInfo.setText(imageTextNew);
            tvUserInfoSmall.setText(imageTextNew);

            if (imageString != null && !imageString.trim().equals("")) {
                imgProfile.setVisibility(View.VISIBLE);
                tvUserInfo.setVisibility(View.GONE);
                imgProfileSmall.setVisibility(View.VISIBLE);
                tvUserInfoSmall.setVisibility(View.GONE);

                DisplayImageUtility utility = DisplayImageUtility.getInstance(getApplicationContext());
                utility.addImage(imageString, imgProfile, R.drawable.ic_profile_male_user);

                utility.addImage(imageString, imgProfileSmall, R.drawable.ic_profile_male_user);

            } else {
                imgProfile.setVisibility(View.GONE);
                tvUserInfo.setVisibility(View.VISIBLE);
                imgProfileSmall.setVisibility(View.GONE);
                tvUserInfoSmall.setVisibility(View.VISIBLE);

                String imageText = "";
                imageText = imageText + objMyApplication.getMyProfile().getData().getFirstName().substring(0, 1).toUpperCase() +
                        objMyApplication.getMyProfile().getData().getLastName().substring(0, 1).toUpperCase();
                tvUserInfo.setText(imageText);
                tvUserInfoSmall.setText(imageText);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.checkInternet(DashboardActivity.this)) {
//            progressDialog = Utils.showProgressDialog(this);
            dashboardViewModel.meProfile();
            dashboardViewModel.mePreferences(objMyApplication);
            transactionsNSV.smoothScrollTo(0, 0);
//            dashboardViewModel.getAppUpdate(getString(R.string.android_text));
            startWebSocket();
        } else {
            Utils.displayAlert(getString(R.string.internet), DashboardActivity.this, "", "");
        }
    }

    private void getBalance(WalletResponseData walletResponse) {
        try {
            String strAmount = "";
            List<WalletInfo> walletInfo = walletResponse.getWalletNames();
            if (walletInfo != null && walletInfo.size() > 0) {
                for (int i = 0; i < walletInfo.size(); i++) {
//                    if (walletInfo.get(i).getWalletType().equals(getString(R.string.currency))) {
//                    objMyApplication.setWalletResponseData(walletResponse);
//                    objMyApplication.setGbtWallet(walletInfo.get(i));
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo.get(i).getAvailabilityToUse()));
                    tvBalance.setText(strAmount);
                    objMyApplication.setGBTBalance(walletInfo.get(i).getAvailabilityToUse(), walletInfo.get(0).getWalletType());
//                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showQuickAction(final Context context) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_quick_action);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        LinearLayout scanLL = dialog.findViewById(R.id.scanLL);
        LinearLayout payRequestLL = dialog.findViewById(R.id.payRequestLL);
        LinearLayout buyTokenLL = dialog.findViewById(R.id.buyTokenLL);
        LinearLayout widthdrawLL = dialog.findViewById(R.id.widthdrawLL);

        scanLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                startActivity(new Intent(DashboardActivity.this, ScanActivity.class));
            }
        });

        payRequestLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                requestPermission();
//                startActivity(new Intent(DashboardActivity.this, AddRecipientActivity.class));
            }
        });

        buyTokenLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                dialog.dismiss();
                Intent i = new Intent(context, BuyTokenPaymentMethodsActivity.class);
                i.putExtra("screen", "dashboard");
                context.startActivity(i);
            }
        });

        widthdrawLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                dialog.dismiss();
                Intent i = new Intent(context, WithdrawPaymentMethodsActivity.class);
                context.startActivity(i);
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void requestPermission() {
        try {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
                // show UI part if you want here to show some rationale !!!
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                        REQUEST_READ_CONTACTS);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                        REQUEST_READ_CONTACTS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private ArrayList<Integer> getDefaultTransactionTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.payRequest);
        transactionType.add(Utils.withdraw);
        transactionType.add(Utils.buyTokens);
        transactionType.add(Utils.refund);
        transactionType.add(Utils.paidInvoice);
        transactionType.add(Utils.businessPayout);
        return transactionType;
    }

    private void featureControlsPermission(List<PermissionResponseList> permissionResponseList, String strFrom) {
        try {
            FeatureControlByUser featureControlByUser = new FeatureControlByUser();
            if (permissionResponseList != null && permissionResponseList.size() > 0) {
                for (int i = 0; i < permissionResponseList.size(); i++) {
                    switch (permissionResponseList.get(i).getFeatureName().toLowerCase()) {
                        case Utils.buyBankEnable:
                            featureControlByUser.setBuyBank(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.buyDebitEnable:
                            featureControlByUser.setBuyDebit(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.buyCreditEnable:
                            featureControlByUser.setBuyCredit(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.buySignetEnable:
                            featureControlByUser.setBuySignet(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.withBankEnable:
                            featureControlByUser.setWithBank(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.withInstantEnable:
                            featureControlByUser.setWithInstant(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.withGiftEnable:
                            featureControlByUser.setWithGift(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.withSignetEnable:
                            featureControlByUser.setWithSignet(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.allControlsEnable:
                            featureControlByUser.setAllControls(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.saleOrderEnable:
                            featureControlByUser.setSaleOrder(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.payBankEnable:
                            featureControlByUser.setPayBank(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.payCreditEnable:
                            featureControlByUser.setPayCredit(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.payDebitEnable:
                            featureControlByUser.setPayDebit(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.paySignetEnable:
                            featureControlByUser.setPaySignet(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.payEnable:
                            featureControlByUser.setPay(permissionResponseList.get(i).getPermission());
                            break;
                        case Utils.requestEnable:
                            featureControlByUser.setRequest(permissionResponseList.get(i).getPermission());
                            break;
                    }
                }
                if (strFrom.equals("user")) {
                    objMyApplication.setFeatureControlByUser(featureControlByUser);
                } else {
                    objMyApplication.setFeatureControlGlobal(featureControlByUser);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void firebaseToken() {
        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            strFCMToken = task.getResult();
                            Log.d("Token", "Token - " + strFCMToken);
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showUpdateDialog(Context context) {
        new MaterialAlertDialogBuilder(context)
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.appUpdate))
                .setCancelable(false)
                .setPositiveButton("Update", (dialog, which) -> {
                    dialog.dismiss();
                    Intent viewIntent =
                            new Intent("android.intent.action.VIEW",
                                    Uri.parse("market://details?id=com.coyni.mapp"));
                    viewIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(viewIntent);
                }).show();
    }

}