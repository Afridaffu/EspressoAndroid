package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.RetEmailAdapter;
import com.greenbox.coyni.model.retrieveemail.RetUserResData;
import com.greenbox.coyni.utils.MyApplication;
import com.santalu.maskara.widget.MaskEditText;

import java.util.List;

public class BindingLayoutActivity extends AppCompatActivity {
    String strScreen = "";
    LinearLayout llCoyniAct, lyClose;
    TextView tvEmail;
    MyApplication objMyApplication;
    CardView reTryAgainBtn,btnChangePassCV;
    CardView editEmailLogoutCV;
    SQLiteDatabase mydatabase;
    RetEmailAdapter retEmailAdapter;
    RecyclerView retEmailRV;
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
            btnChangePassCV=findViewById(R.id.btnCV);
            editEmailLogoutCV = findViewById(R.id.editEmailLogoutCV);
            retEmailRV = findViewById(R.id.retEmailRV);
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            objMyApplication = (MyApplication) getApplicationContext();
            List<RetUserResData> usersData;
            if (objMyApplication.getObjRetUsers() != null) {
                usersData = objMyApplication.getObjRetUsers().getData();
                if (usersData != null && usersData.size() > 0) {
                    retEmailAdapter = new RetEmailAdapter(usersData,this);
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
                    retEmailRV.setLayoutManager(mLayoutManager);
                    retEmailRV.setItemAnimator(new DefaultItemAnimator());
                    retEmailRV.setAdapter(retEmailAdapter);
//                    tvEmail.setText(usersData.get(0).getEmail().replaceAll("(?<=.{4}).(?=.*@)", "*"));
//                    objMyApplication.setStrRetrEmail(usersData.get(0).getEmail());
                }
            }
            btnChangePassCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropAllTables();
                    Intent i = new Intent(BindingLayoutActivity.this, OnboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            });
            llCoyniAct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BindingLayoutActivity.this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            });
//            llCoyniAct.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent i = new Intent(BindingLayoutActivity.this, LoginActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(i);
//                }
//            });
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
                case "ChangePassword": {
                    findViewById(R.id.changePasswordLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.retfailaccontainer).setVisibility(View.GONE);
                    findViewById(R.id.retfoundactcontainer).setVisibility(View.GONE);
                }
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