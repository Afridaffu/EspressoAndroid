package com.greenbox.coyni.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.CustomerProfileViewModel;
import com.greenbox.coyni.viewmodel.PaymentMethodsViewModel;

public class WithdrawTokenActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    PaymentsList selectedCard, objSelected, prevSelectedCard;
    ImageView imgBankIcon, imgArrow, imgConvert;
    TextView tvLimit, tvPayHead, tvAccNumber, tvCurrency, tvBankName, tvBAccNumber, tvError, tvCYN;
    RelativeLayout lyPayMethod;
    LinearLayout lyCDetails, lyWithdrawClose, lyBDetails;
    EditText etAmount;
    CustomKeyboard ctKey;
    PaymentMethodsResponse paymentMethodsResponse;
    CustomerProfileViewModel customerProfileViewModel;
    PaymentMethodsViewModel paymentMethodsViewModel;
    BuyTokenViewModel buyTokenViewModel;
    Dialog payDialog, prevDialog, cvvDialog;
    TransactionLimitResponse objResponse;
    ProgressDialog pDialog;
    String strLimit = "", strType = "", strBankId = "", strCardId = "", strCvv = "", strSubType = "", strSignOn = "";
    public static WithdrawTokenActivity withdrawTokenActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setContentView(R.layout.activity_withdraw_token);
            withdrawTokenActivity = this;
            initialization();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable == etAmount.getEditableText()) {
            try {
                if (editable.length() > 0 && !editable.toString().equals(".") && !editable.toString().equals(".00")) {
                    etAmount.setHint("");
                    if (tvCYN.getVisibility() == View.VISIBLE) {

                    } else {

                    }

                    if (editable.length() > 5) {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33);
                    } else {
                        etAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 53);
                        tvCurrency.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                    }
//                    if (validation()) {
//                        ctKey.enableButton();
//                    } else {
//                        ctKey.disableButton();
//                    }
                } else if (editable.toString().equals(".")) {
                    etAmount.setText("");
                    ctKey.disableButton();
                } else if (editable.length() == 0) {
                    etAmount.setHint("0.00");
//                    cynValue = 0.0;
//                    usdValue = 0.0;
//                    cynValidation = 0.0;
//                    usdValidation = 0.0;
                    ctKey.disableButton();
                    tvError.setVisibility(View.INVISIBLE);
                    ctKey.clearData();
                } else {
                    etAmount.setText("");
//                    cynValue = 0.0;
//                    usdValue = 0.0;
//                    cynValidation = 0.0;
//                    usdValidation = 0.0;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            selectedCard = objMyApplication.getSelectedCard();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            customerProfileViewModel = new ViewModelProvider(this).get(CustomerProfileViewModel.class);
            paymentMethodsViewModel = new ViewModelProvider(this).get(PaymentMethodsViewModel.class);
            imgBankIcon = findViewById(R.id.imgBankIcon);
            imgArrow = findViewById(R.id.imgArrow);
            imgConvert = findViewById(R.id.imgConvert);
            tvLimit = findViewById(R.id.tvLimit);
            tvPayHead = findViewById(R.id.tvPayHead);
            tvBankName = findViewById(R.id.tvBankName);
            tvAccNumber = findViewById(R.id.tvAccNumber);
            tvBAccNumber = findViewById(R.id.tvBAccNumber);
            tvError = findViewById(R.id.tvError);
            tvCYN = findViewById(R.id.tvCYN);
            tvCurrency = findViewById(R.id.tvCurrency);
            lyPayMethod = findViewById(R.id.lyPayMethod);
            lyWithdrawClose = findViewById(R.id.lyWithdrawClose);
            etAmount = findViewById(R.id.etAmount);
            lyCDetails = findViewById(R.id.lyCDetails);
            lyBDetails = findViewById(R.id.lyBDetails);
            ctKey = (CustomKeyboard) findViewById(R.id.ckb);
            ctKey.setKeyAction("Withdraw");
            ctKey.setScreenName("withdraw");
            InputConnection ic = etAmount.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            etAmount.requestFocus();
            etAmount.setShowSoftInputOnFocus(false);
            if (getIntent().getStringExtra("cvv") != null && !getIntent().getStringExtra("cvv").equals("")) {
                strCvv = getIntent().getStringExtra("cvv");
            }
            bindPayMethod(selectedCard);
            etAmount.addTextChangedListener(this);
            etAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(WithdrawTokenActivity.this, v);
                }
            });

            lyWithdrawClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void bindPayMethod(PaymentsList objData) {
        try {
            if (payDialog != null) {
                payDialog.dismiss();
            }
            selectedCard = objData;
            TransactionLimitRequest obj = new TransactionLimitRequest();
            obj.setTransactionType(Integer.parseInt(Utils.withdrawType));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
                strType = "bank";
                strBankId = String.valueOf(objData.getId());
                strSubType = Utils.bankType;
                obj.setTransactionSubType(Integer.parseInt(Utils.bankType));
                lyBDetails.setVisibility(View.VISIBLE);
                lyCDetails.setVisibility(View.GONE);
                params.addRule(RelativeLayout.BELOW, lyBDetails.getId());
                imgBankIcon.setImageResource(R.drawable.ic_bankactive);
                tvBankName.setText(objData.getBankName());
                if (objData.getAccountNumber() != null && objData.getAccountNumber().length() > 4) {
                    tvBAccNumber.setText("**** " + objData.getAccountNumber().substring(objData.getAccountNumber().length() - 4));
                } else {
                    tvBAccNumber.setText(objData.getAccountNumber());
                }
            } else {
                strCardId = String.valueOf(objData.getId());
                if (objData.getCardType().toLowerCase().equals("debit")) {
                    strType = "debit";
                    strSubType = Utils.debitType;
                    obj.setTransactionSubType(Integer.parseInt(Utils.debitType));
                } else if (objData.getCardType().toLowerCase().equals("credit")) {
                    strType = "credit";
                    strSubType = Utils.creditType;
                    obj.setTransactionSubType(Integer.parseInt(Utils.creditType));
                }
                params.addRule(RelativeLayout.BELOW, lyCDetails.getId());
                lyBDetails.setVisibility(View.GONE);
                lyCDetails.setVisibility(View.VISIBLE);
                tvAccNumber.setText("****" + objData.getLastFour());
                switch (objData.getCardBrand().toUpperCase().replace(" ", "")) {
                    case "VISA":
                        tvPayHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType()));
                        imgBankIcon.setImageResource(R.drawable.ic_visaactive);
                        break;
                    case "MASTERCARD":
                        tvPayHead.setText(Utils.capitalize(objData.getCardBrand() + " " + objData.getCardType()));
                        imgBankIcon.setImageResource(R.drawable.ic_masteractive);
                        break;
                    case "AMERICANEXPRESS":
                        tvPayHead.setText("American Express Card");
                        imgBankIcon.setImageResource(R.drawable.ic_amexactive);
                        break;
                    case "DISCOVER":
                        tvPayHead.setText("Discover Card");
                        imgBankIcon.setImageResource(R.drawable.ic_discoveractive);
                        break;
                }
            }
            params.addRule(RelativeLayout.RIGHT_OF, imgBankIcon.getId());
            params.addRule(RelativeLayout.LEFT_OF, imgArrow.getId());
            params.setMargins(Utils.convertPxtoDP(15), Utils.convertPxtoDP(5), 0, 0);
            tvLimit.setLayoutParams(params);
            if (Utils.checkInternet(WithdrawTokenActivity.this)) {
                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
            } else {
                Utils.displayAlert(getString(R.string.internet), WithdrawTokenActivity.this, "", "");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}