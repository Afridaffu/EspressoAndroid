package com.greenbox.coyni.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class QuickAction extends AppCompatActivity {

    TextView clickTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_action);

        clickTV = findViewById(R.id.clickTV);
        clickTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}