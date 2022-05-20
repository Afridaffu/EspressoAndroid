package com.greenbox.coyni.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BatchPayoutListAdapter;
import com.greenbox.coyni.dialogs.BatchNowDialog;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.DateRangePickerDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.ProcessingVolumeDialog;
import com.greenbox.coyni.model.BatchNow.BatchNowRequest;
import com.greenbox.coyni.model.BatchNow.BatchNowResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.RollingListRequest;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.DashboardReserveList.ReserveListData;
import com.greenbox.coyni.model.DashboardReserveList.ReserveListItems;
import com.greenbox.coyni.model.DashboardReserveList.ReserveListResponse;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.RangeDates;
import com.greenbox.coyni.model.business_activity.BusinessActivityData;
import com.greenbox.coyni.model.business_activity.BusinessActivityRequest;
import com.greenbox.coyni.model.business_activity.BusinessActivityResp;
import com.greenbox.coyni.model.business_id_verification.CancelApplicationResponse;
import com.greenbox.coyni.model.check_out_transactions.CheckOutModel;
import com.greenbox.coyni.model.merchant_activity.MerchantActivityRequest;
import com.greenbox.coyni.model.merchant_activity.MerchantActivityResp;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.reserverule.RollingRuleResponse;
import com.greenbox.coyni.utils.CheckOutConstants;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.SeekBarWithFloatingText;
import com.greenbox.coyni.utils.UserData;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.DashboardActivity;
import com.greenbox.coyni.view.NotificationsActivity;
import com.greenbox.coyni.view.business.ApplicationCancelledActivity;
import com.greenbox.coyni.view.business.BusinessAdditionalActionRequiredActivity;
import com.greenbox.coyni.view.business.BusinessBatchPayoutSearchActivity;
import com.greenbox.coyni.view.business.BusinessCreateAccountsActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.view.business.BusinessRegistrationTrackerActivity;
import com.greenbox.coyni.view.business.MerchantTransactionListActivity;
import com.greenbox.coyni.view.business.PayToMerchantActivity;
import com.greenbox.coyni.view.business.ReserveReleasesActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.journeyapps.barcodescanner.Util;

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
    private ImageView mIvUserIcon;
    private CardView mIvUserIconCV, cvReserveView;
    private TextView mTvUserName, mTvUserIconText, mTvReserveList, mPayoutHistory, payoutTimeTV,
            nextPayoutAmountTV, lastPayoutAmountTV, nxtPayoutDatenTimeTV;
    private LinearLayout mLlIdentityVerificationReview, mLlBusinessDashboardView,
            mLlIdentityAdditionDataRequired, mLlIdentityVerificationFailedView,
            mLlBuyTokensFirstTimeView, mLlProcessingVolume, mLlGetStartedView,
            payoutsXmlLL, payoutsLayoutLL, monthlyVolumeViewLl;
    private TextView mTvIdentityReviewCancelMessage, mTvProcessingVolume, mTvContactUs;
    private BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    private CardView mCvAdditionalDataContinue;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private RelativeLayout mUserIconRelativeLayout, notificationsRL;
    private TextView mTvOfficiallyVerified, mTvMerchantTransactions, batchPayoutDateTV, payoutAmountTV, cynTV;
    private TextView lastPayoutDate, mTvReserveBalance, merchantBalanceTV, mTvMonthlyVolume, mTvHighTickets,reserveRuleTV,rulePeriodTV;
    private CardView mCvBatchNow, mCvGetStarted;
    private Long mLastClickTimeQA = 0L;
    private DashboardViewModel mDashboardViewModel;
    private BatchPayoutListAdapter batchPayoutListAdapter;
    private RecyclerView recyclerViewPayouts;
    private List<BatchPayoutListItems> listItems;
    private TextView nextReleaseTV, nextReleaseAmountTV, nextReleaseDateTV, lastReleaseTV,
            lastReleaseAmountTV, lastReleaseDateTV, reserveListDateTV, reserveListAmountTV, sentToDescriptionTV, disable_reserve_list;
    private LinearLayout reserveReleaseListLL, reserveDetailsLL;
    private BatchNowRequest batchNowRequest = null;
    private String openAmount = "", sent = "", availbal = "";
    private int dbaID = 0, currentTimeInHours = 0;
    private String currentTimeHoursText = "";
    private String merchantBalance;
    private SeekBarWithFloatingText mSbTodayVolume;
    private Long mLastClickTime = 0L;
    static boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    private final int CODE_AUTHENTICATION_VERIFICATION = 251;
    static String strToken = "";
    private DatabaseHandler dbHandler;
    private String batchId;
    private TextView mGrossAmount, mTransactions, mRefunds, mProcessingFees, mMISCFees, mNetAmount,
            saleOrdersText, mAverageTicket, mHighestTicket, mDateHighestTicket;
    private LinearLayout mTicketsLayout;
    private TextView mIdVeriStatus;
    private UserData userData;
    private String status = "", reserveAmount = "", timeDate = "", timeDateTemp = "", reserveRules = "", releaseDate = "";


    //Processing Volume Types
    private static final String todayValue = "Today";
    private static final String yesterdayValue = "Yesterday";
    private static final String monthDate = "Month to Date";
    private static final String lastMonthDate = "Last Month";
    private static final String customDate = "Custom Date Range";
    private static final String dateAndTime = "yyyy-MM-dd HH:mm:ss";
    private static final String date = "yyyy-MM-dd";
    private static final String startTime = " 00:00:00";
    private static final String endTime = " 23:59:59";
    private static final String midTime = " 12:00:00";
    private static final String defaultAmount = "0.00";
    private RangeDates rangeDates;
    private String strFromDate, strToDate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_business_dashboard, container, false);
        initFields();

        hideAllStatusViews();
        return mCurrentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.v(TAG, "onViewCreated");
        initViewModels();
        initObservers();
    }

    private void initViewModels() {
        mDashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
        businessDashboardViewModel.getRollingRuleDetails();

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

    @Override
    public void onResume() {
        super.onResume();
        hideAllStatusViews();
        ((BusinessDashboardActivity) getActivity()).showProgressDialog();
        mDashboardViewModel.meProfile();
    }

    @Override
    public void updateData() {

    }

    private void initFields() {
        mUserIconRelativeLayout = mCurrentView.findViewById(R.id.rl_user_icon_layout);
        myApplication = (MyApplication) getActivity().getApplicationContext();
        userData = myApplication.getCurrentUserData();
        mIvUserIcon = mCurrentView.findViewById(R.id.iv_user_icon);
        mIvUserIconCV = mCurrentView.findViewById(R.id.iv_user_icon_CV);
        mTvUserName = mCurrentView.findViewById(R.id.tv_user_name);
        mTvUserIconText = mCurrentView.findViewById(R.id.tv_user_icon_text);
        mCvAdditionalDataContinue = mCurrentView.findViewById(R.id.cv_additional_data_continue);
        mLlBusinessDashboardView = mCurrentView.findViewById(R.id.ll_business_dashboard_view);
        mLlIdentityAdditionDataRequired = mCurrentView.findViewById(R.id.ll_identity_additional_data);
        mLlIdentityVerificationReview = mCurrentView.findViewById(R.id.ll_identity_verification_review);
        mLlIdentityVerificationFailedView = mCurrentView.findViewById(R.id.ll_identity_verification_failed);
        mLlBuyTokensFirstTimeView = mCurrentView.findViewById(R.id.ll_buy_tokens_first_time);
        mLlGetStartedView = mCurrentView.findViewById(R.id.ll_get_started_view);
        mTvIdentityReviewCancelMessage = mCurrentView.findViewById(R.id.tv_identity_review_cancel_text);
        notificationsRL = mCurrentView.findViewById(R.id.notificationsRL);
        mTvOfficiallyVerified = mCurrentView.findViewById(R.id.tv_officially_verified);
        mLlProcessingVolume = mCurrentView.findViewById(R.id.ll_processing_volume);
        mTvProcessingVolume = mCurrentView.findViewById(R.id.tv_processing_volume);
        mTvMerchantTransactions = mCurrentView.findViewById(R.id.tv_merchant_transactions);
        mCvBatchNow = mCurrentView.findViewById(R.id.cv_batch_now);
        mCvGetStarted = mCurrentView.findViewById(R.id.cv_app_get_started);
        mTvContactUs = mCurrentView.findViewById(R.id.contactUSTV);
        mTvReserveList = mCurrentView.findViewById(R.id.tv_reserve_list);
        mPayoutHistory = mCurrentView.findViewById(R.id.tv_PayoutFullHistory);
        merchantBalanceTV = mCurrentView.findViewById(R.id.merchant_balance_tv);
        mTvReserveBalance = mCurrentView.findViewById(R.id.tv_reserve_balance);
        mSbTodayVolume = mCurrentView.findViewById(R.id.sb_today_volume);
        payoutTimeTV = mCurrentView.findViewById(R.id.payoutTimeTV);
        nextPayoutAmountTV = mCurrentView.findViewById(R.id.nextPayoutAmountTV);
        lastPayoutAmountTV = mCurrentView.findViewById(R.id.lastPayoutAmountTV);
        nxtPayoutDatenTimeTV = mCurrentView.findViewById(R.id.nxtPayoutDatenTimeTV);
        recyclerViewPayouts = mCurrentView.findViewById(R.id.payoutRecyclerView);
        payoutsXmlLL = mCurrentView.findViewById((R.id.payoutsXmlLL));
        payoutsLayoutLL = mCurrentView.findViewById((R.id.payoutsLayoutLL));
        batchPayoutDateTV = mCurrentView.findViewById(R.id.batchPayoutDateTV);
        payoutAmountTV = mCurrentView.findViewById(R.id.payoutAmountTV);
        cynTV = mCurrentView.findViewById(R.id.cynTV);
        lastPayoutDate = mCurrentView.findViewById(R.id.lastPayoutDate);
        sentToDescriptionTV = mCurrentView.findViewById(R.id.sentToDescriptionTV);
        cvReserveView = mCurrentView.findViewById(R.id.cv_reserve_view);
        mTvMonthlyVolume = mCurrentView.findViewById(R.id.tv_monthly_volume);
        mTvHighTickets = mCurrentView.findViewById(R.id.tv_high_tickets);
        nextReleaseTV = mCurrentView.findViewById(R.id.nextReleaseTV);
        nextReleaseAmountTV = mCurrentView.findViewById(R.id.nextReleaseAmountTV);
        nextReleaseDateTV = mCurrentView.findViewById(R.id.nextReleaseDateTV);
        lastReleaseTV = mCurrentView.findViewById(R.id.lastReleaseTV);
        lastReleaseAmountTV = mCurrentView.findViewById(R.id.lastReleaseAmountTV);
        lastReleaseDateTV = mCurrentView.findViewById(R.id.lastReleaseDateTV);
        reserveListDateTV = mCurrentView.findViewById(R.id.reserveListDateTV);
        reserveListAmountTV = mCurrentView.findViewById(R.id.reserveListAmountTV);
        reserveListDateTV = mCurrentView.findViewById(R.id.reserveListDateTV);
        reserveReleaseListLL = mCurrentView.findViewById(R.id.reserveReleaseListLL);
        reserveDetailsLL = mCurrentView.findViewById(R.id.reserveDetailsLL);
        mIdVeriStatus = mCurrentView.findViewById(R.id.idVeriStatus);
        monthlyVolumeViewLl = mCurrentView.findViewById(R.id.tv_monthly_volume_view);
        dbHandler = DatabaseHandler.getInstance(getActivity());

        tv_PayoutNoHistory = mCurrentView.findViewById(R.id.tv_PayoutNoHistory);
        batchView = mCurrentView.findViewById(R.id.batchView);
        batchNoTransaction = mCurrentView.findViewById(R.id.batchNoTransaction);

        releaseView = mCurrentView.findViewById(R.id.releaseView);
        nextReleaseNATV = mCurrentView.findViewById(R.id.nextReleaseNATV);
        lastReleaseNATV = mCurrentView.findViewById(R.id.lastReleaseNATV);
        releaseNoTransaction = mCurrentView.findViewById(R.id.releaseNoTransaction);
        disable_reserve_list = mCurrentView.findViewById(R.id.disable_reserve_list);

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


        if (myApplication.getCheckOutModel() != null && myApplication.getCheckOutModel().isCheckOutFlag()) {
            ((BusinessDashboardActivity)getActivity()).showProgressDialog("connecting...");
        }

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
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            startActivity(new Intent(getActivity(), BusinessCreateAccountsActivity.class));
        });

        mCvAdditionalDataContinue.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            startActivity(new Intent(getActivity(), BusinessAdditionalActionRequiredActivity.class));
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

        mCvGetStarted.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            startTracker();
        });

    }

    private void initObservers() {
        businessDashboardViewModel.getCancelApplicationResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<CancelApplicationResponse>() {
            @Override
            public void onChanged(CancelApplicationResponse cancelApplicationResponse) {
                ((BusinessDashboardActivity) getActivity()).dismissDialog();
                if (cancelApplicationResponse != null) {
                    if (cancelApplicationResponse.getStatus() != null
                            && cancelApplicationResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        launchApplicationCancelledScreen();
                    } else {
                        String msg = getString(R.string.something_went_wrong);
                        if (cancelApplicationResponse.getError() != null
                                && cancelApplicationResponse.getError().getErrorDescription() != null
                                && !cancelApplicationResponse.getError().getErrorDescription().trim().equals("")) {
                            msg = cancelApplicationResponse.getError().getErrorDescription();
                        }
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String msg = getString(R.string.something_went_wrong);
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDashboardViewModel.getProfileMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Profile>() {
            @Override
            public void onChanged(Profile profile) {
                ((BusinessDashboardActivity) getActivity()).dismissDialog();
                try {
                    if (profile != null) {
                        myApplication.setMyProfile(profile);
                        showUserData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        businessDashboardViewModel.getRollingListResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BatchPayoutListResponse>() {
            @Override
            public void onChanged(BatchPayoutListResponse batchPayoutListResponse) {
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
                                            processingFee = Double.parseDouble(data.get(position).getFee());
                                        }
                                    } else if (data.get(position).getTransactionType() != null && data.get(position).getTransactionType().equalsIgnoreCase(Utils.refundtxntype)
                                            && data.get(position).getTransactionSubType() == null) {
                                        if (data.get(position).getTotalAmount() != null) {
                                            mRefunds.setText(Utils.convertTwoDecimal(data.get(position).getTotalAmount()));
                                            refunds = Double.parseDouble(data.get(position).getTotalAmount());
                                        }

                                        double processFee = processingFee + Double.parseDouble(data.get(position).getFee());
                                        processingFee = processFee;
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
                                            mDateHighestTicket.setText(myApplication.convertZoneDateTime(data.get(position).getCreatedAt(), dateAndTime, date));
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

                    Handler handler = new Handler();
                    if (myApplication.getCheckOutModel() != null) {
                        CheckOutModel checkOutModel = myApplication.getCheckOutModel();
                        if (checkOutModel.isCheckOutFlag() && checkOutModel.getCheckOutWalletId() != null) {
                            if (myApplication.getLoginResponse().getData().getAccountStatus().equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        try {
                                            ((BusinessDashboardActivity) getActivity()).dismissDialog();
                                            startActivity(new Intent(getContext(), PayToMerchantActivity.class)
                                                    .putExtra(CheckOutConstants.WALLET_ID, checkOutModel.getCheckOutWalletId())
                                                    .putExtra(CheckOutConstants.CheckOutAmount, checkOutModel.getCheckOutAmount()));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, 100);
                            } else {
                                ((BusinessDashboardActivity) getActivity()).dismissDialog();
                                myApplication.setCheckOutModel(new CheckOutModel());
                                Utils.displayAlertNew("Please use active user account to make payments", getContext(), "Coyni");
                            }
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
                                mSbTodayVolume.setProgressWithText(merchantActivityResp.getData().getEarnings().get(0).getKey(), userData.getEarningList());
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
                            reserveRules = ruleResponse.getData().getReserveAmount().split("\\.")[0] + "% per Sale Order with a "  + ruleResponse.getData().getReservePeriod() + " day[s] ";

                            if (!reserveRules.equals("") && reserveRules != null) {
                                rulePeriodTV.setText(reserveRules);
                            }

                        }
                    } else {
//                            Utils.displayAlert(getString(R.string.something_went_wrong), ReserveDetailsActivity.this, "", ruleResponse.getError().getFieldErrors().get(0));
                    }
                }

            }
        });

    }

    private void showData(List<BatchPayoutListItems> items) {
        showBatchPayouts(items);
    }

    private void startTracker() {
        LogUtils.d(TAG, "tracker iddddd" + myApplication.getDbaOwnerId());
        if (myApplication.getDbaOwnerId() != 0) {
            Intent inTracker = new Intent(getActivity(), BusinessRegistrationTrackerActivity.class);
            inTracker.putExtra(Utils.ADD_BUSINESS, true);
            inTracker.putExtra(Utils.ADD_DBA, true);
            startActivity(inTracker);
        } else {
            Intent inTracker = new Intent(getActivity(), BusinessRegistrationTrackerActivity.class);
            startActivity(inTracker);
        }

    }

    private void launchApplicationCancelledScreen() {
        Intent inCancelledApplication = new Intent(getActivity(), ApplicationCancelledActivity.class);
        inCancelledApplication.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activityResultLauncher.launch(inCancelledApplication);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //Display Identity Verification Failed Error
                    //showIdentityVerificationFailed();
                }
            });

    private void showUserData() {
        ((BusinessDashboardActivity) getActivity()).showUserData(mIvUserIcon, mTvUserName, mTvUserIconText);
//        LogUtils.d(TAG, "dashboardmyApplication" + myApplication.getBusinessTrackerResponse());
//        LogUtils.d(TAG, "dashboardisProfileVerified" + myApplication.getBusinessTrackerResponse().getData().isProfileVerified());
//        if (myApplication.getBusinessTrackerResponse() != null && myApplication.getBusinessTrackerResponse().getData() != null
//                && !myApplication.getBusinessTrackerResponse().getData().isProfileVerified()) {
//
//        } else
        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                && myApplication.getMyProfile().getData().getAccountStatus() != null) {
            String accountStatus = myApplication.getMyProfile().getData().getAccountStatus();
            if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus())) {
                showGetStartedView();
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus())) {
                showIdentityVerificationReview();
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())
                    || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ADDITIONAL_INFO_REQUIRED.getStatus())) {
                showAdditionalActionView();
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())
                    || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())) {
                showIdentityVerificationFailed(accountStatus);
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                showIdentityVerificationFailed(accountStatus);
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                showBusinessDashboardView();
            }
        } else {
            LogUtils.v(TAG, "myProfile is null");
        }
    }

    private void showIdentityVerificationFailed(String accountStatus) {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.VISIBLE);
        mLlGetStartedView.setVisibility(View.GONE);

        if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
            mIdVeriStatus.setText(R.string.declined_text);
        } else if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())) {
            mIdVeriStatus.setText(R.string.canceled_text);
        }

        mTvContactUs.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(Utils.mondayURL));
            startActivity(i);
        });
    }

    private void hideAllStatusViews() {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.GONE);
        mLlGetStartedView.setVisibility(View.GONE);
    }

    private void showBusinessDashboardView() {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.VISIBLE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.GONE);
        mLlGetStartedView.setVisibility(View.GONE);
        setBusinessData();
    }

    private void showAdditionalActionView() {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.VISIBLE);
        mLlIdentityVerificationFailedView.setVisibility(View.GONE);
        mLlGetStartedView.setVisibility(View.GONE);
    }

    private void showGetStartedView() {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.GONE);
        mLlGetStartedView.setVisibility(View.VISIBLE);
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

    private void setBusinessData() {
        cvReserveView.setVisibility(myApplication.isReserveEnabled() ? View.VISIBLE : View.GONE);
        batchReq();
        getProcessingVolume(todayValue);
        if (myApplication.isReserveEnabled()) {
            reserveReq();
        }
        Double merchantBalance = getMerchantBalance();
        merchantBalanceTV.setText(Utils.convertBigDecimalUSDC(String.valueOf(merchantBalance)));
        if (merchantBalance != null && merchantBalance == 0.00) {
            businessIdentityVerificationViewModel.getDBAInfo();
        } else {
            monthlyVolumeViewLl.setVisibility(View.GONE);
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


    private void showIdentityVerificationReview() {
        mLlIdentityVerificationReview.setVisibility(View.VISIBLE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.GONE);
        mLlGetStartedView.setVisibility(View.GONE);
        String message = getString(R.string.identity_review_cancel_message);
        message+= " ";
        SpannableString spannableString = new SpannableString(message);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                showCancelApplicationDialog();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        spannableString.setSpan(clickableSpan, message.length() - 11, message.length()-1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvIdentityReviewCancelMessage.setText(spannableString);
        mTvIdentityReviewCancelMessage.setMovementMethod(LinkMovementMethod.getInstance());
        mTvIdentityReviewCancelMessage.setHighlightColor(Color.TRANSPARENT);
    }

    private void showBatchNowDialog(BatchPayoutListItems batchNow) {
        BatchNowDialog batchNowDialog = new BatchNowDialog(getActivity(), batchNow, myApplication.getGBTBalance());
        batchNowDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(Utils.Swiped)) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    businessDashboardViewModel.batchNowSlideData((String) value);
//                    if ((isFaceLock || isTouchId) && Utils.checkAuthentication(getActivity())) {
////                        if (isBiometric && ((isTouchId && Utils.isFingerPrint(getActivity())) || (isFaceLock))) {
////                            Utils.checkAuthentication(getActivity(), CODE_AUTHENTICATION_VERIFICATION);
////                        }
//                    } else {
//                        businessDashboardViewModel.batchNowSlideData((String) value);
//                        Utils.showCustomToast(getActivity(), getResources().getString(R.string.Successfully_Closed_Batch), R.drawable.ic_custom_tick, "Batch");
//                    }
                }
            }
        });
        batchNowDialog.show();
    }

//    private void launchPinActivity(String batchNow) {
//        batchId = batchNow;
//        Intent inPin = new Intent(getActivity(), PINActivity.class);
//        inPin.putExtra("TYPE", "ENTER");
//        inPin.putExtra("screen", "BatchNow");
//        pinActivityResultLauncher.launch(inPin);
//    }
//
//    private void batchAPI(String batchId) {
//     BatchNowSlideRequest req = new BatchNowSlideRequest();
//        req.setBatchId(batchId);
//        businessDashboardViewModel.batchNowSlideData(req.getBatchId());
//    }

//    ActivityResultLauncher<Intent> pinActivityResultLauncher = registerForActivityResult(
//            new ActivityResultContracts.StartActivityForResult(),
//            result -> {
//                if (result.getResultCode() == Activity.RESULT_OK) {
//                    //Call API Here
//                    LogUtils.v(TAG, "RESULT_OK" + result);
//                    businessDashboardViewModel.batchNowSlideData(batchId);
//                     Utils.showCustomToast(getActivity(), getResources().getString(R.string.Successfully_Closed_Batch), R.drawable.ic_custom_tick, "Batch");
//                }
//            });

    public void setToken() {
        strToken = dbHandler.getPermanentToken();
    }

    public void setFaceLock() {
        try {
            isFaceLock = false;
            String value = dbHandler.getFacePinLock();
            if (value != null && value.equals("true")) {
                isFaceLock = true;
                myApplication.setLocalBiometric(true);
            } else {
                isFaceLock = false;
                myApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTouchId() {
        try {
            isTouchId = false;
            String value = dbHandler.getThumbPinLock();
            if (value != null && value.equals("true")) {
                isTouchId = true;
                myApplication.setLocalBiometric(true);
            } else {
                isTouchId = false;
                myApplication.setLocalBiometric(false);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
        mTvProcessingVolume.setText(action);
        switch (action) {
            case todayValue: {
                mTvProcessingVolume.setText(action + "  ");
                mTicketsLayout.setVisibility(View.GONE);
                mSbTodayVolume.setVisibility(View.VISIBLE);
                saleOrdersText.setVisibility(View.VISIBLE);
                strFromDate = myApplication.convertZoneDateTime(getCurrentTimeString(), dateAndTime, date) + startTime;
                strToDate = myApplication.convertZoneDateTime(getCurrentTimeString(), dateAndTime, dateAndTime);
                businessActivityAPICall(strFromDate, strToDate);
                commissionActivityCall(strFromDate, strToDate);
            }
            break;
            case yesterdayValue: {
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
                mTicketsLayout.setVisibility(View.GONE);
                mSbTodayVolume.setVisibility(View.GONE);
                saleOrdersText.setVisibility(View.GONE);
                strFromDate = myApplication.convertZoneDateTime(getFirstDayOfMonthString(), dateAndTime, date) + startTime;
                strToDate = myApplication.convertZoneDateTime(getCurrentTimeString(), dateAndTime, dateAndTime);
                businessActivityAPICall(strFromDate, strToDate);
            }
            break;
            case lastMonthDate: {
                mTicketsLayout.setVisibility(View.VISIBLE);
                mSbTodayVolume.setVisibility(View.GONE);
                saleOrdersText.setVisibility(View.GONE);
                strFromDate = myApplication.convertZoneDateTime(getPreviousMonthFirstDate(), dateAndTime, date) + startTime;
                strToDate = myApplication.convertZoneDateTime(getPreviousMonthLastDate(), dateAndTime, date) + endTime;
                businessActivityAPICall(strFromDate, strToDate);

            }
            break;
            case customDate: {
                mTicketsLayout.setVisibility(View.GONE);
                mSbTodayVolume.setVisibility(View.GONE);
                saleOrdersText.setVisibility(View.GONE);
                DateRangePickerDialog dateRangePickerDialog = new DateRangePickerDialog(getActivity());
                dateRangePickerDialog.show();

                dateRangePickerDialog.setOnDialogClickListener(new OnDialogClickListener() {

                    @Override
                    public void onDialogClicked(String action, Object value) {
                        if (action.equalsIgnoreCase(Utils.datePicker)) {

                            rangeDates = (RangeDates) value;
                            if (rangeDates != null) {
                                String fromDate = rangeDates.getUpdatedFromDate() + midTime;
                                String toDate = rangeDates.getUpdatedToDate().trim() + midTime;
                                strFromDate = myApplication.convertZoneDateTime(fromDate, dateAndTime, date) + startTime;
                                strToDate = myApplication.convertZoneDateTime(toDate, dateAndTime, date) + endTime;

                                businessActivityAPICall(strFromDate, strToDate);
//                                Toast.makeText(getActivity(), strFromDate + strToDate, Toast.LENGTH_LONG).show();
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
//        request.setDuration(value.toUpperCase());
        request.setStartDate(strFromDate);
        request.setEndDate(strToDate);

        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null)
            request.setUserId("" + myApplication.getMyProfile().getData().getId());
        businessDashboardViewModel.merchantActivity(request);

    }


    private void showCancelApplicationDialog() {
        DialogAttributes dialogAttributes = new DialogAttributes(getString(R.string.cancel_application),
                getString(R.string.cancel_application_message), getString(R.string.yes),
                getString(R.string.no));
        CustomConfirmationDialog customConfirmationDialog = new CustomConfirmationDialog
                (getActivity(), dialogAttributes);

        customConfirmationDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equalsIgnoreCase(getString(R.string.yes))) {
                    ((BusinessDashboardActivity) getActivity()).showProgressDialog();
                    businessDashboardViewModel.cancelMerchantApplication();
                }
            }
        });
        customConfirmationDialog.show();
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
        int i = 0;
        boolean isOpen = false, isPaid = false;
        if (listItems != null && listItems.size() > 0 && listItems.get(i).getStatus().equalsIgnoreCase(Utils.OPEN) && !isPaid) {
            batchNoTransaction.setVisibility(View.GONE);
            Collections.sort(listItems, Collections.reverseOrder());
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
                    String date = listItems.get(i).getCreatedAt();
                    if (date.contains(".")) {
                        String res = date.substring(0, date.lastIndexOf("."));
                        nxtPayoutDatenTimeTV.setText(myApplication.convertZoneDateTime(res, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma"));
                    } else {
                        Log.d("date format", date);
                    }
                    isOpen = true;
                }
//                else if (listItems.get(i).getStatus().equalsIgnoreCase(Utils.PAID) && !isPaid) {
//                    String Amount = listItems.get(i).getTotalAmount();
//                    lastPayoutAmountTV.setText(Utils.convertBigDecimalUSDC((Amount)));
//
//                    String date1 = listItems.get(i).getCreatedAt();
//                    if (date1.contains(".")) {
//                        String res = date1.substring(0, date1.lastIndexOf("."));
//                        lastPayoutDate.setText(myApplication.convertZoneDateTime(res, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma"));
//                    } else {
//                        Log.d("jkhj", date1);
//                    }
//                    isPaid = true;
//                }
                else if (listItems.get(i).getStatus().equalsIgnoreCase(Utils.INPROGRESS) && !isPaid) {
                    String Amount = listItems.get(i).getTotalAmount();
                    lastPayoutAmountTV.setText(Utils.convertBigDecimalUSDC((Amount)));

                    String date1 = listItems.get(i).getCreatedAt();
                    if (date1.contains(".")) {
                        String res = date1.substring(0, date1.lastIndexOf("."));
                        lastPayoutDate.setText(myApplication.convertZoneDateTime(res, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma"));
                    } else {
                        Log.d("jkhj", date1);
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
                View xmlView = getLayoutInflater().inflate(R.layout.batch_payouts_dashboard, null);
                if (listItems.get(j).getStatus().equalsIgnoreCase(Utils.PAID)) {
                    TextView payoutDate = xmlView.findViewById(R.id.batchPayoutDateTV);
                    TextView payoutManualTV = xmlView.findViewById(R.id.payoutManualTV);
                    String listDate = listItems.get(j).getCreatedAt();
                    if (listDate.contains(".")) {
                        String listD = listDate.substring(0, listDate.lastIndexOf("."));
                        payoutDate.setText(myApplication.convertZoneDateTime(listD, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma"));
                    } else {
                        Log.d("listDate", listDate);
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
        disable_reserve_list.setVisibility(View.GONE);

        if (listData.getNextReserveReleaseAmount() != null) {
            nextReleaseAmountTV.setText(listData.getNextReserveReleaseAmount());
        }

        if (listData.getNextReserveReleaseDate() != null) {
            String date = listData.getNextReserveReleaseDate();
            if (date.contains(".")) {
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
                String amount = latest.getTotalAmount();
                lastReleaseAmountTV.setText(Utils.convertBigDecimalUSDC((amount)));
                String datee = latest.getCreatedAt();
                if (datee != null && !datee.equals("")) {
                    if (datee.contains(".")) {
                        datee = datee.substring(0, datee.lastIndexOf("."));
                    }
                    lastReleaseDateTV.setText(myApplication.convertZoneDateTime(datee, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy"));
                }

                LinearLayout payoutsList = mCurrentView.findViewById(R.id.reserveReleaseListLL);
                payoutsList.removeAllViews();
                for (int j = 0; j < items.size(); j++) {
                    View xmlView = getLayoutInflater().inflate(R.layout.dashboard_reserve_release_list, null);
                    TextView releaseDate = xmlView.findViewById(R.id.reserveListDateTV);
                    TextView payoutManualTV = xmlView.findViewById(R.id.reserveListManualTV);
                    String listDate = items.get(j).getCreatedAt();
                    if (listDate.contains(".")) {
                        String listD = listDate.substring(0, listDate.lastIndexOf("."));
                        releaseDate.setText(myApplication.convertZoneDateTime(listD, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy"));
                    } else {
                        Log.d("listDate", listDate);
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
                    totalAmount.setText(Utils.convertBigDecimalUSDC(items.get(j).getTotalAmount()));
                    payoutsList.addView(xmlView);
                }
            }

        } else {
//            releaseNoTransaction.setVisibility(View.VISIBLE);
//            mTvReserveList.setVisibility(View.GONE);
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
