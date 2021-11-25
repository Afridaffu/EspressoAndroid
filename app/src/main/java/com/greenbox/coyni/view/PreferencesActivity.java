package com.greenbox.coyni.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.CustomerTimeZonesAdapter;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;

public class PreferencesActivity extends AppCompatActivity {

    MyApplication myApplicationObj;
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;
    boolean isProfile = false;
    TextInputLayout timeZoneTIL,accountTIL;
    TextInputEditText timeZoneET,accountET;
    ConstraintLayout timeZoneCL;
    LinearLayout preferencesCloseLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_preferences);
            initFields();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields(){
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            myApplicationObj = (MyApplication) getApplicationContext();
            timeZoneTIL = findViewById(R.id.timeZoneTIL);
            timeZoneET = findViewById(R.id.timeZoneET);
            accountTIL = findViewById(R.id.accountTIL);
            accountET = findViewById(R.id.accountET);
            timeZoneCL = findViewById(R.id.timeZoneCL);
            preferencesCloseLL = findViewById(R.id.preferencesCloseLL);

            timeZoneCL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET);
                }
            });

            timeZoneET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.populateTimeZones(PreferencesActivity.this, timeZoneET);
                }
            });

            preferencesCloseLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            dashboardViewModel.mePreferences();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers(){
        dashboardViewModel.getPreferenceMutableLiveData().observe(this, new Observer<Preferences>() {
            @Override
            public void onChanged(Preferences user) {

//                dialog.dismiss();
                if (user.getData().getTimeZone() == 0) {
                    timeZoneET.setText(getString(R.string.PST));
                    myApplicationObj.setTimezone("PST");
                } else if (user.getData().getTimeZone() == 1) {
                    timeZoneET.setText(getString(R.string.MST));
                    myApplicationObj.setTimezone("MST");
                } else if (user.getData().getTimeZone() == 2) {
                    timeZoneET.setText(getString(R.string.CST));
                    myApplicationObj.setTimezone("CST");
                } else if (user.getData().getTimeZone() == 3) {
                    timeZoneET.setText(getString(R.string.EST));
                    myApplicationObj.setTimezone("EST");
                } else if (user.getData().getTimeZone() == 4) {
                    timeZoneET.setText(getString(R.string.HST));
                    myApplicationObj.setTimezone("HST");
                }else if (user.getData().getTimeZone() == 5) {
                    timeZoneET.setText(getString(R.string.AST));
                    myApplicationObj.setTimezone("AST");
                }
            }
        });
    }


}