package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.fragments.PayAmountBottomSheet;
import com.greenbox.coyni.fragments.RequestAmountBottomSheet;
import com.greenbox.coyni.interfaces.OnKeyboardVisibilityListener;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.wallet.WalletInfo;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.DashboardViewModel;

import java.util.List;
//import com.greenbox.coyni.fragments.ScanActivityBottomSheetDialog;

public class PayRequestTransactionActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout addNoteClick,prLL,topLL;
    TextView addNoteTV,coynTV;
    ImageView changeCurreIV;
    Boolean isFieldValid=false,isCurrencyEnable=true,isCynEnable=false;
    String strWalletId="";
    ProgressDialog dialog;
    DashboardViewModel dashboardViewModel;
    MyApplication objMyApplication;
    Long mLastClickTime = 0L;

    //For Custome KeyBoard
    private String strAmount=new String();
    private String messagePayReq="",userBalance;
    EditText addnote;
    private TextView keyOne,keyTwo,keyThree,keyFour,keyFive,keySix,keySeven,keyEight,keyNine,keyZero,keyDot,keyActionText,keyPay,keyRquest;
    private ImageView keyBack;
    private SparseArray<String> keyValues=new SparseArray<>();
    InputConnection inputConnection;
    int requestedToUserId=0;
    EditText payRequestET;
    TextView availTV,availBal,errMinAmount,dollorTV;
    TextView requestTV,payTV,nameUser,addreUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_request_transaction);
        try {
            addNoteClick = findViewById(R.id.addNoteClickLL);
            addNoteTV=findViewById(R.id.addNoteTV);
            payRequestET=findViewById(R.id.payrequestET);
            availTV=findViewById(R.id.availBalTV);
            availBal=findViewById(R.id.availBal);
            prLL=findViewById(R.id.payRequestLL);
            topLL=findViewById(R.id.topLL);
            dashboardViewModel=new ViewModelProvider(this).get(DashboardViewModel.class);
            objMyApplication=(MyApplication)getApplicationContext();

           //user Details
            nameUser=findViewById(R.id.tvName);
            addreUser=findViewById(R.id.accAddress);

            changeCurreIV=findViewById(R.id.changeCurrencyTypeIV);
            dollorTV=findViewById(R.id.amontDollorTV);
            errMinAmount=findViewById(R.id.minAmountErr);
            coynTV=findViewById(R.id.coyniTV);
            requestTV=findViewById(R.id.requestTV);
            payTV=findViewById(R.id.payTV);


            userBalance=String.valueOf(objMyApplication.getWalletResponse().getData().getWalletInfo().get(0).getExchangeAmount());
            if (getIntent().getStringExtra("walletId") != null && !getIntent().getStringExtra("walletId").equals("")) {
                strWalletId = getIntent().getStringExtra("walletId");
                if (Utils.checkInternet(PayRequestTransactionActivity.this)) {
//                    dialog = new ProgressDialog(PayRequestTransactionActivity.this, R.style.MyAlertDialogStyle);
//                    dialog.setIndeterminate(false);
//                    dialog.setMessage("Please wait...");
//                    dialog.getWindow().setGravity(Gravity.CENTER);
//                    dialog.show();
                    dashboardViewModel.getUserDetail(strWalletId);
//                    nameUser.setText(objMyApplication.getUserDetails().getData().getFullName());
//                    addreUser.setText(objMyApplication.getUserDetails().getData().getWalletId());

                } else {
                    Utils.displayAlert(getString(R.string.internet), PayRequestTransactionActivity.this,"");
                }
            }
            initObservers();
//            bindImage();
                changeCurreIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String enteredAmount="";
                        enteredAmount=Utils.convertBigDecimalUSDC(payRequestET.getText().toString());
                        payRequestET.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
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
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });
            availBal.setText(userBalance+"CYN");
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

                    try {
                        String enteredAmount="";
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();

                        // custom dialog

//                        enteredAmount=Utils.convertBigDecimalUSDC(payRequestET.getText().toString());
//                        payRequestET.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));

                        final Dialog dialog = new Dialog(PayRequestTransactionActivity.this);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.add_note_layout);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        addnote = dialog.findViewById(R.id.addNoteET);
                        LinearLayout closeBtn=dialog.findViewById(R.id.cancelBtn);
                        TextInputLayout addNoteTIL=dialog.findViewById(R.id.etlMessage);
                        CardView doneBtn=dialog.findViewById(R.id.doneBtn);

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
                                    messagePayReq=addnote.getText().toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });
                        doneBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                addNoteTV.setText(messagePayReq);
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                dialog.dismiss();
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

            if (!(payRequestET.getText().toString().isEmpty())&&!(addNoteTV.getText().toString().isEmpty())){
                prLL.setBackgroundResource(R.drawable.bg_core_colorfill);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initObservers() {
//        dashboardViewModel.getUserDetailsMutableLiveData().observe(this, new Observer<UserDetails>() {
//            @Override
//            public void onChanged(UserDetails userDetails) {
//                dialog.dismiss();
//                if (userDetails != null) {
//                    bindUserInfo(userDetails);
//                }
//            }
//        });
        dashboardViewModel.getUserDetailsMutableLiveData().observe(this, new Observer<UserDetails>() {
            @Override
            public void onChanged(UserDetails userDetails) {
                if (userDetails!=null){
                    bindUserInfo(userDetails);

                }
            }
        });
    }

    private void enablePayReBtn() {


        payTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                try {
                    if (isFieldValid) {
//                        PayAmountBottomSheet payAmountBottomSheet = new PayAmountBottomSheet();
//                        payAmountBottomSheet.show(getSupportFragmentManager(), "TAG");

                        //Slide to confirm
                        MotionLayout lay_lock_main;
                        TextView tv_lable;
                        CardView im_lock;
                        RelativeLayout successRL;

                        String enteredAmount="";
                        TextView messageTV,amountEntered,userNamePay;
                        String getAddNoteTxt="";
                        // custom dialog
                        final Dialog dialog = new Dialog(PayRequestTransactionActivity.this);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.fragment_pay_amount_bottom_sheet);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        messageTV=dialog.findViewById(R.id.messageNoteTV);
                        amountEntered=dialog.findViewById(R.id.amountPayTV);
                        userNamePay=dialog.findViewById(R.id.userNamePayTV);
                        //ids Slide to confime
                        lay_lock_main = dialog.findViewById(R.id.lay_lock_main);
                        tv_lable = dialog.findViewById(R.id.tv_lable);
                        im_lock = dialog.findViewById(R.id.im_lock);
                        successRL = dialog.findViewById(R.id.successRL);

                        //User entered Amount convertion to Decimal
                        enteredAmount=Utils.convertBigDecimalUSDC(payRequestET.getText().toString());
                        amountEntered.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
                        payRequestET.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));

                        //User entered AddNote Message
                        getAddNoteTxt=addNoteTV.getText().toString();
                        messageTV.setText("\""+getAddNoteTxt+"\"");

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
//                    lay_lock_main. = ContextCompat.getDrawable(this@SwipeAnimationScreen,
//                    R.drawable.shape_green_round_rect)
                                    lay_lock_main.setInteractionEnabled(false);
                                    tv_lable.setText("Verifying");

//                    im_lock.setBackgroundResource(R.drawable.left);

                                    CountDownTimer counter = new CountDownTimer(3000, 1000){
                                        public void onTick(long millisUntilDone){

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
                                if (userDetails!=null){
                                    userNamePay.setText("["+userDetails.getData().getFullName()+"]");

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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        requestTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                try {
                    if (isFieldValid){
//                        RequestAmountBottomSheet requestAmountBottomSheet=new RequestAmountBottomSheet();
//                        requestAmountBottomSheet.show(getSupportFragmentManager(),"TAG");
                        // custom dialog
                        String enteredAmount="";
                        TextView messageTV,requestingAmount,userNameR;
                        String getAddNoteTxt="";

                        //Slide to confirm
                        MotionLayout lay_lock_main;
                        TextView tv_lable;
                        CardView im_lock;
                        RelativeLayout successRL;

                        final Dialog dialog = new Dialog(PayRequestTransactionActivity.this);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.fragment_request_amount_bottom_sheet);
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        messageTV=dialog.findViewById(R.id.messageAddNote);
                        requestingAmount=dialog.findViewById(R.id.requetAmountTV);
                        userNameR=dialog.findViewById(R.id.userNameRequestTV);
                        //ids Slide to confime
                        lay_lock_main = dialog.findViewById(R.id.lay_lock_main);
                        tv_lable = dialog.findViewById(R.id.tv_lable);
                        im_lock = dialog.findViewById(R.id.im_lock);
                        successRL = dialog.findViewById(R.id.successRL);

                        dashboardViewModel.getUserDetailsMutableLiveData().observe(PayRequestTransactionActivity.this, new Observer<UserDetails>() {
                            @Override
                            public void onChanged(UserDetails userDetails) {
                                if (userDetails!=null){
                                    userNameR.setText("["+userDetails.getData().getFullName()+"]");

                                }
                            }
                        });
                        //user entered Requesting Amount

                        enteredAmount=Utils.convertBigDecimalUSDC(payRequestET.getText().toString());
                        requestingAmount.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));
                        payRequestET.setText(Utils.USNumberFormat(Double.parseDouble(enteredAmount)));


                        //User entered Message Addnote
                        getAddNoteTxt=addNoteTV.getText().toString();
                        messageTV.setText("\""+getAddNoteTxt+"\"");

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
//                    lay_lock_main. = ContextCompat.getDrawable(this@SwipeAnimationScreen,
//                    R.drawable.shape_green_round_rect)
                                    lay_lock_main.setInteractionEnabled(false);
                                    tv_lable.setText("Verifying");

//                    im_lock.setBackgroundResource(R.drawable.left);

                                    CountDownTimer counter = new CountDownTimer(3000, 1000){
                                        public void onTick(long millisUntilDone){

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
    private void bindUserInfo(UserDetails userDetails) {
        try {
            TextView tvName, tvNameHead,userName,userWalletAddre;
            ImageView userProfile;
            tvName = findViewById(R.id.tvName);
            userName=findViewById(R.id.profileTitle);
            userProfile=findViewById(R.id.profileIV);
            userWalletAddre=findViewById(R.id.accAddress);
//            tvNameHead = findViewById(R.id.tvNameHead);
            requestedToUserId = userDetails.getData().getUserId();
//            if (getIntent().getStringExtra("name") != null && !getIntent().getStringExtra("name").equals("")) {
//                tvName.setText(Utils.capitalize(getIntent().getStringExtra("name")));
//                if (getIntent().getStringExtra("name").contains(" ")) {
//                    tvNameHead.setText(getIntent().getStringExtra("name").split(" ")[0].substring(0, 1).toUpperCase() + getIntent().getStringExtra("name").split(" ")[1].substring(0, 1).toUpperCase());
//                } else {
//                    tvNameHead.setText(getIntent().getStringExtra("name").substring(0, 1).toUpperCase());
//                }
//            } else {
//                tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
//                tvNameHead.setText(userDetails.getData().getFirstName().substring(0, 1).toUpperCase() + userDetails.getData().getLastName().substring(0, 1).toUpperCase());
//            }
            tvName.setText(Utils.capitalize(userDetails.getData().getFullName()));
//            tvNameHead.setText(userDetails.getData().getFirstName().substring(0, 1).toUpperCase() + userDetails.getData().getLastName().substring(0, 1).toUpperCase());
            String imageTextNew = "";
            imageTextNew = imageTextNew + userDetails.getData().getFirstName().substring(0, 1).toUpperCase() +
                    userDetails.getData().getLastName().substring(0, 1).toUpperCase();
            String walletId="";
            walletId=walletId+userDetails.getData().getWalletId().substring(0,16)+"...";
            userName.setText(imageTextNew);
            userWalletAddre.setText(walletId);
            userName.setVisibility(View.VISIBLE);
            userProfile.setVisibility(View.GONE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        addnote.setText(messagePayReq);
    }
}