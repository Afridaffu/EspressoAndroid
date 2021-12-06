package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.PayAmountBottomSheet;
import com.greenbox.coyni.fragments.RequestAmountBottomSheet;
import com.greenbox.coyni.fragments.ScanActivityBottomSheetDialog;

public class PayRequestTransactionActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout addNoteClick,prLL;
    TextView addNoteTV,coynTV;
    ImageView changeCurreIV;
    Boolean isFieldValid=false,isCurrencyEnable=true,isCynEnable=false;
    //For Custome KeyBoard
    private String strAmount=new String();
    private TextView keyOne,keyTwo,keyThree,keyFour,keyFive,keySix,keySeven,keyEight,keyNine,keyZero,keyDot,keyActionText,keyPay,keyRquest;
    private ImageView keyBack;
    private SparseArray<String> keyValues=new SparseArray<>();
    InputConnection inputConnection;
    EditText payRequestET;
    TextView availTV,availBal,errMinAmount,dollorTV;
    TextView requestTV,payTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_request_transaction);
        try {
            addNoteClick = findViewById(R.id.addNoteClickLL);
            payRequestET=findViewById(R.id.payrequestET);
            availTV=findViewById(R.id.availBalTV);
            availBal=findViewById(R.id.availBal);
            prLL=findViewById(R.id.payRequestLL);
            changeCurreIV=findViewById(R.id.changeCurrencyTypeIV);
            dollorTV=findViewById(R.id.amontDollorTV);
            errMinAmount=findViewById(R.id.minAmountErr);
            coynTV=findViewById(R.id.coyniTV);
            requestTV=findViewById(R.id.requestTV);
            payTV=findViewById(R.id.payTV);
            changeCurreIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isCurrencyEnable) {
                        dollorTV.setVisibility(View.VISIBLE);
                        coynTV.setVisibility(View.GONE);
                        isCurrencyEnable = false;
                        isCynEnable=true;
                    }
                    else if (isCynEnable){
                        dollorTV.setVisibility(View.GONE);
                        coynTV.setVisibility(View.VISIBLE);
                        isCurrencyEnable=true;
                        isCynEnable=false;
                    }
                }
            });

            payRequestET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length()==0){
//                        payRequestET.setError("Minimum amount is [act.limit]CYN");
                        availTV.setVisibility(View.GONE);
                        availBal.setVisibility(View.GONE);
                        errMinAmount.setVisibility(View.VISIBLE);
                        isFieldValid=false;
                        enablePayReBtn();
                    }
                    else if(charSequence.toString().trim().length()>0){
                        availTV.setVisibility(View.VISIBLE);
                        availBal.setVisibility(View.VISIBLE);
                        errMinAmount.setVisibility(View.GONE);
                        isFieldValid=true;
                        enablePayReBtn();
                    }
                    if (charSequence.length()>3){
                        payRequestET.setTextSize(32);
                    }
                    else if (charSequence.length()<=3){
                        payRequestET.setTextSize(54);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            activeButtons();
            addNoteClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ScanActivityBottomSheetDialog scanActivityBottomSheetDialog=new ScanActivityBottomSheetDialog();
                    scanActivityBottomSheetDialog.show(getSupportFragmentManager(),"TAG");

                }
            });

            if (!(payRequestET.getText().toString().isEmpty())&&!(addNoteTV.getText().toString().isEmpty())){
                prLL.setBackgroundResource(R.drawable.bg_core_colorfill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enablePayReBtn() {


        payTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isFieldValid) {
                        PayAmountBottomSheet payAmountBottomSheet = new PayAmountBottomSheet();
                        payAmountBottomSheet.show(getSupportFragmentManager(), "TAG");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        requestTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (isFieldValid){
                        RequestAmountBottomSheet requestAmountBottomSheet=new RequestAmountBottomSheet();
                        requestAmountBottomSheet.show(getSupportFragmentManager(),"TAG");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (isFieldValid) {
            prLL.setBackgroundResource(R.drawable.payrequest_activebg);
        } else {

            prLL.setBackgroundResource(R.drawable.payrequest_bgcolor);
        }
    }


    private void activeButtons() {


        try {
            keyOne=findViewById(R.id.keyOneTV);
            keyOne.setOnClickListener(this);

            keyTwo=findViewById(R.id.keyTwoTV);
            keyTwo.setOnClickListener(this);

            keyThree=findViewById(R.id.keyThreeTV);
            keyThree.setOnClickListener(this);

            keyFour=findViewById(R.id.keyFourTV);
            keyFour.setOnClickListener(this);

            keyFive=findViewById(R.id.keyFiveTV);
            keyFive.setOnClickListener(this);

            keySix=findViewById(R.id.keySixTV);
            keySix.setOnClickListener(this);

            keySeven=findViewById(R.id.keySevenTV);
            keySeven.setOnClickListener(this);

            keyEight=findViewById(R.id.keyEightTV);
            keyEight.setOnClickListener(this);

            keyNine=findViewById(R.id.keyNineTV);
            keyNine.setOnClickListener(this);

            keyZero=findViewById(R.id.keyZeroTV);
            keyZero.setOnClickListener(this);

            keyDot=findViewById(R.id.keyDotTV);
            keyDot.setOnClickListener(this);

            keyPay=findViewById(R.id.payTV);
            keyPay.setOnClickListener(this);

            keyRquest=findViewById(R.id.requestTV);
            keyRquest.setOnClickListener(this);
            keyBack=findViewById(R.id.backActionIV);
            keyBack.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        keyActionText=view.findViewById(R.id.keyActionTV);
//        keyValues.put(R.id.keyActionLL,"");
    }

    public void getValue(String s){
        try {
            addNoteTV=findViewById(R.id.addNoteTV);
            addNoteTV.setText(s);
            addNoteTV.setTextColor(getColor(R.color.black));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.keyZeroTV:
                    strAmount+=0;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyOneTV:
                    strAmount+=1;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyTwoTV:
                    strAmount+=2;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyThreeTV:
                    strAmount+=3;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyFourTV:
                    strAmount+=4;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyFiveTV:
                    strAmount+=5;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keySixTV:
                    strAmount+=6;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keySevenTV:
                    strAmount+=7;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyEightTV:
                    strAmount+=8;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyNineTV:
                    strAmount+=9;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyDotTV:
                   strAmount+=".";
                   payRequestET.setText(strAmount);
                    break;
                case R.id.backActionIV:
                    if (strAmount.length()>0){
                        strAmount=strAmount.substring(0,strAmount.length()-1);
                        payRequestET.setText(strAmount);
                    }

                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}