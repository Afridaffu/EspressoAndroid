package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.core.widget.NestedScrollView;
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
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.identity_verification.PhotoIDEntityObject;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CompanyOutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.utils.outline_et.SSNOutlineBoxNumberEditText;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.IdentityVerificationActivity;
import com.greenbox.coyni.view.PreferencesActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;

import org.w3c.dom.Text;

public class CompanyInformationActivity extends BaseActivity implements OnKeyboardVisibilityListener {
    public CardView basicNextCV, addressNextCV;
    ImageView close, businessEntityDD;
    CompanyOutLineBoxPhoneNumberEditText compphoneNumberET;
    TextInputEditText companynameET, companyemailET, timeZoneET, businessET;
    TextInputLayout companynametil, companyemailtil;
    public LinearLayout companynameErrorLL, companyemailErrorLL, compphoneNumberErrorLL, ssnErrorLL;
    public TextView companynameerrorTV, companyemailerrorTV, compphonenumberTV, ssnErrorTV;
    Dialog chooseEntityDialog;
    public boolean iscompanyName = false, iscompanyEmail = false, iscompPhoneNumber = false, isBusinessEntity = false, isSSN = false, isTimeZone = false, isBasicNextEnabled = false;
    public CompanyInformationActivity companyInformationActivity;
    CompanyInforamtionPager companyInforamtionPager;
    AutoScrollViewPager viewPager;
    Long mLastClickTime = 0L;

    //Address
    TextInputLayout companyaddresstil, companyaddress2til, citytil, statetil, zipcodetil;
    TextInputEditText companyaddressET, companyaddress2ET, cityET, stateET, zipcodeET;
    LinearLayout address1ErrorLL, address2ErrorLL, cityErrorLL, stateErrorLL, zipcodeErrorLL;
    TextView address1ErrorTV, address2ErrorTV, cityErrorTV, stateErrorTV, zipcodeErrorTV;
    Dialog popupStates;
    public boolean isCompanyAdress1 = false, isCity = false, isState = false, isZipcode = false, isAddressNextEnabled = false;
    ImageView statedropdownIV;

    View divider0, divider1, divider2, pageOneView, pageTwoView, pageThreeView;
    ConstraintLayout businessEntityCL, timeZoneCL;
    public SSNOutlineBoxNumberEditText ssnET;
    String SSNTYPE = "";
    int identificationType;
    MyApplication objMyApplication;
    public ScrollView basicInfoSL;

    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_company_information);
        initFields();
        initObservers();
        focusWatchers();
        textWatchers();
    }

    protected void onResume() {
        super.onResume();
//        if (companynameET.getId() == focusedID) {
//            companynameET.requestFocus();
//        } else if (companyemailET.getId() == focusedID) {
//            companyemailET.requestFocus();
//        } else if (compphoneNumberET.getId() == focusedID) {
//            compphoneNumberET.requestFocus();
//        } else {
//            companynameET.requestFocus();
//        }
//
//        companynameET.getText().toString();
//        companyemailET.getText().toString();
        companynameET.requestFocus();
    }

    public void initFields() {
        try {
            companyInformationActivity = this;
            objMyApplication = (MyApplication) getApplicationContext();
            setKeyboardVisibilityListener(CompanyInformationActivity.this);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            businessIdentityVerificationViewModel.getCompanyInfo();

            basicInfoSL = findViewById(R.id.basicInfoSL);
            divider0 = findViewById(R.id.divider0);
            divider1 = findViewById(R.id.divider1);
            divider2 = findViewById(R.id.divider2);
            pageOneView = findViewById(R.id.pageOneView);
            pageTwoView = findViewById(R.id.pageTwoView);
            pageThreeView = findViewById(R.id.pageThreeView);

            //Basic info
            companyemailET = findViewById(R.id.companyemailET);
            companyemailtil = findViewById(R.id.companyemailTIL);

            companynameET = findViewById(R.id.companynameET);
            companynametil = findViewById(R.id.companynameTIL);

            companynameerrorTV = findViewById(R.id.companyNameErrorTV);
            companynameErrorLL = findViewById(R.id.companyNameErrorLL);

            companyemailerrorTV = findViewById(R.id.companyemailErrorTV);
            companyemailErrorLL = findViewById(R.id.companyemailErrorLL);

            compphonenumberTV = findViewById(R.id.CompanyphoneNumberErrorTV);
            compphoneNumberET = findViewById(R.id.CompanyphoneNumberOET);
            compphoneNumberET.setFrom("Company_Information", this);
            compphoneNumberErrorLL = findViewById(R.id.CompanyphoneNumberErrorLL);
            ssnErrorLL = findViewById(R.id.ssnErrorLL);
            ssnErrorTV = findViewById(R.id.ssnErrorTV);

            businessEntityCL = findViewById(R.id.businessEntityCL);
            timeZoneCL = findViewById(R.id.timeZoneCL);
            businessET = findViewById(R.id.businessET);
            timeZoneET = findViewById(R.id.timeZoneET);
            businessEntityDD = findViewById(R.id.businessEntityIV);
            ssnET = findViewById(R.id.ssnOutLineBoxET);
            ssnET.setFrom("CompanyInfo", this);


            basicNextCV = findViewById(R.id.basicNextCV);

            // Address info
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

            addressNextCV = findViewById(R.id.addressNextCV);

            close = findViewById(R.id.closeIV);

            companyInforamtionPager = new CompanyInforamtionPager();
            viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(companyInforamtionPager);
            viewPager.setPagingEnabled(false);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.e("onPageScrolled", "onPageScrolled " + position);

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {

                    } else if (position == 1) {

                    } else if (position == 2) {

                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            addressNextCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (isAddressNextEnabled) {

                    }
                }
            });

            divider0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            divider1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            divider2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            businessEntityDD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    chooseBusinessEntityPopup(CompanyInformationActivity.this);
                }
            });

            businessET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    chooseBusinessEntityPopup(CompanyInformationActivity.this);
                }
            });

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            timeZoneET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateTimeZones(CompanyInformationActivity.this, timeZoneET, objMyApplication, "COMPANY_INFO");
                }
            });

            timeZoneCL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Utils.populateTimeZones(CompanyInformationActivity.this, timeZoneET, objMyApplication, "COMPANY_INFO");
                }
            });

            basicNextCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (isBasicNextEnabled) {

                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers(){
        try {
            businessIdentityVerificationViewModel.getGetBusinessTrackerResponse().observe(this, new Observer<BusinessTrackerResponse>() {
                @Override
                public void onChanged(BusinessTrackerResponse businessTrackerResponse) {

                    if (businessTrackerResponse != null) {
                        if (businessTrackerResponse.getStatus().toLowerCase().toString().equals("success")) {
                            objMyApplication.setBusinessTrackerResponse(businessTrackerResponse);

                            Log.e("Tracker resp", new Gson().toJson(objMyApplication.getBusinessTrackerResponse()));
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
            //Basic info
            companynameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus) {
                        companynameET.setHint("");
                        if (companynameET.getText().toString().trim().length() > 1) {
                            companynameErrorLL.setVisibility(GONE);
                            companynametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            companynameET.setHintTextColor(getColor(R.color.light_gray));
                            Utils.setUpperHintColor(companynametil, getColor(R.color.primary_black));

                        } else if (companynameET.getText().toString().trim().length() == 1) {
                            companynametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(companynametil, getColor(R.color.error_red));
                            companynameErrorLL.setVisibility(VISIBLE);
                            companynameerrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            companynametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            companynameET.setHintTextColor(getColor(R.color.light_gray));
                            companynameErrorLL.setVisibility(VISIBLE);
                            companynameerrorTV.setText("Field Required");

                        }
                    } else {
                        companynameET.setHint("Company’s Name");
                        companynametil.setBoxStrokeColor(getColor(R.color.primary_green));
                        companynameET.setHintTextColor(getColor(R.color.light_gray));
                        companynameErrorLL.setVisibility(GONE);
                    }

                }
            });

            companyemailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        companyemailET.setHint("");
                        if (companyemailET.getText().toString().trim().length() > 2 && !Utils.isValidEmail(companyemailET.getText().toString().trim())) {
                            companyemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(companyemailtil, getColor(R.color.error_red));
                            companyemailErrorLL.setVisibility(VISIBLE);
                            companyemailerrorTV.setText("Invalid Email");
                        } else if (companyemailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(companyemailET.getText().toString().trim())) {
                            companyemailtil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(companyemailtil, getColor(R.color.primary_black));
                            companyemailErrorLL.setVisibility(GONE);
                        } else {
                            companyemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(companyemailtil, getColor(R.color.light_gray));
                            companyemailErrorLL.setVisibility(VISIBLE);
                            companyemailerrorTV.setText("Field Required");
                        }
                    } else {
                        companyemailET.setHint("123@coyni.com");
                        companyemailtil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(companyemailtil, getColor(R.color.primary_green));
                    }
                }
            });

            //Address info
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
                        } else if (zipcodeET.getText().toString().trim().length() >= 2 && zipcodeET.getText().toString().trim().length() <= 4) {
                            zipcodetil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(zipcodetil, getColor(R.color.error_red));
                            zipcodeErrorLL.setVisibility(VISIBLE);
                            zipcodeErrorTV.setText("Field Required 5 Characters");

                        } else {
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void textWatchers() {
        try {
            //Basic info
            companynameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 2) {
                        iscompanyName = true;
                        companynametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(companynametil, getResources().getColor(R.color.primary_green));
                    } else {
                        companynameerrorTV.setText("Field Required");
                        iscompanyName = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            companyemailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                        iscompanyEmail = false;
                        companyemailErrorLL.setVisibility(GONE);
                        companyemailtil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(companyemailtil, getResources().getColor(R.color.primary_green));

                    } else if (companyemailET.getText().toString().trim().length() == 0) {
                        companyemailErrorLL.setVisibility(VISIBLE);
                        companyemailerrorTV.setText("Field Required");
                    }
                    if (Utils.isValidEmail(charSequence.toString().trim()) && charSequence.toString().trim().length() > 5) {
                        iscompanyEmail = true;
                    } else {
                        iscompanyEmail = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = companyemailET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                            companyemailET.setText(companyemailET.getText().toString().replaceAll(" ", ""));
                            companyemailET.setSelection(companyemailET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            //Address info
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
                    enableOrDisableAddressNext();
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
                    enableOrDisableAddressNext();
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
                    enableOrDisableAddressNext();
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
                    enableOrDisableAddressNext();
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
                    if (charSequence.toString().trim().length() > 2) {
                        isZipcode = true;
                        zipcodeErrorLL.setVisibility(GONE);
                        zipcodetil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    } else {
                        isZipcode = false;
                        zipcodeET.setHintTextColor(getColor(R.color.light_gray));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Field Required 5 Characters");
                    }
                    enableOrDisableAddressNext();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableOrDisableNext() {
        try {
            if (iscompanyName && iscompanyEmail && iscompPhoneNumber && isBusinessEntity && isSSN && isTimeZone) {
                isBasicNextEnabled = true;
                basicNextCV.setCardBackgroundColor(companyInformationActivity.getResources().getColor(R.color.primary_color));
                viewPager.setPagingEnabled(true);
            } else {
                isBasicNextEnabled = false;
                basicNextCV.setCardBackgroundColor(companyInformationActivity.getResources().getColor(R.color.inactive_color));
                viewPager.setPagingEnabled(false);
            }
        } catch (Exception e) {
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

    class CompanyInforamtionPager extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.basicInfoSL;
                    break;
                case 1:
                    resId = R.id.addressSL;
                    break;
                case 2:
                    resId = R.id.additionalDocsSL;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public int getCount() {
            return 3;
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

    private void chooseBusinessEntityPopup(final Context context) {
        try {
            chooseEntityDialog = new Dialog(context);
            chooseEntityDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            chooseEntityDialog.setContentView(R.layout.activity_choose_business_entity);
            chooseEntityDialog.setCancelable(true);
            Window window = chooseEntityDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            chooseEntityDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            chooseEntityDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            TextView soleLLCTV = chooseEntityDialog.findViewById(R.id.soleLLCTV);
            TextView cCorpTV = chooseEntityDialog.findViewById(R.id.cCorpTV);
            TextView sCorpTV = chooseEntityDialog.findViewById(R.id.sCorpTV);
            TextView partnershipTV = chooseEntityDialog.findViewById(R.id.partnershipTV);
            TextView trustTV = chooseEntityDialog.findViewById(R.id.trustTV);
            TextView llcTV = chooseEntityDialog.findViewById(R.id.llcTV);

            soleLLCTV.setOnClickListener(view -> {
//                ssnET.setVisibility(VISIBLE);
//                ssnET.setSSNTypeText("SSN");
//                businessET.setText(soleLLCTV.getText().toString());
//                isBusinessEntity = true;
//                enableOrDisableNext();
//                SSNTYPE = ssnET.getSSNTypeText();
//                chooseEntityDialog.dismiss();
                setBusinessEntity("SSN", soleLLCTV, chooseEntityDialog);

            });

            cCorpTV.setOnClickListener(view -> {
                setBusinessEntity("EIN/TIN", cCorpTV, chooseEntityDialog);
            });

            sCorpTV.setOnClickListener(view -> {
                setBusinessEntity("SSN", sCorpTV, chooseEntityDialog);
            });

            partnershipTV.setOnClickListener(view -> {
                setBusinessEntity("SSN", partnershipTV, chooseEntityDialog);
            });

            trustTV.setOnClickListener(view -> {
                setBusinessEntity("SSN", trustTV, chooseEntityDialog);
            });

            llcTV.setOnClickListener(view -> {
                setBusinessEntity("SSN", llcTV, chooseEntityDialog);
            });

            chooseEntityDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setBusinessEntity(String type, TextView textView, Dialog dialog) {
        ssnET.setVisibility(VISIBLE);
        ssnET.setSSNTypeText(type);
        businessET.setText(textView.getText().toString());
        isBusinessEntity = true;
        enableOrDisableNext();
        SSNTYPE = ssnET.getSSNTypeText();
        if (SSNTYPE.equals("SSN"))
            identificationType = 11;
        else if (SSNTYPE.equals("EIN/TIN"))
            identificationType = 10;
        dialog.dismiss();
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
            //These are empty view for scrolling
            pageOneView.setVisibility(VISIBLE);
            pageTwoView.setVisibility(VISIBLE);
            pageThreeView.setVisibility(VISIBLE);
        } else {
            pageOneView.setVisibility(GONE);
            pageTwoView.setVisibility(GONE);
            pageThreeView.setVisibility(GONE);
            Utils.isKeyboardVisible = false;
        }
    }

    public void callBasicInfoAPI() {
        PhNoWithCountryCode phone = new PhNoWithCountryCode();
        phone.setCountryCode(Utils.strCCode);
        phone.setPhoneNumber(compphoneNumberET.getUnmaskedText());

        CompanyInfoRequest companyInfoRequest = new CompanyInfoRequest();
        companyInfoRequest.setName(companynameET.getText().toString());
        companyInfoRequest.setEmail(companyemailET.getText().toString());
        companyInfoRequest.setPhoneNumberDto(phone);
        companyInfoRequest.setBusinessEntity(businessET.getText().toString().trim());
        companyInfoRequest.setIdentificationType(identificationType);
        companyInfoRequest.setSsnOrEin(SSNTYPE);
        showProgressDialog();
//        businessIdentityVerificationViewModel.submitBasicCompanyInfo(companyInfoRequest);

    }
}
