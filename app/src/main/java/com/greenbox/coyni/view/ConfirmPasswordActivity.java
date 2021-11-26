package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.regex.Pattern;

public class ConfirmPasswordActivity extends AppCompatActivity {
    TextInputEditText currentPassET;
    TextInputLayout currentTIL;
    CardView saveBtn;
    boolean btnEnabled=false;
    ImageView backBtn;
    String oldPassword;
    private Pattern strong;
    private static final String STRONG_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,})";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        try {
            currentPassET = findViewById(R.id.currentPassET);
            currentTIL = findViewById(R.id.currentPassTIL);
            saveBtn = findViewById(R.id.saveBtnCV);
            backBtn=findViewById(R.id.cpConfirmBackIV);
            strong = Pattern.compile(STRONG_PATTERN);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            currentPassET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() >= 8&&strong.matcher(currentPassET.getText().toString().trim()).matches()) {
                        btnEnabled = true;
                        oldPassword = currentPassET.getText().toString().trim();
                        saveBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                    } else{
                        btnEnabled = false;
                        saveBtn.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btnEnabled){
                        startActivity(new Intent(ConfirmPasswordActivity.this, CreatePasswordActivity.class)
                                .putExtra("screen", "ConfirmPassword")
                                .putExtra("oldpassword", oldPassword)
                        );

                        Log.e("oldPass", "" + oldPassword);
                    }

                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            clearField();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void clearField() {
        currentPassET.setText("");
    }
}