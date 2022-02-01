package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class DBAInfoDetails extends AppCompatActivity {
    LinearLayout closeLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbainfo_details);

        closeLL = findViewById(R.id.closeLL);
        closeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){try {
                Intent intent = new Intent(DBAInfoDetails.this, BusinessProfileActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        });
    }
}