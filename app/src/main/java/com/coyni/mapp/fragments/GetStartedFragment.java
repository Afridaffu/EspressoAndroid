package com.coyni.mapp.fragments;

import android.content.Intent;
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
import androidx.cardview.widget.CardView;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.view.NotificationsActivity;
import com.coyni.mapp.view.business.BusinessDashboardActivity;

public class GetStartedFragment extends BaseFragment {

    private View mCurrentView;
    private CardView mCvGetStarted;
    private Long mLastClickTimeQA = 0L;
    private MyApplication myApplication;
    private ImageView mIvUserIcon;
    private TextView mTvUserName, mTvUserIconText;
    private ImageView mIvNotifications;
    private RelativeLayout mUserIconRelativeLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_get_started, container, false);
        initFields();
        setData();
        return mCurrentView;
    }

    private void setData() {
//        ((BusinessDashboardActivity) getActivity()).showUserData(mIvUserIcon, mTvUserName, mTvUserIconText);
        ((BusinessDashboardActivity) getActivity()).showUserData();
        mCvGetStarted.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            ((BusinessDashboardActivity) getActivity()).startTracker(myApplication.getDbaOwnerId());
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

    private void initFields() {
        myApplication = (MyApplication) getActivity().getApplicationContext();
        mCvGetStarted = mCurrentView.findViewById(R.id.cv_app_get_started);
        mIvUserIcon = mCurrentView.findViewById(R.id.iv_user_icon);
        mTvUserName = mCurrentView.findViewById(R.id.tv_user_name);
        mTvUserIconText = mCurrentView.findViewById(R.id.tv_user_icon_text);
        mIvNotifications = mCurrentView.findViewById(R.id.iv_notifications);
        mUserIconRelativeLayout = mCurrentView.findViewById(R.id.rl_user_icon_layout);
    }

}
