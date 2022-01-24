package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class AddBenifitialOwnersActivity extends AppCompatActivity {

ImageView BackIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_benifitial_owners);


        BackIV = findViewById(R.id.backIV);

        BackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}