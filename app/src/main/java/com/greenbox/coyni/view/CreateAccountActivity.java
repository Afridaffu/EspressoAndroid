package com.greenbox.coyni.view;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.outline_et.OutLineBoxEditText;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;

public class CreateAccountActivity extends AppCompatActivity {

    OutLineBoxPhoneNumberEditText phoneNumberET;
    OutLineBoxEditText firstNameET,lastNameET,emailET,passwordET,confirmPasswordET;
    LinearLayout emailErrorLL;
    TextView passwordInfoTV,privacyTV,tosTV;
    ImageView createAccountCloseIV;
    public boolean isFirstName = false, isLastName = false,isEmail = false,isPhoneNumber = false,
            isPassword = false,isConfirmPassword = false, isNextEnabled = false;
    public String passwordString = "";
    public MaterialCardView nextCV;

    Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_create_account);
        initFields();
    }

    public void initFields(){

        emailErrorLL = findViewById(R.id.emailErrorLL);
        passwordInfoTV = findViewById(R.id.passwordInfoTV);
        privacyTV = findViewById(R.id.privacyTV);
        tosTV = findViewById(R.id.tosTV);
        nextCV = findViewById(R.id.nextCV);
        createAccountCloseIV = findViewById(R.id.createAccountCloseIV);

        firstNameET = findViewById(R.id.firstNameET);
        firstNameET.setField("First Name","First Name","text",false, this);
        firstNameET.setDigits();

        lastNameET = findViewById(R.id.lastNameET);
        lastNameET.setField("Last Name","Last Name","text",false, this);
        lastNameET.setDigits();

        emailET = findViewById(R.id.emailET);
        emailET.setField("Email","Email","textEmailAddress",false, this);

        phoneNumberET = findViewById(R.id.phoneNumberET);
        phoneNumberET.setField("Phone Number","Phone Number","number",this);

        passwordET = findViewById(R.id.passwordET);
        passwordInfoTV = findViewById(R.id.passwordInfoTV);
        passwordET.setField("Password","8-12 Characters","textPassword",true, this);
        passwordET.setPasswordType(true,passwordInfoTV);

        confirmPasswordET = findViewById(R.id.confirmPasswordET);
        confirmPasswordET.setField("Confirm Password","Confirm Password","textPassword",false, this);
        confirmPasswordET.setPasswordType(true, null);


        tosTV.setPaintFlags(tosTV.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        privacyTV.setPaintFlags(privacyTV.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        tosTV.setOnClickListener(view -> {

        });

        privacyTV.setOnClickListener(view -> {

        });

        nextCV.setOnClickListener(view -> {
            if(isNextEnabled){
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(CreateAccountActivity.this, OTPValidation.class)
                        .putExtra("OTP_TYPE","MOBILE")
                        .putExtra("MOBILE",phoneNumberET.getText())
                        .putExtra("EMAIL",emailET.getText())
                );

            }else{
                startActivity(new Intent(CreateAccountActivity.this, OTPValidation.class)
                        .putExtra("OTP_TYPE","EMAIL")
                        .putExtra("MOBILE",phoneNumberET.getText())
                        .putExtra("EMAIL",emailET.getText())
                );
            }

            Log.e("Field1", firstNameET.getText());
            Log.e("Field2", lastNameET.getText());
            Log.e("Field3", emailET.getText());
            Log.e("Field4", phoneNumberET.getText());
            Log.e("Field5", passwordET.getText());
            Log.e("Field6", confirmPasswordET.getText());
        });

        createAccountCloseIV.setOnClickListener(view -> {
            finish();
        });
    }
}