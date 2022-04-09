package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;

import com.greenbox.coyni.R;

public class RefunInsufficeintTokenDialog extends BaseDialog{
    public RefunInsufficeintTokenDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insufficient_funds_token_account_dialog);

    }
}
