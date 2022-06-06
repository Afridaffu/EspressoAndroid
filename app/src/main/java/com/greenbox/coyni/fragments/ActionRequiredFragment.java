package com.greenbox.coyni.fragments;

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

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.NotificationsActivity;
import com.greenbox.coyni.view.business.BusinessAdditionalActionRequiredActivity;
import com.greenbox.coyni.view.business.BusinessCreateAccountsActivity;
import com.greenbox.coyni.view.business.BusinessDashboardActivity;

public class ActionRequiredFragment extends BaseFragment {

    private View mCurrentView;
    private CardView mCvAdditionalDataContinue;
    private Long mLastClickTimeQA = 0L;
    private ImageView mIvUserIcon;
    private TextView mTvUserName, mTvUserIconText;
    private ImageView mIvNotifications;
    private RelativeLayout mUserIconRelativeLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_action_required, container, false);
        initFields();
        return mCurrentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        initObservers();
        setData();
    }

    private void setData() {
        ((BusinessDashboardActivity) getActivity()).showUserData(mIvUserIcon, mTvUserName, mTvUserIconText);
    }

    private void initFields() {
        mCvAdditionalDataContinue = mCurrentView.findViewById(R.id.cv_additional_data_continue);
        mIvUserIcon = mCurrentView.findViewById(R.id.iv_user_icon);
        mTvUserName = mCurrentView.findViewById(R.id.tv_user_name);
        mTvUserIconText = mCurrentView.findViewById(R.id.tv_user_icon_text);
        mIvNotifications = mCurrentView.findViewById(R.id.iv_notifications);
        mUserIconRelativeLayout = mCurrentView.findViewById(R.id.rl_user_icon_layout);

        mCvAdditionalDataContinue.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeQA < 1000) {
                return;
            }
            mLastClickTimeQA = SystemClock.elapsedRealtime();
            ((BusinessDashboardActivity) getActivity()).launchAdditionalActionPage();

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

    private void initViewModels() {
        //businessDashboardViewModel = new ViewModelProvider(this).get(BusinessDashboardViewModel.class);
    }

    private void initObservers() {

    }
}
