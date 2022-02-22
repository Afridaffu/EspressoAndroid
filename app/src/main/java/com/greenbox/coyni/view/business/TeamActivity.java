package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;

public class TeamActivity extends AppCompatActivity {

    private LinearLayout bpbackBtn, addTeamMemberL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_team);
        bpbackBtn = findViewById(R.id.bpbackBtn);
        addTeamMemberL = findViewById(R.id.addTeamMemberL);

        bpbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addTeamMemberL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TeamActivity.this, AddNewTeamMemberActivity.class));
            }
        });
    }
}