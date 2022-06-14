package com.greenbox.coyni.view;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.greenbox.coyni.R;


public class GetHelpWebViewActivity extends BaseActivity {
    WebView webView;
//    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_get_help_web_view);
//            String html = "<iframe src=\"https://forms.monday.com/forms/embed/b56bfd25a8e464124c9973589e4bc7d6?r=use1\" style=\"height: 100%; width: 100%; border: 0px; box-shadow: 5px 5px 56px 0px rgba(0,0,0,0.25);\"></iframe>";
//            String html = "<iframe src=\"https://forms.monday.com/forms/embed/dd62fbae1c38a495940a76dbb1689979?r=use1\" style=\"height: 100%; width: 100%; border: 0px; box-shadow: 5px 5px 56px 0px rgba(0,0,0,0.25);\"></iframe>";
            String html = "<iframe src=\"https://forms.monday.com/forms/embed/574096202882f845863fd5a5c2b1d61d?r=use1\" style=\"height: 100%; width: 100%; border: 0px; box-shadow: 5px 5px 56px 0px rgba(0,0,0,0.25);\"></iframe>";
            showProgressDialog();
            webView = (WebView) findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setVerticalScrollBarEnabled(false);
            webView.loadData(html, "text/html", "utf-8");
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    dismissDialog();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}