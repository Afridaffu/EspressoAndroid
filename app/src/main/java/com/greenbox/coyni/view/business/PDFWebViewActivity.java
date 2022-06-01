package com.greenbox.coyni.view.business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.UpdateSignAgree.UpdateSignAgreementsResponse;
import com.greenbox.coyni.model.profile.DownloadDocumentData;
import com.greenbox.coyni.model.profile.DownloadDocumentResponse;
import com.greenbox.coyni.model.signedagreements.SignedAgreementResponse;
import com.greenbox.coyni.utils.DisplayImageUtility;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PDFWebViewActivity extends BaseActivity {
    ImageView  canceledIV;
    TextView agreNameTV;
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

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webView.invalidate();
        webSettings.setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
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
                onBackPressed();
            }
        });
        agreNameTV.setText(getIntent().getStringExtra("NAME"));
        launchDocumentUrl(getIntent().getStringExtra("URL").replace("&","%26"));

    }

    private void launchDocumentUrl(String url) {

        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);

    }
}

