package com.greenbox.coyni.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.business_id_verification.CancelApplicationResponse;
import com.greenbox.coyni.utils.CustomConfirmationDialog;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.OnDialogButtonClickListener;
import com.greenbox.coyni.view.business.AdditionalInformationRequiredActivity;
import com.greenbox.coyni.view.business.ApplicationCancelledActivity;
import com.greenbox.coyni.view.business.BusinessCreateAccountsActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;


public class BusinessDashboardFragment extends BaseFragment {

    private View mCurrentView;
    private MyApplication myApplication;
    private ImageView mIvUserIcon;
    private CardView mIvUserIconCV;
    private TextView mTvUserName, mTvUserIconText;
    private LinearLayout mLlIdentityVerificationReview, mLlBusinessDashboardView,
            mLlIdentityAdditionDataRequired, mLlIdentityVerificationFailedView;
    private TextView mTvIdentityReviewCancelMessage;
    private CardView mCvAdditionalDataContinue;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private RelativeLayout mUserIconRelativeLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_business_dashboard, container, false);
        initFields();
        initObservers();
        showUserData();
        showIdentityVerificationReview();
        //showAdditionalActionView();

        mUserIconRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BusinessCreateAccountsActivity.class));

            }
        });

        mCvAdditionalDataContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AdditionalInformationRequiredActivity.class));

            }
        });

        showUserData();
        return mCurrentView;
    }

    @Override
    public void updateData() {
        showUserData();
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
        mTvIdentityReviewCancelMessage = mCurrentView.findViewById(R.id.tv_identity_review_cancel_text);
        businessDashboardViewModel = new ViewModelProvider(getActivity()).get(BusinessDashboardViewModel.class);
    }

    private void initObservers() {
        businessDashboardViewModel.getCancelApplicationResponseMutableLiveData().observe(getActivity(), new Observer<CancelApplicationResponse>() {
            @Override
            public void onChanged(CancelApplicationResponse cancelApplicationResponse) {
                if (cancelApplicationResponse != null) {
                    launchApplicationCancelledScreen();
                }
            }
        });
    }

    private void launchApplicationCancelledScreen() {
        Intent inCancelledApplication = new Intent(getActivity(), ApplicationCancelledActivity.class);
        activityResultLauncher.launch(inCancelledApplication);
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    //Display Identity Verification Failed Error
                    showIdentityVerificationFailed();
                }
            });

    private void showUserData() {
        ((BusinessDashboardActivity) getActivity()).showUserData(mIvUserIcon, mTvUserName, mTvUserIconText);
    }

    private void showIdentityVerificationFailed() {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.VISIBLE);
    }

    private void showAdditionalActionView() {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.VISIBLE);
        mLlIdentityVerificationFailedView.setVisibility(View.GONE);
    }

    private void showIdentityVerificationReview() {
        mLlIdentityVerificationReview.setVisibility(View.VISIBLE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.GONE);
        String message = getString(R.string.identity_review_cancel_message);
        SpannableString spannableString = new SpannableString(message);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
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

    private void showCancelApplicationDialog() {
        DialogAttributes dialogAttributes = new DialogAttributes(getString(R.string.cancel_application),
                getString(R.string.cancel_application_message), getString(R.string.yes),
                getString(R.string.no));
        CustomConfirmationDialog customConfirmationDialog = new CustomConfirmationDialog
                (getActivity(), dialogAttributes);
        customConfirmationDialog.setOnButtonClickLister(new OnDialogButtonClickListener() {
            @Override
            public void onPositiveButtonClicked() {
                //businessDashboardViewModel.cancelBusinessApplication();
                launchApplicationCancelledScreen();
            }

            @Override
            public void onNegativeButtonClicked() {

            }
        });
        customConfirmationDialog.show();
    }
}
