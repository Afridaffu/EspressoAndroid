package com.greenbox.coyni.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.login.LoginData;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.utils.Utils;

public class VerificationFailedDialog extends Dialog {

    private Context context;
    private TextView mTvName, mTvDbaName, mTvLegalName;
    private CardView mCvDone;
    private LoginResponse loginResponse;
    private OnDialogClickListener listener;

    public VerificationFailedDialog(@NonNull Context context, LoginResponse response) {
        super(context, R.style.FullScreenDialog);
        this.context = context;
        this.loginResponse = response;
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_failed);

        initFields();

        if (loginResponse != null && loginResponse.getStatus() != null
                && loginResponse.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
            LoginData data = loginResponse.getData();
            if (data != null) {
                if (data.getCompanyName() != null && !data.getCompanyName().equals("")) {
                    mTvName.setText(context.getString(R.string.dear_name, data.getCompanyName()));
                    mTvLegalName.setText(context.getString(R.string.legal_name_name, data.getCompanyName()));
                }

                if (data.getDbaName() != null && !data.getDbaName().equals("")) {
                    mTvDbaName.setText(context.getString(R.string.dba_dba, data.getDbaName()));
                }
            }
        }

        mCvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.onDialogClicked("Done", null);
                    dismiss();
                }
            }
        });
    }

    private void initFields() {
        mTvName = findViewById(R.id.tv_name);
        mTvDbaName = findViewById(R.id.tv_dba_name);
        mTvLegalName = findViewById(R.id.tv_legal_name);
        mCvDone = findViewById(R.id.cv_done);


    }

}