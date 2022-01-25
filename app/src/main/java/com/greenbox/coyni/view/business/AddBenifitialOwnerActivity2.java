package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;

public class AddBenifitialOwnerActivity2 extends AppCompatActivity {
    TextInputEditText address1ET, address2ET, cityET, stateET, zipcodeET;
    TextInputLayout address1TIL, address2TIL, cityTIL, stateTIL, zipcodeTIL, countryTIL;
    ConstraintLayout stateCL;
    MyApplication myApplicationObj;
    CardView Addbenifitialowner2CloseCV;
    boolean isAddress1, isAddress2, isCity, isState, isZipcode, isSaveEnabled;
    Dialog popupStates;
    ImageView backIV,stateimg;
//    StatesListAdapter statesListAdapter;
//    List<States> listStates = new ArrayList<>();
    LinearLayout address1ErrorLL, address2ErrorLL, cityErrorLL, zipcodeErrorLL;
    TextView address1ErrorTV, address2ErrorTV, cityErrorTV, zipcodeErrorTV;
    Long mLastClickTime = 0L;
    public static int focusedID = 0;

    ProgressDialog dialog;
    CustomerProfileViewModel customerProfileViewModel;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_add_benifitial_owner_2);

        stateimg = findViewById(R.id.Stateimg);
        stateimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog((AddBenifitialOwnerActivity2.this));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                dialog.setContentView(R.layout.states_bottom_dialog);
                Window window = dialog.getWindow();
//                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.45);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams wl = window.getAttributes();
                wl.gravity = Gravity.BOTTOM;
                wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wl);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

            }
        });

        initfields();
        textWatchers();
        focusWatchers();
//        initObservers();

    }
    @Override
    protected void onResume() {
        super.onResume();
//        firstNameET.requestFocus();
        if (address1ET.getId() == focusedID) {
            address1ET.requestFocus();
        } else if (cityET.getId() == focusedID) {
            cityET.requestFocus();
        } else if (zipcodeET.getId() == focusedID) {
            zipcodeET.requestFocus();
        }  else {
            address1ET.requestFocus();
        }

        Log.e("ID", "" + focusedID);
    }
    public void initfields() {

//            myApplicationObj = (MyApplication) getApplicationContext();
        address1TIL = findViewById(R.id.mailing_address_TIL);
        address2TIL = findViewById(R.id.m_a_optionalTIL);
        cityTIL = findViewById(R.id.City_TIL);
        stateTIL = findViewById(R.id.stateTIL);
        stateCL = findViewById(R.id.stateCL);
        zipcodeTIL = findViewById(R.id.Zip_TIL);

        address1ET = findViewById(R.id.mailing_address_ET);
        address2ET = findViewById(R.id.m_a_optionalET);
        cityET = findViewById(R.id.City_ET);
        stateET = findViewById(R.id.stateET);
        zipcodeET = findViewById(R.id.Zip_ET);
        backIV = findViewById(R.id.backIV);

        address1ErrorLL = findViewById(R.id.mailing_addressErrorLL);
        address1ErrorTV = findViewById(R.id.mailing_addressErrorTV);

        address2ErrorLL = findViewById(R.id.m_a_optionalErrorLL);
        address2ErrorTV = findViewById(R.id.m_a_optionalErrorTV);

        cityErrorLL = findViewById(R.id.City_ErrorLL);
        cityErrorTV = findViewById(R.id.City_ErrorTV);

        zipcodeErrorLL = findViewById(R.id.Zip_ErrorLL);
        zipcodeErrorTV = findViewById(R.id.Zip_ErrorTV);
        countryTIL = findViewById(R.id.Country_TIL);

        Addbenifitialowner2CloseCV = findViewById(R.id.addbenifitialowner2CloseCV);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        stateCL.setOnClickListener(view -> {
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                return;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//            if (Utils.isKeyboardVisible)
//                Utils.hideKeypad(this);
//            Utils.populateStates(this, stateET, myApplicationObj);
//        });
//
//        stateTIL.setOnClickListener(view -> {
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                return;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//            if (Utils.isKeyboardVisible)
//                Utils.hideKeypad(this);
//            Utils.populateStates(this, stateET, myApplicationObj);
//        });
//
//        stateET.setOnClickListener(view -> {
//            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
//                return;
//            }
//            mLastClickTime = SystemClock.elapsedRealtime();
//            if (Utils.isKeyboardVisible)
//                Utils.hideKeypad(this);
//            Utils.populateStates(this, stateET, myApplicationObj);
//        });
    }

    private void textWatchers() {
        address1ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    isAddress1 = true;
                    address1ErrorLL.setVisibility(GONE);
//                    address1TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(address1TIL, getResources().getColor(R.color.primary_green));
                } else {
                    address1ErrorLL.setVisibility(VISIBLE);
                    address1ErrorTV.setText("Field Required");
                    isAddress1 = false;
                }
                enableOrDisableSave();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = address1ET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        address1ET.setText(str.substring(1));
//                        address1ET.setSelection(address1ET.getText().length());
                        address1ErrorLL.setVisibility(GONE);
                    } else if (str.substring(0).equals(" ")) {
                        address1ET.setText("");
                        address1ET.setSelection(address1ET.getText().length());
                        address1ErrorLL.setVisibility(GONE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        address2ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if(charSequence.length() > 0){
//                    isAddress2 = true;
//                    address2ErrorLL.setVisibility(GONE);
//                    address2TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(address2TIL,getResources().getColor(R.color.primary_green));
//                }else{
//                    address2ErrorLL.setVisibility(VISIBLE);
//                    address2ErrorTV.setText("Field Required");
//                    isAddress2 = false;
//                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = address2ET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        address2ET.setText(str.substring(1));
                        address2ErrorLL.setVisibility(GONE);
                    } else if (str.substring(0).equals(" ")) {
                        address2ET.setText("");
                        address2ET.setSelection(address2ET.getText().length());
                        address2ErrorLL.setVisibility(GONE);
                    }

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
                if (charSequence.length() > 0) {
                    isCity = true;
                    cityErrorLL.setVisibility(GONE);
//                    cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(cityTIL, getResources().getColor(R.color.primary_green));
                } else {
                    cityErrorLL.setVisibility(VISIBLE);
                    cityErrorTV.setText("Field Required");
                    isCity = false;
                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = cityET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        cityET.setText(str.substring(1));
                        cityErrorLL.setVisibility(GONE);
                    } else if (str.substring(0).equals(" ")) {
                        cityET.setText("");
                        cityET.setSelection(cityET.getText().length());
                        cityErrorLL.setVisibility(GONE);
                    } else if (str.length() > 0 && str.substring(str.length() - 1).equals(".")) {
                        cityET.setText(cityET.getText().toString().replaceAll(".", ""));
                        cityET.setSelection(cityET.getText().length());
                        cityErrorLL.setVisibility(GONE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
//        stateET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                try {
//                    if (charSequence.length() > 0) {
//                        Utils.setUpperHintColor(stateTIL, getResources().getColor(R.color.primary_black));
//                        stateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                        isState = true;
//                    } else {
//                        isState = false;
//                    }
//                    enableOrDisableSave();
//                } catch (Resources.NotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        zipcodeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 5) {
                    isZipcode = true;
                    zipcodeErrorLL.setVisibility(GONE);
//                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_green));
                } else if (charSequence.length() > 0 && charSequence.length() < 5) {
                    isZipcode = false;
                    zipcodeErrorLL.setVisibility(GONE);
//                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_green));
                    zipcodeErrorTV.setText("Minimum 5 Characters Required");
                } else if (charSequence.length() == 0) {
                    isZipcode = false;
                    zipcodeErrorLL.setVisibility(VISIBLE);
                    zipcodeErrorTV.setText("Field Required");
                }
                enableOrDisableSave();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void focusWatchers() {
        address1ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    address1ET.setHint("");
                    if (address1ET.getText().toString().trim().length() > 0) {
                        address1ErrorLL.setVisibility(GONE);
                        address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(address1TIL, getColor(R.color.primary_black));

                    } else {
                        address1TIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(address1TIL, getColor(R.color.light_gray));
                        address1ErrorLL.setVisibility(VISIBLE);
                        address1ErrorTV.setText("Field Required");
                    }
                } else {
                    focusedID = address1ET.getId();
                    address1ET.setHint("Street Address");
                    address1TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address1TIL, getColor(R.color.primary_green));
                }
            }
        });

        address2ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    address2ET.setHint("");
                    if (address2ET.getText().toString().length() > 0) {
//                        address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
//                        Utils.setUpperHintColor(address2TIL, getColor(R.color.primary_black));
                    } else {
//                        address2TIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                        Utils.setUpperHintColor(address2TIL, getColor(R.color.light_gray));
                    }
                } else {
                    focusedID = address2ET.getId();
                    address2ET.setHint("Apt#, Suit, Floor ");
//                    address2TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(address2TIL, getColor(R.color.primary_green));
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
                        cityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_black));

                    } else {
                        cityTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.light_gray));
                        cityErrorLL.setVisibility(VISIBLE);
                        cityErrorTV.setText("Field Required");
                    }
                } else {
                    focusedID = cityET.getId();
                    cityET.setHint("City");
                    cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_green));
                }
            }
        });

        zipcodeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    zipcodeET.setHint("");
                    if (zipcodeET.getText().toString().trim().length() == 5) {
                        zipcodeErrorLL.setVisibility(GONE);
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_black));
                    } else if (zipcodeET.getText().toString().trim().length() < 5 && zipcodeET.getText().toString().trim().length() > 0) {
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.error_red));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Minimum 5 Characters Required");

                    } else {
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.light_gray));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Field Required");
                    }
                } else {
                    focusedID = zipcodeET.getId();
                    zipcodeET.setHint("Zipcode");
                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_green));
                }
            }
        });
    }

    public void enableOrDisableSave() {
        try {
            if (isAddress1 && isCity && isZipcode) {
                isSaveEnabled = true;
                Addbenifitialowner2CloseCV.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
            } else {
                isSaveEnabled = false;
                Addbenifitialowner2CloseCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isSaveEnabled) {
            Addbenifitialowner2CloseCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddBenifitialOwnerActivity2.this, AdditionalBeneficialOwnersActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            Addbenifitialowner2CloseCV.setClickable(false);
        }
    }

}























