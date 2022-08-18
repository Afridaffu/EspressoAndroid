package com.coyni.mapp.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.coyni.mapp.R;
import com.coyni.mapp.view.BaseActivity;

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
                setResult(RESULT_OK);
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

    @Override
    public void onBackPressed() {
        return;
    }

    private void initFields() {
        backLL = findViewById(R.id.backLL);
        cvDone = findViewById(R.id.cvDone);
    }
}