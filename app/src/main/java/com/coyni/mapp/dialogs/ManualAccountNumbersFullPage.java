package com.coyni.mapp.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.coyni.mapp.R;

public class ManualAccountNumbersFullPage extends BaseDialog {

    private ImageView ivBack, cardImageIV;
    private Context context;

    public ManualAccountNumbersFullPage(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manualbank_account_numbers);
        ivBack = findViewById(R.id.ivBack);
        cardImageIV = findViewById(R.id.cardImageIV);


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
