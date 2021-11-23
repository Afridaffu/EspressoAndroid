package com.greenbox.coyni.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneUpdateET;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;

public class EditPhoneActivity extends AppCompatActivity {

    OutLineBoxPhoneUpdateET currentPhoneET, newPhoneET;
    MyApplication myApplicationObj;
    NestedScrollView editPhoneSV;
    public boolean isSaveEnabled = false, isCurrentPhone = true, isNewPhone = false;
    public LinearLayout currentPhoneErrorLL, newPhoneErrorLL, editPhoneCloseLL;
    public TextView currentPhoneErrorTV, newPhoneErrorTV;
    public CardView savePhoneCV;
    Long mLastClickTime = 0L;
    ProgressDialog dialog;
    CustomerProfileViewModel customerProfileViewModel;
    public static EditPhoneActivity editPhoneActivity;
    String currentPhoneNumber, newPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_phone);
            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {
        try {
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);

            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            myApplicationObj = (MyApplication) getApplicationContext();
            editPhoneActivity = this;
            currentPhoneET = findViewById(R.id.currentPhoneET);
            currentPhoneET.setHint("Current Phone Number");
            currentPhoneET.disable();
            newPhoneET = findViewById(R.id.newPhoneET);
            newPhoneET.setHint("Phone Number");
            editPhoneSV = findViewById(R.id.editPhoneSV);
            currentPhoneErrorLL = findViewById(R.id.currentPhoneErrorLL);
            newPhoneErrorLL = findViewById(R.id.newPhoneErrorLL);
            currentPhoneErrorTV = findViewById(R.id.currentPhoneErrorTV);
            newPhoneErrorTV = findViewById(R.id.newPhoneErrorTV);
            savePhoneCV = findViewById(R.id.savePhoneCV);
            editPhoneCloseLL = findViewById(R.id.editPhoneCloseLL);

            currentPhoneET.setText(getIntent().getStringExtra("OLD_PHONE"));

            savePhoneCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSaveEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        dialog = new ProgressDialog(EditPhoneActivity.this, R.style.MyAlertDialogStyle);
                        dialog.setIndeterminate(false);
                        dialog.setMessage("Please wait...");
                        dialog.show();

                        callSendPhoneOTPAPI();

                    } else {
                        Log.e("isSaveEnabled", isSaveEnabled + "");
                    }
                }
            });

            editPhoneCloseLL.setOnClickListener(view -> {
                finish();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callSendPhoneOTPAPI() {
        try {
            currentPhoneNumber = currentPhoneET.getText().toString().substring(1, 4) + currentPhoneET.getText().toString().substring(6, 9) + currentPhoneET.getText().toString().substring(10, currentPhoneET.getText().length());
            newPhoneNumber = newPhoneET.getText().toString().substring(1, 4) + newPhoneET.getText().toString().substring(6, 9) + newPhoneET.getText().toString().substring(10, newPhoneET.getText().length());

            UpdatePhoneRequest updatePhoneRequest = new UpdatePhoneRequest();
            updatePhoneRequest.setCurrentPhoneNumber(currentPhoneNumber);
            updatePhoneRequest.setCurrentcountryCode(Utils.getStrCCode());
            updatePhoneRequest.setNewPhoneNumber(newPhoneNumber);
            updatePhoneRequest.setNewcountryCode(Utils.getStrCCode());
            customerProfileViewModel.updatePhoneSendOTP(updatePhoneRequest);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableOrDisableSave() {

        try {
            Log.e("all fields", isCurrentPhone + " " + isNewPhone);
            if (isCurrentPhone && isNewPhone) {
                isSaveEnabled = true;
                savePhoneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isSaveEnabled = false;
                savePhoneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void initObservers() {

        customerProfileViewModel.getUpdatePhoneSendOTPResponse().observe(this, new Observer<UpdatePhoneResponse>() {
            @Override
            public void onChanged(UpdatePhoneResponse updatePhoneResponse) {
                try {
                    dialog.dismiss();
                    if (updatePhoneResponse != null && updatePhoneResponse.getStatus().toLowerCase().equals("success")) {
                        myApplicationObj.setUpdatePhoneResponse(updatePhoneResponse);
                        startActivity(new Intent(EditPhoneActivity.this, OTPValidation.class)
                                .putExtra("screen", "EditPhone")
                                .putExtra("OTP_TYPE", "OTP")
                                .putExtra("IS_OLD_PHONE", "true")
                                .putExtra("OLD_PHONE_MASKED", currentPhoneET.getText().toString().trim())
                                .putExtra("NEW_PHONE_MASKED", newPhoneET.getText().toString().trim())
                                .putExtra("OLD_PHONE", currentPhoneNumber)
                                .putExtra("NEW_PHONE", newPhoneNumber));
                        finish();
                    } else {
                        Utils.displayAlert(updatePhoneResponse.getError().getErrorDescription(), EditPhoneActivity.this, "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            newPhoneET.setFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}