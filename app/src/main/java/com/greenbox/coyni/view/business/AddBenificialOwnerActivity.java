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
import com.greenbox.coyni.view.BaseActivity;

public class AddBenificialOwnerActivity extends BaseActivity {

    TextInputLayout fnametil,lnametil,dobtil,ssntil,ownershiptil;
    TextInputEditText fnameET,lnameET,dobET,ssnET,ownershipET;
    LinearLayout fnameLL,lnameLL,dobLL,ssnLL,ownershipLL;
    TextView fnameTV,lnameTV,dobTV,ssnTV,ownershipTV;
    public CardView nextcv;
    ImageView closeIV;
    public boolean isfname=false,islname=false, isdob=false, isssn=false, isownership=false,isNextEnabled=false;
    public static int focusedID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_benificial_owner);
        initfields();
        textWatchers();
        focusWatchers();


        closeIV = findViewById(R.id.closeIV);
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBenificialOwnerActivity.this, BusinessRegistrationTrackerActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onResume() {
        super.onResume();

        if (fnameET.getId() == focusedID) {
            fnameET.requestFocus();
        } else if (lnameET.getId() == focusedID) {
            lnameET.requestFocus();
        } else if (ssnET.getId() == focusedID) {
            ssnET.requestFocus();
        } else if (ownershipET.getId() == focusedID) {
            ownershipET.requestFocus();
        } else {
            fnameET.requestFocus();
        }
        Log.e("ID", "" + focusedID);
    }

    private void initfields() {

        fnametil = findViewById(R.id.fnameTIL);
        lnametil = findViewById(R.id.lnameTIL);
        dobtil = findViewById(R.id.dobTIL);
        ssntil = findViewById(R.id.ssnTIL);
        ownershiptil = findViewById(R.id.ownershipTIL);

        fnameET = findViewById(R.id.fnameET);
        lnameET = findViewById(R.id.lnameET);
        dobET = findViewById(R.id.dobET);
        ssnET = findViewById(R.id.ssnET);
        ownershipET = findViewById(R.id.ownershipET);

        fnameLL = findViewById(R.id.fnameErrorLL);
        lnameLL = findViewById(R.id.lnameErrorLL);
        dobLL = findViewById(R.id.dobErrorLL);
        ssnLL = findViewById(R.id.ssnErrorLL);
        ownershipLL = findViewById(R.id.ownershipErrorLL);

        fnameTV = findViewById(R.id.fnameErrorTV);
        lnameTV = findViewById(R.id.lnameErrorTV);
        dobTV = findViewById(R.id.dobErrorTV);
        ssnTV = findViewById(R.id.ssnErrorTV);
        ownershipTV = findViewById(R.id.ownershipErrorTV);

        nextcv = findViewById(R.id.nextcv);
    }


    private void focusWatchers() {

        fnameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    fnameET.setHint("");
                    if (fnameET.getText().toString().trim().length() > 0) {
                        fnameLL.setVisibility(GONE);
                        fnametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(fnametil, getColor(R.color.primary_black));
                    }
                    else if (fnameET.getText().toString().trim().length() == 1) {
                        fnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(fnametil, getColor(R.color.error_red));
                        fnameLL.setVisibility(VISIBLE);
                        fnameTV.setText("Field Required Mininmum 2 characters");
                        }
                       else {
                        fnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(fnametil, getColor(R.color.light_gray));
                        fnameLL.setVisibility(VISIBLE);
                        fnameTV.setText("Field Required");
                    }
                } else {
                    fnameET.setHint("First Name");
                    fnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(fnametil, getColor(R.color.primary_green));
                    fnameLL.setVisibility(GONE);
                }
            }
        });
        lnameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    lnameET.setHint("");
                    if (lnameET.getText().toString().trim().length() > 0) {
                        lnameLL.setVisibility(GONE);
                        lnametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(lnametil, getColor(R.color.primary_black));
                    }
                    else if(lnameET.getText().toString().trim().length() == 1) {
                        lnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(lnametil, getColor(R.color.light_gray));
                        lnameLL.setVisibility(VISIBLE);
                        lnameTV.setText("Field Required Mininmum 2 characters");
                        }
                    else {
                        lnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(lnametil, getColor(R.color.light_gray));
                        lnameLL.setVisibility(VISIBLE);
                        lnameTV.setText("Field Required");
                    }
                } else {
                    lnameET.setHint("LastName");
                    lnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(lnametil, getColor(R.color.primary_green));
                    lnameLL.setVisibility(GONE);
                }
            }
        });
        ssnET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ssnET.setHint("");
                    if (ssnET.getText().toString().trim().length() > 0 && ssnET.getText().toString().trim().length() <=8) {
                        ssnLL.setVisibility(GONE);
                        ssntil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(ssntil, getColor(R.color.primary_black));

                    } else {
                        ssntil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(ssntil, getColor(R.color.light_gray));
                        ssnLL.setVisibility(VISIBLE);
                        ssnTV.setText("Field Required");
                    }
                } else {
                    ssnET.setHint("•••-••-••••");
                    ssntil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(ssntil, getColor(R.color.primary_green));
                    ssnLL.setVisibility(GONE);
                }
            }
        });
        ownershipET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ownershipET.setHint("");
                    if (ownershipET.getText().toString().trim().length() == 2) {
                        ownershipLL.setVisibility(GONE);
                        ownershiptil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(ownershiptil, getColor(R.color.primary_black));

                    } else {
                        ownershiptil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(ownershiptil, getColor(R.color.light_gray));
                        ownershipLL.setVisibility(VISIBLE);
                        ownershipTV.setText("Field Required");
                    }
                } else {
                    ownershipET.setHint("Ownership %");
                    ownershiptil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(ownershiptil, getColor(R.color.primary_green));
                    ownershipLL.setVisibility(GONE);
                }
            }
        });
    }

    private void textWatchers() {

        fnameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 2 ) {
                    isfname = true;
                    fnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(fnametil, getResources().getColor(R.color.primary_green));
                }
                else {
                    fnameTV.setText("Field Required");
                    isfname = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        lnameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    islname = true;
                    lnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(lnametil, getResources().getColor(R.color.primary_green));
                } else {
                    islname = false;
                    lnameLL.setVisibility(VISIBLE);
                    lnameTV.setText("Field Required");
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
        ssnET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    isssn = true;
                    ssntil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(ssntil, getResources().getColor(R.color.primary_green));
                } else {
                    ssnTV.setText("Field Required");
                    isssn = false;
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
        ownershipET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() == 2 ) {
                    isownership = true;
                    ownershipLL.setVisibility(GONE);
                    ownershiptil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(ownershiptil, getResources().getColor(R.color.primary_green));
                } else {
                    ownershipTV.setText("Field Required");
                    isownership = false;
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
            if (isfname && islname && isssn && isownership) {
                isNextEnabled = true;
                nextcv.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

                Log.e("All boolean", isfname + " " + islname + " " + isssn + " " + isownership);
            } else {

                Log.e("All boolean", isfname + " " + islname + " " + isssn + " " + isownership);

                isNextEnabled = false;
                nextcv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isNextEnabled == true) {
            nextcv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddBenificialOwnerActivity.this, BenificialMaillingAddressActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
