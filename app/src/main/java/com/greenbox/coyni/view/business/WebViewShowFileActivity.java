package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BusinessProfileRecyclerAdapter;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.profile.BusinessAccountDbaInfo;
import com.greenbox.coyni.model.profile.BusinessAccountsListInfo;
import com.greenbox.coyni.model.wallet.WalletInfo;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WebViewShowFileActivity extends AppCompatActivity {

    private WebView webView;
    private ImageView imageView;
    private static final String HTML_FORMAT = "<html><body style=\"text-align: center; background-color: black; vertical-align: center;\"><img width=\"300\" src = \"%s\" /></body></html>";
    private LinearLayout llClose;
    private ImageView btnClose;
    private RelativeLayout layoutLoader;
    private CircularProgressIndicator cpProgress;
    private String fileURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_review_application_web_view);

            Bundle bundle=getIntent().getExtras();
            if(bundle.getString("FILEURL")!=null) {
                fileURL = bundle.getString("FILEURL");
            }
            LogUtils.d("fileURL","fileURL"+fileURL);
            webView =(WebView)findViewById(R.id.webView);
            llClose =(LinearLayout)findViewById(R.id.layoutClose);
            btnClose =(ImageView)findViewById(R.id.closeBtn);

           // webView.setBackgroundColor(getResources().getColor(R.color.background_color));
            layoutLoader = findViewById(R.id.layoutLoader);
            cpProgress = findViewById(R.id.cpProgress);

            layoutLoader.setVisibility(View.VISIBLE);
            cpProgress.show();


            String extension = fileURL.substring(fileURL.lastIndexOf(".") + 1);

            LogUtils.d("extension","extension"+extension);
            LogUtils.d("extension","fillee"+fileURL.replaceAll(" ","%20"));

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    try {
                        if(extension.equalsIgnoreCase("pdf")){
                            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + fileURL.replaceAll(" ","%20"));
                            layoutLoader.setVisibility(View.GONE);
                        } else {
                            final String html = String.format(HTML_FORMAT, fileURL);
                            webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
                            layoutLoader.setVisibility(View.GONE);
                        }
                            onBackPressed();
                            finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onPageFinished(WebView view, String url) {
                    try {

                            findViewById(R.id.webView).setVisibility(View.VISIBLE);
                            findViewById(R.id.layoutLoader).setVisibility(View.GONE);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }   catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }
    }

    private void startWebView (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

}