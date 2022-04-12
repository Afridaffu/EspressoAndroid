package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class RefundInsufficientMerchnatDialog extends BaseDialog {

private  CardView buytokenDone;
    private static final  String ACTIONN = "insuffintmerchantbalancedialog ";
    private Context context;

    public RefundInsufficientMerchnatDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insufficient_funds_dialog);

        buytokenDone = findViewById(R.id.BuytokenDone);


        buytokenDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnDialogClickListener().onDialogClicked(ACTIONN, null);
                dismiss();
            }
        });

    }
}
