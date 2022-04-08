package com.greenbox.coyni.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.BatchPayoutListAdapter;
import com.greenbox.coyni.dialogs.BatchNowDialog;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.ProcessingVolumeDialog;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.BusinessBatchPayout.RollingListRequest;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.EmptyRequest;
import com.greenbox.coyni.model.business_id_verification.CancelApplicationResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.NotificationsActivity;
import com.greenbox.coyni.view.business.ApplicationCancelledActivity;
import com.greenbox.coyni.view.business.BusinessAdditionalActionRequiredActivity;
import com.greenbox.coyni.view.business.BusinessBatchPayoutSearchActivity;
import com.greenbox.coyni.view.business.BusinessCreateAccountsActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.view.business.BusinessRegistrationTrackerActivity;
import com.greenbox.coyni.view.business.MerchantTransactionListActivity;
import com.greenbox.coyni.view.business.ReserveReleasesActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Business Dashboard Fragment
public class BusinessDashboardFragment extends BaseFragment {

    private View mCurrentView;
    private MyApplication myApplication;
    private ImageView mIvUserIcon;
    private CardView mIvUserIconCV;
    private TextView mTvUserName, mTvUserIconText, mTvReserveList, mPayoutHistory, payoutTimeTV, nextPayoutAmountTV, lastPayoutAmountTV, nxtPayoutDatenTimeTV;
    private LinearLayout mLlIdentityVerificationReview, mLlBusinessDashboardView,
            mLlIdentityAdditionDataRequired, mLlIdentityVerificationFailedView,
            mLlBuyTokensFirstTimeView, mLlProcessingVolume, mLlGetStartedView, payoutsXmlLL, payoutsLayoutLL;
    private TextView mTvIdentityReviewCancelMessage, mTvProcessingVolume, mTvContactUs;
    private CardView mCvAdditionalDataContinue;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private RelativeLayout mUserIconRelativeLayout, notificationsRL;
    private TextView mTvOfficiallyVerified, mTvMerchantTransactions, batchPayoutDateTV, payoutManualTV, payoutAmountTV, cynTV;
    private TextView lastPayoutDate, mTvReserveBalance, merchantBalanceTV;
    private CardView mCvBatchNow, mCvGetStarted;
    private Long mLastClickTimeQA = 0L;
    private DashboardViewModel mDashboardViewModel;
    private BatchPayoutListAdapter batchPayoutListAdapter;
    private RecyclerView recyclerViewPayouts;
    private List<BatchPayoutListItems> listItems;
    private TextView nextReleaseTV, nextReleaseAmountTV, nextReleaseDateTV, lastReleaseTV, lastReleaseAmountTV, lastReleaseDateTV, reserveListDateTV, reserveListAmountTV;
    private LinearLayout reserveReleaseListLL, reserveDetailsLL;
    private boolean isBatch = false;

    private int dbaID = 0;
    private String merchantBalance;

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
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
    }

    private void getMerchantBalance() {
        Double amt = 0.0;
        if (myApplication.getGBTBalance() != null) {
            amt += myApplication.getGBTBalance();
        }
        if (myApplication.getMerchantBalance() != null) {
            amt += myApplication.getMerchantBalance();
        }
        merchantBalanceTV.setText(Utils.convertBigDecimalUSDC(String.valueOf(amt)));

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
        mPayoutHistory = mCurrentView.findViewById(R.id.tv_PayoutHistory);
        merchantBalanceTV = mCurrentView.findViewById(R.id.merchant_balance_tv);
        mTvReserveBalance = mCurrentView.findViewById(R.id.tv_reserve_balance);
        payoutTimeTV = mCurrentView.findViewById(R.id.payoutTimeTV);
        nextPayoutAmountTV = mCurrentView.findViewById(R.id.nextPayoutAmountTV);
        lastPayoutAmountTV = mCurrentView.findViewById(R.id.lastPayoutAmountTV);
        nxtPayoutDatenTimeTV = mCurrentView.findViewById(R.id.nxtPayoutDatenTimeTV);
        recyclerViewPayouts = mCurrentView.findViewById(R.id.payoutRecyclerView);
        payoutsXmlLL = mCurrentView.findViewById((R.id.payoutsXmlLL));
        payoutsLayoutLL = mCurrentView.findViewById((R.id.payoutsLayoutLL));
        batchPayoutDateTV = mCurrentView.findViewById(R.id.batchPayoutDateTV);
        payoutManualTV = mCurrentView.findViewById(R.id.payoutManualTV);
        payoutAmountTV = mCurrentView.findViewById(R.id.payoutAmountTV);
        cynTV = mCurrentView.findViewById(R.id.cynTV);
        lastPayoutDate = mCurrentView.findViewById(R.id.lastPayoutDate);


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
            showBatchNowDialog();
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

        businessDashboardViewModel.getBatchPayoutListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<BatchPayoutListResponse>() {
            @Override
            public void onChanged(BatchPayoutListResponse batchPayoutListResponse) {
                if (batchPayoutListResponse != null) {
                    if (batchPayoutListResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                        if (batchPayoutListResponse.getData() != null && batchPayoutListResponse.getData().getItems() != null) {
                            showData(batchPayoutListResponse.getData().getItems());
                        } else {
                            Log.d(TAG, "No items found");
                        }
                    }
                    Log.d(TAG, "Error");
                }

            }
        });
    }

    private void showData(List<BatchPayoutListItems> items) {
        if(isBatch) {
            showBatchPayouts(items);
            reserveReq();
        } else {
            showReserveRelease(items);
        }
    }

    private void startTracker() {
        LogUtils.d(TAG, "tracker iddddd" + myApplication.getDbaOwnerId());
        if (myApplication.getDbaOwnerId() != 0) {
            Intent inTracker = new Intent(getActivity(), BusinessRegistrationTrackerActivity.class);
            inTracker.putExtra("ADDBUSINESS", true);
            inTracker.putExtra("ADDDBA", true);
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
        LogUtils.d(TAG, "dashboardmyApplication" + myApplication.getBusinessTrackerResponse());
        LogUtils.d(TAG, "dashboardisProfileVerified" + myApplication.getBusinessTrackerResponse().getData().isProfileVerified());
        if (myApplication.getBusinessTrackerResponse() != null && myApplication.getBusinessTrackerResponse().getData() != null
                && !myApplication.getBusinessTrackerResponse().getData().isProfileVerified()) {
            showGetStartedView();
        } else if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                && myApplication.getMyProfile().getData().getAccountStatus() != null) {
            String accountStatus = myApplication.getMyProfile().getData().getAccountStatus();
            if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNDER_REVIEW.getStatus()) || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.UNVERIFIED.getStatus())) {
                showIdentityVerificationReview();
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus()) || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ADDITIONAL_INFO_REQUIRED.getStatus())) {
                showAdditionalActionView();
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())
                    || accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())) {
                showIdentityVerificationFailed();
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
                showIdentityVerificationFailed();
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTIVE.getStatus())) {
                showBusinessDashboardView();
            }
        } else {
            LogUtils.v(TAG, "myProfile is null");
        }
    }

    private void showIdentityVerificationFailed() {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.VISIBLE);
        mLlGetStartedView.setVisibility(View.GONE);

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

    private void setBusinessData() {
//        RollingListRequest listRequest = new RollingListRequest();
//        listRequest.setPayoutType(Utils.batchNow);
//        businessDashboardViewModel.getPayoutListData(listRequest);
        batchReq();
        getMerchantBalance();
        mTvOfficiallyVerified.setText(getResources().getString(R.string.business_officially_verified, "[Business Name]"));
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
        // showBatchPayouts();
    }

    private void showIdentityVerificationReview() {
        mLlIdentityVerificationReview.setVisibility(View.VISIBLE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.GONE);
        mLlGetStartedView.setVisibility(View.GONE);
        String message = getString(R.string.identity_review_cancel_message);
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
        spannableString.setSpan(clickableSpan, message.length() - 10, message.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvIdentityReviewCancelMessage.setText(spannableString);
        mTvIdentityReviewCancelMessage.setMovementMethod(LinkMovementMethod.getInstance());
        mTvIdentityReviewCancelMessage.setHighlightColor(Color.TRANSPARENT);
    }

    private void showBatchNowDialog() {
        BatchNowDialog batchNowDialog = new BatchNowDialog(getActivity());
        batchNowDialog.show();
    }

    public void showProcessingVolumeDialog() {
        ProcessingVolumeDialog processingVolumeDialog = new ProcessingVolumeDialog(getActivity());
        processingVolumeDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                mTvProcessingVolume.setText(action);
            }
        });
        processingVolumeDialog.show();
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

    private void batchReq() {
        isBatch = true;
        RollingListRequest listRequest = new RollingListRequest();
        listRequest.setPayoutType(Utils.batchNow);
        businessDashboardViewModel.getRollingListData(listRequest);
    }

    private void reserveReq() {
        isBatch = false;
        RollingListRequest listRequest = new RollingListRequest();
        listRequest.setPayoutType(Utils.reserveRelease);
        businessDashboardViewModel.getRollingListData(listRequest);
    }

    private void showBatchPayouts(List<BatchPayoutListItems> listItems) {
        if (listItems != null && listItems.size() > 0) {
            int i = 0;
            Collections.sort(listItems, Collections.reverseOrder());
            boolean isOpen = false, isPaid = false;
            while (i < listItems.size()) {
                if (listItems.get(i).getStatus().equalsIgnoreCase(Utils.OPEN) && !isOpen) {
                    String amount = listItems.get(i).getTotalAmount();
                    nextPayoutAmountTV.setText(Utils.convertBigDecimalUSDC((amount)));

                    String date = listItems.get(i).getCreatedAt();
                    if (date.contains(".")) {
                        String res = date.substring(0, date.lastIndexOf("."));
                        nxtPayoutDatenTimeTV.setText(myApplication.convertZoneDateTime(res, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma"));
                    } else {
                        Log.d("date format", date);
                    }
                    isOpen = true;
                } else if (listItems.get(i).getStatus().equalsIgnoreCase(Utils.PAID) && !isPaid) {
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
                    String listDate = listItems.get(j).getCreatedAt();
                    if (listDate.contains(".")) {
                        String listD = listDate.substring(0, listDate.lastIndexOf("."));
                        payoutDate.setText(myApplication.convertZoneDateTime(listD, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy @ hh:mma"));
                    } else {
                        Log.d("listDate", listDate);
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
            Log.d(TAG, "No Batch Payouts for this user");
        }
    }

    private void showReserveReleaseBalance() {
        Double amt = 0.0;
        if (myApplication.getReserveBalance() != null) {
            amt += myApplication.getReserveBalance();
        }
        mTvReserveBalance.setText(Utils.convertBigDecimalUSDC(String.valueOf(amt)));
    }

    private void showReserveRelease(List<BatchPayoutListItems> listItems) {
        showReserveReleaseBalance();
        if (listItems != null && listItems.size() > 0) {
            int i = 0;
            Collections.sort(listItems, Collections.reverseOrder());
            boolean isOpen = false, isReleased = false;
            while (i < listItems.size()) {
                if (listItems.get(i).getStatus().equalsIgnoreCase(Utils.OPEN) && !isOpen) {
                    String amount = listItems.get(i).getTotalAmount();
                    nextReleaseAmountTV.setText(Utils.convertBigDecimalUSDC((amount)));
                    String date = listItems.get(i).getCreatedAt();
//                    nextReleaseDateTV.setText(myApplication.reserveDate(date));
                    if (date.contains(".")) {
                        String res = date.substring(0, date.lastIndexOf("."));
                        nextReleaseDateTV.setText(myApplication.convertZoneDateTime(res, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy"));
                    } else {
                        Log.d("date format", date);
                    }
                    isOpen = true;
                } else if (listItems.get(i).getStatus().equalsIgnoreCase(Utils.RELEASED) && !isReleased) {
                    String Amount = listItems.get(i).getTotalAmount();
                    lastReleaseAmountTV.setText(Utils.convertBigDecimalUSDC((Amount)));

                    String date1 = listItems.get(i).getCreatedAt();
//                    lastReleaseDateTV.setText(myApplication.reserveDate(date1));
                    if (date1.contains(".")) {
                        String res = date1.substring(0, date1.lastIndexOf("."));
                        lastReleaseDateTV.setText(myApplication.convertZoneDateTime(res, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy"));
                    } else {
                        Log.d("jkhj", date1);
                    }
                    isReleased = true;
                }

                if (isReleased && isOpen) {
                    break;
                } else {
                    i++;
                }
            }

            LinearLayout payoutsList = mCurrentView.findViewById(R.id.reserveReleaseListLL);
            payoutsList.removeAllViews();
            int j = 0, releasedItems = 0;
            while (j < listItems.size() && releasedItems < 5) {
                View xmlView = getLayoutInflater().inflate(R.layout.dashboard_reserve_release_list, null);
                if (listItems.get(j).getStatus().equalsIgnoreCase(Utils.RELEASED)) {
                    TextView releaseDate = xmlView.findViewById(R.id.reserveListDateTV);
                    String listDate = listItems.get(j).getCreatedAt();
//                    releaseDate.setText(myApplication.reserveDate(listDate));
                    if (listDate.contains(".")) {
                        String listD = listDate.substring(0, listDate.lastIndexOf("."));
                        releaseDate.setText(myApplication.convertZoneDateTime(listD, "yyyy-MM-dd HH:mm:ss", "MM/dd/yyyy"));
                    } else {
                        Log.d("listDate", listDate);
                    }
                    TextView totalAmount = xmlView.findViewById(R.id.reserveListAmountTV);
                    totalAmount.setText(Utils.convertBigDecimalUSDC(listItems.get(j).getTotalAmount()));
                    payoutsList.addView(xmlView);
                    releasedItems++;
                } else {
                    Log.d(TAG, "reserver release");

                }
                j++;
            }
        } else {
            Log.d(TAG, "No reserve for this user");
        }
    }
}
