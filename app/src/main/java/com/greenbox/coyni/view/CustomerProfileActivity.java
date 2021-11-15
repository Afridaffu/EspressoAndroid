package com.greenbox.coyni.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.angads25.toggle.widget.LabeledSwitch;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.FaceIdSetupBottomSheet;
import com.greenbox.coyni.utils.MyApplication;

public class CustomerProfileActivity extends AppCompatActivity {
    LabeledSwitch labeledSwitch;
    View viewFaceBottom;
    ImageView imgQRCode;
    Dialog dialog;
    TextView customerNameTV;
    MyApplication objMyApplication;
    CardView cvLogout;
    LinearLayout cpUserDetailsLL;
    Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_customer_profile);
            initialization();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            labeledSwitch = findViewById(R.id.switchbtn);
            viewFaceBottom = findViewById(R.id.viewSetupFaceBottom);
            imgQRCode = findViewById(R.id.imgQRCode);
            customerNameTV = findViewById(R.id.customerNameTV);
            cvLogout = findViewById(R.id.cvLogout);
            cpUserDetailsLL = findViewById(R.id.cpUserDetailsLL);

            objMyApplication = (MyApplication) getApplicationContext();
            viewFaceBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FaceIdSetupBottomSheet faceIdSetupBottomSheet = new FaceIdSetupBottomSheet();
                    faceIdSetupBottomSheet.show(getSupportFragmentManager(), faceIdSetupBottomSheet.getTag());
                }
            });
            labeledSwitch.setOnToggledListener((labeledSwitch, isOn) -> {
            });
            imgQRCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    displayQRCode();
                }
            });

            cvLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(CustomerProfileActivity.this, OnboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                }
            });

            customerNameTV.setText(objMyApplication.getStrUserName());

            cpUserDetailsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    startActivity(new Intent(CustomerProfileActivity.this, UserDetailsActivity.class));
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void displayQRCode() {
        try {
            ImageView imgClose;
            dialog = new Dialog(CustomerProfileActivity.this, R.style.DialogTheme);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.profileqrcode);
            Window window = dialog.getWindow();
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.7f;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            dialog.getWindow().setAttributes(lp);
            dialog.show();
            imgClose = dialog.findViewById(R.id.imgClose);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}