package com.coyni.mapp.view.business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.model.Agreements;
import com.coyni.mapp.model.UpdateSignAgree.UpdateSignAgreementsResponse;
import com.coyni.mapp.model.profile.DownloadDocumentData;
import com.coyni.mapp.model.profile.DownloadDocumentResponse;
import com.coyni.mapp.model.signedagreements.SignedAgreementResponse;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.BusinessDashboardViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MerchantsAgrementActivity extends BaseActivity {
    public CardView doneCV, signaturDoneCV;
    LinearLayout signatureEditLl;
    ImageView mIVSignature, canceledIV;
    TextView savedText;
    BusinessDashboardViewModel businessDashboardViewModel;
    DashboardViewModel dashboardViewModel;
    private String filePath = null;
    private boolean isSignatureCaptured = false;
    private WebView webView;
    Long mLastClickTimeQA = 0L;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_merchants_agrement);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        initObservers();

        doneCV = findViewById(R.id.AgreeDoneCv);
        signaturDoneCV = findViewById(R.id.tv_signature_done);
        signatureEditLl = findViewById(R.id.signatureEditLL);
        mIVSignature = findViewById(R.id.signatureEditIV);
        savedText = findViewById(R.id.savedtextTV);
        canceledIV = findViewById(R.id.canceledIV);

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webView.invalidate();
        webSettings.setJavaScriptEnabled(true);
        webView.setVerticalScrollBarEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        showProgressDialog();
        dashboardViewModel.getDocumentUrl(Utils.mAgmt);

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


        doneCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                businessDashboardViewModel.updateSignedAgree();
            }
        });


        signatureEditLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSignatureCaptured) {
                    launchSignature();
                }
            }
        });

        //dashboardViewModel.meAgreementsById();
    }

    private void launchSignature() {
        Intent inSignature = new Intent(MerchantsAgrementActivity.this, SignatureActivity.class);
        activityResultLauncher.launch(inSignature);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    getSignature(result.getData());
                }
            });

    private void getSignature(Intent data) {
        if (data != null) {
            String filePath = data.getStringExtra(Utils.DATA);
            File targetFile = new File(filePath);
            if (targetFile.exists()) {
                this.filePath = filePath;
                Bitmap myBitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
                doneCV.setVisibility(View.VISIBLE);
                savedText.setVisibility(View.INVISIBLE);
                LogUtils.v(TAG, "file size " + myBitmap.getByteCount());
                mIVSignature.setImageBitmap(myBitmap);
                isSignatureCaptured = true;
                showProgressDialog();
                sendSignatureRequest();
            }
        }
    }

    private void sendSignatureRequest() {
        if (filePath != null) {
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("identityFile", file.getName(), requestFile);
            businessDashboardViewModel.signedAgreement(body, Utils.mAgmt);
        } else {
            dismissDialog();
            LogUtils.v(TAG, "File path is null");
        }
    }

    private void deleteTemporarySignatureFile() {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            filePath = null;
        }
    }

    private void initObservers() {

        businessDashboardViewModel.getSignedAgreementResponseMutableLiveData().observe(this, new Observer<SignedAgreementResponse>() {
            @Override
            public void onChanged(SignedAgreementResponse signedAgreementResponse) {
                try {
                    deleteTemporarySignatureFile();
                    dismissDialog();
                    if (signedAgreementResponse != null) {
                        if (signedAgreementResponse.getStatus() != null
                                && signedAgreementResponse.getStatus().equalsIgnoreCase("Success")) {
                        } else {
                            String errorMessage = getString(R.string.something_went_wrong);
                            if (signedAgreementResponse.getError() != null
                                    && signedAgreementResponse.getError().getErrorDescription() != null) {
                                errorMessage = signedAgreementResponse.getError().getErrorDescription();
                            }
                            Utils.displayAlert(errorMessage,
                                    MerchantsAgrementActivity.this, "", signedAgreementResponse.getError().getFieldErrors().get(0));
                        }
                    } else {
                        LogUtils.v(TAG, "signedAgreementResponse is null");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        businessDashboardViewModel.getUpdateSignAgreementsResponseMutableLiveData().observe(this, new Observer<UpdateSignAgreementsResponse>() {
            @Override
            public void onChanged(UpdateSignAgreementsResponse updateSignAgreementsResponse) {
                try {
                    dismissDialog();
                    if (updateSignAgreementsResponse != null) {
                        if (updateSignAgreementsResponse.getStatus() != null
                                && updateSignAgreementsResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            finish();
                        } else {
                            String errorMessage = getString(R.string.something_went_wrong);
                            if (updateSignAgreementsResponse.getError() != null
                                    && updateSignAgreementsResponse.getError().getErrorDescription() != null) {
                                errorMessage = updateSignAgreementsResponse.getError().getErrorDescription();
                            }
                            Utils.displayAlert(errorMessage,
                                    MerchantsAgrementActivity.this, "", updateSignAgreementsResponse.getError().getFieldErrors().get(0));

                        }
                    } else {
                        LogUtils.v(TAG, "updateSignAgreementsResponse is null");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dashboardViewModel.getAgreementsMutableLiveData().observe(this, new Observer<Agreements>() {
            @Override
            public void onChanged(Agreements agreements) {
                LogUtils.v(TAG, agreements + "");
                if (agreements.getStatus() != null && agreements.getStatus().equalsIgnoreCase("Success")) {
                    for (int i = 0; i < agreements.getData().getItems().size(); i++) {
                        if (agreements.getData().getItems().get(i).getSignatureType() == 5
                                && agreements.getData().getItems().get(i).getSignature() != null
                                && android.util.Patterns.WEB_URL.matcher(agreements.getData().getItems().get(i).getSignature()).matches()) {
                            DisplayImageUtility utility = DisplayImageUtility.getInstance(MerchantsAgrementActivity.this);
                            utility.addImage(agreements.getData().getItems().get(i).getSignature(), mIVSignature, R.drawable.ic_sign);
                            doneCV.setVisibility(View.VISIBLE);
                        } else {
                            doneCV.setVisibility(View.GONE);
                        }
                    }
                } else {
                    LogUtils.v(TAG, "Agreements Response is null");
                }
            }
        });

        dashboardViewModel.getDownloadDocumentResponse().observe(this, new Observer<DownloadDocumentResponse>() {
            @Override
            public void onChanged(DownloadDocumentResponse downloadDocumentResponse) {
                dismissDialog();
                if (downloadDocumentResponse != null && downloadDocumentResponse.getStatus() != null) {
                    if (downloadDocumentResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        DownloadDocumentData data = downloadDocumentResponse.getData();
                        if (data != null) {
                            if (data.getDownloadUrl() != null && !data.getDownloadUrl().equals("")) {
                                launchDocumentUrl(data.getDownloadUrl());
                            } else {
                                Utils.displayAlert(getString(R.string.unable_to_get_document), MerchantsAgrementActivity.this, "", "");
                            }
                        }
                    } else {
                        Utils.displayAlert(downloadDocumentResponse.getError().getErrorDescription(), MerchantsAgrementActivity.this, "", "");
                    }
                }
            }
        });

    }

    private void launchDocumentUrl(String url) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        Uri uri = Uri.parse(url);
//        intent.setDataAndType(uri, "application/pdf");
//        startActivity(intent);
        String urlNew = Uri.encode(url);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + urlNew);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("onConfigurationChanged", "onConfigurationChanged");
    }
}

