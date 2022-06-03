package com.greenbox.coyni.fragments;

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

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.NotificationsActivity;
import com.greenbox.coyni.view.business.BusinessCreateAccountsActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;

public class VerificationFailedFragment extends BaseFragment {

    private View mCurrentView;
    private TextView mIdVeriStatus, mTvContactUs;
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
        mIvNotifications = mCurrentView.findViewById(R.id.iv_notifications);
        mUserIconRelativeLayout = mCurrentView.findViewById(R.id.rl_user_icon_layout);
    }

    private void setData() {
        ((BusinessDashboardActivity) getActivity()).showUserData(mIvUserIcon, mTvUserName, mTvUserIconText);
        Profile profile = myApplication.getMyProfile();
        String accountStatus = profile.getData().getAccountStatus();
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
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            startActivity(new Intent(getActivity(), BusinessCreateAccountsActivity.class));
        });
    }
}
