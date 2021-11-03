package com.greenbox.coyni.view;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.greenbox.coyni.R;

public class EnableAuthID extends AppCompatActivity {

    MaterialCardView enableFaceCV,enableTouchCV,successGetStartedCV;
    TextView notNowFaceTV,notNowTouchTV, notNowSuccessTV;
    RelativeLayout faceIDRL,touchIDRL,successRL;
    String enableType;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    SQLiteDatabase mydatabase;
    Cursor dsFacePin;
    ImageView succesCloseIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_enable_auth_id);

            enableFaceCV = findViewById(R.id.enableFaceCV);
            notNowFaceTV = findViewById(R.id.notNowFaceTV);
            successGetStartedCV = findViewById(R.id.successGetStartedCV);

            enableTouchCV = findViewById(R.id.enableTouchCV);
            notNowTouchTV = findViewById(R.id.notNowTouchTV);
            notNowSuccessTV = findViewById(R.id.notNowSuccessTV);

            faceIDRL = findViewById(R.id.faceIDRL);
            touchIDRL = findViewById(R.id.touchIDRL);

            successRL = findViewById(R.id.successRL);
            succesCloseIV = findViewById(R.id.succesCloseIV);

            enableType = getIntent().getStringExtra("ENABLE_TYPE");

            switch (enableType) {
                case "TOUCH":
                    faceIDRL.setVisibility(View.GONE);
                    touchIDRL.setVisibility(View.VISIBLE);
                    successRL.setVisibility(View.GONE);
                    break;
                case "FACE":
                    faceIDRL.setVisibility(View.VISIBLE);
                    touchIDRL.setVisibility(View.GONE);
                    successRL.setVisibility(View.GONE);
                    break;
                case "SUCCESS":
                    faceIDRL.setVisibility(View.GONE);
                    touchIDRL.setVisibility(View.GONE);
                    successRL.setVisibility(View.VISIBLE);
                    break;
            }

            enableFaceCV.setOnClickListener(view -> {
                saveFace("true");
                saveThumb("false");
            });

            notNowFaceTV.setOnClickListener(view -> {
                faceIDRL.setVisibility(View.GONE);
                touchIDRL.setVisibility(View.GONE);
                successRL.setVisibility(View.VISIBLE);
            });

            enableTouchCV.setOnClickListener(view -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
                    if (!fingerprintManager.isHardwareDetected()) {
                        Log.e("Not support","Not support");
                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        final Intent enrollIntent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG);
                        startActivityForResult(enrollIntent, TOUCH_ID_ENABLE_REQUEST_CODE);
                    } else {
                        Log.e("Supports","Supports");
                        saveThumb("true");
                        saveFace("false");
                    }
                }
            });

            notNowTouchTV.setOnClickListener(view -> {
                faceIDRL.setVisibility(View.GONE);
                touchIDRL.setVisibility(View.GONE);
                successRL.setVisibility(View.VISIBLE);
            });

            succesCloseIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if(enableType.equals("TOUCH")){
                      faceIDRL.setVisibility(View.GONE);
                      touchIDRL.setVisibility(View.VISIBLE);
                      successRL.setVisibility(View.GONE);
                  }else{
                      faceIDRL.setVisibility(View.VISIBLE);
                      touchIDRL.setVisibility(View.GONE);
                      successRL.setVisibility(View.GONE);
                  }
                }
            });

            notNowSuccessTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(EnableAuthID.this, DashboardActivity.class));
                }
            });

            successGetStartedCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == TOUCH_ID_ENABLE_REQUEST_CODE  && resultCode  == RESULT_OK) {
                //save lock
                saveThumb("true");
                saveFace("false");
                Log.e("Save","true");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveThumb(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblThumbPinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            mydatabase.execSQL("INSERT INTO tblThumbPinLock(id,isLock) VALUES(null,'" + value + "')");

            dsFacePin = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String data = dsFacePin.getString(1);
                Log.e("Thumb lock",data);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveFace(String value) {
        try {
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            mydatabase.execSQL("CREATE TABLE IF NOT EXISTS tblFacePinLock(id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, isLock TEXT);");
            mydatabase.execSQL("INSERT INTO tblFacePinLock(id,isLock) VALUES(null,'" + value + "')");

            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String data = dsFacePin.getString(1);
                Log.e("Face lock",data);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}