package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BaseActivity;

public class ApplicationCancelledActivity extends BaseActivity {

    private LinearLayout backLL;
    private CardView cvDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_canceled);

        initFields();
        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void initFields() {
        backLL = findViewById(R.id.backLL);
        cvDone = findViewById(R.id.cvDone);
    }
}