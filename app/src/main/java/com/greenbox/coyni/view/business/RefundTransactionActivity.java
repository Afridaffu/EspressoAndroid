package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.utils.DatabaseHandler;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.view.BusinessReceivePaymentActivity;
import com.greenbox.coyni.view.PayRequestActivity;
import com.greenbox.coyni.view.WithdrawTokenActivity;

public class RefundTransactionActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    private ImageView refundBackIV;
    private TextView etremarksTV;
    private EditText refundET, addNoteET;
    private TextView refundcurrencyTV;
    private LinearLayout remarksll;
    private CustomKeyboard cKey;
    private Long mLastClickTime = 0L, bankId, cardId;
    private float fontSize, dollarFont;
    private Dialog payDialog, prevDialog, cvvDialog;
    private Double maxValue = 0.0, pfee = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    private Double usdValue = 0.0, cynValue = 0.0, total = 0.0, cynValidation = 0.0, avaBal = 0.0;
    boolean isAuthenticationCalled = false, isPayClickable = false, isReqClickable = false, isPayClick = false;
    public static RefundTransactionActivity refundTransactionActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_transaction);
        initialization();
        refundBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



        private void setRefundAmountClick() {
        try {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            cynValue = Double.parseDouble(refundET.getText().toString().trim());
            mLastClickTime = SystemClock.elapsedRealtime();
//            isButtonClick = true;
//            convertUSDtoCYN();
//            calculateFee(Utils.USNumberFormat(cynValue));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    private void initialization() {

        objMyApplication = (MyApplication) getApplicationContext();
        etremarksTV = findViewById(R.id.eTremarks);
        refundBackIV = findViewById(R.id.RefundbackIV);
        refundET = findViewById(R.id.refundAmountET);
        refundcurrencyTV = findViewById(R.id.refundCurrencyTV);
        remarksll = findViewById(R.id.remarksLL);

        cKey = findViewById(R.id.ckbrefund);
        InputConnection ic = refundET.onCreateInputConnection(new EditorInfo());
        cKey.setInputConnection(ic);
        cKey.setKeyAction("Refund", RefundTransactionActivity.this);
        cKey.setScreenName("refundables");
//        refundET.addTextChangedListener(this);
        if (getIntent().getStringExtra("amount") != null && !getIntent().getStringExtra("amount").equals("")) {
            refundET.setText(getIntent().getStringExtra("amount"));
//            USFormat(refundET);
            refundET.setEnabled(false);
        } else {
            //enableButtons();
            cKey = (CustomKeyboard) findViewById(R.id.ckbrefund);
            InputConnection i = refundET.onCreateInputConnection(new EditorInfo());
            cKey.setInputConnection(i);
        }
        refundET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        remarksll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                displayComments();
            }
        });

    }

    protected void onResume() {
        try {
            if (cvvDialog != null && addNoteET.hasFocus()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addNoteET.requestFocus();
                    }
                }, 100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        super.onResume();
    }

    private void displayComments() {
        try {
            cvvDialog = new Dialog(RefundTransactionActivity.this);
            cvvDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            cvvDialog.setContentView(R.layout.add_note_layout);
            cvvDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            addNoteET = cvvDialog.findViewById(R.id.addNoteET);
            CardView doneBtn = cvvDialog.findViewById(R.id.doneBtn);
            TextInputLayout addNoteTIL = cvvDialog.findViewById(R.id.etlMessage);
            LinearLayout cancelBtn = cvvDialog.findViewById(R.id.cancelBtn);


            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cvvDialog.dismiss();
                    Utils.hideKeypad(RefundTransactionActivity.this);
                }
            });
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        etremarksTV.setText(addNoteET.getText().toString().trim());
                        cvvDialog.dismiss();
                        Utils.hideKeypad(RefundTransactionActivity.this);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            addNoteET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 0) {
                        addNoteTIL.setCounterEnabled(false);
                    } else {
                        addNoteTIL.setCounterEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = addNoteET.getText().toString();
                        if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                            addNoteET.setText("");
                            addNoteET.setSelection(addNoteET.getText().length());
                        } else if (str.length() > 0 && str.contains(".")) {
                            addNoteET.setText(addNoteET.getText().toString().replaceAll("\\.", ""));
                            addNoteET.setSelection(addNoteET.getText().length());
                        } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                            addNoteET.setText("");
                            addNoteET.setSelection(addNoteET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            if (!etremarksTV.getText().toString().trim().equals("")) {
                addNoteET.setText(etremarksTV.getText().toString().trim());
                addNoteET.setSelection(addNoteET.getText().toString().trim().length());
            }
            Window window = cvvDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            cvvDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            cvvDialog.show();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private String USFormat(EditText etAmount) {
//        String strAmount = "", strReturn = "";
//        try {
//            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
//            strReturn = Utils.USNumberFormat(Double.parseDouble(strAmount));
//            changeTextSize(strReturn);
//            setDefaultLength();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return strReturn;
//    }

    private void changeTextSize(String editable) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            if (editable.length() > 12) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
                refundcurrencyTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 8) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                refundcurrencyTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);
            } else if (editable.length() > 5) {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
                refundET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                refundcurrencyTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
            } else {
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                refundET.setTextSize(Utils.pixelsToSp(RefundTransactionActivity.this, fontSize));
                refundcurrencyTV.setTextSize(Utils.pixelsToSp(RefundTransactionActivity.this, dollarFont));
            }
            refundET.setFilters(FilterArray);
            refundET.setSelection(refundET.getText().length());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    private void disableButtons(Boolean value) {
//        try {
//            if (value) {
////                payRequestLL.setBackgroundResource(R.drawable.payrequest_bgcolor);
//                isPayClickable = false;
//                isReqClickable = false;
//            } else {
////                payRequestLL.setBackgroundResource(R.drawable.payrequest_activebg);
//                isPayClickable = true;
//                isReqClickable = true;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

//    private void setDefaultLength() {
//        try {
//            InputFilter[] FilterArray = new InputFilter[1];
//            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
//            refundET.setFilters(FilterArray);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }


}