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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.greenbox.coyni.R;

public class AdditionDocInfoActivity extends AppCompatActivity {
ImageView back2address2IV;
TextView bottomTV;
public CardView DoneCV;
Dialog choosefile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtion_doc_info);

        back2address2IV = findViewById(R.id.back2address2IV);
        DoneCV = findViewById(R.id.DoneCV);


        back2address2IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdditionDocInfoActivity.this, CompanyAddress2Activity.class);
                startActivity(intent);
            }
        });

        bottomTV = findViewById(R.id.bottomTV);
        bottomTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosefilePopup(AdditionDocInfoActivity.this);

            }
        });

        DoneCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdditionDocInfoActivity.this, StartBusinessAccountActivity.class);
                startActivity(intent);
            }
        });
    }
    private void choosefilePopup(final Context context) {
        try {
            choosefile = new Dialog(context);
            choosefile.requestWindowFeature(Window.FEATURE_NO_TITLE);
            choosefile.setContentView(R.layout.activity_choose_file_botm_sheet);
            choosefile.setCancelable(true);
            Window window = choosefile.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            choosefile.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            choosefile.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            choosefile.show();
            DoneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}