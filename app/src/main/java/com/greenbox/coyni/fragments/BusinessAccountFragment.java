package com.greenbox.coyni.fragments;

import static com.microblink.blinkcard.MicroblinkSDK.getApplicationContext;

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
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.wallet.WalletInfo;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.view.TransactionListActivity;
import com.greenbox.coyni.view.business.BusinessTransactionListActivity;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;

public class BusinessAccountFragment extends BaseFragment {
    LinearLayout viewMoreLL;
    RecyclerView txnRV;
    TextView noTxnTV,tvBalance;
    SwipeRefreshLayout latestTxnRefresh;
    DashboardViewModel dashboardViewModel;
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

        dashboardViewModel.meWallet();
        dashboardViewModel.getLatestTxns();
        latestTxnRefresh.setColorSchemeColors(getResources().getColor(R.color.primary_green));

        latestTxnRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
            }
        });


        initObservers();

        viewMoreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTransact();
            }
        });
        return currentView;
    }

    private void initObservers() {
        dashboardViewModel.getWalletResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<WalletResponse>() {
            @Override
            public void onChanged(WalletResponse walletResponse) {
                if (walletResponse != null) {
                    objMyApplication.setWalletResponse(walletResponse);
                    getBalance(walletResponse);
                }
            }
        });


        dashboardViewModel.getGetUserLatestTxns().observe(getViewLifecycleOwner(), new Observer<LatestTxnResponse>() {
            @Override
            public void onChanged(LatestTxnResponse latestTxnResponse) {
                latestTxnRefresh.setRefreshing(false);
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
                    } else if (latestTxnResponse.getData().size() <= 4 && latestTxnResponse.getData().size()>0) {
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
            }
        });


    }

    public void goToTransact(){
        startActivity(new Intent(requireActivity().getApplication(), TransactionListActivity.class));
    }
    @Override
    public void updateData() {

    }
    private void getBalance(WalletResponse walletResponse) {
        try {
            String strAmount = "";
            List<WalletInfo> walletInfo = walletResponse.getData().getWalletInfo();
            if (walletInfo != null && walletInfo.size() > 0) {
                for (int i = 0; i < walletInfo.size(); i++) {
                    if (walletInfo.get(i).getWalletType().equals(getString(R.string.currency))) {
                        objMyApplication.setGbtWallet(walletInfo.get(i));
                        strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo.get(i).getExchangeAmount()));
                        tvBalance.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
                        objMyApplication.setGBTBalance(walletInfo.get(i).getExchangeAmount());
                    }
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
            transactionsNSV.smoothScrollTo(0, 0);
        } else {
            Utils.displayAlert(getString(R.string.internet),requireActivity(), "", "");
        }
    }
}
