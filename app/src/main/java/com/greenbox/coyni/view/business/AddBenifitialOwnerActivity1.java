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
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.custom_camera.CameraFragment;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BaseActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddBenifitialOwnerActivity1 extends BaseActivity {

    TextInputLayout fnametil, lnametil, dobtil, ssntil, ownershiptil;
    TextInputEditText fnameET, lnameET, dobET, ssnET, ownershipET;
    LinearLayout fnameLL, lnameLL, dobLL, ssnLL, ownershipLL,uploadLL;
    TextView uploadTV ,fnameTV, lnameTV, dobTV, ssnTV, ownershipTV;
    public CardView nextcv;
    ImageView closeIV , CompleteIV, uploadIV;
    public boolean isfname = false, islname = false, isdob = false, isssn = false, isownership = false, isNextEnabled = false,   isDOBSelected = false;
    public static int focusedID = 0;
    public static String dateOfBirth = "";
    int mYear, mMonth, mDay;
    private DatePicker datepicker;
    Long mLastClickTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_add_benifitial_owner_1);
        initfields();
        textWatchers();
        focusWatchers();

        uploadLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout DrivingLicence;
                Dialog dialog = new Dialog((AddBenifitialOwnerActivity1.this));
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawableResource(R.color.mb_transparent);
                dialog.setContentView(R.layout.activity_choose_id_dialog);
                Window window = dialog.getWindow();
//                int height = (int)(getResources().getDisplayMetrics().heightPixels*0.45);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams wl=window.getAttributes();
                wl.gravity= Gravity.BOTTOM;
                wl.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wl);
                dialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
                dialog.setCanceledOnTouchOutside(true);
                DrivingLicence=dialog.findViewById(R.id.payRequestLL);
                DrivingLicence.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            startActivity(new Intent(AddBenifitialOwnerActivity1.this, CameraFragment.class));
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
//        firstNameET.requestFocus();


        if (fnameET.getId() == focusedID) {
            fnameET.requestFocus();
        } else if (lnameET.getId() == focusedID) {
            lnameET.requestFocus();
        } else if (ssnET.getId() == focusedID) {
            ssnET.requestFocus();
        } else if (ownershipET.getId() == focusedID) {
            ownershipET.requestFocus();
        } else {
            fnameET.requestFocus();
        }

        Log.e("ID", "" + focusedID);
    }

    private void initfields() {
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
                                datepicker = new DatePicker(AddBenifitialOwnerActivity1.this);
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
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
                        fnametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(fnametil, getColor(R.color.primary_black));
                    } else if (fnameET.getText().toString().trim().length() == 1) {
                        fnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(fnametil, getColor(R.color.light_gray));
                        fnameLL.setVisibility(VISIBLE);
                        fnameTV.setText("Field Required Mininmum 2 characters");

                    } else {
                        fnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(fnametil, getColor(R.color.light_gray));
                        fnameLL.setVisibility(VISIBLE);
                        fnameTV.setText("Field Required");
                    }
                } else {
                    focusedID = fnameET.getId();
                    fnameET.setHint("First Name");
                    fnametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
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
                        fnametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(lnametil, getColor(R.color.primary_black));
                    }else if (lnameET.getText().toString().trim().length() == 1) {
                        lnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(lnametil, getColor(R.color.light_gray));
                            lnameLL.setVisibility(VISIBLE);
                            lnameTV.setText("Field Required Mininmum 2 characters");
                        }else {
                        lnametil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(lnametil, getColor(R.color.light_gray));
                        lnameLL.setVisibility(VISIBLE);
                        lnameTV.setText("Field Required");
                    }
                } else {
                    focusedID = lnameET.getId();
                    lnameET.setHint("LastName");
                    lnametil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                    Utils.setUpperHintColor(lnametil, getColor(R.color.primary_green));
                    lnameLL.setVisibility(GONE);
                }
            }
        });
        dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (Utils.isKeyboardVisible)
                    Utils.hideKeypad(AddBenifitialOwnerActivity1.this);
            }
        });
        ssnET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    ssnET.setHint("");
                    if (ssnET.getText().toString().trim().length() > 0 && ssnET.getText().toString().trim().length() <= 8) {
                        ssnLL.setVisibility(GONE);
                        ssntil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(ssntil, getColor(R.color.primary_black));

                    } else {
                        ssntil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(ssntil, getColor(R.color.light_gray));
                        ssnLL.setVisibility(VISIBLE);
                        ssnTV.setText("Field Required");
                    }
                } else {
                    focusedID = ssnET.getId();
                    ssnET.setHint("•••-••-••••");
                    ssntil.setBoxStrokeColorStateList(Utils.getNormalColorState());
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
                        ownershiptil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                        Utils.setUpperHintColor(ownershiptil, getColor(R.color.primary_black));

                    } else {
                        ownershiptil.setBoxStrokeColorStateList(Utils.getErrorColorState());
                        Utils.setUpperHintColor(ownershiptil, getColor(R.color.light_gray));
                        ownershipLL.setVisibility(VISIBLE);
                        ownershipTV.setText("Field Required");
                    }
                } else {
                    focusedID = ownershipET.getId();
                    ownershipET.setHint("Ownership %");
                    ownershiptil.setBoxStrokeColorStateList(Utils.getNormalColorState());
                    Utils.setUpperHintColor(ownershiptil, getColor(R.color.primary_green));
                    ownershipLL.setVisibility(GONE);
                }
            }
        });
    }
        private void enableOrDisableNext() {

            try {
//                if (isfname && islname){
//                    isNextEnabled = true;
//                    CompleteIV.setVisibility(VISIBLE);
//                }
                 if (isfname && islname && isDOBSelected && isssn && isownership) {
                    isNextEnabled = true;
//                     CompleteIV.setVisibility(VISIBLE);
                    nextcv.setCardBackgroundColor(getResources().getColor(R.color.primary_green));
                    Log.e("All boolean", isfname + " " + islname + " "+ isDOBSelected + " " + isssn + " " + isownership);
                } else {

                    Log.e("All boolean", isfname + " " + islname + " " + isDOBSelected + " " + isssn + " " + isownership);

                    isNextEnabled = false;
                    nextcv.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(isNextEnabled) {
                nextcv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddBenifitialOwnerActivity1.this, AddBenifitialOwnerActivity2.class);
                        startActivity(intent);
                    }
                });
            }else{
                nextcv.setClickable(false);
            }
        }

    }



