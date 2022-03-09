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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.google.gson.Gson;
import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraActivity;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.intro_slider.OneDirectionViewPager;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoUpdateResp;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;
import com.greenbox.coyni.utils.FileUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.SwipeDirection;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.CompanyOutLineBoxPhoneNumberEditText;
import com.greenbox.coyni.utils.outline_et.SSNOutlineBoxNumberEditText;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CompanyInformationActivity extends BaseActivity implements OnKeyboardVisibilityListener {
    public CardView basicNextCV, addressNextCV;
    ImageView close, backIV;
    CompanyOutLineBoxPhoneNumberEditText compphoneNumberET;
    TextInputEditText companynameET, companyemailET, timeZoneET, businessET;
    public TextInputLayout companynametil, companyemailtil, businessTIL, timezoneTIL;
    public LinearLayout companynameErrorLL, companyemailErrorLL, compphoneNumberErrorLL, ssnErrorLL;
    public TextView companynameerrorTV, companyemailerrorTV, compphonenumberTV, ssnErrorTV;
    Dialog chooseEntityDialog;
    public boolean iscompanyName = false, iscompanyEmail = false, iscompPhoneNumber = false, isBusinessEntity = false, isSSN = false, isTimeZone = false, isBasicNextEnabled = false;
    public static CompanyInformationActivity companyInformationActivity;
    CompanyInforamtionPager companyInforamtionPager;
    OneDirectionViewPager viewPager;
    Long mLastClickTime = 0L;

    //Address
    TextInputLayout companyaddresstil, companyaddress2til, citytil, statetil, zipcodetil, countryTIL;
    TextInputEditText companyaddressET, companyaddress2ET, cityET, stateET, zipcodeET;
    LinearLayout address1ErrorLL, address2ErrorLL, cityErrorLL, stateErrorLL, zipcodeErrorLL;
    TextView address1ErrorTV, address2ErrorTV, cityErrorTV, stateErrorTV, zipcodeErrorTV;
    public boolean isCompanyAdress1 = false, isCity = false, isState = false, isZipcode = false, isAddressNextEnabled = false;
    ImageView statedropdownIV;

    public View divider0, divider1, divider2, pageOneView, pageTwoView;
    ConstraintLayout businessEntityCL, timeZoneCL, stateCL;
    public SSNOutlineBoxNumberEditText ssnET;
    public String SSNTYPE = "";
    int identificationType = 0;
    MyApplication objMyApplication;
    public ScrollView basicInfoSL, addressSL;

    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    int selectedPage = 0;

    //Documents
    LinearLayout aoiLL, einLetterLL, w9FormLL, aoiUploadedLL, einLetterUploadedLL, w9FormUploadedLL;
    TextView aoiUploadTV, aoiUpdatedOnTV, einLetterUploadTV, einLetterUpdatedOnTV, w9FormUploadTV, w9FormUpdatedOnTV;
    CardView doneCV;
    public boolean isAOIUploaded = false, isEINLetterUploaded = false, isW9FormUploaded = false, isDocsDoneEnabled = false;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 102;
    private static final int ACTIVITY_CHOOSE_FILE = 3;
    private static final int PICK_IMAGE_REQUEST = 4;
    public static File aoiFile = null, einLetterFile = null, w9FormFile = null;
    public int docTypeID = 0;
    IdentityVerificationViewModel identityVerificationViewModel;
    String selectedDocType = "", from = "";
    CompanyInformationActivity myActivity;
    View globalView;

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
        companynameET.requestFocus();
    }

    public void initFields() {
        try {
            companyInformationActivity = myActivity = this;
            objMyApplication = (MyApplication) getApplicationContext();
            setKeyboardVisibilityListener(CompanyInformationActivity.this);
            businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
            from = getIntent().getStringExtra("FROM");
            showProgressDialog();
            businessIdentityVerificationViewModel.getCompanyInfo();

            basicInfoSL = findViewById(R.id.basicInfoSL);
            addressSL = findViewById(R.id.addressSL);
            divider0 = findViewById(R.id.divider0);
            divider1 = findViewById(R.id.divider1);
            divider2 = findViewById(R.id.divider2);
//            pageOneView = findViewById(R.id.pageOneView);
//            pageTwoView = findViewById(R.id.pageTwoView);

            //Documents
            aoiLL = findViewById(R.id.aoiLL);
            einLetterLL = findViewById(R.id.einLetterLL);
            w9FormLL = findViewById(R.id.w9FormLL);
            aoiUploadedLL = findViewById(R.id.aoiUploadedLL);
            einLetterUploadedLL = findViewById(R.id.einLetterUploadedLL);
            w9FormUploadedLL = findViewById(R.id.w9FormUploadedLL);
            aoiUploadTV = findViewById(R.id.aoiUploadTV);
            aoiUpdatedOnTV = findViewById(R.id.aoiUpdatedOnTV);
            einLetterUploadTV = findViewById(R.id.einLetterUploadTV);
            einLetterUpdatedOnTV = findViewById(R.id.einLetterUpdatedOnTV);
            w9FormUploadTV = findViewById(R.id.w9FormUploadTV);
            w9FormUpdatedOnTV = findViewById(R.id.w9FormUpdatedOnTV);
            doneCV = findViewById(R.id.doneCV);

            //Basic info
            companyemailET = findViewById(R.id.companyemailET);
            companyemailtil = findViewById(R.id.companyemailTIL);
            timezoneTIL = findViewById(R.id.timezoneTIL);
            businessTIL = findViewById(R.id.businessTIL);

            companynameET = findViewById(R.id.companynameET);
            companynametil = findViewById(R.id.companynameTIL);

            companynameerrorTV = findViewById(R.id.companyNameErrorTV);
            companynameErrorLL = findViewById(R.id.companyNameErrorLL);

            companyemailerrorTV = findViewById(R.id.companyemailErrorTV);
            companyemailErrorLL = findViewById(R.id.companyemailErrorLL);

            compphonenumberTV = findViewById(R.id.CompanyphoneNumberErrorTV);
            compphoneNumberET = findViewById(R.id.CompanyphoneNumberOET);
            compphoneNumberET.setFrom("Company_Information", this);
            compphoneNumberET.setHintText("Company Phone Number");
            compphoneNumberErrorLL = findViewById(R.id.CompanyphoneNumberErrorLL);
            ssnErrorLL = findViewById(R.id.ssnErrorLL);
            ssnErrorTV = findViewById(R.id.ssnErrorTV);

            businessEntityCL = findViewById(R.id.businessEntityCL);
            timeZoneCL = findViewById(R.id.timeZoneCL);
            stateCL = findViewById(R.id.stateCL);
            businessET = findViewById(R.id.businessET);
            timeZoneET = findViewById(R.id.timeZoneET);
            ssnET = findViewById(R.id.ssnOutLineBoxET);
            ssnET.setFrom("CompanyInfo", this);

            basicNextCV = findViewById(R.id.basicNextCV);

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

            close = findViewById(R.id.closeIV);
            backIV = findViewById(R.id.backIV);

            companyInforamtionPager = new CompanyInforamtionPager();
            viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(companyInforamtionPager);
            viewPager.setAllowedSwipeDirection(SwipeDirection.NONE);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.e("onPageScrolled", "onPageScrolled " + position);

                }

                @Override
                public void onPageSelected(int position) {
                    selectedPage = position;
                    if (position == 0) {
                        enableOrDisableNext();
                        close.setVisibility(VISIBLE);
                        backIV.setVisibility(GONE);
                        divider0.setBackgroundResource(R.drawable.button_background);
                        divider1.setBackgroundResource(R.drawable.button_background1);
                        divider2.setBackgroundResource(R.drawable.button_background1);

                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(CompanyInformationActivity.this);

                    } else if (position == 1) {
                        enableOrDisableAddressNext();
                        close.setVisibility(GONE);
                        backIV.setVisibility(VISIBLE);
                        divider0.setBackgroundResource(R.drawable.button_background1);
                        divider1.setBackgroundResource(R.drawable.button_background);
                        divider2.setBackgroundResource(R.drawable.button_background1);

                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(CompanyInformationActivity.this);
                    } else if (position == 2) {
                        enableOrDisableDocsDone();
                        close.setVisibility(GONE);
                        backIV.setVisibility(VISIBLE);
                        divider0.setBackgroundResource(R.drawable.button_background1);
                        divider1.setBackgroundResource(R.drawable.button_background1);
                        divider2.setBackgroundResource(R.drawable.button_background);

                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(CompanyInformationActivity.this);

                        if (SSNTYPE.equals("SSN")) {
                            aoiLL.setVisibility(GONE);
                            einLetterLL.setVisibility(GONE);
                            w9FormLL.setVisibility(VISIBLE);

                        } else {
                            aoiLL.setVisibility(VISIBLE);
                            einLetterLL.setVisibility(VISIBLE);
                            w9FormLL.setVisibility(VISIBLE);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            companynametil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            companyemailtil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            businessTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            timezoneTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));

            companyaddresstil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            companyaddress2til.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            citytil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            statetil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            zipcodetil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
            countryTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));


            divider0.setOnClickListener(view -> {
                close.setVisibility(VISIBLE);
                backIV.setVisibility(GONE);
                viewPager.setCurrentItem(0);
                divider0.setBackgroundResource(R.drawable.button_background);
                divider1.setBackgroundResource(R.drawable.button_background1);
                divider2.setBackgroundResource(R.drawable.button_background1);
            });

            divider1.setOnClickListener(view -> {
                if (isBasicNextEnabled) {
                    close.setVisibility(GONE);
                    backIV.setVisibility(VISIBLE);
                    viewPager.setCurrentItem(1);
                    divider0.setBackgroundResource(R.drawable.button_background1);
                    divider1.setBackgroundResource(R.drawable.button_background);
                    divider2.setBackgroundResource(R.drawable.button_background1);
                }
            });

            divider2.setOnClickListener(view -> {
                if (isBasicNextEnabled & isAddressNextEnabled) {
                    close.setVisibility(GONE);
                    backIV.setVisibility(VISIBLE);
                    viewPager.setCurrentItem(2);
                    divider0.setBackgroundResource(R.drawable.button_background1);
                    divider1.setBackgroundResource(R.drawable.button_background1);
                    divider2.setBackgroundResource(R.drawable.button_background);
                }
            });

            businessTIL.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                chooseBusinessEntityPopup(CompanyInformationActivity.this, businessET);

            });

            businessET.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(this);
                chooseBusinessEntityPopup(CompanyInformationActivity.this, businessET);

            });

            close.setOnClickListener(v -> finish());

            backIV.setOnClickListener(v -> {
                if (selectedPage == 1) {
                    close.setVisibility(VISIBLE);
                    backIV.setVisibility(GONE);
                    viewPager.setCurrentItem(0);
                    divider0.setBackgroundResource(R.drawable.button_background);
                    divider1.setBackgroundResource(R.drawable.button_background1);
                    divider2.setBackgroundResource(R.drawable.button_background1);
                } else if (selectedPage == 2) {
                    close.setVisibility(GONE);
                    backIV.setVisibility(VISIBLE);
                    viewPager.setCurrentItem(1);
                    divider0.setBackgroundResource(R.drawable.button_background1);
                    divider1.setBackgroundResource(R.drawable.button_background);
                    divider2.setBackgroundResource(R.drawable.button_background1);
                }
            });

            basicNextCV.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isBasicNextEnabled) {
                    companyInfoAPICall(prepareRequest());
                }
            });

            addressNextCV.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isAddressNextEnabled) {
                    companyInfoAPICall(prepareRequest());
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

//            aoiLL.setOnClickListener(view -> {
//                aoiClick();
//            });
//
//            einLetterLL.setOnClickListener(view -> {
//               einLetterClick();
//            });
//
//            w9FormLL.setOnClickListener(view -> {
//                w9FormClick();
//            });

            doneCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    if (isDocsDoneEnabled) {
                        businessIdentityVerificationViewModel.postCompanyInfo(prepareRequest());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers() {

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

        try {
            businessIdentityVerificationViewModel.getGetCompanyInfoResponse().observe(this, new Observer<CompanyInfoResp>() {
                @Override
                public void onChanged(CompanyInfoResp companyInfoResp) {
                    dismissDialog();
                    if (companyInfoResp != null) {
                        if (companyInfoResp.getStatus().toLowerCase().toString().equals("success")) {
                            try {
                                CompanyInfoResp.Data cir = companyInfoResp.getData();
                                if (cir.getName() != null && !cir.getName().equals("")) {
                                    companynameET.setText(cir.getName());
                                    iscompanyName = true;
                                    companynameET.setSelection(cir.getName().length());
                                }

                                if (cir.getEmail() != null && !cir.getEmail().equals("")) {
                                    companyemailET.setText(cir.getEmail());
                                    iscompanyEmail = true;
                                    companyemailET.setSelection(cir.getEmail().length());
                                }


                                if (cir.getPhoneNumberDto() != null && cir.getPhoneNumberDto().getPhoneNumber() != null && !cir.getPhoneNumberDto().getPhoneNumber().equals("")) {
                                    compphoneNumberET.setText(cir.getPhoneNumberDto().getPhoneNumber());
                                    iscompPhoneNumber = true;
                                    compphoneNumberET.setSelection();
                                }

                                if (cir.getBusinessEntity() != null && !cir.getBusinessEntity().equals("")) {
                                    businessET.setText(cir.getBusinessEntity());
                                    isBusinessEntity = true;
                                }

                                if (cir.getIdentificationType() != null && !cir.getIdentificationType().equals("") && !cir.getIdentificationType().equals("0")) {
                                    if (cir.getIdentificationType().equals("10")) {
                                        SSNTYPE = "EIN/TIN";
                                    } else if (cir.getIdentificationType().equals("11")) {
                                        SSNTYPE = "SSN";
                                    }
                                    identificationType = Integer.parseInt(cir.getIdentificationType());
                                    ssnET.setSSNTypeText(SSNTYPE);
                                    ssnET.setVisibility(VISIBLE);
                                }

                                if (cir.getSsnOrEin() != null && !cir.getSsnOrEin().equals("")) {
                                    ssnET.setText(cir.getSsnOrEin());
                                    ssnET.setVisibility(VISIBLE);
                                    isSSN = true;
                                    ssnET.setSelection();
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

                                if (cir.getRequiredDocumets() != null && cir.getRequiredDocumets().size() > 0) {
                                    for (int i = 0; i < cir.getRequiredDocumets().size(); i++) {
                                        if (cir.getRequiredDocumets().get(i).getIdentityId() == 5) {
//                                            if (!SSNTYPE.equals("SSN")) {
                                            aoiUploadTV.setVisibility(GONE);
                                            aoiUploadedLL.setVisibility(VISIBLE);
                                            aoiUpdatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
                                            isAOIUploaded = true;
//                                            }
                                        } else if (cir.getRequiredDocumets().get(i).getIdentityId() == 6) {
//                                            if (!SSNTYPE.equals("SSN")) {
                                            einLetterUploadTV.setVisibility(GONE);
                                            einLetterUploadedLL.setVisibility(VISIBLE);
                                            einLetterUpdatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
                                            isEINLetterUploaded = true;
//                                            }
                                        } else if (cir.getRequiredDocumets().get(i).getIdentityId() == 7 || cir.getRequiredDocumets().get(i).getIdentityId() == 11) {
                                            w9FormUploadTV.setVisibility(GONE);
                                            w9FormUploadedLL.setVisibility(VISIBLE);
                                            w9FormUpdatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDate(cir.getRequiredDocumets().get(i).getUpdatedAt()));
                                            isW9FormUploaded = true;
                                        }
                                    }
                                }

                                if (selectedPage == 0)
                                    enableOrDisableNext();
                                else if (selectedPage == 1)
                                    enableOrDisableAddressNext();
                                else if (selectedPage == 2)
                                    enableOrDisableDocsDone();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getUpdateBasicCompanyInfoResponse().observe(this, new Observer<CompanyInfoUpdateResp>() {
                @Override
                public void onChanged(CompanyInfoUpdateResp companyInfoResponse) {

                    if (dialog != null)
                        dismissDialog();
                    if (companyInfoResponse != null) {
                        if (companyInfoResponse.getStatus().toLowerCase().toString().equals("success")) {
                            close.setVisibility(GONE);
                            backIV.setVisibility(VISIBLE);

                            if (selectedPage == 0) {
                                viewPager.setCurrentItem(1);
                                close.setVisibility(GONE);
                                backIV.setVisibility(VISIBLE);
                                divider0.setBackgroundResource(R.drawable.button_background1);
                                divider1.setBackgroundResource(R.drawable.button_background);
                                divider2.setBackgroundResource(R.drawable.button_background1);
                            } else if (selectedPage == 1) {
                                viewPager.setCurrentItem(2);
                                close.setVisibility(GONE);
                                backIV.setVisibility(VISIBLE);
                                divider0.setBackgroundResource(R.drawable.button_background1);
                                divider1.setBackgroundResource(R.drawable.button_background1);
                                divider2.setBackgroundResource(R.drawable.button_background);
                            }
                        } else {
                            Utils.displayAlert(companyInfoResponse.getError().getErrorDescription(),
                                    CompanyInformationActivity.this, "", companyInfoResponse.getError().getFieldErrors().get(0));
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
                    dismissDialog();
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        if (docTypeID == 5) {
                            aoiUploadTV.setVisibility(GONE);
                            aoiUploadedLL.setVisibility(VISIBLE);
                            String dateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis()));
                            aoiUpdatedOnTV.setText("Uploaded on " + dateString);
                            aoiUpdatedOnTV.setVisibility(VISIBLE);
                            isAOIUploaded = true;
                        } else if (docTypeID == 6) {
                            einLetterUploadTV.setVisibility(GONE);
                            einLetterUploadedLL.setVisibility(VISIBLE);
                            String dateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis()));
                            einLetterUpdatedOnTV.setText("Uploaded on " + dateString);
                            einLetterUpdatedOnTV.setVisibility(VISIBLE);
                            isEINLetterUploaded = true;
                        } else if (docTypeID == 7 || docTypeID == 11) {
                            w9FormUploadTV.setVisibility(GONE);
                            w9FormUploadedLL.setVisibility(VISIBLE);
                            String dateString = new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis()));
                            w9FormUpdatedOnTV.setText("Uploaded on " + dateString);
                            w9FormUpdatedOnTV.setVisibility(VISIBLE);
                            isW9FormUploaded = true;
                        }

                        enableOrDisableDocsDone();
                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), CompanyInformationActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
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
                        showProgressDialog();
                        RequestBody requestBody = null;
                        MultipartBody.Part idFile = null;
                        if (docTypeID == 5) {
                            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), aoiFile);
                            idFile = MultipartBody.Part.createFormData("identityFile", aoiFile.getName(), requestBody);
                        } else if (docTypeID == 6) {
                            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), einLetterFile);
                            idFile = MultipartBody.Part.createFormData("identityFile", einLetterFile.getName(), requestBody);
                        } else if (docTypeID == 7 || docTypeID == 11) {
                            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), w9FormFile);
                            idFile = MultipartBody.Part.createFormData("identityFile", w9FormFile.getName(), requestBody);
                        }
                        RequestBody idType = RequestBody.create(MediaType.parse("text/plain"), docTypeID + "");
                        RequestBody idNumber = RequestBody.create(MediaType.parse("text/plain"), ssnET.getText());
                        identityVerificationViewModel.uploadIdentityImage(idFile, idType, idNumber);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getPostCompanyInfoResponse().observe(this, new Observer<CompanyInfoUpdateResp>() {
                @Override
                public void onChanged(CompanyInfoUpdateResp companyInfoResponse) {

                    if (dialog != null)
                        dismissDialog();
                    if (companyInfoResponse != null) {
                        if (companyInfoResponse.getStatus().toLowerCase().toString().equals("success")) {
                            finish();
                        } else {
                            Utils.displayAlert(companyInfoResponse.getError().getErrorDescription(),
                                    CompanyInformationActivity.this, "", companyInfoResponse.getError().getFieldErrors().get(0));
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
                            companynametil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            companynameET.setHintTextColor(getColor(R.color.light_gray));
                            Utils.setUpperHintColor(companynametil, getColor(R.color.primary_black));

                        } else if (companynameET.getText().toString().trim().length() == 1) {
                            companynametil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            Utils.setUpperHintColor(companynametil, getColor(R.color.error_red));
                            companynameErrorLL.setVisibility(VISIBLE);
                            companynameerrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            Utils.setUpperHintColor(companynametil, getColor(R.color.light_gray));
                            companynametil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
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
                        if ((companyemailET.getText().toString().trim().length() > 5 && !Utils.isValidEmail(companyemailET.getText().toString().trim())) || (companyemailET.getText().toString().trim().length() <= 5 && companyemailET.getText().toString().trim().length() > 0)) {
                            companyemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            Utils.setUpperHintColor(companyemailtil, getColor(R.color.error_red));
                            companyemailErrorLL.setVisibility(VISIBLE);
                            companyemailerrorTV.setText("Please Enter a Valid Email");
                        } else if (companyemailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(companyemailET.getText().toString().trim())) {
                            companyemailtil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            Utils.setUpperHintColor(companyemailtil, getColor(R.color.primary_black));
                            companyemailErrorLL.setVisibility(GONE);
                        } else {
                            companyemailtil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            Utils.setUpperHintColor(companyemailtil, getColor(R.color.light_gray));
                            companyemailErrorLL.setVisibility(VISIBLE);
                            companyemailerrorTV.setText("Field Required");
                        }

                    } else {
                        companyemailET.setHint("Company’s Name");
                        companyemailET.setHint(getResources().getString(R.string.company_s_email));
                        companyemailtil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(companyemailtil, getColor(R.color.primary_green));
                        companyemailErrorLL.setVisibility(GONE);
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
                        companyaddressET.setHint(getResources().getString(R.string.company_address_line_1));
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
                        companyaddress2ET.setHint(getResources().getString(R.string.company_address_line_2_optional));
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
                        zipcodeET.requestFocus();
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(CompanyInformationActivity.this);

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
            companynameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 1) {
                        iscompanyName = true;
                        companynametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(companynametil, getResources().getColor(R.color.primary_green));
                    } else {
//                        companynameerrorTV.setText("Field Required");
                        iscompanyName = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String str = companynameET.getText().toString();
                        if (str.length() > 0 && str.toString().trim().length() == 0) {
                            companynameET.setText("");
                            companynameET.setSelection(companynameET.getText().length());
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            companynameET.setText(str.trim());
                        } else if (str.length() > 0 && str.contains(".")) {
                            companynameET.setText(companynameET.getText().toString().replaceAll("\\.", ""));
                            companynameET.setSelection(companynameET.getText().length());
                        } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                            companynameET.setText("");
                            companynameET.setSelection(companynameET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
                        Utils.setUpperHintColor(companyemailtil, getResources().getColor(R.color.primary_black));

                    } else if (companyemailET.getText().toString().trim().length() == 0) {
//                        companyemailErrorLL.setVisibility(VISIBLE);
//                        companyemailerrorTV.setText("Field Required");
                        iscompanyEmail = false;
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

            businessET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.length() > 0) {
                            Utils.setUpperHintColor(businessTIL, getResources().getColor(R.color.primary_black));
                            isBusinessEntity = true;
                        } else {
                            isBusinessEntity = false;
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
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            companyaddressET.setText(str.trim());
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
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            companyaddress2ET.setText(str.trim());
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
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            cityET.setText(str.trim());
                        } else if (str.length() > 0 && str.substring(0).equals(" ")) {
                            cityET.setText("");
                            cityET.setSelection(cityET.getText().length());
                            cityErrorLL.setVisibility(GONE);
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
//                            statetil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
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
            if (iscompanyName && iscompanyEmail && iscompPhoneNumber && isBusinessEntity && isSSN) {
                isBasicNextEnabled = true;
                basicNextCV.setCardBackgroundColor(companyInformationActivity.getResources().getColor(R.color.primary_color));
                viewPager.setAllowedSwipeDirection(SwipeDirection.RIGHT);
            } else {
                isBasicNextEnabled = false;
                basicNextCV.setCardBackgroundColor(companyInformationActivity.getResources().getColor(R.color.inactive_color));
                viewPager.setAllowedSwipeDirection(SwipeDirection.NONE);
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
                viewPager.setAllowedSwipeDirection(SwipeDirection.ALL);
            } else {
                isAddressNextEnabled = false;
                addressNextCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                viewPager.setAllowedSwipeDirection(SwipeDirection.LEFT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableOrDisableDocsDone() {

        viewPager.setAllowedSwipeDirection(SwipeDirection.ALL);

        if (SSNTYPE.equals("SSN")) {
            try {
                if (isW9FormUploaded) {
                    isDocsDoneEnabled = true;
                    doneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                } else {
                    isDocsDoneEnabled = false;
                    doneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (SSNTYPE.equals("EIN/TIN")) {
            try {
                if (isAOIUploaded && isEINLetterUploaded && isW9FormUploaded) {
                    isDocsDoneEnabled = true;
                    doneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                } else {
                    isDocsDoneEnabled = false;
                    doneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                    resId = R.id.additionalDocsRL;
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

    private void chooseBusinessEntityPopup(final Context context, EditText editText) {
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
                if (!soleLLCTV.getText().toString().equals(editText.getText().toString()))
                    setBusinessEntity("SSN", soleLLCTV, chooseEntityDialog);
                else
                    chooseEntityDialog.dismiss();

                ssnET.requestETFocus();
            });

            cCorpTV.setOnClickListener(view -> {
                if (!cCorpTV.getText().toString().equals(editText.getText().toString()))
                    setBusinessEntity("EIN/TIN", cCorpTV, chooseEntityDialog);
                else
                    chooseEntityDialog.dismiss();
                ssnET.requestETFocus();
            });

            sCorpTV.setOnClickListener(view -> {
                if (!sCorpTV.getText().toString().equals(editText.getText().toString()))
                    setBusinessEntity("EIN/TIN", sCorpTV, chooseEntityDialog);
                else
                    chooseEntityDialog.dismiss();
                ssnET.requestETFocus();
            });

            partnershipTV.setOnClickListener(view -> {
                if (!partnershipTV.getText().toString().equals(editText.getText().toString()))
                    setBusinessEntity("EIN/TIN", partnershipTV, chooseEntityDialog);
                else
                    chooseEntityDialog.dismiss();
                ssnET.requestETFocus();
            });

            trustTV.setOnClickListener(view -> {
                if (!trustTV.getText().toString().equals(editText.getText().toString()))
                    setBusinessEntity("EIN/TIN", trustTV, chooseEntityDialog);
                else
                    chooseEntityDialog.dismiss();
                ssnET.requestETFocus();
            });

            llcTV.setOnClickListener(view -> {
                if (!llcTV.getText().toString().equals(editText.getText().toString()))
                    setBusinessEntity("EIN/TIN", llcTV, chooseEntityDialog);
                else
                    chooseEntityDialog.dismiss();
                ssnET.requestETFocus();
            });

            chooseEntityDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setBusinessEntity(String type, TextView textView, Dialog dialog) {
        ssnET.setVisibility(VISIBLE);
        ssnET.setText("");
        ssnET.setSSNTypeText(type);
        businessET.setText(textView.getText().toString());
        isBusinessEntity = true;
        enableOrDisableNext();
        SSNTYPE = ssnET.getSSNTypeText();
        if (SSNTYPE.equals("SSN")) {
            identificationType = 11;
        } else if (SSNTYPE.equals("EIN/TIN")) {
            identificationType = 10;
        }

        w9FormUploadTV.setVisibility(VISIBLE);
        w9FormUploadedLL.setVisibility(GONE);
        w9FormUpdatedOnTV.setVisibility(GONE);
        isW9FormUploaded = false;

        aoiUploadTV.setVisibility(VISIBLE);
        aoiUploadedLL.setVisibility(GONE);
        aoiUpdatedOnTV.setVisibility(GONE);
        isAOIUploaded = false;

        einLetterUploadTV.setVisibility(VISIBLE);
        einLetterUploadedLL.setVisibility(GONE);
        einLetterUpdatedOnTV.setVisibility(GONE);
        isEINLetterUploaded = false;

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
//            pageOneView.setVisibility(VISIBLE);
//            pageTwoView.setVisibility(VISIBLE);
        } else {
//            pageOneView.setVisibility(GONE);
//            pageTwoView.setVisibility(GONE);
            Utils.isKeyboardVisible = false;
        }
    }

    public CompanyInfoRequest prepareRequest() {
        CompanyInfoRequest companyInfoRequest = new CompanyInfoRequest();

        try {
            //Basic
//            if (isBasicNextEnabled) {

            //Phone
            PhNoWithCountryCode phone = new PhNoWithCountryCode();
            phone.setCountryCode(Utils.strCCode);
            phone.setPhoneNumber(compphoneNumberET.getUnmaskedText().trim());
            if (phone.getCountryCode() != null && phone.getPhoneNumber().length() == 10)
                companyInfoRequest.setPhoneNumberDto(phone);
            //name
            if (companynameET.getText().toString().trim().length() > 1)
                companyInfoRequest.setName(companynameET.getText().toString().trim());
            //Email
            if (Utils.isValidEmail(companyemailET.getText().toString().trim()))
                companyInfoRequest.setEmail(companyemailET.getText().toString().trim());
            //Business Entity
            if (businessET.getText().toString().trim().length() > 1)
                companyInfoRequest.setBusinessEntity(businessET.getText().toString().trim());
            //Identfication ID and SSN
            if (identificationType != 0) {
                companyInfoRequest.setIdentificationType(identificationType);
            }

            if (ssnET.getText().trim().length() == 9)
                companyInfoRequest.setSsnOrEin(ssnET.getText().trim());
//            }

            //Address
//            if (isAddressNextEnabled) {
            if (companyaddressET.getText().toString().trim().length() > 0)
                companyInfoRequest.setAddressLine1(companyaddressET.getText().toString().trim());
            if (companyaddress2ET.getText().toString().trim().length() > 0)
                companyInfoRequest.setAddressLine2(companyaddress2ET.getText().toString().trim());
            if (cityET.getText().toString().trim().length() > 0)
                companyInfoRequest.setCity(cityET.getText().toString().trim());
            if (stateET.getText().toString().trim().length() > 0)
                companyInfoRequest.setState(stateET.getText().toString().trim());
            if (zipcodeET.getText().toString().trim().length() >= 5)
                companyInfoRequest.setZipCode(zipcodeET.getText().toString().trim());
            companyInfoRequest.setCountry("us");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return companyInfoRequest;
    }

    public void companyInfoAPICall(CompanyInfoRequest companyInfoRequest) {
        businessIdentityVerificationViewModel.patchCompanyInfo(companyInfoRequest);
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
//            if(isBasicNextEnabled && isAddressNextEnabled && isDocsDoneEnabled){
//                businessIdentityVerificationViewModel.postCompanyInfo(companyInfoRequest);
//            }else{
//
//            }
            companyInfoAPICall(prepareRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedPage == 0) {
            super.onBackPressed();
        } else if (selectedPage == 1) {
            close.setVisibility(VISIBLE);
            backIV.setVisibility(GONE);
            viewPager.setCurrentItem(0);
            divider0.setBackgroundResource(R.drawable.button_background);
            divider1.setBackgroundResource(R.drawable.button_background1);
            divider2.setBackgroundResource(R.drawable.button_background1);

        } else if (selectedPage == 2) {
            close.setVisibility(GONE);
            backIV.setVisibility(VISIBLE);
            viewPager.setCurrentItem(1);
            divider0.setBackgroundResource(R.drawable.button_background1);
            divider1.setBackgroundResource(R.drawable.button_background);
            divider2.setBackgroundResource(R.drawable.button_background1);

        }
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
                        Utils.displayAlert("Requires Access to Camera.", CompanyInformationActivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", CompanyInformationActivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", CompanyInformationActivity.this, "", "");

                    } else {
//                        startActivity(new Intent(this, CameraActivity.class));
//                        chooseFilePopup(this, selectedDocType);
                        if (Utils.isKeyboardVisible)
                            Utils.hideKeypad(CompanyInformationActivity.this);

                        if (selectedDocType.equals("CI-AOI")) {
                            aoiClick(findViewById(R.id.aoiLL).getRootView());
                        } else if (selectedDocType.equals("CI-EINLETTER")) {
                            einLetterClick(findViewById(R.id.einLetterLL).getRootView());
                        } else if (selectedDocType.equals("CI-W9")) {
                            w9FormClick(findViewById(R.id.w9FormLL).getRootView());
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAndUploadAdditionalDoc(int docID) {
        docTypeID = docID;
        identityVerificationViewModel.removeIdentityImage(docTypeID + "");
    }

    private void chooseFilePopup(final Context context, String type) {
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
                startActivity(new Intent(CompanyInformationActivity.this, CameraActivity.class).putExtra("FROM", type));

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
            if (selectedDocType.equals("CI-AOI")) {
                aoiFile = mediaFile;
                removeAndUploadAdditionalDoc(5);
            } else if (selectedDocType.equals("CI-EINLETTER")) {
                einLetterFile = mediaFile;
                removeAndUploadAdditionalDoc(6);
            } else if (selectedDocType.equals("CI-W9")) {
                w9FormFile = mediaFile;
                if (SSNTYPE.equals("SSN"))
                    removeAndUploadAdditionalDoc(11);
                else if (SSNTYPE.equals("EIN/TIN"))
                    removeAndUploadAdditionalDoc(7);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void aoiClick(View view) {
        if (checkAndRequestPermissions(CompanyInformationActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            selectedDocType = "CI-AOI";
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(this);
            chooseFilePopup(this, selectedDocType);
        }
    }

    public void einLetterClick(View view) {
        if (checkAndRequestPermissions(CompanyInformationActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            selectedDocType = "CI-EINLETTER";
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(this);
            chooseFilePopup(this, selectedDocType);
        }
    }

    public void w9FormClick(View view) {
        if (checkAndRequestPermissions(CompanyInformationActivity.this)) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            selectedDocType = "CI-W9";
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(this);
            chooseFilePopup(this, selectedDocType);
        }
    }
}
