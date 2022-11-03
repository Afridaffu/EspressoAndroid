package com.coyni.mapp.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.coyni.mapp.R;
import com.coyni.mapp.databinding.ActivitySignAgreementsBinding;
import com.coyni.mapp.model.SignAgreementsResp;
import com.coyni.mapp.model.States;
import com.coyni.mapp.model.UpdateSignAgreementsResp;
import com.coyni.mapp.model.UpdateSignRequest;
import com.coyni.mapp.model.profile.DownloadDocumentData;
import com.coyni.mapp.model.profile.DownloadDocumentResponse;
import com.coyni.mapp.utils.JavaScriptInterface;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.download.DownloadTask;
import com.coyni.mapp.view.DashboardActivity;
import com.coyni.mapp.view.business.BusinessDashboardActivity;
import com.coyni.mapp.view.business.SignAgreementsActivity;
import com.coyni.mapp.view.business.SignatureActivity;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SignAgreementsDialog extends BaseDialog {
    private Context context;
    private ActivitySignAgreementsBinding binding;
    private DashboardViewModel dashboardViewModel;
    private LoginViewModel loginViewModel;
    private String myUrl = "", MATERIAL = "M", NON_MATERIAL = "N";
    private boolean isActionEnabled = false;
    private MyApplication objMyApplication;
    private int AGREE_TYPE, iterationCount = 0, currentIteration = 0;
    private Long mLastClickTime = 0L;
    private DownloadManager manager;
    private Dialog downloadDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private JavaScriptInterface jsInterface;
    private SignAgreementsResp agrementsResponse;
    private boolean isSignatureCaptured = false;
    private String filePath = null;

    public SignAgreementsDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
//        setContentView(R.layout.authorized_signers_learn_more_dialog);
            binding = DataBindingUtil
                    .inflate(LayoutInflater.from(context), R.layout.activity_sign_agreements, null, false);
            setContentView(binding.getRoot());

//            jsInterface = new JavaScriptInterface(context);
//            WebSettings webSettings = binding.webView.getSettings();
//            webSettings.setJavaScriptEnabled(true);
//            binding.webView.addJavascriptInterface(jsInterface, "JSInterface");
//            webSettings.setDomStorageEnabled(true);
//            webSettings.setLoadWithOverviewMode(true);
//            webSettings.setUseWideViewPort(true);
//            webSettings.setBuiltInZoomControls(true);
//            webSettings.setDisplayZoomControls(false);
//            webSettings.setSupportZoom(true);
//            webSettings.setDefaultTextEncodingName("utf-8");
//
//            initFields();
//            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    private void initFields() {
//        try {
//            showProgressDialog();
//            objMyApplication = (MyApplication) context.getApplicationContext();
//            dashboardViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(DashboardViewModel.class);
//            loginViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(LoginViewModel.class);
//            loginViewModel.hasToSignAgreements();
//            binding.webView.setWebChromeClient(new WebChromeClient() {
//                @Override
//                public boolean onJsAlert(WebView view, final String url, String message, JsResult result) {
//                    Log.e("Bottom page", result.toString());
//                    try {
////                        binding.webView.loadUrl("javascript:clearListener()");
//                        result.cancel();
//                        binding.accknowledgeTV.setTextColor(context.getColor(R.color.black));
//                        binding.actionCV.setVisibility(View.VISIBLE);
//                        binding.actionCV.setClickable(true);
//                        binding.agreeCB.setEnabled(true);
//                        binding.signatureTV.setTextColor(context.getColor(R.color.black));
//                        if (!isSignatureCaptured)
//                            binding.signatureEditIV.setImageDrawable(context.getDrawable(R.drawable.ic_sign));
//                        binding.signatureEditLL.setClickable(true);
//                        binding.signatureEditLL.setEnabled(true);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return true;
//                }
//            });
//
//            binding.webView.setWebViewClient(new WebViewClient() {
//                @Override
//                public void onPageFinished(WebView view, String url) {
//                    dismissDialog();
//                    binding.webView.loadUrl("javascript:showPDF('" + Uri.encode(myUrl) + "')");
//                }
//            });
//
//            binding.agreeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                    if (b) {
//                        isActionEnabled = true;
//                        binding.actionCV.setCardBackgroundColor(context.getResources().getColor(R.color.primary_color));
//                    } else {
//                        isActionEnabled = false;
//                        binding.actionCV.setCardBackgroundColor(context.getResources().getColor(R.color.inactive_color));
//                    }
//                }
//            });
////
//            binding.actionCV.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    try {
//                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                            return;
//                        }
//                        mLastClickTime = SystemClock.elapsedRealtime();
//                        if (isActionEnabled) {
//                            if (AGREE_TYPE == Utils.mAgmt) {
//                                showProgressDialog();
//                                File doc = new File(filePath);
//                                RequestBody requestBody = null;
//                                MultipartBody.Part idFile = null;
//                                requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), doc);
//                                idFile = MultipartBody.Part.createFormData("image", doc.getName(), requestBody);
//                                loginViewModel.signUpdatedAgreementDoc(agrementsResponse.getData().get(currentIteration).getId(), idFile);
//                            } else {
//                                showProgressDialog();
//                                UpdateSignRequest updateSignRequest = new UpdateSignRequest();
//
//                                if (!objMyApplication.getStrDBAName().equals("") && objMyApplication.getStrDBAName() != null)
//                                    updateSignRequest.setSignature(objMyApplication.getStrDBAName());
//                                else if (!objMyApplication.getStrUserName().equals("") && objMyApplication.getStrUserName() != null)
//                                    updateSignRequest.setSignature(objMyApplication.getStrUserName());
//
//                                updateSignRequest.setAgreementId(agrementsResponse.getData().get(currentIteration).getId());
//                                loginViewModel.signUpdatedAgreement(updateSignRequest);
//                            }
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
//
//            binding.signatureEditLL.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                        return;
//                    }
//                    mLastClickTime = SystemClock.elapsedRealtime();
//                    if (!isSignatureCaptured) {
//                        launchSignature();
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void launchSignature() {
////        Intent inSignature = new Intent(context, SignatureActivity.class);
////        activityResultLauncher.launch(inSignature);
//    }

//    ActivityResultLauncher<Intent> activityResultLauncher = getOwnerActivity().registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    getSignature(result.getData());
//                }
//            });

//    private void getSignature(Intent data) {
//        if (data != null) {
//            String filePath = data.getStringExtra(Utils.DATA);
//            File targetFile = new File(filePath);
//            if (targetFile.exists()) {
//                this.filePath = filePath;
//                Bitmap myBitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());
//                LogUtils.v(TAG, "file size " + myBitmap.getByteCount());
//                binding.signatureEditIV.setImageBitmap(myBitmap);
//                isSignatureCaptured = true;
//                binding.actionCV.setVisibility(View.VISIBLE);
//                isActionEnabled = true;
//                binding.actionCV.setCardBackgroundColor(context.getResources().getColor(R.color.primary_color));
////                showProgressDialog();
////                sendSignatureRequest();
//            }
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private void setupLablesAndUI(String materialType, String effectiveDate) {
//        try {
//            if (materialType.equalsIgnoreCase(MATERIAL)) {
//                binding.infoTV.setVisibility(View.GONE);
//                isActionEnabled = false;
//                binding.actionCV.setCardBackgroundColor(context.getResources().getColor(R.color.inactive_color));
//                binding.actionCV.setVisibility(View.INVISIBLE);
//                binding.agreeCB.setVisibility(View.VISIBLE);
//                binding.accknowledgeTV.setVisibility(View.VISIBLE);
//                binding.thankyouTV.setVisibility(View.GONE);
//                binding.actionTV.setText("Agree");
//                if (AGREE_TYPE == Utils.cTOS) {
////                    binding.signatureTV.setTextColor(getColor(R.color.light_gray));
////                    binding.signatureEditIV.setImageDrawable(getDrawable(R.drawable.ic_sign_gray));
////                    binding.signatureEditLL.setClickable(false);
////                    binding.signatureEditLL.setEnabled(false);
////                    binding.signatureEditLL.setVisibility(View.VISIBLE);
////                    binding.agreeCB.setVisibility(View.GONE);
//
//                    binding.signatureEditLL.setVisibility(View.GONE);
//                    binding.agreNameTV.setText(context.getString(R.string.gbx_tos) + " Update");
//                    binding.accknowledgeTV.setText("I agree to the " + context.getString(R.string.gbx_tos) + " changes that take effect on " + effectiveDate);
//                } else if (AGREE_TYPE == Utils.cPP) {
////                    binding.signatureTV.setTextColor(getColor(R.color.light_gray));
////                    binding.signatureEditIV.setImageDrawable(getDrawable(R.drawable.ic_sign_gray));
////                    binding.signatureEditLL.setClickable(false);
////                    binding.signatureEditLL.setEnabled(false);
////                    binding.signatureEditLL.setVisibility(View.VISIBLE);
////                    binding.agreeCB.setVisibility(View.GONE);
//
//                    binding.signatureEditLL.setVisibility(View.GONE);
//                    binding.agreNameTV.setText(context.getString(R.string.gbx_pp) + " Update");
//                    binding.accknowledgeTV.setText("I agree to the " + context.getString(R.string.gbx_pp) + " changes that take effect on " + effectiveDate);
//                } else if (AGREE_TYPE == Utils.mAgmt) {
//                    binding.signatureTV.setTextColor(context.getColor(R.color.light_gray));
//                    binding.signatureEditIV.setImageDrawable(context.getDrawable(R.drawable.ic_sign_gray));
//                    binding.signatureEditLL.setClickable(false);
//                    binding.signatureEditLL.setEnabled(false);
//                    binding.signatureEditLL.setVisibility(View.VISIBLE);
//                    binding.agreeCB.setVisibility(View.GONE);
//
//                    binding.agreNameTV.setText(context.getString(R.string.gbx_merchant) + " Update");
//                    binding.accknowledgeTV.setText("I agree to the " + context.getString(R.string.gbx_merchant) + " changes that take effect on " + effectiveDate);
//                }
//            } else {
//                binding.signatureEditLL.setVisibility(View.GONE);
//                setSpannableText(effectiveDate);
//                binding.infoTV.setVisibility(View.VISIBLE);
//                isActionEnabled = true;
//                binding.actionCV.setCardBackgroundColor(context.getResources().getColor(R.color.primary_color));
//                binding.actionCV.setVisibility(View.VISIBLE);
//                binding.agreeCB.setVisibility(View.GONE);
//                binding.accknowledgeTV.setVisibility(View.GONE);
//                binding.thankyouTV.setVisibility(View.VISIBLE);
//                binding.actionTV.setText("OK");
//                if (AGREE_TYPE == Utils.cTOS)
//                    binding.agreNameTV.setText(context.getString(R.string.gbx_tos) + " Update");
//                else if (AGREE_TYPE == Utils.cPP)
//                    binding.agreNameTV.setText(context.getString(R.string.gbx_pp) + " Update");
//                else if (AGREE_TYPE == Utils.mAgmt)
//                    binding.agreNameTV.setText(context.getString(R.string.gbx_merchant) + " Update");
//            }
//        } catch (Resources.NotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void initObservers() {
//        dashboardViewModel.getDownloadDocumentResponse().observe(this, new Observer<DownloadDocumentResponse>() {
//            @Override
//            public void onChanged(DownloadDocumentResponse downloadDocumentResponse) {
//                try {
//                    dismissDialog();
//                    if (downloadDocumentResponse != null && downloadDocumentResponse.getStatus() != null) {
//                        if (downloadDocumentResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
//                            DownloadDocumentData data = downloadDocumentResponse.getData();
//                            if (data != null) {
//                                if (data.getDownloadUrl() != null && !data.getDownloadUrl().equals("")) {
//                                    myUrl = data.getDownloadUrl();
//                                    binding.webView.setVisibility(View.VISIBLE);
//                                    binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");
//
//                                } else {
//                                    Utils.displayAlert(context.getString(R.string.unable_to_get_document), (Activity) context, "", "");
//                                }
//                            }
//                        } else {
////                            binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");
//
//                            Utils.displayAlert(downloadDocumentResponse.getError().getErrorDescription(), (Activity) context, "", "");
//                        }
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
//
//        loginViewModel.getUpdatedSignAgreementLiveData().observe(this, new Observer<UpdateSignAgreementsResp>() {
//            @Override
//            public void onChanged(UpdateSignAgreementsResp updateSignAgreementsResp) {
//                dismissDialog();
//                if (updateSignAgreementsResp != null) {
//                    if (updateSignAgreementsResp.getStatus().equalsIgnoreCase("success")) {
//                        showToast(context, updateSignAgreementsResp.getData().getMessage(), R.drawable.ic_custom_tick, "");
//                    } else {
//                        Utils.displayAlert(updateSignAgreementsResp.getError().getErrorDescription(), (Activity) context, "", "");
//                    }
//                }
//            }
//        });
//
//        loginViewModel.getUploadSignAgreementLiveData().observe(this, new Observer<UpdateSignAgreementsResp>() {
//            @Override
//            public void onChanged(UpdateSignAgreementsResp updateSignAgreementsResp) {
//                dismissDialog();
//                if (updateSignAgreementsResp != null) {
//                    if (updateSignAgreementsResp.getStatus().equalsIgnoreCase("success")) {
//                        showToast(context, updateSignAgreementsResp.getData().getMessage(), R.drawable.ic_custom_tick, "");
//                    } else {
//                        Utils.displayAlert(updateSignAgreementsResp.getError().getErrorDescription(), (Activity) context, "", "");
//                    }
//                }
//            }
//        });
//
//        loginViewModel.getHasToSignResponseMutableLiveData().observe(this, new Observer<SignAgreementsResp>() {
//            @Override
//            public void onChanged(SignAgreementsResp signAgreementsResp) {
//                if (signAgreementsResp != null) {
//                    agrementsResponse = signAgreementsResp;
//                    if (signAgreementsResp.getStatus().equalsIgnoreCase("success")) {
////                        //block section
////                        String resp = "{\"status\":\"SUCCESS\",\"timestamp\":\"2022-10-26T06:19:40.161+00:00\",\"" +
////                                "data\":[" +
////
////                                "{\"id\":147,\"version\":\"v1.76\",\"startDate\":\"2022-10-25 07:00:00\",\"status\":0," +
////                                "\"effectiveDate\":\"2022-10-26 05:00:00\",\"materialType\":\"M\",\"agreementType\":1," +
////                                "\"agreementFileRefPath\":\"YWdyZWVtZW50cy8xNjY2NzU3NDc1NjE1X0dyZWVuYm94K1BPUytHRFBSK1ByaXZhY3krUG9saWN5LnBkZg==\"}," +
////
////                                "{\"id\":146,\"version\":\"v1.76\",\"startDate\":\"2022-10-25 07:00:00\",\"status\":3,\"" +
////                                "effectiveDate\":\"2022-10-26 05:00:00\",\"materialType\":\"M\",\"agreementType\":0,\"" +
////                                "agreementFileRefPath\":\"YWdyZWVtZW50cy8xNjY2NzU3NDIzNDc4XzEwN19HZW4rMytWMStUT1MrdjYucGRm\"}," +
////                                "" +
////                                "{\"id\":145,\"version\":\"v1.74\",\"startDate\":\"2022-10-22 07:00:00\",\"status\":0,\"" +
////                                "effectiveDate\":\"2022-10-23 07:00:00\",\"materialType\":\"M\",\"agreementType\":0,\"" +
////                                "agreementFileRefPath\":\"YWdyZWVtZW50cy8xNjY2MzM3NjYxMzE5X0dyZWVuQm94LVRlcm1zIG9mIFNlcnZpY2UgQWdyZWVtZW50LnBkZg==\"}" +
////
////                                "],\"error\":null}";
////                        Gson gson = new Gson();
////                        Type type = new TypeToken<SignAgreementsResp>() {
////                        }.getType();
////                        signAgreementsResp = gson.fromJson(resp, type);
////                        agrementsResponse = signAgreementsResp;
////                        Log.e("response", signAgreementsResp.getData().get(0).getAgreementFileRefPath() + "");
////                        //block section
//
//
//                        iterationCount = signAgreementsResp.getData().size();
//                        if (iterationCount > 0) {
//                            iterationCount--;
//                            AGREE_TYPE = signAgreementsResp.getData().get(0).getAgreementType();
//                            setupLablesAndUI(signAgreementsResp.getData().get(0).getMaterialType(),
//                                    Utils.convertEffectiveDate(signAgreementsResp.getData().get(0).getEffectiveDate()));
//                            currentIteration = 0;
//                            dashboardViewModel.getDocumentUrl(AGREE_TYPE);
//                        }
//
//                    }
//                } else {
//                    Utils.displayAlert(signAgreementsResp.getError().getErrorDescription(), (Activity) context, "", "");
//                }
//            }
//        });
//    }
//
//    private void downloadPDFPopup(final Context context) {
//        try {
//            downloadDialog = new Dialog(context);
//            downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            downloadDialog.setContentView(R.layout.download_bottom_sheet);
//            downloadDialog.setCancelable(true);
//            Window window = downloadDialog.getWindow();
//            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//            downloadDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//            WindowManager.LayoutParams wlp = window.getAttributes();
//            wlp.gravity = Gravity.BOTTOM;
//            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//            window.setAttributes(wlp);
//            downloadDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//
//            TextView cancelTV = downloadDialog.findViewById(R.id.cancelTV);
//            LinearLayout downloadLL = downloadDialog.findViewById(R.id.downloadLL);
//
//            cancelTV.setOnClickListener(view -> {
//                downloadDialog.dismiss();
//            });
//
//            downloadLL.setOnClickListener(view -> {
//                downloadDialog.dismiss();
//                final DownloadTask downloadTask = new DownloadTask(context, AGREE_TYPE);
//                downloadTask.execute(myUrl);
//            });
//
//            downloadDialog.show();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        try {
//            switch (requestCode) {
//                case REQUEST_ID_MULTIPLE_PERMISSIONS:
//
//                    if (ContextCompat.checkSelfPermission(context,
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        Utils.displayAlert("Requires Access to Your Storage.", (Activity) context, "", "");
//                    } else if (ContextCompat.checkSelfPermission(context,
//                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        Utils.displayAlert("Requires Access to Your Storage.", SignAgreementsActivity.this, "", "");
//                    } else {
//                        downloadPDFPopup(SignAgreementsActivity.this);
//                    }
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");
//    }
//
//    public class HttpGetRequest extends AsyncTask<String, Void, String> {
//        public static final String REQUEST_METHOD = "GET";
//        public static final int READ_TIMEOUT = 15000;
//        public static final int CONNECTION_TIMEOUT = 15000;
//
//        @Override
//        protected String doInBackground(String... params) {
//            String stringUrl = params[0];
//            String result;
//            String inputLine;
//            try {
//                //Create a URL object holding our url
//                URL myUrl = new URL(objMyApplication.getStrStatesUrl());
//                //Create a connection
//                HttpURLConnection connection = (HttpURLConnection)
//                        myUrl.openConnection();
//                //Set methods and timeouts
//                connection.setRequestMethod(REQUEST_METHOD);
//                connection.setReadTimeout(READ_TIMEOUT);
//                connection.setConnectTimeout(CONNECTION_TIMEOUT);
//
//                //Connect to our url
//                connection.connect();
//                //Create a new InputStreamReader
//                InputStreamReader streamReader = new
//                        InputStreamReader(connection.getInputStream());
//                //Create a new buffered reader and String Builder
//                BufferedReader reader = new BufferedReader(streamReader);
//                StringBuilder stringBuilder = new StringBuilder();
//                //Check if the line we are reading is not null
//                while ((inputLine = reader.readLine()) != null) {
//                    stringBuilder.append(inputLine);
//                }
//                //Close our InputStream and Buffered reader
//                reader.close();
//                streamReader.close();
//                //Set our result equal to our stringBuilder
//                result = stringBuilder.toString();
//            } catch (IOException e) {
//                e.printStackTrace();
//                result = null;
//            }
//            return result;
//        }
//
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<States>>() {
//            }.getType();
//            List<States> listStates = gson.fromJson(result, type);
//            objMyApplication.setListStates(listStates);
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (agrementsResponse.getData().get(currentIteration).getMaterialType().equalsIgnoreCase(NON_MATERIAL) ||
//                (agrementsResponse.getData().get(currentIteration).getStatus() == Utils.SCHEDULED_AGREEMENT)) {
//            launchDashboard();
//        }
//    }
//
//    public void setSpannableText(String date) {
//
//        String agreeName = "";
//        if (AGREE_TYPE == Utils.cTOS)
//            agreeName = getString(R.string.gbx_tos);
//        else if (AGREE_TYPE == Utils.cPP)
//            agreeName = getString(R.string.gbx_pp);
//        else if (AGREE_TYPE == Utils.mAgmt)
//            agreeName = getString(R.string.gbx_merchant);
//
//        String formString = date + ", we’re making some changes to our " + agreeName +
//                " These changes won’t affect the way you use our services, but they’ll make it easier for you to understand what to expect — and what we expect from you — as you use our services. You can review the new terms here. At a glance, here’s what this update means for you:";
//        SpannableString ss = new SpannableString(formString);
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View textView) {
//                Log.e("Click", "click");
//                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                    return;
//                }
//                mLastClickTime = SystemClock.elapsedRealtime();
//            }
//
//            @Override
//            public void updateDrawState(TextPaint ds) {
//                super.updateDrawState(ds);
//                ds.setUnderlineText(true);
//            }
//        };
//
//        int start = ss.toString().indexOf("new terms here"), end = ss.toString().indexOf("new terms here") + 14;
//        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
////        ss.setSpan(new ForegroundColorSpan(Color.parseColor("#00a6a2")), ss.toString().indexOf("new terms here"), ss.toString().indexOf("new terms here") + 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new ForegroundColorSpan(getColor(R.color.primary_green)), ss.toString().indexOf("new terms here"), ss.toString().indexOf("new terms here") + 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        binding.infoTV.setText(ss);
//        binding.infoTV.setMovementMethod(LinkMovementMethod.getInstance());
//        binding.infoTV.setHighlightColor(Color.TRANSPARENT);
//    }
//
//    public void showToast(final Context context, String text, int imageID, String strScreen) {
//        // custom dialog
//        final Dialog dialog = new Dialog(context);
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.custom_toast);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//
//        DisplayMetrics mertics = context.getResources().getDisplayMetrics();
//        int width = mertics.widthPixels;
//
//        Window window = dialog.getWindow();
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
//        WindowManager.LayoutParams wlp = window.getAttributes();
//
//        wlp.gravity = Gravity.TOP;
//        wlp.flags &= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//        window.setAttributes(wlp);
//        TextView textTV = dialog.findViewById(R.id.toastTV);
//        ImageView imageIV = dialog.findViewById(R.id.toastIV);
//        textTV.setText(text);
//        if (imageID == 0) {
//            imageIV.setVisibility(View.GONE);
//        } else {
//            try {
//                imageIV.setVisibility(View.VISIBLE);
//                imageIV.setImageResource(imageID);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.show();
//        toastTimer(dialog);
//    }
//
//    public void toastTimer(Dialog dialog) {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    synchronized (this) {
//                        wait(2000);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    dialog.dismiss();
//                                    if (iterationCount > 0) {
//                                        binding.webView.setVisibility(View.INVISIBLE);
//                                        binding.webView.addJavascriptInterface(jsInterface, "JSInterface");
//                                        binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");
//                                        binding.accknowledgeTV.setTextColor(getColor(R.color.light_gray));
//                                        binding.actionCV.setVisibility(View.INVISIBLE);
//                                        binding.actionCV.setClickable(false);
//                                        binding.agreeCB.setEnabled(false);
//                                        binding.agreeCB.setChecked(false);
//                                        isSignatureCaptured = false;
//
//                                        iterationCount--;
//                                        currentIteration++;
//                                        AGREE_TYPE = agrementsResponse.getData().get(currentIteration).getAgreementType();
//                                        setupLablesAndUI(agrementsResponse.getData().get(currentIteration).getMaterialType(),
//                                                Utils.convertEffectiveDate(agrementsResponse.getData().get(currentIteration).getEffectiveDate()));
//                                        dashboardViewModel.getDocumentUrl(AGREE_TYPE);
//                                    } else {
//                                        launchDashboard();
//                                    }
//                                } catch (Exception ex) {
//                                    ex.printStackTrace();
//                                }
//                            }
//                        });
//
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            ;
//        }.start();
//    }
//
//    public void launchDashboard() {
//        if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT || objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT) {
//            Intent intent = new Intent(SignAgreementsActivity.this, BusinessDashboardActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(SignAgreementsActivity.this, DashboardActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        }
//    }
}
