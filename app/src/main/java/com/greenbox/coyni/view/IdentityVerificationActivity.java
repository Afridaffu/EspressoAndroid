package com.greenbox.coyni.view;

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
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraActivity;
import com.greenbox.coyni.fragments.IdVeBottomSheetFragment;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.utils.OnSwipeTouchListener;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;
import com.greenbox.coyni.viewmodel.IdentityVerificationViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class IdentityVerificationActivity extends AppCompatActivity {
    TextInputLayout dobTIL, ssnTIL;
    TextInputEditText dobET, ssnET, cityET;
    TextView idveriUItext, idveriUItextSuc, exitBtn, btnExit, ssnErrorTV;
    TextInputEditText mailAddr1, city, state, zipcode;
    ConstraintLayout idveriDOBConLayout;
    LinearLayout bottomSheet, fileSelectedLL;
    public static CardView btnNext, btnSubmit;
    ScrollView secondIVeri;
    LinearLayout firstIVeri;
    View viewLeft, viewRight;
    ImageButton closebtn, backbtn;
    ImageView upIdSuccessImg;
    final Calendar myCalendar = Calendar.getInstance();
    int mYear, mMonth, mDay;
    CardView buttonSubmit;
    String dateFormat, date;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    public static boolean isMailAddr1 = true, isCity = false, isState = false, isZip = false, isSubmit = false, isNext = false;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    public static File identityFile;
    public static String identityNumber;
    public static int identityType = 0;
    public static boolean isFileSelected = false, isSSNSelected = false, isDOBSelected = false;

    public static IdentityVerificationActivity identityVerificationActivity;
    LinearLayout ssnErrorLL, swipeLL;

    IdentityVerificationViewModel identityVerificationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            setContentView(R.layout.activity_identity_verification);

            identityVerificationViewModel = new ViewModelProvider(this).get(IdentityVerificationViewModel.class);
            identityVerificationActivity = this;
            initFields();
            initObservers();

            bottomSheet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showIdentityTypePopup(IdentityVerificationActivity.this);
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
                }

            });


            swipeLL.setOnTouchListener(new OnSwipeTouchListener(IdentityVerificationActivity.this) {

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
                                enableNext();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, mYear, mMonth, mDay);

            long years = 568025136000L;
            long yearsback = c.getTimeInMillis() - years;
            datePickerDialog.getDatePicker().setMaxDate(yearsback);
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
                    if (charSequence.toString().trim().length() > 0) {
                        isDOBSelected = true;
                    } else {
                        isDOBSelected = false;
                    }
                    enableNext();

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
                    if (charSequence.toString().trim().length() > 0) {
                        isMailAddr1 = true;
                    } else {
                        isMailAddr1 = false;
                    }
                    enableORdiableSubmit();

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            city.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {
                        isCity = true;
                    } else {
                        isCity = false;
                    }
                    enableORdiableSubmit();

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            state.addTextChangedListener(new TextWatcher() {
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
                    enableORdiableSubmit();

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
                    if (charSequence.toString().trim().length() > 0) {
                        isZip = true;
                    } else {
                        isZip = false;
                    }
                    enableORdiableSubmit();

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            ssnET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        if (ssnET.getText().toString().trim().length() == 4) {
                            ssnErrorLL.setVisibility(GONE);
                            ssnTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.primary_black));

                        } else if (ssnET.getText().toString().trim().length() > 0 && ssnET.getText().toString().trim().length() < 4) {
                            ssnTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.error_red));
                            ssnErrorLL.setVisibility(VISIBLE);
                            ssnErrorTV.setText("Required Last 4 Digits");

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

            ssnET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() == 4) {
                        isSSNSelected = true;
                        ssnErrorLL.setVisibility(GONE);
                        ssnTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(ssnTIL, getResources().getColor(R.color.primary_green));
                    } else {
                        isSSNSelected = false;
                    }
                    enableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


        } catch (Exception ex) {
            ex.printStackTrace();
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
            city = findViewById(R.id.cityET);
            state = findViewById(R.id.stateET);
            zipcode = findViewById(R.id.zipcodeET);
            fileSelectedLL = findViewById(R.id.fileSelectedLL);
            ssnErrorLL = findViewById(R.id.ssnErrorLL);
            ssnErrorTV = findViewById(R.id.ssnErrorTV);
            swipeLL = findViewById(R.id.swipeLL);

            TextWatchers();

            idveriDOBConLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.hideSoftKeyboard(IdentityVerificationActivity.this);
                    setToDate(dobET);
                }
            });
            dobET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.hideSoftKeyboard(IdentityVerificationActivity.this);
                    setToDate(dobET);
                }
            });
            dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        Utils.hideSoftKeyboard(IdentityVerificationActivity.this);
                        setToDate(dobET);
                    }
                }
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

                        firstIVeri.setVisibility(GONE);
                        secondIVeri.setVisibility(VISIBLE);
                        viewLeft.setBackgroundResource(R.drawable.button_background1);
                        viewRight.setBackgroundResource(R.drawable.button_background);
                        backbtn.setVisibility(VISIBLE);
                        closebtn.setVisibility(GONE);

                    }
                }
            });
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSubmit) {
                        btnNext.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                        finish();
                    }
                }
            });
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
                        Utils.displayAlert("Requires Access to Camera.", IdentityVerificationActivity.this, "");

                    } else if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Utils.displayAlert("Requires Access to Your Storage.", IdentityVerificationActivity.this, "");

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

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            identityVerificationViewModel.getRemoveIdentityImageResponse().observe(this, new Observer<ImageResponse>() {
                @Override
                public void onChanged(ImageResponse imageResponse) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}