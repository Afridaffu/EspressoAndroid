package com.greenbox.coyni.view;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.SelectedPaymentMethodsAdapter;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentsList;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.keyboards.CustomKeyboard;

public class BuyTokenActivity extends AppCompatActivity {
    MyApplication objMyApplication;
    PaymentsList selectedCard;
    ImageView imgBankIcon, imgArrow;
    TextView tvLimit, tvPayHead, tvAccNumber, tvCurrency, tvBankName, tvBAccNumber;
    RelativeLayout lyPayMethod, lyBDetails;
    LinearLayout lyCDetails, lyBuyClose;
    EditText etAmount;
    CustomKeyboard ctKey;
    PaymentMethodsResponse paymentMethodsResponse;
    Dialog payDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_buy_token);
            initialization();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initialization() {
        try {
            objMyApplication = (MyApplication) getApplicationContext();
            selectedCard = objMyApplication.getSelectedCard();
            paymentMethodsResponse = objMyApplication.getPaymentMethodsResponse();
            imgBankIcon = findViewById(R.id.imgBankIcon);
            imgArrow = findViewById(R.id.imgArrow);
            tvLimit = findViewById(R.id.tvLimit);
            tvPayHead = findViewById(R.id.tvPayHead);
            tvBankName = findViewById(R.id.tvBankName);
            tvAccNumber = findViewById(R.id.tvAccNumber);
            tvBAccNumber = findViewById(R.id.tvBAccNumber);
            tvCurrency = findViewById(R.id.tvCurrency);
            lyPayMethod = findViewById(R.id.lyPayMethod);
            lyBuyClose = findViewById(R.id.lyBuyClose);
            etAmount = findViewById(R.id.etAmount);
            lyCDetails = findViewById(R.id.lyCDetails);
            lyBDetails = findViewById(R.id.lyBDetails);
            ctKey = (CustomKeyboard) findViewById(R.id.ckb);
            ctKey.setKeyAction("Buy\nToken");
            ctKey.setScreenName("buy");
            InputConnection ic = etAmount.onCreateInputConnection(new EditorInfo());
            ctKey.setInputConnection(ic);
            etAmount.setShowSoftInputOnFocus(false);
            etAmount.setEnabled(false);
            bindPayMethod(selectedCard);

            etAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideSoftKeypad(BuyTokenActivity.this, v);
                }
            });

            etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Utils.hideSoftKeypad(BuyTokenActivity.this, view);
                }
            });

            etAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() > 0) {
                        ctKey.enableButton();
                    } else {
                        ctKey.disableButton();
                    }
                }
            });

            lyPayMethod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPayMethod();
                }
            });

            lyBuyClose.setOnClickListener(new View.OnClickListener() {
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
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (objData.getPaymentMethod().toLowerCase().equals("bank")) {
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
            params.setMargins(Utils.convertPxtoDP(15), Utils.convertPxtoDP(15), 0, 0);
            tvLimit.setLayoutParams(params);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void selectPayMethod() {
        try {
            payDialog = new Dialog(BuyTokenActivity.this);
            payDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            payDialog.setContentView(R.layout.choosepaymentmethod);
            payDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            RecyclerView rvSelPayMethods = payDialog.findViewById(R.id.rvSelPayMethods);
            SelectedPaymentMethodsAdapter selectedPaymentMethodsAdapter;
            if (paymentMethodsResponse.getData().getData() != null && paymentMethodsResponse.getData().getData().size() > 0) {
                selectedPaymentMethodsAdapter = new SelectedPaymentMethodsAdapter(paymentMethodsResponse.getData().getData(), BuyTokenActivity.this, "buytoken");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(BuyTokenActivity.this);
                rvSelPayMethods.setLayoutManager(mLayoutManager);
                rvSelPayMethods.setItemAnimator(new DefaultItemAnimator());
                rvSelPayMethods.setAdapter(selectedPaymentMethodsAdapter);
            }

            Window window = payDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            payDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            payDialog.setCanceledOnTouchOutside(true);
            payDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}