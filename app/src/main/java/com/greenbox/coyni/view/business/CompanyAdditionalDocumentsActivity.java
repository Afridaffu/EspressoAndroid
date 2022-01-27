package com.greenbox.coyni.view.business;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.view.BaseActivity;

public class CompanyAdditionalDocumentsActivity extends BaseActivity {
    ImageView back2address2IV;
    TextView bottomTV;
    public CardView DoneCV;
    Dialog chooseFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_additional_documents);

        back2address2IV = findViewById(R.id.back2address2IV);
        DoneCV = findViewById(R.id.DoneCV);


        back2address2IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyAdditionalDocumentsActivity.this, CompanyAddressActivity.class);
                startActivity(intent);
            }
        });

        bottomTV = findViewById(R.id.bottomTV);
        bottomTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFilePopup(CompanyAdditionalDocumentsActivity.this);

            }
        });

        DoneCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyAdditionalDocumentsActivity.this, BusinessRegistrationTrackerActivity.class);
                startActivity(intent);
            }
        });
    }

    private void chooseFilePopup(final Context context) {
        try {
            chooseFile = new Dialog(context);
            chooseFile.requestWindowFeature(Window.FEATURE_NO_TITLE);
            chooseFile.setContentView(R.layout.activity_choose_file_botm_sheet);
            chooseFile.setCancelable(true);
            Window window = chooseFile.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            chooseFile.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            chooseFile.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            chooseFile.show();
            DoneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}