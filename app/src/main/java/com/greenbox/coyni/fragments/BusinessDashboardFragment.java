package com.greenbox.coyni.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.MerchantTransactionListPostedNewAdapter;
import com.greenbox.coyni.adapters.TransactionListPendingAdapter;
import com.greenbox.coyni.dialogs.BatchNowDialog;
import com.greenbox.coyni.dialogs.DateRangePickerDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.ProcessingVolumeDialog;
import com.greenbox.coyni.model.BatchNow.BatchNowPaymentRequest;
import com.greenbox.coyni.model.BatchNow.BatchNowRequest;
import com.greenbox.coyni.model.BatchNow.BatchNowResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.RollingListRequest;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.DashboardReserveList.ReserveListData;
import com.greenbox.coyni.model.DashboardReserveList.ReserveListItems;
import com.greenbox.coyni.model.DashboardReserveList.ReserveListResponse;
import com.greenbox.coyni.model.RangeDates;
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.business_activity.BusinessActivityData;
import com.greenbox.coyni.model.business_activity.BusinessActivityRequest;
import com.greenbox.coyni.model.business_activity.BusinessActivityResp;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.businesswallet.WalletRequest;
import com.greenbox.coyni.model.merchant_activity.MerchantActivityRequest;
import com.greenbox.coyni.model.merchant_activity.MerchantActivityResp;
import com.greenbox.coyni.model.reserverule.RollingRuleResponse;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListPending;
import com.greenbox.coyni.model.transaction.TransactionListPosted;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.CustomTypefaceSpan;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.SeekBarWithFloatingText;
import com.greenbox.coyni.utils.UserData;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.NotificationsActivity;
import com.greenbox.coyni.view.PayRequestActivity;
import com.greenbox.coyni.view.ValidatePinActivity;
import com.greenbox.coyni.view.business.BusinessBatchPayoutSearchActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.view.business.MerchantTransactionListActivity;
import com.greenbox.coyni.view.business.ReserveReleasesActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.CoyniViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.NotificationsViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

//Business Dashboard Fragment
public class BusinessDashboardFragment extends BaseFragment {

    private View mCurrentView, batchView, releaseView;
    private TextView tv_PayoutNoHistory, batchNoTransaction, nextReleaseNATV,
            lastReleaseNATV, releaseNoTransaction;
    private MyApplication myApplication;
    private CardView cvReserveView;
    private ImageView mIvUserIcon;
    private TextView mTvUserName, mTvUserIconText, spannableTextView;
    private TextView mTvReserveList, mPayoutHistory,
            nextPayoutAmountTV, lastPayoutAmountTV, nxtPayoutDatenTimeTV;
    private LinearLayout mLlBuyTokensFirstTimeView, mLlProcessingVolume, monthlyVolumeViewLl;
    private TextView mTvProcessingVolume;
    private TransactionListPendingAdapter transactionListPendingAdapter;
    private MerchantTransactionListPostedNewAdapter transactionListPostedAdapter;

    private DashboardViewModel dashboardViewModel;
    private List<TransactionListPending> globalPending = new ArrayList<>();
    private List<TransactionListPosted> globalPosted = new ArrayList<>();

    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private NotificationsViewModel notificationsViewModel;
    private RelativeLayout mUserIconRelativeLayout, notificationsRL;
    private TextView mTvOfficiallyVerified, mTvMerchantTransactions;
    private TextView lastPayoutDate, mTvReserveBalance, merchantBalanceTV, mTvMonthlyVolume, mTvHighTickets, reserveRuleTV, rulePeriodTV;
    private CardView mCvBatchNow;
    private Long mLastClickTimeQA = 0L;
    private TextView nextReleaseAmountTV, nextReleaseDateTV,
            lastReleaseAmountTV, lastReleaseDateTV, disable_reserve_list;
    private SeekBarWithFloatingText mSbTodayVolume;
    private Long mLastClickTime = 0L;
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private final int CODE_AUTHENTICATION_VERIFICATION = 251;
    int TOUCH_ID_ENABLE_REQUEST_CODE = 100;
    static String strToken = "";
    private DatabaseHandler dbHandler;
    private String batchId;
    private TextView mGrossAmount, mTransactions, mRefunds, mProcessingFees, mMISCFees, mNetAmount,
            saleOrdersText, mAverageTicket, mHighestTicket, mDateHighestTicket;
    private LinearLayout mTicketsLayout;
    private UserData userData;
    private String reserveRules = "";
    private CoyniViewModel coyniViewModel;
    //Processing Volume Types
    private static final String todayValue = "Today";
    private static final String yesterdayValue = "Yesterday";
    private static final String monthDate = "Month to Date";
    private static final String lastMonthDate = "Last Month";
    private static final String customDate = "Custom Date Range";
    private static final String dateAndTime = "yyyy-MM-dd HH:mm:ss";
    private static final String date = "yyyy-MM-dd";
    private static final String dateResult = "MM/dd/yyyy";
    private static final String startTime = " 00:00:00";
    private static final String endTime = " 23:59:59";
    private static final String midTime = " 12:00:00";
    private static final String defaultAmount = "0.00";
    private RangeDates rangeDates;
    private String strFromDate, strToDate;
    int walletCount = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.v(TAG, "onCreateView");
        mCurrentView = inflater.inflate(R.layout.fragment_business_dashboard, container, false);
        initViewModels();
        initObservers();
        initFields();
        return mCurrentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.v(TAG, "onViewCreated");
        setBusinessData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeObservers();
        LogUtils.v(TAG, "onDestroyView");
    }

    private void initViewModels() {
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
    }

    private Double getMerchantBalance() {
        Double amt = 0.0;
        if (myApplication.getCurrentUserData().getReserveGBTBalance() != null) {
            amt += myApplication.getCurrentUserData().getReserveGBTBalance();
        }
        if (myApplication.getCurrentUserData().getMerchnatGBTBalance() != null) {
            amt += myApplication.getCurrentUserData().getMerchnatGBTBalance();
        }
        return amt;
    }

    private void initFields() {
        mUserIconRelativeLayout = mCurrentView.findViewById(R.id.rl_user_icon_layout);
        myApplication = (MyApplication) getActivity().getApplicationContext();
        userData = myApplication.getCurrentUserData();
        mLlBuyTokensFirstTimeView = mCurrentView.findViewById(R.id.ll_buy_tokens_first_time);
        notificationsRL = mCurrentView.findViewById(R.id.notificationsRL);
        mTvOfficiallyVerified = mCurrentView.findViewById(R.id.tv_officially_verified);
        mLlProcessingVolume = mCurrentView.findViewById(R.id.ll_processing_volume);
        mTvProcessingVolume = mCurrentView.findViewById(R.id.tv_processing_volume);
        mTvMerchantTransactions = mCurrentView.findViewById(R.id.tv_merchant_transactions);
        mCvBatchNow = mCurrentView.findViewById(R.id.cv_batch_now);
        mTvReserveList = mCurrentView.findViewById(R.id.tv_reserve_list);
        mIvUserIcon = mCurrentView.findViewById(R.id.iv_user_icon);
        mTvUserName = mCurrentView.findViewById(R.id.tv_user_name);
        mTvUserIconText = mCurrentView.findViewById(R.id.tv_user_icon_text);
        mPayoutHistory = mCurrentView.findViewById(R.id.tv_PayoutFullHistory);
        merchantBalanceTV = mCurrentView.findViewById(R.id.merchant_balance_tv);
        mTvReserveBalance = mCurrentView.findViewById(R.id.tv_reserve_balance);
        mSbTodayVolume = mCurrentView.findViewById(R.id.sb_today_volume);
        nextPayoutAmountTV = mCurrentView.findViewById(R.id.nextPayoutAmountTV);
        lastPayoutAmountTV = mCurrentView.findViewById(R.id.lastPayoutAmountTV);
        nxtPayoutDatenTimeTV = mCurrentView.findViewById(R.id.nxtPayoutDatenTimeTV);
        lastPayoutDate = mCurrentView.findViewById(R.id.lastPayoutDate);
        cvReserveView = mCurrentView.findViewById(R.id.cv_reserve_view);
        mTvMonthlyVolume = mCurrentView.findViewById(R.id.tv_monthly_volume);
        mTvHighTickets = mCurrentView.findViewById(R.id.tv_high_tickets);
        nextReleaseAmountTV = mCurrentView.findViewById(R.id.nextReleaseAmountTV);
        nextReleaseDateTV = mCurrentView.findViewById(R.id.nextReleaseDateTV);
        lastReleaseAmountTV = mCurrentView.findViewById(R.id.lastReleaseAmountTV);
        lastReleaseDateTV = mCurrentView.findViewById(R.id.lastReleaseDateTV);
        monthlyVolumeViewLl = mCurrentView.findViewById(R.id.tv_monthly_volume_view);
        dbHandler = DatabaseHandler.getInstance(getActivity());

        tv_PayoutNoHistory = mCurrentView.findViewById(R.id.tv_PayoutNoHistory);
        batchView = mCurrentView.findViewById(R.id.batchView);
        batchNoTransaction = mCurrentView.findViewById(R.id.batchNoTransaction);
        spannableTextView = mCurrentView.findViewById(R.id.spannableTextView);

        releaseView = mCurrentView.findViewById(R.id.releaseView);
        nextReleaseNATV = mCurrentView.findViewById(R.id.nextReleaseNATV);
        lastReleaseNATV = mCurrentView.findViewById(R.id.lastReleaseNATV);
        releaseNoTransaction = mCurrentView.findViewById(R.id.releaseNoTransaction);
//        disable_reserve_list = mCurrentView.findViewById(R.id.disable_reserve_list);

        // For Processing Volume
        mGrossAmount = mCurrentView.findViewById(R.id.gross_amount);
        mTransactions = mCurrentView.findViewById(R.id.transactions);
        mRefunds = mCurrentView.findViewById(R.id.refunds);
        mProcessingFees = mCurrentView.findViewById(R.id.processing_fees);
        mMISCFees = mCurrentView.findViewById(R.id.misc_fee);
        mNetAmount = mCurrentView.findViewById(R.id.net_amount);
        mTicketsLayout = mCurrentView.findViewById(R.id.tickets_layout);
        saleOrdersText = mCurrentView.findViewById(R.id.sale_order_text);
        mAverageTicket = mCurrentView.findViewById(R.id.average_ticket);
        mHighestTicket = mCurrentView.findViewById(R.id.highest_ticket);
        mDateHighestTicket = mCurrentView.findViewById(R.id.date_of_highest_ticket);

        reserveRuleTV = mCurrentView.findViewById(R.id.reserveRuleTV);
        rulePeriodTV = mCurrentView.findViewById(R.id.rulePeriodTV);
        mDateHighestTicket = mCurrentView.findViewById(R.id.date_of_highest_ticket);

        isBiometric = Utils.getIsBiometric();
//        setFaceLock();
//        setTouchId();
        myApplication.initializeDBHandler(getContext());
        isFaceLock = myApplication.setFaceLock();
        isTouchId = myApplication.setTouchId();
        if (isFaceLock || isTouchId) {
            myApplication.setLocalBiometric(true);
        } else {
            myApplication.setLocalBiometric(false);
        }
        SpannableString ss = new SpannableString("All Payouts are deposited into Business Token Account. Your active batch is set to automatically pay out at 11:59:59 pm PST ");
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/opensans_bold.ttf");
        ss.setSpan(new CustomTypefaceSpan("", font), 31, 53, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ss.setSpan(new CustomTypefaceSpan("", font), 108, 123, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableTextView.setText(ss);

        notificationsRL.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
        });

        mLlBuyTokensFirstTimeView.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            ((BusinessDashboardActivity) getActivity()).launchBuyTokens();
        });

        mLlProcessingVolume.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            showProcessingVolumeDialog();
        });

        mUserIconRelativeLayout.setOnClickListener(view -> {
            ((BusinessDashboardActivity) getActivity()).launchSwitchAccountPage();
        });

        mTvMerchantTransactions.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            startActivity(new Intent(getActivity(), MerchantTransactionListActivity.class));
        });

        mCvBatchNow.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            //showBatchNowDialog(batchNowRequest);
            initiateBatchNow();
        });

    }

    private ArrayList<Integer> getDefaultTransactionTypes() {
        ArrayList<Integer> transactionType = new ArrayList<>();
        transactionType.add(Utils.saleOrder);
        transactionType.add(Utils.refund);
        transactionType.add(Utils.merchantPayout);
        transactionType.add(Utils.monthlyServiceFee);
        return transactionType;
    }

    private void transactionsAPI(TransactionListRequest transactionListRequest) {
//        showProgressDialog();
        dashboardViewModel.meTransactionList(transactionListRequest);
    }

    private void removeObservers() {
        businessDashboardViewModel.getRollingListResponseMutableLiveData().removeObservers(getViewLifecycleOwner());
        businessDashboardViewModel.getReserveListResponseMutableLiveData().removeObservers(getViewLifecycleOwner());
        businessDashboardViewModel.getBatchNowResponseMutableLiveData().removeObservers(getViewLifecycleOwner());
        businessDashboardViewModel.getBatchNowSlideResponseMutableLiveData().removeObservers(getViewLifecycleOwner());
        businessIdentityVerificationViewModel.getGetDBAInfoResponse().removeObservers(getViewLifecycleOwner());
        businessDashboardViewModel.getBusinessActivityRespMutableLiveData().removeObservers(getViewLifecycleOwner());
        businessDashboardViewModel.getMerchantActivityRespMutableLiveData().removeObservers(getViewLifecycleOwner());
        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().removeObservers(getViewLifecycleOwner());
        coyniViewModel.getBiometricTokenResponseMutableLiveData().removeObservers(getViewLifecycleOwner());
        dashboardViewModel.getTransactionListMutableLiveData().removeObservers(getViewLifecycleOwner());
    }

    private void initObservers() {

        businessDashboardViewModel.getRollingListResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BatchPayoutListResponse>() {
            @Override
            public void onChanged(BatchPayoutListResponse batchPayoutListResponse) {
                Log.d(TAG, "BatchPayoutListResponse success");
                if (batchPayoutListResponse != null) {
                    if (batchPayoutListResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                        if (batchPayoutListResponse.getData() != null && batchPayoutListResponse.getData().getItems() != null) {
                            tv_PayoutNoHistory.setVisibility(View.GONE);
                            mPayoutHistory.setVisibility(View.VISIBLE);
                            showData(batchPayoutListResponse.getData().getItems());
                        } else {
                            Log.d(TAG, "No items found");
                        }
                    }
                    Log.d(TAG, "Error");
                }

            }
        });

        businessDashboardViewModel.getReserveListResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<ReserveListResponse>() {
            @Override
            public void onChanged(ReserveListResponse reserveListResponse) {
                if (reserveListResponse != null) {
                    if (reserveListResponse.getStatus().equalsIgnoreCase(("SUCCESS"))) {
                        if (reserveListResponse.getData() != null && reserveListResponse.getData() != null) {
                            showReserveRelease(reserveListResponse.getData());
                        }
                    }
                }
            }
        });

        businessDashboardViewModel.getBatchNowResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BatchPayoutListResponse>() {
            @Override
            public void onChanged(BatchPayoutListResponse batchPayoutListResponse) {
                ((BusinessDashboardActivity) getActivity()).dismissDialog();
                if (batchPayoutListResponse != null) {
                    if (batchPayoutListResponse.getData() != null
                            && batchPayoutListResponse.getData().getItems() != null
                            && batchPayoutListResponse.getData().getItems().size() > 0) {
                        showBatchNowDialog(batchPayoutListResponse.getData().getItems().get(0));
                    } else {
                        Log.d(TAG, "No items found");
                    }
                }
            }
        });

        businessDashboardViewModel.getBatchNowSlideResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BatchNowResponse>() {
            @Override
            public void onChanged(BatchNowResponse batchNowResponse) {
                Log.d(TAG, "BatchNowResponse success");
                if (batchNowResponse != null) {
                    if (batchNowResponse.getStatus() != null && batchNowResponse.getData() != null) {
                        Log.d(TAG, "Batched successfully");
                        batchReq();
                        Utils.showCustomToast(getActivity(), getResources().getString(R.string.Successfully_Closed_Batch), R.drawable.ic_custom_tick, "Batch");
                    } else {
                        Log.d(TAG, "No items found");
                        String msg = getString(R.string.something_went_wrong);
                        if (batchNowResponse.getError() != null
                                && batchNowResponse.getError().getErrorDescription() != null
                                && !batchNowResponse.getError().getErrorDescription().trim().equals("")) {
                            msg = batchNowResponse.getError().getErrorDescription();
                        } else {
                            String mesg = getString(R.string.something_went_wrong);
                        }
                    }
                }
            }
        });

        businessIdentityVerificationViewModel.getGetDBAInfoResponse().observe(getViewLifecycleOwner(), new Observer<DBAInfoResp>() {
            @Override
            public void onChanged(DBAInfoResp dbaInfoResp) {
                if (dbaInfoResp != null && dbaInfoResp.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                    myApplication.setDbaInfoResp(dbaInfoResp);
                    setMonthlyVolumeData();
                }
            }
        });

        businessDashboardViewModel.getBusinessActivityRespMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BusinessActivityResp>() {
            @Override
            public void onChanged(BusinessActivityResp businessActivityResp) {
                try {
                    if (businessActivityResp != null && businessActivityResp.getData() != null) {
                        double processingFee = 0.0,
                                grossAmount = 0.0,
                                refunds = 0.0,
                                miscFee = 0.0,
                                netAmount = 0.0,
                                averageTicket = 0.0;
                        int totalTransactions = 1;
                        //                    mSbTodayVolume.setEnabled(false);
                        if (businessActivityResp.getStatus() != null && businessActivityResp.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                            if (businessActivityResp.getData() != null && businessActivityResp.getData().size() > 0) {
                                List<BusinessActivityData> data = businessActivityResp.getData();
                                for (int position = 0; position < data.size(); position++) {
                                    if (data.get(position).getTransactionType() != null && data.get(position).getTransactionType().equalsIgnoreCase(Utils.saleOrdertxntype)
                                            && data.get(position).getTransactionSubType() == null) {
                                        if (data.get(position).getTotalAmount() != null) {
                                            mGrossAmount.setText(Utils.convertTwoDecimal(data.get(position).getTotalAmount()));
                                            grossAmount = Double.parseDouble(data.get(position).getTotalAmount());
                                        }
                                        if (data.get(position).getCount() > 0) {
                                            mTransactions.setText(String.valueOf(data.get(position).getCount()));
                                            totalTransactions = data.get(position).getCount();
                                        } else {
                                            mTransactions.setText("0");
                                        }

                                        if (data.get(position).getFee() != null) {
                                            processingFee = processingFee + Double.parseDouble(data.get(position).getFee());
                                        }
                                    } else if (data.get(position).getTransactionType() != null && data.get(position).getTransactionType().equalsIgnoreCase(Utils.refundtxntype)
                                            && data.get(position).getTransactionSubType() == null) {
                                        if (data.get(position).getTotalAmount() != null) {
                                            mRefunds.setText(Utils.convertTwoDecimal(data.get(position).getTotalAmount()));
                                            refunds = Double.parseDouble(data.get(position).getTotalAmount());
                                        }

                                        processingFee = processingFee + Double.parseDouble(data.get(position).getFee());
                                    } else if (data.get(position).getTransactionType() != null && data.get(position).getTransactionType().equalsIgnoreCase(Utils.monthlyServiceFeetxntype)
                                            && data.get(position).getTransactionSubType() == null) {

                                        if (data.get(position).getTotalAmount() != null) {
                                            mMISCFees.setText(Utils.convertTwoDecimal(data.get(position).getTotalAmount()));
                                            miscFee = Double.parseDouble(data.get(position).getTotalAmount());
                                        }
                                    } else if (data.get(position).getTransactionType() == null && data.get(position).getTransactionSubType() == null) {
                                        if (data.get(position).getTotalAmount() != null)
                                            mHighestTicket.setText(Utils.convertTwoDecimal(data.get(position).getTotalAmount()));

                                        if (data.get(position).getCreatedAt() != null) {
                                            mDateHighestTicket.setText(myApplication.convertZoneDateTime(data.get(position).getCreatedAt(), dateAndTime, dateResult));
                                        } else {
                                            mDateHighestTicket.setVisibility(View.GONE);
                                        }
                                    }
                                }
                                mProcessingFees.setText(Utils.convertTwoDecimal(String.valueOf(processingFee)));
                                netAmount = grossAmount - refunds - processingFee - miscFee;
                                mNetAmount.setText(Utils.convertTwoDecimal(String.valueOf(netAmount)));
                                if (grossAmount > 0 && totalTransactions >= 1) {
                                    averageTicket = grossAmount / totalTransactions;
                                } else {
                                    averageTicket = 0;
                                }
                                mAverageTicket.setText(Utils.convertTwoDecimal(String.valueOf(averageTicket)));

                            } else {
                                mGrossAmount.setText(defaultAmount);
                                mTransactions.setText("0");
                                mRefunds.setText(defaultAmount);
                                mProcessingFees.setText(defaultAmount);
                                mMISCFees.setText(defaultAmount);
                                mNetAmount.setText(defaultAmount);
                                mAverageTicket.setText(defaultAmount);
                                mHighestTicket.setText(defaultAmount);
                                mDateHighestTicket.setText("");
                            }
                        } else {
                            mGrossAmount.setText(defaultAmount);
                            mTransactions.setText("0");
                            mRefunds.setText(defaultAmount);
                            mProcessingFees.setText(defaultAmount);
                            mMISCFees.setText(defaultAmount);
                            mNetAmount.setText(defaultAmount);
                            mAverageTicket.setText(defaultAmount);
                            mHighestTicket.setText(defaultAmount);
                            mDateHighestTicket.setText("");
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        businessDashboardViewModel.getMerchantActivityRespMutableLiveData().observe(getViewLifecycleOwner(), new Observer<MerchantActivityResp>() {
            @Override
            public void onChanged(MerchantActivityResp merchantActivityResp) {
                try {
                    if (merchantActivityResp != null && merchantActivityResp.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (merchantActivityResp.getData() != null) {
                            // for SeekBar Graph
                            userData.setEarningList(merchantActivityResp.getData().getEarnings());

                            mSbTodayVolume.setEnabled(true);
                            if (merchantActivityResp.getData().getEarnings() != null && merchantActivityResp.getData().getEarnings().size() > 0)
                                mSbTodayVolume.setProgressWithText(merchantActivityResp.getData().getEarnings().get(merchantActivityResp.getData().getEarnings().size() - 1).getKey(), userData.getEarningList());
                            else
                                mSbTodayVolume.setProgressWithText(0, userData.getEarningList());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        businessDashboardViewModel.getRollingRuleResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<RollingRuleResponse>() {
            @Override
            public void onChanged(RollingRuleResponse ruleResponse) {
                if (ruleResponse != null) {
                    if (ruleResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        if (ruleResponse.getData() != null) {
                            reserveRules = "Reserve Rule: " + ruleResponse.getData().getReserveAmount().split("\\.")[0] + "% per Sale Order with a " + ruleResponse.getData().getReservePeriod() + " day[s] rolling period.";
                            SpannableString spannableString = new SpannableString(reserveRules);
                            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/opensans_bold.ttf");
                            spannableString.setSpan(new CustomTypefaceSpan("", font), 14, reserveRules.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                            if (!reserveRules.equals("") && reserveRules != null) {
//                                reserveRuleTV.setText(Html.fromHtml("<b>"+reserveRules+"</b>"));
                                reserveRuleTV.setText(spannableString);
                            }

                        }
                    } else {
//                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveDetailsActivity.this, "", ruleResponse.getError().getFieldErrors().get(0));
                    }
                }

            }
        });

        businessDashboardViewModel.getBusinessWalletResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BusinessWalletResponse>() {
            @Override
            public void onChanged(BusinessWalletResponse businessWalletResponse) {
                walletCount++;
                try {
                    if (businessWalletResponse != null) {
                        myApplication.setWalletResponseData(businessWalletResponse.getData());
                        if (businessWalletResponse.getData() != null && businessWalletResponse.getData().getWalletNames() != null && businessWalletResponse.getData().getWalletNames().size() > 0) {
                            myApplication.setGBTBalance(businessWalletResponse.getData().getWalletNames().get(0).getAvailabilityToUse(),
                                    businessWalletResponse.getData().getWalletNames().get(0).getWalletType());
                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (walletCount == 3) {
                    walletCount = 0;
                    updateUIAfterWalletBalance();
                }
            }
        });

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BiometricTokenResponse>() {
            @Override
            public void onChanged(BiometricTokenResponse biometricTokenResponse) {
                if (biometricTokenResponse != null) {
                    if (biometricTokenResponse.getStatus().toLowerCase().equals("success")) {
                        if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
//                            Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                            tokenReq(biometricTokenResponse.getData().getRequestToken());
                        }
                    }
                }
            }
        });

        dashboardViewModel.getTransactionListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<TransactionList>() {
            @Override
            public void onChanged(TransactionList transactionList) {
                if (transactionList.getData().getItems().getPostedTransactions().size() > 0) {
                    if (transactionList.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        LogUtils.d(TAG, "list" + transactionList.getData().getItems().getPostedTransactions());
                        mTvMerchantTransactions.setTextColor(getResources().getColor(R.color.primary_color));
                        mTvMerchantTransactions.setClickable(true);
                        monthlyVolumeViewLl.setVisibility(View.GONE);
                    }
                } else {
                    mTvMerchantTransactions.setTextColor(getResources().getColor(R.color.dark_gray));
                    mTvMerchantTransactions.setClickable(false);
                    monthlyVolumeViewLl.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void updateUIAfterWalletBalance() {
        Double merchantBalance = getMerchantBalance();
        merchantBalanceTV.setText(Utils.convertBigDecimalUSDC(String.valueOf(merchantBalance)));
        if (merchantBalance != null && merchantBalance == 0.00) {
            businessIdentityVerificationViewModel.getDBAInfo();
        } else {
            monthlyVolumeViewLl.setVisibility(View.GONE);
        }
        showReserveReleaseBalance();
    }

    private void showData(List<BatchPayoutListItems> items) {
        showBatchPayouts(items);
    }

    private void setMonthlyVolumeData() {
        monthlyVolumeViewLl.setVisibility(View.VISIBLE);
        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                && myApplication.getMyProfile().getData().getCompanyName() != null) {
            mTvOfficiallyVerified.setText(getResources().getString(R.string.business_officially_verified, myApplication.getMyProfile().getData().getCompanyName()));
        }
        DBAInfoResp resp = myApplication.getDbaInfoResp();
        if (resp != null && resp.getData() != null) {
            mTvMonthlyVolume.setText("$" + Utils.convertBigDecimalUSDC(resp.getData().getMonthlyProcessingVolume()));
            mTvHighTickets.setText("$" + Utils.convertBigDecimalUSDC(resp.getData().getHighTicket()));
        }
    }

    private void getWalletData() {
        WalletRequest walletRequest = new WalletRequest();
        walletRequest.setWalletType(Utils.MERCHANT);
//        walletRequest.setUserId(String.valueOf(myApplication.getLoginUserId()));
        businessDashboardViewModel.meMerchantWallet(walletRequest);

        walletRequest.setWalletType(Utils.RESERVE);
        businessDashboardViewModel.meMerchantWallet(walletRequest);

        walletRequest.setWalletType(Utils.TOKEN);
        businessDashboardViewModel.meMerchantWallet(walletRequest);
    }

    private void setBusinessData() {
        batchReq();
        TransactionListRequest transactionListRequest = new TransactionListRequest();
        transactionListRequest.setTransactionType(getDefaultTransactionTypes());
        transactionListRequest.setPageSize(String.valueOf(Utils.pageSize));
        transactionsAPI(transactionListRequest);
//        ((BusinessDashboardActivity) getActivity()).showUserData(mIvUserIcon, mTvUserName, mTvUserIconText);
        ((BusinessDashboardActivity) getActivity()).showUserData();
        getWalletData();
        cvReserveView.setVisibility(myApplication.isReserveEnabled() ? View.VISIBLE : View.GONE);
        if (myApplication.isReserveEnabled()) {
            businessDashboardViewModel.getRollingRuleDetails();
        }
        getProcessingVolume(todayValue);
        if (myApplication.isReserveEnabled()) {
            reserveReq();
        }
//        mSbTodayVolume.setEnabled(true);
//        mSbTodayVolume.setProgressWithText(0,userData);
        mTvReserveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inReleases = new Intent(getActivity(), ReserveReleasesActivity.class);
                startActivity(inReleases);
            }
        });

        mPayoutHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BusinessBatchPayoutSearchActivity.class));
            }
        });
    }

    private void showBatchNowDialog(BatchPayoutListItems batchNow) {
        BatchNowDialog batchNowDialog = new BatchNowDialog(getActivity(), batchNow, myApplication.getGBTBalance());
        batchNowDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(Utils.Swiped)) {
                    batchId = (String) value;
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(getActivity())) {
                        if (isBiometric && ((isTouchId && Utils.isFingerPrint(getActivity())) || (isFaceLock))) {
                            Utils.checkAuthentication(BusinessDashboardFragment.this, CODE_AUTHENTICATION_VERIFICATION);
                        }
                    } else {
                        launchPinActivity();
                    }
                }
            }
        });
        batchNowDialog.show();
    }


    private void launchPinActivity() {
        Intent inPin = new Intent(getActivity(), ValidatePinActivity.class);
        inPin.putExtra(Utils.ACTION_TYPE, Utils.batchnowActionType);
        pinActivityResultLauncher.launch(inPin);
    }

    ActivityResultLauncher<Intent> pinActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    LogUtils.v(TAG, "RESULT_OK" + result);
                    tokenReq(result.getData().getStringExtra(Utils.TRANSACTION_TOKEN));
                }
            });

    private void tokenReq(String token) {
        BatchNowPaymentRequest request = new BatchNowPaymentRequest();
        request.setRequestToken(token);
        request.setPayoutId(batchId);
        businessDashboardViewModel.batchNowSlideData(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_AUTHENTICATION_VERIFICATION) {
            switch (resultCode) {
                case RESULT_OK:
                    try {
                        BiometricTokenRequest request = new BiometricTokenRequest();
                        request.setDeviceId(Utils.getDeviceID());
                        request.setMobileToken(myApplication.getStrMobileToken());
                        request.setActionType(Utils.batchnowActionType);
                        coyniViewModel.biometricToken(request);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                case RESULT_CANCELED:
                    launchPinActivity();
                    break;
            }
        }
    }

//    public void setFaceLock() {
//        try {
//            isFaceLock = false;
//            String value = dbHandler.getFacePinLock();
//            if (value != null && value.equals("true")) {
//                isFaceLock = true;
//                myApplication.setLocalBiometric(true);
//            } else {
//                isFaceLock = false;
//                myApplication.setLocalBiometric(false);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void setTouchId() {
//        try {
//            isTouchId = false;
//            String value = dbHandler.getThumbPinLock();
//            if (value != null && value.equals("true")) {
//                isTouchId = true;
//                myApplication.setLocalBiometric(true);
//            } else {
//                isTouchId = false;
////                myApplication.setLocalBiometric(false);
//                if (!isFaceLock) {
//                    myApplication.setLocalBiometric(false);
//                }
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public void showProcessingVolumeDialog() {
        ProcessingVolumeDialog processingVolumeDialog = new ProcessingVolumeDialog(getActivity());
        processingVolumeDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action != null) {
                    getProcessingVolume(action);
                }

            }
        });
        processingVolumeDialog.show();
    }

    private void getProcessingVolume(String action) {
        switch (action) {
            case todayValue: {
                mTvProcessingVolume.setText(action + "  ");
                mTicketsLayout.setVisibility(View.GONE);
                mSbTodayVolume.setVisibility(View.VISIBLE);
                saleOrdersText.setVisibility(View.VISIBLE);
                strFromDate = myApplication.convertZoneDateTime(getCurrentTimeString(), dateAndTime, date) + startTime;
                strToDate = myApplication.convertZoneDateTime(getCurrentTimeString(), dateAndTime, date) + endTime;
                businessActivityAPICall(strFromDate, strToDate);
                commissionActivityCall(strFromDate, strToDate);
            }
            break;
            case yesterdayValue: {
                mTvProcessingVolume.setText(action);
                mTicketsLayout.setVisibility(View.GONE);
                mSbTodayVolume.setVisibility(View.VISIBLE);
                saleOrdersText.setVisibility(View.VISIBLE);
                strFromDate = myApplication.convertZoneDateTime(getYesterdayDateString(), dateAndTime, date) + startTime;
                strToDate = myApplication.convertZoneDateTime(getYesterdayDateString(), dateAndTime, date) + endTime;
                businessActivityAPICall(strFromDate, strToDate);
                commissionActivityCall(strFromDate, strToDate);
            }
            break;
            case monthDate: {
                mTvProcessingVolume.setText(action);
                mTicketsLayout.setVisibility(View.VISIBLE);
                mSbTodayVolume.setVisibility(View.GONE);
                saleOrdersText.setVisibility(View.GONE);
                strFromDate = myApplication.convertZoneDateTime(getFirstDayOfMonthString(), dateAndTime, date) + startTime;
                strToDate = myApplication.convertZoneDateTime(getCurrentTimeString(), dateAndTime, date) + endTime;
                businessActivityAPICall(strFromDate, strToDate);
            }
            break;
            case lastMonthDate: {
                mTvProcessingVolume.setText(action);
                mTicketsLayout.setVisibility(View.VISIBLE);
                mSbTodayVolume.setVisibility(View.GONE);
                saleOrdersText.setVisibility(View.GONE);
                strFromDate = myApplication.convertZoneDateTime(getPreviousMonthFirstDate(), dateAndTime, date) + startTime;
                strToDate = myApplication.convertZoneDateTime(getPreviousMonthLastDate(), dateAndTime, date) + endTime;
                businessActivityAPICall(strFromDate, strToDate);

            }
            break;
            case customDate: {
                saleOrdersText.setVisibility(View.GONE);
                DateRangePickerDialog dateRangePickerDialog = new DateRangePickerDialog(getActivity());
                dateRangePickerDialog.show();

                dateRangePickerDialog.setOnDialogClickListener(new OnDialogClickListener() {

                    @Override
                    public void onDialogClicked(String action, Object value) {
                        if (action.equalsIgnoreCase(Utils.datePicker)) {

                            rangeDates = (RangeDates) value;
                            if (rangeDates != null) {
                                mTicketsLayout.setVisibility(View.GONE);
                                mSbTodayVolume.setVisibility(View.GONE);
                                String fromDate = rangeDates.getUpdatedFromDate() + midTime;
                                String toDate = rangeDates.getUpdatedToDate().trim() + midTime;
                                strFromDate = Utils.convertZoneDateTime(fromDate, "MM-dd-yyyy HH:mm:ss", date, "UTC") + startTime;
                                strToDate = Utils.convertZoneDateTime(toDate, "MM-dd-yyyy HH:mm:ss", date, "UTC") + endTime;
                                mTvProcessingVolume.setText(R.string.custom_date_range);
                                businessActivityAPICall(strFromDate, strToDate);
                            }
                        }
                    }
                });

            }
            break;
        }
    }

    private void commissionActivityCall(String strFromDate, String strToDate) {
        MerchantActivityRequest request = new MerchantActivityRequest();
        request.setStartDate(strFromDate);
        request.setEndDate(strToDate);

        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null) {
            request.setUserId("" + myApplication.getMyProfile().getData().getId());
        }
        businessDashboardViewModel.merchantActivity(request);

    }

    private void initiateBatchNow() {
        BatchNowRequest request = new BatchNowRequest();
        request.setPayoutType(Utils.batchNow);
        ArrayList<Integer> status = new ArrayList<>();
        status.add(Utils.open);
        request.setStatus(status);
        ((BusinessDashboardActivity) getActivity()).showProgressDialog();
        businessDashboardViewModel.getBatchNowData(request);
    }

    private void batchReq() {
        RollingListRequest listRequest = new RollingListRequest();
        listRequest.setPayoutType(Utils.batchNow);
        businessDashboardViewModel.getRollingListData(listRequest);
    }

    private void reserveReq() {
        if (myApplication.isReserveEnabled()) {
            businessDashboardViewModel.getReserveList();
        }
    }

    private void showBatchPayouts(List<BatchPayoutListItems> listItems) {
        if (listItems != null && listItems.size() > 0) {
            batchNoTransaction.setVisibility(View.GONE);
            Collections.sort(listItems, Collections.reverseOrder());
            int i = 0;
            boolean isOpen = false, isPaid = false;
            while (i < listItems.size()) {
                if (listItems.get(i).getStatus().equalsIgnoreCase(Utils.OPEN) && !isOpen) {

                    String amount = listItems.get(i).getTotalAmount();
                    String amt = Utils.convertBigDecimalUSDC((amount));
                    nextPayoutAmountTV.setText(amt);

                    if (amt.equals("0.00")) {
                        mCvBatchNow.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                        mCvBatchNow.setClickable(false);
                    } else {
                        mCvBatchNow.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                        mCvBatchNow.setClickable(true);
                        nextPayoutAmountTV.setText(amt);
                    }
                    Date d = new Date();
                    d.setHours(23);
                    d.setMinutes(59);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy @ hh:mma");
                    nxtPayoutDatenTimeTV.setText(dateFormat.format(d).toLowerCase());
//                    String date = listItems.get(i).getCreatedAt();
//                    if (date.contains(".")) {
//                        String res = date.substring(0, date.lastIndexOf("."));
//                        nxtPayoutDatenTimeTV.setText(myApplication.convertZoneDateTime(res, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma").toLowerCase());
//                    } else {
//                        Log.d("date format", date);
//                    }
                    isOpen = true;
                } else if (listItems.get(i).getStatus().equalsIgnoreCase(Utils.PAID) && !isPaid) {
                    String Amount = listItems.get(i).getTotalAmount();
                    lastPayoutAmountTV.setText(Utils.convertBigDecimalUSDC((Amount)));
                    String date1 = listItems.get(i).getUpdatedAt();
                    if (date1 != null && date1.contains(".")) {
                        String res = date1.substring(0, date1.lastIndexOf("."));
                        lastPayoutDate.setText(myApplication.convertZoneDateTime(res, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma").toLowerCase());
                    } else {
//                        lastPayoutDate.setText("N/A");
                    }
                    isPaid = true;
                }
                if (isPaid && isOpen) {
                    break;
                } else {
                    i++;
                }
            }

            LinearLayout payoutsList = mCurrentView.findViewById(R.id.payoutsLayoutLL);
            payoutsList.removeAllViews();
            int j = 0, paidItems = 0;
            while (j < listItems.size() && paidItems < 5) {
                batchView.setVisibility(View.GONE);
                View xmlView = getLayoutInflater().inflate(R.layout.batch_payouts_dashboard, null);
                if (listItems.get(j).getStatus().equalsIgnoreCase(Utils.PAID) ||
                        listItems.get(j).getStatus().equalsIgnoreCase(Utils.INPROGRESS) ||
                        listItems.get(j).getStatus().equalsIgnoreCase(Utils.MERCHANT_TRANSACTION_FAILED)) {
                    TextView payoutDate = xmlView.findViewById(R.id.batchPayoutDateTV);
                    TextView payoutManualTV = xmlView.findViewById(R.id.payoutManualTV);
                    String listDate = listItems.get(j).getUpdatedAt();
                    if (listDate != null && listDate.contains(".")) {
                        String listD = listDate.substring(0, listDate.lastIndexOf("."));
                        payoutDate.setText(myApplication.convertZoneDateTime(listD, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma").toLowerCase());
                    } else {
//                        payoutDate.setText("N/A");
                    }
                    try {
                        String type = listItems.get(j).getProcessType();
                        if (type != null && type.equalsIgnoreCase(Utils.processType)) {
                            payoutManualTV.setVisibility(View.VISIBLE);
                        } else {
                            payoutManualTV.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    TextView totalAmount = xmlView.findViewById(R.id.payoutAmountTV);
                    totalAmount.setText(Utils.convertBigDecimalUSDC(listItems.get(j).getTotalAmount()));
                    payoutsList.addView(xmlView);
                    paidItems++;
                } else {
                    Log.d(TAG, "open and inprogress Batch Payouts");

                }
                j++;
            }

            if (paidItems == 0) {
                batchView.setVisibility(View.VISIBLE);
            }
        } else {
            batchNoTransaction.setVisibility(View.VISIBLE);
            batchView.setVisibility(View.VISIBLE);
            mPayoutHistory.setVisibility(View.GONE);
            tv_PayoutNoHistory.setVisibility(View.VISIBLE);
            mCvBatchNow.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            mCvBatchNow.setClickable(false);

        }
        Log.d(TAG, "No Batch Payouts for this user");

    }

    private void showReserveReleaseBalance() {
        Double amt = 0.0;
        if (myApplication.getCurrentUserData().getReserveGBTBalance() != null) {
            amt += myApplication.getCurrentUserData().getReserveGBTBalance();
        }
        mTvReserveBalance.setText(Utils.convertBigDecimalUSDC(String.valueOf(amt)));
    }

    private void showReserveRelease(ReserveListData listData) {
        showReserveReleaseBalance();
        mTvReserveList.setVisibility(View.VISIBLE);

        if (listData.getNextReserveReleaseAmount() != null) {
            nextReleaseAmountTV.setText(listData.getNextReserveReleaseAmount());
        }

        if (listData.getNextReserveReleaseDate() != null) {
            String date = listData.getNextReserveReleaseDate();
            if (date != null && date.contains(".")) {
                date = date.substring(0, date.lastIndexOf("."));
            }
            nextReleaseNATV.setVisibility(View.GONE);
            nextReleaseDateTV.setVisibility(View.VISIBLE);
            nextReleaseDateTV.setText(myApplication.convertZoneDateTime(date, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy"));
        }
        List<ReserveListItems> items = listData.getResponseList();
        if (items != null && items.size() > 0) {

            lastReleaseDateTV.setVisibility(View.VISIBLE);
            lastReleaseNATV.setVisibility(View.GONE);
            releaseView.setVisibility(View.GONE);
            releaseNoTransaction.setVisibility(View.GONE);
            Collections.sort(items, Collections.reverseOrder());
            if (items.size() > 0) {
                ReserveListItems latest = items.get(0);
                String amount = latest.getReserveAmount();
                lastReleaseAmountTV.setText(Utils.convertBigDecimalUSDC((amount)));
                String datee = latest.getScheduledRelease();
                if (datee != null && !datee.equals("")) {
                    if (datee.contains(".")) {
                        datee = datee.substring(0, datee.lastIndexOf("."));
                    }
                    lastReleaseDateTV.setText(myApplication.convertZoneDateTime(datee, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy"));
                }

                LinearLayout payoutsList = mCurrentView.findViewById(R.id.reserveReleaseListLL);
                payoutsList.removeAllViews();
                int target = items.size() > 5 ? 5 : items.size();
                for (int j = 0; j < target; j++) {
                    View xmlView = getLayoutInflater().inflate(R.layout.dashboard_reserve_release_list, null);
                    TextView releaseDate = xmlView.findViewById(R.id.reserveListDateTV);
                    TextView payoutManualTV = xmlView.findViewById(R.id.reserveListManualTV);
                    String listDate = items.get(j).getScheduledRelease();
                    if (listDate != null) {
                        if (listDate.contains(".")) {
                            String listD = listDate.substring(0, listDate.lastIndexOf("."));
                            releaseDate.setText(myApplication.convertZoneDateTime(listD, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy"));
                        } else {
                            Log.d("listDate", listDate);
                        }
                    }
                    try {
                        String type = items.get(j).getProcessType();
                        if (type != null && type.equalsIgnoreCase(Utils.processType)) {
                            payoutManualTV.setVisibility(View.VISIBLE);
                        } else {
                            payoutManualTV.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    TextView totalAmount = xmlView.findViewById(R.id.reserveListAmountTV);
                    totalAmount.setText(Utils.convertBigDecimalUSDC(items.get(j).getReserveAmount()));
                    payoutsList.addView(xmlView);
                }
            }

        } else {
            LogUtils.v(TAG, "Reserve release summary is empty");
        }
    }

    private void businessActivityAPICall(String strFromDate, String strToDate) {
        BusinessActivityRequest request = new BusinessActivityRequest();
        request.setFromDate(strFromDate);
        request.setToDate(strToDate);
        try {
            mGrossAmount.setText(defaultAmount);
            mTransactions.setText("0");
            mRefunds.setText(defaultAmount);
            mProcessingFees.setText(defaultAmount);
            mMISCFees.setText(defaultAmount);
            mNetAmount.setText(defaultAmount);
            mAverageTicket.setText(defaultAmount);
            mHighestTicket.setText(defaultAmount);
            mDateHighestTicket.setText("");
            businessDashboardViewModel.businessActivity(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private Date firstDayOfCurrentMonth() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private Date previousMonthFirstDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);  // get Previous Month
        cal.set(Calendar.DATE, 1);      // I am setting Date 1
        Date firstDateOfPreviousMonth = cal.getTime();

        return firstDateOfPreviousMonth;
    }

    private Date previousMonthLastDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); // setting last date of previous month

        Date lastDateOfPreviousMonth = cal.getTime();
        return lastDateOfPreviousMonth;
    }

    private String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat(dateAndTime);
        return dateFormat.format(yesterday());
    }

    private String getFirstDayOfMonthString() {
        DateFormat dateFormat = new SimpleDateFormat(dateAndTime);
        return dateFormat.format(firstDayOfCurrentMonth());
    }

    private String getCurrentTimeString() {
        DateFormat dateFormat = new SimpleDateFormat(dateAndTime);
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    private String getPreviousMonthFirstDate() {
        DateFormat dateFormat = new SimpleDateFormat(dateAndTime);
        return dateFormat.format(previousMonthFirstDate());
    }

    private String getPreviousMonthLastDate() {
        DateFormat dateFormat = new SimpleDateFormat(dateAndTime);
        return dateFormat.format(previousMonthLastDate());
    }

}
