package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraFragment;
import com.greenbox.coyni.intro_slider.AutoScrollViewPager;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AddCardActivity;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.CreateAccountActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddBeneficialOwnerActivity extends BaseActivity {

    TextInputLayout fnametil, lnametil, dobtil, ssntil, ownershiptil,address1TIL, address2TIL, cityTIL, stateTIL, zipcodeTIL, countryTIL;
    TextInputEditText fnameET, lnameET, dobET, ssnET, ownershipET,address1ET, address2ET, cityET, stateET, zipcodeET;
    LinearLayout fnameLL, lnameLL, dobLL, ssnLL, ownershipLL, uploadLL, address1ErrorLL, address2ErrorLL, cityErrorLL, zipcodeErrorLL;
    ConstraintLayout stateCL;
    TextView uploadTV, fnameTV, lnameTV, dobTV, ssnTV, ownershipTV, address1ErrorTV, address2ErrorTV, cityErrorTV, zipcodeErrorTV;
    public CardView nextcv, Addbenifitialowner2CloseCV;
    ImageView closeIV, CompleteIV, uploadIV, backIV, stateimg;
    View divider1, divider2;
    public boolean isfname = false, islname = false, isssn = false, isownership = false, isNextEnabled = false, isDOBSelected = false, isAddress1, isAddress2, isCity, isState, isZipcode, isSaveEnabled;

    public static String dateOfBirth = "";
    int mYear, mMonth, mDay;
    private DatePicker datepicker;
    Long mLastClickTime = 0L;
    IdentityPagerAdapter identityPagerAdapter;
    static AutoScrollViewPager viewPager;
    int pagerPosition = 0, diffMonths = -1;
    public static int focusedID = 0;
    RelativeLayout layoutUpload,layoutMailingAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_add_benificial_owner);

        identityPagerAdapter = new IdentityPagerAdapter();
            viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(identityPagerAdapter);
            viewPager.setPagingEnabled(false);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.e("onPageScrolled", "onPageScrolled " + position);

                }

                @Override
                public void onPageSelected(int position) {
                    pagerPosition = position;
                    if (position == 0) {
                        divider1.setBackgroundResource(R.drawable.bg_core_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                    } else if (position == 1) {
                        divider1.setBackgroundResource(R.drawable.bg_core_new_4r_colorfill);
                        divider2.setBackgroundResource(R.drawable.bg_core_colorfill);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        stateimg = findViewById(R.id.Stateimg);
        stateimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog((AddBeneficialOwnerActivity.this));
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
        initFields();
        textWatchers();
        focusWatchers();

        uploadLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout DrivingLicence;
                Dialog dialog = new Dialog((AddBeneficialOwnerActivity.this));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                dialog.setContentView(R.layout.activity_choose_id_dialog);
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
                DrivingLicence = dialog.findViewById(R.id.payRequestLL);
                DrivingLicence.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            startActivity(new Intent(AddBeneficialOwnerActivity.this, CameraFragment.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                dialog.show();
            }
        });

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fnameET.getId() == focusedID) {
            fnameET.requestFocus();
        }
        else if(address1ET.getId()==focusedID){
            address1ET.requestFocus();
        }
    }

    private void initFields() {
        divider1 = findViewById(R.id.divider1);
        divider2 = findViewById(R.id.divider2);
        layoutUpload=findViewById(R.id.layoutUpload);
        layoutMailingAddress=findViewById(R.id.layoutMailingAddress);
        closeIV = findViewById(R.id.addbenifitialownerCloseIV);
        uploadLL = findViewById(R.id.UploadLL);
        fnametil = findViewById(R.id.FirstNameTIL);
        lnametil = findViewById(R.id.LastNameTIL);
        dobtil = findViewById(R.id.IdveriDOBTIL);
        ssntil = findViewById(R.id.SsnTIL);
        ownershiptil = findViewById(R.id.OwnershipTIL);

        fnameET = findViewById(R.id.FirstNameET);
        lnameET = findViewById(R.id.LastNameET);
        dobET = findViewById(R.id.IdveriDOBET);
        ssnET = findViewById(R.id.SsnET);
        ownershipET = findViewById(R.id.OwnershipET);

        fnameLL = findViewById(R.id.FirstNameErrorLL);
        lnameLL = findViewById(R.id.LastNameErrorLL);
        dobLL = findViewById(R.id.IdveriDOBLL);
        ssnLL = findViewById(R.id.SsnErrorLL);
        ownershipLL = findViewById(R.id.OwnershipErrorLL);
        uploadTV = findViewById(R.id.UploadTV);
        fnameTV = findViewById(R.id.FirstNameErrorTV);
        lnameTV = findViewById(R.id.LastNameErrorTV);
        dobTV = findViewById(R.id.IdveriDOBTV);
        ssnTV = findViewById(R.id.SsnErrorTV);
        ownershipTV = findViewById(R.id.OwnershipErrorTV);
        CompleteIV = findViewById(R.id.complete);
        nextcv = findViewById(R.id.nextcv);

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
//        backIV = findViewById(R.id.backIV);

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
        dobtil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ssnET.clearFocus();
                setToDate(dobET);
            }
        });

        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ssnET.clearFocus();
                setToDate(dobET);
            }
        });

        dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ssnET.clearFocus();
                    setToDate(dobET);
                }
            }
        });

//        backIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

    }

    public static String convertDate(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("dd/MM/yyyy");
            Date newDate = spf.parse(date);
            spf = new SimpleDateFormat("MMMM dd, yyyy");
            strDate = spf.format(newDate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return strDate;
    }

    private void setToDate(EditText dob) {
        try {

            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CalendarDialogTheme,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            try {
                                String dateToConvert = Utils.changeFormat(dayOfMonth) + "/" + Utils.changeFormat((monthOfYear + 1)) + "/" + year;
                                String convertedDate = convertDate(dateToConvert);
                                dob.setText(convertedDate);
                                isDOBSelected = true;
                                dateOfBirth = year + "-" + Utils.changeFormat((monthOfYear + 1)) + "-" + Utils.changeFormat(dayOfMonth);
                                enableOrDisableNext();
                                ssnET.clearFocus();
                                datepicker = new DatePicker(AddBeneficialOwnerActivity.this);
                                datepicker.init(year, monthOfYear + 1, dayOfMonth, null);
                                Utils.setUpperHintColor(dobtil, getResources().getColor(R.color.primary_black));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, mYear, mMonth, mDay);

            long years = 568025136000L;
            long yearsback = c.getTimeInMillis() - years;
            datePickerDialog.getDatePicker().setMaxDate(yearsback);
            if (datepicker != null) {
                datePickerDialog.updateDate(datepicker.getYear(), datepicker.getMonth() - 1, datepicker.getDayOfMonth());
            }
            datePickerDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void textWatchers() {
// AddBeneficialOwners Basic Info
        fnameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                    isfname = true;
                    fnameLL.setVisibility(GONE);
                    fnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(fnametil, getResources().getColor(R.color.primary_green));
                } else if (fnameET.getText().toString().trim().length() == 0) {
                    fnameLL.setVisibility(VISIBLE);
                    fnameTV.setText("Field Required");
                } else {
                    isfname = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = fnameET.getText().toString();
                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                        fnameET.setText("");
                        fnameET.setSelection(fnameET.getText().length());
                    } else if (str.length() > 0 && str.contains(".")) {
                        fnameET.setText(fnameET.getText().toString().replaceAll("\\.", ""));
                        fnameET.setSelection(fnameET.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        fnameET.setText("");
                        fnameET.setSelection(fnameET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
                    String str = lnameET.getText().toString();
                    if (str.length() > 0 && str.substring(0).equals(" ")) {
                        lnameET.setText(lnameET.getText().toString().replaceAll(" ", ""));
                        lnameET.setSelection(lnameET.getText().length());
                    } else if (str.length() > 0 && str.substring(str.length() - 1).equals(".")) {
                        lnameET.setText(lnameET.getText().toString().replaceAll(".", ""));
                        lnameET.setSelection(lnameET.getText().length());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        dobET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.toString().trim().length() > 0) {
                        isDOBSelected = true;
                    } else {
                        isDOBSelected = false;
                    }
                    enableOrDisableNext();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

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
                if (charSequence.toString().trim().length() == 2) {
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
                    if(s.length()==2){
                        s.append("%");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
// AddBeneficialOwners Address
        address1ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    isAddress1 = true;
                    address1ErrorLL.setVisibility(GONE);
                    address1TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address1TIL, getResources().getColor(R.color.primary_green));
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
                    cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(cityTIL, getResources().getColor(R.color.primary_green));
                    cityErrorLL.setVisibility(GONE);
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
                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_green));
                } else if (charSequence.length() > 0 && charSequence.length() < 5) {
                    isZipcode = false;
                    zipcodeErrorLL.setVisibility(GONE);
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
        fnameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    fnameET.setHint("");
                    if (fnameET.getText().toString().trim().length() > 1 ) {
                        fnameLL.setVisibility(GONE);
                        fnametil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(fnametil, getColor(R.color.primary_black));
                    } else if (fnameET.getText().toString().trim().length() == 1) {
                        fnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(fnametil, getColor(R.color.light_gray));
                        fnameLL.setVisibility(VISIBLE);
                        fnameTV.setText("Field Required Mininmum 2 characters");

                    } else {
                        fnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(fnametil, getColor(R.color.light_gray));
                        fnameLL.setVisibility(VISIBLE);
                        fnameTV.setText("Field Required");
                    }
                } else {
                    focusedID = fnameET.getId();
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
                    if (lnameET.getText().toString().trim().length() > 1) {
                        lnameLL.setVisibility(GONE);
                        fnametil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(lnametil, getColor(R.color.primary_black));
                    }else if (lnameET.getText().toString().trim().length() == 1) {
                        lnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(lnametil, getColor(R.color.light_gray));
                        lnameLL.setVisibility(VISIBLE);
                        lnameTV.setText("Field Required Mininmum 2 characters");
                    }else {
                        lnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(lnametil, getColor(R.color.light_gray));
                        lnameLL.setVisibility(VISIBLE);
                        lnameTV.setText("Field Required");
                    }
                } else {
                    focusedID = lnameET.getId();
                    lnameET.setHint("LastName");
                    lnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(lnametil, getColor(R.color.primary_green));
                    lnameLL.setVisibility(GONE);
                }
            }
        });
        dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(AddBeneficialOwnerActivity.this);
            }
        });
        ssnET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ssnET.setHint("");
                    if (ssnET.getText().toString().trim().length() > 0 && ssnET.getText().toString().trim().length() <= 8) {
                        ssnLL.setVisibility(GONE);
                        ssntil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(ssntil, getColor(R.color.primary_black));

                    } else {
                        ssntil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
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
                        ownershiptil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(ownershiptil, getColor(R.color.primary_black));

                    } else {
                        ownershiptil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
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

        address1ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    address1ET.setHint("");
                    if (address1ET.getText().toString().trim().length() > 0) {
                        address1ErrorLL.setVisibility(GONE);
                        address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(address1TIL, getColor(R.color.primary_black));

                    } else {
                        address1TIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(address1TIL, getColor(R.color.light_gray));
                        address1ErrorLL.setVisibility(VISIBLE);
                        address1ErrorTV.setText("Field Required");
                    }
                } else {
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
//                        address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
//                        Utils.setUpperHintColor(address2TIL, getColor(R.color.primary_black));
                    } else {
//                        address2TIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
//                        Utils.setUpperHintColor(address2TIL, getColor(R.color.light_gray));
                    }
                } else {
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
                        cityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_black));

                    } else {
                        cityTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.light_gray));
                        cityErrorLL.setVisibility(VISIBLE);
                        cityErrorTV.setText("Field Required");
                    }
                } else {
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
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_black));
                    } else if (zipcodeET.getText().toString().trim().length() < 5 && zipcodeET.getText().toString().trim().length() > 0) {
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.error_red));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Minimum 5 Characters Required");

                    } else {
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.light_gray));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Field Required");
                    }
                } else {
                    zipcodeET.setHint("Zipcode");
                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_green));
                }
            }
        });

    }

    private void enableOrDisableNext() {

        try {
            if (isfname && islname && isDOBSelected && isssn && isownership) {
                isNextEnabled = true;
//                     CompleteIV.setVisibility(VISIBLE);
                nextcv.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                Log.e("All boolean", isfname + " " + islname + " " + isDOBSelected + " " + isssn + " " + isownership);
            } else {

                Log.e("All boolean", isfname + " " + islname + " " + isDOBSelected + " " + isssn + " " + isownership);

                isNextEnabled = false;
                nextcv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isNextEnabled) {
            nextcv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddBeneficialOwnerActivity.this, AddBeneficialOwnerActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            nextcv.setClickable(false);
        }
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
                    Intent intent = new Intent(AddBeneficialOwnerActivity.this, AdditionalBeneficialOwnersActivity.class);
                    startActivity(intent);
                }
            });
        }
        else {
            Addbenifitialowner2CloseCV.setClickable(false);
        }
    }

    class IdentityPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layoutUpload;
                    break;
                case 1:
                    resId = R.id.layoutMailingAddress;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // No super
        }
    }
}



