package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
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

public class DBAddressActivity extends AppCompatActivity {
    TextInputLayout adresstil1,adresstil2,citytil1,statetil1,zipcodetil1;
    TextInputEditText adressET1,adressET2,cityET1,stateET1,zipcodeET1;
    LinearLayout adressll,adress2ll,cityll1,statell1,zipcodell1;
    TextView adressTV,adress2TV,cityTV1,stateTV1,zipcodeTV1;
    public CardView nextcv1;
    ImageView DBAcancelIV;
    public boolean isAdress1=false,isCity1=false, isState1=false, isZipcode1=false, isNextEnabled=false;
    public static int focusedID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbaddress);
        initfields();
        textWatchers();
        focusWatchers();
        DBAcancelIV = findViewById(R.id.DBACancelIV);
        DBAcancelIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DBAddressActivity.this, DBAInfoAcivity.class);
                startActivity(intent);

            }
        });
    }
    protected void onResume() {
        super.onResume();

        if (adressET1.getId() == focusedID) {
            adressET1.requestFocus();
        } else if (adressET2.getId() == focusedID) {
            adressET2.requestFocus();
        } else if (cityET1.getId() == focusedID) {
            cityET1.requestFocus();
        } else if (stateET1.getId() == focusedID) {
            stateET1.requestFocus();
        } else if (zipcodeET1.getId() == focusedID) {
            zipcodeET1.requestFocus();
        } else {
            adressET1.requestFocus();
        }
        Log.e("ID", "" + focusedID);
    }


    private void initfields() {
        adresstil1 = findViewById(R.id.adress1TIL);
        adresstil2 = findViewById(R.id.adress2TIL);
        citytil1 = findViewById(R.id.cityTIL1);
        statetil1 = findViewById(R.id.stateTIL1);
        zipcodetil1 = findViewById(R.id.zipcodeTIL1);

        adressET1 = findViewById(R.id.adress1ET);
        adressET2 = findViewById(R.id.adress2ET);
        cityET1 = findViewById(R.id.cityET1);
        stateET1 = findViewById(R.id.stateET1);
        zipcodeET1 = findViewById(R.id.zipcodeET1);

        adressll = findViewById(R.id.dbaAdressErrorLL);
        adress2ll = findViewById(R.id.dbaAdres2ErrorLL);
        cityll1 = findViewById(R.id.city1LL);
        statell1 = findViewById(R.id.state1ErrorLL);
        zipcodell1 =findViewById(R.id.zipcode1ErrorLL);

        adressTV = findViewById(R.id.dbaAdressErrorTV);
        adress2TV = findViewById(R.id.dbaAdress2ErrorTV);
        cityTV1 = findViewById(R.id.city1ErrorTV);
        stateTV1 = findViewById(R.id.state1ErrorTV);
        zipcodeTV1 = findViewById(R.id.zipcode1ErrorTV);

        nextcv1 = findViewById(R.id.NextCV1);
    }

    private void focusWatchers() {
        adressET1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    adressET1.setHint("");
                    if (adressET1.getText().toString().trim().length() > 1) {
                        adresstil1.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        adressET1.setHintTextColor(getColor(R.color.light_gray));
                        Utils.setUpperHintColor(adresstil1, getColor(R.color.primary_black));
                        adressll.setVisibility(GONE);
                    } else if (adressET1.getText().toString().trim().length() == 1) {
                        adresstil1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        adressET1.setHintTextColor(getColor(R.color.light_gray));
                        Utils.setUpperHintColor(adresstil1, getColor(R.color.error_red));
                        adressll.setVisibility(View.VISIBLE);
                        adressTV.setText("Minimum 2 Characters Required");
                    } else {
                        adresstil1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(adresstil1, getColor(R.color.light_gray));
                        adressll.setVisibility(VISIBLE);
                        adressTV.setText("Field Required");

                    }
                } else {
                    adressET1.setHint("Street Address");
                    adresstil1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(adresstil1, getColor(R.color.primary_green));
                    adressll.setVisibility(GONE);

                }
            }
        });
        adressET2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    adressET2.setHint("");
                    adresstil2.setBoxStrokeColorStateList(Utils.getNormalColorState());
                    Utils.setUpperHintColor(adresstil2, getColor(R.color.light_gray));
                } else {
                    adressET2.setHint("Apt#, Suit, Floor");
                    adresstil2.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(adresstil2, getColor(R.color.primary_green));
                    adress2ll.setVisibility(GONE);
                }
            }
        });
        cityET1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    cityET1.setHint("");
                    if (cityET1.getText().toString().trim().length() > 0) {
                        cityll1.setVisibility(GONE);
                        citytil1.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(citytil1, getColor(R.color.primary_black));

                    } else {
                        citytil1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(citytil1, getColor(R.color.light_gray));
                        cityll1.setVisibility(VISIBLE);
                        cityTV1.setText("Field Required");
                    }
                } else {
                    cityET1.setHint("City");
                    citytil1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(citytil1, getColor(R.color.primary_green));
                    cityll1.setVisibility(GONE);
                }
            }
        });
        stateET1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    stateET1.setHint("");
                    if (stateET1.getText().toString().trim().length() > 0) {
                        statell1.setVisibility(GONE);
                        statetil1.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(statetil1, getColor(R.color.primary_black));

                    } else {
                        statetil1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(statetil1, getColor(R.color.light_gray));
                        statell1.setVisibility(VISIBLE);
                        stateTV1.setText("Field Required");
                    }
                } else {
                    stateET1.setHint("State");
                    statetil1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(statetil1, getColor(R.color.primary_green));
                    statell1.setVisibility(GONE);
                }
            }
        });
        zipcodeET1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    zipcodeET1.setHint("");
                    if (zipcodeET1.getText().toString().trim().length() > 0 && zipcodeET1.getText().toString().trim().length() == 5) {
                        zipcodell1.setVisibility(GONE);
                        zipcodetil1.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(zipcodetil1, getColor(R.color.primary_black));

                    }
                    else if (zipcodeET1.getText().toString().trim().length() >=1 && zipcodeET1.getText().toString().trim().length() <= 4 ) {
                        zipcodetil1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(zipcodetil1, getColor(R.color.error_red));
                        zipcodell1.setVisibility(VISIBLE);
                        zipcodeTV1.setText("Field Required 5 Characters");
                    }
                    else {
                        zipcodetil1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(zipcodetil1, getColor(R.color.light_gray));
                        zipcodell1.setVisibility(VISIBLE);
                        zipcodeTV1.setText("Field Required");
                    }
                } else {
                    zipcodeET1.setHint("Zipcode");
                    zipcodetil1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodetil1, getColor(R.color.primary_green));
                    zipcodell1.setVisibility(GONE);
                }
            }
        });
    }
    private void textWatchers() {
        adressET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2 ) {
                    isAdress1 = true;
                    adresstil1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(adresstil1, getResources().getColor(R.color.primary_green));
                } else {
                    adressTV.setText("Field Required");
                    isAdress1 = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        cityET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    isCity1 = true;
                    citytil1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(citytil1, getResources().getColor(R.color.primary_green));
                } else {
                    isCity1 = false;
                    cityll1.setVisibility(VISIBLE);
                    cityTV1.setText("Field Required");
                }
                enableOrDisableNext();
            }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = cityET1.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        cityET1.setText("");
                        cityET1.setSelection(cityET1.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        cityET1.setText(cityET1.getText().toString());
                        cityET1.setSelection(cityET1.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        stateET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    isState1 = true;
                } else {
                    isState1 = false;
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
        zipcodeET1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2 && charSequence.toString().trim().length() == 5) {
                    isZipcode1 = true;
                    zipcodell1.setVisibility(GONE);
                    zipcodetil1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodetil1, getResources().getColor(R.color.primary_green));
                } else {
                    isZipcode1 = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = zipcodeET1.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        zipcodeET1.setText("");
                        zipcodeET1.setSelection(zipcodeET1.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        zipcodeET1.setText(zipcodeET1.getText().toString());
                        zipcodeET1.setSelection(zipcodeET1.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void enableOrDisableNext() {
        try {
            if (isAdress1 && isCity1 && isState1 && isZipcode1) {
                isNextEnabled = true;
                nextcv1.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isAdress1 + " " + isCity1 + " " + isState1 + " " + isZipcode1);
            } else {

                Log.e("All boolean", isAdress1+ " " + isCity1 + " " + isState1 + " " + isZipcode1);

                isNextEnabled = false;
                nextcv1.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isNextEnabled == true) {
            nextcv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DBAddressActivity.this, BusinessRegistrationTrackerActivity.class);
                    startActivity(intent);
                }
            });
        }

    }
}