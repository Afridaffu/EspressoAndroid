package com.coyni.mapp.view.business;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.coyni.mapp.R;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.view.BaseActivity;

public class WebViewShowFileActivity extends BaseActivity {

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

            Bundle bundle = getIntent().getExtras();
            if (bundle.getString("FILEURL") != null) {
                fileURL = bundle.getString("FILEURL");
            }
            LogUtils.d("fileURL", "fileURL" + fileURL);
            webView = (WebView) findViewById(R.id.webView);
            llClose = (LinearLayout) findViewById(R.id.layoutClose);
            btnClose = (ImageView) findViewById(R.id.closeBtn);

            layoutLoader = findViewById(R.id.layoutLoader);
            cpProgress = findViewById(R.id.cpProgress);

            layoutLoader.setVisibility(View.VISIBLE);
            cpProgress.show();

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            String extension = fileURL.substring(fileURL.lastIndexOf(".") + 1);

            LogUtils.d(TAG, "extension" + extension);
            LogUtils.d(TAG, "fillee" + fileURL.replaceAll(" ", "%20"));

            if (extension.equalsIgnoreCase("pdf")) {
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + fileURL.replaceAll(" ", "%20"));
            } else {
                final String html = String.format(HTML_FORMAT, fileURL);
                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
            }
            layoutLoader.setVisibility(View.GONE);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return (false);
        }
    }

    private void startWebView(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

}