package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class SecureAccount extends AppCompatActivity {

    private static int CODE_AUTHENTICATION_VERIFICATION = 241;
    private static int BIOMETRIC_REQUEST_CODE = 777;
    MaterialCardView secureNextCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_secure_account);

        secureNextCV = findViewById(R.id.secureNextCV);

        secureNextCV.setOnClickListener(view -> {

//                if(Utils.checkAuthentication(this)){
////                    Utils.checkAuthentication(this, CODE_AUTHENTICATION_VERIFICATION);
//                    if(Utils.isFingerPrint(SecureAccount.this)){
//                        Log.e("isFingerPrint", "True");
//                        startActivity(new Intent(SecureAccount.this, EnableTouchID.class));
//                    }else{
//                        Log.e("isFingerPrint", "False");
//                        final Intent enrollIntent;
//
//                        enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
//                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
//                                BIOMETRIC_STRONG);
//                        startActivityForResult(enrollIntent, BIOMETRIC_REQUEST_CODE);
//                    }
//                }else{
//
//                }

            startActivity(new Intent(SecureAccount.this, PINActivity.class).putExtra("TYPE", "CHOOSE"));

        });
    }
}