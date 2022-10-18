package com.coyni.mapp.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.mapp.R;
import com.coyni.mapp.adapters.GiftCardFixedAmountsAdapter;
import com.coyni.mapp.interfaces.OnKeyboardVisibilityListener;
import com.coyni.mapp.model.biometric.BiometricTokenRequest;
import com.coyni.mapp.model.biometric.BiometricTokenResponse;
import com.coyni.mapp.model.giftcard.Brand;
import com.coyni.mapp.model.giftcard.BrandsResponse;
import com.coyni.mapp.model.giftcard.Items;
import com.coyni.mapp.model.transactionlimit.LimitResponseData;
import com.coyni.mapp.model.transactionlimit.TransactionLimitRequest;
import com.coyni.mapp.model.transactionlimit.TransactionLimitResponse;
import com.coyni.mapp.model.transferfee.TransferFeeRequest;
import com.coyni.mapp.model.transferfee.TransferFeeResponse;
import com.coyni.mapp.model.withdraw.GiftCardWithDrawInfo;
import com.coyni.mapp.model.withdraw.RecipientDetail;
import com.coyni.mapp.model.withdraw.WithdrawRequest;
import com.coyni.mapp.model.withdraw.WithdrawResponse;
import com.coyni.mapp.utils.CustomeTextView.AnimatedGradientTextView;
import com.coyni.mapp.utils.DatabaseHandler;
import com.coyni.mapp.utils.DisplayImageUtility;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;
import com.coyni.mapp.viewmodel.BuyTokenViewModel;
import com.coyni.mapp.viewmodel.CoyniViewModel;
import com.coyni.mapp.viewmodel.GiftCardsViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GiftCardDetails extends BaseActivity implements OnKeyboardVisibilityListener {
    TextInputEditText firstNameET, lastNameET, emailET, amountET;
    TextInputLayout firstNameTIL, lastNameTIL, emailTIL, amountTIL;
    public LinearLayout emailErrorLL, firstNameErrorLL, lastNameErrorLL, giftCardDetailsLL, amountErrorLL;
    public TextView emailErrorTV, firstNameErrorTV, lastNameErrorTV, amountErrorTV, brandNameTV, brandDescTV, viewAllTV, tvFeePer;
    ImageView brandIV, amountDDICon;
    GiftCardsViewModel giftCardsViewModel;
    BuyTokenViewModel buyTokenViewModel;
    CoyniViewModel coyniViewModel;
    BrandsResponse brandsResponseObj;
    DatabaseHandler dbHandler;
    Brand objBrand;
    Dialog pDialog;
    Double fee = 0.0, min = 0.0, max = 0.0, maxValue = 0.0, minValue = 0.0, feeInAmount = 0.0, feeInPercentage = 0.0;
    List<Items> listAmounts = new ArrayList<>();
    String amountETString = "", amount = "", strLimit = "", strBrandDesc = "";
    public String selectedFixedAmount = "";
    public boolean isFirstName = false, isLastName = false, isEmail = false, isAmount = false, isNextEnabled = false;
    CardView purchaseCV;
    TransactionLimitResponse objTranLimit;
    RelativeLayout amountRL;
    public static GiftCardDetails giftCardDetails;
    MyApplication objMyApplication;

    SQLiteDatabase mydatabase;
    Cursor dsFacePin, dsTouchID;
    boolean isFaceLock = false, isTouchId = false, isBiometric = false;
    int CODE_AUTHENTICATION_VERIFICATION = 251;
    int FOR_RESULT = 235;
    Dialog prevDialog;
    Long mLastClickTime = 0L;
    boolean isAuthenticationCalled = false;
    private MotionLayout withDrawSlideToConfirm;
    private AnimatedGradientTextView tv_lable;
    private CardView im_lock, im_lock_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_gift_card_details);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.WHITE);
            initFields();
            initObservers();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initFields() {
        try {
            giftCardDetails = this;
            setKeyboardVisibilityListener(GiftCardDetails.this);
            objMyApplication = (MyApplication) getApplicationContext();
            dbHandler = DatabaseHandler.getInstance(GiftCardDetails.this);
            firstNameErrorLL = findViewById(R.id.firstNameErrorLL);
            lastNameErrorLL = findViewById(R.id.lastNameErrorLL);
            emailErrorLL = findViewById(R.id.emailErrorLL);
            amountErrorLL = findViewById(R.id.amountErrorLL);

            firstNameErrorTV = findViewById(R.id.firstNameErrorTV);
            lastNameErrorTV = findViewById(R.id.lastNameErrorTV);
            emailErrorTV = findViewById(R.id.emailErrorTV);
            amountErrorTV = findViewById(R.id.amountErrorTV);

            firstNameET = findViewById(R.id.firstNameET);
            firstNameTIL = findViewById(R.id.firstNameTIL);
            lastNameET = findViewById(R.id.lastNameET);
            lastNameTIL = findViewById(R.id.lastNameTIL);
            emailET = findViewById(R.id.emailET);
            emailTIL = findViewById(R.id.emailTIL);
            amountET = findViewById(R.id.amountET);
            amountTIL = findViewById(R.id.amountTIL);

            brandIV = findViewById(R.id.brandIV);
            brandNameTV = findViewById(R.id.brandNameTV);
            brandDescTV = findViewById(R.id.brandDescTV);
            viewAllTV = findViewById(R.id.viewAllTV);
            giftCardDetailsLL = findViewById(R.id.giftCardDetailsLL);
            amountDDICon = findViewById(R.id.amountDDICon);
            purchaseCV = findViewById(R.id.purchaseCV);
            amountRL = findViewById(R.id.amountRL);
            tvFeePer = findViewById(R.id.tvFeePer);

            giftCardsViewModel = new ViewModelProvider(this).get(GiftCardsViewModel.class);
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);
            coyniViewModel = new ViewModelProvider(this).get(CoyniViewModel.class);

            emailET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(255)});
            firstNameET.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
            firstNameET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

            lastNameET.setFilters(new InputFilter[]{acceptonlyAlphabetValuesnotNumbersMethod()});
            lastNameET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});

            amountTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            firstNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            lastNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
            emailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));

//            if (amountET.requestFocus()==true){
//                amountET.setHint("Coyni@example.com");
//                amountTIL.setBoxStrokeColorStateList(getColorStateList(R.color.primary_green));
//            }
//            else {
//                amountET.setHint("");
//            }

            //isBiometric = Utils.checkBiometric(GiftCardDetails.this);
//            SetFaceLock();
//            SetTouchId();
//            setFaceLock();
//            setTouchId();
            objMyApplication.initializeDBHandler(GiftCardDetails.this);
            isFaceLock = objMyApplication.setFaceLock();
            isTouchId = objMyApplication.setTouchId();
            if (isFaceLock || isTouchId) {
                objMyApplication.setLocalBiometric(true);
            } else {
                objMyApplication.setLocalBiometric(false);
            }

            giftCardsViewModel.getGiftCardDetails(getIntent().getStringExtra("BRAND_KEY"));

            viewAllTV.setPaintFlags(viewAllTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            viewAllTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        brandDescTV.setText(strBrandDesc);
                        Utils.hideKeypad(GiftCardDetails.this, view);
                        if (brandDescTV.getMaxLines() == 3) {
                            brandDescTV.setMaxLines(Integer.MAX_VALUE);
                            viewAllTV.setText(getResources().getString(R.string.view_less));
                        } else {
                            brandDescTV.setMaxLines(3);
                            viewAllTV.setText(getResources().getString(R.string.view_all));
                        }
//                        if (viewAllTV.getText().toString().equals(getResources().getString(R.string.view_all))) {
//                            viewAllTV.setText(getResources().getString(R.string.view_less));
//                            brandDescTV.setText(strBrandDesc.substring(0, 200));
//                        } else {
//                            viewAllTV.setText(getResources().getString(R.string.view_all));
//                            brandDescTV.setText(strBrandDesc);
//                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            giftCardDetailsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Utils.hideKeypad(GiftCardDetails.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });

            if (Utils.checkInternet(GiftCardDetails.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.withdrawType));
                obj.setTransactionSubType(Integer.parseInt(Utils.giftcardType));
//                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                pDialog = Utils.showProgressDialog(GiftCardDetails.this);
                if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
                } else {
                    buyTokenViewModel.transactionLimits(obj, Utils.userTypeBusiness);
                }
            } else {
                Utils.displayAlert(getString(R.string.internet), GiftCardDetails.this, "", "");
            }

            focusWatchers();
            textWatchers();

            amountRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (brandsResponseObj.getData().getBrands().get(0).getItems().get(0).getValueType().toUpperCase().equals("FIXED_VALUE")) {
                            showFixedAmounts(GiftCardDetails.this, amountET, listAmounts);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            amountET.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        if (brandsResponseObj.getData().getBrands().get(0).getItems().get(0).getValueType().toUpperCase().equals("FIXED_VALUE")) {
                            showFixedAmounts(GiftCardDetails.this, amountET, listAmounts);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            purchaseCV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (isNextEnabled) {
                            Utils.hideKeypad(GiftCardDetails.this, view);
                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                                return;
                            }
                            mLastClickTime = SystemClock.elapsedRealtime();
                            getGCWithdrawRequest();
                            giftCardPreview(view);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });

            calculateFee("1");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers() {
        giftCardsViewModel.getGiftCardDetailsMutableLiveData().observe(this, new Observer<BrandsResponse>() {
            @Override
            public void onChanged(BrandsResponse brandsResponse) {
                try {
                    if (brandsResponse != null) {
                        if (brandsResponse.getStatus().equalsIgnoreCase("SUCCESS")) {
                            brandsResponseObj = brandsResponse;
                            objMyApplication.setSelectedBrandResponse(brandsResponse);
                            objBrand = brandsResponse.getData().getBrands().get(0);
                            brandNameTV.setText(brandsResponse.getData().getBrands().get(0).getBrandName());
//                            String description = "", htmlString = "";
//                            if (brandsResponse.getData().getBrands().get(0).getDescription().length() > 200) {
//                                description = brandsResponse.getData().getBrands().get(0).getDescription().substring(0, 200).replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\s+", " ").trim() + " ...View All";
//                                htmlString = Html.fromHtml(description).toString();
//                                getDescription(htmlString, "...View All");
//                            } else {
//                                brandDescTV.setText(Html.fromHtml(brandsResponse.getData().getBrands().get(0).getDescription().replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\s+", " ").trim()));
//                            }
                            strBrandDesc = (Html.fromHtml(brandsResponse.getData().getBrands().get(0).getDescription().replaceAll("[\\t\\r]+", " ").replaceAll("\\s+", " "), Html.FROM_HTML_MODE_COMPACT).toString().trim());
                            brandDescTV.setText(strBrandDesc);
//                            Glide.with(GiftCardDetails.this).load(brandsResponse.getData().getBrands().get(0).getImageUrls().get_1200w326ppi().trim()).into(brandIV);

                            DisplayImageUtility utility = DisplayImageUtility.getInstance(GiftCardDetails.this);
                            utility.addImage(brandsResponse.getData().getBrands().get(0).getImageUrls().get_1200w326ppi().trim(), brandIV, 0);

                            if (objBrand.getItems() != null && objBrand.getItems().size() > 0) {
                                listAmounts = new ArrayList<>();
                                for (int i = 0; i < objBrand.getItems().size(); i++) {
                                    if (objBrand.getItems().get(i).getValueType().toUpperCase().equals("FIXED_VALUE")) {
                                        listAmounts.add(objBrand.getItems().get(i));
                                    } else {
                                        min = objBrand.getItems().get(i).getMinValue();
                                        max = objBrand.getItems().get(i).getMaxValue();
                                    }
                                }
                                if (listAmounts != null && listAmounts.size() > 0) {
                                    amountDDICon.setVisibility(View.VISIBLE);
                                    amountTIL.setPrefixText("");
                                    amountET.setFocusable(false);
                                    amountET.setCursorVisible(false);
                                } else {
                                    amountDDICon.setVisibility(View.GONE);
                                    amountTIL.setPrefixText("$");
                                    amountET.setFocusable(true);
                                    amountET.setCursorVisible(true);

                                    amountETString = "$" + Utils.convertBigDecimalUSD(String.valueOf(min)) + " - $" + Utils.convertBigDecimalUSD(String.valueOf(max));
                                    amountET.requestFocus();
//                                    if (!Utils.isKeyboardVisible)
//                                        Utils.shwForcedKeypad(GiftCardDetails.this);
//                                    selectedItem = objBrand.getItems().get(0);
                                }
                            }
                        } else {
                            Utils.displayAlert(brandsResponse.getError().getErrorDescription(), GiftCardDetails.this, "", brandsResponse.getError().getFieldErrors().get(0));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buyTokenViewModel.getTransferFeeResponseMutableLiveData().observe(this, new Observer<TransferFeeResponse>() {
            @Override
            public void onChanged(TransferFeeResponse transferFeeResponse) {
                try {
                    if (transferFeeResponse != null) {
                        fee = transferFeeResponse.getData().getFee();
                        feeInAmount = transferFeeResponse.getData().getFeeInAmount();
                        feeInPercentage = transferFeeResponse.getData().getFeeInPercentage();
                    }

                    String feeString = "Fees: ";

                    if (feeInAmount != 0 && feeInPercentage != 0)
                        feeString = feeString + "$" + Utils.convertTwoDecimalPoints(feeInAmount) + " + " + Utils.convertTwoDecimalPoints(feeInPercentage) + "%";

                    else if (feeInAmount != 0 && feeInPercentage == 0)
                        feeString = feeString + "$" + Utils.convertTwoDecimalPoints(feeInAmount);

                    else if (feeInAmount == 0 && feeInPercentage != 0)
                        feeString = feeString + Utils.convertTwoDecimalPoints(feeInPercentage) + "%";

                    if (!feeString.equals("Fees: "))
                        tvFeePer.setText(feeString);
                    else
                        tvFeePer.setVisibility(View.GONE);




                    //-----------------------

                    Double walletAmount = 0.0;
                    if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
                        walletAmount = Utils.doubleParsing(objMyApplication.getCurrentUserData().getTokenWalletResponse().getExchangeAmount() + "".replace(",", ""));
                    } else {
//                                walletAmount = Utils.doubleParsing(objMyApplication.getCurrentUserData().getMerchantWalletResponse().getWalletNames().get(0).getExchangeAmount() + "".replace(",", ""));
                        walletAmount = Utils.doubleParsing(objMyApplication.getCurrentUserData().getTokenWalletResponse().getExchangeAmount() + "".replace(",", ""));
                    }
//                            Double giftCardAmount = (Utils.doubleParsing(amountET.getText().toString().replace(",", "")) + Utils.doubleParsing(fee.toString().replace(",", "")));
//                    Double giftCardAmount = Utils.doubleParsing(amountET.getText().toString().replace(",", "")) * (1 - (feeInPercentage / 100)) - feeInAmount;
                    Double giftCardAmount =  (Utils.doubleParsing(amountET.getText().toString().replace(",", "")) + (Utils.doubleParsing(amountET.getText().toString().replace(",", "")) * (feeInPercentage / 100))) + feeInAmount;
                    Double giftCardETAmount = Utils.doubleParsing(amountET.getText().toString().replace(",", ""));
                    if (objTranLimit.getData() != null && objTranLimit.getData().getMinimumLimit() != null) {
                        minValue = Utils.doubleParsing(objTranLimit.getData().getMinimumLimit());
                    }
                    if (minValue < min) {
                        minValue = min;
                    }

                    String strPFee = Utils.convertBigDecimalUSD(String.valueOf(fee));
                    Double total = Utils.doubleParsing(amountET.getText().toString().trim().replace(",", "")) + Utils.doubleParsing(strPFee);
//                    if (walletAmount.equals(giftCardETAmount)) {
                    if (giftCardETAmount <= walletAmount && (walletAmount < giftCardAmount)) {
                        isAmount = false;
                        amountErrorLL.setVisibility(VISIBLE);
                        amountErrorTV.setText("Insufficient funds. Your transaction fee will increase your total withdrawal amount, exceeding your balance.");

                    } else if ((walletAmount < giftCardAmount) || (total > walletAmount)) {
                        isAmount = false;
                        amountErrorLL.setVisibility(VISIBLE);
                        amountErrorTV.setText("Amount entered exceeds available balance");

                    } else if (giftCardETAmount < minValue) {
                        isAmount = false;
                        amountErrorLL.setVisibility(VISIBLE);
                        amountErrorTV.setText("Amount should be equal to or greater than " + Utils.USNumberFormat(minValue) + " USD");
                    } else if (giftCardETAmount > max) {
                        isAmount = false;
                        amountErrorLL.setVisibility(VISIBLE);
                        amountErrorTV.setText("Amount entered exceeds limit");
                    } else {
                        String limitType = objTranLimit.getData().getLimitType();
                        if (limitType.equalsIgnoreCase("PER TRANSACTION")) {
                            if (giftCardETAmount > Utils.doubleParsing(objTranLimit.getData().getTransactionLimit())) {
                                isAmount = false;
                                amountErrorLL.setVisibility(VISIBLE);
                                amountErrorTV.setText("Amount entered exceeds transaction limit");
                            } else {
                                isAmount = true;
                                amountErrorLL.setVisibility(GONE);
                            }
                        } else if (limitType.equalsIgnoreCase("DAILY")) {
                            if (giftCardETAmount > Utils.doubleParsing(objTranLimit.getData().getTransactionLimit())) {
                                isAmount = false;
                                amountErrorLL.setVisibility(VISIBLE);
                                amountErrorTV.setText("Amount entered exceeds daily limit");
                            } else {
                                isAmount = true;
                                amountErrorLL.setVisibility(GONE);
                            }
                        } else if (!limitType.equalsIgnoreCase("NO LIMIT")) {
                            if (giftCardETAmount > Utils.doubleParsing(objTranLimit.getData().getTransactionLimit())) {
                                isAmount = false;
                                amountErrorLL.setVisibility(VISIBLE);
                                amountErrorTV.setText("Amount entered exceeds weekly limit");
                            } else {
                                isAmount = true;
                                amountErrorLL.setVisibility(GONE);
                            }
                        } else if (limitType.equalsIgnoreCase("NO LIMIT")) {
//                                    if (giftCardETAmount > Utils.doubleParsing(objTranLimit.getData().getTransactionLimit())) {
//                                        isAmount = false;
//                                        amountErrorLL.setVisibility(VISIBLE);
//                                        amountErrorTV.setText("Amount entered exceeds weekly limit");
//                                    } else {
                            isAmount = true;
                            amountErrorLL.setVisibility(GONE);
//                                    }
                        }
                    }

                    enableOrDisableNext();
                    //------------------------
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (pDialog != null) {
                    pDialog.dismiss();
                }
                if (transactionLimitResponse != null) {
                    objTranLimit = transactionLimitResponse;
                }
            }
        });

        buyTokenViewModel.getWithdrawResponseMutableLiveData().observe(this, new Observer<WithdrawResponse>() {
            @Override
            public void onChanged(WithdrawResponse withdrawResponse) {
                try {
                    if (prevDialog != null) {
                        prevDialog.dismiss();
                    }
                    if (pDialog != null) {
                        pDialog.dismiss();
                    }
//                    Utils.setStrToken("");
                    objMyApplication.clearStrToken();
                    if (withdrawResponse != null) {
                        objMyApplication.setWithdrawResponse(withdrawResponse);
                        if (withdrawResponse.getStatus().equalsIgnoreCase("success")) {
                            startActivity(new Intent(GiftCardDetails.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "inprogress")
                                    .putExtra("subtype", "giftcard")
                                    .putExtra("fee", fee.toString()));
                        } else {
                            startActivity(new Intent(GiftCardDetails.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "failed")
                                    .putExtra("subtype", "giftcard")
                                    .putExtra("fee", fee.toString()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        coyniViewModel.getBiometricTokenResponseMutableLiveData().observe(this, new Observer<BiometricTokenResponse>() {
            @Override
            public void onChanged(BiometricTokenResponse biometricTokenResponse) {
                if (biometricTokenResponse != null) {
                    if (biometricTokenResponse.getStatus().toLowerCase().equals("success")) {
                        if (biometricTokenResponse.getData().getRequestToken() != null && !biometricTokenResponse.getData().getRequestToken().equals("")) {
//                            Utils.setStrToken(biometricTokenResponse.getData().getRequestToken());
                            objMyApplication.setStrToken(biometricTokenResponse.getData().getRequestToken());
                        }
                        withdrawGiftCard();
                    }
                }
            }
        });
    }

    public void textWatchers() {
        try {
            amountET.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try {
                        if (charSequence.toString().trim().length() > 0) {

////                            amountTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
////                            Utils.setUpperHintColor(amountTIL, getResources().getColor(R.color.primary_black));
//                            Double walletAmount = 0.0;
//                            if (objMyApplication.getAccountType() == Utils.PERSONAL_ACCOUNT) {
//                                walletAmount = Utils.doubleParsing(objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getExchangeAmount() + "".replace(",", ""));
//                            } else {
////                                walletAmount = Utils.doubleParsing(objMyApplication.getCurrentUserData().getMerchantWalletResponse().getWalletNames().get(0).getExchangeAmount() + "".replace(",", ""));
//                                walletAmount = Utils.doubleParsing(objMyApplication.getCurrentUserData().getTokenWalletResponse().getWalletNames().get(0).getExchangeAmount() + "".replace(",", ""));
//                            }
////                            Double giftCardAmount = (Utils.doubleParsing(amountET.getText().toString().replace(",", "")) + Utils.doubleParsing(fee.toString().replace(",", "")));
//                            Double giftCardAmount = Utils.doubleParsing(amountET.getText().toString().replace(",", "")) * (1 - (feeInPercentage / 100)) - feeInAmount;
//                            Double giftCardETAmount = Utils.doubleParsing(amountET.getText().toString().replace(",", ""));
//                            if (objTranLimit.getData() != null && objTranLimit.getData().getMinimumLimit() != null) {
//                                minValue = Utils.doubleParsing(objTranLimit.getData().getMinimumLimit());
//                            }
//                            if (minValue < min) {
//                                minValue = min;
//                            }
//
//                            String strPFee = Utils.convertBigDecimalUSD(String.valueOf(fee));
//                            Double total = Utils.doubleParsing(amountET.getText().toString().trim().replace(",", "")) + Utils.doubleParsing(strPFee);
//                            if (walletAmount.equals(giftCardETAmount)) {
//                                isAmount = false;
//                                amountErrorLL.setVisibility(VISIBLE);
//                                amountErrorTV.setText("Insufficient funds. Your transaction fee will increase your total withdrawal amount, exceeding your balance.");
//
//                            } else if ((walletAmount < giftCardAmount) || (total > walletAmount)) {
//                                isAmount = false;
//                                amountErrorLL.setVisibility(VISIBLE);
//                                amountErrorTV.setText("Amount entered exceeds available balance");
//
//                            } else if (giftCardETAmount < minValue) {
//                                isAmount = false;
//                                amountErrorLL.setVisibility(VISIBLE);
//                                amountErrorTV.setText("Amount should be equal to or greater than " + Utils.USNumberFormat(minValue) + " USD");
//                            } else if (giftCardETAmount > max) {
//                                isAmount = false;
//                                amountErrorLL.setVisibility(VISIBLE);
//                                amountErrorTV.setText("Amount entered exceeds limit");
//                            } else {
//                                String limitType = objTranLimit.getData().getLimitType();
//                                if (limitType.equalsIgnoreCase("PER TRANSACTION")) {
//                                    if (giftCardETAmount > Utils.doubleParsing(objTranLimit.getData().getTransactionLimit())) {
//                                        isAmount = false;
//                                        amountErrorLL.setVisibility(VISIBLE);
//                                        amountErrorTV.setText("Amount entered exceeds transaction limit");
//                                    } else {
//                                        isAmount = true;
//                                        amountErrorLL.setVisibility(GONE);
//                                    }
//                                } else if (limitType.equalsIgnoreCase("DAILY")) {
//                                    if (giftCardETAmount > Utils.doubleParsing(objTranLimit.getData().getTransactionLimit())) {
//                                        isAmount = false;
//                                        amountErrorLL.setVisibility(VISIBLE);
//                                        amountErrorTV.setText("Amount entered exceeds daily limit");
//                                    } else {
//                                        isAmount = true;
//                                        amountErrorLL.setVisibility(GONE);
//                                    }
//                                } else if (!limitType.equalsIgnoreCase("NO LIMIT")) {
//                                    if (giftCardETAmount > Utils.doubleParsing(objTranLimit.getData().getTransactionLimit())) {
//                                        isAmount = false;
//                                        amountErrorLL.setVisibility(VISIBLE);
//                                        amountErrorTV.setText("Amount entered exceeds weekly limit");
//                                    } else {
//                                        isAmount = true;
//                                        amountErrorLL.setVisibility(GONE);
//                                    }
//                                } else if (limitType.equalsIgnoreCase("NO LIMIT")) {
////                                    if (giftCardETAmount > Utils.doubleParsing(objTranLimit.getData().getTransactionLimit())) {
////                                        isAmount = false;
////                                        amountErrorLL.setVisibility(VISIBLE);
////                                        amountErrorTV.setText("Amount entered exceeds weekly limit");
////                                    } else {
//                                    isAmount = true;
//                                    amountErrorLL.setVisibility(GONE);
////                                    }
//                                }
//                            }
                        } else if (amountET.getText().toString().trim().length() == 0) {
                            amountErrorLL.setVisibility(GONE);
//                            amountErrorTV.setText("Field Required");
                            isAmount = false;
                        }
//                        enableOrDisableNext();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (s.length() > 0 && !s.toString().equals(".") && !s.toString().equals(".00")) {
                            calculateFee(s.toString().replace(",", ""));
                            amount = s.toString();
                        } else if (s.toString().equals(".")) {
                            amountET.setText("");
                            amount = "";
                        } else if (s.toString().equals(".")) {
                            amountET.setText(amountET.getText().toString().replaceAll("\\.", ""));
                            amountET.setSelection(amountET.getText().length());
                        } else {

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            firstNameET.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                        isFirstName = true;
                        firstNameErrorLL.setVisibility(GONE);
//                        firstNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        Utils.setUpperHintColor(firstNameTIL, getResources().getColor(R.color.primary_black));
                    } else if (firstNameET.getText().toString().trim().length() == 0) {
//                        firstNameErrorLL.setVisibility(VISIBLE);
//                        firstNameErrorTV.setText("Field Required");
                    } else {
                        isFirstName = false;
                    }

                    if (firstNameET.getText().toString().contains("  ")) {
                        firstNameET.setText(firstNameET.getText().toString().replace("  ", " "));
                        firstNameET.setSelection(firstNameET.getText().length());
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String str = firstNameET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ")) {
                            firstNameET.setText("");
                            firstNameET.setSelection(firstNameET.getText().length());
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            firstNameET.setText(str.trim());
                        } else if (str.length() > 0 && str.contains(".")) {
                            firstNameET.setText(firstNameET.getText().toString().replaceAll("\\.", ""));
                            firstNameET.setSelection(firstNameET.getText().length());
                        } else if (str.length() > 0 && str.contains("http") || str.length() > 0 && str.contains("https")) {
                            firstNameET.setText("");
                            firstNameET.setSelection(firstNameET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            lastNameET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.toString().trim().length() > 1 && charSequence.toString().trim().length() < 31) {
                        isLastName = true;
                        lastNameErrorLL.setVisibility(GONE);
//                        lastNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        Utils.setUpperHintColor(lastNameTIL, getResources().getColor(R.color.primary_black));
                    } else if (lastNameET.getText().toString().trim().length() == 0) {
//                        lastNameErrorLL.setVisibility(VISIBLE);
//                        lastNameErrorTV.setText("Field Required");
                    } else {
                        isLastName = false;
                    }

                    if (lastNameET.getText().toString().contains("  ")) {
                        lastNameET.setText(lastNameET.getText().toString().replace("  ", " "));
                        lastNameET.setSelection(lastNameET.getText().length());
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = lastNameET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ")) {
                            lastNameET.setText(lastNameET.getText().toString().replaceAll(" ", ""));
                            lastNameET.setSelection(lastNameET.getText().length());
                        } else if (str.length() > 0 && String.valueOf(str.charAt(0)).equals(" ")) {
                            lastNameET.setText(str.trim());
                        } else if (str.length() > 0 && str.substring(str.length() - 1).equals(".")) {
                            lastNameET.setText(lastNameET.getText().toString().replaceAll(".", ""));
                            lastNameET.setSelection(lastNameET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            emailET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (charSequence.length() > 5 && Utils.isValidEmail(charSequence.toString().trim())) {
                        emailErrorLL.setVisibility(GONE);
//                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
//                        Utils.setUpperHintColor(emailTIL, getResources().getColor(R.color.primary_black));

                    } else if (emailET.getText().toString().trim().length() == 0) {
//                        emailErrorLL.setVisibility(VISIBLE);
//                        emailErrorTV.setText("Field Required");
                    }
                    if (Utils.isValidEmail(charSequence.toString().trim()) && charSequence.toString().trim().length() > 5) {
                        isEmail = true;
                    } else {
                        isEmail = false;
                    }
                    enableOrDisableNext();
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        String str = emailET.getText().toString();
                        if (str.length() > 0 && str.substring(0).equals(" ") || (str.length() > 0 && str.contains(" "))) {
                            emailET.setText(emailET.getText().toString().replaceAll(" ", ""));
                            emailET.setSelection(emailET.getText().length());
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void focusWatchers() {
        try {
            amountET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        USFormat(amountET);
//                        Utils.hideKeypad(GiftCardDetails.this);
                        amountET.setHint("");
                        if (amountET.getText().toString().trim().length() > 0) {
                            if (amountErrorLL.getVisibility() == VISIBLE) {
                                amountErrorLL.setVisibility(VISIBLE);
                            } else {
                                amountErrorLL.setVisibility(GONE);
                            }
                            amountTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(amountTIL, getColor(R.color.primary_black));

                            if (amountErrorLL.getVisibility() == VISIBLE || !isAmount) {
                                amountTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                                Utils.setUpperHintColor(amountTIL, getColor(R.color.error_red));
                            }
                        } else {
                            amountTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
//                            Utils.setUpperHintColor(amountTIL, getColor(R.color.error_red));
                            Utils.setUpperHintColor(amountTIL, getColor(R.color.light_gray));
//                            amountTIL.setHint("");
//                            amountET.setHint("$ Amount");
                            amountErrorLL.setVisibility(VISIBLE);
                            amountErrorTV.setText("Field Required");
                        }
                    } else {
//                        amountET.setHint("Amount");
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                        amountET.setFilters(FilterArray);
                        amountET.setHint(amountETString);
                        amountTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(amountTIL, getColor(R.color.primary_green));
                        amountErrorLL.setVisibility(GONE);
                    }
                }
            });

            firstNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        firstNameET.setHint("");
                        if (firstNameET.getText().toString().trim().length() > 1) {
                            firstNameErrorLL.setVisibility(GONE);
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.primary_black));

                        } else if (firstNameET.getText().toString().trim().length() == 1) {
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.error_red));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
//                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.error_red));
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.light_gray));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Field Required");
                        }
                        if (firstNameET.getText().toString().length() > 0 && !firstNameET.getText().toString().substring(0, 1).equals(" ")) {
                            firstNameET.setText(firstNameET.getText().toString().substring(0, 1).toUpperCase() + firstNameET.getText().toString().substring(1));
                        }
                    } else {
//                        firstNameET.setHint("First Name");
                        firstNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(firstNameTIL, getColor(R.color.primary_green));
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(GiftCardDetails.this);
                        firstNameErrorLL.setVisibility(GONE);
                    }
                }
            });

            lastNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        lastNameET.setHint("");
                        if (lastNameET.getText().toString().trim().length() > 1) {
                            lastNameErrorLL.setVisibility(GONE);
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.primary_black));

                        } else if (lastNameET.getText().toString().trim().length() == 1) {
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.error_red));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            //Utils.setUpperHintColor(lastNameTIL, getColor(R.color.error_red));
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.light_gray));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Field Required");
                        }
                        if (lastNameET.getText().toString().length() > 0 && lastNameET.getText().toString().charAt(0) != ' ') {
                            lastNameET.setText(lastNameET.getText().toString().substring(0, 1).toUpperCase() + lastNameET.getText().toString().substring(1));
                        }
                    } else {
//                        lastNameET.setHint("Last Name");
                        lastNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(lastNameTIL, getColor(R.color.primary_green));
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(GiftCardDetails.this);
                        lastNameErrorLL.setVisibility(GONE);
                    }
                }
            });

            emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        emailET.setHint("");
                        if (emailET.getText().toString().trim().length() > 5 && !Utils.isValidEmail(emailET.getText().toString().trim())) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Please enter a valid Email");
                        } else if (emailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(emailET.getText().toString().trim())) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.primary_black));
                            emailErrorLL.setVisibility(GONE);
                        } else if (emailET.getText().toString().trim().length() == 0) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
//                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.light_gray));
//                            emailTIL.setHint("Coyni@example.com");
                            emailTIL.setHint("Email");
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Field Required");
                        } else {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(getApplicationContext()));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Please enter a valid Email");
                        }
                    } else {
//                        emailET.setHint("Coyni@example.com");
//                        emailET.setHint("Email");
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        emailTIL.setHint("Email");
                        Utils.setUpperHintColor(emailTIL, getColor(R.color.primary_green));
                        if (!Utils.isKeyboardVisible)
                            Utils.shwForcedKeypad(GiftCardDetails.this);
                        emailErrorLL.setVisibility(GONE);
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void enableOrDisableNext() {
        try {
            if (isFirstName && isLastName && isEmail && isAmount) {
                isNextEnabled = true;
                purchaseCV.setCardBackgroundColor(getResources().getColor(R.color.primary_color));
            } else {
                isNextEnabled = false;
                purchaseCV.setCardBackgroundColor(getResources().getColor(R.color.inactive_color));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void USFormat(TextInputEditText etAmount) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            etAmount.setFilters(FilterArray);
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSD(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
            etAmount.setSelection(etAmount.getText().length() - 3);
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            etAmount.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void calculateFee(String strAmount) {
        try {
            if (!strAmount.trim().equals("")) {
                TransferFeeRequest request = new TransferFeeRequest();
                request.setTokens(strAmount.trim());
                request.setTxnType(Utils.withdraw + "");
                request.setTxnSubType(Utils.giftCard + "");
                if (Utils.checkInternet(GiftCardDetails.this)) {
                    buyTokenViewModel.transferFee(request);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showFixedAmounts(Context mContext, EditText editText, List<Items> listAmounts) {
        // custom dialog
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timezones_bottom_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics mertics = mContext.getResources().getDisplayMetrics();
        int width = mertics.widthPixels;

        CardView actionCV = dialog.findViewById(R.id.cvAction);
        TextView actionText = dialog.findViewById(R.id.tvAction);
        RecyclerView fixedAmountsRV = dialog.findViewById(R.id.timezonesRV);

        if (amountET.getText().toString().equals("")) {
            selectedFixedAmount = "";
        }
        for (int i = 0; i < listAmounts.size(); i++) {
            if (selectedFixedAmount.equals(listAmounts.get(i).getFaceValue().toString())) {
                listAmounts.get(i).setSelected(true);
            } else {
                listAmounts.get(i).setSelected(false);
            }
        }

        GiftCardFixedAmountsAdapter giftCardFixedAmountsAdapter = new GiftCardFixedAmountsAdapter(listAmounts, mContext);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        fixedAmountsRV.setLayoutManager(mLayoutManager);
        fixedAmountsRV.setItemAnimator(new DefaultItemAnimator());
        fixedAmountsRV.setAdapter(giftCardFixedAmountsAdapter);

        actionCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    amountET.setText(Utils.USNumberFormat(Utils.doubleParsing(selectedFixedAmount)));
                    amountTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(getApplicationContext()));
                    Utils.setUpperHintColor(amountTIL, getColor(R.color.primary_black));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
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

    private void setDailyWeekLimit(LimitResponseData objLimit) {
        try {
            if (objLimit.getTokenLimitFlag()) {
//                tvLimit.setVisibility(View.VISIBLE);
                Double week = 0.0, daily = 0.0;
                String strCurrency = "", strAmount = "";
                if (objLimit.getWeeklyAccountLimit() != null && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("NA") && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("unlimited")) {
                    week = Utils.doubleParsing(objLimit.getWeeklyAccountLimit());
                }
                if (objLimit.getDailyAccountLimit() != null && !objLimit.getDailyAccountLimit().toLowerCase().equals("NA") && !objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    daily = Utils.doubleParsing(objLimit.getDailyAccountLimit());
                }
                strCurrency = " USD";
                minValue = Utils.doubleParsing(objLimit.getMinimumLimit());
                if ((week == 0 || week < 0) && daily > 0) {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSD(String.valueOf(daily));
//                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
                } else if ((daily == 0 || daily < 0) && week > 0) {
                    strLimit = "week";
                    maxValue = week;
                    strAmount = Utils.convertBigDecimalUSD(String.valueOf(week));
//                    tvLimit.setText("Your weekly limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
                } else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    strLimit = "unlimited";
//                    tvLimit.setText("Your daily limit is " + objLimit.getDailyAccountLimit() + " USD");
                } else {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSD(String.valueOf(daily));
//                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Utils.doubleParsing(strAmount)) + strCurrency);
                }
            } else {
//                tvLimit.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void giftCardPreview(View view) {
        try {
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(GiftCardDetails.this);
            prevDialog = new Dialog(GiftCardDetails.this);
            prevDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            prevDialog.setContentView(R.layout.gift_card_order_preview);
            prevDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics mertics = getResources().getDisplayMetrics();
            int width = mertics.widthPixels;

            TextView giftCardAmountTV = prevDialog.findViewById(R.id.giftCardAmountTV);
            TextView giftCardTypeTV = prevDialog.findViewById(R.id.giftCardTypeTV);
            TextView recipientMailTV = prevDialog.findViewById(R.id.recipientMailTV);
            TextView subTotalTV = prevDialog.findViewById(R.id.subTotalTV);
            TextView feeTV = prevDialog.findViewById(R.id.feeTV);
            TextView totalTV = prevDialog.findViewById(R.id.totalTV);
            tv_lable = prevDialog.findViewById(R.id.tv_lable);
            TextView tv_lable_verify = prevDialog.findViewById(R.id.tv_lable_verify);
            im_lock_ = prevDialog.findViewById(R.id.im_lock_);
            im_lock = prevDialog.findViewById(R.id.im_lock);

            withDrawSlideToConfirm = prevDialog.findViewById(R.id.slideToConfirm);

            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSD(String.valueOf(fee));
            feeTV.setText(Utils.USNumberFormat(Utils.doubleParsing(strPFee)) + " CYN");
            Double total = Utils.doubleParsing(amountET.getText().toString().trim().replace(",", "")) + Utils.doubleParsing(strPFee);
            totalTV.setText(Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSD(total.toString()))) + " CYN");
            subTotalTV.setText(Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSD(amountET.getText().toString().trim().replace(",", "")))) + " CYN");
            giftCardAmountTV.setText(Utils.USNumberFormat(Utils.doubleParsing(Utils.convertBigDecimalUSD(amountET.getText().toString().trim().replace(",", "")))));
            giftCardTypeTV.setText(brandsResponseObj.getData().getBrands().get(0).getBrandName());
            recipientMailTV.setText(emailET.getText().toString());

            isAuthenticationCalled = false;

            withDrawSlideToConfirm.setTransitionListener(new MotionLayout.TransitionListener() {
                @Override
                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

                    if (progress > Utils.slidePercentage) {
                        im_lock_.setAlpha(1.0f);
                        motionLayout.setTransition(R.id.middle, R.id.end);
                        motionLayout.transitionToState(motionLayout.getEndState());
                        withDrawSlideToConfirm.setInteractionEnabled(false);
//                        tv_lable.setVisibility(GONE);
//                        tv_lable.setText("Verifying");
//                        tv_lable_verify.setVisibility(VISIBLE);
                        if (!isAuthenticationCalled) {
                            tv_lable.setText("Verifying");
                            if ((isFaceLock || isTouchId) && Utils.checkAuthentication(GiftCardDetails.this)) {
                                if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(GiftCardDetails.this)) || (isFaceLock))) {
//                                    prevDialog.dismiss();
                                    isAuthenticationCalled = true;
                                    Utils.checkAuthentication(GiftCardDetails.this, CODE_AUTHENTICATION_VERIFICATION);
                                } else {
                                    prevDialog.dismiss();
                                    isAuthenticationCalled = true;
                                    startActivity(new Intent(GiftCardDetails.this, PINActivity.class)
                                            .putExtra("TYPE", "ENTER")
                                            .putExtra("subtype", "giftcard")
                                            .putExtra("screen", "Withdraw"));
                                }
                            } else {
                                Log.e("elsee", "elssee");
                                prevDialog.dismiss();
                                isAuthenticationCalled = true;
                                startActivity(new Intent(GiftCardDetails.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("subtype", "giftcard")
                                        .putExtra("screen", "Withdraw"));
                            }
                        }

                    }
                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
//                    if (currentId == motionLayout.getEndState()) {
//                        slideToConfirm.setInteractionEnabled(false);
//                        tv_lable.setText("Verifying");
//                        if ((isFaceLock || isTouchId) && Utils.checkAuthentication(GiftCardDetails.this)) {
//                            if (Utils.getIsBiometric() && ((isTouchId && Utils.isFingerPrint(GiftCardDetails.this)) || (isFaceLock))) {
//                                prevDialog.dismiss();
//                                Utils.checkAuthentication(GiftCardDetails.this, CODE_AUTHENTICATION_VERIFICATION);
//                            } else {
//                                prevDialog.dismiss();
//                                startActivityForResult(new Intent(GiftCardDetails.this, PINActivity.class)
//                                        .putExtra("TYPE", "ENTER")
//                                        .putExtra("screen", "GiftCard"), FOR_RESULT);
//                            }
//                        } else {
//                            prevDialog.dismiss();
//                            startActivityForResult(new Intent(GiftCardDetails.this, PINActivity.class)
//                                    .putExtra("TYPE", "ENTER")
//                                    .putExtra("screen", "GiftCard"), FOR_RESULT);
//                        }
//                    }
                }

                @Override
                public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

                }
            });

            Window window = prevDialog.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.BOTTOM;
            wlp.flags &= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            prevDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

            prevDialog.setCanceledOnTouchOutside(true);
            prevDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void withdrawGiftCard() {
        try {
            if (Utils.checkInternet(GiftCardDetails.this)) {
                buyTokenViewModel.withdrawTokens(objMyApplication.getWithdrawRequest(), objMyApplication.getStrToken());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public void SetFaceLock() {
//        try {
//            isFaceLock = false;
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
//            dsFacePin.moveToFirst();
//            if (dsFacePin.getCount() > 0) {
//                String value = dsFacePin.getString(1);
//                if (value.equals("true")) {
//                    isFaceLock = true;
//                    objMyApplication.setLocalBiometric(true);
//                } else {
//                    isFaceLock = false;
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void SetTouchId() {
//        try {
//            isTouchId = false;
//            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
//            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
//            dsTouchID.moveToFirst();
//            if (dsTouchID.getCount() > 0) {
//                String value = dsTouchID.getString(1);
//                if (value.equals("true")) {
//                    isTouchId = true;
//                    objMyApplication.setLocalBiometric(true);
//                } else {
//                    isTouchId = false;
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

//
//    public void setFaceLock() {
//        try {
//            isFaceLock = false;
//            String value = dbHandler.getFacePinLock();
//            if (value != null && value.equals("true")) {
//                isFaceLock = true;
//                objMyApplication.setLocalBiometric(true);
//            } else {
//                isFaceLock = false;
//                objMyApplication.setLocalBiometric(false);
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void setTouchId() {
//        try {
//            isTouchId = false;
//            String value = dbHandler.getThumbPinLock();
//            if (value != null && value.equals("true")) {
//                isTouchId = true;
//                objMyApplication.setLocalBiometric(true);
//            } else {
//                isTouchId = false;
////                objMyApplication.setLocalBiometric(false);
//                if (!isFaceLock) {
//                    objMyApplication.setLocalBiometric(false);
//                }
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
            case 235: {
                try {
                    //withdrawGiftCard();
                    pDialog = Utils.showProgressDialog(GiftCardDetails.this);
                    BiometricTokenRequest request = new BiometricTokenRequest();
                    request.setDeviceId(Utils.getDeviceID());
                    request.setMobileToken(objMyApplication.getStrMobileToken());
                    request.setActionType(Utils.withdrawActionType);
                    coyniViewModel.biometricToken(request);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;
            case 0:
                startActivity(new Intent(GiftCardDetails.this, PINActivity.class)
                        .putExtra("TYPE", "ENTER")
                        .putExtra("subtype", "giftcard")
                        .putExtra("screen", "Withdraw"));
                break;
        }
    }

    public WithdrawRequest getGCWithdrawRequest() {
        WithdrawRequest request = new WithdrawRequest();
        GiftCardWithDrawInfo giftCardWithDrawInfo = new GiftCardWithDrawInfo();
        giftCardWithDrawInfo.setGiftCardName(brandsResponseObj.getData().getBrands().get(0).getBrandName());
        giftCardWithDrawInfo.setGiftCardCurrency(brandsResponseObj.getData().getBrands().get(0).getItems().get(0).getCurrencyCode());
        giftCardWithDrawInfo.setTotalAmount(Utils.doubleParsing(Utils.convertBigDecimalUSD(amountET.getText().toString().replace(",", ""))));
        giftCardWithDrawInfo.setUtid(brandsResponseObj.getData().getBrands().get(0).getItems().get(0).getUtid());

        List<RecipientDetail> recipientDetailList = new ArrayList<>();
        RecipientDetail recipientDetail = new RecipientDetail();
        recipientDetail.setAmount(Utils.doubleParsing(Utils.convertBigDecimalUSD(amountET.getText().toString().replace(",", ""))));
        recipientDetail.setFirstName(firstNameET.getText().toString());
        recipientDetail.setLastName(lastNameET.getText().toString());
        recipientDetail.setEmail(emailET.getText().toString());
        recipientDetailList.add(recipientDetail);

        giftCardWithDrawInfo.setRecipientDetails(recipientDetailList);

        request.setBankId(0L);
        request.setCardId(0L);
        request.setGiftCardWithDrawInfo(giftCardWithDrawInfo);
        request.setTokens(Utils.doubleParsing(Utils.convertBigDecimalUSD(amountET.getText().toString().trim().replace(",", ""))));
        request.setRemarks("");
        request.setWithdrawType(Utils.giftcardType);

        objMyApplication.setWithdrawRequest(request);
        return request;
    }

    public static InputFilter acceptonlyAlphabetValuesnotNumbersMethod() {
        return new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                boolean isCheck = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) {
                        sb.append(c);
                    } else {
                        isCheck = false;
                    }
                }
                if (isCheck)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString spannableString = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, spannableString, 0);
                        return spannableString;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
                Matcher match = pattern.matcher(String.valueOf(c));
                return match.matches();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        final View parentView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            private boolean alreadyOpen;
            private final int defaultKeyboardHeightDP = 100;
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();

            @Override
            public void onGlobalLayout() {
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);
                boolean isShown = heightDiff >= estimatedKeyboardHeight;

                if (isShown == alreadyOpen) {
                    Log.i("Keyboard state", "Ignoring global layout change...");
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
        if (visible) {
            Utils.isKeyboardVisible = true;
        } else {
            Utils.isKeyboardVisible = false;
        }
        Log.e("isKeyboardVisible", Utils.isKeyboardVisible + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (Utils.isKeyboardVisible)
//            Utils.hideKeypad(this);

        if (prevDialog != null) {
            changeSlideState();
        }
    }

    private void changeSlideState() {
        try {
            withDrawSlideToConfirm.setInteractionEnabled(true);
            withDrawSlideToConfirm.setTransition(R.id.start, R.id.start);
            tv_lable.setText("Slide to Confirm");
            tv_lable.setVisibility(View.VISIBLE);
            withDrawSlideToConfirm.setProgress(0);
            im_lock.setAlpha(1.0f);
            isAuthenticationCalled = false;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}