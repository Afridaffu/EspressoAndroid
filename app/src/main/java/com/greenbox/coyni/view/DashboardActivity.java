package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;

import okhttp3.internal.Util;

public class DashboardActivity extends AppCompatActivity {
    LinearLayout layoutProfile, layoutCrypto, layoutCard;
    LinearLayout scanQr,viewMoreLL;
    DashboardViewModel dashboardViewModel;
    TextView tvUserName;
    MyApplication objMyApplication;
    Dialog dialog;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_dashboard);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            layoutProfile = findViewById(R.id.layoutProfile);
            layoutCrypto = findViewById(R.id.layoutCrypto);
            layoutCard = findViewById(R.id.layoutCard);
            tvUserName = findViewById(R.id.tvUserName);
            viewMoreLL=findViewById(R.id.viewMoreLL);
            scanQr=findViewById(R.id.scanQrLL);
            objMyApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            if (Utils.checkInternet(DashboardActivity.this)) {
                progressDialog = Utils.showProgressDialog(this);
                dashboardViewModel.meProfile();
            } else {
                Utils.displayAlert(getString(R.string.internet), DashboardActivity.this, "");
            }
            layoutProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(DashboardActivity.this, CustomerProfileActivity.class);
                    startActivity(i);
                }
            });
            viewMoreLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(DashboardActivity.this, TransactionDetailsActivity.class);
                        startActivity(intent);
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
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
                    startActivity(new Intent(DashboardActivity.this,PayRequestScanActivity.class));
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
                    } else {
                        tvUserName.setText("Hi " + strName);
                    }
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

}