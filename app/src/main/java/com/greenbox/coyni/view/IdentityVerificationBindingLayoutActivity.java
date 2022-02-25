package com.greenbox.coyni.view;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;

public class IdentityVerificationBindingLayoutActivity extends AppCompatActivity {
    String strScreen = "";
    LinearLayout successCloseIV,underReviewCloseIV;
    MyApplication objMyApplication;
    SQLiteDatabase mydatabase;
    CardView ivSuccessCV,idveriCardViewExitBtn,idveriDoneBtn;
    TextView contactUSTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_iv_binding_layout);
            initialization();
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                strScreen = getIntent().getStringExtra("screen");
                ControlMethod(strScreen);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            successCloseIV = findViewById(R.id.successCloseIV);
            ivSuccessCV = findViewById(R.id.ivSuccessCV);

            contactUSTV = findViewById(R.id.contactUSTV);
            idveriCardViewExitBtn = findViewById(R.id.idveriCardViewExitBtn);

            underReviewCloseIV = findViewById(R.id.underReviewCloseIV);
            idveriDoneBtn = findViewById(R.id.idveriDoneBtn);

            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            objMyApplication = (MyApplication) getApplicationContext();

            Log.d("objMyApplication","objMyApplication"+objMyApplication.getAccountType());

            ivSuccessCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(objMyApplication.getAccountType()==2) {
                        Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, BusinessDashboardActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, DashboardActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }
                }
            });

            successCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(objMyApplication.getAccountType()==2) {
                        Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, BusinessDashboardActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, DashboardActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }

                }
            });

            contactUSTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(Utils.mondayURL));
                    startActivity(i);
                }
            });

            idveriCardViewExitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, OnboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

            underReviewCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

            idveriDoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(IdentityVerificationBindingLayoutActivity.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "SUCCESS": {
                    findViewById(R.id.ivSuccessLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.ivFailedLayout).setVisibility(View.GONE);
                    findViewById(R.id.ivUnderReviewLayout).setVisibility(View.GONE);
                }
                break;
                case "FAILED": {
                    findViewById(R.id.ivSuccessLayout).setVisibility(View.GONE);
                    findViewById(R.id.ivFailedLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.ivUnderReviewLayout).setVisibility(View.GONE);
                }
                break;
                case "UNDER_REVIEW": {
                    findViewById(R.id.ivSuccessLayout).setVisibility(View.GONE);
                    findViewById(R.id.ivFailedLayout).setVisibility(View.GONE);
                    findViewById(R.id.ivUnderReviewLayout).setVisibility(View.VISIBLE);
                }
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}