package com.coyni.mapp.view.business;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.mapp.R;
import com.coyni.mapp.databinding.ActivityAcceptAgreementsBinding;
import com.coyni.mapp.model.States;
import com.coyni.mapp.model.UpdateSignAgreementsResp;
import com.coyni.mapp.model.UpdateSignRequest;
import com.coyni.mapp.model.profile.DownloadDocumentData;
import com.coyni.mapp.model.profile.DownloadDocumentResponse;
import com.coyni.mapp.model.register.OTPValidateResponse;
import com.coyni.mapp.model.register.SignAgreementRequest;
import com.coyni.mapp.model.register.SignAgreementResponse;
import com.coyni.mapp.utils.JavaScriptInterface;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.utils.download.DownloadTask;
import com.coyni.mapp.view.BaseActivity;
import com.coyni.mapp.view.OTPValidation;
import com.coyni.mapp.viewmodel.DashboardViewModel;
import com.coyni.mapp.viewmodel.LoginViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class AcceptAgreementsActivity extends BaseActivity {

    private ActivityAcceptAgreementsBinding binding;
    private DashboardViewModel dashboardViewModel;
    private LoginViewModel loginViewModel;
    private String myUrl = "", ACT_TYPE, DOC_NAME = "", DOC_URL = "", SCREEN = "";
    private boolean isActionEnabled = false;
    private MyApplication objMyApplication;
    private int AGREE_TYPE;
    private Long mLastClickTime = 0L;
    private DownloadManager manager;
    private Dialog downloadDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private JavaScriptInterface jsInterface;
//    private boolean canEnableCheckBox = false;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = ActivityAcceptAgreementsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            jsInterface = new JavaScriptInterface(this);
            WebSettings webSettings = binding.webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            binding.webView.addJavascriptInterface(jsInterface, "JSInterface");
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(true);
            webSettings.setDefaultTextEncodingName("utf-8");

            initFields();
            initObservers();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initFields() {
        try {
            showProgressDialog();
            AGREE_TYPE = getIntent().getIntExtra(Utils.AGREEMENT_TYPE, -1);
            ACT_TYPE = getIntent().getStringExtra(Utils.ACT_TYPE);
            DOC_NAME = getIntent().getStringExtra(Utils.DOC_NAME);
            DOC_URL = getIntent().getStringExtra(Utils.DOC_URL);
            SCREEN = getIntent().getStringExtra(Utils.SCREEN);

            setupLablesAndUI();

            objMyApplication = (MyApplication) getApplicationContext();
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            if (DOC_URL != null && !DOC_URL.equals("")) {
                myUrl = DOC_URL;
                binding.webView.setVisibility(View.VISIBLE);
                binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");
            } else {
                dashboardViewModel.getDocumentUrl(AGREE_TYPE);
            }

            binding.webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, final String url, String message, JsResult result) {
                    Log.e("Bottom page", result.toString());
                    try {
//                        binding.webView.loadUrl("javascript:clearListener()");
                        result.cancel();
                        binding.accknowledgeTV.setTextColor(getColor(R.color.black));
                        binding.actionCV.setVisibility(View.VISIBLE);
                        binding.actionCV.setClickable(true);
                        binding.agreeCB.setEnabled(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            binding.webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    dismissDialog();
                    binding.webView.loadUrl("javascript:showPDF('" + Uri.encode(myUrl) + "')");

                }
            });

            binding.closeLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            binding.agreeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        isActionEnabled = true;
                        binding.actionCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                    } else {
                        isActionEnabled = false;
                        binding.actionCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }
                }
            });

            binding.actionCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (isActionEnabled) {
//                            canEnableCheckBox = false;
                            SignAgreementRequest request = new SignAgreementRequest();
                            if (binding.actionTV.getText().toString().equalsIgnoreCase("Done")) {
                                setResult(RESULT_OK);
                                finish();
                            } else if (binding.actionTV.getText().toString().equalsIgnoreCase("I Agree")) {
                                showProgressDialog();
                                UpdateSignRequest updateSignRequest = new UpdateSignRequest();

                                if (objMyApplication.getStrDBAName() != null && !objMyApplication.getStrDBAName().equals(""))
                                    updateSignRequest.setSignature(objMyApplication.getStrDBAName());
                                else if (objMyApplication.getStrUserName() != null && !objMyApplication.getStrUserName().equals(""))
                                    updateSignRequest.setSignature(objMyApplication.getStrUserName());

                                updateSignRequest.setUserId(objMyApplication.getLoginUserId());
                                updateSignRequest.setDocId(Integer.parseInt(getIntent().getStringExtra(Utils.REF_ID)));
                                loginViewModel.signTrackerAgreement(updateSignRequest);
                            } else if (binding.actionTV.getText().toString().equalsIgnoreCase("Next")) {
                                binding.webView.setVisibility(View.INVISIBLE);
                                showProgressDialog();
                                request.setToken(objMyApplication.getStrRegisToken());
                                request.setAgreementType(Utils.cTOS);
                                loginViewModel.signAgreementTOS(request);
                            } else {
                                showProgressDialog();
                                request.setToken(objMyApplication.getStrRegisToken());
                                request.setAgreementType(Utils.cPP);
                                loginViewModel.signAgreementPP(request);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            binding.optionsIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    if (!myUrl.equals("")) {
                        if (Utils.checkAndRequestStoragePermission(AcceptAgreementsActivity.this))
                            downloadPDFPopup(AcceptAgreementsActivity.this);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void setupLablesAndUI() {
        if (AGREE_TYPE == Utils.cTOS) {
            binding.agreNameTV.setText(getString(R.string.gbx_tos));
            binding.infoTV.setText(getString(R.string.read_agree_text) + " " + getString(R.string.tos) + ".");
            binding.accknowledgeTV.setText(getString(R.string.read_accept_text) + " " + getString(R.string.tos) + " Agreement");
        } else if (AGREE_TYPE == Utils.cPP) {
            binding.agreNameTV.setText(getString(R.string.gbx_pp));
            binding.infoTV.setText(getString(R.string.read_agree_text) + " " + getString(R.string.pp) + ".");
            binding.accknowledgeTV.setText(getString(R.string.read_accept_text) + " " + getString(R.string.pp) + " Agreement");
        } else if (AGREE_TYPE == Utils.mAgmt) {
            binding.agreNameTV.setText(getString(R.string.gbx_merchant));
            binding.infoTV.setText(getString(R.string.read_agree_text) + " " + getString(R.string.m_agree));
            binding.accknowledgeTV.setText(getString(R.string.read_accept_text) + " " + getString(R.string.m_agree));
        }

        if (ACT_TYPE.equals(Utils.single)) {
            if (DOC_URL != null && !DOC_URL.equals("")) {
                binding.optionsIV.setVisibility(View.VISIBLE);
                binding.agreNameTV.setText(DOC_NAME);
                binding.bottomLL.setVisibility(View.GONE);
                binding.infoLL.setVisibility(View.GONE);
                binding.closeIV.setImageDrawable(getDrawable(R.drawable.ic_back));
            } else {
                binding.optionsIV.setVisibility(View.INVISIBLE);
                binding.closeIV.setImageDrawable(getDrawable(R.drawable.ic_close));
            }
            if (!SCREEN.equals(""))
                binding.actionTV.setText("I Agree");
            else
                binding.actionTV.setText("Done");
        } else if (ACT_TYPE.equals(Utils.multiple)) {
            binding.optionsIV.setVisibility(View.VISIBLE);
            binding.closeIV.setImageDrawable(getDrawable(R.drawable.ic_close));
            binding.actionTV.setText("Next");
        }
    }

    private void initObservers() {
        dashboardViewModel.getDownloadDocumentResponse().observe(this, new Observer<DownloadDocumentResponse>() {
            @Override
            public void onChanged(DownloadDocumentResponse downloadDocumentResponse) {
                try {
                    dismissDialog();
                    if (downloadDocumentResponse != null && downloadDocumentResponse.getStatus() != null) {
                        if (downloadDocumentResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            DownloadDocumentData data = downloadDocumentResponse.getData();
                            if (data != null) {
                                if (data.getDownloadUrl() != null && !data.getDownloadUrl().equals("")) {
                                    myUrl = data.getDownloadUrl();
                                    binding.webView.setVisibility(View.VISIBLE);
                                    binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");

                                } else {
                                    Utils.displayAlert(getString(R.string.unable_to_get_document), AcceptAgreementsActivity.this, "", "");
                                }
                            }
                        } else {
//                            binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");
                            myUrl = "";
                            binding.webView.setVisibility(View.INVISIBLE);
                            Utils.displayAlert(downloadDocumentResponse.getError().getErrorDescription(), AcceptAgreementsActivity.this, "", "");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getSignAgreementTOSotpLiveData().observe(this, new Observer<OTPValidateResponse>() {
            @Override
            public void onChanged(OTPValidateResponse otpValidateResponse) {
                try {
                    if (otpValidateResponse != null && otpValidateResponse.getStatus().toLowerCase().equals("success")) {
                        binding.webView.addJavascriptInterface(jsInterface, "JSInterface");
                        binding.webView.setVisibility(View.VISIBLE);
                        binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");
                        AGREE_TYPE = Utils.cPP;
                        setupLablesAndUI();
                        dashboardViewModel.getDocumentUrl(AGREE_TYPE);
                        objMyApplication.setStrRegisToken(otpValidateResponse.getData().getToken());
                        binding.actionTV.setText("Finish Signup");
                        binding.accknowledgeTV.setTextColor(getColor(R.color.light_gray));
                        binding.actionCV.setVisibility(View.INVISIBLE);
                        binding.actionCV.setClickable(false);
                        binding.agreeCB.setEnabled(false);
                        binding.agreeCB.setChecked(false);
                    } else {
                        dismissDialog();
                        Utils.displayAlert(otpValidateResponse.getError().getErrorDescription(), AcceptAgreementsActivity.this, "", otpValidateResponse.getError().getFieldErrors().get(0));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getSignAgreementPPotpLiveData().observe(this, new Observer<SignAgreementResponse>() {
            @Override
            public void onChanged(SignAgreementResponse signAgreementResponse) {
                try {
                    dismissDialog();
                    if (signAgreementResponse != null && signAgreementResponse.getStatus().toLowerCase().equals("success")) {
                        Utils.setStrAuth(signAgreementResponse.getData().getJwtToken());
                        objMyApplication.setLoginUserId(Integer.parseInt(signAgreementResponse.getData().getUserId() + ""));
                        getStatesUrl(signAgreementResponse.getData().getStateList().getUS());
                        Intent i = new Intent(AcceptAgreementsActivity.this, OTPValidation.class);
                        i.putExtra("screen", "SignUp");
                        i.putExtra("OTP_TYPE", "SECURE");
                        startActivity(i);
                    } else {
                        Utils.displayAlert(signAgreementResponse.getError().getErrorDescription(), AcceptAgreementsActivity.this, "", signAgreementResponse.getError().getFieldErrors().get(0));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        loginViewModel.getSignTrackerAgreementLiveData().observe(this, new Observer<UpdateSignAgreementsResp>() {
            @Override
            public void onChanged(UpdateSignAgreementsResp updateSignAgreementsResp) {
                try {
                    dismissDialog();
                    if (updateSignAgreementsResp != null && updateSignAgreementsResp.getStatus().toLowerCase().equals("success")) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private void downloadPDFPopup(final Context context) {
        try {
            downloadDialog = new Dialog(context);
            downloadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            downloadDialog.setContentView(R.layout.download_bottom_sheet);
            downloadDialog.setCancelable(true);
            Window window = downloadDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            downloadDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            downloadDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            TextView cancelTV = downloadDialog.findViewById(R.id.cancelTV);
            LinearLayout downloadLL = downloadDialog.findViewById(R.id.downloadLL);

            cancelTV.setOnClickListener(view -> {
                downloadDialog.dismiss();
            });

            downloadLL.setOnClickListener(view -> {
                downloadDialog.dismiss();
                final DownloadTask downloadTask = new DownloadTask(AcceptAgreementsActivity.this, AGREE_TYPE);
                downloadTask.execute(myUrl);
            });

            downloadDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            switch (requestCode) {
                case REQUEST_ID_MULTIPLE_PERMISSIONS:

                    if (ContextCompat.checkSelfPermission(AcceptAgreementsActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", AcceptAgreementsActivity.this, "", "");
                    } else if (ContextCompat.checkSelfPermission(AcceptAgreementsActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", AcceptAgreementsActivity.this, "", "");
                    } else {
                        downloadPDFPopup(AcceptAgreementsActivity.this);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.webView.loadUrl("file:///android_asset/pdfViewerScript.html");
    }

    private void getStatesUrl(String strCode) {
        try {
            byte[] valueDecoded = new byte[0];
            valueDecoded = Base64.decode(strCode.getBytes("UTF-8"), Base64.DEFAULT);
            objMyApplication.setStrStatesUrl(new String(valueDecoded));
            Log.e("States url", objMyApplication.getStrStatesUrl() + "   sdssd");
            try {
                new HttpGetRequest().execute("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected String doInBackground(String... params) {
            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(objMyApplication.getStrStatesUrl());
                //Create a connection
                HttpURLConnection connection = (HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(result, type);
            objMyApplication.setListStates(listStates);
        }
    }

}

