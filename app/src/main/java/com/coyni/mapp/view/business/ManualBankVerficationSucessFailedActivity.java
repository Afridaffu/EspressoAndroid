package com.coyni.mapp.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.coyni.mapp.R;

public class ManualBankVerficationSucessFailedActivity extends AppCompatActivity {

    private TextView headerTV,errorDescriptnTV,statusTV,nameOnBankTV,bankNameTV,routingNumTV,accNumTV,doneTextTV;
    private ImageView imageIV;
    private CardView doneCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_bank_verfication_sucess_failed);

        initfields();
    }

    private void initfields() {

        headerTV = findViewById(R.id.headerTV);
        imageIV = findViewById(R.id.imageIV);
        errorDescriptnTV = findViewById(R.id.errorDescriptnTV);
        statusTV = findViewById(R.id.statusTV);
        nameOnBankTV = findViewById(R.id.nameOnBankTV);
        bankNameTV = findViewById(R.id.bankNameTV);
        routingNumTV = findViewById(R.id.routingNumTV);
        accNumTV = findViewById(R.id.accNumTV);
        doneTextTV = findViewById(R.id.doneTextTV);
        doneCV = findViewById(R.id.validateCV);

    }
}