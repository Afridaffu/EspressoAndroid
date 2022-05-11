package com.greenbox.coyni.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AgreeListAdapter;
import com.greenbox.coyni.adapters.PastAgreeListAdapter;
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.model.profile.DownloadImageData;
import com.greenbox.coyni.model.profile.DownloadImageResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.business.ReviewApplicationActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;


public class AgreementsActivity extends BaseActivity {
    DashboardViewModel dashboardViewModel;
    LinearLayout backIV;
    RecyclerView recyclerView, recyclPastAgree;
    AgreeListAdapter adapter;
    PastAgreeListAdapter pastAdapter;
    CardView noPastAgree, cvPast;
    LinearLayoutManager linearLayoutManager;
    AgreeListAdapter.RecyclerClickListener listener;
    String status;
    String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
    String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";
    String merchantagreeURL = "https://crypto-resources.s3.amazonaws.com/Gen-3-V1-Merchant-TOS-v6.pdf";
    Agreements agreements;
    MyApplication objMyApplication;
    TextView pastTV, activeTV;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        showProgressDialog();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
//        dashboardViewModel.agreementsByType("1");
        setContentView(R.layout.activity_agreements);
        recyclerView = findViewById(R.id.recyclerview);
        recyclPastAgree = findViewById(R.id.recyclPastAgree);
        noPastAgree = findViewById(R.id.noPastCV);
        cvPast = findViewById(R.id.cvPast);
        backIV = findViewById(R.id.backAgreeIV);
        pastTV = findViewById(R.id.pastTV);
        activeTV = findViewById(R.id.activeTV);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        dashboardViewModel.meAgreementsById();
        //dashboardViewModel.agreementsByType(String.valueOf(Utils.mPP));
        initObserver();
        objMyApplication = (MyApplication) getApplicationContext();

        setOnClickListener();

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initObserver() {
        try {
            dashboardViewModel.getAgreementsMutableLiveData().observe(this, new Observer<Agreements>() {
                @Override
                public void onChanged(Agreements agreements) {
                    try {
                        dismissDialog();
                        Log.e("act", agreements.getStatus());
                        if (agreements.getStatus().contains("SUCCESS")) {
                            List<Item> activeItems = new ArrayList<>();
                            List<Item> pastItems = new ArrayList<>();
                            List<Integer> versions = new ArrayList<>();
                            int cPPVersion = 0, cTSVersion = 0, bMAVersion = 0;
                            if (agreements.getData().getItems() != null && agreements.getData().getItems().size() > 0) {
                                for (int i = 0; i < agreements.getData().getItems().size(); i++) {
                                    if (agreements.getData().getItems().get(i).getSignatureType() == Utils.mTOS) {
                                        if (cTSVersion == 0) {
                                            cTSVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim());
                                        } else {
                                            if (cTSVersion < Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim())) {
                                                cTSVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim());
                                            }
                                        }
                                    }
                                    if (agreements.getData().getItems().get(i).getSignatureType() == Utils.mPP) {
                                        if (cPPVersion == 0) {
                                            cPPVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim());
                                        } else {
                                            if (cPPVersion < Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim())) {
                                                cPPVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim());
                                            }
                                        }
                                    }
                                    if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                                        if (agreements.getData().getItems().get(i).getSignatureType() == Utils.mAgmt) {
                                            if (bMAVersion == 0) {
                                                bMAVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim());
                                            } else {
                                                if (bMAVersion < Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim())) {
                                                    bMAVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim());
                                                }
                                            }
                                        }
                                    }
                                }

                                Item privacyPolicy = new Item();
                                Item tos = new Item();
                                Item merchantAgre = new Item();

                                for (int i = 0; i < agreements.getData().getItems().size(); i++) {
                                    if (agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().contains("v")) {
                                        versions.add(Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim()));
                                    }
                                    if (agreements.getData().getItems().get(i).getDocumentVersion().contains("v") && objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                                        versions.add(Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().toLowerCase().replace("v", "").replace(".", "").trim()));
                                    }
                                    int signatureType = agreements.getData().getItems().get(i).getSignatureType();
                                    if (cTSVersion == versions.get(i) && (signatureType == Utils.mTOS || signatureType == Utils.cTOS)) {
                                        activeItems.add(agreements.getData().getItems().get(i));
                                        tos = agreements.getData().getItems().get(i);
                                    } else if (cPPVersion == versions.get(i) && (signatureType == Utils.mPP || signatureType == Utils.cPP)) {
                                        activeItems.add(agreements.getData().getItems().get(i));
                                        privacyPolicy = agreements.getData().getItems().get(i);
                                    } else if (bMAVersion == versions.get(i) && signatureType == Utils.mAgmt) {
                                        activeItems.add(agreements.getData().getItems().get(i));
                                        merchantAgre = agreements.getData().getItems().get(i);
                                    } else {
                                        pastItems.add(agreements.getData().getItems().get(i));
                                    }
                                }

                                activeItems = new ArrayList<>();
                                activeItems.add(privacyPolicy);
                                activeItems.add(tos);
                                if (objMyApplication.getAccountType() == Utils.BUSINESS_ACCOUNT) {
                                    activeItems.add(merchantAgre);
                                }
                            }

                            adapter = new AgreeListAdapter(AgreementsActivity.this, activeItems, dashboardViewModel, listener);
                            recyclerView.setAdapter(adapter);

                            if (activeItems != null && activeItems.size() > 0) {
                                findViewById(R.id.cvActive).setVisibility(View.VISIBLE);
                            }

                            if (pastItems != null && pastItems.size() > 0) {
                                cvPast.setVisibility(View.VISIBLE);
                                pastTV.setVisibility(View.VISIBLE);
                                activeTV.setVisibility(View.VISIBLE);
                                findViewById(R.id.cvActive).setVisibility(View.VISIBLE);
                                pastAdapter = new PastAgreeListAdapter(pastItems, AgreementsActivity.this);
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(AgreementsActivity.this);
                                recyclPastAgree.setLayoutManager(mLayoutManager);
                                recyclPastAgree.setItemAnimator(new DefaultItemAnimator());
                                recyclPastAgree.setAdapter(pastAdapter);
                            } else {
                                pastTV.setVisibility(View.GONE);
                                cvPast.setVisibility(View.GONE);
                                activeTV.setVisibility(View.GONE);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            dashboardViewModel.getAgreementsPdfMutableLiveData().observe(this, new Observer<AgreementsPdf>() {
                @Override
                public void onChanged(AgreementsPdf agreementsPdf) {
                    if (agreementsPdf.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        adapter = new AgreeListAdapter(AgreementsActivity.this, agreements.getData().getItems(), dashboardViewModel, listener);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });

            dashboardViewModel.getDownloadUrlResponse().observe(this, new Observer<DownloadImageResponse>() {
                @Override
                public void onChanged(DownloadImageResponse downloadImageResponse) {
                    dismissDialog();
                    if(downloadImageResponse != null && downloadImageResponse.getStatus() != null) {
                        if(downloadImageResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            DownloadImageData data = downloadImageResponse.getData();
                            if(data != null && data.getDownloadUrl() != null && !data.getDownloadUrl().equals("")) {
                                launchDocumentUrl(data.getDownloadUrl());
                            }
                        } else {
                            Utils.displayAlert(downloadImageResponse.getError().getErrorDescription(), AgreementsActivity.this, "", "");
                        }
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void launchDocumentUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        intent.setDataAndType(uri,"application/pdf");
        startActivity(intent);
    }

    private void setOnClickListener() {
        try {
            listener = (view, doc) -> {
                showProgressDialog();
                dashboardViewModel.getDocumentUrl(doc);
//                switch (position) {
//                    case 0:
//                        dashboardViewModel.getDocumentUrl(1);
//                        break;
//                    case 1:
//                        dashboardViewModel.getDocumentUrl(0);
//                        break;
//                    case 2:
//                        dashboardViewModel.getDocumentUrl(5);
//                        break;
//                }

            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}