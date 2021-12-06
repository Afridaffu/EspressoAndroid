package com.greenbox.coyni.utils.keyboards;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.AddCardActivity;

public class CustomKeyboard extends LinearLayout implements View.OnClickListener {

    private TextView keyOne, keyTwo, keyThree, keyFour, keyFive, keySix, keySeven, keyEight, keyNine, keyZero, keyDot, keyActionText;
    private LinearLayout keyBack, keyAction;
    private SparseArray<String> keyValues = new SparseArray<>();
    InputConnection inputConnection;
    Context mContext;
    String strScreen = "";

    public CustomKeyboard(Context context) {
        this(context, null, 0);

    }

    public CustomKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CustomKeyboard(Context context, AttributeSet attrs, int defStyleattr) {
        super(context, attrs, defStyleattr);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attributeSet) {
        LayoutInflater.from(context).inflate(R.layout.activity_custom_keyboard, this, true);
        mContext = context;
        keyOne = findViewById(R.id.keyOneTV);
        keyOne.setOnClickListener(this);

        keyTwo = findViewById(R.id.keyTwoTV);
        keyTwo.setOnClickListener(this);

        keyThree = findViewById(R.id.keyThreeTV);
        keyThree.setOnClickListener(this);

        keyFour = findViewById(R.id.keyFourTV);
        keyFour.setOnClickListener(this);

        keyFive = findViewById(R.id.keyFiveTV);
        keyFive.setOnClickListener(this);

        keySix = findViewById(R.id.keySixTV);
        keySix.setOnClickListener(this);

        keySeven = findViewById(R.id.keySevenTV);
        keySeven.setOnClickListener(this);

        keyEight = findViewById(R.id.keyEightTV);
        keyEight.setOnClickListener(this);

        keyNine = findViewById(R.id.keyNineTV);
        keyNine.setOnClickListener(this);

        keyZero = findViewById(R.id.keyZeroTV);
        keyZero.setOnClickListener(this);

        keyDot = findViewById(R.id.keyDotTV);
        keyDot.setOnClickListener(this);

        keyBack = findViewById(R.id.keyBackLL);
        keyBack.setOnClickListener(this);

        keyAction = findViewById(R.id.keyActionLL);
        keyAction.setOnClickListener(this);

        keyActionText = findViewById(R.id.keyActionTV);

        keyValues.put(R.id.keyZeroTV, "0");
        keyValues.put(R.id.keyOneTV, "1");
        keyValues.put(R.id.keyTwoTV, "2");
        keyValues.put(R.id.keyThreeTV, "3");
        keyValues.put(R.id.keyFourTV, "4");
        keyValues.put(R.id.keyFiveTV, "5");
        keyValues.put(R.id.keySixTV, "6");
        keyValues.put(R.id.keySevenTV, "7");
        keyValues.put(R.id.keyEightTV, "8");
        keyValues.put(R.id.keyNineTV, "9");
        keyValues.put(R.id.keyDotTV, ".");
        keyValues.put(R.id.keyActionLL, "");


    }

    @Override
    public void onClick(View view) {
        if (inputConnection == null) {
            CharSequence selectedText = inputConnection.getSelectedText(0);
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0);

            } else {
                inputConnection.commitText("", 1);
            }
        } else {
            String value = keyValues.get(view.getId());
            try {
                inputConnection.commitText(value, 1);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        keyBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String chatSet = (String) inputConnection.getSelectedText(0);
                try {
                    inputConnection.deleteSurroundingText(1, 0);
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        });
        keyAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Toast.makeText(mContext, "Verify click", Toast.LENGTH_LONG).show();
                    if (strScreen.equals("addcard")) {
                        AddCardActivity.addCardActivity.verifyClick();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    public void setInputConnection(InputConnection ic) {
        inputConnection = ic;
    }

    public void setKeyAction(String actionName) {
        keyActionText.setText(actionName);
    }


    public void setScreenName(String screenName) {
        strScreen = screenName;
    }
}