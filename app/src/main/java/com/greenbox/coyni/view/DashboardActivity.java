package com.greenbox.coyni.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.LatestTxnAdapter;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.model.wallet.WalletInfo;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout layoutProfile, layoutCrypto, layoutCard, layoutMainMenu;
    LinearLayout scanQr, viewMoreLL;
    DashboardViewModel dashboardViewModel;
    CustomerProfileViewModel customerProfileViewModel;
    IdentityVerificationViewModel identityVerificationViewModel;
    TextView tvUserName, tvUserNameSmall, tvUserInfoSmall, tvUserInfo, noTxnTV, tvBalance;
    MyApplication objMyApplication;
    Dialog dialog;
    ProgressDialog progressDialog;
    RelativeLayout cvHeaderRL, cvSmallHeaderRL, statusCardsRL;
    NestedScrollView transactionsNSV;
    CardView getStartedCV, welcomeCoyniCV, underReviewCV, additionalActionCV, buyTokensCV, newUserGetStartedCV;
    ImageView imgProfileSmall, imgProfile;
    Long mLastClickTime = 0L, mLastClickTimeQA = 0L;
    RecyclerView txnRV;
    SwipeRefreshLayout latestTxnRefresh;
    String strName = "";
    ConstraintLayout cvProfileSmall, cvProfile;

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
//            getStates();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            cvHeaderRL = findViewById(R.id.cvHeaderRL);
            cvSmallHeaderRL = findViewById(R.id.cvSmallHeaderRL);
            getStartedCV = findViewById(R.id.getStartedCV);
            transactionsNSV = findViewById(R.id.transactionsNSV);
            imgProfileSmall = findViewById(R.id.imgProfileSmall);
            imgProfile = findViewById(R.id.imgProfile);

            newUserGetStartedCV = findViewById(R.id.newUserGetStartedCV);

            welcomeCoyniCV = findViewById(R.id.welcomeCoyniCV);
            underReviewCV = findViewById(R.id.underReviewCV);
            additionalActionCV = findViewById(R.id.additionalActionCV);
            buyTokensCV = findViewById(R.id.buyTokensCV);

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

            objMyApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);

            layoutMainMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (objMyApplication.getTrackerResponse().getData().isPersonIdentified()) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        showQuickAction(DashboardActivity.this);
                    } else {
                        Utils.showCustomToast(DashboardActivity.this, "Please complete your Identity Verification process.", 0, "");
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
                    startActivity(new Intent(DashboardActivity.this, PayRequestScanActivity.class));
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
                    startActivity(new Intent(DashboardActivity.this, IdVeAdditionalActionActivity.class));
                }
            });

            buyTokensCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
//                    Intent i = new Intent(DashboardActivity.this, PaymentMethodsActivity.class);
//                    i.putExtra("screen", "quick_action");
//                    startActivity(i);
                    Intent i = new Intent(DashboardActivity.this, BuyTokenPaymentMethodsActivity.class);
                    i.putExtra("screen", "dashboard");
                    startActivity(i);
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
                    if (objMyApplication.getTrackerResponse().getData().isPersonIdentified()
                            && objMyApplication.getTrackerResponse().getData().isPaymentModeAdded()) {
                        dashboardViewModel.getLatestTxns();
                        dashboardViewModel.meWallet();
                        transactionsNSV.smoothScrollTo(0, 0);
                    } else {
                        latestTxnRefresh.setRefreshing(false);
                    }
                }
            });

            tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tvUserName.getText().toString().contains("...")) {
                        if (strName.length() == 21 || strName.length() > 21) {
                            tvUserName.setText(strName.substring(0, 20));
                        } else {
                            tvUserName.setText(strName);
                        }
                    } else {
                        if (strName.length() == 21) {
                            tvUserName.setText(strName.substring(0, 20) + "...");
                        } else if (strName.length() > 22) {
                            tvUserName.setText(strName.substring(0, 22) + "...");
                        } else {
                            tvUserName.setText(strName);
                        }
                    }
                }
            });

            tvUserNameSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tvUserNameSmall.getText().toString().contains("...")) {
                        if (strName.length() == 21 || strName.length() > 21) {
                            tvUserNameSmall.setText(strName.substring(0, 20));
                        } else {
                            tvUserNameSmall.setText(strName);
                        }
                    } else {
                        if (strName.length() == 21) {
                            tvUserNameSmall.setText(strName.substring(0, 20) + "...");
                        } else if (strName.length() > 22) {
                            tvUserNameSmall.setText(strName.substring(0, 22) + "...");
                        } else {
                            tvUserNameSmall.setText(strName);
                        }
                    }
                }
            });

            cvProfileSmall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardActivity.this, AccountsActivity.class));
                }
            });

            cvProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardActivity.this, AccountsActivity.class));
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {

        dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                if (profile != null) {
//                    progressDialog.dismiss();
                    objMyApplication.setMyProfile(profile);
                    identityVerificationViewModel.getStatusTracker();
                    objMyApplication.setStrUserName(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                    strName = Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName());
//                    if (strName != null && strName.length() > 21) {
//                        tvUserName.setText( strName.substring(0, 21) + "...");
//                        tvUserNameSmall.setText( strName.substring(0, 21) + "...");
//                    } else {
//                        tvUserName.setText( strName);
//                        tvUserNameSmall.setText( strName);
//                    }
                    if (objMyApplication.getStrUserName().length() > 20) {
                        tvUserName.setText(strName.substring(0, 20));
                        tvUserNameSmall.setText(strName.substring(0, 20));
                    } else {
                        tvUserName.setText(strName);
                        tvUserNameSmall.setText(strName);
                    }
                    bindImage();
                }
                new FetchData(DashboardActivity.this).execute();
            }
        });

        dashboardViewModel.getWalletResponseMutableLiveData().observe(this, new Observer<WalletResponse>() {
            @Override
            public void onChanged(WalletResponse walletResponse) {
                if (walletResponse != null) {
                    objMyApplication.setWalletResponse(walletResponse);
                    getBalance(walletResponse);
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
                            underReviewCV.setVisibility(View.GONE);
                            additionalActionCV.setVisibility(View.GONE);
                            buyTokensCV.setVisibility(View.GONE);

                            dashboardViewModel.getLatestTxns();

                        } else {
                            welcomeCoyniCV.setVisibility(View.VISIBLE);
                            underReviewCV.setVisibility(View.GONE);
                            additionalActionCV.setVisibility(View.GONE);
                            buyTokensCV.setVisibility(View.GONE);
                            txnRV.setVisibility(View.GONE);
                            noTxnTV.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (objMyApplication.getMyProfile().getData().getAccountStatus().equals("Unverified")) {
                            cvHeaderRL.setVisibility(View.GONE);
                            cvSmallHeaderRL.setVisibility(View.VISIBLE);
                            getStartedCV.setVisibility(View.VISIBLE);
                            transactionsNSV.setVisibility(View.GONE);
                        } else if (objMyApplication.getMyProfile().getData().getAccountStatus().equals("Under Review")) {
                            cvHeaderRL.setVisibility(View.VISIBLE);
                            cvSmallHeaderRL.setVisibility(View.GONE);
                            getStartedCV.setVisibility(View.GONE);
                            transactionsNSV.setVisibility(View.VISIBLE);

                            welcomeCoyniCV.setVisibility(View.GONE);
                            underReviewCV.setVisibility(View.VISIBLE);
                            additionalActionCV.setVisibility(View.GONE);
                            buyTokensCV.setVisibility(View.GONE);

                        } else if (objMyApplication.getMyProfile().getData().getAccountStatus().equals("Action Required")) {
                            cvHeaderRL.setVisibility(View.VISIBLE);
                            cvSmallHeaderRL.setVisibility(View.GONE);
                            getStartedCV.setVisibility(View.GONE);
                            transactionsNSV.setVisibility(View.VISIBLE);

                            welcomeCoyniCV.setVisibility(View.GONE);
                            underReviewCV.setVisibility(View.GONE);
                            additionalActionCV.setVisibility(View.VISIBLE);
                            buyTokensCV.setVisibility(View.GONE);

                        }


                    }
                }

            }
        });

        dashboardViewModel.getGetUserLatestTxns().observe(this, new Observer<LatestTxnResponse>() {
            @Override
            public void onChanged(LatestTxnResponse latestTxnResponse) {
                latestTxnRefresh.setRefreshing(false);
                if (latestTxnResponse != null && latestTxnResponse.getStatus().equalsIgnoreCase("success")) {
                    cvHeaderRL.setVisibility(View.VISIBLE);
                    cvSmallHeaderRL.setVisibility(View.GONE);
                    getStartedCV.setVisibility(View.GONE);
                    transactionsNSV.setVisibility(View.VISIBLE);

                    welcomeCoyniCV.setVisibility(View.GONE);
                    underReviewCV.setVisibility(View.GONE);
                    additionalActionCV.setVisibility(View.GONE);

                    if (latestTxnResponse.getData().size() == 0) {
                        txnRV.setVisibility(View.GONE);
                        noTxnTV.setVisibility(View.VISIBLE);
                        buyTokensCV.setVisibility(View.VISIBLE);

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
                    objMyApplication.setPaymentMethodsResponse(paymentMethodsResponse);
                }
            }
        });
    }

    private void cryptoAssets() {
        try {
            LinearLayout layoutClose;
            dialog = new Dialog(DashboardActivity.this, R.style.DialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_crypto_assets_cmng_sn);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            dialog.getWindow().setAttributes(lp);
            dialog.show();
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
            dialog = new Dialog(DashboardActivity.this, R.style.DialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_issuing_card_cmng_sn);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            dialog.getWindow().setAttributes(lp);
            dialog.show();
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
//                buyViewModel.meBanks();
//                notificationsViewModel.meNotifications();
//                payViewModel.getReceiveRequests();
                customerProfileViewModel.meSignOn();
                dashboardViewModel.mePaymentMethods();
                dashboardViewModel.meWallet();
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

                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(imgProfile);
                Glide.with(this)
                        .load(imageString)
                        .placeholder(R.drawable.ic_profile_male_user)
                        .into(imgProfileSmall);

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
            dashboardViewModel.mePreferences();
        } else {
            Utils.displayAlert(getString(R.string.internet), DashboardActivity.this, "", "");
        }
    }

    private void getBalance(WalletResponse walletResponse) {
        try {
            String strAmount = "";
            List<WalletInfo> walletInfo = walletResponse.getData().getWalletInfo();
            if (walletInfo != null && walletInfo.size() > 0) {
                for (int i = 0; i < walletInfo.size(); i++) {
                    if (walletInfo.get(i).getWalletType().equals(getString(R.string.currency))) {
                        objMyApplication.setGbtWallet(walletInfo.get(i));
                        strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo.get(i).getExchangeAmount()));
                        tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
                        objMyApplication.setGBTBalance(walletInfo.get(i).getExchangeAmount());
                    }
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
                startActivity(new Intent(DashboardActivity.this, PayRequestScanActivity.class));
            }
        });

        payRequestLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 2000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                dialog.dismiss();
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

}