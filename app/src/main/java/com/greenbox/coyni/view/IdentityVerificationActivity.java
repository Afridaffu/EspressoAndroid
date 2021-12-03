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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraActivity;
import com.greenbox.coyni.fragments.IdVeBottomSheetFragment;
import com.greenbox.coyni.utils.Utils;

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
    TextView idveriUItext, idveriUItextSuc, exitBtn, btnExit,ssnErrorTV;
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

    public static boolean isupload = false, isSnn = false, isDOB = false, isNext = false;
    boolean isMailAddr1 = true, isCity = false, isState = false, isZip = false, isSubmit = false;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    public static File identityFile;
    public static String identityNumber;
    public static int identityType = 0;
    public static boolean isFileSelected = false, isSSNSelected = false, isDOBSelected = false;

    public static IdentityVerificationActivity identityVerificationActivity;
    LinearLayout ssnErrorLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verification);

        identityVerificationActivity = this;
        initFields();

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                date = new String(day + "/" + month + "/" + year);
                dateFormat = convertDate(date);
                Log.e("date", "" + date);
                Log.e("date", "" + dateFormat);
                dobET.setText(dateFormat);
                long years = 568025136000L;
                long yearsback = myCalendar.getTimeInMillis() - years;


            }
        };

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
//        ssnET.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (ssnET.length() == 4) {
//                    isSnn = true;
//                }
//            }
//        });


    }

    private void initFieldsNext() {


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
            ssnET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() == 4) {
                        isSnn = true;
                    } else {
                        isSnn = false;
                    }
                    enableORdiableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }


            });
            dobET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {
                        isDOB = true;
                    } else {
                        isDOB = false;
                    }
                    enableORdiableNext();

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
                        if (ssnET.getText().toString().trim().length() == 0) {
                            ssnErrorLL.setVisibility(GONE);
                            ssnTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.primary_black));

                        }else if (ssnET.getText().toString().trim().length() > 0 && ssnET.getText().toString().trim().length() < 4) {
                            ssnTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(ssnTIL, getColor(R.color.error_red));
                            ssnErrorLL.setVisibility(VISIBLE);
                            ssnErrorTV.setText("Required Last 4 Characters");

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
                    if (charSequence.toString().trim().length() ==4) {
                        isSnn = true;
                        ssnErrorLL.setVisibility(GONE);
                        ssnTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(ssnTIL,getResources().getColor(R.color.primary_green));
                    } else {
                        isSnn = false;
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

    private void enableORdiableNext() {
        Log.e("boolean", "" + isDOB + " " + isSnn);

        if (isSnn && isDOB) {
            isNext = true;
            btnNext.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

        } else {
            isNext = false;
            btnNext.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }

    private void enableORdiableSubmit() {

        if (isMailAddr1 && isCity && isState && isZip) {
            isSubmit = true;
            btnSubmit.setCardBackgroundColor(getResources().getColor(R.color.primary_color));

        } else {
            isSubmit = false;
            btnSubmit.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
        }
    }

    public void initFields() {
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

        TextWatchers();

        idveriDOBConLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDOB = true;
                setToDate(dobET);
            }
        });
        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDOB = true;
                setToDate(dobET);
            }
        });
        dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    setToDate(dobET);
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNext) {
                    btnNext.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
                    firstIVeri.setVisibility(View.GONE);
                    secondIVeri.setVisibility(View.VISIBLE);
                    viewLeft.setBackgroundResource(R.drawable.button_background1);
                    viewRight.setBackgroundResource(R.drawable.button_background);
                    closebtn.setVisibility(View.GONE);
                    backbtn.setVisibility(View.VISIBLE);
                } else {
//                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//
//                    MultipartBody.Part body =MultipartBody.Part.createFormData("identityFile", file.getName(), requestBody);
//
//                    RequestBody ItemId = RequestBody.create(okhttp3.MultipartBody.FORM, "22");
//                    RequestBody ImageNumber = RequestBody.create(okhttp3.MultipartBody.FORM,"1");
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

    }

    public static void showIdentityTypePopup(final Context context) {
        // custom dialog
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
    }

    @Override
    protected void onResume() {
        super.onResume();

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
    }

    public static void enableNext() {
        if (isFileSelected && isSSNSelected && isDOBSelected) {
            isNext = true;
            btnNext.setCardBackgroundColor(identityVerificationActivity.getResources().getColor(R.color.primary_color));
        } else {
            isNext = false;
            btnNext.setCardBackgroundColor(identityVerificationActivity.getResources().getColor(R.color.inactive_color));
        }
    }
}