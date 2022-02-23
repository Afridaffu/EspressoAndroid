package com.greenbox.coyni.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.LatestTxnAdapter;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletName;
import com.greenbox.coyni.model.businesswallet.WalletResponseData;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.wallet.WalletInfo;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.TransactionListActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;

public class BusinessAccountFragment extends BaseFragment {
    LinearLayout viewMoreLL;
    RecyclerView txnRV;
    TextView noTxnTV,tvBalance;
    SwipeRefreshLayout latestTxnRefresh;
    DashboardViewModel dashboardViewModel;
    BusinessDashboardViewModel businessDashboardViewModel;
    MyApplication objMyApplication;
    NestedScrollView transactionsNSV;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_business_account, container, false);
        viewMoreLL=currentView.findViewById(R.id.viewMoreLL);
        txnRV = currentView.findViewById(R.id.txnRV);
        noTxnTV = currentView.findViewById(R.id.noTxnTV);
        tvBalance = currentView.findViewById(R.id.tvBalance);
        transactionsNSV = currentView.findViewById(R.id.transactionsNSV);
        latestTxnRefresh = currentView.findViewById(R.id.latestTxnRefresh);
        objMyApplication = (MyApplication) requireContext().getApplicationContext();
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);

        try {
            getBalance(objMyApplication.getWalletResponseData());
            getLatestTxns(objMyApplication.getListLatestTxn());
        } catch (Exception e) {
            e.printStackTrace();
        }

        latestTxnRefresh.setColorSchemeColors(getResources().getColor(R.color.primary_green, null));
        latestTxnRefresh.setOnRefreshListener(() -> {
            try {
//                    if (objMyApplication.getTrackerResponse().getData().isPersonIdentified()
//                            && objMyApplication.getTrackerResponse().getData().isPaymentModeAdded()) {
                    dashboardViewModel.getLatestTxns();
                    dashboardViewModel.meWallet();
                    transactionsNSV.smoothScrollTo(0, 0);
//                    } else {
                    latestTxnRefresh.setRefreshing(false);
//                    }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });


        initObservers();

        viewMoreLL.setOnClickListener(view -> goToTransact());
        return currentView;
    }

    private void initObservers() {
//        dashboardViewModel.getWalletResponseMutableLiveData().observe(getViewLifecycleOwner(), walletResponse -> {
//            if (walletResponse != null) {
//                objMyApplication.setWalletResponse(walletResponse);
//                getBalance(walletResponse);
//            }
//        });
        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                if (businessWalletResponse != null){
                    try {
                        List<WalletName> walletInfo = businessWalletResponse.getData().getWalletNames();
                        if (walletInfo != null && walletInfo.size() > 0) {
                                objMyApplication.setWalletResponseData(businessWalletResponse.getData());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        dashboardViewModel.getGetUserLatestTxns().observe(getViewLifecycleOwner(), latestTxnResponse -> {
            latestTxnRefresh.setRefreshing(false);

            objMyApplication.setListLatestTxn(latestTxnResponse);
            if (latestTxnResponse != null && latestTxnResponse.getStatus().equalsIgnoreCase("success")) {
//                    cvHeaderRL.setVisibility(View.VISIBLE);
//                    cvSmallHeaderRL.setVisibility(View.GONE);
//                    getStartedCV.setVisibility(View.GONE);
                transactionsNSV.setVisibility(View.VISIBLE);
//
//                    welcomeCoyniCV.setVisibility(View.GONE);
//                    underReviewCV.setVisibility(View.GONE);
//                    additionalActionCV.setVisibility(View.GONE);

                if (latestTxnResponse.getData().size() == 0) {
                    txnRV.setVisibility(View.GONE);
                    noTxnTV.setVisibility(View.VISIBLE);
//                        buyTokensCV.setVisibility(View.VISIBLE);

                } else if (latestTxnResponse.getData().size() > 4) {
//                        buyTokensCV.setVisibility(View.GONE);
                    txnRV.setVisibility(View.VISIBLE);
                    viewMoreLL.setVisibility(View.VISIBLE);
                    noTxnTV.setVisibility(View.GONE);
                    LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(latestTxnResponse, requireContext().getApplicationContext());
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireContext().getApplicationContext());
                    txnRV.setLayoutManager(mLayoutManager);
                    txnRV.setItemAnimator(new DefaultItemAnimator());
                    txnRV.setAdapter(latestTxnAdapter);
                } else if (latestTxnResponse.getData().size() <= 4) {
//                        buyTokensCV.setVisibility(View.GONE);
                    txnRV.setVisibility(View.VISIBLE);
                    viewMoreLL.setVisibility(View.GONE);
                    noTxnTV.setVisibility(View.GONE);
                    LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(latestTxnResponse, requireContext().getApplicationContext());
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireContext().getApplicationContext());
                    txnRV.setLayoutManager(mLayoutManager);
                    txnRV.setItemAnimator(new DefaultItemAnimator());
                    txnRV.setAdapter(latestTxnAdapter);
                }

            }
        });


    }

    public void goToTransact(){
        startActivity(new Intent(requireActivity().getApplication(), TransactionListActivity.class));
    }
    @Override
    public void updateData() {

    }
    private void getBalance(WalletResponseData walletResponse) {
        try {
            String strAmount;
                for (int i = 0; i < walletResponse.getWalletNames().size(); i++) {
                    if (walletResponse.getWalletNames().get(i).getWalletCategory().equals(getString(R.string.currency))) {
                        strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletResponse.getWalletNames().get(i).getExchangeAmount()));
                        tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.checkInternet(requireContext().getApplicationContext())) {
//            progressDialog = Utils.showProgressDialog(this);
            businessDashboardViewModel.meMerchantWallet();
            dashboardViewModel.getLatestTxns();
            transactionsNSV.smoothScrollTo(0, 0);
        } else {
            Utils.displayAlert(getString(R.string.internet),requireActivity(), "", "");
        }
    }
    public void getLatestTxns(LatestTxnResponse daata){

        try {
            transactionsNSV.setVisibility(View.VISIBLE);

            if (daata.getData().size() == 0) {
                txnRV.setVisibility(View.GONE);
                noTxnTV.setVisibility(View.VISIBLE);

            } else if (daata.getData().size() > 4) {
                txnRV.setVisibility(View.VISIBLE);
                viewMoreLL.setVisibility(View.VISIBLE);
                noTxnTV.setVisibility(View.GONE);
                LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(daata, requireContext().getApplicationContext());
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireContext().getApplicationContext());
                txnRV.setLayoutManager(mLayoutManager);
                txnRV.setItemAnimator(new DefaultItemAnimator());
                txnRV.setAdapter(latestTxnAdapter);
            } else if (daata.getData().size() <= 4) {
                txnRV.setVisibility(View.VISIBLE);
                viewMoreLL.setVisibility(View.GONE);
                noTxnTV.setVisibility(View.GONE);
                LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(daata, requireContext().getApplicationContext());
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(requireContext().getApplicationContext());
                txnRV.setLayoutManager(mLayoutManager);
                txnRV.setItemAnimator(new DefaultItemAnimator());
                txnRV.setAdapter(latestTxnAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    }
