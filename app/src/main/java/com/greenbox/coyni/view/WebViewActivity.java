package com.greenbox.coyni.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SignOnData;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;

public class WebViewActivity extends AppCompatActivity {
    Boolean isFirst = true, isReturn = false, isBackPress = false;
    MyApplication objMyApplication;
    String strURL = "";
    RelativeLayout layoutLoader;
    CircularProgressIndicator cpProgress;
    WebView mywebview;
    ImageButton closeBtn;
    CustomerProfileViewModel customerProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
            setContentView(R.layout.activity_web_view);
            mywebview = (WebView) findViewById(R.id.webView);
            closeBtn = findViewById(R.id.closeBtn);
            closeBtn.setOnClickListener(view -> finish());
            SignOnData signOnData;
            objMyApplication = (MyApplication) getApplicationContext();
            layoutLoader = findViewById(R.id.layoutLoader);
            cpProgress = findViewById(R.id.cpProgress);

            mywebview.setVisibility(View.GONE);
            layoutLoader.setVisibility(View.VISIBLE);
            cpProgress.show();

            WebSettings webSettings = mywebview.getSettings();
            webSettings.setJavaScriptEnabled(true);


            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            customerProfileViewModel.meSignOn();
            initObserver();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        objMyApplication.setResolveUrl(false);
    }

    public void initObserver() {
        customerProfileViewModel.getSignOnMutableLiveData().observe(this, new Observer<SignOn>() {
            @Override
            public void onChanged(SignOn signOn) {
                try {
                    if (signOn != null) {
                        if (signOn.getStatus().toUpperCase().equals("SUCCESS")) {

                            SignOnData signOnData = signOn.getData();
                            String html = "";
                            String strLogin_acct_id = "", strAcct_id = "", strInvocation_mode = "", strReturn_url = "", strError_url = "";
                            if (signOnData != null) {

                                strLogin_acct_id = signOnData.getLogin_acct_id();
                                strAcct_id = signOnData.getAcct_id();
                                strInvocation_mode = signOnData.getInvocation_mode();
                                strReturn_url = "http://localhost:3000/";
                                strError_url = "http://localhost:3000/";
                                if (signOnData.getUrl() != null) {
                                    strURL = signOnData.getUrl();
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
                                    strURL = signOnData.getResolveUrl();
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
                            }

                            mywebview.loadData(html, "text/html", null);
                            mywebview.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                    super.onPageStarted(view, url, favicon);
                                    try {
                                        if (!isReturn && (url.contains("http://localhost:3000/?fiLoginAcctId") || url.contains("action=cancel"))) {
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


                        } else {
                            displayAlertNew(signOn.getError().getErrorDescription(), WebViewActivity.this, "", customerProfileViewModel);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public static void displayAlertNew(String msg, final Context context, String headerText, CustomerProfileViewModel customerProfileViewModel) {
        // custom dialog
        Dialog displayAlertDialog = new Dialog(context);
        displayAlertDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        displayAlertDialog.setContentView(R.layout.bottom_sheet_alert_dialog);
        displayAlertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView header = displayAlertDialog.findViewById(R.id.tvHead);
        TextView message = displayAlertDialog.findViewById(R.id.tvMessage);
        CardView actionCV = displayAlertDialog.findViewById(R.id.cvAction);
        TextView actionText = displayAlertDialog.findViewById(R.id.tvAction);

        actionText.setText("Retry");

        if (!headerText.equals("")) {
            header.setVisibility(View.VISIBLE);
            header.setText(headerText);
        }

        actionCV.setOnClickListener(view -> {
            customerProfileViewModel.meSignOn();
            displayAlertDialog.dismiss();
        });

        message.setText(msg);
        Window window = displayAlertDialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        displayAlertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        displayAlertDialog.setCanceledOnTouchOutside(true);
        displayAlertDialog.show();
    }

}