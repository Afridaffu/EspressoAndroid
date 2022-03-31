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
import com.greenbox.coyni.adapters.OnItemClickListener;
import com.greenbox.coyni.dialogs.BatchNowDialog;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.dialogs.ProcessingVolumeDialog;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListItems;
import com.greenbox.coyni.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.business_id_verification.CancelApplicationResponse;
import com.greenbox.coyni.model.preferences.Preferences;
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
            mLlBuyTokensFirstTimeView, mLlProcessingVolume, mLlGetStartedView,payoutsXmlLL,payoutsLayoutLL;
    private TextView mTvIdentityReviewCancelMessage, mTvProcessingVolume, mTvContactUs;
    private CardView mCvAdditionalDataContinue;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private BusinessDashboardFragment businessDashboardFragment;
    private RelativeLayout mUserIconRelativeLayout, notificationsRL;
    private TextView mTvOfficiallyVerified, mTvMerchantTransactions,batchPayoutDateTV,payoutManualTV,payoutAmountTV,cynTV;
    private TextView dasboardPayoutDate;
    private CardView mCvBatchNow, mCvGetStarted;
    private Long mLastClickTimeQA = 0L;
    private DashboardViewModel mDashboardViewModel;
    private BatchPayoutListAdapter batchPayoutListAdapter;
    RecyclerView recyclerViewPayouts;
    List<BatchPayoutListItems> listItems;
    DashboardViewModel dashboardViewModel;

    private int dbaID = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_business_dashboard, container, false);
        initFields();
        initObservers();
        hideAllStatusViews();
        return mCurrentView;
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
        businessDashboardViewModel = new ViewModelProvider(getActivity()).get(BusinessDashboardViewModel.class);
        mDashboardViewModel = new ViewModelProvider(getActivity()).get(DashboardViewModel.class);

        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
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
        dasboardPayoutDate = mCurrentView.findViewById(R.id.dasboardPayoutDate);



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
        businessDashboardViewModel.getCancelApplicationResponseMutableLiveData().observe(getActivity(), new Observer<CancelApplicationResponse>() {
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

        mDashboardViewModel.getProfileMutableLiveData().observe(getActivity(), new Observer<Profile>() {
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
                if (getViewLifecycleOwner().getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    if (batchPayoutListResponse != null) {
                        if (batchPayoutListResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
//                        BatchPayoutListAdapter payoutListAdapter = new BatchPayoutListAdapter(getActivity(), payoutList);
                            if (batchPayoutListResponse.getData() != null && batchPayoutListResponse.getData().getItems() != null) {
                                showBatchPayouts(batchPayoutListResponse.getData().getItems());
                            } else {
                                Log.d(TAG, "No items found");
                            }
                        }
                        Log.d(TAG, "Error");
                    }
                }
            }
        });
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
            } else if (accountStatus.equalsIgnoreCase(Utils.BUSINESS_ACCOUNT_STATUS.ACTION_REQUIRED.getStatus())) {
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
        businessDashboardViewModel.getPayoutListData();
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

    private void showBatchPayouts(List<BatchPayoutListItems> listItems) {
        if(listItems!=null) {
            Collections.sort(listItems, Collections.reverseOrder());
            if (listItems.get(0).getStatus().equalsIgnoreCase("open")) {
                String amount = listItems.get(0).getTotalAmount();
                lastPayoutAmountTV.setText(Utils.convertBigDecimalUSDC((amount)));

                String date = listItems.get(0).getCreatedAt();
                dasboardPayoutDate.setText(myApplication.convertZoneDateTime(date, "yyyy-MM-dd HH:mm:ss.S", "MM/dd/yyyy @ hh:mma"));

            }
            String date = listItems.get(0).getCreatedAt();
            nxtPayoutDatenTimeTV.setText(Utils.addNxtDay((date)));

            LinearLayout payoutsList = mCurrentView.findViewById(R.id.payoutsLayoutLL);
            payoutsList.removeAllViews();

            for (int i = 0; i < 5; i++) {
                View xmlView = getLayoutInflater().inflate(R.layout.batch_payouts_dashboard, null);

                TextView payoutDate = xmlView.findViewById(R.id.batchPayoutDateTV);
                String listDate = listItems.get(i).getCreatedAt();
                payoutDate.setText(myApplication.convertZoneDateTime(listDate, "yyyy-MM-dd HH:mm:ss.S", "MM/dd/yyyy @ hh:mma"));

                TextView totalAmount = xmlView.findViewById(R.id.payoutAmountTV);
                totalAmount.setText(Utils.convertBigDecimalUSDC(listItems.get(i).getTotalAmount()));

                payoutsList.addView(xmlView);

            }
        }
        else {
            Log.d(TAG,"No Batch Payouts for this user");
        }
    }
}
