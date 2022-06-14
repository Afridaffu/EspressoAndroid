package com.greenbox.coyni.view.business;

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

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BaseActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setLoadWithOverviewMode(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
//        webSettings.setSupportZoom(true);
//        webSettings.setDefaultTextEncodingName("utf-8");
        webView.invalidate();
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVerticalScrollBarEnabled(true);
        showProgressDialog();

//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onJsAlert(WebView view, final String url, String message, JsResult result) {
//                enableText.setText("Enabled");
//                enableText.setVisibility(View.VISIBLE);
//                result.cancel();
//                return true;
//            }
//        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//                dismissDialog();
//                String myUrl = getIntent().getStringExtra("URL").replace("&", "%26");
//                showProgressDialog();
//                webView.loadUrl("javascript:showPDF('" + myUrl + "')");

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

//        webView.loadUrl("file:///android_asset/pdfViewerScript.html");

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

