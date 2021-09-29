package com.coyni.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coyni.android.R;
import com.coyni.android.adapters.CustomerWalletsAdapter;
import com.coyni.android.model.user.User;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.model.wallet.WalletResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.DashboardViewModel;

public class AssetFragment extends Fragment {
    View view;
    static Context context;
    DashboardViewModel dashboardViewModel;
    ProgressDialog dialog;
    TextView tvHead, tvUserInfo, tvBalance;
    MyApplication objMyApplication;
    Boolean isAPI = false;

    public AssetFragment() {
    }

    public static AssetFragment newInstance(Context cxt) {
        AssetFragment fragment = new AssetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        context = cxt;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.assetlayout, container, false);
        try {
            objMyApplication = (MyApplication) context.getApplicationContext();
            tvHead = view.findViewById(R.id.tvHead);
            tvUserInfo = view.findViewById(R.id.tvUserInfo);
            tvBalance = view.findViewById(R.id.tvBalance);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            if (Build.VERSION.SDK_INT >= 23) {
                getActivity().getWindow().setStatusBarColor(Color.parseColor("#13A5A1"));
            }
            if (Utils.checkInternet(context)) {
                if (objMyApplication.getWalletResponse() == null) {
                    dialog = new ProgressDialog(context, R.style.MyAlertDialogStyle);
                    dialog.setIndeterminate(false);
                    dialog.setMessage("Please wait...");
                    dialog.getWindow().setGravity(Gravity.CENTER);
                    dialog.show();
                    dashboardViewModel.meWallet();
                } else {
                    bindWallets(objMyApplication.getWalletResponse());
                }
                if (objMyApplication.getStrUser().equals("")) {
                    dashboardViewModel.meProfile();
                } else {
                    tvHead.setText("Welcome, " + objMyApplication.getStrUser());
                    tvUserInfo.setText(objMyApplication.getStrUserCode());
                }
                if (objMyApplication.getUserTracker() == null) {
                    dashboardViewModel.meTracker();
                } else {
                    bindUserTracker(objMyApplication.getUserTracker());
                }
            } else {
                Toast.makeText(context, getString(R.string.internet), Toast.LENGTH_LONG).show();
            }
            initObservables();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return view;
    }

    private void initObservables() {
        dashboardViewModel.getWalletResponseMutableLiveData().observe(getActivity(), new Observer<WalletResponse>() {
            @Override
            public void onChanged(WalletResponse walletResponse) {
                dialog.dismiss();
                if (walletResponse != null) {
                    objMyApplication.setWalletResponse(walletResponse);
                    bindWallets(walletResponse);
                }
            }
        });
        dashboardViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    tvHead.setText("Welcome, " + user.getData().getFirstName());
                    tvUserInfo.setText(user.getData().getFirstName().substring(0, 1).toUpperCase() + user.getData().getLastName().substring(0, 1).toUpperCase());
                    objMyApplication.setStrUser(user.getData().getFirstName() + " " + user.getData().getLastName());
                    objMyApplication.setStrUserCode(user.getData().getFirstName().substring(0, 1).toUpperCase() + user.getData().getLastName().substring(0, 1).toUpperCase());
                }
            }
        });
        dashboardViewModel.getUserTrackerMutableLiveData().observe(getActivity(), new Observer<UserTracker>() {
            @Override
            public void onChanged(UserTracker userTracker) {
                if (userTracker != null) {
                    objMyApplication.setUserTracker(userTracker);
                    bindUserTracker(userTracker);
                }
            }
        });
    }

    private void bindWallets(WalletResponse walletResponse) {
        RecyclerView rvWallets;
        CustomerWalletsAdapter customerWalletsAdapter;
        try {
            if (walletResponse.getData() != null && walletResponse.getData().getWalletInfo() != null && walletResponse.getData().getWalletInfo().size() > 0) {
                rvWallets = (RecyclerView) view.findViewById(R.id.rvWallets);
                customerWalletsAdapter = new CustomerWalletsAdapter(walletResponse.getData().getWalletInfo(), context);
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
                rvWallets.setLayoutManager(mLayoutManager);
                rvWallets.setItemAnimator(new DefaultItemAnimator());
                rvWallets.setAdapter(customerWalletsAdapter);
                tvBalance.setText("$ " + Utils.convertBigDecimal(String.valueOf(walletResponse.getData().getTotalWalletsBalance())));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void bindUserTracker(UserTracker userTracker) {
        try {
            ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            int progress = 0;
            if (userTracker.getData().getProfileVerified()) {
                progress += 1;
            }
            if (userTracker.getData().getAuthyRegistered()) {
                progress += 1;
            }
            if (userTracker.getData().getAddressAvailable()) {
                progress += 1;
            }
            if (userTracker.getData().getPaymentModeAdded()) {
                progress += 1;
            }
            progressBar.setProgress((progress * 25));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

