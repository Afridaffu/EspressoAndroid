package com.greenbox.coyni.view.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class AccountHasCreatedSucessful extends AppCompatActivity {
public CardView cardnextcv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_has_created_sucessful);

        cardnextcv = findViewById(R.id.Cardnextcv);
        cardnextcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountHasCreatedSucessful.this,StartBusinessAccountActivity.class);
                startActivity(intent);
            }
        });
    }
}