package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;

public class WebViewActivity1 extends AppCompatActivity {
    Boolean isFirst = true, isReturn = false, isBackPress = false;
    ProgressDialog dialog;
    MyApplication objMyApplication;
    String strURL = "";
    RelativeLayout layoutLoader;
    CircularProgressIndicator cpProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_web_view1);
//            layoutLoader = findViewById(R.id.layoutLoader);
//            cpProgress = findViewById(R.id.cpProgress);
//            WebView mywebview = (WebView) findViewById(R.id.webView);
//            mywebview.setVisibility(View.GONE);
//            layoutLoader.setVisibility(View.VISIBLE);
//            cpProgress.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}