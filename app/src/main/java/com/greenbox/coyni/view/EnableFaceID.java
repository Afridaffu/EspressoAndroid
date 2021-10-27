package com.greenbox.coyni.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.greenbox.coyni.R;

public class EnableFaceID extends AppCompatActivity {

    MaterialCardView enableFaceCV;
    TextView faceIDNotNowTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_enable_face_id);

            enableFaceCV = findViewById(R.id.enableFaceCV);
            faceIDNotNowTV = findViewById(R.id.faceIDNotNowTV);

            enableFaceCV.setOnClickListener(view -> {
                startActivity(new Intent(this, SignupSuccess.class));
            });
            faceIDNotNowTV.setOnClickListener(view -> {

            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}