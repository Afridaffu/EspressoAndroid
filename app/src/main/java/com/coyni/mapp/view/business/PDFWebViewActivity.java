package com.coyni.mapp.view.business;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.view.BaseActivity;

public class PDFWebViewActivity extends BaseActivity {
    ImageView canceledIV;
    TextView agreNameTV, enableText;
    private WebView webView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_pdf_web_view);

        canceledIV = findViewById(R.id.canceledIV);
        agreNameTV = findViewById(R.id.agreNameTV);
        enableText = findViewById(R.id.enableText);

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.invalidate();
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVerticalScrollBarEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        showProgressDialog();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    if (view.getTitle().equals("")) {
                        view.reload();
                    } else {
                        dismissDialog();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        canceledIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        agreNameTV.setText(getIntent().getStringExtra("NAME"));
        launchDocumentUrl(getIntent().getStringExtra("URL"));

    }

    private void launchDocumentUrl(String url) {
        String urlNew = Uri.encode(url);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + urlNew);
        Log.e("URL", "https://docs.google.com/gview?embedded=true&url=" + urlNew);
    }

}

