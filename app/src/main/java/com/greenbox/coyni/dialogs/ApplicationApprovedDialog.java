package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.greenbox.coyni.R;
import com.greenbox.coyni.view.business.BusinessApplicationApprovedActivity;

public class ApplicationApprovedDialog extends BaseDialog{
    private Context context;
    private ImageView closeIV;

    public ApplicationApprovedDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_learn_more);
        closeIV = findViewById(R.id.closeIv);

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
