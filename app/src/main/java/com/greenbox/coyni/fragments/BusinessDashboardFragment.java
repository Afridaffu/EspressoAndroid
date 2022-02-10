package com.greenbox.coyni.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;

public class BusinessDashboardFragment extends BaseFragment {

    private View mCurrentView;
    private MyApplication myApplication;
    private ImageView mIvUserIcon;
    private TextView mTvUserName, mTvUserIconText;
    RelativeLayout mRlBaseLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCurrentView = inflater.inflate(R.layout.fragment_business_dashboard, container, false);
        initFields();
        showUserData();
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
    }

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
}
