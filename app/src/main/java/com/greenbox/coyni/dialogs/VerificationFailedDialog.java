package com.greenbox.coyni.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class VerificationFailedDialog extends Dialog {

    private TextView mTvName, mTvDbaName, mTvLegalName;
    private CardView mCvDone;

    public VerificationFailedDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_failed);

        initFields();
    }

    private void initFields() {
        mTvName = findViewById(R.id.tv_name);
        mTvDbaName = findViewById(R.id.tv_dba_name);
        mTvLegalName = findViewById(R.id.tv_legal_name);
        mCvDone = findViewById(R.id.cv_done);
    }

}