package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CompanyOutLineBoxPhoneNumberEditText;

public class CompanyInformationActivity extends AppCompatActivity {
    public CardView nextCV;
    ImageView close,dropdown;
    CompanyOutLineBoxPhoneNumberEditText phoneNumberET;
    TextInputEditText companynameET, companyemailET,timeZoneET;
    TextInputLayout companynametil, companyemailtil;
    public LinearLayout companynameErrorLL,companyemailErrorLL,phoneNumberErrorLL;
    public TextView companynameerrorTV, companyemailerrorTV,phonenumberTV;
    Dialog chooseEntity;

    public boolean iscompanyName = false, iscompanyEmail = false,isPhoneNumber=false,isNextEnabled = false;
    boolean isEmailError = false, isPhoneError = false;
    public static CompanyInformationActivity companyInformationActivity;
    public static int focusedID = 0;

    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_information);
        companyInformationActivity = this;
        initFields();
        focusWatchers();
        textWatchers();
        close = findViewById(R.id.closeIV);

        dropdown = findViewById(R.id.dropdown_entity);
        dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosebusinessEntityPopup(CompanyInformationActivity.this);

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyInformationActivity.this, BusinessRegistrationTrackerActivity.class);
                startActivity(intent);
            }
        });

    }

    private void choosebusinessEntityPopup(final Context context) {
        try {
            chooseEntity = new Dialog(context);
            chooseEntity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            chooseEntity.setContentView(R.layout.activity_choose_business_entity);
            chooseEntity.setCancelable(true);
            Window window = chooseEntity.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            chooseEntity.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            chooseEntity.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            chooseEntity.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void initFields() {
        try {
            companyemailET = findViewById(R.id.companyemailET);
            companyemailtil = findViewById(R.id.companyemailTIL);

            companynameET = findViewById(R.id.companynameET);
            companynametil = findViewById(R.id.companynameTIL);

            companynameerrorTV = findViewById(R.id.companyNameErrorTV);
            companynameErrorLL = findViewById(R.id.companyNameErrorLL);

            companyemailerrorTV = findViewById(R.id.companyemailErrorTV);
            companyemailErrorLL = findViewById(R.id.companyemailErrorLL);

            phonenumberTV = findViewById(R.id.CompanyphoneNumberErrorTV);
            phoneNumberET = findViewById(R.id.CompanyphoneNumberOET);
            phoneNumberErrorLL = findViewById(R.id.CompanyphoneNumberErrorLL);
            nextCV = findViewById(R.id.nextCv);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void focusWatchers() {
        companynameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    companynameET.setHint("");
                    if (companynameET.getText().toString().trim().length() > 1) {
                        companynameErrorLL.setVisibility(GONE);
                        companynametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        companynameET.setHintTextColor(getColor(R.color.light_gray));
                        Utils.setUpperHintColor(companynametil, getColor(R.color.primary_black));

                    } else if (companynameET.getText().toString().trim().length() == 1) {
                        companynametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(companynametil, getColor(R.color.error_red));
                        companynameErrorLL.setVisibility(VISIBLE);
                        companynameerrorTV.setText("Minimum 2 Characters Required");
                    } else {
                        companynametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        companynameET.setHintTextColor(getColor(R.color.light_gray));
                        companynameErrorLL.setVisibility(VISIBLE);
                        companynameerrorTV.setText("Field Required");

                    }
                } else {
                    companynameET.setHint("Company’s Name");
                    focusedID = companynameET.getId();
                    companynametil.setBoxStrokeColor(getColor(R.color.primary_green));
                    companynameET.setHintTextColor(getColor(R.color.light_gray));
                    companynameErrorLL.setVisibility(GONE);
                }

            }
        });
        companyemailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    companyemailET.setHint("");
                    if (companyemailET.getText().toString().trim().length() > 2 && !Utils.isValidEmail(companyemailET.getText().toString().trim())) {
                        companyemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(companyemailtil, getColor(R.color.error_red));
                        companyemailErrorLL.setVisibility(VISIBLE);
                        companyemailerrorTV.setText("Invalid Email");
                    } else if (companyemailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(companyemailET.getText().toString().trim())) {
                        companyemailtil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(companyemailtil, getColor(R.color.primary_black));
                        companyemailErrorLL.setVisibility(GONE);
                    } else {
                        companyemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(companyemailtil, getColor(R.color.light_gray));
                        companyemailErrorLL.setVisibility(VISIBLE);
                        companyemailerrorTV.setText("Field Required");
                    }
                } else {
                    companyemailET.setHint("123@coyni.com");
                    focusedID = companyemailET.getId();
                    companyemailtil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(companyemailtil, getColor(R.color.primary_green));
                }
            }
        });

    }

    private void textWatchers() {
        companynameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2) {
                    iscompanyName = true;
                    companynametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(companynametil, getResources().getColor(R.color.primary_green));
                } else {
                    companynameerrorTV.setText("Field Required");
                    iscompanyName = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        companyemailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                    iscompanyEmail = false;
                    companyemailErrorLL.setVisibility(GONE);
                    companyemailtil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(companyemailtil,getResources().getColor(R.color.primary_green));

                } else if (companyemailET.getText().toString().trim().length() == 0) {
                    companyemailErrorLL.setVisibility(VISIBLE);
                    companyemailerrorTV.setText("Field Required");
                }
                if (Utils.isValidEmail(charSequence.toString().trim()) && charSequence.toString().trim().length() > 5) {
                    iscompanyEmail = true;
                } else {
                    iscompanyEmail = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = companyemailET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                        companyemailET.setText(companyemailET.getText().toString().replaceAll(" ", ""));
                        companyemailET.setSelection(companyemailET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void enableOrDisableNext() {

        try {
            if (iscompanyName && iscompanyEmail && isPhoneNumber) {
                isNextEnabled = true;
                nextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", iscompanyName + " "+ iscompanyEmail + " "+ isPhoneNumber + " ");
            } else {

                Log.e("All boolean", iscompanyName + " "+ isPhoneNumber + " " + iscompanyEmail + " " );

                isNextEnabled = false;
                nextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
                if(isNextEnabled) {
                    nextCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(CompanyInformationActivity.this, CompanyAddress2Activity.class);
                            startActivity(intent);
                        }
                    });
                }

    }

}
