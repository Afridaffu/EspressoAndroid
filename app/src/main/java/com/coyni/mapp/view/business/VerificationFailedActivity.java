package com.coyni.mapp.view.business;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.coyni.mapp.R;
import com.coyni.mapp.model.login.LoginData;
import com.coyni.mapp.model.login.LoginResponse;
import com.coyni.mapp.model.signin.BiometricSignIn;
import com.coyni.mapp.model.signin.BiometricSignInData;
import com.coyni.mapp.model.signin.InitializeResponse;
import com.coyni.mapp.model.signin.InitializeResponseData;
import com.coyni.mapp.utils.CustomTypefaceSpan;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.DashboardActivity;

public class VerificationFailedActivity extends AppCompatActivity {

    private TextView mTvName, mTvDbaName, mTvLegalName, descTV;
    private CardView mCvDone;
    private ImageView mIvClose;
    private BiometricSignIn loginResponse;
    private InitializeResponse initializeResponse;
    MyApplication myApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_failed);
        myApplication = (MyApplication) getApplicationContext();
        initFields();

        MyApplication objMyApplication = (MyApplication) getApplicationContext();
        initializeResponse = objMyApplication.getInitialResponse();
        Typeface font = Typeface.createFromAsset(getAssets(), "font/opensans_bold.ttf");
        if (initializeResponse != null && initializeResponse.getStatus() != null
                && initializeResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
            InitializeResponseData data = initializeResponse.getData();
            if (data != null) {
                if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
                    if (data.getFirstName() != null && data.getLastName() != null) {
                        String name = getString(R.string.dear_name, data.getFirstName() + " " + data.getLastName());
                        SpannableStringBuilder spannableName = new SpannableStringBuilder(name);
                        spannableName.setSpan(new CustomTypefaceSpan("", font), 5, name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        mTvName.setText(spannableName);
                    }
                    if (data.getCompanyName() != null && !data.getCompanyName().equals("")) {
//                        String name = getString(R.string.dear_name, data.getCompanyName());
//                        SpannableStringBuilder spannableName = new SpannableStringBuilder(name);
//                        spannableName.setSpan(new CustomTypefaceSpan("", font), 5, name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//                        mTvName.setText(spannableName);

                        String legalName = getString(R.string.legal_name_name, data.getCompanyName());
                        SpannableStringBuilder spannableLegalName = new SpannableStringBuilder(legalName);
                        spannableLegalName.setSpan(new CustomTypefaceSpan("", font), 11, legalName.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        mTvLegalName.setText(spannableLegalName);
                    }
                    if (data.getDbaName() != null && !data.getDbaName().equals("")) {
                        String dbaName = getString(R.string.verification_dba_name, data.getDbaName());
                        SpannableStringBuilder spannableLegalName = new SpannableStringBuilder(dbaName);
                        spannableLegalName.setSpan(new CustomTypefaceSpan("", font), 5, dbaName.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        mTvDbaName.setText(spannableLegalName);
                    }
                } else if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    if (data.getEmail() != null && !data.getEmail().equals("")) {

                        String name = getString(R.string.dear_name, data.getFirstName() + " " + data.getLastName());
                        SpannableStringBuilder spannableName = new SpannableStringBuilder(name);
                        spannableName.setSpan(new CustomTypefaceSpan("", font), 5, name.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                        mTvName.setText(spannableName);
                    }
                    mTvLegalName.setText(" ");
                    mTvDbaName.setText(" ");

//                    mTvDbaName.setVisibility(View.GONE);
//                    mTvLegalName.setVisibility(View.GONE);
                }
            }
        }

        mCvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDashboard();
            }
        });

        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchDashboard();
            }
        });

    }

    @Override
    public void onBackPressed() {

    }

    private void initFields() {
        mTvName = findViewById(R.id.tv_name);
        mTvDbaName = findViewById(R.id.tv_dba_name);
        mTvLegalName = findViewById(R.id.tv_legal_name);
        mCvDone = findViewById(R.id.cv_done);
        mIvClose = findViewById(R.id.iv_close);
        descTV = findViewById(R.id.descTV);
        if (myApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
            descTV.setText(getString(R.string.interest_in_coyni_customer_application));
        } else if (myApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || myApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
            descTV.setText(getString(R.string.interest_in_coyni_merchant_application));
        }
    }

    private void launchDashboard() {
        Intent dashboardIntent = new Intent(VerificationFailedActivity.this, BusinessDashboardActivity.class);
        if (myApplication.getAccountType() == Utils.PERSONAL_ACCOUNT)
            dashboardIntent = new Intent(VerificationFailedActivity.this, DashboardActivity.class);
        dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(dashboardIntent);
    }

}