package com.greenbox.coyni.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.utils.MyApplication;

public class WebViewActivity extends AppCompatActivity {
    Boolean isFirst = true, isReturn = false, isBackPress = false;
    MyApplication objMyApplication;
    String strURL = "";
    RelativeLayout layoutLoader;
    CircularProgressIndicator cpProgress;
    WebView mywebview;
    LinearLayout layoutClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
            setContentView(R.layout.activity_web_view);
            mywebview = (WebView) findViewById(R.id.webView);
            layoutClose = findViewById(R.id.layoutClose);
            layoutClose.setOnClickListener(view -> finish());
            SignOnData signOnData;
            objMyApplication = (MyApplication) getApplicationContext();
            layoutLoader = findViewById(R.id.layoutLoader);
            cpProgress = findViewById(R.id.cpProgress);
            if (getIntent().getSerializableExtra("signon") != null && !getIntent().getSerializableExtra("signon").equals("")) {
                signOnData = (SignOnData) getIntent().getSerializableExtra("signon");
            } else {
                signOnData = new SignOnData();
            }

            mywebview.setVisibility(View.GONE);
            layoutLoader.setVisibility(View.VISIBLE);
            cpProgress.show();
            WebSettings webSettings = mywebview.getSettings();
            webSettings.setJavaScriptEnabled(true);
//            mywebview.setListener(this, this);
//            mywebview.setMixedContentAllowed(false);
            String strLogin_acct_id = "", strAcct_id = "", strInvocation_mode = "", strReturn_url = "", strError_url = "";
            if (signOnData != null && signOnData.getUrl() != null) {
                if (!objMyApplication.getResolveUrl()) {
                    strURL = signOnData.getUrl();
                } else {
                    strURL = signOnData.getResolveUrl();
                }
                strLogin_acct_id = signOnData.getLogin_acct_id();
                strAcct_id = signOnData.getAcct_id();
                strInvocation_mode = signOnData.getInvocation_mode();
                strReturn_url = "http://localhost:3000/";
                strError_url = "http://localhost:3000/";
            }
            String html = "";
            if (!objMyApplication.getResolveUrl()) {
                html = "<html>\n" +
                        " <body>\n" +
                        " <form role='form' id=\"form\" action='" + strURL + "' method='post'>\n" +
                        " <input type=\"hidden\" name=\"acct_id\" value=\"" + strAcct_id + "\" />\n" +
                        " <input type=\"hidden\" name=\"return_url\" value=\"" + strReturn_url + "\" />\n" +
                        " <input type=\"hidden\" name=\"error_url\" value=\"" + strError_url + "\" />\n" +
                        " <input type=\"hidden\" name=\"invocation_mode\" value=\"" + strInvocation_mode + "\" />\n" +
                        " <input style=\"visibility: hidden;\" type=\"submit\" value=\"Submit\" id=\"submit-btn\" />\n" +
                        " </form>\n" +
                        " </body>\n" +
                        " </html>";
            } else {
                html = "<html>\n" +
                        " <body>\n" +
                        " <form role='form' id=\"form\" action='" + strURL + "' method='post'>\n" +
                        " <input type=\"hidden\" name=\"login_acct_id \" value=\"" + strLogin_acct_id + "\"/>\n" +
                        " <input type=\"hidden\" name=\"acct_id\" value=\"" + strAcct_id + "\" />\n" +
                        " <input type=\"hidden\" name=\"return_url\" value=\"" + strReturn_url + "\" />\n" +
                        " <input type=\"hidden\" name=\"error_url\" value=\"" + strError_url + "\" />\n" +
                        " <input type=\"hidden\" name=\"invocation_mode\" value=\"" + strInvocation_mode + "\" />\n" +
                        " <input style=\"visibility: hidden;\" type=\"submit\" value=\"Submit\" id=\"submit-btn\" />\n" +
                        " </form>\n" +
                        " </body>\n" +
                        " </html>";
            }
            mywebview.loadData(html, "text/html", null);
            mywebview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    try {
                        if (!isReturn && (url.contains("http://localhost:3000/?fiLoginAcctId") || url.contains("http://localhost:3000/?&action=cancel"))) {
                            String string = url.replace("#", "?");
                            String action = Uri.parse(string).getQueryParameter("action");
                            objMyApplication.setStrFiservError(action);
                            isBackPress = true;
                            onBackPressed();
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void onPageFinished(WebView view, String url) {
                    try {
                        if (!isFirst && url.contains(strURL)) {
                            findViewById(R.id.webView).setVisibility(View.VISIBLE);
                            findViewById(R.id.layoutLoader).setVisibility(View.GONE);
                        } else if (!isReturn && url.contains("http://localhost:3000/?fiLoginAcctId")) {
                            isReturn = true;
                        } else {
                            mywebview.loadUrl("javascript:document.getElementById('submit-btn').click()");
                            isFirst = false;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (!isBackPress) {
                Intent i = new Intent();
                i.putExtra("backpressed", "true");
                setResult(RESULT_OK, i);
                finish();
            }
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onPageStarted(String url, Bitmap favicon) {
//        try {
//            if (!isReturn && (url.contains("http://localhost:3000/?fiLoginAcctId") || url.contains("http://localhost:3000/?&action=cancel"))) {
//                String string = url.replace("#", "?");
//                String action = Uri.parse(string).getQueryParameter("action");
//                objMyApplication.setStrFiservError(action);
//                isBackPress = true;
//                onBackPressed();
//                finish();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    @Override
//    public void onPageFinished(String url) {
//        try {
//            if (!isFirst && url.contains(strURL)) {
//                findViewById(R.id.webView).setVisibility(View.VISIBLE);
//                findViewById(R.id.layoutLoader).setVisibility(View.GONE);
//            } else if (!isReturn && url.contains("http://localhost:3000/?fiLoginAcctId")) {
//                isReturn = true;
//            } else {
//                mywebview.loadUrl("javascript:document.getElementById('submit-btn').click()");
//                isFirst = false;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    @Override
//    public void onPageError(int errorCode, String description, String failingUrl) {
//        Toast.makeText(this, "Error Occured", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
//
//    }
//
//    @Override
//    public void onExternalPageRequest(String url) {
//
//    }

}