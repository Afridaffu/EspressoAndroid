package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

public class BenificialOwnerDetails extends AppCompatActivity {
    private ProgressBar mProgress;
    TextView mPercentage,mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benificial_owner_details);
        final double percentages = 0;
        //percentages= (double) Math.round(getPercentages() * 100) / 100;
        mProgress.setProgress((int)percentages);
        mPercentage.setText((String.valueOf(percentages).replace(".0", "")) + "%");
    }
}