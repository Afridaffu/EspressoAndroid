package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;

public class CompanyAddress2Activity extends AppCompatActivity {
    ImageView statedropdownIV, previouspageIV;
    public CardView nextCV;
    TextInputLayout companyaddresstil, companyaddress2til, citytil, statetil, zipcodetil;
    TextInputEditText companyaddressET, companyaddress2ET, cityET, stateET, zipcodeET;
    LinearLayout address1ErrorLL, address2ErrorLL, cityErrorLL, stateErrorLL, zipcodeErrorLL;
    TextView address1ErrorTV, address2ErrorTV, cityErrorTV, stateErrorTV, zipcodeErrorTV;
    Dialog popupStates;
    public boolean isCompanyAdress1 = false, isCity = false, isState = false, isZipcode = false, isNextEnabled = false;
    public static int focusedID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_address2);
        initfields();
        textWatchers();
        focusWatchers();
        previouspageIV = findViewById(R.id.previousPageIV);
        previouspageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CompanyAddress2Activity.this, CompanyInformationActivity.class);
                startActivity(intent);
            }
        });


    }

    protected void onResume() {
        super.onResume();

        if (companyaddressET.getId() == focusedID) {
            companyaddressET.requestFocus();
        } else if (companyaddress2ET.getId() == focusedID) {
            companyaddress2ET.requestFocus();
        } else if (cityET.getId() == focusedID) {
            cityET.requestFocus();
        } else if (stateET.getId() == focusedID) {
            stateET.requestFocus();
        } else if (zipcodeET.getId() == focusedID) {
            zipcodeET.requestFocus();
        } else {
            companyaddressET.requestFocus();
        }
        Log.e("ID", "" + focusedID);
    }

    private void initfields() {
        try {
            companyaddresstil = findViewById(R.id.companyaddressTIL);
            companyaddress2til = findViewById(R.id.companyaddress2TIL);
            citytil = findViewById(R.id.cityTIL);
            statetil = findViewById(R.id.stateTIL);
            zipcodetil = findViewById(R.id.zipcodeTIL);

            companyaddressET = findViewById(R.id.companyaddressET);
            companyaddress2ET = findViewById(R.id.companyaddress2ET);
            cityET = findViewById(R.id.cityET);
            stateET = findViewById(R.id.stateET);
            zipcodeET = findViewById(R.id.zipcodeET);

            address1ErrorLL = findViewById(R.id.address1ErrorLL);
            address2ErrorLL = findViewById(R.id.address2ErrorLL);
            cityErrorLL = findViewById(R.id.cityErrorLL);
            stateErrorLL = findViewById(R.id.stateErrorLL);
            zipcodeErrorLL = findViewById(R.id.zipcodeErrorLL);

            address1ErrorTV = findViewById(R.id.address1ErrorTV);
            address2ErrorTV = findViewById(R.id.address2ErrorTV);
            cityErrorTV = findViewById(R.id.cityErrorTV);
            stateErrorTV = findViewById(R.id.stateErrorTV);
            zipcodeErrorTV = findViewById(R.id.zipcodeErrorTV);
            statedropdownIV = findViewById(R.id.stateimageIV);

            nextCV = findViewById(R.id.nextCV);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void focusWatchers() {
        companyaddressET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    companyaddressET.setHint("");
                    if (companyaddressET.getText().toString().trim().length() > 1) {
                        companyaddresstil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(companyaddresstil, getColor(R.color.primary_black));
                        address1ErrorLL.setVisibility(GONE);

                    } else if (companyaddressET.getText().toString().trim().length() == 1) {
                        companyaddresstil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(companyaddresstil, getColor(R.color.error_red));
                        address1ErrorLL.setVisibility(VISIBLE);
                        address1ErrorTV.setText("Minimum 2 Characters Required");
                    } else {
                        companyaddresstil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        companyaddressET.setHintTextColor(getColor(R.color.light_gray));
                        address1ErrorLL.setVisibility(VISIBLE);
                        address1ErrorTV.setText("Field Required");
                    }
                } else {
                    companyaddressET.setHint("Street Address");
                    companyaddresstil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(companyaddresstil, getColor(R.color.light_gray));
                    address1ErrorLL.setVisibility(GONE);

                }
            }
        });
        companyaddress2ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                      if (!b) {
                          companyaddress2ET.setHint("");
                          companyaddress2til.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(companyaddress2til, getColor(R.color.black));
                        } else {
                          companyaddress2ET.setHint("Apt#, Suit, Floor ");
                            companyaddress2til.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(companyaddress2til, getColor(R.color.primary_green));
                          address2ErrorLL.setVisibility(GONE);
                        }
            }
        });
        cityET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    cityET.setHint("");
                    if (cityET.getText().toString().trim().length() > 0) {
                        cityErrorLL.setVisibility(GONE);
                        citytil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(citytil, getColor(R.color.primary_black));

                    } else {
                        citytil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(citytil, getColor(R.color.light_gray));
                        cityErrorLL.setVisibility(VISIBLE);
                        cityErrorTV.setText("Field Required");
                    }
                } else {
                    cityET.setHint("City");
                    citytil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(citytil, getColor(R.color.primary_green));
                    cityErrorLL.setVisibility(GONE);
                }
            }
        });
        stateET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    stateET.setHint("");
                    if (stateET.getText().toString().trim().length() > 0) {
                        stateErrorLL.setVisibility(GONE);
                        statetil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(statetil, getColor(R.color.primary_black));

                    } else {
                        statetil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(statetil, getColor(R.color.light_gray));
                        stateErrorLL.setVisibility(VISIBLE);
                        stateErrorTV.setText("Field Required");
                    }
                } else {
                    stateET.setHint("State");
                    statetil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(statetil, getColor(R.color.primary_green));
                    stateErrorLL.setVisibility(GONE);
                }
            }
        });
        zipcodeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    zipcodeET.setHint("");
                    if (zipcodeET.getText().toString().trim().length() > 0 && zipcodeET.getText().toString().trim().length() == 5) {
                        zipcodeErrorLL.setVisibility(GONE);
                        zipcodetil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(zipcodetil, getColor(R.color.primary_black));
                    }
                    else if (zipcodeET.getText().toString().trim().length() >=2 && zipcodeET.getText().toString().trim().length() <= 4 ) {
                        zipcodetil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(zipcodetil, getColor(R.color.error_red));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Field Required 5 Characters");

                    }
                    else {
                        zipcodetil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(zipcodetil, getColor(R.color.light_gray));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Field Required");
                    }
                } else {
                    zipcodeET.setHint("Zipcode");
                    zipcodetil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodetil, getColor(R.color.primary_green));
                    zipcodeErrorLL.setVisibility(GONE);
                }
            }
        });

    }

    private void textWatchers() {

        companyaddressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2) {
                    isCompanyAdress1 = true;
                    companyaddresstil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(companyaddresstil, getResources().getColor(R.color.primary_green));
                } else {
                    address1ErrorTV.setText("Field Required");
                    isCompanyAdress1 = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });
        companyaddress2ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2 && charSequence.toString().trim().length() < 30) {
                    companyaddress2til.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(companyaddress2til, getResources().getColor(R.color.primary_green));
                } else {
                }
                enableOrDisableNext();
            }
            @Override
            public void afterTextChanged(Editable editable) {
                try {
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        cityET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    isCity = true;
                    citytil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(citytil, getResources().getColor(R.color.primary_green));
                } else {
                    isCity = false;
                    cityErrorLL.setVisibility(VISIBLE);
                    cityErrorTV.setText("Field Required");
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = cityET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        cityET.setText("");
                        cityET.setSelection(cityET.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        cityET.setText(cityET.getText().toString());
                        cityET.setSelection(cityET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        stateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    isState = true;
                } else {
                    isState = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        zipcodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2 ) {
                    isZipcode = true;
                    zipcodeErrorLL.setVisibility(GONE);
                    zipcodetil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                } else {
                    isZipcode = false;
                        zipcodeET.setHintTextColor(getColor(R.color.light_gray));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Field Required 5 Characters");
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void enableOrDisableNext() {

            try {
                if (isCompanyAdress1 && isCity && isState && isZipcode) {
                    isNextEnabled = true;
                    nextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                    Log.e("All boolean", isCompanyAdress1 + " " + isCity + " " + isState + " " + isZipcode);
                } else {

                    Log.e("All boolean", isCompanyAdress1 + " " + isCity + " " + isState + " " + isZipcode);

                    isNextEnabled = false;
                    nextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        if(isNextEnabled == true) {
            nextCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CompanyAddress2Activity.this, AdditionDocInfoActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
//    private void statesPopup() {
//        try {
//            popupStates = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//            popupStates.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            popupStates.setContentView(R.layout.countries_list);
//            popupStates.setCanceledOnTouchOutside(false);
//            popupStates.setCancelable(false);
//            popupStates.show();
//            bindStates();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    private void bindStates() {
    }


}