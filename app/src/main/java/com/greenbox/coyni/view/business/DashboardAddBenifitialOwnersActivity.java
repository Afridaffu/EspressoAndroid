package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class DashboardAddBenifitialOwnersActivity extends AppCompatActivity {

    ImageView crossIV;
    TextView startTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_add_benifitial_owners);


        startTV = findViewById(R.id.StartTV);

        startTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardAddBenifitialOwnersActivity.this,AddBenifitialOwnerActivity1.class);
           startActivity(i);
            }
        });

        crossIV = findViewById(R.id.CrossIV);
        crossIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
