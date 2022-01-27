package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class GetstartedSuccessAcivity extends AppCompatActivity {
    CardView getstartcv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_getstarted_success);
            getstartcv =findViewById(R.id.GetCV);

            getstartcv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GetstartedSuccessAcivity.this, BusinessRegistrationTrackerActivity.class);
                    startActivity(intent);
                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}