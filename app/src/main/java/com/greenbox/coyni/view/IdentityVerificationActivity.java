package com.greenbox.coyni.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.IdVeBottomSheetFragment;
import com.greenbox.coyni.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IdentityVerificationActivity extends AppCompatActivity {
    TextInputLayout dobTIL,ssnTIL;
    TextInputEditText dobET,ssnET;
    TextView idveriUItext,idveriUItextSuc,exitBtn,btnExit;
    ConstraintLayout idveriDOBConLayout;
    LinearLayout bottomSheet;
    CardView btnNext;
    ScrollView secondIVeri;
    LinearLayout firstIVeri;
    View viewLeft, viewRight;
    ImageButton closebtn, backbtn;
    ImageView upIdSuccessImg;
    final Calendar myCalendar=Calendar.getInstance();
    int mYear, mMonth, mDay;
    CardView buttonSubmit;
    String dateFormat,date;
    DatePickerDialog.OnDateSetListener onDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_verification);
        bottomSheet = findViewById(R.id.clickBottomSheet);
        idveriUItext=findViewById(R.id.idveriUpIdTxt);
        idveriUItextSuc=findViewById(R.id.idveriUpIdSuccessTxt);
        exitBtn=findViewById(R.id.exitBtn);
        btnExit=findViewById(R.id.btnExit);
        btnNext = findViewById(R.id.nextBtn);
        firstIVeri = findViewById(R.id.linearLayoutIdVe);
        secondIVeri = findViewById(R.id.scrlViewIV2nd);
        viewLeft = findViewById(R.id.viewBarLeft);
        viewRight = findViewById(R.id.viewBarRight);
        closebtn = findViewById(R.id.closeBtn);
        backbtn = findViewById(R.id.backBtn);
        dobET=findViewById(R.id.idveriDOBET);
        dobTIL=findViewById(R.id.idveriDOBTIL);
        buttonSubmit = findViewById(R.id.submitBtn);
        upIdSuccessImg=findViewById(R.id.idveriUpIdSuccessImg);
        dobET.setInputType(InputType.TYPE_NULL);
        idveriDOBConLayout=findViewById(R.id.idveriDOBCL);
        ssnTIL=findViewById(R.id.idveriSSNTIL);
        ssnET=findViewById(R.id.idVeriSSNET);


        idveriDOBConLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DatePickerDialog datePickerDialog=new DatePickerDialog(IdentityVerificationActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,onDateSetListener,year,month,day);
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                datePickerDialog.show();

                setToDate(dobET);
            }
        });
        dobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DatePickerDialog datePickerDialog=new DatePickerDialog(IdentityVerificationActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,onDateSetListener,year,month,day);
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                datePickerDialog.show();

                setToDate(dobET);
            }
        });
       dobET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
               if(b){
//                   DatePickerDialog datePickerDialog=new DatePickerDialog(IdentityVerificationActivity.this,android.R.style.Theme_Holo_Light_Dialog_MinWidth,onDateSetListener,year,month,day);
//                    datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    datePickerDialog.show();

                   setToDate(dobET);
                }
           }
       });

        onDateSetListener =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month=month+1;
                date=new String(day+"/"+month+"/"+year);
                dateFormat=convertDate(date);
                Log.e("date",""+date);
                Log.e("date",""+dateFormat);
                dobET.setText(dateFormat);
                long years = 568025136000L;
                long yearsback=myCalendar.getTimeInMillis() - years;


            }
        };







        bottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IdVeBottomSheetFragment idVeBottomSheetFragment = new IdVeBottomSheetFragment();
                idVeBottomSheetFragment.show(getSupportFragmentManager(), idVeBottomSheetFragment.getTag());
                idveriUItext.setVisibility(View.GONE);
                idveriUItextSuc.setVisibility(View.VISIBLE);
                upIdSuccessImg.setVisibility(View.VISIBLE);

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                firstIVeri.setVisibility(View.GONE);
                secondIVeri.setVisibility(View.VISIBLE);
                viewLeft.setBackgroundResource(R.drawable.button_background1);
                viewRight.setBackgroundResource(R.drawable.button_background);
                closebtn.setVisibility(View.GONE);
                backbtn.setVisibility(View.VISIBLE);
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

                                String dateToConvert = Utils.changeFormat(dayOfMonth) + "/" + Utils.changeFormat((monthOfYear + 1)) +"/" +  year;
                                String convertedDate = convertDate(dateToConvert);
                                dob.setText(convertedDate);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }, mYear, mMonth, mDay);

            long years = 568025136000L;
            long yearsback=c.getTimeInMillis() - years;
            datePickerDialog.getDatePicker().setMaxDate(yearsback);
            datePickerDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}