package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BaseActivity;

public class VerificationFailedActivity extends BaseActivity {

    private TextView mTvName, mTvDbaName, mTvLegalName;
    private CardView mCvDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_failed);

        initFields();
        initObservers();
        showData();

        mCvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initFields(){
        mTvName = findViewById(R.id.tv_name);
        mTvDbaName = findViewById(R.id.tv_dba_name);
        mTvLegalName = findViewById(R.id.tv_legal_name);
        mCvDone = findViewById(R.id.cv_done);
    }

    private void initObservers() {

    }

    private void showData() {

    }
}