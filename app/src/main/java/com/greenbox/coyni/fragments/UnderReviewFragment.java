package com.greenbox.coyni.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.greenbox.coyni.R;
import com.greenbox.coyni.dialogs.CustomConfirmationDialog;
import com.greenbox.coyni.dialogs.OnDialogClickListener;
import com.greenbox.coyni.model.DialogAttributes;
import com.greenbox.coyni.model.business_id_verification.CancelApplicationResponse;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.NotificationsActivity;
import com.greenbox.coyni.view.business.ApplicationCancelledActivity;
import com.greenbox.coyni.view.business.BusinessCreateAccountsActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;
import com.greenbox.coyni.viewmodel.BusinessDashboardViewModel;

public class UnderReviewFragment extends BaseFragment {

    private View mCurrentView;
    private BusinessDashboardViewModel businessDashboardViewModel;
    private Long mLastClickTimeQA = 0L;
    private TextView mTvIdentityReviewCancelMessage;
    private ImageView mIvUserIcon;
    private TextView mTvUserName, mTvUserIconText;
    private ImageView mIvNotifications;
    private RelativeLayout mUserIconRelativeLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_under_review, container, false);
        initFields();
        showData();
        return mCurrentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        initObservers();
    }

    private void initFields() {
        mIvUserIcon = mCurrentView.findViewById(R.id.iv_user_icon);
        mTvUserName = mCurrentView.findViewById(R.id.tv_user_name);
        mTvUserIconText = mCurrentView.findViewById(R.id.tv_user_icon_text);
        mIvNotifications = mCurrentView.findViewById(R.id.iv_notifications);
        mUserIconRelativeLayout = mCurrentView.findViewById(R.id.rl_user_icon_layout);
        mTvIdentityReviewCancelMessage = mCurrentView.findViewById(R.id.tv_identity_review_cancel_text);
    }

    private void initViewModels() {
        businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
    }

    private void showData() {
        ((BusinessDashboardActivity) getActivity()).showUserData(mIvUserIcon, mTvUserName, mTvUserIconText);

        String message = getString(R.string.identity_review_cancel_message);
        message += " ";
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
        spannableString.setSpan(clickableSpan, message.length() - 11, message.length() - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvIdentityReviewCancelMessage.setText(spannableString);
        mTvIdentityReviewCancelMessage.setMovementMethod(LinkMovementMethod.getInstance());
        mTvIdentityReviewCancelMessage.setHighlightColor(Color.TRANSPARENT);

        mIvNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                    return;
                }
                mLastClickTimeQA = SystemClock.elapsedRealtime();
                startActivity(new Intent(getActivity(), NotificationsActivity.class));
            }
        });

        mUserIconRelativeLayout.setOnClickListener(view -> {
            ((BusinessDashboardActivity) getActivity()).launchSwitchAccountPage();
        });

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

    private void initObservers() {
        businessDashboardViewModel.getCancelApplicationResponseMutableLiveData().observe(getViewLifecycleOwner(), new Observer<CancelApplicationResponse>() {
            @Override
            public void onChanged(CancelApplicationResponse cancelApplicationResponse) {
                ((BusinessDashboardActivity) getActivity()).dismissDialog();
                if (cancelApplicationResponse != null) {
                    if (cancelApplicationResponse.getStatus() != null
                            && cancelApplicationResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
                        ((BusinessDashboardActivity) getActivity()).launchApplicationCancelledScreen();
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
    }
}
