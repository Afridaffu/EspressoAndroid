package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.cards.CardResponseData;
import com.greenbox.coyni.utils.MaskEditText.widget.MaskEditText;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CardNumberEditText;
import com.greenbox.coyni.view.AddCardActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BusinessAddCardActivity extends AppCompatActivity {

    RelativeLayout layoutCard, layoutAddress;
    LinearLayout layoutClose, nameErrorLL, expiryErrorLL, layoutExpiry;
    LinearLayout address1ErrorLL, cityErrorLL, stateErrorLL, zipErrorLL;
    public LinearLayout cardErrorLL;
    View divider1, divider2;
    TextView tvCardHead, nameErrorTV, expiryErrorTV, address1ErrorTV, cityErrorTV, stateErrorTV, zipErrorTV;
    public TextView cardErrorTV;
    Boolean isName = false, isExpiry = false,  isNextEnabled = false;
    String strName = "", strCardNo = "", strExpiry = "",strAdd1 = "", strAdd2 = "", strCity = "", strState = "", strZip = "", strCountry = "";
    Boolean isAddress1 = false, isCity = false, isState = false, isZipcode = false, isAddEnabled = false;
    TextInputEditText etName, etCVV, etAddress1, etAddress2, etCity, etState, etZipCode, etCountry;
    CardNumberEditText etCardNumber;
    TextInputLayout etlState, etlName, etlExpiry, etlAddress1, etlAddress2, etlCity, etlZipCode;
    MaskEditText etExpiry;
    ConstraintLayout clStates;
    CardView cvNext, cvAddCard;
    Long mLastClickTime = 0L;
    Dialog preDialog, preAuthDialog;
    CardResponseData cardResponseData;
    ProgressDialog progressDialog;
//    public static AddCardActivity addCardActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_business_add_card);

        initialization();
        textWatchers();
        focusWatchers();
    }

    @Override
    public void onBackPressed() {
        try {
            if (layoutAddress.getVisibility() == View.VISIBLE) {
                layoutCard.setVisibility(View.VISIBLE);
                layoutAddress.setVisibility(View.GONE);
                divider1.setBackgroundResource(R.drawable.bg_core_colorfill);
                divider2.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                //etCVV.setText("");
            } else {
                super.onBackPressed();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void initialization() {

        layoutCard = findViewById(R.id.LayoutCard);
        layoutAddress = findViewById(R.id.LayoutAddress);
        layoutClose = findViewById(R.id.LayoutClose);
        nameErrorLL = findViewById(R.id.nameErrorLL);
        cardErrorLL = findViewById(R.id.CardErrorLL);
        expiryErrorLL = findViewById(R.id.ExpiryErrorLL);
        layoutExpiry = findViewById(R.id.LayoutExpiry);
        etlName = findViewById(R.id.EtlName);
        etlExpiry = findViewById(R.id.EtlExpiry);
        divider1 = findViewById(R.id.Divider1);
        divider2 = findViewById(R.id.Divider2);
        tvCardHead = findViewById(R.id.TvCardHead);
        nameErrorTV = findViewById(R.id.NameErrorTV);
        cardErrorTV = findViewById(R.id.CardErrorTV);
        expiryErrorTV = findViewById(R.id.ExpiryErrorTV);
        cvNext = findViewById(R.id.CvNext);
        cvAddCard = findViewById(R.id.CvAddCard);
        etName = findViewById(R.id.EtName);
        etCardNumber = findViewById(R.id.EtlCard);
        etExpiry = findViewById(R.id.EtExpiry);
        etAddress1 = findViewById(R.id.EtAddress1);
        etAddress2 = findViewById(R.id.EtAddress2);
        etCity = findViewById(R.id.EtCity);
        etState = findViewById(R.id.EtState);
        etZipCode = findViewById(R.id.EtZipCode);
        etCountry = findViewById(R.id.EtCountry);
        clStates = findViewById(R.id.ClStates);
        etlState = findViewById(R.id.EtlState);
        address1ErrorLL = findViewById(R.id.Address1ErrorLL);
        address1ErrorTV = findViewById(R.id.Address1ErrorTV);
        cityErrorLL = findViewById(R.id.CityErrorLL);
        cityErrorTV = findViewById(R.id.CityErrorTV);
        stateErrorLL = findViewById(R.id.StateErrorLL);
        stateErrorTV = findViewById(R.id.StateErrorTV);
        zipErrorLL = findViewById(R.id.ZipErrorLL);
        zipErrorTV = findViewById(R.id.ZipErrorTV);
        etlAddress1 = findViewById(R.id.EtlAddress1);
        etlAddress2 = findViewById(R.id.EtlAddress2);
        etlCity = findViewById(R.id.EtlCity);
        etlZipCode = findViewById(R.id.EtlZipCode);

        layoutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        cvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isNextEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        layoutCard.setVisibility(View.GONE);
                        layoutAddress.setVisibility(View.VISIBLE);
                        divider1.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_colorfill);
                        strName = etName.getText().toString().trim();
                        strCardNo = etCardNumber.getText().toString().trim().replace(" ", "");
                        strExpiry = etExpiry.getText().toString().trim();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        cvAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isAddEnabled) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        progressDialog = Utils.showProgressDialog(BusinessAddCardActivity.this);
                        strAdd1 = etAddress1.getText().toString().trim();
                        strAdd2 = etAddress2.getText().toString().trim();
                        strCity = etCity.getText().toString().trim();
                        strState = etState.getText().toString().trim();
                        strZip = etZipCode.getText().toString().trim();
                        strCountry = Utils.getStrCCode();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                prepareJson();
                            }
                        }, 100);
//                            prepareJson();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        divider1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (layoutAddress.getVisibility() == View.VISIBLE) {
                        layoutCard.setVisibility(View.VISIBLE);
                        layoutAddress.setVisibility(View.GONE);
                        divider1.setBackgroundResource(R.drawable.bg_core_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

//        etCardNumber.getCardReaderIVRef().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isLicense) {
//                    startScanning();
//                } else {
//                    Utils.hideKeypad(BusinessAddCardActivity.this, view);
//                    Utils.displayAlert("License has expired", BusinessAddCardActivity.this, "", "");
//                }
//            }
//        });

    }

    private Boolean validateExpiry() {
        Boolean value = true;
        try {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            String year = "";
            SimpleDateFormat ydf = new SimpleDateFormat("yy");
            year = ydf.format(Calendar.getInstance().getTime());
            if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) < Integer.parseInt(year)) {
                value = false;
            } else if (Integer.parseInt(etExpiry.getText().toString().split("/")[0]) == 0 || Integer.parseInt(etExpiry.getText().toString().split("/")[0]) > 12) {
                value = false;
            } else if (Integer.parseInt(etExpiry.getText().toString().split("/")[1]) <= Integer.parseInt(year) && Integer.parseInt(etExpiry.getText().toString().split("/")[0]) < month) {
                value = false;
            }
        } catch (Exception ex) {
            value = false;
            ex.printStackTrace();
        }
        return value;
    }

    private void focusWatchers() {

            etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            etName.setHint("");
                            if (etName.getText().toString().trim().length() > 1) {
                                isName = true;
                                nameErrorLL.setVisibility(GONE);
                                etlName.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
                            } else if (etName.getText().toString().trim().length() == 1) {
                                isName = false;
                                etlName.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlName, getColor(R.color.error_red));
                                nameErrorLL.setVisibility(VISIBLE);
                                nameErrorTV.setText("Minimum 2 Characters Required");
                            } else {
                                isName = false;
                                etlName.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                Utils.setUpperHintColor(etlName, getColor(R.color.error_red));
                                Utils.setUpperHintColor(etlName, getColor(R.color.light_gray));
                                nameErrorLL.setVisibility(VISIBLE);
                                nameErrorTV.setText("Field Required");
                            }

                            if (etName.getText().toString().length() > 0 && !etName.getText().toString().substring(0, 1).equals(" ")) {
                                etName.setText(etName.getText().toString().substring(0, 1).toUpperCase() + etName.getText().toString().substring(1));
                            }

                        } else {
                            etName.setHint("Name on Card");
                            etlName.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlName, getColor(R.color.primary_green));
                            InputMethodManager imm = (InputMethodManager) BusinessAddCardActivity.this.getSystemService(Service.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(etName, 0);
                        }
//                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etExpiry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            etExpiry.setHint("");
                            if (etExpiry.getText().toString().trim().length() > 0) {
                                if (validateExpiry()) {
                                    isExpiry = true;
                                    expiryErrorLL.setVisibility(GONE);
                                    etlExpiry.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                    Utils.setUpperHintColor(etlExpiry, getColor(R.color.primary_black));
                                } else {
                                    isExpiry = false;
                                    etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                    Utils.setUpperHintColor(etlExpiry, getColor(R.color.error_red));
                                    expiryErrorLL.setVisibility(VISIBLE);
                                    expiryErrorTV.setText("Please enter valid Expiry Date");
                                }
                            } else {
                                isExpiry = false;
                                etlExpiry.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                Utils.setUpperHintColor(etlExpiry, getColor(R.color.error_red));
                                Utils.setUpperHintColor(etlExpiry, getColor(R.color.light_gray));
                                expiryErrorLL.setVisibility(VISIBLE);
                                expiryErrorTV.setText("Field Required");
                            }
                        } else {
                            etExpiry.setHint("MM/YY");
                            etlExpiry.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlExpiry, getColor(R.color.primary_green));
                        }
//                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });



            etAddress1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            if (etAddress1.getText().toString().trim().length() > 0) {
                                address1ErrorLL.setVisibility(GONE);
                                etlAddress1.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_black));

                            } else {
                                etlAddress1.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlAddress1, getColor(R.color.error_red));
                                address1ErrorLL.setVisibility(VISIBLE);
                                address1ErrorTV.setText("Field Required");
                            }
                        } else {
                            etlAddress1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlAddress1, getColor(R.color.primary_green));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            if (etCity.getText().toString().trim().length() > 0) {
                                cityErrorLL.setVisibility(GONE);
                                etlCity.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlCity, getColor(R.color.primary_black));

                            } else {
                                etlCity.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlCity, getColor(R.color.error_red));
                                cityErrorLL.setVisibility(VISIBLE);
                                cityErrorTV.setText("Field Required");
                            }
                        } else {
                            etlCity.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlCity, getColor(R.color.primary_green));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etState.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            if (etState.getText().toString().trim().length() > 0) {
                                stateErrorLL.setVisibility(GONE);
                                etlState.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlState, getColor(R.color.primary_black));

                            } else {
                                etlState.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlState, getColor(R.color.error_red));
                                stateErrorLL.setVisibility(VISIBLE);
                                stateErrorTV.setText("Field Required");
                            }
                        } else {
                            etlState.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlState, getColor(R.color.primary_green));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            etZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try {
                        if (!b) {
                            if (etZipCode.getText().toString().trim().length() == 5) {
                                zipErrorLL.setVisibility(GONE);
                                etlZipCode.setBoxStrokeColorStateList(Utils.getNormalColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_black));

                            } else if (etZipCode.getText().toString().trim().length() > 0 && etZipCode.getText().toString().trim().length() < 5) {
                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
                                zipErrorLL.setVisibility(VISIBLE);
                                zipErrorTV.setText("Minimum 5 Characters Required");
                            } else if (etZipCode.getText().toString().trim().length() == 0) {
                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState());
                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
                                zipErrorLL.setVisibility(VISIBLE);
                                zipErrorTV.setText("Field Required");
                            }
//                            else {
//                                etlZipCode.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                                Utils.setUpperHintColor(etlZipCode, getColor(R.color.error_red));
//                                zipErrorLL.setVisibility(VISIBLE);
//                                zipErrorTV.setText("Zip Code must have at least 5 numbers");
//                                isZipcode = false;
//                            }
                        } else {
                            etlZipCode.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(etlZipCode, getColor(R.color.primary_green));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        }


    private void textWatchers() {

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (i2 > 2) {
                        if (charSequence != null && charSequence.length() < 31) {
                            isName = true;
                        }
                    }
                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 61) {
                        isName = true;
                        nameErrorLL.setVisibility(GONE);
                        etlName.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlName, getResources().getColor(R.color.primary_green));
                    } else {
                        isName = false;
                    }


                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!etName.hasFocus() && etName.getText().toString().trim().length() > 1) {
                        etlName.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(etlName, getColor(R.color.primary_black));
                    }
                    String str = etName.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        etName.setText("");
                        etName.setSelection(etName.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etName.setText(etName.getText().toString().replaceAll("\\.", ""));
                        etName.setSelection(etName.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etName.setText("");
                        etName.setSelection(etName.getText().length());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etExpiry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 6 && validateExpiry()) {
                        isExpiry = true;
                        expiryErrorLL.setVisibility(GONE);
                        etlExpiry.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlExpiry, getResources().getColor(R.color.primary_green));
                    } else {
                        isExpiry = false;
                        if (charSequence.toString().trim().length() == 0) {
                            expiryErrorLL.setVisibility(GONE);
                            etlExpiry.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(etlExpiry, getResources().getColor(R.color.light_gray));
                        }
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etExpiry.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etExpiry.setText("");
                        etExpiry.setSelection(etExpiry.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etExpiry.setText(etExpiry.getText().toString().replaceAll("\\.", ""));
                        etExpiry.setSelection(etExpiry.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etExpiry.setText("");
                        etExpiry.setSelection(etExpiry.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        etAddress1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 101) {
                        isAddress1 = true;
                        address1ErrorLL.setVisibility(GONE);
                        etlAddress1.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlAddress1, getResources().getColor(R.color.primary_green));
                    } else {
                        isAddress1 = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etAddress1.getText().toString();
                    if (str.substring(0).equals(" ")) {
                        etAddress1.setText("");
                        etAddress1.setSelection(etAddress1.getText().length());
                        address1ErrorLL.setVisibility(GONE);
                    } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etAddress1.setText("");
                        etAddress1.setSelection(etAddress1.getText().length());
                        address1ErrorLL.setVisibility(GONE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 51) {
                        isCity = true;
                        cityErrorLL.setVisibility(GONE);
                        etlCity.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlCity, getResources().getColor(R.color.primary_green));
                    } else {
                        isCity = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etCity.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etCity.setText("");
                        etCity.setSelection(etCity.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etCity.setText(etCity.getText().toString().replaceAll("\\.", ""));
                        etCity.setSelection(etCity.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etCity.setText("");
                        etCity.setSelection(etCity.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        etState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0) {
                        isState = true;
                        stateErrorLL.setVisibility(GONE);
                    } else {
                        isState = false;
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etZipCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() == 5) {
                        isZipcode = true;
                        zipErrorLL.setVisibility(GONE);
                        etlZipCode.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlZipCode, getResources().getColor(R.color.primary_green));
                    } else {
                        isZipcode = false;
                        zipErrorLL.setVisibility(GONE);
                        etlZipCode.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(etlZipCode, getResources().getColor(R.color.primary_green));
//                        zipErrorLL.setVisibility(VISIBLE);
//                        zipErrorTV.setText("Zip Code must have at least 5 numbers");
                    }
                    enableOrDisableNext();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = etZipCode.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        etZipCode.setText("");
                        etZipCode.setSelection(etCity.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        etZipCode.setText(etZipCode.getText().toString().replaceAll("\\.", ""));
                        etZipCode.setSelection(etZipCode.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        etZipCode.setText("");
                        etZipCode.setSelection(etZipCode.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void enableOrDisableNext() {
        try {
            if (isName && isExpiry) {
                isNextEnabled = true;
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isNextEnabled = false;
                cvNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
            if (isAddress1 && isCity && isZipcode && isState) {
                isAddEnabled = true;
                cvAddCard.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isAddEnabled = false;
                cvAddCard.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}