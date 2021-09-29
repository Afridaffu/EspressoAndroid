package com.coyni.android.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.coyni.android.view.MainActivity;
import com.coyni.android.R;
import com.coyni.android.model.user.AccountLimits;
import com.coyni.android.model.user.User;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.model.usertracker.UserTrackerData;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.DashboardViewModel;

public class AccountLimitsFragment extends Fragment {
    View view;
    static Context context;
    ImageView imgBack;
    TextView tvTokenSendWeekLimit, tvTokenBuyCreditCardWeeekLimit, tokenWithdrawalSignetWeekLimit, tvTokenWithdrawalBankWeekLimit, tvTokenWithdrawalInstantpayWeekLimit, tvTokenWithdrawalGiftcardWeekLimit, tvTokenBuyCardWeeekLimit, tvTokenBuyBankWeeekLimit;
    MyApplication objMyApplication;
    DashboardViewModel dashboardViewModel;
    ProgressDialog dialog;

    public AccountLimitsFragment() {
    }

    public static AccountLimitsFragment newInstance(Context cxt) {
        AccountLimitsFragment fragment = new AccountLimitsFragment();
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_acc_limits, container, false);
        try {
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication = (MyApplication) context.getApplicationContext();
            if (Build.VERSION.SDK_INT >= 23) {
                //getActivity().getWindow().setStatusBarColor(getActivity().getColor(R.color.statusbar));
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            int userType = 0;


            getActivity().findViewById(R.id.layoutMenu).setVisibility(View.GONE);

            imgBack = (ImageView) view.findViewById(R.id.imgBack);


            tvTokenWithdrawalBankWeekLimit = (TextView) view.findViewById(R.id.tvTokenWithdrawalBankWeekLimit);
            tvTokenWithdrawalInstantpayWeekLimit = (TextView) view.findViewById(R.id.tvTokenWithdrawalInstantpayWeekLimit);
            tvTokenWithdrawalGiftcardWeekLimit = (TextView) view.findViewById(R.id.tvTokenWithdrawalGiftcardWeekLimit);
            tokenWithdrawalSignetWeekLimit = view.findViewById(R.id.tokenWithdrawalSignetWeekLimit);

            tvTokenBuyCardWeeekLimit = (TextView) view.findViewById(R.id.tvTokenBuyCardWeeekLimit);
            tvTokenBuyCreditCardWeeekLimit = (TextView) view.findViewById(R.id.tvTokenBuyCreditCardWeeekLimit);
            tvTokenBuyBankWeeekLimit = (TextView) view.findViewById(R.id.tvTokenBuyBankWeeekLimit);
            tvTokenSendWeekLimit = (TextView) view.findViewById(R.id.tvTokenSendWeekLimit);

            dashboardViewModel.meAccountLimits(userType);
            viewAccountLimits();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).loadProfile();
            }
        });
        return view;
    }

    private void viewAccountLimits() {
        dashboardViewModel.getUserAccountLimitsMutableLiveData().observe(getActivity(), new Observer<AccountLimits>() {
            @Override
            public void onChanged(AccountLimits user) {
                if (user != null) {

                    if (user.getData().getTokenWithdrawalBankDayLimit() > 0 && user.getData().getTokenWithdrawalBankWeekLimit() <= 0) {
                        tvTokenWithdrawalBankWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalBankDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else if (user.getData().getTokenWithdrawalBankDayLimit() <= 0 && user.getData().getTokenWithdrawalBankWeekLimit() > 0) {
                        tvTokenWithdrawalBankWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalBankWeekLimit()))) + " " + getString(R.string.usdcurrency) + " / Weekly");
                    } else if (user.getData().getTokenWithdrawalBankDayLimit() > 0 && user.getData().getTokenWithdrawalBankWeekLimit() > 0) {
                        tvTokenWithdrawalBankWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalBankDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else {
                        tvTokenWithdrawalBankWeekLimit.setText("No Limit");
                    }

                    if (user.getData().getTokenWithdrawalInstantpayDayLimit() > 0 && user.getData().getTokenWithdrawalInstantpayWeekLimit() <= 0) {
                        tvTokenWithdrawalInstantpayWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalInstantpayDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else if (user.getData().getTokenWithdrawalInstantpayDayLimit() <= 0 && user.getData().getTokenWithdrawalInstantpayWeekLimit() > 0) {
                        tvTokenWithdrawalInstantpayWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalInstantpayWeekLimit()))) + " " + getString(R.string.usdcurrency) + " / Weekly");
                    } else if (user.getData().getTokenWithdrawalInstantpayDayLimit() > 0 && user.getData().getTokenWithdrawalInstantpayWeekLimit() > 0) {
                        tvTokenWithdrawalInstantpayWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalInstantpayDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else {
                        tvTokenWithdrawalInstantpayWeekLimit.setText("No Limit");
                    }

                    if (user.getData().getTokenWithdrawalGiftcardDayLimit() > 0 && user.getData().getTokenWithdrawalGiftcardWeekLimit() <= 0) {
                        tvTokenWithdrawalGiftcardWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalGiftcardDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else if (user.getData().getTokenWithdrawalGiftcardDayLimit() <= 0 && user.getData().getTokenWithdrawalGiftcardWeekLimit() > 0) {
                        tvTokenWithdrawalGiftcardWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalGiftcardWeekLimit()))) + " " + getString(R.string.usdcurrency) + " / Weekly");
                    } else if (user.getData().getTokenWithdrawalGiftcardDayLimit() > 0 && user.getData().getTokenWithdrawalGiftcardWeekLimit() > 0) {
                        tvTokenWithdrawalGiftcardWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalGiftcardDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else {
                        tvTokenWithdrawalGiftcardWeekLimit.setText("No Limit");
                    }

                    if (user.getData().getTokenWithdrawalSignetDayLimit() > 0 && user.getData().getTokenWithdrawalSignetWeekLimit() <= 0) {
                        tokenWithdrawalSignetWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalSignetDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else if (user.getData().getTokenWithdrawalSignetDayLimit() <= 0 && user.getData().getTokenWithdrawalSignetWeekLimit() > 0) {
                        tokenWithdrawalSignetWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalSignetWeekLimit()))) + " " + getString(R.string.usdcurrency) + " / Weekly");
                    } else if (user.getData().getTokenWithdrawalSignetDayLimit() > 0 && user.getData().getTokenWithdrawalSignetWeekLimit() > 0) {
                        tokenWithdrawalSignetWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenWithdrawalSignetDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else {
                        tokenWithdrawalSignetWeekLimit.setText("No Limit");
                    }

                    if (user.getData().getTokenBuyCardDayLimit() > 0 && user.getData().getTokenBuyCardWeekLimit() <= 0) {
                        tvTokenBuyCardWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyCardDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else if (user.getData().getTokenBuyCardDayLimit() <= 0 && user.getData().getTokenBuyCardWeekLimit() > 0) {
                        tvTokenBuyCardWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyCardWeekLimit()))) + " " + getString(R.string.usdcurrency) + " / Weekly");
                    } else if (user.getData().getTokenBuyCardDayLimit() > 0 && user.getData().getTokenBuyCardWeekLimit() > 0) {
                        tvTokenBuyCardWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyCardDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else {
                        tvTokenBuyCardWeeekLimit.setText("No Limit");
                    }

                    if (user.getData().getTokenBuyCardDayLimit() > 0 && user.getData().getTokenBuyCardWeekLimit() <= 0) {
                        tvTokenBuyCreditCardWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyCardDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else if (user.getData().getTokenBuyCardDayLimit() <= 0 && user.getData().getTokenBuyCardWeekLimit() > 0) {
                        tvTokenBuyCreditCardWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyCardWeekLimit()))) + " " + getString(R.string.usdcurrency) + " / Weekly");
                    } else if (user.getData().getTokenBuyCardDayLimit() > 0 && user.getData().getTokenBuyCardWeekLimit() > 0) {
                        tvTokenBuyCreditCardWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyCardDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else {
                        tvTokenBuyCreditCardWeeekLimit.setText("No Limit");
                    }

                    if (user.getData().getTokenBuyBankDayLimit() > 0 && user.getData().getTokenBuyBankWeeekLimit() <= 0) {
                        tvTokenBuyBankWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyBankDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else if (user.getData().getTokenBuyBankDayLimit() <= 0 && user.getData().getTokenBuyBankWeeekLimit() > 0) {
                        tvTokenBuyBankWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyBankWeeekLimit()))) + " " + getString(R.string.usdcurrency) + " / Weekly");
                    } else if (user.getData().getTokenBuyBankDayLimit() > 0 && user.getData().getTokenBuyBankWeeekLimit() > 0) {
                        tvTokenBuyBankWeeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenBuyBankDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else {
                        tvTokenBuyBankWeeekLimit.setText("No Limit");
                    }

                    if (user.getData().getTokenSendDayLimit() > 0 && user.getData().getTokenSendWeekLimit() <= 0) {
                        tvTokenSendWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenSendDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else if (user.getData().getTokenSendDayLimit() <= 0 && user.getData().getTokenSendWeekLimit() > 0) {
                        tvTokenSendWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenSendWeekLimit()))) + " " + getString(R.string.usdcurrency) + " / Weekly");
                    } else if (user.getData().getTokenSendDayLimit() > 0 && user.getData().getTokenSendWeekLimit() > 0) {
                        tvTokenSendWeekLimit.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC("" + user.getData().getTokenSendDayLimit()))) + " " + getString(R.string.usdcurrency) + " / Daily");
                    } else {
                        tvTokenSendWeekLimit.setText("No Limit");
                    }

                    objMyApplication.setTokenWithdrawalBankDayLimit(user.getData().getTokenWithdrawalBankDayLimit());
                    objMyApplication.setTokenWithdrawalInstantpayDayLimit(user.getData().getTokenWithdrawalInstantpayDayLimit());
                    objMyApplication.setTokenWithdrawalGiftcardDayLimit(user.getData().getTokenWithdrawalGiftcardDayLimit());
                    objMyApplication.setTokenWithdrawalSignetDayLimit(user.getData().getTokenWithdrawalSignetDayLimit());

                    objMyApplication.setTokenBuyCardWeekLimit(user.getData().getTokenBuyCardDayLimit());
                    objMyApplication.setTokenBuyBankWeeekLimit(user.getData().getTokenBuyBankDayLimit());
                    objMyApplication.setTokenSendDayLimit(user.getData().getTokenSendDayLimit());

                }
            }
        });
    }

    private void initObservables() {
        dashboardViewModel.getUserMutableLiveData().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                dialog.dismiss();
                if (user != null) {

                    objMyApplication.setStrAddressLine1(user.getData().getAddressLine1());
                    objMyApplication.setStrAddressLine2(user.getData().getAddressLine2());
                    objMyApplication.setStrState(user.getData().getState());
                    objMyApplication.setStrCity(user.getData().getCity());
                    // objMyApplication.setStrCountry(user.getData().getCountry());
                    objMyApplication.setStrZipCode(user.getData().getZipCode());
                    bindUserTracker(objMyApplication.getUserTracker());
                }
                ProfileFragment profileFragment = ProfileFragment.newInstance(getActivity());
                try {
                    openFragment(profileFragment, "profile");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void bindUserTracker(UserTracker userTracker) {
        UserTrackerData data = userTracker.getData();
        if (data.getAddressAvailable()) {
            objMyApplication.getUserTracker().getData().setAddressAvailable(true);
        }
    }

    private void openFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment, tag).addToBackStack(tag);
//            transaction.replace(R.id.container, fragment, tag);
            transaction.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

