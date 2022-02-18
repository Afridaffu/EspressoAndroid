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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.business_id_verification.CancelApplicationResponse;
import com.greenbox.coyni.utils.CustomConfirmationDialog;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.OnDialogButtonClickListener;
import com.greenbox.coyni.view.business.ApplicationCancelledActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

public class BusinessDashboardFragment extends BaseFragment {

    private View mCurrentView;
    private MyApplication myApplication;
    private ImageView mIvUserIcon;
    private TextView mTvUserName, mTvUserIconText;
    private RelativeLayout mRlBaseLayout;
    private LinearLayout mLlIdentityVerificationReview, mLlBusinessDashboardView,
            mLlIdentityAdditionDataRequired, mLlIdentityVerificationFailedView;
    private TextView mTvIdentityReviewCancelMessage;
    private BusinessDashboardViewModel businessDashboardViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_business_dashboard, container, false);
        initFields();
        initObservers();
        showUserData();
        showIdentityVerificationReview();
        return mCurrentView;
    }

    @Override
    public void updateData() {
        showUserData();
    }

    private void initFields() {
        mRlBaseLayout = mCurrentView.findViewById(R.id.rl_user_icon_layout);
        myApplication = (MyApplication) getActivity().getApplicationContext();
        mIvUserIcon = mCurrentView.findViewById(R.id.iv_user_icon);
        mTvUserName = mCurrentView.findViewById(R.id.tv_user_name);
        mTvUserIconText = mCurrentView.findViewById(R.id.tv_user_icon_text);
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
        String iconText = "";
        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                && myApplication.getMyProfile().getData().getFirstName() != null) {
            String firstName = myApplication.getMyProfile().getData().getFirstName();
            iconText = firstName.substring(0, 1).toUpperCase();
            String username = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
            if (myApplication.getMyProfile().getData().getLastName() != null) {
                String lastName = myApplication.getMyProfile().getData().getFirstName();
                iconText = iconText + lastName.substring(0, 1).toUpperCase();
                username = username + " ";
                username = username + lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase();
            }
            mTvUserName.setText(getResources().getString(R.string.dba_name, username));
        }
        if (myApplication.getMyProfile() != null && myApplication.getMyProfile().getData() != null
                && myApplication.getMyProfile().getData().getImage() != null) {
            mTvUserIconText.setVisibility(View.GONE);
            mIvUserIcon.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(myApplication.getMyProfile().getData().getImage())
                    .placeholder(R.drawable.ic_profile_male_user)
                    .into(mIvUserIcon);
        } else {
            mTvUserIconText.setVisibility(View.VISIBLE);
            mIvUserIcon.setVisibility(View.GONE);
            mTvUserIconText.setText(iconText);
        }
    }

    private void showIdentityVerificationFailed() {
        mLlIdentityVerificationReview.setVisibility(View.GONE);
        mLlBusinessDashboardView.setVisibility(View.GONE);
        mLlIdentityAdditionDataRequired.setVisibility(View.GONE);
        mLlIdentityVerificationFailedView.setVisibility(View.VISIBLE);
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
