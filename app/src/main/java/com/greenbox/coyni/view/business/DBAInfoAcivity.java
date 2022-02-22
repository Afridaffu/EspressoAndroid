package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraActivity;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.intro_slider.AutoScrollViewPager;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoRequest;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoUpdateResp;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.utils.FileUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CompanyOutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.utils.outline_et.VolumeEditText;
import com.greenbox.coyni.utils.outline_et.WebsiteOutlineEditText;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class DBAInfoAcivity extends BaseActivity implements OnKeyboardVisibilityListener {
    public TextInputLayout dbanameTIL, dbaemailTIL, businessTypeTIL, timezoneTIL;
    TextInputEditText dbanameET, dbaemailET, businessTypeET, timeZoneET;
    CompanyOutLineBoxPhoneNumberEditText dbaPhoneOET;
    ImageView closeIV, backIV, eCommerceIV, retailIV;
    public WebsiteOutlineEditText websiteOET;
    public LinearLayout dbanameLL, dbaemailLL, customerphonenumLL, eCommerceLL, retailLL, dbaFillingUploadedLL, dbaFillingLL;
    public VolumeEditText mpvOET, highTicketOET, avgTicketOET;
    public TextView dbanameTV, dbaemailTV, customernumTV, dbaFillinguploadTV, dbaFillingUpdatedOnTV;
    public CardView dbaNextCV, addressNextCV;
    public static DBAInfoAcivity dbaInfoAcivity;
    public boolean isdbaName = false, isdbaEmail = false, iscustPhoneNumber = false, isBusinessType = false, isECommerce = false, isRetail = false,
            isWebsite = false, isMPV = false, isHighTkt = false, isAvgTkt = false, isDBAFiling = false, isTimeZone = false, isNextEnabled = false;
    ConstraintLayout businessTypeCL, timeZoneCL, stateCL;
    public View viewBarLeft, viewBarRight, pageOneView, pageTwoView;
    Long mLastClickTime = 0L;
    MyApplication objMyApplication;
    public ScrollView dbaBasicSL, addressSL;

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
    public String type = "", selectedBTKey = "";
    IdentityVerificationViewModel identityVerificationViewModel;
    public static File dbaFile = null;
    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int PICK_IMAGE_REQUEST = 4;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    DBAInfoAcivity myActivity;

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
//            businessIdentityVerificationViewModel.getBusinessType();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            dbaInfoAPICall(prepareRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedPage == 0) {
            super.onBackPressed();
        } else if (selectedPage == 1) {
            closeIV.setVisibility(VISIBLE);
            backIV.setVisibility(GONE);
            viewPager.setCurrentItem(0);
            viewBarLeft.setBackgroundResource(R.drawable.button_background);
            viewBarLeft.setBackgroundResource(R.drawable.button_background1);

        }
    }


    private void initFields() {

        try {
            dbaInfoAcivity = myActivity = this;
            objMyApplication = (MyApplication) getApplicationContext();
            closeIV = findViewById(R.id.closeIV);
            backIV = findViewById(R.id.backIV);
            viewBarLeft = findViewById(R.id.viewBarLeft);
            viewBarRight = findViewById(R.id.viewBarRight);
            pageOneView = findViewById(R.id.pageOneView);
            pageTwoView = findViewById(R.id.pageTwoView);
            dbaBasicSL = findViewById(R.id.dbaBasicSL);
            addressSL = findViewById(R.id.addressSL);
            setKeyboardVisibilityListener(DBAInfoAcivity.this);
            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            type = getIntent().getStringExtra("TYPE");
            businessIdentityVerificationViewModel.getBusinessType();
//            businessIdentityVerificationViewModel.getDBAInfo();

            //Screen 1
            dbanameTIL = findViewById(R.id.DBAnameTIL);
            dbanameET = findViewById(R.id.DBAnameET);
            dbanameLL = findViewById(R.id.DBAnameErrorLL);
            dbanameTV = findViewById(R.id.DBAnameErrorTV);

            dbaemailTIL = findViewById(R.id.DBAemailTIL);
            dbaemailLL = findViewById(R.id.DBAemailErrorLL);
            dbaemailET = findViewById(R.id.DBAemailET);
            dbaemailTV = findViewById(R.id.DBAemailErrorTV);

            dbaPhoneOET = findViewById(R.id.dbaPhoneOET);
            dbaPhoneOET.setFrom("DbaInfo", this);
            dbaPhoneOET.setHintText("Customer Service Phone Number");
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
            websiteOET.setHint("Website");
            websiteOET.setFrom("DBA_INFO", this);

            mpvOET = findViewById(R.id.mpvOET);
            mpvOET.setHint("Monthly Processing Volume");
            mpvOET.setFrom("DBA_INFO", this, "MPV");

            highTicketOET = findViewById(R.id.highTicketOET);
            highTicketOET.setHint("High Ticket");
            highTicketOET.setFrom("DBA_INFO", this, "HT");

            avgTicketOET = findViewById(R.id.avgTicketOET);
            avgTicketOET.setHint("Average Ticket");
            avgTicketOET.setFrom("DBA_INFO", this, "AT");

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
            stateCL = findViewById(R.id.stateCL);
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

            dbanameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            dbaemailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            businessTypeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            timezoneTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));

            companyaddresstil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            companyaddress2til.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            citytil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            statetil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            zipcodetil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            countryTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));

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
                closeIV.setVisibility(VISIBLE);
                backIV.setVisibility(GONE);
                viewPager.setCurrentItem(0);
                viewBarLeft.setBackgroundResource(R.drawable.button_background);
                viewBarRight.setBackgroundResource(R.drawable.button_background1);
            });

            timeZoneET.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateTimeZones(DBAInfoAcivity.this, timeZoneET, objMyApplication, "DBA_INFO");
            });

            timeZoneCL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateTimeZones(DBAInfoAcivity.this, timeZoneET, objMyApplication, "DBA_INFO");

            });

            businessTypeCL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateBusinessTypes(this, businessTypeET, objMyApplication, "DBA_INFO");


            });

            businessTypeET.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateBusinessTypes(this, businessTypeET, objMyApplication, "DBA_INFO");

            });

            eCommerceLL.setOnClickListener(view -> {
                if (!isECommerce) {
                    eCommerceIV.setImageResource(R.drawable.ic_rb_selected);
                    retailIV.setImageResource(R.drawable.ic_rb_unselected);
                    websiteOET.setHint("Website");
                    isECommerce = true;
                    isRetail = false;
                    identificationType = 9;
                    dbaFillingLL.setVisibility(GONE);
                }
                enableOrDisableNext();


            });

            retailLL.setOnClickListener(view -> {
                if (!isRetail) {
                    websiteOET.setHint("Website(Optional)");
                    isECommerce = false;
                    isRetail = true;
                    identificationType = 8;
                    dbaFillingLL.setVisibility(VISIBLE);
                    retailIV.setImageResource(R.drawable.ic_rb_selected);
                    eCommerceIV.setImageResource(R.drawable.ic_rb_unselected);
                }
                enableOrDisableNext();
            });

            dbaNextCV.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isNextEnabled) {
                    dbaInfoAPICall(prepareRequest());
                }
            });

            addressNextCV.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isAddressNextEnabled) {
                    businessIdentityVerificationViewModel.postDBAInfo(prepareRequest());
                }
            });

            dbaFillingLL.setOnClickListener(v -> {
                if (checkAndRequestPermissions(DBAInfoAcivity.this)) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (Utils.isKeyboardVisible)
                        Utils.hideKeypad(this);
                    chooseFilePopup(DBAInfoAcivity.this);
                }

            });

            stateET.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateStates(this, stateET, objMyApplication);
            });

            stateCL.setOnClickListener(view -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                Utils.populateStates(this, stateET, objMyApplication);
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
                            loadCompanyInfo();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getUpdateBasicDBAInfoResponse().observe(this, new Observer<DBAInfoUpdateResp>() {
                @Override
                public void onChanged(DBAInfoUpdateResp dbaInfoUpdateResp) {

                    if (dialog != null)
                        dismissDialog();

                    if (dbaInfoUpdateResp != null) {
                        if (dbaInfoUpdateResp.getStatus().toLowerCase().toString().equals("success")) {
                            closeIV.setVisibility(GONE);
                            backIV.setVisibility(VISIBLE);

                            if (selectedPage == 0) {
                                viewPager.setCurrentItem(1);
                                closeIV.setVisibility(GONE);
                                backIV.setVisibility(VISIBLE);
                                viewBarLeft.setBackgroundResource(R.drawable.button_background1);
                                viewBarRight.setBackgroundResource(R.drawable.button_background);
                            }
                        } else {
                            Utils.displayAlert(dbaInfoUpdateResp.getError().getErrorDescription(),
                                    DBAInfoAcivity.this, "", dbaInfoUpdateResp.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            identityVerificationViewModel.getUploadIdentityImageResponse().observe(this, new Observer<IdentityImageResponse>() {
                @Override
                public void onChanged(IdentityImageResponse identityImageResponse) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {

                        dbaFillinguploadTV.setVisibility(GONE);
                        dbaFillingUploadedLL.setVisibility(VISIBLE);
                        String dateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis()));
                        dbaFillingUpdatedOnTV.setText(dateString);
                        isDBAFiling = true;

                        enableOrDisableNext();
                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), DBAInfoAcivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
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
                        RequestBody requestBody = null;
                        MultipartBody.Part idFile = null;
                        requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), dbaFile);
                        idFile = MultipartBody.Part.createFormData("identityFile", dbaFile.getName(), requestBody);
                        RequestBody idType = RequestBody.create(MediaType.parse("text/plain"), identificationType + "");
                        RequestBody idNumber = RequestBody.create(MediaType.parse("text/plain"), objMyApplication.getCompanyInfoResp().getData().getSsnOrEin());
                        identityVerificationViewModel.uploadIdentityImage(idFile, idType, idNumber);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getPostDBAInfoResponse().observe(this, new Observer<DBAInfoUpdateResp>() {
                @Override
                public void onChanged(DBAInfoUpdateResp dbaInfoUpdateResp) {

                    if (dialog != null)
                        dismissDialog();
                    if (dbaInfoUpdateResp != null) {
                        if (dbaInfoUpdateResp.getStatus().toLowerCase().toString().equals("success")) {
                            finish();
                        } else {
                            Utils.displayAlert(dbaInfoUpdateResp.getError().getErrorDescription(),
                                    DBAInfoAcivity.this, "", dbaInfoUpdateResp.getError().getFieldErrors().get(0));
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
                            dbanameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            dbanameET.setHintTextColor(getColor(R.color.light_gray));
                            Utils.setUpperHintColor(dbanameTIL, getColor(R.color.primary_black));

                        } else if (dbanameET.getText().toString().trim().length() == 1) {
                            dbanameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            Utils.setUpperHintColor(dbanameTIL, getColor(R.color.error_red));
                            dbanameET.setHintTextColor(getColor(R.color.light_gray));
                            dbanameLL.setVisibility(VISIBLE);
                            dbanameTV.setText("Minimum 2 Characters Required");
                        } else {
                            dbanameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
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
                            dbaemailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            dbaemailLL.setVisibility(VISIBLE);
                            dbaemailET.setHintTextColor(getColor(R.color.error_red));
                            Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.error_red));
                            dbaemailTV.setText("Please Enter a Valid Email");

                        } else if (dbaemailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(dbaemailET.getText().toString().trim())) {
                            dbaemailLL.setVisibility(GONE);
                            dbaemailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            dbaemailET.setHintTextColor(getColor(R.color.primary_black));
                            Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.primary_black));

                        } else if (dbaemailET.getText().toString().trim().length() > 0 && dbaemailET.getText().toString().trim().length() <= 5) {
                            dbaemailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            Utils.setUpperHintColor(dbaemailTIL, getColor(R.color.error_red));
                            dbaemailLL.setVisibility(VISIBLE);
                            dbaemailTV.setText("Please Enter a Valid Email");
                        } else {
                            dbaemailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
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

            //Address info
            companyaddressET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        companyaddressET.setHint("");
                        if (companyaddressET.getText().toString().trim().length() > 0) {
                            address1ErrorLL.setVisibility(GONE);
                            companyaddresstil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            Utils.setUpperHintColor(companyaddresstil, getColor(R.color.primary_black));

                        } else {
                            companyaddresstil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
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
                            companyaddress2til.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        } else {
                            Utils.setUpperHintColor(companyaddress2til, getColor(R.color.light_gray));
                            companyaddress2til.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));

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
                            citytil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            Utils.setUpperHintColor(citytil, getColor(R.color.primary_black));

                        } else {
                            citytil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
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
                            zipcodetil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            Utils.setUpperHintColor(zipcodetil, getColor(R.color.primary_black));

                        } else if (zipcodeET.getText().toString().trim().length() < 5 && zipcodeET.getText().toString().trim().length() > 0) {
                            zipcodetil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            Utils.setUpperHintColor(zipcodetil, getColor(R.color.error_red));
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Minimum 5 Characters Required");

                        } else {
                            zipcodetil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
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
            if (isECommerce) {
                if (isdbaName && isdbaEmail && iscustPhoneNumber && isBusinessType && isWebsite && isMPV
                        && isHighTkt && isAvgTkt && isTimeZone) {
                    isNextEnabled = true;
                    dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                    viewPager.setPagingEnabled(true);
                } else {
                    isNextEnabled = false;
                    dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    viewPager.setPagingEnabled(false);
                }
            } else if (isRetail) {
                if (isdbaName && isdbaEmail && iscustPhoneNumber && isBusinessType && isMPV
                        && isHighTkt && isAvgTkt && isTimeZone && isDBAFiling) {
                    isNextEnabled = true;
                    dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                    viewPager.setPagingEnabled(true);
                } else {
                    isNextEnabled = false;
                    dbaNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                    viewPager.setPagingEnabled(false);
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
                if (isNextEnabled)
                    viewPager.setPagingEnabled(true);
            } else {
                isAddressNextEnabled = false;
                addressNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                viewPager.setPagingEnabled(false);
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
//                pageOneView.setVisibility(VISIBLE);
//                pageTwoView.setVisibility(VISIBLE);
            } else {
//                pageOneView.setVisibility(GONE);
//                pageTwoView.setVisibility(GONE);
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

    private void loadCompanyInfo() {
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
                    dbaemailET.setSelection(cir.getEmail().length());
                }

                if (cir.getPhoneNumberDto() != null && cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equalsIgnoreCase("")) {
                    String pn = cir.getPhoneNumberDto().getPhoneNumber();
                    String phoneNumber = "(" + pn.substring(0, 3) + ") " + pn.substring(3, 6) + "-" + pn.substring(6, pn.length());
                    dbaPhoneOET.setText(phoneNumber);
                    iscustPhoneNumber = true;
                    dbaPhoneOET.setSelection();
                }

                if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
                    companyaddressET.setText(cir.getAddressLine1());
                    isCompanyAdress1 = true;
                    companyaddressET.setSelection(cir.getAddressLine1().length());
                }

                if (cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")) {
                    companyaddress2ET.setText(cir.getAddressLine2());
                    companyaddress2ET.setSelection(cir.getAddressLine2().length());
                }

                if (cir.getCity() != null && !cir.getCity().equals("")) {
                    cityET.setText(cir.getCity());
                    isCity = true;
                    cityET.setSelection(cir.getCity().length());
                }

                if (cir.getState() != null && !cir.getState().equals("")) {
                    stateET.setText(cir.getState());
                    isState = true;
                    stateET.setSelection(cir.getState().length());
                }

                if (cir.getZipCode() != null && !cir.getZipCode().equals("")) {
                    zipcodeET.setText(cir.getZipCode());
                    isZipcode = true;
                    zipcodeET.setSelection(cir.getZipCode().length());
                }

                enableOrDisableNext();
                enableOrDisableAddressNext();
            }
        } else if (type.equalsIgnoreCase("DIFF")) {
            isCopyCompanyInfo = false;
        } else if (getIntent().getStringExtra("TYPE").equalsIgnoreCase("EXIST")) {
            isCopyCompanyInfo = false;

            if (objMyApplication.getDbaInfoResp() != null && objMyApplication.getDbaInfoResp().getStatus().equalsIgnoreCase("SUCCESS")) {
                DBAInfoResp.Data cir = objMyApplication.getDbaInfoResp().getData();
                if (cir.getName() != null && !cir.getName().equals("")) {
                    dbanameET.setText(cir.getName());
                    dbanameET.setSelection(cir.getName().length());
                    isdbaName = true;
                }

                if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                    dbaemailET.setText(cir.getEmail());
                    isdbaEmail = true;
                    dbaemailET.setSelection(dbaemailET.getText().toString().length());
                }

                if (cir.getPhoneNumberDto() != null && cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equalsIgnoreCase("")) {
                    String pn = cir.getPhoneNumberDto().getPhoneNumber();
                    String phoneNumber = "(" + pn.substring(0, 3) + ") " + pn.substring(3, 6) + "-" + pn.substring(6, pn.length());
                    dbaPhoneOET.setText(phoneNumber);
                    iscustPhoneNumber = true;
                    dbaPhoneOET.setSelection();

                }

                if (cir.getBusinessType() != null && !cir.getBusinessType().equals("")) {
                    selectedBTKey = cir.getBusinessType();
                    for (int i = 0; i < btResponse.getData().size(); i++) {
                        if (selectedBTKey.toLowerCase().trim().equals(btResponse.getData().get(i).getKey().toLowerCase())) {
                            businessTypeET.setText(btResponse.getData().get(i).getValue());
                            break;
                        }
                    }
                    isBusinessType = true;
                }

                if (cir.getWebsite() != null && !cir.getWebsite().equals("")) {
                    websiteOET.setText(cir.getWebsite());
                    isWebsite = true;
                    websiteOET.setSelection();
                }

                if (cir.getRequiredDocuments().size() > 0) {
                    dbaFillingLL.setVisibility(VISIBLE);
                    dbaFillinguploadTV.setVisibility(GONE);
                    dbaFillingUploadedLL.setVisibility(VISIBLE);
                    dbaFillingUpdatedOnTV.setText(Utils.convertDocUploadedDate(cir.getRequiredDocuments().get(0).getUpdatedAt()));
                    isDBAFiling = true;

                    websiteOET.setHint("Website(Optional)");
                    isECommerce = false;
                    isRetail = true;
                    identificationType = 8;
                    retailIV.setImageResource(R.drawable.ic_rb_selected);
                    eCommerceIV.setImageResource(R.drawable.ic_rb_unselected);
                } else {
                    eCommerceIV.setImageResource(R.drawable.ic_rb_selected);
                    retailIV.setImageResource(R.drawable.ic_rb_unselected);
                    websiteOET.setHint("Website");
                    isECommerce = true;
                    isRetail = false;
                    identificationType = 9;
                }

                if (cir.getMonthlyProcessingVolume() != null && !cir.getMonthlyProcessingVolume().equals("")) {
                    mpvOET.setText(Utils.USNumberFormat(Double.parseDouble(cir.getMonthlyProcessingVolume())));
                    isMPV = true;
                    mpvOET.setSelection();
                }

                if (cir.getHighTicket() != null && !cir.getHighTicket().equals("")) {
                    highTicketOET.setText(Utils.USNumberFormat(Double.parseDouble(cir.getHighTicket())));
                    isHighTkt = true;
                    highTicketOET.setSelection();
                }

                if (cir.getAverageTicket() != null && !cir.getAverageTicket().equals("")) {
                    avgTicketOET.setText(Utils.USNumberFormat(Double.parseDouble(cir.getAverageTicket())));
                    isAvgTkt = true;
                    avgTicketOET.setSelection();
                }


                if (cir.getTimeZone() == 0) {
                    timeZoneET.setText(getString(R.string.PST));
                    objMyApplication.setTempTimezone(getString(R.string.PST));
                    objMyApplication.setTempTimezoneID(0);
                    objMyApplication.setStrPreference("PST");
                } else if (cir.getTimeZone() == 1) {
                    timeZoneET.setText(getString(R.string.MST));
                    objMyApplication.setTempTimezone(getString(R.string.MST));
                    objMyApplication.setTempTimezoneID(1);
                    objMyApplication.setStrPreference("America/Denver");
                } else if (cir.getTimeZone() == 2) {
                    timeZoneET.setText(getString(R.string.CST));
                    objMyApplication.setTempTimezone(getString(R.string.CST));
                    objMyApplication.setTempTimezoneID(2);
                    objMyApplication.setStrPreference("CST");
                } else if (cir.getTimeZone() == 3) {
                    timeZoneET.setText(getString(R.string.EST));
                    objMyApplication.setTempTimezone(getString(R.string.EST));
                    objMyApplication.setTempTimezoneID(3);
                    objMyApplication.setStrPreference("America/New_York");
                } else if (cir.getTimeZone() == 4) {
                    timeZoneET.setText(getString(R.string.HST));
                    objMyApplication.setTempTimezone(getString(R.string.HST));
                    objMyApplication.setTempTimezoneID(4);
                    objMyApplication.setStrPreference("HST");
                } else if (cir.getTimeZone() == 5) {
                    timeZoneET.setText(getString(R.string.AST));
                    objMyApplication.setTempTimezone(getString(R.string.AST));
                    objMyApplication.setTempTimezoneID(5);
                    objMyApplication.setStrPreference("AST");
                }


                if (cir.getAddressLine1() != null && !cir.getAddressLine1().equals("")) {
                    companyaddressET.setText(cir.getAddressLine1());
                    isCompanyAdress1 = true;
                    companyaddressET.setSelection(cir.getAddressLine1().length());
                }

                if (cir.getAddressLine2() != null && !cir.getAddressLine2().equals("")) {
                    companyaddress2ET.setText(cir.getAddressLine2());
                    companyaddress2ET.setSelection(cir.getAddressLine2().length());

                }

                if (cir.getCity() != null && !cir.getCity().equals("")) {
                    cityET.setText(cir.getCity());
                    isCity = true;
                    cityET.setSelection(cir.getCity().length());

                }

                if (cir.getState() != null && !cir.getState().equals("")) {
                    stateET.setText(cir.getState());
                    isState = true;
                    stateET.setSelection(cir.getState().length());

                }

                if (cir.getZipCode() != null && !cir.getZipCode().equals("")) {
                    zipcodeET.setText(cir.getZipCode());
                    isZipcode = true;
                    zipcodeET.setSelection(cir.getZipCode().length());
                }

                enableOrDisableNext();
                enableOrDisableAddressNext();
            }
        }

    }

    public void dbaInfoAPICall(DBAInfoRequest dbaInfoRequest) {
        businessIdentityVerificationViewModel.patchDBAInfo(dbaInfoRequest);
    }

    public DBAInfoRequest prepareRequest() {
        DBAInfoRequest dbaInfoRequest = new DBAInfoRequest();
        try {
            //Basic
//            if (isNextEnabled) {
            PhNoWithCountryCode phone = new PhNoWithCountryCode();
            phone.setCountryCode(Utils.strCCode);
            String phoneNumber = dbaPhoneOET.getText().toString().substring(1, 4) + dbaPhoneOET.getText().toString().substring(6, 9) + dbaPhoneOET.getText().toString().substring(10, dbaPhoneOET.getText().length());
            phone.setPhoneNumber(phoneNumber.trim());
            //Phone
            if (phone.getCountryCode() != null && phone.getPhoneNumber().length() == 10)
                dbaInfoRequest.setPhoneNumberDto(phone);
            //Name
            if (dbanameET.getText().toString().trim().length() > 1)
                dbaInfoRequest.setName(dbanameET.getText().toString().trim());
            //Email
            if (Utils.isValidEmail(dbaemailET.getText().toString().trim()))
                dbaInfoRequest.setEmail(dbaemailET.getText().toString().trim());
            //Business Type
            if (!selectedBTKey.equals(""))
                dbaInfoRequest.setBusinessType(selectedBTKey.trim());
            //IdentificationID
            if (identificationType != 0)
                dbaInfoRequest.setIdentificationType(identificationType);
            //Avg ticket
            if (avgTicketOET.getText().trim().length() > 0)
                dbaInfoRequest.setAverageTicket(Integer.parseInt(Utils.convertBigDecimalUSDC(avgTicketOET.getText().trim().replace(",", "")).split("\\.")[0]));
            //high ticket
            if (highTicketOET.getText().trim().length() > 0)
                dbaInfoRequest.setHighTicket(Integer.parseInt(Utils.convertBigDecimalUSDC(highTicketOET.getText().trim().replace(",", "")).split("\\.")[0]));
            //MPV
            if (mpvOET.getText().trim().length() > 0)
                dbaInfoRequest.setMonthlyProcessingVolume(Integer.parseInt(Utils.convertBigDecimalUSDC(mpvOET.getText().trim().replace(",", "")).split("\\.")[0]));
            dbaInfoRequest.setCopyCompanyInfo(isCopyCompanyInfo);
            dbaInfoRequest.setTimeZone(objMyApplication.getTimezoneID());
            //Website
            if (isValidUrl(websiteOET.getText().trim()))
                dbaInfoRequest.setWebsite(websiteOET.getText().trim());
//            }

            //Address
//            if (isAddressNextEnabled) {
            if (companyaddressET.getText().toString().trim().length() > 0)
                dbaInfoRequest.setAddressLine1(companyaddressET.getText().toString().trim());
            if (companyaddress2ET.getText().toString().trim().length() > 0)
                dbaInfoRequest.setAddressLine2(companyaddress2ET.getText().toString().trim());
            if (cityET.getText().toString().trim().length() > 0)
                dbaInfoRequest.setCity(cityET.getText().toString().trim());
            if (stateET.getText().toString().trim().length() > 0)
                dbaInfoRequest.setState(stateET.getText().toString().trim());
            if (zipcodeET.getText().toString().trim().length() >= 5)
                dbaInfoRequest.setZipCode(zipcodeET.getText().toString().trim());
            dbaInfoRequest.setCountry("us");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbaInfoRequest;
    }

    public static boolean checkAndRequestPermissions(final Activity context) {
        try {
            int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.CAMERA);
            int internalStorage = ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();
            if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
            }
            if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (internalStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded
                        .add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
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
                        Utils.displayAlert("Requires Access to Camera.", DBAInfoAcivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", DBAInfoAcivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", DBAInfoAcivity.this, "", "");

                    } else {
//                        startActivity(new Intent(this, CameraActivity.class));
                        chooseFilePopup(this);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAndUploadAdditionalDoc(int docID) {
        identityVerificationViewModel.removeIdentityImage(docID + "");
    }

    private void chooseFilePopup(final Context context) {
        try {
            Dialog chooseFile = new Dialog(context);
            chooseFile.requestWindowFeature(Window.FEATURE_NO_TITLE);
            chooseFile.setContentView(R.layout.activity_choose_file_botm_sheet);
            chooseFile.setCancelable(true);
            Window window = chooseFile.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            chooseFile.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            chooseFile.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            TextView libraryTV = chooseFile.findViewById(R.id.libraryTV);
            TextView takePhotoTV = chooseFile.findViewById(R.id.takePhotoTV);
            TextView browseFileTV = chooseFile.findViewById(R.id.browseFileTV);

            libraryTV.setOnClickListener(view -> {
                chooseFile.dismiss();

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
            });

            takePhotoTV.setOnClickListener(view -> {
                chooseFile.dismiss();
                startActivity(new Intent(DBAInfoAcivity.this, CameraActivity.class).putExtra("FROM", "DBA_INFO"));

            });

            browseFileTV.setOnClickListener(view -> {
                chooseFile.dismiss();

                Intent pickIntent = new Intent();
                pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
                pickIntent.setType("*/*");
                String[] extraMimeTypes = {"application/pdf", "image/*"};
                pickIntent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeTypes);
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent chooserIntent = Intent.createChooser(pickIntent, "Select Picture");
                startActivityForResult(chooserIntent, ACTIVITY_CHOOSE_FILE);

            });

            chooseFile.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != RESULT_OK) return;
            String path = "";
            if (requestCode == ACTIVITY_CHOOSE_FILE) {
                uploadDocumentFromLibrary(data.getData(), ACTIVITY_CHOOSE_FILE);
            } else if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                uploadDocumentFromLibrary(data.getData(), PICK_IMAGE_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void uploadDocumentFromLibrary(Uri uri, int reqType) {
        try {
            String FilePath = "";
            if (reqType == ACTIVITY_CHOOSE_FILE) {
                FilePath = FileUtils.getReadablePathFromUri(getApplicationContext(), uri);
            } else {
                FilePath = getRealPathFromURI(uri);
            }
            File mediaFile = new File(FilePath);
            dbaFile = mediaFile;
            removeAndUploadAdditionalDoc(identificationType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }
}