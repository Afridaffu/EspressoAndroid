package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.retrieveemail.RetUserResData;
import com.greenbox.coyni.utils.MyApplication;

import java.util.List;

public class BindingLayoutActivity extends AppCompatActivity {
    String strScreen = "";
    LinearLayout llCoyniAct, lyClose;
    TextView tvEmail;
    MyApplication objMyApplication;
    CardView reTryAgainBtn,editEmailLogoutCV;
    SQLiteDatabase mydatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_binding_layout);
            if (getIntent().getStringExtra("screen") != null && !getIntent().getStringExtra("screen").equals("")) {
                strScreen = getIntent().getStringExtra("screen");
                ControlMethod(strScreen);
            }
            initialization();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            llCoyniAct = findViewById(R.id.llCoyniAct);
            lyClose = findViewById(R.id.lyClose);
            tvEmail = findViewById(R.id.tvEmail);
            reTryAgainBtn = findViewById(R.id.reTryAgainBtn);
            editEmailLogoutCV = findViewById(R.id.editEmailLogoutCV);
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            objMyApplication = (MyApplication) getApplicationContext();
            List<RetUserResData> usersData;
            if (objMyApplication.getObjRetUsers() != null) {
                usersData = objMyApplication.getObjRetUsers().getData();
                if (usersData != null && usersData.size() > 0) {
                    tvEmail.setText(usersData.get(0).getEmail().replaceAll("(?<=.{4}).(?=.*@)", "*"));
                    objMyApplication.setStrRetrEmail(usersData.get(0).getEmail());
                }
            }
            llCoyniAct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            lyClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, OnboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
            reTryAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, RetrieveEmailActivity.class);
                    startActivity(i);
                    finish();
                }
            });

            editEmailLogoutCV.setOnClickListener(view -> {
                try {
                    dropAllTables();
                    Intent i = new Intent(BindingLayoutActivity.this, OnboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ControlMethod(String methodToShow) {
        try {
            switch (methodToShow) {
                case "retEmailfound": {
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.GONE);
                    findViewById(R.id.editEmailSuccessLayout).setVisibility(View.GONE);
                }
                break;
                case "retEmailfail": {
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.GONE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.VISIBLE);
                    findViewById(R.id.editEmailSuccessLayout).setVisibility(View.GONE);
                }
                break;
                case "EditEmail": {
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.GONE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.GONE);
                    findViewById(R.id.editEmailSuccessLayout).setVisibility(View.VISIBLE);
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

    private void dropAllTables() {
        try {
            mydatabase.execSQL("DROP TABLE IF EXISTS tblUserDetails;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblRemember;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblThumbPinLock;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblFacePinLock;");
            mydatabase.execSQL("DROP TABLE IF EXISTS tblPermanentToken;");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}