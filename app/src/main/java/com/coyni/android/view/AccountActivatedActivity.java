package com.coyni.android.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.android.R;
import com.coyni.android.fragments.IdentityVerificationFragment;
import com.coyni.android.fragments.UserAddressFragment;
import com.coyni.android.model.login.Login;
import com.coyni.android.model.login.LoginRequest;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.LoginViewModel;

public class AccountActivatedActivity extends AppCompatActivity {
    LoginViewModel loginViewModel;
    MyApplication objMyApplication;
    String strPassword = "";
    RelativeLayout rlMailingAddress, rlIdentityVerification, rlPaymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_account_activated);
            initialization();
//            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            CardView cvStart = (CardView) findViewById(R.id.cvStart);
            TextView tvSkip = (TextView) findViewById(R.id.tvSkip);
            rlMailingAddress = (RelativeLayout) findViewById(R.id.rlMailingAddress);
            rlIdentityVerification = (RelativeLayout) findViewById(R.id.rlIdentityVerification);
            rlPaymentMethods = (RelativeLayout) findViewById(R.id.rlPaymentMethods);
            Utils.statusBar(AccountActivatedActivity.this);
            objMyApplication = (MyApplication) getApplicationContext();
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            tvSkip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                        onBackPressed();
                    } else {
                        Intent i = new Intent(AccountActivatedActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }
            });
            if (getIntent().getStringExtra("password") != null && !getIntent().getStringExtra("password").equals("")) {
                strPassword = getIntent().getStringExtra("password");
                LoginRequest loginRequest = new LoginRequest();
                loginRequest.setEmail(objMyApplication.getStrEmail().trim());
                loginRequest.setPassword(strPassword);
                loginViewModel.login(loginRequest);
            }
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                UserTracker userTracker = objMyApplication.getUserTracker();
                if (userTracker != null && userTracker.getData().getPaymentModeAdded()) {
                    ImageView imgPayPending = (ImageView) findViewById(R.id.imgPayPending);
                    imgPayPending.setImageResource(R.drawable.ic_active_done);
                }
            }
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                UserTracker userTracker = objMyApplication.getUserTracker();
                if (userTracker != null && userTracker.getData().getAddressAvailable()) {
                    ImageView imgMailingAddrPending = (ImageView) findViewById(R.id.imgMailingAddrPending);
                    imgMailingAddrPending.setImageResource(R.drawable.ic_active_done);
                }
                else
                {
                    ImageView imgMailingAddrPending = (ImageView) findViewById(R.id.imgMailingAddrPending);
                    imgMailingAddrPending.setImageResource(R.drawable.ic_active_pending);
                }
            }
            rlMailingAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAddressFragment profileFragment = UserAddressFragment.newInstance(AccountActivatedActivity.this);
                    try {
                        openFragment(profileFragment, "userAddress");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            rlPaymentMethods.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserAddressFragment profileFragment = UserAddressFragment.newInstance(AccountActivatedActivity.this);
                    try {
                        openFragment(profileFragment, "userAddress");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            rlIdentityVerification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IdentityVerificationFragment identityVerificationFragment = IdentityVerificationFragment.newInstance(AccountActivatedActivity.this);
                    try {
                        openFragment(identityVerificationFragment, "identityVerificationFragment");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragment, tag).addToBackStack(tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        loginViewModel.getLoginLiveData().observe(this, new Observer<Login>() {
            @Override
            public void onChanged(Login login) {
                if (login != null) {
                    Utils.setStrAuth(login.getData().getJwtToken());

                }
            }
        });
    }
}