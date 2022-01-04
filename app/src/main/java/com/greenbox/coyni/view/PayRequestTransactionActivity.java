package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

public class PayRequestTransactionActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout addNoteClick, prLL, topLL, lyPayClose;
    TextView addNoteTV, coynTV;
    ImageView changeCurreIV;
    Boolean isFieldValid = false, isCurrencyEnable = true, isCynEnable = false;
    String strWalletId = "", reciepientAddress = "", strUserName = "";
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;
    BuyTokenViewModel buyTokenViewModel;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;
    //For Custome KeyBoard
    private String strAmount = new String();
    private String messagePayReq = "", userBalance;
    EditText addnote;
    private TextView keyOne, keyTwo, keyThree, keyFour, keyFive, keySix, keySeven, keyEight, keyNine, keyZero, keyDot, keyActionText, keyPay, keyRquest;
    private ImageView keyBack;
    int requestedToUserId = 0;
    EditText payRequestET;
    TextView availTV, availBal, errMinAmount, dollorTV;
    TextView requestTV, payTV;
    TransactionLimitResponse objResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pay_request_transaction);
            initialization();
            initObservers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialization() {
        try {
            addNoteClick = findViewById(R.id.addNoteClickLL);
            addNoteTV = findViewById(R.id.addNoteTV);
            payRequestET = findViewById(R.id.payrequestET);
            availTV = findViewById(R.id.availBalTV);
            availBal = findViewById(R.id.availBal);
            prLL = findViewById(R.id.payRequestLL);
            topLL = findViewById(R.id.topLL);
            lyPayClose = findViewById(R.id.lyPayClose);
            dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            objMyApplication = (MyApplication) getApplicationContext();
            availBal.setText(Utils.USNumberFormat(objMyApplication.getGBTBalance()));
            changeCurreIV = findViewById(R.id.changeCurrencyTypeIV);
            dollorTV = findViewById(R.id.amontDollorTV);
            errMinAmount = findViewById(R.id.minAmountErr);
            coynTV = findViewById(R.id.coyniTV);
            requestTV = findViewById(R.id.requestTV);
            payTV = findViewById(R.id.payTV);

            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                strWalletId = getIntent().getStringExtra("walletId");
                if (Utils.checkInternet(PayRequestTransactionActivity.this)) {
                    dashboardViewModel.getUserDetail(strWalletId);
                } else {
                    Utils.displayAlert(getString(R.string.internet), PayRequestTransactionActivity.this, "", "");
                }
            }

            changeCurreIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String enteredAmount = "";
                        if (!payRequestET.getText().toString().contains(",")) {
                            enteredAmount = Utils.convertBigDecimalUSDC(payRequestET.getText().toString());
                            payRequestET.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
                        }

                        if (isCurrencyEnable) {
                            dollorTV.setVisibility(View.VISIBLE);
                            coynTV.setVisibility(View.GONE);
                            isCurrencyEnable = false;
                            isCynEnable = true;
                        } else if (isCynEnable) {
                            dollorTV.setVisibility(View.GONE);
                            coynTV.setVisibility(View.VISIBLE);
                            isCurrencyEnable = true;
                            isCynEnable = false;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });

            payRequestET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(PayRequestTransactionActivity.this, v);
                }
            });

            payRequestET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length() == 0) {
//                        payRequestET.setError("Minimum amount is [act.limit]CYN");
                        availTV.setVisibility(View.GONE);
                        availBal.setVisibility(View.GONE);
                        errMinAmount.setVisibility(View.VISIBLE);
                        isFieldValid = false;
                        enablePayReBtn();
                    } else if (charSequence.length() > 0) {
                        availTV.setVisibility(View.VISIBLE);
                        availBal.setVisibility(View.VISIBLE);
                        errMinAmount.setVisibility(View.GONE);
                        isFieldValid = true;
                        enablePayReBtn();

                        if (charSequence.charAt(0) == 0) {
                            payRequestET.setText(charSequence.subSequence(1, charSequence.length()));
                        }
                        if (charSequence.length() > 7 && charSequence.length() <= 11) {
                            payRequestET.setTextSize(30);
                            dollorTV.setTextSize(30);
                        }
                        if (charSequence.length() > 5 && charSequence.length() <= 7) {
                            payRequestET.setTextSize(38);
                            dollorTV.setTextSize(38);
                        } else if (charSequence.length() <= 3) {
                            payRequestET.setTextSize(54);
                            dollorTV.setTextSize(54);
                        } else if (charSequence.length() > 3 && charSequence.length() <= 5) {
                            payRequestET.setTextSize(46);
                            dollorTV.setTextSize(46);
                        }
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
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        final Dialog dialog = new Dialog(PayRequestTransactionActivity.this);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.add_note_layout);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        addnote = dialog.findViewById(R.id.addNoteET);
                        LinearLayout closeBtn = dialog.findViewById(R.id.cancelBtn);
                        TextInputLayout addNoteTIL = dialog.findViewById(R.id.etlMessage);
                        CardView doneBtn = dialog.findViewById(R.id.doneBtn);
                        addnote.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                try {
                                    if (charSequence.length() == 0) {
                                        addNoteTIL.setCounterEnabled(false);
                                    } else {
                                        addNoteTIL.setCounterEnabled(true);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                                try {
                                    String str = addnote.getText().toString();
                                    if (str.length() > 0 && str.substring(0, 1).equals(" ")) {
                                        addnote.setText("");
                                        addnote.setSelection(addnote.getText().length());
                                    } else if (str.length() > 0 && str.contains(".")) {
                                        addnote.setText(addnote.getText().toString().replaceAll("\\.", ""));
                                        addnote.setSelection(addnote.getText().length());
                                    } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                                        addnote.setText("");
                                        addnote.setSelection(addnote.getText().length());
                                    }

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        addnote.requestFocus();
                        addnote.setShowSoftInputOnFocus(true);
                        addnote.setText(messagePayReq);
                        addnote.setSelection(messagePayReq.length());

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        Window window = dialog.getWindow();
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.gravity = Gravity.BOTTOM;
                        wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);

                        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                        dialog.setCanceledOnTouchOutside(false);

                        doneBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                try {
                                    messagePayReq = addnote.getText().toString();
                                    addNoteTV.setText(addnote.getText().toString());
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                    dialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        closeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

            });

            lyPayClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

            if (!(payRequestET.getText().toString().isEmpty()) && !(addNoteTV.getText().toString().isEmpty())) {
                prLL.setBackgroundResource(R.drawable.bg_core_colorfill);
            }
            if (Utils.checkInternet(PayRequestTransactionActivity.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.payType));
                obj.setTransactionSubType(Integer.parseInt(Utils.paySubType));
                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
            } else {
                Utils.displayAlert(getString(R.string.internet), PayRequestTransactionActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObservers() {
        dashboardViewModel.getUserDetailsMutableLiveData().observe(this, new Observer<UserDetails>() {
            @Override
            public void onChanged(UserDetails userDetails) {
                if (userDetails != null) {
                    bindUserInfo(userDetails);
                }
            }
        });

        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (transactionLimitResponse != null) {
                    objResponse = transactionLimitResponse;

                }
            }
        });
    }

    private void enablePayReBtn() {
        try {
            if (isFieldValid) {
                prLL.setBackgroundResource(R.drawable.payrequest_activebg);
                payTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //Slide to confirm
                            MotionLayout lay_lock_main;
                            TextView tv_lable;
                            CardView im_lock;
                            RelativeLayout successRL;

                            String enteredAmount = "";
                            TextView messageTV, amountEntered, userNamePay, recipAddre;
                            String getAddNoteTxt = "";
                            TextView messageTxt;
                            LinearLayout recipientCopy;
                            // custom dialog
                            final Dialog dialog = new Dialog(PayRequestTransactionActivity.this);
                            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.fragment_pay_amount_bottom_sheet);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            messageTV = dialog.findViewById(R.id.messageNoteTV);
                            messageTxt = dialog.findViewById(R.id.messageTxt);
                            amountEntered = dialog.findViewById(R.id.amountPayTV);
                            userNamePay = dialog.findViewById(R.id.userNamePayTV);
                            recipAddre = dialog.findViewById(R.id.recipAddreTV);
                            recipientCopy = dialog.findViewById(R.id.copyRecipientLL);
                            //ids Slide to confime
                            lay_lock_main = dialog.findViewById(R.id.lay_lock_main);
                            tv_lable = dialog.findViewById(R.id.tv_lable);
                            im_lock = dialog.findViewById(R.id.im_lock);
                            successRL = dialog.findViewById(R.id.successRL);

                            try {
                                recipAddre.setText(reciepientAddress.substring(0, 16) + "...");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            recipientCopy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Utils.copyText(reciepientAddress, PayRequestTransactionActivity.this);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            //User entered Amount convertion to Decimal
                            if (!payRequestET.getText().toString().contains(",")) {
                                enteredAmount = Utils.convertBigDecimalUSDC(payRequestET.getText().toString());
                                amountEntered.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
                                payRequestET.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
                            } else {
                                amountEntered.setText(payRequestET.getText().toString());
                            }

                            //User entered AddNote Message
                            try {
                                getAddNoteTxt = addNoteTV.getText().toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (!getAddNoteTxt.isEmpty()) {
                                messageTxt.setVisibility(View.VISIBLE);
                                messageTV.setText("\"" + getAddNoteTxt + "\"");
                            } else {
                                messageTxt.setVisibility(View.GONE);
                                messageTV.setText("");
                            }


                            lay_lock_main.setTransitionListener(new MotionLayout.TransitionListener() {
                                @Override
                                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                                }

                                @Override
                                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

                                }

                                @Override
                                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                                    if (currentId == motionLayout.getEndState()) {
                                        lay_lock_main.setInteractionEnabled(false);
                                        tv_lable.setText("Verifying");
                                        CountDownTimer counter = new CountDownTimer(3000, 1000) {
                                            public void onTick(long millisUntilDone) {

                                            }

                                            public void onFinish() {
                                                lay_lock_main.setVisibility(View.GONE);
                                                successRL.setVisibility(View.VISIBLE);
                                            }
                                        }.start();

                                    }
                                }

                                @Override
                                public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

                                }
                            });


                            dashboardViewModel.getUserDetailsMutableLiveData().observe(PayRequestTransactionActivity.this, new Observer<UserDetails>() {
                                @Override
                                public void onChanged(UserDetails userDetails) {
                                    if (userDetails != null) {
                                        userNamePay.setText("[" + userDetails.getData().getFullName() + "]");

                                    }
                                }
                            });

                            Window window = dialog.getWindow();
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            WindowManager.LayoutParams wlp = window.getAttributes();

                            wlp.gravity = Gravity.BOTTOM;
                            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                            window.setAttributes(wlp);

                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                requestTV.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            String enteredAmount = "";
                            TextView messageTV, requestingAmount, userNameR, messageRequestTxt, recipientAddress;
                            String getAddNoteTxt = "";

                            //Slide to confirm
                            MotionLayout lay_lock_main;
                            TextView tv_lable;
                            CardView im_lock;
                            RelativeLayout successRL;
                            LinearLayout copyRequestRecipient;
                            final Dialog dialog = new Dialog(PayRequestTransactionActivity.this);
                            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.fragment_request_amount_bottom_sheet);
                            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            messageTV = dialog.findViewById(R.id.messageAddNote);
                            requestingAmount = dialog.findViewById(R.id.requetAmountTV);
                            userNameR = dialog.findViewById(R.id.userNameRequestTV);
                            messageRequestTxt = dialog.findViewById(R.id.messageReTxt);
                            copyRequestRecipient = dialog.findViewById(R.id.copyrequeRecipientLL);
                            recipientAddress = dialog.findViewById(R.id.recipReqAddreTV);
                            //ids Slide to confime
                            lay_lock_main = dialog.findViewById(R.id.lay_lock_main);
                            tv_lable = dialog.findViewById(R.id.tv_lable);
                            im_lock = dialog.findViewById(R.id.im_lock);
                            successRL = dialog.findViewById(R.id.successRL);
                            userNameR.setText(strUserName);
                            recipientAddress.setText(reciepientAddress.substring(0, 16) + "...");

                            copyRequestRecipient.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Utils.copyText(reciepientAddress, PayRequestTransactionActivity.this);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            //user entered Requesting Amount

                            if (!payRequestET.getText().toString().contains(",")) {
                                enteredAmount = Utils.convertBigDecimalUSDC(payRequestET.getText().toString());
                                requestingAmount.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
                                payRequestET.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
                            } else {
                                requestingAmount.setText(payRequestET.getText().toString());
                            }
                            //User entered Message Addnote
                            getAddNoteTxt = addNoteTV.getText().toString();

                            if (!getAddNoteTxt.isEmpty()) {
                                messageRequestTxt.setVisibility(View.VISIBLE);
                                messageTV.setText("\"" + getAddNoteTxt + "\"");
                            } else {
                                messageRequestTxt.setVisibility(View.GONE);
                                messageTV.setText("");
                            }

                            lay_lock_main.setTransitionListener(new MotionLayout.TransitionListener() {
                                @Override
                                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                                }

                                @Override
                                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

                                }

                                @Override
                                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                                    if (currentId == motionLayout.getEndState()) {
                                        lay_lock_main.setInteractionEnabled(false);
                                        tv_lable.setText("Verifying");
                                        CountDownTimer counter = new CountDownTimer(3000, 1000) {
                                            public void onTick(long millisUntilDone) {

                                            }

                                            public void onFinish() {
                                                lay_lock_main.setVisibility(View.GONE);
                                                successRL.setVisibility(View.VISIBLE);
                                            }
                                        }.start();

                                    }
                                }

                                @Override
                                public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

                                }
                            });


                            Window window = dialog.getWindow();
                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

                            WindowManager.LayoutParams wlp = window.getAttributes();

                            wlp.gravity = Gravity.BOTTOM;
                            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                            window.setAttributes(wlp);

                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                            dialog.setCanceledOnTouchOutside(true);
                            dialog.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                prLL.setBackgroundResource(R.drawable.payrequest_bgcolor);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void activeButtons() {
        try {
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

            keyPay = findViewById(R.id.payTV);
            keyPay.setOnClickListener(this);

            keyRquest = findViewById(R.id.requestTV);
            keyRquest.setOnClickListener(this);
            keyBack = findViewById(R.id.backActionIV);
            keyBack.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {
                case R.id.keyZeroTV:
                    strAmount += 0;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyOneTV:
                    strAmount += 1;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyTwoTV:
                    strAmount += 2;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyThreeTV:
                    strAmount += 3;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyFourTV:
                    strAmount += 4;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyFiveTV:
                    strAmount += 5;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keySixTV:
                    strAmount += 6;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keySevenTV:
                    strAmount += 7;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyEightTV:
                    strAmount += 8;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyNineTV:
                    strAmount += 9;
                    payRequestET.setText(strAmount);
                    break;
                case R.id.keyDotTV:
                    strAmount += ".";
                    payRequestET.setText(strAmount);
                    break;
                case R.id.backActionIV:
                    if (strAmount.length() > 0) {
                        strAmount = strAmount.substring(0, strAmount.length() - 1);
                        payRequestET.setText(strAmount);
                    }

                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindUserInfo(UserDetails userDetails) {
        try {
            TextView tvName, userName, userWalletAddre;
            ImageView userProfile;
            tvName = findViewById(R.id.tvName);
            userName = findViewById(R.id.profileTitle);
            userProfile = findViewById(R.id.profileIV);
            userWalletAddre = findViewById(R.id.accAddress);
            requestedToUserId = userDetails.getData().getUserId();
            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
            strUserName = Utils.capitalize(userDetails.getData().getFullName());
            String imageTextNew = "";
            imageTextNew = userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
                    userDetails.getData().getLastName().substring(0, 1).toUpperCase();
            userName.setText(imageTextNew);
            userWalletAddre.setText("Account Address " + userDetails.getData().getWalletId());
            userName.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
            reciepientAddress = "";
            reciepientAddress = userDetails.getData().getWalletId().toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}