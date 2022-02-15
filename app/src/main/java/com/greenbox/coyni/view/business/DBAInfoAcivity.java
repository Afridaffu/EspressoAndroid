package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.intro_slider.AutoScrollViewPager;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CompanyOutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.utils.outline_et.VolumeEditText;
import com.greenbox.coyni.utils.outline_et.WebsiteOutlineEditText;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

public class DBAInfoAcivity extends BaseActivity implements OnKeyboardVisibilityListener {
    TextInputLayout dbanameTIL, dbaemailTIL, businessTypeTIL, timezoneTIL;
    TextInputEditText dbanameET, dbaemailET, businessTypeET, timeZoneET;
    CompanyOutLineBoxPhoneNumberEditText customerphoneNumberET;
    ImageView closeIV, backIV, eCommerceIV, retailIV;
    WebsiteOutlineEditText websiteOET;
    public LinearLayout dbanameLL, dbaemailLL, customerphonenumLL, eCommerceLL, retailLL, dbaFillingUploadedLL, dbaFillingLL;
    VolumeEditText mpvOET, highTicketOET, avgTicketOET;
    public TextView dbanameTV, dbaemailTV, customernumTV, dbaFillinguploadTV, dbaFillingUpdatedOnTV;
    public CardView dbaNextCV, addressNextCV;
    public DBAInfoAcivity dbaInfoAcivity;
    public boolean isdbaName = false, isdbaEmail = false, iscustPhoneNumber = false, isBusinessType = false, isRB = false,
            isWebsite = false, isMPV = false, isHighTkt = false, isAvgTkt = false, isDBAFiling = false, isTimeZone = false, isNextEnabled = false;
    ConstraintLayout businessTypeCL, timeZoneCL;
    View viewBarLeft, viewBarRight;
    Long mLastClickTime = 0L;
    MyApplication objMyApplication;
    ScrollView dbaBasicSL, addressSL;

    //Address
    TextInputLayout companyaddresstil, companyaddress2til, citytil, statetil, zipcodetil, countryTIL;
    TextInputEditText companyaddressET, companyaddress2ET, cityET, stateET, zipcodeET;
    LinearLayout address1ErrorLL, address2ErrorLL, cityErrorLL, stateErrorLL, zipcodeErrorLL;
    TextView address1ErrorTV, address2ErrorTV, cityErrorTV, stateErrorTV, zipcodeErrorTV;
    public boolean isCompanyAdress1 = false, isCity = false, isState = false, isZipcode = false, isAddressNextEnabled = false, isCopyCompanyInfo = false;
    ImageView statedropdownIV;

    DBAPager dbaPager;
    AutoScrollViewPager viewPager;
    int selectedPage = 0, identificationType = 0;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    BusinessTypeResp btResponse;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_dba_information);
            initFields();
            initObservers();
            focusWatchers();
            textWatchers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        try {
            super.onResume();
            dbanameET.requestFocus();
            businessIdentityVerificationViewModel.getBusinessType();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFields() {

        try {
            dbaInfoAcivity = this;
            objMyApplication = (MyApplication) getApplicationContext();
            closeIV = findViewById(R.id.closeIV);
            backIV = findViewById(R.id.backIV);
            viewBarLeft = findViewById(R.id.viewBarLeft);
            viewBarRight = findViewById(R.id.viewBarRight);
            dbaBasicSL = findViewById(R.id.dbaBasicSL);
            addressSL = findViewById(R.id.addressSL);
            setKeyboardVisibilityListener(DBAInfoAcivity.this);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            type = getIntent().getStringExtra("TYPE");

            //Screen 1
            dbanameTIL = findViewById(R.id.DBAnameTIL);
            dbanameET = findViewById(R.id.DBAnameET);
            dbanameLL = findViewById(R.id.DBAnameErrorLL);
            dbanameTV = findViewById(R.id.DBAnameErrorTV);

            dbaemailTIL = findViewById(R.id.DBAemailTIL);
            dbaemailLL = findViewById(R.id.DBAemailErrorLL);
            dbaemailET = findViewById(R.id.DBAemailET);
            dbaemailTV = findViewById(R.id.DBAemailErrorTV);

            customerphoneNumberET = findViewById(R.id.customerphoneNumberOET);
            customerphoneNumberET.setFrom("DbaInfo", this);
            customerphoneNumberET.setHintText("Customer Service Phone Number");
            customerphonenumLL = findViewById(R.id.customerphoneNumberErrorLL);
            customernumTV = findViewById(R.id.customerphoneNumberErrorTV);

            businessTypeCL = findViewById(R.id.businessTypeCL);
            businessTypeTIL = findViewById(R.id.businessTypeTIL);
            businessTypeET = findViewById(R.id.businessTypeET);

            eCommerceLL = findViewById(R.id.eCommerceLL);
            eCommerceIV = findViewById(R.id.eCommerceIV);
            retailLL = findViewById(R.id.retailLL);
            retailIV = findViewById(R.id.retailIV);

            websiteOET = findViewById(R.id.websiteOET);
            websiteOET.setFrom("DBA_INFO", this);

            mpvOET = findViewById(R.id.mpvOET);
            mpvOET.setHint("Monthly Processing Volume");
            mpvOET.setFrom("DBA_INFO",this,"MPV");

            highTicketOET = findViewById(R.id.highTicketOET);
            highTicketOET.setHint("High Ticket");
            highTicketOET.setFrom("DBA_INFO",this,"HT");

            avgTicketOET = findViewById(R.id.avgTicketOET);
            avgTicketOET.setHint("Average Ticket");
            avgTicketOET.setFrom("DBA_INFO",this,"AT");

            timezoneTIL = findViewById(R.id.timeZoneTIL);
            timeZoneET = findViewById(R.id.timeZoneET);
            timeZoneCL = findViewById(R.id.timeZoneCL);

            dbaFillingLL = findViewById(R.id.dbaFillingLL);
            dbaFillingUploadedLL = findViewById(R.id.dbaFillingUploadedLL);
            dbaFillinguploadTV = findViewById(R.id.dbaFillinguploadTV);
            dbaFillingUpdatedOnTV = findViewById(R.id.dbaFillingUpdatedOnTV);

            dbaNextCV = findViewById(R.id.dbaNextCV);


            // Address info
            companyaddresstil = findViewById(R.id.companyaddressTIL);
            companyaddress2til = findViewById(R.id.companyaddress2TIL);
            citytil = findViewById(R.id.cityTIL);
            statetil = findViewById(R.id.stateTIL);
            zipcodetil = findViewById(R.id.zipcodeTIL);
            countryTIL = findViewById(R.id.countryTIL);

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

            addressNextCV = findViewById(R.id.addressNextCV);

            dbaPager = new DBAPager();
            viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(dbaPager);
            viewPager.setPagingEnabled(false);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.e("onPageScrolled", "onPageScrolled " + position);

                }

                @Override
                public void onPageSelected(int position) {
                    selectedPage = position;
                    if (position == 0) {
                        closeIV.setVisibility(VISIBLE);
                        backIV.setVisibility(GONE);
                        viewBarLeft.setBackgroundResource(R.drawable.button_background);
                        viewBarRight.setBackgroundResource(R.drawable.button_background1);
                    } else if (position == 1) {
                        closeIV.setVisibility(GONE);
                        backIV.setVisibility(VISIBLE);
                        viewBarLeft.setBackgroundResource(R.drawable.button_background1);
                        viewBarRight.setBackgroundResource(R.drawable.button_background);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            dbanameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            dbaemailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            businessTypeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            timezoneTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

            companyaddresstil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            companyaddress2til.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            citytil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            statetil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            zipcodetil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            countryTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

            if (type.equalsIgnoreCase("SAME")) {
                isCopyCompanyInfo = true;
                if (objMyApplication.getCompanyInfoResp() != null) {
                    CompanyInfoResp.Data cir = objMyApplication.getCompanyInfoResp().getData();
                    if (cir.getName() != null && !cir.getName().equals("")) {
                        dbanameET.setText(cir.getName());
                        dbanameET.setSelection(cir.getName().length());
                        isdbaName = true;
                    }

                    if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                        dbaemailET.setText(cir.getEmail());
                        isdbaEmail = true;
                    }

                    if (cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equalsIgnoreCase("")) {
                        customerphoneNumberET.setText(cir.getPhoneNumberDto().getPhoneNumber());
                        iscustPhoneNumber = true;
                    }

                    if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
                        companyaddressET.setText(cir.getAddressLine1());
                        isCompanyAdress1 = true;
                    }

                    if (cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")) {
                        companyaddress2ET.setText(cir.getAddressLine2());
                    }

                    if (cir.getCity() != null && !cir.getCity().equals("")) {
                        cityET.setText(cir.getCity());
                        isCity = true;
                    }

                    if (cir.getState() != null && !cir.getState().equals("")) {
                        stateET.setText(cir.getState());
                        isState = true;
                    }

                    if (cir.getZipCode() != null && !cir.getZipCode().equals("")) {
                        zipcodeET.setText(cir.getZipCode());
                        isZipcode = true;
                    }
                    enableOrDisableNext();
                    enableOrDisableAddressNext();
                }
            } else if (type.equalsIgnoreCase("DIFF")) {
                isCopyCompanyInfo = false;
            } else if (getIntent().getStringExtra("TYPE").equalsIgnoreCase("EXIST")) {
                isCopyCompanyInfo = true;
            }

            viewBarLeft.setOnClickListener(view -> {
                closeIV.setVisibility(VISIBLE);
                backIV.setVisibility(GONE);
                viewPager.setCurrentItem(0);
                viewBarLeft.setBackgroundResource(R.drawable.button_background);
                viewBarRight.setBackgroundResource(R.drawable.button_background1);
            });

            viewBarRight.setOnClickListener(view -> {
                if (isNextEnabled) {
                    closeIV.setVisibility(GONE);
                    backIV.setVisibility(VISIBLE);
                    viewPager.setCurrentItem(1);
                    viewBarLeft.setBackgroundResource(R.drawable.button_background1);
                    viewBarRight.setBackgroundResource(R.drawable.button_background);
                }
            });

            closeIV.setOnClickListener(v -> finish());

            backIV.setOnClickListener(v -> {
                if (selectedPage == 1) {
                    closeIV.setVisibility(VISIBLE);
                    backIV.setVisibility(GONE);
                    viewPager.setCurrentItem(0);
                    viewBarLeft.setBackgroundResource(R.drawable.button_background);
                    viewBarRight.setBackgroundResource(R.drawable.button_background1);
                } else if (selectedPage == 2) {
                    closeIV.setVisibility(GONE);
                    backIV.setVisibility(VISIBLE);
                    viewPager.setCurrentItem(1);
                    viewBarLeft.setBackgroundResource(R.drawable.button_background1);
                    viewBarRight.setBackgroundResource(R.drawable.button_background);
                }
            });

            timeZoneET.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Utils.populateTimeZones(DBAInfoAcivity.this, timeZoneET, objMyApplication, "DBA_INFO");
            });

            timeZoneCL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Utils.populateTimeZones(DBAInfoAcivity.this, timeZoneET, objMyApplication, "DBA_INFO");
            });

            businessTypeCL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Utils.populateBusinessTypes(this, businessTypeET, objMyApplication);
            });

            businessTypeET.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Utils.populateBusinessTypes(this, businessTypeET, objMyApplication);
            });

            eCommerceLL.setOnClickListener(view -> {
                isRB = true;
                identificationType = 9;
                dbaFillingLL.setVisibility(GONE);
                if (eCommerceIV.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_rb_unselected).getConstantState()) {
                    eCommerceIV.setImageResource(R.drawable.ic_rb_selected);
                    retailIV.setImageResource(R.drawable.ic_rb_unselected);
                }
                enableOrDisableNext();
            });

            retailLL.setOnClickListener(view -> {
                isRB = true;
                identificationType = 8;
                dbaFillingLL.setVisibility(VISIBLE);
                if (retailIV.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.ic_rb_unselected).getConstantState()) {
                    retailIV.setImageResource(R.drawable.ic_rb_selected);
                    eCommerceIV.setImageResource(R.drawable.ic_rb_unselected);
                }
                enableOrDisableNext();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initObservers() {

        try {
            businessIdentityVerificationViewModel.getBusinessTypesResponse().observe(this, new Observer<BusinessTypeResp>() {
                @Override
                public void onChanged(BusinessTypeResp businessTypeResp) {

                    if (businessTypeResp != null) {
                        if (businessTypeResp.getStatus().toLowerCase().toString().equals("success")) {
                            btResponse = businessTypeResp;
                            objMyApplication.setBusinessTypeResp(businessTypeResp);
                            Log.e("BT resp", new Gson().toJson(businessTypeResp));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void focusWatchers() {

        try {
            //Basic Info
            dbanameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        dbanameET.setHint("");
                        if (dbanameET.getText().toString().trim().length() > 1) {
                            dbanameLL.setVisibility(GONE);
                            dbanameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            dbanameET.setHintTextColor(getColor(R.color.light_gray));
                            Utils.setUpperHintColor(dbanameTIL, getColor(R.color.primary_black));

                        } else if (dbanameET.getText().toString().trim().length() == 1) {
                            dbanameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(dbanameTIL, getColor(R.color.error_red));
                            dbanameET.setHintTextColor(getColor(R.color.light_gray));
                            dbanameLL.setVisibility(VISIBLE);
                            dbanameTV.setText("Minimum 2 Characters Required");
                        } else {
                            dbanameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            dbanameLL.setVisibility(VISIBLE);
                            dbanameTV.setText("Field Required");
                            Utils.setUpperHintColor(dbanameTIL, getColor(R.color.light_gray));
                        }
                    } else {
                        dbanameET.setHint("DBA Name");
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
                            dbaemailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            dbaemailLL.setVisibility(VISIBLE);
                            dbaemailET.setHintTextColor(getColor(R.color.error_red));
                            Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.error_red));
                            dbaemailTV.setText("Please Enter a Valid Email");

                        } else if (dbaemailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(dbaemailET.getText().toString().trim())) {
                            dbaemailLL.setVisibility(GONE);
                            dbaemailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            dbaemailET.setHintTextColor(getColor(R.color.primary_black));
                            Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.primary_black));

                        } else if (dbaemailET.getText().toString().trim().length() > 0 && dbaemailET.getText().toString().trim().length() <= 5) {
                            dbaemailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.light_gray));
                            dbaemailLL.setVisibility(VISIBLE);
                            dbaemailTV.setText("Please Enter a Valid Email");
                        } else {
                            dbaemailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.light_gray));
                            dbaemailLL.setVisibility(VISIBLE);
                            dbaemailTV.setText("Field Required");
                        }
                    } else {
                        dbaemailET.setHint("support@firebbq.com");
                        dbaemailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        dbaemailET.setHintTextColor(getColor(R.color.light_gray));
                        dbaemailLL.setVisibility(GONE);
                    }
                }
            });

//            businessTypeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View view, boolean hasFocus) {
//                    if (businessTypeET.getText().toString().trim().length() > 0) {
//                        Utils.setUpperHintColor(businessTypeTIL, getColor(R.color.primary_black));
//                    } else {
//                        Utils.setUpperHintColor(businessTypeTIL, getColor(R.color.light_gray));
//                    }
//                }
//            });

            //Address info
            companyaddressET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        companyaddressET.setHint("");
                        if (companyaddressET.getText().toString().trim().length() > 0) {
                            address1ErrorLL.setVisibility(GONE);
                            companyaddresstil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(companyaddresstil, getColor(R.color.primary_black));

                        } else {
                            companyaddresstil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(companyaddresstil, getColor(R.color.light_gray));
                            address1ErrorLL.setVisibility(VISIBLE);
                            address1ErrorTV.setText("Field Required");
                        }
                    } else {
                        companyaddressET.setHint("Street Address");
                        companyaddresstil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(companyaddresstil, getColor(R.color.primary_green));
                        address1ErrorLL.setVisibility(GONE);
                    }
                }
            });

            companyaddress2ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        companyaddress2ET.setHint("");
                        if (companyaddress2ET.getText().toString().trim().length() > 0) {
                            Utils.setUpperHintColor(companyaddress2til, getColor(R.color.primary_black));
                            companyaddress2til.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                        } else {
                            Utils.setUpperHintColor(companyaddress2til, getColor(R.color.light_gray));
                            companyaddress2til.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

                        }
                    } else {
                        companyaddress2til.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(companyaddress2til, getColor(R.color.primary_green));
                        companyaddress2ET.setHint("Apt#, Suit, Floor");
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
                            citytil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(citytil, getColor(R.color.primary_black));

                        } else {
                            citytil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
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

            zipcodeET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        zipcodeET.setHint("");
                        if (zipcodeET.getText().toString().trim().length() == 5) {
                            zipcodeErrorLL.setVisibility(GONE);
                            zipcodetil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(zipcodetil, getColor(R.color.primary_black));

                        } else if (zipcodeET.getText().toString().trim().length() < 5 && zipcodeET.getText().toString().trim().length() > 0) {
                            zipcodetil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(zipcodetil, getColor(R.color.error_red));
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Minimum 5 Characters Required");

                        } else {
                            zipcodetil.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(zipcodetil, getColor(R.color.light_gray));
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Field Required");
                        }
                    } else {
                        zipcodeET.setHint("Zip Code");
                        zipcodetil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(zipcodetil, getColor(R.color.primary_green));
                        zipcodeErrorLL.setVisibility(GONE);
                    }
                }
            });

            zipcodeET.setOnTouchListener((view, motionEvent) -> {
                addressSL.scrollTo(zipcodeET.getLeft(), zipcodeET.getBottom());
                return false;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void textWatchers() {

        try {
            //Basic info
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
                        dbaemailLL.setVisibility(GONE);
                        isdbaEmail = true;
                        Utils.setUpperHintColor(dbaemailTIL, getResources().getColor(R.color.primary_black));
                    } else if (dbaemailET.getText().toString().trim().length() == 0) {
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

            businessTypeET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() > 0) {
                        Utils.setUpperHintColor(businessTypeTIL, getResources().getColor(R.color.primary_black));
                        isBusinessType = true;
                    } else {
                        isBusinessType = false;
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

            timeZoneET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 0) {
                        Utils.setUpperHintColor(timezoneTIL, getResources().getColor(R.color.primary_black));
                        isTimeZone = true;
                    } else {
                        isTimeZone = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            //Address info
            companyaddressET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() > 0) {
                            isCompanyAdress1 = true;
                            address1ErrorLL.setVisibility(GONE);
                            //                        mailingAddTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(companyaddresstil, getResources().getColor(R.color.primary_black));
                        } else {
                            //                            address1ErrorLL.setVisibility(VISIBLE);
                            //                            address1ErrorTV.setText("Field Required");
                            isCompanyAdress1 = false;
                        }
                        enableOrDisableAddressNext();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = companyaddressET.getText().toString();
                        if (str.substring(0).equals(" ")) {
                            companyaddressET.setText("");
                            companyaddressET.setSelection(companyaddressET.getText().length());
                            address1ErrorLL.setVisibility(GONE);
                        } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                            companyaddressET.setText("");
                            companyaddressET.setSelection(companyaddressET.getText().length());
                            address1ErrorLL.setVisibility(GONE);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            companyaddress2ET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() > 0) {
                            Utils.setUpperHintColor(companyaddress2til, getResources().getColor(R.color.primary_black));
                        }
                        enableOrDisableAddressNext();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = companyaddress2ET.getText().toString();
                        if (str.substring(0).equals(" ")) {
                            companyaddress2ET.setText("");
                            companyaddress2ET.setSelection(companyaddress2ET.getText().length());
                            address2ErrorLL.setVisibility(GONE);
                        } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                            companyaddress2ET.setText("");
                            companyaddress2ET.setSelection(companyaddress2ET.getText().length());
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
                            Utils.setUpperHintColor(citytil, getResources().getColor(R.color.primary_black));
                        } else {
                            //                            cityErrorLL.setVisibility(VISIBLE);
                            //                            cityErrorTV.setText("Field Required");
                            isCity = false;
                        }
                        enableOrDisableAddressNext();
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

            stateET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() > 0) {
                            Utils.setUpperHintColor(statetil, getResources().getColor(R.color.primary_black));
                            //                            statetil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            isState = true;
                        } else {
                            isState = false;
                        }
                        enableOrDisableAddressNext();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            zipcodeET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() == 5) {
                            isZipcode = true;
                            zipcodeErrorLL.setVisibility(GONE);
                            //                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(zipcodetil, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.length() < 5 && charSequence.length() > 0) {
                            isZipcode = false;
                            zipcodeErrorLL.setVisibility(GONE);
                            //                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                            Utils.setUpperHintColor(zipcodetil, getResources().getColor(R.color.primary_black));
                        } else if (charSequence.length() == 0) {
                            isZipcode = false;
                            //                            zipcodeErrorLL.setVisibility(VISIBLE);
                            //                            zipcodeErrorTV.setText("Field Required");
                        }
                        enableOrDisableAddressNext();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableOrDisableNext() {

        try {
            if (getIntent().getStringExtra("TYPE").equalsIgnoreCase("SAME")) {
                if (isdbaName && isdbaEmail && iscustPhoneNumber && isBusinessType && isRB && isWebsite && isMPV
                        && isHighTkt && isAvgTkt && isTimeZone) {
                    isNextEnabled = true;
                    dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                } else {
                    isNextEnabled = false;
                    dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            } else {
                if (isdbaName && isdbaEmail && iscustPhoneNumber && isBusinessType && isRB && isWebsite && isMPV
                        && isHighTkt && isAvgTkt && isTimeZone && isDBAFiling) {
                    isNextEnabled = true;
                    dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                } else {
                    isNextEnabled = false;
                    dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

    }

    private void enableOrDisableAddressNext() {

        try {
            if (isCompanyAdress1 && isCity && isState && isZipcode) {
                isAddressNextEnabled = true;
                addressNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isAddressNextEnabled = false;
                addressNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        try {
            if (visible) {
                Utils.isKeyboardVisible = true;
                //            pageOneView.setVisibility(VISIBLE);
                //            pageTwoView.setVisibility(VISIBLE);
            } else {
                //            pageOneView.setVisibility(GONE);
                //            pageTwoView.setVisibility(GONE);
                Utils.isKeyboardVisible = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DBAPager extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.dbaBasicSL;
                    break;
                case 1:
                    resId = R.id.addressSL;
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