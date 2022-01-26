package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class ReviewApplicationActivity extends AppCompatActivity {
    TextView edit1, edit2, edit3;
    CheckBox agreeCB;
    public boolean isNextEnabled = false, isagreed = false;
    public CardView submitcv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_application);

        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2TV);
        edit3 = findViewById(R.id.edit3TV);
        agreeCB = findViewById(R.id.agreeCB);
        submitcv = findViewById(R.id.submitCV);

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, CompanyInformationActivity.class);
                startActivity(intent);
            }
        });
        edit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, DBAbasicInformationAcivity.class);
                startActivity(intent);
            }
        });
        edit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, ReviewApplicationActivity.class);
                startActivity(intent);
            }
        });
        agreeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((agreeCB.isEnabled())) {
                    submitcv.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                }
                else
                {
                    submitcv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            }
        });
        submitcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewApplicationActivity.this, ReviewApplicationActivity.class);
                startActivity(intent);
            }
        });
    }
}
