package com.greenbox.coyni.view.business;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
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
import com.greenbox.coyni.model.UpdateSignAgree.UpdateSignAgreementsResponse;
import com.greenbox.coyni.model.signedagreements.SignedAgreementResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

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
    private String filePath = null;
    private boolean isSignatureCaptured = false;
    private WebView webView;
    Long mLastClickTimeQA = 0L;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchants_agrement);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);

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
        String fileURL = "https://crypto-resources.s3.amazonaws.com/Gen-3-V1-Merchant-TOS-v6.pdf";
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + fileURL);
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


        doneCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                sendSignatureRequest();
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
//                showProgressDialog();
//                sendSignatureRequest();
            }
        }
    }

    private void sendSignatureRequest() {
        if (filePath != null) {
            File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("identityFile", file.getName(), requestFile);
            businessDashboardViewModel.signedAgreement(body, 5);
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
                    //businessDashboardViewModel.updateSignedAgree();
                    if (signedAgreementResponse != null) {
                        if (signedAgreementResponse.getStatus() != null
                                && signedAgreementResponse.getStatus().equalsIgnoreCase("Success")) {
                            //If require need to show the Toast to the User.
                            //finish();
                            businessDashboardViewModel.updateSignedAgree();
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
                    if (updateSignAgreementsResponse != null) {
                        if (updateSignAgreementsResponse != null && updateSignAgreementsResponse.getStatus().equalsIgnoreCase("Sucess"))
                            ;
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

