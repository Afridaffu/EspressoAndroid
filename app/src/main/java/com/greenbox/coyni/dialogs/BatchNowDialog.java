package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;

import com.greenbox.coyni.R;

public class BatchNowDialog extends BaseDialog {

    public BatchNowDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clear_batch_prompt);

    }
}
