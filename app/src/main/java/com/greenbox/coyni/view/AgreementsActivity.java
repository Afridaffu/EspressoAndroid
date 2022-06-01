package com.greenbox.coyni.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.greenbox.coyni.model.AgreementsData;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.model.profile.DownloadDocumentData;
import com.greenbox.coyni.model.profile.DownloadDocumentResponse;
import com.greenbox.coyni.model.profile.DownloadImageData;
import com.greenbox.coyni.model.profile.DownloadImageResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private void processAgreements(AgreementsData data) {
        if (data == null) {
            return;
        }
        List<Item> activeItems = new ArrayList<>();
        List<Item> pastItems = new ArrayList<>();

        if (data.getItems() != null && data.getItems().size() > 0) {
            for (int i = 0; i < data.getItems().size(); i++) {
                Item item = data.getItems().get(i);
                if (!activeItems.contains(item)) {
                    activeItems.add(item);
                } else {
                    for (int count = 0; count < activeItems.size(); count++) {
                        Item existingItem = activeItems.get(count);
                        if (existingItem.getSignatureType() == item.getSignatureType()) {
                            if (existingItem.getDocumentVersion() != null && !existingItem.getDocumentVersion().trim().equals("")
                                    && item.getDocumentVersion() != null && !item.getDocumentVersion().trim().equals("")) {
                                double existingVersion = Double.parseDouble(existingItem.getDocumentVersion().toLowerCase().replace("v", "").replace(" ", "").trim());
                                double itemVersion = Double.parseDouble(item.getDocumentVersion().toLowerCase().replace("v", "").replace(" ", "").trim());
                                if (existingVersion < itemVersion) {
                                    pastItems.add(existingItem);
                                    activeItems.remove(existingItem);
                                    activeItems.add(item);
                                } else {
                                    pastItems.add(item);
                                }
                            }
                        }
                    }
                }
            }
        }

        ArrayList<Integer> sortTODisplay = new ArrayList<>();
        if(objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
            sortTODisplay.add(Utils.cPP);
            sortTODisplay.add(Utils.cTOS);
        } else {
            sortTODisplay.add(Utils.mPP);
            sortTODisplay.add(Utils.mTOS);
            sortTODisplay.add(Utils.mAgmt);
        }
        Collections.sort(activeItems, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                int o1Index = sortTODisplay.indexOf(o1.getSignatureType());
                int o2Index = sortTODisplay.indexOf(o2.getSignatureType());
                if(o1Index > o2Index) {
                    return 1;
                } else if(o1Index < o2Index) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        adapter = new AgreeListAdapter(AgreementsActivity.this, activeItems, listener);
        recyclerView.setAdapter(adapter);

        if (activeItems.size() > 0) {
            findViewById(R.id.cvActive).setVisibility(View.VISIBLE);
        }

        if (pastItems.size() > 0) {
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

    private void initObserver() {
        try {
            dashboardViewModel.getAgreementsMutableLiveData().observe(this, new Observer<Agreements>() {
                @Override
                public void onChanged(Agreements agreements) {
                    try {
                        dismissDialog();
                        LogUtils.v(TAG, agreements.getStatus());
                        if (agreements.getStatus().contains(Utils.SUCCESS)) {
                            processAgreements(agreements.getData());
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
                        adapter = new AgreeListAdapter(AgreementsActivity.this, agreements.getData().getItems(), listener);
                        recyclerView.setAdapter(adapter);
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
                            if(data != null ) {
                                if (data.getDownloadUrl() != null && !data.getDownloadUrl().equals("")) {
                                    launchDocumentUrl(data.getDownloadUrl());
                                } else {
                                    Utils.displayAlert(getString(R.string.unable_to_get_document), AgreementsActivity.this, "", "");
                                }
                            }
                        } else {
                            Utils.displayAlert(downloadDocumentResponse.getError().getErrorDescription(), AgreementsActivity.this, "", "");
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
        intent.setDataAndType(uri, "application/pdf");
        startActivity(intent);
    }

    private void setOnClickListener() {
        try {
            listener = (view, doc) -> {
                showProgressDialog();
                dashboardViewModel.getDocumentUrl(doc);
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}