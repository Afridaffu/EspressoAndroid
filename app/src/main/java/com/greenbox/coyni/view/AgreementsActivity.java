package com.greenbox.coyni.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AgreeListAdapter;
import com.greenbox.coyni.adapters.PastAgreeListAdapter;
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.Item;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AgreementsActivity extends AppCompatActivity {
    DashboardViewModel dashboardViewModel;
    LinearLayout backIV;
    RecyclerView recyclerView, recyclerpastAgree;
    AgreeListAdapter adapter;
    PastAgreeListAdapter pastAdapter;
    CardView noPastAgree;
    LinearLayoutManager linearLayoutManager;
    AgreeListAdapter.RecyclerClickListener listener;
    PastAgreeListAdapter.RecyclerClickListener pastListener;
    String status;
    String privacyURL = "https://crypto-resources.s3.amazonaws.com/Greenbox+POS+GDPR+Privacy+Policy.pdf";
    String tosURL = "https://crypto-resources.s3.amazonaws.com/Gen+3+V1+TOS+v6.pdf";
    Agreements agreements;
    MyApplication objMyApplication;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        setContentView(R.layout.activity_agreements);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerpastAgree = findViewById(R.id.recyclPastAgree);
        noPastAgree = findViewById(R.id.noPastCV);
        backIV = findViewById(R.id.backAgreeIV);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        initObserver();
        dashboardViewModel.meAgreementsById();

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
                        Log.e("act", agreements.getStatus());
                        if (agreements.getStatus().contains("SUCCESS")) {
                            List<Item> activeItems = new ArrayList<>();
                            List<Item> pastItems = new ArrayList<>();
                            int cPPVersion = 0, cTSVersion = 0;
                            if (agreements.getData().getItems() != null && agreements.getData().getItems().size() > 0) {
                                for (int i = 0; i < agreements.getData().getItems().size(); i++) {
                                    if (agreements.getData().getItems().get(i).getSignatureType() == 0) {
                                        if (cTSVersion == 0) {
                                            cTSVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().replace("V ", "").replace(".", "").trim());
                                        } else {
                                            if (cTSVersion < Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().replace("V ", "").replace(".", "").trim())) {
                                                cTSVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().replace("V ", "").replace(".", "").trim());
                                            }
                                        }
                                    } else {
                                        if (cPPVersion == 0) {
                                            cPPVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().replace("V ", "").replace(".", "").trim());
                                        } else {
                                            if (cPPVersion < Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().replace("V ", "").replace(".", "").trim())) {
                                                cPPVersion = Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().replace("V ", "").replace(".", "").trim());
                                            }
                                        }
                                    }
                                }
                                for (int i = 0; i < agreements.getData().getItems().size(); i++) {
                                    if (cTSVersion == Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().replace("V ", "").replace(".", "").trim()) && agreements.getData().getItems().get(i).getSignatureType() == 0) {
                                        activeItems.add(agreements.getData().getItems().get(i));
                                    } else if (cPPVersion == Integer.parseInt(agreements.getData().getItems().get(i).getDocumentVersion().replace("V ", "").replace(".", "").trim()) && agreements.getData().getItems().get(i).getSignatureType() == 1) {
                                        activeItems.add(agreements.getData().getItems().get(i));
                                    } else {
                                        pastItems.add(agreements.getData().getItems().get(i));
                                    }
                                }
                            }
                            adapter = new AgreeListAdapter(AgreementsActivity.this, activeItems, dashboardViewModel, listener);
                            recyclerView.setAdapter(adapter);

                            pastAdapter = new PastAgreeListAdapter(AgreementsActivity.this, pastItems, dashboardViewModel, pastListener);
                            recyclerpastAgree.setAdapter(pastAdapter);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            dashboardViewModel.getAgreementsPdfMutableLiveData().observe(this, new Observer<AgreementsPdf>() {
                @Override
                public void onChanged(AgreementsPdf agreementsPdf) {
                    if (agreementsPdf.getStatus().equalsIgnoreCase("SUCCESS")) {
                        objMyApplication.setAgreementsPdf(agreementsPdf);
                        adapter = new AgreeListAdapter(AgreementsActivity.this, agreements.getData().getItems(), dashboardViewModel, listener);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setOnClickListener() {
        try {

            listener = (view, position) -> {
                if (position == 1) {
                    Intent inte = new Intent(Intent.ACTION_VIEW);
                    inte.setDataAndType(
                            Uri.parse(tosURL),
                            "application/pdf");
                    startActivity(inte);

                }
                if (position == 0) {
                    Intent inte = new Intent(Intent.ACTION_VIEW);
                    inte.setDataAndType(
                            Uri.parse(privacyURL),
                            "application/pdf");
                    startActivity(inte);

                }
            };
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}