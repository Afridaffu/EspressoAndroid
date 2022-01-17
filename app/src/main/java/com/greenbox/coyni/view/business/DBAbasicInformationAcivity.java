package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CompanyOutLineBoxPhoneNumberEditText;

public class DBAbasicInformationAcivity extends AppCompatActivity {
TextInputLayout dbaNameTIL,dbaEmailTIL;
LinearLayout dbanameLL,dbaemailLL;
CompanyOutLineBoxPhoneNumberEditText phoneNumberET;
TextInputEditText dbanameET,dbaemailET;
TextView dbanameTV,dbaemailTV;
public CardView dbaNextCV;
public static int focusedID = 0;
public static DBAbasicInformationAcivity dbAbasicInformationAcivity;



public boolean isdbaName = false, isdbaEmail = false,isPhoneNumber=false,isNextEnabled = false;
boolean isEmailError = false, isPhoneError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbabasic_information);
        initFields();
        focusWatchers();
        textWatchers();
    }

    protected void onResume() {
        super.onResume();
        if (dbanameET.getId() == focusedID) {
            dbanameET.requestFocus();
        } else if (dbaemailET.getId() == focusedID) {
            dbaemailET.requestFocus();
        }else if (phoneNumberET.getETID() == focusedID) {
            phoneNumberET.requestETFocus();
        } else {
            dbanameET.requestFocus();
        }
        Log.e("ID", "" + focusedID);
    }

    private void initFields() {
        dbaNameTIL = findViewById(R.id.DBAnameTIL);
        dbaEmailTIL = findViewById(R.id.DBAemailTIL);

        dbanameLL = findViewById(R.id.DBAnameErrorLL);
        dbaemailLL = findViewById(R.id.DBAemailErrorLL);

        dbanameET = findViewById(R.id.DBAnameET);
        dbaemailET = findViewById(R.id.DBAemailET);

        dbanameTV = findViewById(R.id.DBAnameErrorTV);
        dbaemailTV = findViewById(R.id.DBAemailErrorTV);
        dbaNextCV = findViewById(R.id.dbaNextCV);
        phoneNumberET = findViewById(R.id.phoneNumberET);


    }

    private void focusWatchers() {
        dbanameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    dbanameET.setHint("");
                    if (dbanameET.getText().toString().trim().length() > 1) {
                        dbanameLL.setVisibility(GONE);
                        dbaEmailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        dbanameET.setHintTextColor(getColor(R.color.light_gray));
                        Utils.setUpperHintColor(dbaEmailTIL, getColor(R.color.light_gray));

                    } else if (dbanameET.getText().toString().trim().length() == 1) {
                        dbaEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.error_red));
                        dbanameET.setHintTextColor(getColor(R.color.error_red));
                        dbanameLL.setVisibility(VISIBLE);
                        dbanameTV.setText("Minimum 2 Characters Required");
                    } else {
                        dbaEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        dbanameET.setHintTextColor(getColor(R.color.light_gray));
                        dbanameLL.setVisibility(VISIBLE);
                        dbanameTV.setText("Field Required");

                    }
                } else {
                    dbanameET.setHint("Fire BBQ");
                    focusedID = dbanameET.getId();
                    dbaEmailTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                    dbanameET.setHintTextColor(getColor(R.color.light_gray));
                    dbanameLL.setVisibility(GONE);
                }

            }
        });
        dbaemailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    dbaemailET.setHint("");
                    if (dbaemailET.getText().toString().trim().length() > 5 && !Utils.isValidEmail(dbaemailET.getText().toString().trim())) {
                        dbaEmailTIL.setBoxStrokeColor(getColor(R.color.primary_green));
                        dbaemailLL.setVisibility(VISIBLE);
                        dbaemailET.setHintTextColor(getColor(R.color.error_red));
                        Utils.setUpperHintColor(dbaEmailTIL, getColor(R.color.error_red));
                        dbaemailTV.setText("Invalid Email");

                    } else if (dbaemailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(dbaemailET.getText().toString().trim())) {
                        dbaemailLL.setVisibility(GONE);
                        dbaEmailTIL.setBoxStrokeColorStateList(getColorStateList(R.color.primary_green));
                        dbaemailET.setHintTextColor(getColor(R.color.primary_black));
                        Utils.setUpperHintColor(dbaEmailTIL, getColor(R.color.primary_black));


                    } else {
                        dbaEmailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        dbaemailET.setHintTextColor(getColor(R.color.light_gray));
                        dbaemailLL.setVisibility(VISIBLE);
                        dbaemailTV.setText("Field Required");
                    }
                } else {
                    dbaemailET.setHint("support@firebbq.com");
                    focusedID = dbaemailET.getId();
                    dbaEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    dbaemailET.setHintTextColor(getColor(R.color.light_gray));
                    dbaemailLL.setVisibility(GONE);
                }
            }
        });
    }

    private void textWatchers() {
        dbanameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2) {
                    isdbaName = true;
                    dbaNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(dbaNameTIL, getResources().getColor(R.color.primary_black));
                } else {
                    dbanameTV.setText("Field Required");
                    isdbaName = false;
                }
                enableOrDisableNext();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        dbaemailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 5 && Utils.isValidEmail(dbaemailET.getText().toString().trim())) {
                    isdbaEmail = true;
                    dbaEmailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(dbaEmailTIL, getResources().getColor(R.color.primary_black));
                } else {
                    isdbaEmail = false;
                }
                enableOrDisableNext();
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    public void enableOrDisableNext() {
        try {
            if (isdbaName && isdbaEmail) {
                isNextEnabled = true;
                dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isdbaName + " " + isdbaEmail + " ");
            }
            else {
                Log.e("All boolean", isdbaName + " "+ isPhoneNumber + " " + isdbaEmail + " " );

                isNextEnabled = false;
                dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isNextEnabled) {
            dbaNextCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DBAbasicInformationAcivity.this, DBAddressActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

}