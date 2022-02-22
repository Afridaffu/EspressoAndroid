package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.greenbox.coyni.R;

public class AddNewTeamMemberActivity extends AppCompatActivity {

    private LinearLayout backBtnLL;
    LinearLayout administatorLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_team_member);

        backBtnLL = findViewById(R.id.backBtnLL);
        administatorLL = findViewById(R.id.administatorLL);
        backBtnLL.setOnClickListener(v -> onBackPressed());
    }
}