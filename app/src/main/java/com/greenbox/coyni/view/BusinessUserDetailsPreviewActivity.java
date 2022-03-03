package com.greenbox.coyni.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class BusinessUserDetailsPreviewActivity extends AppCompatActivity {

    private String authenticateType = "", phoneFormat = "";
    private TextView heading, title, value;
    private CardView changeCV;
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private static final int CODE_AUTHENTICATION_VERIFICATION = 251;
    private DashboardViewModel dashboardViewModel;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private Long mLastClickTime = 0L;
    private MyApplication myApplicationObj;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_user_details_preview);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        heading = findViewById(R.id.intentName);
        title = findViewById(R.id.titleTV);
        value = findViewById(R.id.contentTV);
        changeCV = findViewById(R.id.changeCV);
        myApplicationObj = (MyApplication) getApplicationContext();
        isBiometric = Utils.getIsBiometric();

        initObservers();
        findViewById(R.id.dialogCLoseLL).setOnClickListener(view -> {
            try {
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails") && getIntent().getStringExtra("title").equalsIgnoreCase("EMAIL")) {
            heading.setText(getString(R.string.email));
            title.setText(getString(R.string.email_curr));
            authenticateType = getIntent().getStringExtra("title");
            value.setText(getIntent().getStringExtra("value"));
            isTouchId = Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock = Boolean.parseBoolean(getIntent().getStringExtra("face"));
            changeCV.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                try {
                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(BusinessUserDetailsPreviewActivity.this)) {
                        if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(BusinessUserDetailsPreviewActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(BusinessUserDetailsPreviewActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "EditEmail"));
                        }
                    } else {
                        startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditEmail"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails") && getIntent().getStringExtra("title").equalsIgnoreCase("ADDRESS")) {
            heading.setText(getString(R.string.address));
            title.setText(getString(R.string.address_curr));
            if (value.getText().toString().equals("")) {
                value.setText(getIntent().getStringExtra("value"));
            }
            authenticateType = getIntent().getStringExtra("title");
            isTouchId = Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock = Boolean.parseBoolean(getIntent().getStringExtra("face"));

            changeCV.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                try {

                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(BusinessUserDetailsPreviewActivity.this)) {
                        if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(BusinessUserDetailsPreviewActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(BusinessUserDetailsPreviewActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "EditAddress"));
                        }
                    } else {
                        startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditAddress"));
                    }
//shiva
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        }
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("UserDetails") && getIntent().getStringExtra("title").equalsIgnoreCase("PHONE")) {
            heading.setText(getString(R.string.user_phonenumber));
            title.setText(getString(R.string.phonenumber_curr));
            value.setText(getIntent().getStringExtra("value"));
            phoneFormat = getIntent().getStringExtra("value");
            authenticateType = getIntent().getStringExtra("title");
            isTouchId = Boolean.parseBoolean(getIntent().getStringExtra("touch"));
            isFaceLock = Boolean.parseBoolean(getIntent().getStringExtra("face"));

            changeCV.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                try {
                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(BusinessUserDetailsPreviewActivity.this)) {
                        if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(BusinessUserDetailsPreviewActivity.this)) || (isFaceLock))) {
                            Utils.checkAuthentication(BusinessUserDetailsPreviewActivity.this, CODE_AUTHENTICATION_VERIFICATION);
                        } else {
                            startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("OLD_PHONE", phoneFormat)
                                    .putExtra("screen", "EditPhone"));
                        }
                    } else {
                        startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("OLD_PHONE", phoneFormat)
                                .putExtra("screen", "EditPhone"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("DBAInfo") && getIntent().getStringExtra("action").equalsIgnoreCase("EditEmailDBA")){
            heading.setText(getString(R.string.email));
            title.setText(getString(R.string.email_curr));
            value.setText(getIntent().getStringExtra("value"));

            if (myApplicationObj.getDbaInfoResp()!=null){
                value.setText(myApplicationObj.getDbaInfoResp().getData().getEmail());
            }


            changeCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    try {
                        startActivity(new Intent(BusinessUserDetailsPreviewActivity.this,EditEmailActivity.class).putExtra("screen","DBAChangeEmail").putExtra("action","EditEmailDBA"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else if (getIntent().getStringExtra("screen").equalsIgnoreCase("DBAInfo") && getIntent().getStringExtra("action").equalsIgnoreCase("EditPhoneDBA")){
            heading.setText(getString(R.string.phone));
            title.setText(getString(R.string.phonenumber_curr));
            value.setText(getIntent().getStringExtra("value"));
            phoneFormat = getIntent().getStringExtra("value");

            if (myApplicationObj.getDbaInfoResp()!=null){
                value.setText( "(" + myApplicationObj.getDbaInfoResp().getData().getPhoneNumberDto().getPhoneNumber().substring(0, 3) + ") " + myApplicationObj.getDbaInfoResp().getData().getPhoneNumberDto().getPhoneNumber().substring(3, 6) + "-" + myApplicationObj.getDbaInfoResp().getData().getPhoneNumberDto().getPhoneNumber().substring(6, 10));
            }


            changeCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    try {
                        startActivity(new Intent(BusinessUserDetailsPreviewActivity.this,EditPhoneActivity.class).putExtra("screen","DBAChangePhone").putExtra("action","EditPhoneDBA").putExtra("OLD_PHONE", phoneFormat));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    @SuppressLint("SetTextI18n")
    private void initObservers() {
        dashboardViewModel.getProfileMutableLiveData().observe(BusinessUserDetailsPreviewActivity.this, profile -> {
            myApplicationObj.setMyProfile(profile);
            if (getIntent().getStringExtra("title")!=null&&getIntent().getStringExtra("title").equalsIgnoreCase("ADDRESS")) {
                String addressFormatted = "";
                if (profile.getData().getAddressLine1() != null && !profile.getData().getAddressLine1().equals("")) {
                    addressFormatted = addressFormatted + profile.getData().getAddressLine1() + ", ";
                }
                if (profile.getData().getAddressLine2() != null && !profile.getData().getAddressLine2().equals("")) {
                    addressFormatted = addressFormatted + profile.getData().getAddressLine2() + ", ";
                }
                if (profile.getData().getCity() != null && !profile.getData().getCity().equals("")) {
                    addressFormatted = addressFormatted + profile.getData().getCity() + ", ";
                }
                if (profile.getData().getState() != null && !profile.getData().getState().equals("")) {
                    addressFormatted = addressFormatted + profile.getData().getState() + ", ";
                }

                if (profile.getData().getZipCode() != null && !profile.getData().getZipCode().equals("")) {
                    addressFormatted = addressFormatted + profile.getData().getZipCode() + ", ";
                }

                value.setText(addressFormatted.substring(0, addressFormatted.trim().length() - 1) + ".");
            }
            if (getIntent().getStringExtra("title")!=null&&getIntent().getStringExtra("title").equalsIgnoreCase("EMAIL")){
                value.setText(profile.getData().getEmail());
            }
            if (getIntent().getStringExtra("title")!=null&&getIntent().getStringExtra("title").equalsIgnoreCase("PHONE")){
                String phne_number=profile.getData().getPhoneNumber().split(" ")[1];
                phne_number = "(" + phne_number.substring(0, 3) + ") " + phne_number.substring(3, 6) + "-" + phne_number.substring(6, 10);

                value.setText(phne_number);
            }

        });

        businessIdentityVerificationViewModel.getGetDBAInfoResponse().observe(this, new Observer<DBAInfoResp>() {
            @Override
            public void onChanged(DBAInfoResp dbaInfoResp) {
                if (dbaInfoResp!=null&&dbaInfoResp.getStatus().equalsIgnoreCase("SUCCESS")){
                    myApplicationObj.setDbaInfoResp(dbaInfoResp);
                    if (getIntent().getStringExtra("screen").equalsIgnoreCase("DBAInfo") && getIntent().getStringExtra("action").equalsIgnoreCase("EditEmailDBA")) {
                        value.setText(dbaInfoResp.getData().getEmail());
                    }
                    else if (getIntent().getStringExtra("screen").equalsIgnoreCase("DBAInfo") && getIntent().getStringExtra("action").equalsIgnoreCase("EditPhoneDBA")){
                       value.setText("(" +dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().substring(0, 3) + ") " + dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().substring(3, 6) + "-" + dbaInfoResp.getData().getPhoneNumberDto().getPhoneNumber().substring(6, 10));
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            if (resultCode == RESULT_OK) {
                switch (authenticateType) {
                    case "EMAIL":
                        Intent ee = new Intent(BusinessUserDetailsPreviewActivity.this, EditEmailActivity.class);
                        startActivity(ee);
                        break;
                    case "ADDRESS":
                        Intent ea = new Intent(BusinessUserDetailsPreviewActivity.this, EditAddressActivity.class);
                        startActivity(ea);
                        break;
                    case "PHONE":
                        Intent ep = new Intent(BusinessUserDetailsPreviewActivity.this, EditPhoneActivity.class);
                        ep.putExtra("OLD_PHONE", phoneFormat);
                        startActivity(ep);

                        break;
                }
            } else {
                switch (authenticateType) {
                    case "EMAIL":
                        startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditEmail"));
                        break;
                    case "ADDRESS":
                        startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("screen", "EditAddress"));
                        break;
                    case "PHONE":
                        startActivity(new Intent(BusinessUserDetailsPreviewActivity.this, PINActivity.class)
                                .putExtra("TYPE", "ENTER")
                                .putExtra("OLD_PHONE", phoneFormat)
                                .putExtra("screen", "EditPhone"));

                        break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dashboardViewModel.meProfile();
            businessIdentityVerificationViewModel.getDBAInfo();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}