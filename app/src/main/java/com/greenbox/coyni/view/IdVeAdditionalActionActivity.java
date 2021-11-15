package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.greenbox.coyni.R;
import com.santalu.maskara.widget.MaskEditText;

public class IdVeAdditionalActionActivity extends AppCompatActivity {
    MaskEditText ssnET;
    CardView idveriDone;
    boolean isssn=false,isSubmitEnabled=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_ve_additional_action);
        ssnET=findViewById(R.id.ssnET);
        idveriDone=findViewById(R.id.idveridoneBtn);


        ssnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() ==11) {
                    isssn = true;
                    isSubmitEnabled = true;
                } else {
                    isssn = false;
                    isSubmitEnabled = false;
                }
                enableORdiableNext();

            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        idveriDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isSubmitEnabled){
                    finish();
                }
            }
        });

    }
    private void enableORdiableNext() {
        if (isssn){
            idveriDone.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
        }else{
            idveriDone.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }
}