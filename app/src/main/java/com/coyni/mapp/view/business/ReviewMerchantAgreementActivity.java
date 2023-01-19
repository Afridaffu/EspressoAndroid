package com.coyni.mapp.view.business;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
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
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.JavaScriptInterface;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.viewmodel.BusinessDashboardViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ReviewMerchantAgreementActivity extends BaseActivity {

    BusinessDashboardViewModel businessDashboardViewModel;
    DashboardViewModel dashboardViewModel;
    private String filePath = null;
    private boolean isSignatureCaptured = false;
    private JavaScriptInterface jsInterface;
    private String myUrl = "", signatureKey = "";
    boolean isBottom = false, isChecked = false, isDone = false;
    private WebView webView;
    private CardView AgreeDoneCv;
    private TextView signatureTv, savedtextTV;
    private ImageView signatureEditIV;
    private CheckBox agreeCB;
    private LinearLayout signatureEditLL, closeLL, llCheckBox;
    private View viewColorV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_merchant_agreement);
        initfields();
        initObservers();
        showProgressDialog();
        dashboardViewModel.getDocumentUrl(Utils.mAgmt);
    }

    private void initfields() {
        webView = findViewById(R.id.webView);
        AgreeDoneCv = findViewById(R.id.AgreeDoneCv);
        signatureTv = findViewById(R.id.signatureTv);
        savedtextTV = findViewById(R.id.savedtextTV);
        signatureEditIV = findViewById(R.id.signatureEditIV);
        agreeCB = findViewById(R.id.agreeCB);
        signatureEditLL = findViewById(R.id.signatureEditLL);
        closeLL = findViewById(R.id.closeLL);
        llCheckBox = findViewById(R.id.llCheckBox);
        viewColorV = findViewById(R.id.viewColorV);

        jsInterface = new JavaScriptInterface(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(jsInterface, "JSInterface");
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, final String url, String message, JsResult result) {
                Log.e("Bottom page", result.toString());
                result.cancel();
                try {
                    if(!isBottom) {
                        isBottom = true;
                        signatureTv.setTextColor(getColor(R.color.black));
                        viewColorV.setBackgroundColor(getColor(R.color.light_gray));
                        signatureEditIV.setImageResource(R.drawable.ic_sign);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                dismissDialog();
                webView.loadUrl("javascript:showPDF('" + Uri.encode(myUrl) + "')");

            }
        });

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);


        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AgreeDoneCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
//                SignSubmitRequest request = new SignSubmitRequest();
//                request.setDocumentType(Utils.mAgmt);
//                request.setSignature(signatureKey);
//                businessDashboardViewModel.signSubmitApplication(request);
                Intent i = new Intent(ReviewMerchantAgreementActivity.this, BusinessDashboardActivity.class);
                startActivity(i);
//                sendSignatureRequest();

            }
        });


        signatureEditLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSignatureCaptured && isBottom) {
                    launchSignature();
                }
            }
        });

        agreeCB.setOnClickListener(view -> {
            if (!isChecked) {
                isChecked = true;
                llCheckBox.setEnabled(true);
                enableOrDisableNext();
            } else {
                isChecked = false;
                llCheckBox.setEnabled(false);
                enableOrDisableNext();
            }
        });

    }

    private void enableOrDisableNext() {

        if (isSignatureCaptured && isChecked) {
            isDone = true;
            AgreeDoneCv.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
        } else {
            isDone = false;
            AgreeDoneCv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }


    private void launchSignature() {
        Intent inSignature = new Intent(ReviewMerchantAgreementActivity.this, SignatureActivity.class);
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
                AgreeDoneCv.setVisibility(View.VISIBLE);
                llCheckBox.setVisibility(View.VISIBLE);
                savedtextTV.setVisibility(View.INVISIBLE);
                LogUtils.v(TAG, "file size " + myBitmap.getByteCount());
                signatureEditIV.setImageBitmap(myBitmap);
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
                                    ReviewMerchantAgreementActivity.this, "", updateSignAgreementsResponse.getError().getFieldErrors().get(0));

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
                            DisplayImageUtility utility = DisplayImageUtility.getInstance(ReviewMerchantAgreementActivity.this);
                            utility.addImage(agreements.getData().getItems().get(i).getSignature(), signatureEditIV, R.drawable.ic_sign);
                            AgreeDoneCv.setVisibility(View.VISIBLE);
                        } else {
                            AgreeDoneCv.setVisibility(View.GONE);
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
                                Utils.displayAlert(getString(R.string.unable_to_get_document), ReviewMerchantAgreementActivity.this, "", "");
                            }
                        }
                    } else {
                        Utils.displayAlert(downloadDocumentResponse.getError().getErrorDescription(), ReviewMerchantAgreementActivity.this, "", "");
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
        myUrl = url;
//        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + urlNew);
        webView.loadUrl("file:///android_asset/pdfViewerScript.html");

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("onConfigurationChanged", "onConfigurationChanged");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.loadUrl("file:///android_asset/pdfViewerScript.html");
    }

    public void toastTimer(Dialog dialog) {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(3500);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dialog.dismiss();
                                    Intent i = new Intent(ReviewMerchantAgreementActivity.this, BusinessDashboardActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            ;
        }.start();
    }

    public void appCompletedToast(final Context context, String text, int imageID) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_toast);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.TOP;
        wlp.flags &= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        window.setAttributes(wlp);
        TextView textTV = dialog.findViewById(R.id.toastTV);
        ImageView imageIV = dialog.findViewById(R.id.toastIV);
        textTV.setText(text);
        if (imageID == 0) {
            imageIV.setVisibility(View.GONE);
        } else {
            try {
                imageIV.setVisibility(View.VISIBLE);
                imageIV.setImageResource(imageID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        toastTimer(dialog);

    }

}

