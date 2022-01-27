package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.greenbox.coyni.R;

public class DBAinfoBtmSheetActivity extends AppCompatActivity {
   LinearLayout samebtn;
   TextView sametv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbainfo_btm_sheet);

        samebtn = findViewById(R.id.sameBtnLL);
        sametv = findViewById(R.id.sameTV);

        samebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DBAinfoBtmSheetActivity.this, DBAbasicInformationAcivity.class);
                startActivity(intent);
            }
        });
    }
}