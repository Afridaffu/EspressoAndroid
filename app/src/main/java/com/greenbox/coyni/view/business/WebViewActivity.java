package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.view.BaseActivity;

public class WebViewActivity extends BaseActivity {

    private WebView webView;
    private ImageView imageView;
    private static final String HTML_FORMAT = "<html><body style=\"text-align: center; background-color: black; vertical-align: center;\"><img width=\"300\" src = \"%s\" /></body></html>";
    private LinearLayout llClose;
    private ImageView btnClose;
    private String fileURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_web_view);

            Bundle bundle=getIntent().getExtras();
            if(bundle.getString("FILEURL")!=null) {
                fileURL = bundle.getString("FILEURL");
            }
            LogUtils.d("fileURL","fileURL"+fileURL);
            webView =(WebView)findViewById(R.id.webView);
            llClose =(LinearLayout)findViewById(R.id.layoutClose);
            btnClose =(ImageView)findViewById(R.id.closeBtn);

            String extension = fileURL.substring(fileURL.lastIndexOf(".") + 1);

            LogUtils.d("extension","extension"+extension);
            LogUtils.d("extension","fillee"+fileURL.replaceAll(" ","%20"));

            if(extension.equalsIgnoreCase("pdf")){
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + fileURL.replaceAll(" ","%20"));
            } else {
                final String html = String.format(HTML_FORMAT, fileURL);
                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

            }

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