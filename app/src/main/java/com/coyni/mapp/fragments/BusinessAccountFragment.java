package com.coyni.mapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import com.coyni.mapp.R;
import com.coyni.mapp.adapters.LatestTxnAdapter;
import com.coyni.mapp.model.businesswallet.BusinessWalletResponse;
import com.coyni.mapp.model.businesswallet.WalletInfo;
import com.coyni.mapp.model.businesswallet.WalletRequest;
import com.coyni.mapp.model.identity_verification.LatestTransactionsRequest;
import com.coyni.mapp.model.identity_verification.LatestTxnResponse;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.NotificationsActivity;
import com.coyni.mapp.view.business.BusinessDashboardActivity;
import com.coyni.mapp.view.business.BusinessTransactionListActivity;
import com.coyni.mapp.viewmodel.BusinessDashboardViewModel;
import com.coyni.mapp.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.List;

public class BusinessAccountFragment extends BaseFragment {

    private LinearLayout viewMoreLL;
    private RelativeLayout mUserIconRelativeLayout;
    private RecyclerView txnRV;
    private TextView noTxnTV, tvBalance;
    private SwipeRefreshLayout latestTxnRefresh;
    private ImageView mIvUserIcon;
    private TextView mTvUserName, mTvUserIconText;
    private DashboardViewModel dashboardViewModel;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private MyApplication objMyApplication;
    private Long mLastClickTimeQA = 0L;
    private NestedScrollView transactionsNSV;
    private ImageView mIvNotifications;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.fragment_business_account, container, false);
        viewMoreLL = currentView.findViewById(R.id.viewMoreLL);
        txnRV = currentView.findViewById(R.id.txnRV);
        noTxnTV = currentView.findViewById(R.id.noTxnTV);
        tvBalance = currentView.findViewById(R.id.tvBalance);
        mIvUserIcon = currentView.findViewById(R.id.iv_user_icon);
        mTvUserName = currentView.findViewById(R.id.tv_user_name);
        mTvUserIconText = currentView.findViewById(R.id.tv_user_icon_text);
        transactionsNSV = currentView.findViewById(R.id.transactionsNSV);
        latestTxnRefresh = currentView.findViewById(R.id.latestTxnRefresh);
        mIvNotifications = currentView.findViewById(R.id.iv_notifications);
        mUserIconRelativeLayout = currentView.findViewById(R.id.rl_user_icon_layout);
        objMyApplication = (MyApplication) requireContext().getApplicationContext();
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);

        viewMoreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTransact();
            }
        });

        latestTxnRefresh.setColorSchemeColors(getResources().getColor(R.color.primary_green, null));
        latestTxnRefresh.setOnRefreshListener(() -> {
            try {
                fetchTransactions();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        viewMoreLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTransact();
            }
        });

        mIvNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });

        mUserIconRelativeLayout.setOnClickListener(view -> {
            ((BusinessDashboardActivity) getActivity()).launchSwitchAccountPage();
        });

        initObservers();
        showUserData();
        return currentView;
    }

    private void showUserData() {
        ((BusinessDashboardActivity) getActivity()).showUserData();
    }

    private void initObservers() {
        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                if (businessWalletResponse == null) {
                    return;
                }
                if (businessWalletResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                    walletInfoData(businessWalletResponse);
                }
            }

        });


        dashboardViewModel.getGetUserLatestTxns().observe(getViewLifecycleOwner(), latestTxnResponse -> {
            latestTxnRefresh.setRefreshing(false);
            objMyApplication.setListLatestTxn(latestTxnResponse);
            if (latestTxnResponse != null && latestTxnResponse.getStatus().equalsIgnoreCase("success")) {
                transactionsNSV.setVisibility(View.VISIBLE);
                if (latestTxnResponse.getData().size() == 0) {
                    txnRV.setVisibility(View.GONE);
                    noTxnTV.setVisibility(View.VISIBLE);
                } else if (latestTxnResponse.getData().size() > 4) {
                    txnRV.setVisibility(View.VISIBLE);
                    viewMoreLL.setVisibility(View.VISIBLE);
                    noTxnTV.setVisibility(View.GONE);
                    LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(latestTxnResponse, getActivity());
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    txnRV.setLayoutManager(mLayoutManager);
                    txnRV.setItemAnimator(new DefaultItemAnimator());
                    txnRV.setAdapter(latestTxnAdapter);
                } else if (latestTxnResponse.getData().size() <= 4) {
                    txnRV.setVisibility(View.VISIBLE);
                    viewMoreLL.setVisibility(View.GONE);
                    noTxnTV.setVisibility(View.GONE);
                    LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(latestTxnResponse, getActivity());
                    LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    txnRV.setLayoutManager(mLayoutManager);
                    txnRV.setItemAnimator(new DefaultItemAnimator());
                    txnRV.setAdapter(latestTxnAdapter);
                }
            }
            ((BusinessDashboardActivity) getActivity()).dismissDialog();
        });
    }

    private void goToTransact() {
        startActivity(new Intent(requireActivity().getApplication(), BusinessTransactionListActivity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utils.checkInternet(requireContext().getApplicationContext())) {
//            WalletRequest walletRequest = new WalletRequest();
//            walletRequest.setWalletType(Utils.TOKEN);
            businessDashboardViewModel.meWallets();
            LatestTransactionsRequest request = new LatestTransactionsRequest();
            request.setTransactionType(getDefaultTransactionTypes());
            request.setMerchantTokenTransactions(true);

            dashboardViewModel.getLatestTxns(request);
            ((BusinessDashboardActivity) getActivity()).notificationsAPICall();
            ((BusinessDashboardActivity) getActivity()).showProgressDialog();
            transactionsNSV.smoothScrollTo(0, 0);
        } else {
            Utils.displayAlert(getString(R.string.internet), requireActivity(), "", "");
        }
    }

    @Override
    public void onNotificationUpdate() {
//        super.onNotificationUpdate();
        latestTxnRefresh.setRefreshing(true);
        fetchTransactions();
    }

    public void getLatestTxns(LatestTxnResponse daata) {
        try {
            transactionsNSV.setVisibility(View.VISIBLE);

            if (daata.getData() != null && daata.getData().size() == 0) {
                txnRV.setVisibility(View.GONE);
                noTxnTV.setVisibility(View.VISIBLE);

            } else if (daata.getData() != null && daata.getData().size() > 4) {
                txnRV.setVisibility(View.VISIBLE);
                viewMoreLL.setVisibility(View.VISIBLE);
                noTxnTV.setVisibility(View.GONE);
                LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(daata, getActivity());
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                txnRV.setLayoutManager(mLayoutManager);
                txnRV.setItemAnimator(new DefaultItemAnimator());
                txnRV.setAdapter(latestTxnAdapter);
            } else if (daata.getData() != null && daata.getData().size() <= 4) {
                txnRV.setVisibility(View.VISIBLE);
                viewMoreLL.setVisibility(View.GONE);
                noTxnTV.setVisibility(View.GONE);
                LatestTxnAdapter latestTxnAdapter = new LatestTxnAdapter(daata, getActivity());
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                txnRV.setLayoutManager(mLayoutManager);
                txnRV.setItemAnimator(new DefaultItemAnimator());
                txnRV.setAdapter(latestTxnAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Integer> getDefaultTransactionTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.payRequest);
        transactionType.add(Utils.withdraw);
        transactionType.add(Utils.buyTokens);
        transactionType.add(Utils.refund);
        transactionType.add(Utils.paidInvoice);
        transactionType.add(Utils.businessPayout);
        transactionType.add(Utils.reserveRelease);
        return transactionType;
    }

    private void fetchTransactions() {
        try {
            LatestTransactionsRequest request = new LatestTransactionsRequest();
            request.setTransactionType(getDefaultTransactionTypes());
            request.setMerchantTokenTransactions(true);
            ((BusinessDashboardActivity) getActivity()).showProgressDialog();
            dashboardViewModel.getLatestTxns(request);
            transactionsNSV.smoothScrollTo(0, 0);
            latestTxnRefresh.setRefreshing(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void walletInfoData(BusinessWalletResponse businessWalletResponse) {
        if (businessWalletResponse.getData() != null && businessWalletResponse.getData().getWalletNames() != null && businessWalletResponse.getData().getWalletNames().size() > 0) {
            List<WalletInfo> walletInfo = businessWalletResponse.getData().getWalletNames();
            for (WalletInfo walletInfo1 : walletInfo) {
                if (walletInfo1.getWalletType().equalsIgnoreCase(Utils.TOKEN_STR)) {
                    String strAmount;
                    objMyApplication.setGBTBalance(walletInfo1.getAvailabilityToUse(), walletInfo1.getWalletType());
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(walletInfo1.getAvailabilityToUse()));
                    tvBalance.setText(strAmount);
                }
            }
        }
    }

}
