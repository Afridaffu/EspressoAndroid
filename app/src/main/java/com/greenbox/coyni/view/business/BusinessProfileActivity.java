package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.Business_UserDetailsListenersActivity;
import com.greenbox.coyni.view.UserDetailsActivity;

public class BusinessProfileActivity extends AppCompatActivity {

    private LinearLayout feesLL, teamLL,bpbackBtn,switchOffLL,switchOnLL;
    boolean isTogleBtn=false;
    private Long mLastClickTime = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_profile);
       initFields();
    }

    private void initFields() {
        try {
            feesLL = findViewById(R.id.feesLL);
            teamLL = findViewById(R.id.teamLL);
            bpbackBtn = findViewById(R.id.bpbackBtn);
            switchOnLL=findViewById(R.id.switchOn);
            switchOffLL=findViewById(R.id.switchOff);
            switchOffLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTogleBtn=true;
                    switchOnLL.setVisibility(View.VISIBLE);
                    switchOffLL.setVisibility(View.GONE);
                }
            });

            switchOnLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTogleBtn=false;
                    switchOnLL.setVisibility(View.GONE);
                    switchOffLL.setVisibility(View.VISIBLE);
                }
            });


            bpbackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            teamLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this,TeamActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            feesLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent(BusinessProfileActivity.this,FeesActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            findViewById(R.id.business_UserDetailsLL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        startActivity(new Intent(BusinessProfileActivity.this, UserDetailsActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}