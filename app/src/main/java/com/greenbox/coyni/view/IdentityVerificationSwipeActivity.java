package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.AutoScrollPagerAdapter;
import com.greenbox.coyni.custom_camera.CameraActivity;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.intro_slider.AutoScrollViewPager;
import com.greenbox.coyni.model.States;
import com.greenbox.coyni.model.identity_verification.AddressObj;
import com.greenbox.coyni.model.identity_verification.IdentityAddressRequest;
import com.greenbox.coyni.model.identity_verification.IdentityAddressResponse;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.PhotoIDEntityObject;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.OnSwipeTouchListener;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class IdentityVerificationSwipeActivity extends AppCompatActivity implements OnKeyboardVisibilityListener {
    TextInputLayout dobTIL, ssnTIL, mailingAddTIL, mailingAddlineoptTIL, cityTIL, stateTIL, zipcodeTIL, countryTIL;
    TextInputEditText dobET, ssnET, cityET, mailAddr1, mailAddr2, state, zipcode;
    TextView idveriUItext, idveriUItextSuc, exitBtn, btnExit, ssnErrorTV;
    ConstraintLayout idveriDOBConLayout, stateCL;
    LinearLayout bottomSheet, fileSelectedLL, ssnErrorLL, swipeLL, bottomNaviLL;
    ScrollView firstIVeri;
    public static CardView btnNext, btnSubmit;
    ScrollView secondIVeri;
    View viewLeft, viewRight;
    ImageButton closebtn, backbtn;
    ImageView upIdSuccessImg;
    int mYear, mMonth, mDay;
    public static boolean isMailAddr1 = false, isCity = false, isState = false, isZip = false, isSubmit = false, isNext = false;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    public static File identityFile;
    public static String dateOfBirth = "";
    public static int identityType = 0;
    public static boolean isFileSelected = false, isSSNSelected = false, isDOBSelected = false;

    public static IdentityVerificationSwipeActivity identityVerificationActivity;
    IdentityVerificationViewModel identityVerificationViewModel;
    ProgressDialog dialog;

    LinearLayout address1ErrorLL, address2ErrorLL, cityErrorLL, zipcodeErrorLL;
    TextView address1ErrorTV, address2ErrorTV, cityErrorTV, zipcodeErrorTV;
    MyApplication myApplicationObj;
    Long mLastClickTime = 0L;
    DashboardViewModel dashboardViewModel;
    private DatePicker datepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_identity_swipe);

            AutoScrollPagerAdapter autoScrollPagerAdapter =
                    new AutoScrollPagerAdapter(getSupportFragmentManager());
            AutoScrollViewPager viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(autoScrollPagerAdapter);
            TabLayout tabs = findViewById(R.id.tabs);
            tabs.setupWithViewPager(viewPager);
//            viewPager.setStopScrollWhenTouch(true);


            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            identityVerificationActivity = this;
            myApplicationObj = (MyApplication) getApplicationContext();
            dashboardViewModel.meProfile();
//            setStates();
            initFields();
            initObservers();
//            swipeListeners();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {

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
                                enableNext();
                                ssnET.clearFocus();
                                datepicker = new DatePicker(IdentityVerificationSwipeActivity.this);
                                datepicker.init(year, monthOfYear + 1, dayOfMonth, null);
                                Utils.setUpperHintColor(dobTIL, getResources().getColor(R.color.primary_black));
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

    private void TextWatchers() {
        try {
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
                        enableNext();
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
                    try {
                        if (charSequence.toString().trim().length() > 0 && charSequence.toString().trim().length() < 4) {
                            isSSNSelected = false;
                            Utils.setUpperHintColor(ssnTIL, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.toString().trim().length() == 4) {
                            isSSNSelected = true;
                            ssnErrorLL.setVisibility(GONE);
                            //                        ssnTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(ssnTIL, getResources().getColor(R.color.primary_black));
                        } else {
                            isSSNSelected = false;
                        }
                        enableNext();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            mailAddr1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() > 0) {
                            isMailAddr1 = true;
                            address1ErrorLL.setVisibility(GONE);
                            //                        mailingAddTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(mailingAddTIL, getResources().getColor(R.color.primary_black));
                        } else {
                            address1ErrorLL.setVisibility(VISIBLE);
                            address1ErrorTV.setText("Field Required");
                            isMailAddr1 = false;
                        }
                        enableORdiableSubmit();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = mailAddr1.getText().toString();
                        if (str.substring(0).equals(" ")) {
                            mailAddr1.setText("");
                            mailAddr1.setSelection(mailAddr1.getText().length());
                            address1ErrorLL.setVisibility(GONE);
                        } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                            mailAddr1.setText("");
                            mailAddr1.setSelection(mailAddr1.getText().length());
                            address1ErrorLL.setVisibility(GONE);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            mailAddr2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() > 0) {

                            Utils.setUpperHintColor(mailingAddlineoptTIL, getResources().getColor(R.color.primary_black));
                        }
//                else{
//                    address2ErrorLL.setVisibility(VISIBLE);
//                    address2ErrorTV.setText("Field Required");
//                    isAddress2 = false;
//                }
                        enableORdiableSubmit();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = mailAddr2.getText().toString();
                        if (str.substring(0).equals(" ")) {
                            mailAddr2.setText("");
                            mailAddr2.setSelection(mailAddr2.getText().length());
                            address2ErrorLL.setVisibility(GONE);
                        } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                            mailAddr2.setText("");
                            mailAddr2.setSelection(mailAddr2.getText().length());
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
                    try {
                        if (charSequence.length() > 0) {
                            isCity = true;
                            cityErrorLL.setVisibility(GONE);
                            //                        cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(cityTIL, getResources().getColor(R.color.primary_black));
                        } else {
                            cityErrorLL.setVisibility(VISIBLE);
                            cityErrorTV.setText("Field Required");
                            isCity = false;
                        }
                        enableORdiableSubmit();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = cityET.getText().toString();
                        if (str.substring(0).equals(" ")) {
                            cityET.setText("");
                            cityET.setSelection(cityET.getText().length());
                            cityErrorLL.setVisibility(GONE);
                        } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                            cityET.setText("");
                            cityET.setSelection(cityET.getText().length());
                            cityErrorLL.setVisibility(GONE);
                        } else if (str.length() > 0 && str.substring(str.length() - 1).equals(".")) {
                            cityET.setText(cityET.getText().toString().replaceAll(".", ""));
                            cityET.setSelection(cityET.getText().length());
                            cityErrorLL.setVisibility(GONE);
                        } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                            cityET.setText("");
                            cityET.setSelection(cityET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            state.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() > 0) {
                            Utils.setUpperHintColor(stateTIL, getResources().getColor(R.color.primary_black));
                            stateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            isState = true;
                        } else {
                            isState = false;
                        }
                        enableORdiableSubmit();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            zipcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() == 5) {
                            isZip = true;
                            zipcodeErrorLL.setVisibility(GONE);
                            //                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.length() < 5 && charSequence.length() > 0) {
                            isZip = false;
                            zipcodeErrorLL.setVisibility(GONE);
                            //                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.length() == 0) {
                            isZip = false;
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Field Required");
                        }
                        enableORdiableSubmit();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public void focusWatchers() {

        try {
            mailAddr1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        mailAddr1.setHint("");
                        if (mailAddr1.getText().toString().trim().length() > 0) {
                            address1ErrorLL.setVisibility(GONE);
                            mailingAddTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(mailingAddTIL, getColor(R.color.primary_black));

                        } else {
                            mailingAddTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(mailingAddTIL, getColor(R.color.light_gray));
                            address1ErrorLL.setVisibility(VISIBLE);
                            address1ErrorTV.setText("Field Required");
                        }
                    } else {
                        mailAddr1.setHint("Street Address");
                        mailingAddTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(mailingAddTIL, getColor(R.color.primary_green));
                    }
                }
            });
            mailAddr2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        mailAddr2.setHint("");
                        if (mailAddr2.getText().toString().trim().length() > 0) {
                            Utils.setUpperHintColor(mailingAddlineoptTIL, getColor(R.color.primary_black));
                            mailingAddlineoptTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        } else {
                            Utils.setUpperHintColor(mailingAddlineoptTIL, getColor(R.color.light_gray));
                            mailingAddlineoptTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());

                        }
                    } else {
                        mailingAddlineoptTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(mailingAddlineoptTIL, getColor(R.color.primary_green));
                        mailAddr2.setHint("Apt#, Suit, Floor");
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
                        cityET.setHint("City");
                        cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_green));
                    }
                }
            });


            zipcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        zipcode.setHint("");
                        if (zipcode.getText().toString().trim().length() == 5) {
                            zipcodeErrorLL.setVisibility(GONE);
                            zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_black));

                        } else if (zipcode.getText().toString().trim().length() < 5 && zipcode.getText().toString().trim().length() > 0) {
                            zipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.error_red));
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Minimum 5 Characters Required");

                        } else {
                            zipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.light_gray));
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Field Required");
                        }
                    } else {
                        zipcode.setHint("Zip Code");
                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_green));
                    }
                }
            });

            dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(IdentityVerificationSwipeActivity.this);
                }
            });

            ssnET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(IdentityVerificationSwipeActivity.this);
                        if (ssnET.getText().toString().trim().length() == 4) {
                            ssnErrorLL.setVisibility(GONE);
                            ssnTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.primary_black));

                        } else if (ssnET.getText().toString().trim().length() > 0 && ssnET.getText().toString().trim().length() < 4) {
                            ssnTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.error_red));
                            ssnErrorLL.setVisibility(VISIBLE);
                            ssnErrorTV.setText("Minimum 4 Characters Required");

                        } else {
                            ssnTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.error_red));
                            ssnErrorLL.setVisibility(VISIBLE);
                            ssnErrorTV.setText("Field Required");
                        }
                    } else {
                        ssnTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(ssnTIL, getColor(R.color.primary_green));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void enableORdiableSubmit() {
        try {
            if (isMailAddr1 && isCity && isState && isZip) {
                isSubmit = true;
                btnSubmit.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

            } else {
                isSubmit = false;
                btnSubmit.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initFields() {
        try {
            setKeyboardVisibilityListener(IdentityVerificationSwipeActivity.this);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);

            btnSubmit = findViewById(R.id.submitBtn);
            bottomSheet = findViewById(R.id.clickBottomSheet);
            idveriUItext = findViewById(R.id.idveriUpIdTxt);
            idveriUItextSuc = findViewById(R.id.idveriUpIdSuccessTxt);
            exitBtn = findViewById(R.id.exitBtn);
            btnExit = findViewById(R.id.btnExit);
            btnNext = findViewById(R.id.nextBtn);
            firstIVeri = findViewById(R.id.linearLayoutIdVe);
            secondIVeri = findViewById(R.id.scrlViewIV2nd);
            viewLeft = findViewById(R.id.viewBarLeft);
            viewRight = findViewById(R.id.viewBarRight);
            closebtn = findViewById(R.id.closeBtn);
            backbtn = findViewById(R.id.backBtn);
            dobET = findViewById(R.id.idveriDOBET);
            dobTIL = findViewById(R.id.idveriDOBTIL);
            upIdSuccessImg = findViewById(R.id.idveriUpIdSuccessImg);
            dobET.setInputType(InputType.TYPE_NULL);
            idveriDOBConLayout = findViewById(R.id.idveriDOBCL);
            ssnTIL = findViewById(R.id.idveriSSNTIL);
            ssnET = findViewById(R.id.idVeriSSNET);

            mailAddr1 = findViewById(R.id.mailingAddET);
            mailAddr2 = findViewById(R.id.mailingAddlineoptET);
            cityET = findViewById(R.id.cityET);
            state = findViewById(R.id.stateET);
            stateCL = findViewById(R.id.stateCL);
            zipcode = findViewById(R.id.zipcodeET);
            fileSelectedLL = findViewById(R.id.fileSelectedLL);
            ssnErrorLL = findViewById(R.id.ssnErrorLL);
            ssnErrorTV = findViewById(R.id.ssnErrorTV);

            mailingAddTIL = findViewById(R.id.mailingAddTIL);
            mailingAddlineoptTIL = findViewById(R.id.mailingAddlineoptTIL);
            cityTIL = findViewById(R.id.cityTIL);
            stateTIL = findViewById(R.id.stateTIL);
            zipcodeTIL = findViewById(R.id.zipcodeTIL);
            countryTIL = findViewById(R.id.countryTIL);

            address1ErrorLL = findViewById(R.id.address1ErrorLL);
            address1ErrorTV = findViewById(R.id.address1ErrorTV);

            address2ErrorLL = findViewById(R.id.address2ErrorLL);
            address2ErrorTV = findViewById(R.id.address2ErrorTV);

            cityErrorLL = findViewById(R.id.cityErrorLL);
            cityErrorTV = findViewById(R.id.cityErrorTV);

            zipcodeErrorLL = findViewById(R.id.zipcodeErrorLL);
            zipcodeErrorTV = findViewById(R.id.zipcodeErrorTV);

            bottomNaviLL = findViewById(R.id.bottomNaviLL);


            TextWatchers();

            focusWatchers();

            idveriDOBConLayout.setOnClickListener(new View.OnClickListener() {
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

            bottomSheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    ssnET.clearFocus();
                    showIdentityTypePopup(IdentityVerificationSwipeActivity.this);
                }
            });

            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firstIVeri.setVisibility(View.VISIBLE);
                    secondIVeri.setVisibility(View.GONE);
                    backbtn.setVisibility(View.GONE);
                    closebtn.setVisibility(View.VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background);
                    viewRight.setBackgroundResource(R.drawable.button_background1);
                    if (Utils.isKeyboardVisible) {
                        Utils.hideKeypad(IdentityVerificationSwipeActivity.this);
                    }
                }
            });

            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            exitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            viewLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firstIVeri.setVisibility(View.VISIBLE);
                    secondIVeri.setVisibility(View.GONE);
                    viewLeft.setBackgroundResource(R.drawable.button_background);
                    viewRight.setBackgroundResource(R.drawable.button_background1);
                    backbtn.setVisibility(View.GONE);
                    closebtn.setVisibility(View.VISIBLE);
                    if (Utils.isKeyboardVisible) {
                        Utils.hideKeypad(IdentityVerificationSwipeActivity.this);
                    }
                }

            });

            stateCL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateStates(this, state, myApplicationObj);
            });

            stateTIL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateStates(this, state, myApplicationObj);
            });

            state.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateStates(this, state, myApplicationObj);
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNext) {
//                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), identityFile);
//                        MultipartBody.Part idFile = MultipartBody.Part.createFormData("identityFile", identityFile.getName(), requestBody);
//                        RequestBody idType = RequestBody.create(MediaType.parse("text/plain"), identityType+"");
//                        RequestBody idNumber = RequestBody.create(MediaType.parse("text/plain"), ssnET.getText().toString());
//                        identityVerificationViewModel.uploadIdentityImage(idFile,idType,idNumber);
//
//                        firstIVeri.setVisibility(GONE);
//                        secondIVeri.setVisibility(VISIBLE);
//                        viewLeft.setBackgroundResource(R.drawable.button_background1);
//                        viewRight.setBackgroundResource(R.drawable.button_background);
//                        backbtn.setVisibility(VISIBLE);
//                        closebtn.setVisibility(GONE);
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        ssnET.clearFocus();
                        dialog = Utils.showProgressDialog(IdentityVerificationSwipeActivity.this);
                        identityVerificationViewModel.removeIdentityImage(identityType + "");
                    }
                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSubmit) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        dialog = Utils.showProgressDialog(IdentityVerificationSwipeActivity.this);
                        IdentityAddressRequest identityAddressRequest = new IdentityAddressRequest();
                        identityAddressRequest.setFirstName(myApplicationObj.getMyProfile().getData().getFirstName());
                        identityAddressRequest.setLastName(myApplicationObj.getMyProfile().getData().getLastName());
                        identityAddressRequest.setPhoneNumber(myApplicationObj.getMyProfile().getData().getPhoneNumber().split(" ")[1]);
                        identityAddressRequest.setEmail(myApplicationObj.getMyProfile().getData().getEmail());
                        identityAddressRequest.setDateOfBirth(dateOfBirth);
                        identityAddressRequest.setSsn(ssnET.getText().toString().trim());

                        AddressObj addressObj = new AddressObj();
                        addressObj.setAddressLine1(mailAddr1.getText().toString().trim());
                        addressObj.setAddressLine2(mailAddr2.getText().toString().trim());
                        addressObj.setAddressType(0);
                        addressObj.setCity(cityET.getText().toString().trim());
//                        addressObj.setState(state.getText().toString().trim());
                        addressObj.setState(Utils.tempStateName);
                        addressObj.setStateCode(Utils.tempStateCode);
                        addressObj.setCountry("us");
                        addressObj.setZipCode(zipcode.getText().toString().trim());

                        PhotoIDEntityObject photoIDEntityObject = new PhotoIDEntityObject();
                        photoIDEntityObject.setNumber(ssnET.getText().toString().trim());
                        photoIDEntityObject.setType(identityType);
//                        photoIDEntityObject.setIssuer(state.getText().toString().trim());
                        photoIDEntityObject.setIssuer(Utils.tempStateCode);

                        identityAddressRequest.setAddressObj(addressObj);
                        identityAddressRequest.setPhotoIDEntityObject(photoIDEntityObject);

                        identityVerificationViewModel.uploadIdentityAddress(identityAddressRequest);
                    }
                }
            });

            ssnTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            dobTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());

            mailingAddTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            mailingAddlineoptTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            cityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            stateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
            countryTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showIdentityTypePopup(final Context context) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.fragment_id_ve_bottom_sheet);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            LinearLayout driverlicensell = dialog.findViewById(R.id.driverlicensell);
            LinearLayout passportll = dialog.findViewById(R.id.passportll);
            LinearLayout sicardll = dialog.findViewById(R.id.sicardll);

            DisplayMetrics mertics = context.getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            Window window = dialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            driverlicensell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    //                context.startActivity(new Intent(context, CameraActivity.class));
                    if (checkAndRequestPermissions((Activity) context)) {
                        identityType = 0;
                        context.startActivity(new Intent(context, CameraActivity.class));
                    }
                }
            });

            passportll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (checkAndRequestPermissions((Activity) context)) {
                        identityType = 1;
                        context.startActivity(new Intent(context, CameraActivity.class));
                    }
                }
            });

            sicardll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (checkAndRequestPermissions((Activity) context)) {
                        identityType = 2;
                        context.startActivity(new Intent(context, CameraActivity.class));
                    }
                }
            });

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        try {
            int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.CAMERA);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA);
            }
            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                        .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(context, listPermissionsNeeded
                                .toArray(new String[listPermissionsNeeded.size()]),
                        REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode) {
                case REQUEST_ID_MULTIPLE_PERMISSIONS:
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Camera.", IdentityVerificationSwipeActivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", IdentityVerificationSwipeActivity.this, "", "");

                    } else {
                        startActivity(new Intent(this, CameraActivity.class));
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (isFileSelected) {
                idveriUItext.setVisibility(View.GONE);
                fileSelectedLL.setVisibility(View.VISIBLE);
                if (identityType == 0) {
                    idveriUItextSuc.setText("Uploaded Driver’s License");
                } else if (identityType == 1) {
                    idveriUItextSuc.setText("Uploaded Passport");
                } else if (identityType == 2) {
                    idveriUItextSuc.setText("Uploaded State-Issued Card");
                }
            } else {
                idveriUItext.setVisibility(View.VISIBLE);
                fileSelectedLL.setVisibility(View.GONE);
            }
            enableNext();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void enableNext() {
        try {
            if (isFileSelected && isSSNSelected && isDOBSelected) {
                isNext = true;
                btnNext.setCardBackgroundColor(identityVerificationActivity.getResources().getColor(R.color.primary_color));
            } else {
                isNext = false;
                btnNext.setCardBackgroundColor(identityVerificationActivity.getResources().getColor(R.color.inactive_color));
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void initObservers() {
        try {
            identityVerificationViewModel.getUploadIdentityImageResponse().observe(this, new Observer<IdentityImageResponse>() {
                @Override
                public void onChanged(IdentityImageResponse identityImageResponse) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        firstIVeri.setVisibility(GONE);
                        secondIVeri.setVisibility(VISIBLE);
                        viewLeft.setBackgroundResource(R.drawable.button_background1);
                        viewRight.setBackgroundResource(R.drawable.button_background);
                        backbtn.setVisibility(VISIBLE);
                        closebtn.setVisibility(GONE);
                        mailAddr1.requestFocus();
                        address1ErrorLL.setVisibility(GONE);
                        if (!Utils.isKeyboardVisible) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        }

                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), IdentityVerificationSwipeActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            identityVerificationViewModel.getRemoveIdentityImageResponse().observe(this, new Observer<RemoveIdentityResponse>() {
                @Override
                public void onChanged(RemoveIdentityResponse imageResponse) {
                    if (imageResponse != null) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), identityFile);
                        MultipartBody.Part idFile = MultipartBody.Part.createFormData("identityFile", identityFile.getName(), requestBody);
                        RequestBody idType = RequestBody.create(MediaType.parse("text/plain"), identityType + "");
                        RequestBody idNumber = RequestBody.create(MediaType.parse("text/plain"), ssnET.getText().toString().trim());
                        identityVerificationViewModel.uploadIdentityImage(idFile, idType, idNumber);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            identityVerificationViewModel.getUploadIdentityAddressResponse().observe(this, new Observer<IdentityAddressResponse>() {
                @Override
                public void onChanged(IdentityAddressResponse identityAddressResponse) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (identityAddressResponse.getStatus().equalsIgnoreCase("success")) {
                        String respCode = identityAddressResponse.getData().getGiactResponseName();
                        if (respCode.equalsIgnoreCase("ND02") || respCode.equalsIgnoreCase("CA11")
                                || respCode.equalsIgnoreCase("CI11") || respCode.equalsIgnoreCase("CA24")
                                || respCode.equalsIgnoreCase("CI24")) {
                            //Success
                            startActivity(new Intent(IdentityVerificationSwipeActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "SUCCESS"));
                        } else if (respCode.equalsIgnoreCase("CA22") || respCode.equalsIgnoreCase("CI22")) {
                            //SSN Error
                            startActivity(new Intent(IdentityVerificationSwipeActivity.this, IdVeAdditionalActionActivity.class));

                        } else if (respCode.equalsIgnoreCase("CA25") || respCode.equalsIgnoreCase("CI25")
                                || respCode.equalsIgnoreCase("CA21") || respCode.equalsIgnoreCase("CI21")
                                || respCode.equalsIgnoreCase("CA01") || respCode.equalsIgnoreCase("CI01")
                                || respCode.equalsIgnoreCase("CA30") || respCode.equalsIgnoreCase("CI30")
                                || respCode.equalsIgnoreCase("CA23") || respCode.equalsIgnoreCase("CI23")) {
                            //Under Review
                            startActivity(new Intent(IdentityVerificationSwipeActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "UNDER_REVIEW"));

                        } else {
                            //Failed
                            startActivity(new Intent(IdentityVerificationSwipeActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                    .putExtra("screen", "FAILED"));

                        }
                    } else {
                        Utils.displayAlert(identityAddressResponse.getError().getErrorDescription(), IdentityVerificationSwipeActivity.this, "", identityAddressResponse.getError().getFieldErrors().get(0));
                    }

                    identityVerificationViewModel.getStatusTracker();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            identityVerificationViewModel.getGetStatusTracker().observe(this, new Observer<TrackerResponse>() {
                @Override
                public void onChanged(TrackerResponse trackerResponse) {
                    if (trackerResponse != null && trackerResponse.getStatus().equalsIgnoreCase("success")) {
                        myApplicationObj.setTrackerResponse(trackerResponse);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dashboardViewModel.getProfileMutableLiveData().observe(this, new Observer<Profile>() {
                @Override
                public void onChanged(Profile profile) {
                    if (profile != null && profile.getStatus().equalsIgnoreCase("success")) {
                        myApplicationObj.setMyProfile(profile);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setStates() {
        String json = null;
        try {
            InputStream is = getAssets().open("states.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type type = new TypeToken<List<States>>() {
            }.getType();
            List<States> listStates = gson.fromJson(json, type);
            myApplicationObj.setListStates(listStates);
            Log.e("list states", listStates.size() + "");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void swipeListeners() {
        swipeLL.setOnTouchListener(new OnSwipeTouchListener(IdentityVerificationSwipeActivity.this) {

            public void onSwipeRight() {
                firstIVeri.setVisibility(View.VISIBLE);
                secondIVeri.setVisibility(View.GONE);
                viewLeft.setBackgroundResource(R.drawable.button_background);
                viewRight.setBackgroundResource(R.drawable.button_background1);
                backbtn.setVisibility(View.GONE);
                closebtn.setVisibility(View.VISIBLE);
            }

            public void onSwipeLeft() {
                if (isNext) {
                    firstIVeri.setVisibility(GONE);
                    secondIVeri.setVisibility(VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background1);
                    viewRight.setBackgroundResource(R.drawable.button_background);
                    backbtn.setVisibility(VISIBLE);
                    closebtn.setVisibility(GONE);
                }
            }


        });

        ssnTIL.setOnTouchListener(new OnSwipeTouchListener(IdentityVerificationSwipeActivity.this) {

            public void onSwipeRight() {
                firstIVeri.setVisibility(View.VISIBLE);
                secondIVeri.setVisibility(View.GONE);
                viewLeft.setBackgroundResource(R.drawable.button_background);
                viewRight.setBackgroundResource(R.drawable.button_background1);
                backbtn.setVisibility(View.GONE);
                closebtn.setVisibility(View.VISIBLE);
            }

            public void onSwipeLeft() {
                if (isNext) {
                    firstIVeri.setVisibility(GONE);
                    secondIVeri.setVisibility(VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background1);
                    viewRight.setBackgroundResource(R.drawable.button_background);
                    backbtn.setVisibility(VISIBLE);
                    closebtn.setVisibility(GONE);
                }
            }


        });

        dobTIL.setOnTouchListener(new OnSwipeTouchListener(IdentityVerificationSwipeActivity.this) {

            public void onSwipeRight() {
                firstIVeri.setVisibility(View.VISIBLE);
                secondIVeri.setVisibility(View.GONE);
                viewLeft.setBackgroundResource(R.drawable.button_background);
                viewRight.setBackgroundResource(R.drawable.button_background1);
                backbtn.setVisibility(View.GONE);
                closebtn.setVisibility(View.VISIBLE);
            }

            public void onSwipeLeft() {
                if (isNext) {
                    firstIVeri.setVisibility(GONE);
                    secondIVeri.setVisibility(VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background1);
                    viewRight.setBackgroundResource(R.drawable.button_background);
                    backbtn.setVisibility(VISIBLE);
                    closebtn.setVisibility(GONE);
                }
            }


        });

        bottomNaviLL.setOnTouchListener(new OnSwipeTouchListener(IdentityVerificationSwipeActivity.this) {

            public void onSwipeRight() {
                firstIVeri.setVisibility(View.VISIBLE);
                secondIVeri.setVisibility(View.GONE);
                viewLeft.setBackgroundResource(R.drawable.button_background);
                viewRight.setBackgroundResource(R.drawable.button_background1);
                backbtn.setVisibility(View.GONE);
                closebtn.setVisibility(View.VISIBLE);
            }

            public void onSwipeLeft() {
                if (isNext) {
                    firstIVeri.setVisibility(GONE);
                    secondIVeri.setVisibility(VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background1);
                    viewRight.setBackgroundResource(R.drawable.button_background);
                    backbtn.setVisibility(VISIBLE);
                    closebtn.setVisibility(GONE);
                }
            }


        });

        dobET.setOnTouchListener(new OnSwipeTouchListener(IdentityVerificationSwipeActivity.this) {

            public void onSwipeRight() {
                firstIVeri.setVisibility(View.VISIBLE);
                secondIVeri.setVisibility(View.GONE);
                viewLeft.setBackgroundResource(R.drawable.button_background);
                viewRight.setBackgroundResource(R.drawable.button_background1);
                backbtn.setVisibility(View.GONE);
                closebtn.setVisibility(View.VISIBLE);
            }

            public void onSwipeLeft() {
                if (isNext) {
                    firstIVeri.setVisibility(GONE);
                    secondIVeri.setVisibility(VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background1);
                    viewRight.setBackgroundResource(R.drawable.button_background);
                    backbtn.setVisibility(VISIBLE);
                    closebtn.setVisibility(GONE);
                }
            }


        });

        bottomSheet.setOnTouchListener(new OnSwipeTouchListener(IdentityVerificationSwipeActivity.this) {

            public void onSwipeRight() {
                firstIVeri.setVisibility(View.VISIBLE);
                secondIVeri.setVisibility(View.GONE);
                viewLeft.setBackgroundResource(R.drawable.button_background);
                viewRight.setBackgroundResource(R.drawable.button_background1);
                backbtn.setVisibility(View.GONE);
                closebtn.setVisibility(View.VISIBLE);
            }

            public void onSwipeLeft() {
                if (isNext) {
                    firstIVeri.setVisibility(GONE);
                    secondIVeri.setVisibility(VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background1);
                    viewRight.setBackgroundResource(R.drawable.button_background);
                    backbtn.setVisibility(VISIBLE);
                    closebtn.setVisibility(GONE);
                }
            }


        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        identityFile = null;
        isFileSelected = false;

        isSSNSelected = false;
        isDOBSelected = false;
        isMailAddr1 = false;
        isCity = false;
        isState = false;
        isZip = false;
        isSubmit = false;
        isNext = false;
    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            Utils.isKeyboardVisible = true;
        } else {
            Utils.isKeyboardVisible = false;
        }
        Log.e("isKeyboardVisible", Utils.isKeyboardVisible + "");
    }

}