package com.coyni.mapp.utils.keyboards;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coyni.mapp.R;
import com.coyni.mapp.view.AddCardActivity;
import com.coyni.mapp.view.BusinessReceivePaymentActivity;
import com.coyni.mapp.view.BuyTokenActivity;
import com.coyni.mapp.view.BuyTokenPaymentMethodsActivity;
import com.coyni.mapp.view.ScanActivity;
import com.coyni.mapp.view.WithdrawPaymentMethodsActivity;
import com.coyni.mapp.view.WithdrawTokenActivity;
import com.coyni.mapp.view.business.PayToMerchantActivity;
import com.coyni.mapp.view.business.RefundTransactionActivity;

public class CustomKeyboard extends LinearLayout implements View.OnClickListener {

    private TextView keyOne, keyTwo, keyThree, keyFour, keyFive, keySix, keySeven, keyEight, keyNine, keyZero, keyDot, keyActionText;
    private LinearLayout keyBack, keyAction;
    private SparseArray<String> keyValues = new SparseArray<>();
    InputConnection inputConnection;
    Context mContext, activityContext;
    String strScreen = "";
    String enteredText = "";

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
//        keyBack.setOnClickListener(this);

        keyAction = findViewById(R.id.keyActionLL);
//        keyAction.setOnClickListener(this);

        keyAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    switch (strScreen) {
                        case "addcard":
                            AddCardActivity.addCardActivity.verifyClick();
                            break;
                        case "cvv":
                            BuyTokenPaymentMethodsActivity bpm = (BuyTokenPaymentMethodsActivity) activityContext;
                            bpm.okClick();
                            break;
                        case "buy":
                            BuyTokenActivity.buyTokenActivity.buyTokenClick();
                            break;
                        case "buycvv":
                            BuyTokenActivity.buyTokenActivity.okClick();
                            break;
                        case "withdraw":
                            WithdrawTokenActivity.withdrawTokenActivity.withdrawTokenClick();
                            break;
                        case "setAmount":
                            ScanActivity.scanActivity.setAmountClick();
                            break;
                        case "receivables":
                            BusinessReceivePaymentActivity.businessreceivePaymentActivity.setAmountClick();
                            break;
                        case "payToMerch":
                            PayToMerchantActivity.payToMerchantActivity.payAmountClick();
                            break;
                        case "refundables":
                            RefundTransactionActivity.refundTransactionActivity.setRefundAmountClick();
                            break;
                        case "notoken":
                            WithdrawPaymentMethodsActivity wpm = (WithdrawPaymentMethodsActivity) activityContext;
                            wpm.okClick();
                            break;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        keyBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                String chatSet = (String) inputConnection.getSelectedText(0);
                try {

                    inputConnection.deleteSurroundingText(1, 0);
                    enteredText = enteredText.substring(0, enteredText.length() - 1);

//                    inputConnection.deleteSurroundingText(1, 0);
//                    StringBuilder stringBuilder = new StringBuilder(enteredText);
//                    stringBuilder.deleteCharAt(inputConnection.getTextBeforeCursor(1, 0).length());
//                    enteredText = stringBuilder.toString();

                    if (strScreen.equals("addcard")) {
                        AddCardActivity.addCardActivity.enableOrDisableFocus(enteredText);
                    }
                    if (strScreen.equals("refundables")) {
                        RefundTransactionActivity.refundTransactionActivity.clearAmountCards();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (strScreen.equals("addcard")) {
                        AddCardActivity.addCardActivity.enableOrDisableFocus(enteredText);
                    }

                }
            }
        });


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
        try {
            if (inputConnection == null) {
                CharSequence selectedText = inputConnection.getSelectedText(0);
                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0);

                } else {
                    inputConnection.commitText("", 1);
                }
            } else {
                if (strScreen.equals("refundables")) {
                    RefundTransactionActivity refundTransactionActivity = (RefundTransactionActivity) activityContext;
                    String value = keyValues.get(view.getId());
                    if ((refundTransactionActivity.isfullamount || refundTransactionActivity.ishalfamount) && !value.equals(".")) {

                        refundTransactionActivity.isfullamount = false;
                        refundTransactionActivity.ishalfamount = false;
                        refundTransactionActivity.refundET.setText("");
                        enteredText = "";
                        refundTransactionActivity.clearAmountCards();
                    }
                }
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
                        if (strScreen.equals("addcard")) {
                            AddCardActivity.addCardActivity.enableOrDisableFocus(enteredText);
                        }
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

    public void setKeyAction(String actionName, Context context) {
        keyActionText.setText(actionName);
        activityContext = context;
    }


    public void setScreenName(String screenName) {
        strScreen = screenName;
    }

    public void enableButton() {
        keyAction.setBackgroundResource(R.drawable.custom_keyboard_action_btn_bg);
        keyAction.setEnabled(true);
    }

    public void disableButton() {
        keyAction.setBackgroundResource(R.drawable.custom_keyboard_action_btn_disable_bg);
        keyAction.setEnabled(false);
    }

    public void clearData() {
        enteredText = "";
    }

    public void setText(String strText) {
        enteredText = strText;
    }

    public void setEnteredText(String text) {
        enteredText = text.trim();
    }
}