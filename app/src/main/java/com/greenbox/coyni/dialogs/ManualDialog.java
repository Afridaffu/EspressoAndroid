package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.greenbox.coyni.R;

public class ManualDialog extends BaseDialog{

    public ManualDialog(@NonNull Context context) {
        super(context);
    }

    public ManualDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reserve_manual);

    }
}
