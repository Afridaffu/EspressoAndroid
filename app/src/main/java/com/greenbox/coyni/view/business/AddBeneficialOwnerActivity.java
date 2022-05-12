package com.greenbox.coyni.view.business;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
//import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.greenbox.coyni.model.BeneficialOwners.BOPatchResp;
import com.greenbox.coyni.model.BeneficialOwners.BORequest;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.outline_et.SSNBOEditText;
import com.greenbox.coyni.view.BaseActivity;
import com.greenbox.coyni.view.BuyTokenPaymentMethodsActivity;
import com.greenbox.coyni.view.IdentityVerificationActivity;
import com.greenbox.coyni.view.PayRequestActivity;
import com.greenbox.coyni.viewmodel.BusinessIdentityVerificationViewModel;
import com.ideyalabs.wheelpicker.DatePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddBeneficialOwnerActivity extends BaseActivity implements OnKeyboardVisibilityListener {

    TextInputLayout fnametil, lnametil, dobtil, ownershiptil, address1TIL, address2TIL, cityTIL, stateTIL, zipcodeTIL, countryTIL;
    TextInputEditText fnameET, lnameET, dobET, ownershipET, address1ET, address2ET, cityET, stateET, zipcodeET;
    public LinearLayout fnameLL, lnameLL, dobLL, ssnLL, ownershipLL, uploadLL, address1ErrorLL, address2ErrorLL, cityErrorLL, zipcodeErrorLL, uploadedLL;
    ConstraintLayout stateCL;
    public TextView uploadTV, fnameTV, lnameTV, dobTV, ssnTV, ownershipTV, address1ErrorTV, address2ErrorTV, cityErrorTV, zipcodeErrorTV,
            updatedOnTV, uploadedTV;
    public static CardView nextcv, doneCV;
    ImageView closeIV, backIV;
    View divider1, divider2;
    public static boolean isfname = false, islname = false, isssn = false, isownership = false, isNextEnabled = false,
            isDOBSelected = false, isAddress1 = false, isAddress2 = false, isCity = false, isState = false,
            isZipcode = false, isSaveEnabled = false, isFileUploaded = false;
    private boolean isCPwdEye = false;
    public String dateOfBirth = "";
    int mYear, mMonth, mDay;
    private DatePicker datepicker;
    Long mLastClickTime = 0L;
    BOPagerAdapter boPagerAdapter;
    static AutoScrollViewPager viewPager;
    int pagerPosition = 0, diffMonths = -1, selectedPage = 0;
    RelativeLayout layoutUpload, layoutMailingAddress;
    String fromScreen = "";
    int boID = -1, totalOwnerShipPerc = 0;
    MyApplication objMyApplication;
    BusinessIdentityVerificationViewModel businessIdentityVerificationViewModel;
    AddBeneficialOwnerActivity myActivity;
    public static AddBeneficialOwnerActivity addBeneficialOwnerActivity;
    public static File identityFile;
    public static int identityType = 0, existingIdentityType = -1;
    public static boolean isFileSelected = false;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    SSNBOEditText ssnET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_add_benificial_owner);

        initFields();
        initObservers();
        textWatchers();
        focusWatchers();

    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            fnameET.requestFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initFields() {
        businessIdentityVerificationViewModel = new ViewModelProvider(this).get(BusinessIdentityVerificationViewModel.class);
        objMyApplication = (MyApplication) getApplicationContext();
        addBeneficialOwnerActivity = myActivity = this;
        setKeyboardVisibilityListener(AddBeneficialOwnerActivity.this);

        boPagerAdapter = new BOPagerAdapter();
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(boPagerAdapter);
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
                    divider1.setBackgroundResource(R.drawable.button_background);
                    divider2.setBackgroundResource(R.drawable.button_background1);
                    fnameET.requestFocus();
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(AddBeneficialOwnerActivity.this);

                } else if (position == 1) {
                    closeIV.setVisibility(GONE);
                    backIV.setVisibility(VISIBLE);
                    divider1.setBackgroundResource(R.drawable.button_background1);
                    divider2.setBackgroundResource(R.drawable.button_background);
//                    address1ET.requestFocus();
//                    if (!Utils.isKeyboardVisible)
//                        Utils.shwForcedKeypad(AddBeneficialOwnerActivity.this);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fromScreen = getIntent().getStringExtra("FROM");
        boID = getIntent().getIntExtra("ID", -1);

        businessIdentityVerificationViewModel.getBeneficialOwners();

        divider1 = findViewById(R.id.divider1);
        divider2 = findViewById(R.id.divider2);
        layoutUpload = findViewById(R.id.layoutUpload);
        layoutMailingAddress = findViewById(R.id.layoutMailingAddress);
        closeIV = findViewById(R.id.closeIV);
        backIV = findViewById(R.id.backIV);
        uploadLL = findViewById(R.id.UploadLL);
        fnametil = findViewById(R.id.FirstNameTIL);
        lnametil = findViewById(R.id.LastNameTIL);
        dobtil = findViewById(R.id.IdveriDOBTIL);
        ownershiptil = findViewById(R.id.OwnershipTIL);

        fnameET = findViewById(R.id.FirstNameET);
        lnameET = findViewById(R.id.LastNameET);
        dobET = findViewById(R.id.IdveriDOBET);
        ssnET = findViewById(R.id.ssnOutLineBoxET);
        ssnET.setFrom("ADD_BO", this);
        ownershipET = findViewById(R.id.OwnershipET);

        fnameLL = findViewById(R.id.FirstNameErrorLL);
        lnameLL = findViewById(R.id.LastNameErrorLL);
        dobLL = findViewById(R.id.IdveriDOBLL);
        ssnLL = findViewById(R.id.SsnErrorLL);
        ownershipLL = findViewById(R.id.OwnershipErrorLL);
        uploadTV = findViewById(R.id.uploadTV);
        updatedOnTV = findViewById(R.id.updatedOnTV);
        uploadedTV = findViewById(R.id.uploadedTV);
        uploadedLL = findViewById(R.id.uploadedLL);

        fnameTV = findViewById(R.id.FirstNameErrorTV);
        lnameTV = findViewById(R.id.LastNameErrorTV);
        dobTV = findViewById(R.id.IdveriDOBTV);
        ssnTV = findViewById(R.id.SsnErrorTV);
        ownershipTV = findViewById(R.id.OwnershipErrorTV);
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

        address1ErrorLL = findViewById(R.id.mailing_addressErrorLL);
        address1ErrorTV = findViewById(R.id.mailing_addressErrorTV);

        address2ErrorLL = findViewById(R.id.m_a_optionalErrorLL);
        address2ErrorTV = findViewById(R.id.m_a_optionalErrorTV);

        cityErrorLL = findViewById(R.id.City_ErrorLL);
        cityErrorTV = findViewById(R.id.City_ErrorTV);

        zipcodeErrorLL = findViewById(R.id.Zip_ErrorLL);
        zipcodeErrorTV = findViewById(R.id.Zip_ErrorTV);
        countryTIL = findViewById(R.id.Country_TIL);

        fnametil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        lnametil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        dobtil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        ownershiptil.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));


        address1TIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        cityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        stateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
        countryTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));


        doneCV = findViewById(R.id.doneCV);
        dobtil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(AddBeneficialOwnerActivity.this);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ssnET.clearFocus();
//                setToDate(dobET);
                setToDateWheelPicker(dobET);
            }
        });

        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(AddBeneficialOwnerActivity.this);
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                ssnET.clearFocus();
//                setToDate(dobET);
                setToDateWheelPicker(dobET);
            }
        });

        dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    ssnET.clearFocus();
//                    setToDate(dobET);
                    setToDateWheelPicker(dobET);
                }
            }
        });

        uploadLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (checkAndRequestPermissions(AddBeneficialOwnerActivity.this)) {
                    ssnET.clearFocus();
                    showIdentityTypePopup(AddBeneficialOwnerActivity.this);
                }

            }
        });

//        closeIV.setOnClickListener(v -> finish());
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (fromScreen != null && !fromScreen.equals("EDIT_BO")) {
                        confirmationAlert();
                    } else {
                        finish();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        backIV.setOnClickListener(v -> {
            if (selectedPage == 1) {
                closeIV.setVisibility(VISIBLE);
                backIV.setVisibility(GONE);
                viewPager.setCurrentItem(0);
                divider1.setBackgroundResource(R.drawable.button_background);
                divider2.setBackgroundResource(R.drawable.button_background1);
            } else if (selectedPage == 2) {
                closeIV.setVisibility(GONE);
                backIV.setVisibility(VISIBLE);
                viewPager.setCurrentItem(1);
                divider1.setBackgroundResource(R.drawable.button_background1);
                divider2.setBackgroundResource(R.drawable.button_background);
            }
        });

        nextcv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isNextEnabled) {
                    beneficialOwnerAPICall(boID, prepareRequest());
                }
            }
        });

        divider1.setOnClickListener(view -> {
            closeIV.setVisibility(VISIBLE);
            backIV.setVisibility(GONE);
            viewPager.setCurrentItem(0);
            divider1.setBackgroundResource(R.drawable.button_background);
            divider2.setBackgroundResource(R.drawable.button_background1);
        });

        divider2.setOnClickListener(view -> {
            if (isNextEnabled) {
                closeIV.setVisibility(GONE);
                backIV.setVisibility(VISIBLE);
                viewPager.setCurrentItem(1);
                divider1.setBackgroundResource(R.drawable.button_background1);
                divider2.setBackgroundResource(R.drawable.button_background);
            }
        });

        doneCV.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            if (isSaveEnabled) {
                beneficialOwnerAPICall(boID, prepareRequest());
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


    }

    private void initObservers() {
        try {
            businessIdentityVerificationViewModel.getBeneficialOwnersResponse().observe(this, new Observer<BOResp>() {
                @Override
                public void onChanged(BOResp boResp) {

                    if (boResp != null) {
                        if (boResp.getStatus().toLowerCase().toString().equals("success") && boResp.getData().size() > 0) {
                            objMyApplication.setBeneficialOwnersResponse(boResp);

                            for (int i = 0; i < boResp.getData().size(); i++) {
                                if (boResp.getData().get(i).getId() != boID) {
                                    totalOwnerShipPerc = totalOwnerShipPerc + boResp.getData().get(i).getOwnershipParcentage();
                                }

                                if (boResp.getData().get(i).getId() == boID) {
                                    if (boResp.getData().get(i).getFirstName() != null && !boResp.getData().get(i).getFirstName().equals("")) {
                                        fnameET.setText(boResp.getData().get(i).getFirstName());
                                        fnameET.setSelection(boResp.getData().get(i).getFirstName().length());
                                        isfname = true;
                                    }

                                    if (boResp.getData().get(i).getLastName() != null && !boResp.getData().get(i).getLastName().equals("")) {
                                        lnameET.setText(boResp.getData().get(i).getLastName());
                                        lnameET.setSelection(boResp.getData().get(i).getLastName().length());
                                        islname = true;
                                    }

                                    if (boResp.getData().get(i).getDob() != null && !boResp.getData().get(i).getDob().equals("")) {
                                        dobET.setText(convertDateNew(boResp.getData().get(i).getDob()));
                                        dateOfBirth = boResp.getData().get(i).getDob();
                                        isDOBSelected = true;
                                    }

                                    if (boResp.getData().get(i).getSsn() != null && !boResp.getData().get(i).getSsn().equals("")) {
                                        ssnET.setText(boResp.getData().get(i).getSsn());
//                                        ssnET.setSelection(boResp.getData().get(i).getSsn().length());
                                        isssn = true;
                                    }

                                    if (boResp.getData().get(i).getOwnershipParcentage() != -1 && boResp.getData().get(i).getOwnershipParcentage() != 0) {
                                        ownershipET.setText(boResp.getData().get(i).getOwnershipParcentage() + "%");
                                        ownershipET.setSelection(String.valueOf(boResp.getData().get(i).getOwnershipParcentage()).length());
                                        isownership = true;
                                        Utils.setUpperHintColor(ownershiptil, getResources().getColor(R.color.primary_black));
                                    }

                                    if (boResp.getData().get(i).getAddressLine1() != null && !boResp.getData().get(i).getAddressLine1().equals("")) {
                                        address1ET.setText(boResp.getData().get(i).getAddressLine1());
                                        address1ET.setSelection(boResp.getData().get(i).getAddressLine1().length());
                                        isAddress1 = true;
                                    }

                                    if (boResp.getData().get(i).getAddressLine2() != null && !boResp.getData().get(i).getAddressLine2().equals("")) {
                                        address2ET.setText(boResp.getData().get(i).getAddressLine2());
                                        address2ET.setSelection(boResp.getData().get(i).getAddressLine2().length());
                                        isAddress2 = true;
                                    }

                                    if (boResp.getData().get(i).getCity() != null && !boResp.getData().get(i).getCity().equals("")) {
                                        cityET.setText(boResp.getData().get(i).getCity());
                                        cityET.setSelection(boResp.getData().get(i).getCity().length());
                                        isCity = true;
                                    }

                                    if (boResp.getData().get(i).getState() != null && !boResp.getData().get(i).getState().equals("")) {
                                        stateET.setText(boResp.getData().get(i).getState());
                                        stateET.setSelection(boResp.getData().get(i).getState().length());
                                        isState = true;
                                    }

                                    if (boResp.getData().get(i).getZipCode() != null && !boResp.getData().get(i).getZipCode().equals("")) {
                                        zipcodeET.setText(boResp.getData().get(i).getZipCode());
                                        zipcodeET.setSelection(boResp.getData().get(i).getZipCode().length());
                                        isZipcode = true;
                                    }

                                    if (boResp.getData().get(i).getRequiredDocuments().size() > 0) {
                                        existingIdentityType = boResp.getData().get(i).getRequiredDocuments().get(0).getIdentityId();

                                        isFileUploaded = true;

                                        uploadTV.setVisibility(GONE);
                                        uploadedLL.setVisibility(VISIBLE);
                                        updatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDate(boResp.getData().get(i).getRequiredDocuments().get(0).getUpdatedAt()));

                                        if (existingIdentityType == 4)
                                            uploadedTV.setText("Uploaded Driver’s License");
                                        else if (existingIdentityType == 2)
                                            uploadedTV.setText("Uploaded Passport");
                                        else if (existingIdentityType == 1)
                                            uploadedTV.setText("Uploaded State-Issued Card");

                                    }
                                    enableOrDisableNext();
                                    enableOrDisableSave();
                                }
                            }

                        } else {
                            Utils.displayAlert(boResp.getError().getErrorDescription(), AddBeneficialOwnerActivity.this, "", boResp.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getRemoveBODocResponse().observe(this, new Observer<RemoveIdentityResponse>() {
                @Override
                public void onChanged(RemoveIdentityResponse imageResponse) {
                    try {
                        if (imageResponse != null) {
                            showProgressDialog();
                            RequestBody requestBody = null;
                            MultipartBody.Part idFile = null;

                            requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), identityFile);
                            idFile = MultipartBody.Part.createFormData("identityFile", identityFile.getName(), requestBody);

                            RequestBody idType = RequestBody.create(MediaType.parse("text/plain"), identityType + "");
                            businessIdentityVerificationViewModel.uploadBODoc(boID, idFile, idType);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getUploadBODocResponse().observe(this, new Observer<IdentityImageResponse>() {
                @Override
                public void onChanged(IdentityImageResponse identityImageResponse) {
                    dismissDialog();
                    Log.e("upload respo", identityImageResponse.toString());
                    if (identityImageResponse.getStatus().equalsIgnoreCase("success")) {
                        isFileUploaded = true;
                        if (identityType == 4) {
                            uploadTV.setVisibility(GONE);
                            uploadedLL.setVisibility(VISIBLE);
                            uploadedTV.setText("Uploaded Driver’s License");
                            updatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDateAPITime(identityImageResponse.getTimestamp().split("T")[0]));
                        } else if (identityType == 2) {
                            uploadTV.setVisibility(GONE);
                            uploadedLL.setVisibility(VISIBLE);
                            updatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDateAPITime(identityImageResponse.getTimestamp().split("T")[0]));
                            uploadedTV.setText("Uploaded Passport");
                        } else if (identityType == 1) {
                            uploadTV.setVisibility(GONE);
                            uploadedLL.setVisibility(VISIBLE);
                            updatedOnTV.setText("Uploaded on " + Utils.convertDocUploadedDateAPITime(identityImageResponse.getTimestamp().split("T")[0]));
                            uploadedTV.setText("Uploaded State-Issued Card");
                        } else {
                            isFileUploaded = false;
                            uploadTV.setVisibility(VISIBLE);
                            uploadedLL.setVisibility(GONE);
                        }

                        enableOrDisableNext();
                        enableOrDisableSave();
                    } else {
                        Utils.displayAlert(identityImageResponse.getError().getErrorDescription(), AddBeneficialOwnerActivity.this, "", identityImageResponse.getError().getFieldErrors().get(0));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            businessIdentityVerificationViewModel.getPatchBOresponse().observe(this, new Observer<BOPatchResp>() {
                @Override
                public void onChanged(BOPatchResp boPatchResp) {

                    if (boPatchResp != null) {
                        if (boPatchResp.getStatus().toLowerCase().toString().equals("success")) {
//                            objMyApplication.setBeneficialOwnersResponse(boResp);

                            closeIV.setVisibility(GONE);
                            backIV.setVisibility(VISIBLE);

                            if (selectedPage == 0) {
                                viewPager.setCurrentItem(1);
                                closeIV.setVisibility(GONE);
                                backIV.setVisibility(VISIBLE);
                                divider1.setBackgroundResource(R.drawable.button_background1);
                                divider2.setBackgroundResource(R.drawable.button_background);
                            } else if (selectedPage == 1) {
                                startActivity(new Intent(AddBeneficialOwnerActivity.this, AdditionalBeneficialOwnersActivity.class));
                                finish();
                            }

                        } else {
                            Utils.displayAlert(boPatchResp.getError().getErrorDescription(), AddBeneficialOwnerActivity.this, "", boPatchResp.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public static String convertDateNew(String date) {
        String strDate = "";
        try {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
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
//                                enableOrDisableNext();
//                                ssnET.clearFocus();
//                                datepicker = new DatePicker(AddBeneficialOwnerActivity.this);
//                                datepicker.init(year, monthOfYear + 1, dayOfMonth, null);
//                                Utils.setUpperHintColor(dobtil, getResources().getColor(R.color.primary_black));
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
            long years = 568025136000L;
            com.ideyalabs.wheelpicker.DatePicker picker = new com.ideyalabs.wheelpicker.DatePicker(AddBeneficialOwnerActivity.this);
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
                        enableOrDisableNext();
                        ssnET.clearFocus();
//                        datepicker = new DatePicker(IdentityVerificationActivity.this);
//                        datepicker.init(year, monthOfYear + 1, dayOfMonth, null);
                        Utils.setUpperHintColor(dobtil, getResources().getColor(R.color.primary_black));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    Log.e("WheelPicker", dateOfBirth);
                    picker.hide();
                }
            });

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    picker.show(getWindow());
                }
            }, 500);
        } catch (Exception e) {
            e.printStackTrace();
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
//                    fnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    if (!fnameET.hasFocus())
                    Utils.setUpperHintColor(fnametil, getResources().getColor(R.color.primary_black));
//                    String str = fnameET.getText().toString();
//                    if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
//                        fnameET.setText(fnameET.getText().toString().replaceAll(" ", ""));
//                        fnameET.setSelection(fnameET.getText().length());
//                    }
                } else {
                    isfname = false;
                }

                if (fnameET.getText().toString().contains("  ")){
                    fnameET.setText(fnameET.getText().toString().replace("  "," "));
                    fnameET.setSelection(fnameET.getText().length());
                }

                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    String str = fnameET.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        fnameET.setText("");
                        fnameET.setSelection(fnameET.getText().length());
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        fnameET.setText(str.trim());
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
                if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                    islname = true;
                    lnameLL.setVisibility(GONE);
//                    lnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    if (!lnameET.hasFocus())
                    Utils.setUpperHintColor(lnametil, getResources().getColor(R.color.primary_black));
//                        String str = lnameET.getText().toString();
//                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
//                            lnameET.setText(lnameET.getText().toString().replaceAll(" ", ""));
//                            lnameET.setSelection(lnameET.getText().length());
//                        }
                } else {
                    islname = false;
                }


                if (lnameET.getText().toString().contains("  ")){
                    lnameET.setText(lnameET.getText().toString().replace("  "," "));
                    lnameET.setSelection(lnameET.getText().length());
                }

                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String str = lnameET.getText().toString();
                    if (str.length() > 0 && str.toString().trim().length() == 0) {
                        lnameET.setText("");
                        lnameET.setSelection(lnameET.getText().length());
                    } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                        lnameET.setText(str.trim());
                    } else if (str.length() > 0 && str.contains(".")) {
                        lnameET.setText(lnameET.getText().toString().replaceAll("\\.", ""));
                        lnameET.setSelection(lnameET.getText().length());
                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                        lnameET.setText("");
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
                        Utils.setUpperHintColor(dobtil, getResources().getColor(R.color.primary_black));
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

//        ssnET.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().length() == 9) {
//                    isssn = true;
//                    ssntil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(ssntil, getResources().getColor(R.color.primary_black));
//                } else {
////                    ssnTV.setText("Field Required");
//                    isssn = false;
//                }
//                enableOrDisableNext();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                try {
//                    String text = ssnET.getText().toString().trim().replace("-", "");
//                    if (text.length() == 5) {
//                        String hifened = text.substring(0, 3) + "-" + text.substring(3, 5);
//                        ssnET.setText(hifened);
//                    } else if (text.length() == 4) {
//                        String hifened = text.substring(0, 3) + "-" + text.substring(3);
//                        ssnET.setText(hifened);
//                    } else if (text.length() < 4) {
//
//                    } else {
//                        String hifened = text.substring(0, 3) + "-" + text.substring(3, 5) + "-" + text.substring(5);
//                        ssnET.setText(hifened);
//                    }
//                    ssnET.setSelection(ssnET.getText().toString().trim().length());
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });

        ownershipET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().replace("%", "").length() > 0) {
                    int allowPerc = 100 - totalOwnerShipPerc;
                    int enteredAmt = Integer.parseInt(charSequence.toString().trim().replace("%", ""));
                    isownership = enteredAmt > 0 && enteredAmt <= allowPerc;
//                    ownershipLL.setVisibility(GONE);
//                    ownershiptil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(ownershiptil, getResources().getColor(R.color.primary_black));
                } else {
//                    ownershipTV.setText("Field Required");
                    isownership = false;
                }
                enableOrDisableNext();
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int perc = Integer.parseInt(ownershipET.getText().toString());
                    if (perc > 100 || perc == 0) {
                        ownershipET.setText(String.valueOf(perc).substring(0, String.valueOf(perc).length() - 1));
                        ownershipET.setSelection(ownershipET.getText().length());
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
//                    address1TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    if (!address1ET.hasFocus())
                    Utils.setUpperHintColor(address1TIL, getResources().getColor(R.color.primary_black));
                } else {
//                    address1ErrorLL.setVisibility(VISIBLE);
//                    address1ErrorTV.setText("Field Required");
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
                if (charSequence.length() > 0) {
                    if (!address2ET.hasFocus())
                    Utils.setUpperHintColor(address2TIL, getResources().getColor(R.color.primary_black));
                }
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
//                    cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    if (!cityET.hasFocus())
                    Utils.setUpperHintColor(cityTIL, getResources().getColor(R.color.primary_black));
                    cityErrorLL.setVisibility(GONE);
                } else {
//                    cityErrorLL.setVisibility(VISIBLE);
//                    cityErrorTV.setText("Field Required");
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

        stateET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (charSequence.length() > 0) {
                        Utils.setUpperHintColor(stateTIL, getResources().getColor(R.color.primary_black));
//                        stateTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        isState = true;
                    } else {
                        isState = false;
                    }
                    enableOrDisableSave();
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
                if (charSequence.length() == 5) {
                    isZipcode = true;
                    zipcodeErrorLL.setVisibility(GONE);
                    //                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    if (!zipcodeET.hasFocus())
                        Utils.setUpperHintColor(zipcodeTIL, getResources().getColor(R.color.primary_black));
                } else if (charSequence.length() < 5 && charSequence.length() > 0) {
                    isZipcode = false;
                    zipcodeErrorLL.setVisibility(GONE);
                    //                        zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                            Utils.setUpperHintColor(zipcodetil, getResources().getColor(R.color.primary_black));
                } else if (charSequence.length() == 0) {
                    isZipcode = false;
                    //                            zipcodeErrorLL.setVisibility(VISIBLE);
                    //                            zipcodeErrorTV.setText("Field Required");
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
                    if (fnameET.getText().toString().trim().length() > 1) {
                        fnameLL.setVisibility(GONE);
                        fnametil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        Utils.setUpperHintColor(fnametil, getColor(R.color.primary_black));

                    } else if (fnameET.getText().toString().trim().length() == 1) {
                        fnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(fnametil, getColor(R.color.error_red));
                        fnameLL.setVisibility(VISIBLE);
                        fnameTV.setText("Minimum 2 Characters Required");
                    } else {
                        fnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(fnametil, getColor(R.color.light_gray));
                        fnameLL.setVisibility(VISIBLE);
                        fnameTV.setText("Field Required");
                    }
                    if (fnameET.getText().toString().length() > 0 && !fnameET.getText().toString().substring(0, 1).equals(" ")) {
                        fnameET.setText(fnameET.getText().toString().substring(0, 1).toUpperCase() + fnameET.getText().toString().substring(1));
                        fnameET.setSelection(fnameET.getText().toString().length());
                    }
                } else {
                    fnameLL.setVisibility(GONE);
//                    fnameET.setHint("First Name");
                    fnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(fnametil, getColor(R.color.primary_green));
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
                        lnametil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        Utils.setUpperHintColor(lnametil, getColor(R.color.primary_black));

                    } else if (lnameET.getText().toString().trim().length() == 1) {
                        lnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(lnametil, getColor(R.color.error_red));
                        lnameLL.setVisibility(VISIBLE);
                        lnameTV.setText("Minimum 2 Characters Required");
                    } else {
                        lnametil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(lnametil, getColor(R.color.light_gray));
                        lnameLL.setVisibility(VISIBLE);
                        lnameTV.setText("Field Required");
                    }
                    if (lnameET.getText().toString().length() > 0 && !lnameET.getText().toString().substring(0, 1).equals(" ")) {
                        lnameET.setText(lnameET.getText().toString().substring(0, 1).toUpperCase() + lnameET.getText().toString().substring(1));
                        lnameET.setSelection(lnameET.getText().toString().length());
                    }
                } else {
                    lnameLL.setVisibility(GONE);
//                    lnameET.setHint("Last Name");
                    lnametil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(lnametil, getColor(R.color.primary_green));
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

//        ssnET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (!b) {
//                    ssnET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
//                    ssnET.setHint("");
//                    if (ssnET.getText().toString().trim().length() == 9) {
//                        ssnLL.setVisibility(GONE);
//                        ssntil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
//                        Utils.setUpperHintColor(ssntil, getColor(R.color.primary_black));
//
//                    } else if (ssnET.getText().toString().trim().length() > 0 && ssnET.getText().toString().trim().length() < 9) {
//                        ssntil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
//                        Utils.setUpperHintColor(ssntil, getColor(R.color.error_red));
//                        ssnLL.setVisibility(VISIBLE);
//                        ssnTV.setText("Please enter a valid SSN");
//
//                    } else {
//                        ssntil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
//                        Utils.setUpperHintColor(ssntil, getColor(R.color.light_gray));
//                        ssnLL.setVisibility(VISIBLE);
//                        ssnTV.setText("Field Required");
//                    }
//                } else {
//                    ssnET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
//                    ssnLL.setVisibility(GONE);
//                    ssnET.setHint("•••-••-••••");
//                    ssntil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                    Utils.setUpperHintColor(ssntil, getColor(R.color.primary_green));
//                }
//            }
//        });
//
//        ssntil.setEndIconOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    if (!isCPwdEye) {
//                        isCPwdEye = true;
//                        ssntil.setEndIconDrawable(R.drawable.ic_eyeopen);
//                        ssnET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//                        String text = ssnET.getText().toString().trim().replace("-", "");
//                        if (text.length() == 5) {
//                            String hifened = text.substring(0, 3) + "-" + text.substring(3, 5);
//                            ssnET.setText(hifened);
//                        } else if (text.length() == 4) {
//                            String hifened = text.substring(0, 3) + "-" + text.substring(3);
//                            ssnET.setText(hifened);
//                        } else if (text.length() < 4) {
//
//                        } else {
//                            String hifened = text.substring(0, 3) + "-" + text.substring(3, 5) + "-" + text.substring(5);
//                            ssnET.setText(hifened);
//                        }
//
//                    } else {
//                        isCPwdEye = false;
//                        ssntil.setEndIconDrawable(R.drawable.ic_eyeclose);
//                        ssnET.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    }
//                    if (ssnET.getText().length() > 0) {
//                        ssnET.setSelection(ssnET.getText().length());
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });


        ownershipET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ownershipET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    ownershipET.setHint("");
                    if (ownershipET.getText().toString().trim().replace("%", "").length() > 0) {
                        int allowPerc = 100 - totalOwnerShipPerc;
                        if (Integer.parseInt(ownershipET.getText().toString().trim()) <= allowPerc && Integer.parseInt(ownershipET.getText().toString().trim()) > 0) {
                            ownershipLL.setVisibility(GONE);
                            ownershiptil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            Utils.setUpperHintColor(ownershiptil, getColor(R.color.primary_black));
                        } else {
                            ownershiptil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            Utils.setUpperHintColor(ownershiptil, getColor(R.color.error_red));
                            ownershipLL.setVisibility(VISIBLE);
//                            ownershipTV.setText("Please enter a valid Ownership Percentage");
//                            ownershipTV.setText("Please enter value from 1% to " + allowPerc+"%");
                            ownershipTV.setText("Please enter less than or equal to " + allowPerc + "%");
                        }

                        ownershipET.setText(ownershipET.getText().toString() + "%");
                    } else {
                        ownershiptil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(ownershiptil, getColor(R.color.light_gray));
                        ownershipLL.setVisibility(VISIBLE);
                        ownershipTV.setText("Field Required");
                    }
                } else {
                    ownershipET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
//                    ownershipET.setHint("Ownership %");
                    ownershiptil.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(ownershiptil, getColor(R.color.primary_green));
                    ownershipLL.setVisibility(GONE);

                    if (ownershipET.getText().toString().length() > 0) {
                        ownershipET.setText(ownershipET.getText().toString().replace("%", ""));
                        ownershipET.setSelection(ownershipET.getText().toString().length());
                    }
                }
            }
        });

        ownershipET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        ownershipET.clearFocus();
                        int allowPerc = 100 - totalOwnerShipPerc;
                        if (!ownershipET.getText().toString().trim().equals("") && Integer.parseInt(ownershipET.getText().toString().trim()) <= allowPerc) {
                            ownershipLL.setVisibility(GONE);
                            ownershiptil.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                            Utils.setUpperHintColor(ownershiptil, getColor(R.color.primary_black));
                        } else {
                            ownershiptil.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                            Utils.setUpperHintColor(ownershiptil, getColor(R.color.light_gray));
                            ownershipLL.setVisibility(VISIBLE);
//                            ownershipTV.setText("Please enter a valid Ownership Percentage");
                            ownershipTV.setText("Please enter less than or equal to " + allowPerc + "%");
//                            ownershipTV.setText("Please enter value from 1% to " + allowPerc+"%");
                        }
//                    ownershipET.setText(ownershipET.getText().toString()+"%");
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                return false;
            }
        });

        address1ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    address1ET.setHint("");
                    if (address1ET.getText().toString().trim().length() > 0) {
                        address1ErrorLL.setVisibility(GONE);
                        address1TIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        Utils.setUpperHintColor(address1TIL, getColor(R.color.primary_black));

                    } else {
                        address1TIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(address1TIL, getColor(R.color.light_gray));
                        address1ErrorLL.setVisibility(VISIBLE);
                        address1ErrorTV.setText("Field Required");
                    }
                    if (address1ET.getText().toString().length() > 0 && !address1ET.getText().toString().substring(0, 1).equals(" ")) {
                        address1ET.setText(address1ET.getText().toString().substring(0, 1).toUpperCase() + address1ET.getText().toString().substring(1));
                        address1ET.setSelection(address1ET.getText().toString().length());

                    }
                } else {
//                    address1ET.setHint("Street Address");
                    address1ET.requestFocus();
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(AddBeneficialOwnerActivity.this);
                    address1TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address1TIL, getColor(R.color.primary_green));
                    address1ErrorLL.setVisibility(GONE);


                }
            }
        });

        address2ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    address2ET.setHint("");
                    if (address2ET.getText().toString().length() > 0) {
                        address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        Utils.setUpperHintColor(address2TIL, getColor(R.color.primary_black));
                    } else {
                        address2TIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        Utils.setUpperHintColor(address2TIL, getColor(R.color.light_gray));
                    }
                    if (address2ET.getText().toString().length() > 0 && !address2ET.getText().toString().substring(0, 1).equals(" ")) {
                        address2ET.setText(address2ET.getText().toString().substring(0, 1).toUpperCase() + address2ET.getText().toString().substring(1));
                        address2ET.setSelection(address2ET.getText().toString().length());
                    }

                } else {
//                    address2ET.setHint("Apt#, Suit, Floor ");
                    address2ErrorLL.setVisibility(GONE);
                    address2TIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(address2TIL, getColor(R.color.primary_green));
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
                        cityTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_black));

                    } else {
                        cityTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(cityTIL, getColor(R.color.light_gray));
                        cityErrorLL.setVisibility(VISIBLE);
                        cityErrorTV.setText("Field Required");
                    }
                    if (cityET.getText().toString().length() > 0 && !cityET.getText().toString().substring(0, 1).equals(" ")) {
                        cityET.setText(cityET.getText().toString().substring(0, 1).toUpperCase() + cityET.getText().toString().substring(1));
                        cityET.setSelection(cityET.getText().toString().length());
                    }
                } else {
//                    cityET.setHint("City");
                    cityTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(cityTIL, getColor(R.color.primary_green));
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
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(myActivity));
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_black));
                    } else if (zipcodeET.getText().toString().trim().length() < 5 && zipcodeET.getText().toString().trim().length() > 0) {
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.error_red));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Minimum 5 Digits Required");

                    } else {
                        zipcodeTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(myActivity));
                        Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.light_gray));
                        zipcodeErrorLL.setVisibility(VISIBLE);
                        zipcodeErrorTV.setText("Field Required");
                    }
                } else {
                    zipcodeET.requestFocus();
                    if (!Utils.isKeyboardVisible)
                        Utils.shwForcedKeypad(AddBeneficialOwnerActivity.this);
//                    zipcodeET.setHint("Zip Code");
                    zipcodeTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                    Utils.setUpperHintColor(zipcodeTIL, getColor(R.color.primary_green));
                    zipcodeErrorLL.setVisibility(GONE);
                }
            }
        });

    }

    public static void enableOrDisableNext() {

        try {
            if (isfname && islname && isDOBSelected && isssn && isownership && isFileUploaded) {
                isNextEnabled = true;
                nextcv.setCardBackgroundColor(addBeneficialOwnerActivity.getResources().getColor(R.color.primary_green));
                viewPager.setPagingEnabled(true);
            } else {
                isNextEnabled = false;
                nextcv.setCardBackgroundColor(addBeneficialOwnerActivity.getResources().getColor(R.color.inactive_color));
                viewPager.setPagingEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableOrDisableSave() {
        try {
            if (isAddress1 && isCity && isState && isZipcode) {
                isSaveEnabled = true;
                doneCV.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
            } else {
                isSaveEnabled = false;
                doneCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class BOPagerAdapter extends PagerAdapter {

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

    public BORequest prepareRequest() {
        BORequest boRequest = new BORequest();

        try {
            //Basic
//            if (isBasicNextEnabled) {

            if (fnameET.getText().toString().trim().length() > 1)
                boRequest.setFirstName(fnameET.getText().toString().trim());

            if (lnameET.getText().toString().trim().length() > 1)
                boRequest.setLastName(lnameET.getText().toString().trim());

            if (dateOfBirth.trim().length() > 0)
                boRequest.setDob(dateOfBirth.trim());

            if (ssnET.getUnMasked().trim().length() == 9)
                boRequest.setSsn(ssnET.getUnMasked());

            if (ownershipET.getText().toString().trim().length() > 0)
                boRequest.setOwnershipParcentage(Integer.parseInt(ownershipET.getText().toString().replace("%", "")));
//            }

            //Address
//            if (isAddressNextEnabled) {
            if (address1ET.getText().toString().trim().length() > 0)
                boRequest.setAddressLine1(address1ET.getText().toString().trim());

            if (address2ET.getText().toString().trim().length() > 0)
                boRequest.setAddressLine2(address2ET.getText().toString().trim());

            if (cityET.getText().toString().trim().length() > 0)
                boRequest.setCity(cityET.getText().toString().trim());

            if (stateET.getText().toString().trim().length() > 0)
                boRequest.setState(stateET.getText().toString().trim());

            if (zipcodeET.getText().toString().trim().length() >= 5)
                boRequest.setZipCode(zipcodeET.getText().toString().trim());
            boRequest.setCountry("us");

//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return boRequest;
    }

    public void beneficialOwnerAPICall(int boID, BORequest boRequest) {
        businessIdentityVerificationViewModel.patchBeneficialOwner(boID, boRequest);
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
                        identityType = 4;
                        context.startActivity(new Intent(context, CameraActivity.class).putExtra("FROM", "ADD_BO"));
                    }
                }
            });

            passportll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (checkAndRequestPermissions((Activity) context)) {
                        identityType = 2;
                        context.startActivity(new Intent(context, CameraActivity.class).putExtra("FROM", "ADD_BO"));
                    }
                }
            });

            sicardll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (checkAndRequestPermissions((Activity) context)) {
                        identityType = 1;
                        context.startActivity(new Intent(context, CameraActivity.class).putExtra("FROM", "ADD_BO"));
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
                        Utils.displayAlert("Requires Access to Camera.", AddBeneficialOwnerActivity.this, "", "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", AddBeneficialOwnerActivity.this, "", "");

                    } else {
                        showIdentityTypePopup(AddBeneficialOwnerActivity.this);
                    }
                    break;
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
        if (visible) {
            Utils.isKeyboardVisible = true;
        } else {
            Utils.isKeyboardVisible = false;
        }
    }

    public void removeAndUploadBODoc() {
        try {
            if (existingIdentityType != -1)
                businessIdentityVerificationViewModel.removeBODoc(existingIdentityType + "", boID + "");
            else
                businessIdentityVerificationViewModel.removeBODoc(identityType + "", boID + "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedPage == 0) {
            try {
                if (fromScreen != null && !fromScreen.equals("EDIT_BO")) {
                    confirmationAlert();
                } else {
                    super.onBackPressed();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } else if (selectedPage == 1) {
            closeIV.setVisibility(VISIBLE);
            backIV.setVisibility(GONE);
            viewPager.setCurrentItem(0);
            divider1.setBackgroundResource(R.drawable.button_background);
            divider2.setBackgroundResource(R.drawable.button_background1);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            //businessIdentityVerificationViewModel.patchBeneficialOwner(boID, prepareRequest());

            isfname = false;
            islname = false;
            isssn = false;
            isownership = false;
            isNextEnabled = false;
            isDOBSelected = false;
            isAddress1 = false;
            isAddress2 = false;
            isCity = false;
            isState = false;
            isZipcode = false;
            isSaveEnabled = false;
            isFileUploaded = false;

            identityFile = null;
            identityType = 0;
            existingIdentityType = -1;
            isFileSelected = false;
            dateOfBirth = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmationAlert() {
        // custom dialog
        final Dialog dialog = new Dialog(AddBeneficialOwnerActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_confirmation_alert);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        TextView tvNo = dialog.findViewById(R.id.tvNo);
        TextView tvYes = dialog.findViewById(R.id.tvYes);

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (fromScreen != null && !fromScreen.equals("EDIT_BO")) {
                    businessIdentityVerificationViewModel.deleteBeneficialOwner(boID);
                    finish();
                }
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}



