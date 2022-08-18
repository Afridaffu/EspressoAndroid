package com.coyni.mapp.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.utils.Utils;

public class RefundInsufficeintTokenDialog extends BaseDialog {

    private LinearLayout toKenaccountLL;
    private TextView accountbalanceTV;
    private Double businessBalance;
    private static final String ACTION = "RefundPreviewDialog";
    private Context context;

    public RefundInsufficeintTokenDialog(Context context, Double businessBalance) {
        super(context);
        this.context = context;
        this.businessBalance = businessBalance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insufficient_funds_token_account_dialog);

        toKenaccountLL = findViewById(R.id.TOKenaccountLL);
        accountbalanceTV = findViewById(R.id.AccountbalanceTV);
        if (businessBalance != null) {
            accountbalanceTV.setText(Utils.USNumberFormat(Utils.doubleParsing(String.valueOf(businessBalance))));
        }


        toKenaccountLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getOnDialogClickListener().onDialogClicked(ACTION, null);
                dismiss();
            }
        });

    }
}
