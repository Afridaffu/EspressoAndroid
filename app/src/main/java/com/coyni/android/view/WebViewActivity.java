package com.coyni.android.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coyni.android.R;
import com.coyni.android.model.bank.SignOnData;
import com.coyni.android.utils.MyApplication;

public class WebViewActivity extends AppCompatActivity {
    Boolean isFirst = true, isReturn = false, isBackPress = false;
    ProgressDialog dialog;
    MyApplication objMyApplication;
    String strURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_web_view);
            SignOnData signOnData;
            objMyApplication = (MyApplication) getApplicationContext();
            if (getIntent().getSerializableExtra("signon") != null && !getIntent().getSerializableExtra("signon").equals("")) {
                signOnData = (SignOnData) getIntent().getSerializableExtra("signon");
            } else {
                signOnData = new SignOnData();
            }
            WebView mywebview = (WebView) findViewById(R.id.webView);
            dialog = new ProgressDialog(WebViewActivity.this, R.style.MyAlertDialogStyle);
            dialog.setIndeterminate(false);
            dialog.setMessage("Please wait...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
            WebSettings webSettings = mywebview.getSettings();
            webSettings.setJavaScriptEnabled(true);
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
                    if (!isReturn && (url.contains("http://localhost:3000/?fiLoginAcctId") || url.contains("http://localhost:3000/?&action=cancel"))) {
                        String string = url.replace("#", "?");
                        String action = Uri.parse(string).getQueryParameter("action");
                        objMyApplication.setStrFiservError(action);
                        isBackPress = true;
                        onBackPressed();
                        finish();
                    }
                }

                public void onPageFinished(WebView view, String url) {
                    try {
//                        if (!isFirst && url.contains("https://aggqa.alldata.cashedge.com")) {
                        if (!isFirst && url.contains(strURL)) {
                            dialog.dismiss();
                        } else if (!isReturn && url.contains("http://localhost:3000/?fiLoginAcctId")) {
                            isReturn = true;
                        } else {
                            view.loadUrl("javascript:document.getElementById('submit-btn').click()");
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
        if (!isBackPress) {
            Intent i = new Intent();
            i.putExtra("backpressed", "true");
            setResult(RESULT_OK, i);
            finish();
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(WebViewActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(WebViewActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(WebViewActivity.this, this, false);
    }

}