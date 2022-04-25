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
import android.os.Handler;
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
//import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.R;
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
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.LoginViewModel;
import com.ideyalabs.wheelpicker.DatePicker;

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

public class IdentityVerificationActivity extends AppCompatActivity implements OnKeyboardVisibilityListener {
    private TextInputLayout dobTIL, ssnTIL, mailingAddTIL, mailingAddlineoptTIL, cityTIL, stateTIL, zipcodeTIL, countryTIL;
    private TextInputEditText dobET, ssnET, cityET, mailAddr1, mailAddr2, state, zipcode;
    private TextView idveriUItext, idveriUItextSuc, exitBtn, btnExit, ssnErrorTV;
    private ConstraintLayout idveriDOBConLayout, stateCL;
    private LinearLayout bottomSheet, fileSelectedLL, ssnErrorLL, swipeLL, bottomNaviLL;
    private RelativeLayout firstIVeri;
    public static CardView btnNext, btnSubmit;
    private ScrollView secondIVeri;
    private View viewLeft, viewRight;
    private ImageButton closebtn, backbtn;
    private ImageView upIdSuccessImg;
    private int mYear, mMonth, mDay;
    public static boolean isMailAddr1 = false, isCity = false, isState = false, isZip = false, isSubmit = false, isNext = false;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    public static File identityFile;
    public static String dateOfBirth = "";
    public static int identityType = 0;
    public static boolean isFileSelected = false, isSSNSelected = false, isDOBSelected = false;

    public static IdentityVerificationActivity identityVerificationActivity;
    private IdentityVerificationViewModel identityVerificationViewModel;
    ProgressDialog dialog;
    private LinearLayout address1ErrorLL, address2ErrorLL, cityErrorLL, zipcodeErrorLL;
    private TextView address1ErrorTV, address2ErrorTV, cityErrorTV, zipcodeErrorTV;
    private MyApplication myApplicationObj;
    private Long mLastClickTime = 0L;
    private DashboardViewModel dashboardViewModel;
    private DatePicker picker;
    private LoginViewModel loginViewModel;
    private String addBusiness = "false";

    private IdentityPagerAdapter identityPagerAdapter;
    public static AutoScrollViewPager viewPager;
    private String respCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_identity_verification);

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
                    if (position == 0) {
                        backbtn.setVisibility(View.GONE);
                        closebtn.setVisibility(View.VISIBLE);
                        viewLeft.setBackgroundResource(R.drawable.button_background);
                        viewRight.setBackgroundResource(R.drawable.button_background1);
                    } else if (position == 1) {
                        viewLeft.setBackgroundResource(R.drawable.button_background1);
                        viewRight.setBackgroundResource(R.drawable.button_background);
                        backbtn.setVisibility(VISIBLE);
                        closebtn.setVisibility(GONE);
                        mailAddr1.requestFocus();
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(IdentityVerificationActivity.this);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

            if (getIntent().getStringExtra("ADDPERSONAL") != null) {
                addBusiness = getIntent().getStringExtra("ADDPERSONAL");
                LogUtils.d("addBusiness", "addBusiness" + addBusiness);
            }

            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            identityVerificationActivity = this;
            myApplicationObj = (MyApplication) getApplicationContext();
            dashboardViewModel.meProfile();
            initFields();
            initObservers();

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
//        try {
//
//            Calendar c = Calendar.getInstance();
//            mYear = c.get(Calendar.YEAR);
//            mMonth = c.get(Calendar.MONTH);
//            mDay = c.get(Calendar.DAY_OF_MONTH);
//
//            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.CalendarDialogTheme,
//                    new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker view, int year,
//                                              int monthOfYear, int dayOfMonth) {
//                            try {
//                                String dateToConvert = Utils.changeFormat(dayOfMonth) + "/" + Utils.changeFormat((monthOfYear + 1)) + "/" + year;
//                                String convertedDate = convertDate(dateToConvert);
//                                dob.setText(convertedDate);
//                                isDOBSelected = true;
//                                dateOfBirth = year + "-" + Utils.changeFormat((monthOfYear + 1)) + "-" + Utils.changeFormat(dayOfMonth);
//                                enableNext();
//                                ssnET.clearFocus();
//                                datepicker = new DatePicker(IdentityVerificationActivity.this);
//                                datepicker.init(year, monthOfYear + 1, dayOfMonth, null);
//                                Utils.setUpperHintColor(dobTIL, getResources().getColor(R.color.primary_black));
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//                    }, mYear, mMonth, mDay);
//
//            long years = 568025136000L;
//            long yearsback = c.getTimeInMillis() - years;
//            datePickerDialog.getDatePicker().setMaxDate(yearsback);
//            if (datepicker != null) {
//                datePickerDialog.updateDate(datepicker.getYear(), datepicker.getMonth() - 1, datepicker.getDayOfMonth());
//            }
//            datePickerDialog.show();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    private void setToDateWheelPicker(TextInputEditText dobET) {
        try {
//            Utils.hideKeypad(IdentityVerificationActivity.this);
            long years = 568025136000L;
            picker = new DatePicker(IdentityVerificationActivity.this);
            Date maxDate = new Date(System.currentTimeMillis() - years);
            picker.getPickerView().setMaxDate(maxDate);
            if (!dateOfBirth.equals("")) {
                picker.getPickerView().setDate(Integer.parseInt(dateOfBirth.split("-")[0]),
                        Integer.parseInt(dateOfBirth.split("-")[1]) - 1,
                        Integer.parseInt(dateOfBirth.split("-")[2]));
            } else {
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis() - years);
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                picker.getPickerView().setDate(mYear, mMonth, mDay);
            }

//            dobTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//            dobTIL.setExpandedHintEnabled(false);
//            Utils.setUpperHintColor(dobTIL, getResources().getColor(R.color.primary_green));

            picker.setContinueClickButtonListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        int mYear = picker.getPickerView().getCurrentData().getFirst();
                        int mMonth = picker.getPickerView().getCurrentData().getSecond();
                        int mDay = picker.getPickerView().getCurrentData().getThird();
                        String dateToConvert = Utils.changeFormat(mDay) + "/" + Utils.changeFormat((mMonth + 1)) + "/" + mYear;
//                        String dateToConvert = Utils.changeFormat(mDay) + "/" + Utils.changeFormat((mMonth)) + "/" + mYear;
                        String convertedDate = convertDate(dateToConvert);
                        dobET.setText(convertedDate);
                        isDOBSelected = true;
                        dateOfBirth = mYear + "-" + Utils.changeFormat((mMonth + 1)) + "-" + Utils.changeFormat(mDay);
//                        dateOfBirth = mYear + "-" + Utils.changeFormat((mMonth)) + "-" + Utils.changeFormat(mDay);
                        enableNext();
                        ssnET.clearFocus();

                        Utils.setUpperHintColor(dobTIL, getResources().getColor(R.color.primary_black));
                        dobTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_black));
//                        dobTIL.setExpandedHintEnabled(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Log.e("WheelPicker", dateOfBirth);
                    picker.hide();
                }
            });

            picker.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                }
            });

//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.gravity = Gravity.CENTER;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    picker.show(getWindow());
                    ssnET.setEnabled(true);
                }
            }, 500);


        } catch (Exception e) {
            e.printStackTrace();
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
//                            Utils.setUpperHintColor(ssnTIL, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.toString().trim().length() == 4) {
                            isSSNSelected = true;
                            ssnErrorLL.setVisibility(GONE);
                            //                        ssnTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                            Utils.setUpperHintColor(ssnTIL, getResources().getColor(R.color.primary_black));
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
//                            Utils.setUpperHintColor(mailingAddTIL, getResources().getColor(R.color.primary_black));
                        } else {
//                            address1ErrorLL.setVisibility(VISIBLE);
//                            address1ErrorTV.setText("Field Required");
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
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            mailAddr1.setText(str.trim());
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

//                            Utils.setUpperHintColor(mailingAddlineoptTIL, getResources().getColor(R.color.primary_black));
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
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            mailAddr2.setText(str.trim());
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
//                            Utils.setUpperHintColor(cityTIL, getResources().getColor(R.color.primary_black));
                        } else {
//                            cityErrorLL.setVisibility(VISIBLE);
//                            cityErrorTV.setText("Field Required");
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
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            cityET.setText(str.trim());
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
                            stateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
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
//                            Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.length() < 5 && charSequence.length() > 0) {
                            isZip = false;
                            zipcodeErrorLL.setVisibility(GONE);
                            //                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                            Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.length() == 0) {
                            isZip = false;
//                            zipcodeErrorLL.setVisibility(VISIBLE);
//                            zipcodeErrorTV.setText("Field Required");
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
                            mailingAddTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(mailingAddTIL, getColor(R.color.primary_black));

                        } else {
                            mailingAddTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(mailingAddTIL, getColor(R.color.light_gray));
                            address1ErrorLL.setVisibility(VISIBLE);
                            address1ErrorTV.setText("Field Required");
                        }
                        if (mailAddr1.getText().toString().length() > 0 && !mailAddr1.getText().toString().substring(0, 1).equals(" ")) {
                            mailAddr1.setText(mailAddr1.getText().toString().substring(0, 1).toUpperCase() + mailAddr1.getText().toString().substring(1).toLowerCase());
                            mailAddr1.setSelection(mailAddr1.getText().toString().trim().length());
                        }
                    } else {
//                        mailAddr1.setHint("Street Address");
                        mailingAddTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(mailingAddTIL, getColor(R.color.primary_green));
                        address1ErrorLL.setVisibility(GONE);
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
                            mailingAddlineoptTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        } else {
                            Utils.setUpperHintColor(mailingAddlineoptTIL, getColor(R.color.light_gray));
                            mailingAddlineoptTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        }

                        if (mailAddr2.getText().toString().length() > 0 && !mailAddr2.getText().toString().substring(0, 1).equals(" ")) {
                            mailAddr2.setText(mailAddr2.getText().toString().substring(0, 1).toUpperCase() + mailAddr2.getText().toString().substring(1).toLowerCase());
                            mailAddr2.setSelection(mailAddr2.getText().toString().trim().length());
                        }
                    } else {
                        mailingAddlineoptTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(mailingAddlineoptTIL, getColor(R.color.primary_green));
//                        mailAddr2.setHint("Apt#, Suit, Floor");
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
                        if (cityET.getText().toString().length() > 0 && !cityET.getText().toString().substring(0, 1).equals(" ")) {
                            cityET.setText(cityET.getText().toString().substring(0, 1).toUpperCase() + cityET.getText().toString().substring(1));
                            cityET.setSelection(cityET.getText().toString().trim().length());
                        }
                    } else {
//                        cityET.setHint("City");
                        cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_green));
                        cityErrorLL.setVisibility(GONE);
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
                            zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_black));

                        } else if (zipcode.getText().toString().trim().length() < 5 && zipcode.getText().toString().trim().length() > 0) {
                            zipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.error_red));
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Minimum 5 Digits Required");

                        } else {
                            zipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.light_gray));
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(IdentityVerificationActivity.this);
//                        zipcode.setHint("Zip Code");
                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_green));
                        zipcodeErrorLL.setVisibility(GONE);
                    }
                }
            });

            dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(IdentityVerificationActivity.this);
                }
            });

            ssnET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        ssnET.setHint("");
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(IdentityVerificationActivity.this);
                        if (ssnET.getText().toString().trim().length() == 4) {
                            ssnErrorLL.setVisibility(GONE);
                            ssnTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.primary_black));

                        } else if (ssnET.getText().toString().trim().length() > 0 && ssnET.getText().toString().trim().length() < 4) {
                            ssnTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.error_red));
                            ssnErrorLL.setVisibility(VISIBLE);
                            ssnErrorTV.setText("Minimum 4 Characters Required");

                        } else {
                            ssnTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.light_gray));
                            ssnErrorLL.setVisibility(VISIBLE);
                            ssnErrorTV.setText("Field Required");
                        }
                    } else {
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(IdentityVerificationActivity.this);
                        ssnTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(ssnTIL, getColor(R.color.primary_green));
//                        ssnET.setHint(R.string.ssnlast);
                        ssnErrorLL.setVisibility(GONE);
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
            setKeyboardVisibilityListener(IdentityVerificationActivity.this);
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
                    ssnET.setEnabled(false);
                    //                    if (Utils.isKeyboardVisible)
//                        Utils.hideKeypad(IdentityVerificationActivity.this);
//                    setToDate(dobET);
                    setToDateWheelPicker(dobET);
//                    showWheelDatePicker(IdentityVerificationActivity.this);
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
                    ssnET.setEnabled(false);
//                    if (Utils.isKeyboardVisible)
//                        Utils.hideKeypad(IdentityVerificationActivity.this);
//                    setToDate(dobET);
                    setToDateWheelPicker(dobET);
//                    showWheelDatePicker(IdentityVerificationActivity.this);
                }
            });

            dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        ssnET.clearFocus();
                        ssnET.setEnabled(false);
//                        if (Utils.isKeyboardVisible)
//                            Utils.hideKeypad(IdentityVerificationActivity.this);
//                        setToDate(dobET);
                        setToDateWheelPicker(dobET);
//                        showWheelDatePicker(IdentityVerificationActivity.this);
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
                    showIdentityTypePopup(IdentityVerificationActivity.this);
                }
            });

            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(0);
//                    firstIVeri.setVisibility(View.VISIBLE);
//                    secondIVeri.setVisibility(View.GONE);
                    backbtn.setVisibility(View.GONE);
                    closebtn.setVisibility(View.VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background);
                    viewRight.setBackgroundResource(R.drawable.button_background1);
                    if (Utils.isKeyboardVisible) {
                        Utils.hideKeypad(IdentityVerificationActivity.this);
                    }
                }
            });

            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishMethod();
                }
            });

            exitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishMethod();
                }
            });

            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishMethod();
                }
            });

            viewLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(0);
//                    firstIVeri.setVisibility(View.VISIBLE);
//                    secondIVeri.setVisibility(View.GONE);
                    viewLeft.setBackgroundResource(R.drawable.button_background);
                    viewRight.setBackgroundResource(R.drawable.button_background1);
                    backbtn.setVisibility(View.GONE);
                    closebtn.setVisibility(View.VISIBLE);
                    if (Utils.isKeyboardVisible) {
                        Utils.hideKeypad(IdentityVerificationActivity.this);
                    }
                }

            });

            viewRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNext) {
                        viewPager.setCurrentItem(1);
                        viewLeft.setBackgroundResource(R.drawable.button_background1);
                        viewRight.setBackgroundResource(R.drawable.button_background);
                        backbtn.setVisibility(VISIBLE);
                        closebtn.setVisibility(GONE);
                        if (Utils.isKeyboardVisible) {
                            Utils.hideKeypad(IdentityVerificationActivity.this);
                        }
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
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        ssnET.clearFocus();
                        dialog = Utils.showProgressDialog(IdentityVerificationActivity.this);
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

                        dialog = Utils.showProgressDialog(IdentityVerificationActivity.this);
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
//                        addressObj.setCountry("us");
                        addressObj.setCountry(Utils.getStrCCode());
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

            ssnTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            dobTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

            mailingAddTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            mailingAddlineoptTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            cityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            stateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            countryTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void finishMethod() {

        if (addBusiness.equalsIgnoreCase("true")) {
            loginViewModel.postChangeAccount(myApplicationObj.getLoginUserId());
        } else {
            finish();
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
                    if (checkAndRequestPermissions((Activity) context)) {
                        identityType = 0;
                        context.startActivity(new Intent(context, CameraActivity.class).putExtra("FROM", "IDVE"));
                    }
                }
            });

            passportll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (checkAndRequestPermissions((Activity) context)) {
                        identityType = 1;
                        context.startActivity(new Intent(context, CameraActivity.class).putExtra("FROM", "IDVE"));
                    }
                }
            });

            sicardll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (checkAndRequestPermissions((Activity) context)) {
                        identityType = 2;
                        context.startActivity(new Intent(context, CameraActivity.class).putExtra("FROM", "IDVE"));
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
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.CAMERA);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                        Utils.displayAlert("Requires Access to Camera.", IdentityVerificationActivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", IdentityVerificationActivity.this, "", "");

                    } else {
                        startActivity(new Intent(this, CameraActivity.class).putExtra("FROM", "IDVE"));
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
                viewPager.setPagingEnabled(true);
            } else {
                isNext = false;
                btnNext.setCardBackgroundColor(identityVerificationActivity.getResources().getColor(R.color.inactive_color));
                viewPager.setPagingEnabled(false);
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
//                        firstIVeri.setVisibility(GONE);
//                        secondIVeri.setVisibility(VISIBLE);
                        viewPager.setCurrentItem(1);
                        viewLeft.setBackgroundResource(R.drawable.button_background1);
                        viewRight.setBackgroundResource(R.drawable.button_background);
                        backbtn.setVisibility(VISIBLE);
                        closebtn.setVisibility(GONE);
                        mailAddr1.requestFocus();
                        address1ErrorLL.setVisibility(GONE);
                        if (!Utils.isKeyboardVisible) {
                            Utils.shwForcedKeypad(IdentityVerificationActivity.this);
                        }

                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), IdentityVerificationActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            loginViewModel.postChangeAccountResponse().observe(this, new Observer<AddBusinessUserResponse>() {
                @Override
                public void onChanged(AddBusinessUserResponse btResp) {
                    LogUtils.d("addBusiness", "addBusiness" + btResp);
                    if (btResp != null) {
                        LogUtils.d("afternulll", "addBusiness" + btResp.getStatus());
                        if (btResp.getStatus().toLowerCase().toString().equals("success") || btResp.getStatus().equals("SUCCESS")) {
                            LogUtils.d("btResp", "btResp" + btResp);
                            Utils.setStrAuth(btResp.getData().getJwtToken());
                            if (respCode.equalsIgnoreCase("ND02") || respCode.equalsIgnoreCase("CA11")
                                    || respCode.equalsIgnoreCase("CI11") || respCode.equalsIgnoreCase("CA24")
                                    || respCode.equalsIgnoreCase("CI24")) {
                                //Success
                                startActivity(new Intent(IdentityVerificationActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                        .putExtra("screen", "SUCCESS"));
                            } else if (respCode.equalsIgnoreCase("CA22") || respCode.equalsIgnoreCase("CI22")) {
                                //SSN Error
                                startActivity(new Intent(IdentityVerificationActivity.this, IdVeAdditionalActionActivity.class));

                            } else if (respCode.equalsIgnoreCase("CA25") || respCode.equalsIgnoreCase("CI25")
                                    || respCode.equalsIgnoreCase("CA21") || respCode.equalsIgnoreCase("CI21")
                                    || respCode.equalsIgnoreCase("CA01") || respCode.equalsIgnoreCase("CI01")
                                    || respCode.equalsIgnoreCase("CA30") || respCode.equalsIgnoreCase("CI30")
                                    || respCode.equalsIgnoreCase("CA23") || respCode.equalsIgnoreCase("CI23")) {
                                //Under Review
                                startActivity(new Intent(IdentityVerificationActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                        .putExtra("screen", "UNDER_REVIEW"));

                            } else {
                                //Failed
                                startActivity(new Intent(IdentityVerificationActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                        .putExtra("screen", "FAILED"));

                            }
                        } else {
                            LogUtils.d("elseeeee", "addBusiness" + btResp);
                        }
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
                        LogUtils.d("addBusiness", "addBusiness" + addBusiness);
                        respCode = identityAddressResponse.getData().getGiactResponseName();

                        if (addBusiness.equalsIgnoreCase("true")) {
                            loginViewModel.postChangeAccount(myApplicationObj.getLoginUserId());
                        } else {
                            if (respCode.equalsIgnoreCase("ND02") || respCode.equalsIgnoreCase("CA11")
                                    || respCode.equalsIgnoreCase("CI11") || respCode.equalsIgnoreCase("CA24")
                                    || respCode.equalsIgnoreCase("CI24")) {
                                //Success
                                startActivity(new Intent(IdentityVerificationActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                        .putExtra("screen", "SUCCESS"));
                            } else if (respCode.equalsIgnoreCase("CA22") || respCode.equalsIgnoreCase("CI22")) {
                                //SSN Error
                                startActivity(new Intent(IdentityVerificationActivity.this, IdVeAdditionalActionActivity.class));

                            } else if (respCode.equalsIgnoreCase("CA25") || respCode.equalsIgnoreCase("CI25")
                                    || respCode.equalsIgnoreCase("CA21") || respCode.equalsIgnoreCase("CI21")
                                    || respCode.equalsIgnoreCase("CA01") || respCode.equalsIgnoreCase("CI01")
                                    || respCode.equalsIgnoreCase("CA30") || respCode.equalsIgnoreCase("CI30")
                                    || respCode.equalsIgnoreCase("CA23") || respCode.equalsIgnoreCase("CI23")) {
                                //Under Review
                                startActivity(new Intent(IdentityVerificationActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                        .putExtra("screen", "UNDER_REVIEW"));

                            } else {
                                //Failed
                                startActivity(new Intent(IdentityVerificationActivity.this, IdentityVerificationBindingLayoutActivity.class)
                                        .putExtra("screen", "FAILED"));

                            }

                        }

                    } else {
                        Utils.displayAlert(identityAddressResponse.getError().getErrorDescription(), IdentityVerificationActivity.this, "", identityAddressResponse.getError().getFieldErrors().get(0));
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

    class IdentityPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.linearLayoutIdVe;
                    break;
                case 1:
                    resId = R.id.scrlViewIV2nd;
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