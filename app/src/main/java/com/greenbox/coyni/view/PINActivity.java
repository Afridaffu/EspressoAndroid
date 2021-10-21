package com.greenbox.coyni.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.greenbox.coyni.R;

import java.util.ArrayList;

public class PINActivity extends AppCompatActivity implements View.OnClickListener {
    View i1,i2,i3,i4,i5,i6;
    TextView b0,b1,b2,b3,b4,b5,b6,b7,b8,b9;
    ImageView b_back;
    ArrayList<String> number_list=new ArrayList<>();
    String passcode="";
    String num1,num2,num3,num4,num5,num6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinkeyboard);
       initializeComponents();
    }

    private void initializeComponents() {

        i1=(View) findViewById(R.id.imageview_circle1);
        i2=(View) findViewById(R.id.imageview_circle2);
        i3=(View) findViewById(R.id.imageview_circle3);
        i4=(View) findViewById(R.id.imageview_circle4);
        i5=(View) findViewById(R.id.imageview_circle5);
        i6=(View) findViewById(R.id.imageview_circle6);

        b0=(TextView) findViewById(R.id.textview_0);
        b1=(TextView) findViewById(R.id.textview_1);
        b2=(TextView) findViewById(R.id.textview_2);
        b3=(TextView) findViewById(R.id.textview_3);
        b4=(TextView) findViewById(R.id.textview_4);
        b5=(TextView) findViewById(R.id.textview_5);
        b6=(TextView) findViewById(R.id.textview_6);
        b7=(TextView) findViewById(R.id.textview_7);
        b8=(TextView) findViewById(R.id.textview_8);
        b9=(TextView) findViewById(R.id.textview_9);
        b_back=(ImageView) findViewById(R.id.button_back1);
        b0.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        b_back.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.textview_0:
                number_list.add("0");
                passNumber(number_list);
                break;
            case R.id.textview_1:
                number_list.add("1");
                passNumber(number_list);
                break;
            case R.id.textview_2:
                number_list.add("2");
                passNumber(number_list);
                break;
            case R.id.textview_3:
                number_list.add("3");
                passNumber(number_list);
                break;
            case R.id.textview_4:
                number_list.add("4");
                passNumber(number_list);
                break;
            case R.id.textview_5:
                number_list.add("5");
                passNumber(number_list);
                break;
            case R.id.textview_6:
                number_list.add("6");
                passNumber(number_list);
                break;
            case R.id.textview_7:
                number_list.add("7");
                passNumber(number_list);
                break;
            case R.id.textview_8:
                number_list.add("8");
                passNumber(number_list);
                break;
            case R.id.textview_9:
                number_list.add("9");
                passNumber(number_list);
                break;
            case R.id.button_back1:
                number_list.clear();
                passNumber(number_list);
                break;

        }

    }

    private void passNumber(ArrayList<String> number_list) {

        if(number_list.size()==0){
            i1.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            i2.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            i3.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            i4.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            i5.setBackgroundResource(R.drawable.ic_baseline_circle_white);
            i6.setBackgroundResource(R.drawable.ic_baseline_circle_white);
        }
        else {
            switch (number_list.size()){
                case 1:
                    num1=number_list.get(0);
                    i1.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 2:
                    num2=number_list.get(1);
                    i2.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 3:
                    num3=number_list.get(2);
                    i3.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 4:
                    num4=number_list.get(3);
                    i4.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 5:
                    num5=number_list.get(4);
                    i5.setBackgroundResource(R.drawable.ic_baseline_circle);
                    break;
                case 6:
                    num6=number_list.get(5);
                    i6.setBackgroundResource(R.drawable.ic_baseline_circle);
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

    private void matchPasscode() {

        if (getPasscode().equals(passcode)){
//            startActivity(new Intent(this,PassCode_Success.class));
        }
        else {
            Toast.makeText(this, "Passcode doest match plzz try again!!!"+getPasscode(), Toast.LENGTH_SHORT).show();
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