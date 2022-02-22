package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class CompanyInfoDetails extends AppCompatActivity {
    private LinearLayout bpCloseLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info_details);

        bpCloseLL = findViewById(R.id.bpCloseLL);

        bpCloseLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}