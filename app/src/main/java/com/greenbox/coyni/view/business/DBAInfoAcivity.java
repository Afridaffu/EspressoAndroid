package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.greenbox.coyni.utils.outline_et.MonthlyProcessingVolumeEditText;

public class DBAInfoAcivity extends AppCompatActivity {
    TextInputLayout dbanameTIL, dbaemailTIL, typetil;
    public LinearLayout dbanameLL, dbaemailLL, customerphonenumLL, uploadLL;
    CompanyOutLineBoxPhoneNumberEditText customerphoneNumberET;
    MonthlyProcessingVolumeEditText volumeET1, volumeET2, volumeET3;
    public TextInputEditText dbanameET, dbaemailET;
    public TextView dbanameTV, dbaemailTV, customernumTV;
    public CardView dbaNextCV;
    public static int focusedID = 0;
    TextView uploadtext;
    ImageView businessEntity;
    Dialog entity;
    public static DBAInfoAcivity dbAbasicInformationAcivity;


    public boolean isdbaName = false, isdbaEmail = false, iscustPhoneNumber = false, isNextEnabled = false;
    boolean isEmailError = false, isPhoneError = false;
    int[][] errorState, state;
    int[] errorColor, color;
    ColorStateList errorColorState, colorState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbAbasicInformationAcivity = this;
        setContentView(R.layout.activity_dba_information);
        initFields();
        focusWatchers();
        textWatchers();

        businessEntity = findViewById(R.id.businesEntityIV);
        businessEntity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseEntityPopup();

            }
        });
    }

    protected void onResume() {
        super.onResume();
        if (dbanameET.getId() == focusedID) {
            dbanameET.requestFocus();
        } else if (dbaemailET.getId() == focusedID) {
            dbaemailET.requestFocus();
        } else if (customerphoneNumberET.getId() == focusedID) {
            customerphoneNumberET.requestFocus();
        }
        dbanameET.requestFocus();
        Log.e("ID", "" + focusedID);
    }

    private void chooseEntityPopup() {
        try {
            entity = new Dialog(DBAInfoAcivity.this);
            entity.requestWindowFeature(Window.FEATURE_NO_TITLE);
            entity.setContentView(R.layout.activity_business_type_bottom_sheet);
            entity.setCancelable(true);
            Window window = entity.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            entity.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            entity.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            entity.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initFields() {
        errorState = new int[][]{new int[]{android.R.attr.state_focused}};
        errorColor = new int[]{getResources().getColor(R.color.error_red)};
        errorColorState = new ColorStateList(errorState, errorColor);

        state = new int[][]{new int[]{android.R.attr.state_enabled}};
        color = new int[]{getResources().getColor(R.color.primary_green)};
        colorState = new ColorStateList(state, color);

        dbanameTIL = findViewById(R.id.DBAnameTIL);
        dbaemailTIL = findViewById(R.id.DBAemailTIL);

        dbanameLL = findViewById(R.id.DBAnameErrorLL);
        dbaemailLL = findViewById(R.id.DBAemailErrorLL);

        dbanameET = findViewById(R.id.DBAnameET);
        dbaemailET = findViewById(R.id.DBAemailET);

        dbanameTV = findViewById(R.id.DBAnameErrorTV);
        dbaemailTV = findViewById(R.id.DBAemailErrorTV);
        dbaNextCV = findViewById(R.id.dbaNextCV);
        customerphoneNumberET = findViewById(R.id.customerphoneNumberOET);
        customerphoneNumberET.setFrom("DbaInfo",this);
        customerphoneNumberET.setHintText("Customer Service Phone Number");

        volumeET1 = findViewById(R.id.volumeET);

        volumeET2 = findViewById(R.id.highET);
        volumeET2.setHint("High Ticket");

        volumeET3 = findViewById(R.id.averageET);
        volumeET3.setHint("Average Ticket");

        customerphonenumLL = findViewById(R.id.customerphoneNumberErrorLL);
        customernumTV = findViewById(R.id.customerphoneNumberErrorTV);

        uploadLL = findViewById(R.id.uploadLL);
        uploadtext = findViewById(R.id.uploadTextTV);
    }

    private void focusWatchers() {
        dbanameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    dbanameET.setHint("");
                    if (dbanameET.getText().toString().trim().length() > 1) {
                        dbanameLL.setVisibility(GONE);
                        dbanameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        dbanameET.setHintTextColor(getColor(R.color.light_gray));
                        Utils.setUpperHintColor(dbanameTIL, getColor(R.color.primary_black));

                    } else if (dbanameET.getText().toString().trim().length() == 1) {
                        dbanameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(dbanameTIL, getColor(R.color.error_red));
                        dbanameET.setHintTextColor(getColor(R.color.light_gray));
                        dbanameLL.setVisibility(VISIBLE);
                        dbanameTV.setText("Minimum 2 Characters Required");
                    } else {
                        dbanameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        dbanameET.setHintTextColor(getColor(R.color.light_gray));
                        dbanameLL.setVisibility(VISIBLE);
                        dbanameTV.setText("Field Required");

                    }
                } else {
                    dbanameET.setHint("Fire BBQ");
                    focusedID = dbanameET.getId();
                    dbanameTIL.setBoxStrokeColor(getColor(R.color.primary_green));
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
                        dbaemailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        dbaemailLL.setVisibility(VISIBLE);
                        dbaemailET.setHintTextColor(getColor(R.color.error_red));
                        Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.error_red));
                        dbaemailTV.setText("Invalid Email");

                    } else if (dbaemailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(dbaemailET.getText().toString().trim())) {
                        dbaemailLL.setVisibility(GONE);
                        dbaemailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        dbaemailET.setHintTextColor(getColor(R.color.primary_black));
                        Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.primary_black));

                    } else if (dbaemailET.getText().toString().trim().length() > 0 && dbaemailET.getText().toString().trim().length() <= 5) {
                        dbaemailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.error_red));
                        dbaemailLL.setVisibility(VISIBLE);
                        dbaemailTV.setText("Field Required");
                    }
                } else {
                    dbaemailET.setHint("support@firebbq.com");
                    focusedID = dbaemailET.getId();
                    dbaemailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
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
                    dbanameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(dbanameTIL, getResources().getColor(R.color.primary_black));
                } else {
                    dbanameTV.setText("Field Required");
                    isdbaName = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dbaemailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                    isEmailError = false;
                    dbaemailLL.setVisibility(GONE);
                    dbaemailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        emailTIL.setHintTextColor(colorState);
                    Utils.setUpperHintColor(dbaemailTIL, getResources().getColor(R.color.primary_green));

                } else if (dbaemailET.getText().toString().trim().length() == 0) {
                    dbaemailLL.setVisibility(VISIBLE);
                    dbaemailTV.setText("Field Required");
                }
                if (Utils.isValidEmail(charSequence.toString().trim()) && charSequence.toString().trim().length() > 5) {
                    isdbaEmail = true;
                } else {
                    isdbaEmail = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = dbaemailET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                        dbaemailET.setText(dbaemailET.getText().toString().replaceAll(" ", ""));
                        dbaemailET.setSelection(dbaemailET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void enableOrDisableNext() {
        try {
            if (isdbaName && isdbaEmail && iscustPhoneNumber) {
                isNextEnabled = true;
                uploadtext.setVisibility(GONE);
                uploadLL.setVisibility(VISIBLE);
                dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                Log.e("All boolean", isdbaName + " " + isdbaEmail + " " + iscustPhoneNumber);
            } else {
                Log.e("All boolean", isdbaName + " " + isdbaEmail + " " + iscustPhoneNumber);

                isNextEnabled = false;
                dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isNextEnabled) {
            dbaNextCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DBAInfoAcivity.this, DBAddressActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

}