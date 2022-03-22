package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;

import com.greenbox.coyni.R;

public class CalenderRangeDialog extends BaseDialog {

    public CalenderRangeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_dialog);

    }
}
