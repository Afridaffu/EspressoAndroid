package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.internal.Util;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout layoutProfile, layoutCrypto, layoutCard;
    LinearLayout scanQr;
    DashboardViewModel dashboardViewModel;
    IdentityVerificationViewModel identityVerificationViewModel;
    TextView tvUserName, tvUserNameSmall,tvUserInfoSmall,tvUserInfo;
    MyApplication objMyApplication;
    Dialog dialog;
    ProgressDialog progressDialog;
    RelativeLayout cvHeaderRL, cvSmallHeaderRL, transactionsRL;
    CardView getStartedCV, welcomeCoyniCV, underReviewCV,additionalActionCV;
    ImageView imgProfileSmall, imgProfile;

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
            getStates();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            cvHeaderRL = findViewById(R.id.cvHeaderRL);
            cvSmallHeaderRL = findViewById(R.id.cvSmallHeaderRL);
            getStartedCV = findViewById(R.id.getStartedCV);
            transactionsRL = findViewById(R.id.transactionsRL);
            imgProfileSmall = findViewById(R.id.imgProfileSmall);
            imgProfile = findViewById(R.id.imgProfile);

            welcomeCoyniCV = findViewById(R.id.welcomeCoyniCV);
            underReviewCV = findViewById(R.id.underReviewCV);
            additionalActionCV = findViewById(R.id.additionalActionCV);

            layoutProfile = findViewById(R.id.layoutProfile);
            layoutCrypto = findViewById(R.id.layoutCrypto);
            layoutCard = findViewById(R.id.layoutCard);
            tvUserName = findViewById(R.id.tvUserName);
            tvUserNameSmall = findViewById(R.id.tvUserNameSmall);
            tvUserInfo = findViewById(R.id.tvUserInfo);
            tvUserInfoSmall = findViewById(R.id.tvUserInfoSmall);
            scanQr = findViewById(R.id.scanQrLL);

            objMyApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);

            layoutProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DashboardActivity.this, CustomerProfileActivity.class);
                    startActivity(i);
                }
            });

            layoutCrypto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cryptoAssets();
                }
            });

            layoutCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueCards();
                }
            });
            scanQr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DashboardActivity.this, PayRequestScanActivity.class));
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
                    progressDialog.dismiss();
                    objMyApplication.setMyProfile(profile);
                    objMyApplication.setStrUserName(Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName()));
                    String strName = Utils.capitalize(profile.getData().getFirstName() + " " + profile.getData().getLastName());
                    if (strName != null && strName.length() > 21) {
                        tvUserName.setText("Hi " + strName.substring(0, 21) + "...");
                        tvUserNameSmall.setText("Hi " + strName.substring(0, 21) + "...");
                    } else {
                        tvUserName.setText("Hi " + strName);
                        tvUserNameSmall.setText("Hi " + strName);
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
                }
            }
        });

        try {
            identityVerificationViewModel.getGetStatusTracker().observe(this, new Observer<TrackerResponse>() {
                @Override
                public void onChanged(TrackerResponse trackerResponse) {

                    if (trackerResponse != null && trackerResponse.getStatus().equalsIgnoreCase("success")) {
                        objMyApplication.setTrackerResponse(trackerResponse);

                        if (trackerResponse.getData().isPersonIdentified()) {
                            cvHeaderRL.setVisibility(View.VISIBLE);
                            cvSmallHeaderRL.setVisibility(View.GONE);
                            getStartedCV.setVisibility(View.GONE);
                            transactionsRL.setVisibility(View.VISIBLE);

                            if (trackerResponse.getData().isPaymentModeAdded()) {
                                welcomeCoyniCV.setVisibility(View.GONE);
                                underReviewCV.setVisibility(View.GONE);
                                additionalActionCV.setVisibility(View.GONE);
                            } else {
                                welcomeCoyniCV.setVisibility(View.VISIBLE);
                                underReviewCV.setVisibility(View.GONE);
                                additionalActionCV.setVisibility(View.GONE);
                            }
                        } else {
                            cvHeaderRL.setVisibility(View.GONE);
                            cvSmallHeaderRL.setVisibility(View.VISIBLE);
                            getStartedCV.setVisibility(View.VISIBLE);
                            transactionsRL.setVisibility(View.GONE);
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//                buyViewModel.meSignOn();
//                dashboardViewModel.mePaymentMethods();
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

    private void getStates() {
        String json = null;
        try {
            InputStream is = getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(json, type);
            objMyApplication.setListStates(listStates);
            Log.e("list states", listStates.size() + "");
        } catch (IOException ex) {
            ex.printStackTrace();
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
            progressDialog = Utils.showProgressDialog(this);
            dashboardViewModel.meProfile();
            identityVerificationViewModel.getStatusTracker();
        } else {
            Utils.displayAlert(getString(R.string.internet), DashboardActivity.this, "");
        }
    }
}