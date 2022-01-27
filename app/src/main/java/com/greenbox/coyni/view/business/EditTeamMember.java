package com.greenbox.coyni.view.business;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class EditTeamMember extends AppCompatActivity {
    TextInputLayout editfnametil,editlnametil,editemailtil;
    TextInputEditText editfnameET,editlnameET,editemail;
    LinearLayout fnameLL,lnameLL,dobLL,ssnLL,ownershipLL;
    TextView fnameTV,lnameTV,dobTV,ssnTV,ownershipTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_team_member);

        initfields();
        focusWatchers();

    }

    private void initfields() {
        editfnametil = findViewById(R.id.editfnametil);
        editlnametil = findViewById(R.id.editlnametil);
        editemailtil = findViewById(R.id.editemailtil);

        editfnameET = findViewById(R.id.editfnameET);
        editlnameET = findViewById(R.id.editlnameET);
        editemail = findViewById(R.id.editemailET);

    }
    private void focusWatchers() {

        editfnameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    editfnameET.setHint("");
                    if (editfnameET.getText().toString().trim().length() > 0) {
                        editfnametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(editfnametil, getColor(R.color.primary_black));
                    }
                    else if (editfnameET.getText().toString().trim().length() == 1) {
                        editfnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(editfnametil, getColor(R.color.error_red));
                        fnameTV.setText("Field Required Mininmum 2 characters");
                    }
                    else {
                        editfnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(editfnametil, getColor(R.color.light_gray));
                    }
                } else {
                        editfnameET.setHint("First Name");
                    editfnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(editfnametil, getColor(R.color.primary_green));
                }
            }
        });
        editlnameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    editlnameET.setHint("");
                    if (editlnameET.getText().toString().trim().length() > 0) {
                        editlnametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(editlnametil, getColor(R.color.primary_black));
                    }
                    else if(editlnameET.getText().toString().trim().length() == 1) {
                        editlnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(editlnametil, getColor(R.color.light_gray));
                        lnameTV.setText("Field Required Mininmum 2 characters");
                    }
                    else {
                        editlnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(editlnametil, getColor(R.color.light_gray));
                    }
                } else {
                    editlnameET.setHint("Last Name");
                    editlnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(editlnametil, getColor(R.color.primary_green));
                }
            }
        });
        editemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    editemail.setHint("");
                    if (editemail.getText().toString().trim().length() > 0) {
                        editemailtil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(editemailtil, getColor(R.color.primary_black));
                    }
                    else if(editemail.getText().toString().trim().length() == 1) {
                        editemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(editemailtil, getColor(R.color.light_gray));
                    }
                    else {
                        editemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(editemailtil, getColor(R.color.light_gray));
                  }
                } else {
                    editemail.setHint("Email");
                    editemailtil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(editemailtil, getColor(R.color.primary_green));
                }
            }
        });

    }

}