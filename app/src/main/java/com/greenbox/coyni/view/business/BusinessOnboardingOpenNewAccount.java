package com.greenbox.coyni.view.business;

import android.os.Bundle;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BaseActivity;

public class BusinessOnboardingOpenNewAccount extends BaseActivity {
    public CardView cardnextcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_onboarding_open_new_account);
    }
}
