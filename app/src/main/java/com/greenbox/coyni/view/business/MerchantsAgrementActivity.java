package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class MerchantsAgrementActivity extends AppCompatActivity {
public CheckBox agreemntCB;
public CardView doneCV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchants_agrement);

        agreemntCB = findViewById(R.id.agreementCB);
        doneCV = findViewById(R.id.AgreeDoneCv);

        agreemntCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(agreemntCB.isEnabled()) {
                    doneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                }
                else{
                    doneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            }
        });
        doneCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MerchantsAgrementActivity.this, BusinessRegistrationTrackerActivity.class);
                startActivity(intent);
            }
        });
    }
}