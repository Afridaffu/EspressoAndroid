package com.greenbox.coyni.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.outline_et.OutLineBoxPhoneNumberEditText;

public class RetrieveEmailActivity extends AppCompatActivity {
    OutLineBoxPhoneNumberEditText phoneNumberET;
    MaterialCardView nextBtn;
    TextInputEditText firstName,lastName;
    TextInputLayout firstTIL,lastTIL;
    private boolean isPnNoEnable,isFirstNameEnable,isLastNameEnable,isNextBtnEnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_email);
        phoneNumberET=findViewById(R.id.rePhoneNumber);
        nextBtn=findViewById(R.id.reCardViewNextBtn);
        firstName=findViewById(R.id.reFirstNameET);
        lastName=findViewById(R.id.reLastNameET);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber, first, last,next;
                phoneNumber = phoneNumberET.getText().toString();
                first = firstName.getText().toString();
                last = lastName.getText().toString();
                firstTIL=findViewById(R.id.reFirstNameTIL);
                lastTIL=findViewById(R.id.reLastNameTIL);
                 if (phoneNumber.length() < 14) {
                        phoneNumberET.setErrorOutlineBox();
                        isPnNoEnable = false;

                    } else {
                        isPnNoEnable = true;
                    }

                    if(first.isEmpty()){
                        isFirstNameEnable=false;
                    }
                    else
                    {
                        isFirstNameEnable=true;

                    }

                    if(last.isEmpty()){
                        isLastNameEnable=false;
                   }
                    else
                    {
                        isLastNameEnable=true;
                    }


                    if(isPnNoEnable&&isFirstNameEnable&&isLastNameEnable){
                        isNextBtnEnable=true;
                        nextBtn.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                    }
                    else {
                        isNextBtnEnable=false;
                    }

                    if(isNextBtnEnable){
                        final Dialog dialog = new Dialog(RetrieveEmailActivity.this);
                        dialog.setContentView(R.layout.retrieve_email_processing_layout);
                        dialog.getWindow().setBackgroundDrawableResource(R.color.white);
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
                        dialog.show();
                        startTimer(dialog);
                    }

                }
        });
}
    public void startTimer(Dialog dialog) {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(9000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        });

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}