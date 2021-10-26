package com.greenbox.coyni.view;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.greenbox.coyni.R;
import java.util.ArrayList;

public class PINActivity extends AppCompatActivity implements View.OnClickListener {
    View chooseCircleOne,chooseCircleTwo,chooseCircleThree,chooseCircleFour,chooseCircleFive,chooseCircleSix;
    View confirmCircleOne,confirmCircleTwo,confirmCircleThree,confirmCircleFour,confirmCircleFive,confirmCircleSix;
    TextView keyZeroTV,keyOneTV,keyTwoTV,keyThreeTV,keyFourTV,keyFiveTV,keySixTV,keySevenTV,keyEightTV,keyNineTV;
    ImageView backActionIV;
    ArrayList<String> chooseList=new ArrayList<>();
    ArrayList<String> confirmList=new ArrayList<>();
    ArrayList<String> enterList=new ArrayList<>();
    String passcode="",passcodeConfirm="",passcodeEntered="",num1,num2,num3,num4,num5,num6, TYPE;
    LinearLayout choosePINLL, confirmPINLL, enterPINLL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_keyboard);

        choosePINLL = findViewById(R.id.choosePINLL);
        confirmPINLL = findViewById(R.id.confirmPINLL);
        enterPINLL = findViewById(R.id.enterPINLL);

        TYPE = getIntent().getStringExtra("TYPE");
        if(TYPE.equals("CHOOSE")){
            choosePINLL.setVisibility(View.VISIBLE);
            confirmPINLL.setVisibility(View.GONE);
        }else if(TYPE.equals("CONFIRM")){
            choosePINLL.setVisibility(View.GONE);
            confirmPINLL.setVisibility(View.VISIBLE);
        }
        initializeComponents();

    }

    private void initializeComponents() {

        chooseCircleOne=(View) findViewById(R.id.chooseCircleOne);
        chooseCircleTwo=(View) findViewById(R.id.chooseCircleTwo);
        chooseCircleThree=(View) findViewById(R.id.chooseCircleThree);
        chooseCircleFour=(View) findViewById(R.id.chooseCircleFour);
        chooseCircleFive=(View) findViewById(R.id.chooseCircleFive);
        chooseCircleSix=(View) findViewById(R.id.chooseCircleSix);

        confirmCircleOne=(View) findViewById(R.id.confirmCircleOne);
        confirmCircleTwo=(View) findViewById(R.id.confirmCircleTwo);
        confirmCircleThree=(View) findViewById(R.id.confirmCircleThree);
        confirmCircleFour=(View) findViewById(R.id.confirmCircleFour);
        confirmCircleFive=(View) findViewById(R.id.confirmCircleFive);
        confirmCircleSix=(View) findViewById(R.id.confirmCircleSix);

        keyZeroTV=(TextView) findViewById(R.id.keyZeroTV);
        keyOneTV=(TextView) findViewById(R.id.keyOneTV);
        keyTwoTV=(TextView) findViewById(R.id.keyTwoTV);
        keyThreeTV=(TextView) findViewById(R.id.keyThreeTV);
        keyFourTV=(TextView) findViewById(R.id.keyFourTV);
        keyFiveTV=(TextView) findViewById(R.id.keyFiveTV);
        keySixTV=(TextView) findViewById(R.id.keySixTV);
        keySevenTV=(TextView) findViewById(R.id.keySevenTV);
        keyEightTV=(TextView) findViewById(R.id.keyEightTV);
        keyNineTV=(TextView) findViewById(R.id.keyNineTV);
        backActionIV=(ImageView) findViewById(R.id.backActionIV);

        keyZeroTV.setOnClickListener(this);
        keyOneTV.setOnClickListener(this);
        keyTwoTV.setOnClickListener(this);
        keyThreeTV.setOnClickListener(this);
        keyFourTV.setOnClickListener(this);
        keyFiveTV.setOnClickListener(this);
        keySixTV.setOnClickListener(this);
        keySevenTV.setOnClickListener(this);
        keyEightTV.setOnClickListener(this);
        keyNineTV.setOnClickListener(this);
        backActionIV.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.keyZeroTV:

                if(TYPE.equals("CHOOSE")){
                    chooseList.add("0");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("0");
                    passNumberConfirm(confirmList);
                }

                break;
            case R.id.keyOneTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("1");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("1");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.keyTwoTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("2");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("2");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.keyThreeTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("3");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("3");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.keyFourTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("4");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("4");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.keyFiveTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("5");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("5");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.keySixTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("6");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("6");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.keySevenTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("7");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("7");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.keyEightTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("8");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("8");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.keyNineTV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.add("9");
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.add("9");
                    passNumberConfirm(confirmList);
                }
                break;
            case R.id.backActionIV:
                if(TYPE.equals("CHOOSE")){
                    chooseList.clear();
                    passNumber(chooseList);
                } else if(TYPE.equals("CONFIRM")){
                    confirmList.clear();
                    passNumberConfirm(confirmList);
                }
                break;

        }

    }

    private void passNumber(ArrayList<String> number_list) {

        if(number_list.size()==0){
            chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle_white);
        } else {
            switch (number_list.size()){
                case 1:
                    num1=number_list.get(0);
                    chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 2:
                    num2=number_list.get(1);
                    chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 3:
                    num3=number_list.get(2);
                    chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 4:
                    num4=number_list.get(3);
                    chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 5:
                    num5=number_list.get(4);
                    chooseCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 6:
                    num6=number_list.get(5);
                    chooseCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle);
                    passcode=num1+num2+num3+num4+num5+num6;
                    if(getPasscode().length()==0){
                        savePasscode(passcode);

                    }
                    else {
                        matchPasscode();
                    }
                    break;

            }
        }
    }

    private void passNumberConfirm(ArrayList<String> number_list) {

        if(number_list.size()==0){
            confirmCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            confirmCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            confirmCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            confirmCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            confirmCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            confirmCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle_white);
        } else {
            switch (number_list.size()){
                case 1:
                    num1=number_list.get(0);
                    confirmCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 2:
                    num2=number_list.get(1);
                    confirmCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 3:
                    num3=number_list.get(2);
                    confirmCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 4:
                    num4=number_list.get(3);
                    confirmCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 5:
                    num5=number_list.get(4);
                    confirmCircleFive.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 6:
                    num6=number_list.get(5);
                    confirmCircleSix.setBackgroundResource(R.drawable.ic_baseline_circle);
                    passcodeConfirm=num1+num2+num3+num4+num5+num6;
                    if(getPasscode().length()==0){
                        savePasscode(passcodeConfirm);
                    }
                    else {
                        matchPasscodeConfirm();
                    }
                    break;

            }
        }
    }

    private void matchPasscode() {

        if (getPasscode().equals(passcode)){
            choosePINLL.setVisibility(View.GONE);
            confirmPINLL.setVisibility(View.VISIBLE);
            TYPE = "CONFIRM";
        } else {
            choosePINLL.setVisibility(View.VISIBLE);
            confirmPINLL.setVisibility(View.GONE);
//            Toast.makeText(this, "Passcode doest match plzz try again!!!"+getPasscode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void matchPasscodeConfirm() {
        if (getPasscode().equals(passcodeConfirm)){
            startActivity(new Intent(this, EnableFaceID.class));
        }
    }

    private SharedPreferences.Editor savePasscode(String passcode){
        SharedPreferences preferences=getSharedPreferences("passcode_pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("passcode",passcode);
        editor.commit();
        return editor;

    }

    private String getPasscode(){
        SharedPreferences preferences=getSharedPreferences("passcode_pref",Context.MODE_PRIVATE);
        return preferences.getString("passcode","");
    }
}