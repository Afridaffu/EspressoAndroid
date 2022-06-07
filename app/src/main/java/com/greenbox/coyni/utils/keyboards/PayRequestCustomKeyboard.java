package com.greenbox.coyni.utils.keyboards;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.greenbox.coyni.R;

public class PayRequestCustomKeyboard extends LinearLayout implements View.OnClickListener {
    private TextView keyOne, keyTwo, keyThree, keyFour, keyFive, keySix, keySeven, keyEight, keyNine, keyZero, keyDot, keyActionText;
    private ImageView keyBack;
    private SparseArray<String> keyValues = new SparseArray<>();
    InputConnection inputConnection;
    Context mContext;
    String strScreen = "";
    String enteredText = "";

    public PayRequestCustomKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PayRequestCustomKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PayRequestCustomKeyboard(Context context) {
        super(context);
    }


    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.payrequest_customkeyboard, this, true);
        mContext = context;

        keyOne = findViewById(R.id.prkeyOneTV);
        keyOne.setOnClickListener(this);

        keyTwo = findViewById(R.id.prkeyTwoTV);
        keyTwo.setOnClickListener(this);

        keyThree = findViewById(R.id.prkeyThreeTV);
        keyThree.setOnClickListener(this);

        keyFour = findViewById(R.id.prkeyFourTV);
        keyFour.setOnClickListener(this);

        keyFive = findViewById(R.id.prkeyFiveTV);
        keyFive.setOnClickListener(this);

        keySix = findViewById(R.id.prkeySixTV);
        keySix.setOnClickListener(this);

        keySeven = findViewById(R.id.prkeySevenTV);
        keySeven.setOnClickListener(this);

        keyEight = findViewById(R.id.prkeyEightTV);
        keyEight.setOnClickListener(this);

        keyNine = findViewById(R.id.prkeyNineTV);
        keyNine.setOnClickListener(this);

        keyZero = findViewById(R.id.prkeyZeroTV);
        keyZero.setOnClickListener(this);

        keyDot = findViewById(R.id.prkeyDotTV);
        keyDot.setOnClickListener(this);

        keyBack = findViewById(R.id.prbackActionIV);
//        keyBack.setOnClickListener(this);


        keyBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String chatSet = (String) inputConnection.getSelectedText(0);
                try {
                    inputConnection.deleteSurroundingText(1, 0);
                    enteredText = enteredText.substring(0, enteredText.length() - 1);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
                Log.e("entered back", enteredText);
            }
        });


        keyValues.put(R.id.prkeyZeroTV, "0");
        keyValues.put(R.id.prkeyOneTV, "1");
        keyValues.put(R.id.prkeyTwoTV, "2");
        keyValues.put(R.id.prkeyThreeTV, "3");
        keyValues.put(R.id.prkeyFourTV, "4");
        keyValues.put(R.id.prkeyFiveTV, "5");
        keyValues.put(R.id.prkeySixTV, "6");
        keyValues.put(R.id.prkeySevenTV, "7");
        keyValues.put(R.id.prkeyEightTV, "8");
        keyValues.put(R.id.prkeyNineTV, "9");
        keyValues.put(R.id.prkeyDotTV, ".");


    }

    @Override
    public void onClick(View view) {
        try {
            if (inputConnection == null) {
                CharSequence selectedText = inputConnection.getSelectedText(0);
                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0);

                } else {
                    inputConnection.commitText("", 1);
                }
            } else {
                if (enteredText.length() < 8) {
                    String value = keyValues.get(view.getId());
//                inputConnection.commitText(value, 1);
                    if ((enteredText.equals("") || enteredText.contains(".") || (strScreen.equals("addcard") && enteredText.length() == 3)) && value.equals(".")) {

                    } else {
//                    String[] split = enteredText.split("\\.");
//                    if (split.length == 2 && split[1].length() == 2) {
//
//                    } else {
                        enteredText = enteredText + value;
                        inputConnection.commitText(value, 1);
//                    }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setInputConnection(InputConnection ic) {
        inputConnection = ic;
    }

    public void setScreenName(String screenName) {
        strScreen = screenName;
    }

    public void clearData() {
        enteredText = "";
    }

    public void setEnteredText(String text) {
        enteredText = text.trim();
    }
}
