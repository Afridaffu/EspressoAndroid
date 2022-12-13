package com.coyni.mapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.coyni.mapp.R;
import com.coyni.mapp.model.profile.Profile;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.view.CustomerProfileActivity;
import com.coyni.mapp.view.GetHelpActivity;
import com.coyni.mapp.view.NotificationsActivity;
import com.coyni.mapp.view.business.BusinessDashboardActivity;

public class VerificationFailedFragment extends BaseFragment {

    private View mCurrentView;
    private TextView mIdVeriStatus, mTvContactUs, failedcontent;
    private Long mLastClickTimeQA = 0L;
    private MyApplication myApplication;
    private ImageView mIvUserIcon;
    private TextView mTvUserName, mTvUserIconText;
    private ImageView mIvNotifications;
    private RelativeLayout mUserIconRelativeLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_verification_failed, container, false);
        initFields();
        setData();
        return mCurrentView;
    }

    private void initFields() {
        myApplication = (MyApplication) getActivity().getApplicationContext();
        mIvUserIcon = mCurrentView.findViewById(R.id.iv_user_icon);
        mTvUserName = mCurrentView.findViewById(R.id.tv_user_name);
        mTvUserIconText = mCurrentView.findViewById(R.id.tv_user_icon_text);
        mIdVeriStatus = mCurrentView.findViewById(R.id.idVeriStatus);
        mTvContactUs = mCurrentView.findViewById(R.id.contactUSTV);
        failedcontent = mCurrentView.findViewById(R.id.failedcontent);
        mIvNotifications = mCurrentView.findViewById(R.id.iv_notifications);
        mUserIconRelativeLayout = mCurrentView.findViewById(R.id.rl_user_icon_layout);
    }

    private void setData() {
//        ((BusinessDashboardActivity) getActivity()).showUserData(mIvUserIcon, mTvUserName, mTvUserIconText);
        ((BusinessDashboardActivity) getActivity()).showUserData();
        Profile profile = myApplication.getMyProfile();
        String accountStatus = profile.getData().getAccountStatus();
        if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.DECLINED.getStatus())) {
            mIdVeriStatus.setText(R.string.declined_text);
            failedcontent.setText(R.string.iv_verification_failed_content);
        } else if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.REGISTRATION_CANCELED.getStatus())) {
            mIdVeriStatus.setText(R.string.canceled_text);
            failedcontent.setText(R.string.iv_verification_failed_content);
        } else if (accountStatus.equals(Utils.BUSINESS_ACCOUNT_STATUS.TERMINATED.getStatus())) {
            mIdVeriStatus.setText(R.string.terminated_text);
            failedcontent.setText(R.string.acc_terminated_content);
        }

        mTvContactUs.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(Utils.mondayURL));
//            startActivity(i);
            startActivity(new Intent(getActivity(), GetHelpActivity.class));
        });

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
}
