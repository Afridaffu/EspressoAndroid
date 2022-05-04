package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class UnderReviewErrorMsgDialog extends BaseDialog{

    private CardView okCV;
    private Context context;

    public UnderReviewErrorMsgDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public UnderReviewErrorMsgDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.under_review_error_msg_dialog);

        okCV = findViewById(R.id.okCV);

        okCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
