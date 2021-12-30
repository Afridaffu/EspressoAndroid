package com.greenbox.coyni.view;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.greenbox.coyni.R;
import com.greenbox.coyni.adapters.GiftCardFixedAmountsAdapter;
import com.greenbox.coyni.model.buytoken.BuyTokenRequest;
import com.greenbox.coyni.model.giftcard.Brand;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.model.giftcard.Items;
import com.greenbox.coyni.model.transactionlimit.LimitResponseData;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.withdraw.GiftCardWithDrawInfo;
import com.greenbox.coyni.model.withdraw.RecipientDetail;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.model.withdraw.WithdrawResponse;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.viewmodel.BuyTokenViewModel;
import com.greenbox.coyni.viewmodel.GiftCardsViewModel;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

public class GiftCardDetails extends AppCompatActivity {

    TextInputEditText firstNameET, lastNameET, emailET, amountET;
    TextInputLayout firstNameTIL, lastNameTIL, emailTIL, amountTIL;
    public LinearLayout emailErrorLL, firstNameErrorLL, lastNameErrorLL, giftCardDetailsLL, amountErrorLL;
    public TextView emailErrorTV, firstNameErrorTV, lastNameErrorTV, amountErrorTV, brandNameTV, brandDescTV, viewAllTV;
    ImageView brandIV, amountDDICon;
    boolean isView = false;
    GiftCardsViewModel giftCardsViewModel;
    BuyTokenViewModel buyTokenViewModel;
    BrandsResponse brandsResponseObj;
    Brand objBrand;
    Double fee = 0.0, min = 0.0, max = 0.0, maxValue = 0.0, minValue = 0.0;
    List<Items> listAmounts = new ArrayList<>();
    String amountETString = "", amount = "", strLimit = "";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
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
            objMyApplication = (MyApplication) getApplicationContext();
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

            giftCardsViewModel = new ViewModelProvider(this).get(GiftCardsViewModel.class);
            buyTokenViewModel = new ViewModelProvider(this).get(BuyTokenViewModel.class);

            isBiometric = Utils.checkBiometric(GiftCardDetails.this);
            SetFaceLock();
            SetTouchId();

            giftCardsViewModel.getGiftCardDetails(getIntent().getStringExtra("BRAND_KEY"));

            viewAllTV.setPaintFlags(viewAllTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            viewAllTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (brandDescTV.getMaxLines() == 2) {
                            brandDescTV.setMaxLines(Integer.MAX_VALUE);
                            viewAllTV.setText(getResources().getString(R.string.view_less));
                        } else {
                            brandDescTV.setMaxLines(2);
                            viewAllTV.setText(getResources().getString(R.string.view_all));
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            giftCardDetailsLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            if (Utils.checkInternet(GiftCardDetails.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.withdrawType));
                obj.setTransactionSubType(Integer.parseInt(Utils.giftcardType));
                buyTokenViewModel.transactionLimits(obj, Utils.userTypeCust);
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
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        giftCardPreview();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initObservers() {
        try {
            giftCardsViewModel.getGiftCardDetailsMutableLiveData().observe(this, new Observer<BrandsResponse>() {
                @Override
                public void onChanged(BrandsResponse brandsResponse) {
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
                            brandDescTV.setText((Html.fromHtml(brandsResponse.getData().getBrands().get(0).getDescription().replaceAll("[\\t\\r]+", " ").replaceAll("\\s+", " ")).toString().trim()));
                            Glide.with(GiftCardDetails.this).load(brandsResponse.getData().getBrands().get(0).getImageUrls().get_1200w326ppi().trim()).into(brandIV);

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

                                    amountETString = "$" + Utils.convertBigDecimalUSDC(String.valueOf(min)) + " - $" + Utils.convertBigDecimalUSDC(String.valueOf(max));
                                    amountET.requestFocus();
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                                    selectedItem = objBrand.getItems().get(0);
                                }
                            }
                        } else {
                            Utils.displayAlert(brandsResponse.getError().getErrorDescription(), GiftCardDetails.this, "", brandsResponse.getError().getFieldErrors().get(0));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            buyTokenViewModel.getTransferFeeResponseMutableLiveData().observe(this, new Observer<TransferFeeResponse>() {
                @Override
                public void onChanged(TransferFeeResponse transferFeeResponse) {
                    if (transferFeeResponse != null) {
                        fee = transferFeeResponse.getData().getFee();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            buyTokenViewModel.getTransactionLimitResponseMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
                @Override
                public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                    if (transactionLimitResponse != null) {
                        objTranLimit = transactionLimitResponse;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            buyTokenViewModel.getWithdrawResponseMutableLiveData().observe(this, new Observer<WithdrawResponse>() {
                @Override
                public void onChanged(WithdrawResponse withdrawResponse) {
                    prevDialog.dismiss();
                    if (withdrawResponse != null) {
                        objMyApplication.setWithdrawResponse(withdrawResponse);
                        if (withdrawResponse.getStatus().equalsIgnoreCase("success")) {
                            startActivity(new Intent(GiftCardDetails.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "inprogress")
                                    .putExtra("fee", fee.toString()));
                        } else {
                            startActivity(new Intent(GiftCardDetails.this, GiftCardBindingLayoutActivity.class)
                                    .putExtra("status", "failed")
                                    .putExtra("fee", fee.toString()));
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void textWatchers() {

        try {
            amountET.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() > 0) {

                        amountTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(amountTIL, getResources().getColor(R.color.primary_green));

                        Double walletAmount = Double.parseDouble(objMyApplication.getWalletResponse().getData().getWalletInfo().get(0).getExchangeAmount() + "".replace(",", ""));
                        Double giftCardAmount = (Double.parseDouble(amountET.getText().toString().replace(",", "")) + Double.parseDouble(fee.toString().replace(",", "")));
                        Double giftCardETAmount = Double.parseDouble(amountET.getText().toString().replace(",", ""));
                        minValue = Double.parseDouble(objTranLimit.getData().getMinimumLimit());

                        if (walletAmount < giftCardAmount) {
                            isAmount = false;
                            amountErrorLL.setVisibility(VISIBLE);
                            amountErrorTV.setText("Amount entered exceeds available balance");
                        } else if (giftCardETAmount < minValue) {
                            isAmount = false;
                            amountErrorLL.setVisibility(VISIBLE);
                            amountErrorTV.setText("Amount should be equal to or greater than " + minValue + " USD");
                        } else if (giftCardETAmount > max) {
                            isAmount = false;
                            amountErrorLL.setVisibility(VISIBLE);
                            amountErrorTV.setText("Amount entered exceeds limit");
                        } else if (objTranLimit.getData().getTokenLimitFlag()) {
                            String limitType = objTranLimit.getData().getLimitType();
                            if (limitType.equalsIgnoreCase("DAILY")) {
                                if (giftCardETAmount > Double.parseDouble(objTranLimit.getData().getTransactionLimit())) {
                                    isAmount = false;
                                    amountErrorLL.setVisibility(VISIBLE);
                                    amountErrorTV.setText("Amount entered exceeds transaction limit");
                                } else if (giftCardETAmount > Double.parseDouble(objTranLimit.getData().getDailyAccountLimit())) {
                                    isAmount = false;
                                    amountErrorLL.setVisibility(VISIBLE);
                                    amountErrorTV.setText("Amount entered exceeds daily limit");
                                } else {
                                    isAmount = true;
                                    amountErrorLL.setVisibility(GONE);
                                }
                            } else {
                                if (giftCardETAmount > Double.parseDouble(objTranLimit.getData().getTransactionLimit())) {
                                    isAmount = false;
                                    amountErrorLL.setVisibility(VISIBLE);
                                    amountErrorTV.setText("Amount entered exceeds transaction limit");
                                } else if (giftCardETAmount > Double.parseDouble(objTranLimit.getData().getWeeklyAccountLimit())) {
                                    isAmount = false;
                                    amountErrorLL.setVisibility(VISIBLE);
                                    amountErrorTV.setText("Amount entered exceeds weekly limit");
                                } else {
                                    isAmount = true;
                                    amountErrorLL.setVisibility(GONE);
                                }
                            }
                        } else {
                            isAmount = true;
                            amountErrorLL.setVisibility(GONE);
                        }
                    } else if (amountET.getText().toString().trim().length() == 0) {
                        amountErrorLL.setVisibility(VISIBLE);
                        amountErrorTV.setText("Field Required");
                        isAmount = false;
                    }
                    enableOrDisableNext();
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
                        firstNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(firstNameTIL, getResources().getColor(R.color.primary_green));
                    } else if (firstNameET.getText().toString().trim().length() == 0) {
                        firstNameErrorLL.setVisibility(VISIBLE);
                        firstNameErrorTV.setText("Field Required");
                    } else {
                        isFirstName = false;
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
                        lastNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(lastNameTIL, getResources().getColor(R.color.primary_green));
                    } else if (lastNameET.getText().toString().trim().length() == 0) {
                        lastNameErrorLL.setVisibility(VISIBLE);
                        lastNameErrorTV.setText("Field Required");
                    } else {
                        isLastName = false;
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
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(emailTIL, getResources().getColor(R.color.primary_green));

                    } else if (emailET.getText().toString().trim().length() == 0) {
                        emailErrorLL.setVisibility(VISIBLE);
                        emailErrorTV.setText("Field Required");
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
                        amountET.setHint("");
                        if (amountET.getText().toString().trim().length() > 0) {
                            if(amountErrorLL.getVisibility()==VISIBLE){
                                amountErrorLL.setVisibility(VISIBLE);
                            }else{
                                amountErrorLL.setVisibility(GONE);
                            }
                            amountTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(amountTIL, getColor(R.color.primary_black));
                        } else {
                            amountTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            Utils.setUpperHintColor(amountTIL, getColor(R.color.error_red));
                            Utils.setUpperHintColor(amountTIL, getColor(R.color.light_gray));
                            amountErrorLL.setVisibility(VISIBLE);
                            amountErrorTV.setText("Field Required");
                        }
                    } else {
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                        amountET.setFilters(FilterArray);
                        amountET.setHint(amountETString);
                        amountTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(amountTIL, getColor(R.color.primary_green));
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
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.primary_black));

                        } else if (firstNameET.getText().toString().trim().length() == 1) {
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.error_red));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            firstNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.error_red));
                            Utils.setUpperHintColor(firstNameTIL, getColor(R.color.light_gray));
                            firstNameErrorLL.setVisibility(VISIBLE);
                            firstNameErrorTV.setText("Field Required");
                        }
                    } else {
                        firstNameET.setHint("First Name");
                        firstNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(firstNameTIL, getColor(R.color.primary_green));
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
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.primary_black));

                        } else if (lastNameET.getText().toString().trim().length() == 1) {
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.error_red));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Minimum 2 Characters Required");
                        } else {
                            lastNameTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            //Utils.setUpperHintColor(lastNameTIL, getColor(R.color.error_red));
                            Utils.setUpperHintColor(lastNameTIL, getColor(R.color.light_gray));
                            lastNameErrorLL.setVisibility(VISIBLE);
                            lastNameErrorTV.setText("Field Required");

                        }
                    } else {
                        lastNameET.setHint("Last Name");
                        lastNameTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        Utils.setUpperHintColor(lastNameTIL, getColor(R.color.primary_green));
                    }
                }
            });

            emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b) {
                        emailET.setHint("");
                        if (emailET.getText().toString().trim().length() > 5 && !Utils.isValidEmail(emailET.getText().toString().trim())) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Please Enter a valid Email");
                        } else if (emailET.getText().toString().trim().length() > 5 && Utils.isValidEmail(emailET.getText().toString().trim())) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.primary_black));
                            emailErrorLL.setVisibility(GONE);
                        } else if (emailET.getText().toString().trim().length() == 0) {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
//                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.light_gray));
                            emailTIL.setHint("Coyni@example.com");
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Field Required");
                        } else {
                            emailTIL.setBoxStrokeColorStateList(Utils.getErrorColorState());
                            Utils.setUpperHintColor(emailTIL, getColor(R.color.error_red));
                            emailErrorLL.setVisibility(VISIBLE);
                            emailErrorTV.setText("Please Enter a valid Email");
                        }
                    } else {
                        emailET.setHint("Coyni@example.com");
                        emailTIL.setBoxStrokeColor(getResources().getColor(R.color.primary_green));
                        emailTIL.setHint("Email");
                        Utils.setUpperHintColor(emailTIL, getColor(R.color.primary_green));
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getDescription(String htmlString, String option) {
        try {
            SpannableString ss = new SpannableString(htmlString);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    try {
                        String strDesc = "";
                        if (option.equals("...View More")) {
                            strDesc = Html.fromHtml(brandsResponseObj.getData().getBrands().get(0).getDescription().replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\s+", " ").trim() + " ...View Less").toString();
                            getDescription(strDesc, "...View Less");
                        } else {
                            String description = brandsResponseObj.getData().getBrands().get(0).getDescription().substring(0, 200).replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\s+", " ").trim() + " ...View More";
                            strDesc = Html.fromHtml(description).toString();
                            getDescription(strDesc, "...View More");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.parseColor("#35BAB6"));
                    ds.setUnderlineText(false);
                }
            };
            int startIndex = htmlString.indexOf(option);
            int endIndex = htmlString.length();
            ss.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            brandDescTV.setText(ss);
            brandDescTV.setMovementMethod(LinkMovementMethod.getInstance());
            brandDescTV.setHighlightColor(Color.TRANSPARENT);
        } catch (Exception ex) {
            ex.printStackTrace();
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
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
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
                    amountET.setText(Utils.USNumberFormat(Double.parseDouble(selectedFixedAmount)));
                    amountTIL.setBoxStrokeColorStateList(Utils.getNormalColorState());
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
                    week = Double.parseDouble(objLimit.getWeeklyAccountLimit());
                }
                if (objLimit.getDailyAccountLimit() != null && !objLimit.getDailyAccountLimit().toLowerCase().equals("NA") && !objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    daily = Double.parseDouble(objLimit.getDailyAccountLimit());
                }
                strCurrency = " USD";
                minValue = Double.parseDouble(objLimit.getMinimumLimit());
                if ((week == 0 || week < 0) && daily > 0) {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
//                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                } else if ((daily == 0 || daily < 0) && week > 0) {
                    strLimit = "week";
                    maxValue = week;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(week));
//                    tvLimit.setText("Your weekly limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                } else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    strLimit = "unlimited";
//                    tvLimit.setText("Your daily limit is " + objLimit.getDailyAccountLimit() + " USD");
                } else {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
//                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                }
            } else {
//                tvLimit.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void giftCardPreview() {
        try {
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
            TextView tv_lable = prevDialog.findViewById(R.id.tv_lable);

            MotionLayout slideToConfirm = prevDialog.findViewById(R.id.slideToConfirm);

            String strPFee = "";
            strPFee = Utils.convertBigDecimalUSDC(String.valueOf(fee));
            feeTV.setText(Utils.USNumberFormat(Double.parseDouble(strPFee)) + " CYN");
            Double total = Double.parseDouble(amountET.getText().toString()) + Double.parseDouble(strPFee);
            totalTV.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(total.toString()))) + " CYN");
            subTotalTV.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(amountET.getText().toString()))) + " CYN");
            giftCardAmountTV.setText(Utils.USNumberFormat(Double.parseDouble(Utils.convertBigDecimalUSDC(amountET.getText().toString()))));
            giftCardTypeTV.setText(brandsResponseObj.getData().getBrands().get(0).getItems().get(0).getRewardName());
            recipientMailTV.setText(emailET.getText().toString());


            slideToConfirm.setTransitionListener(new MotionLayout.TransitionListener() {
                @Override
                public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

                }

                @Override
                public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

                }

                @Override
                public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                    if (currentId == motionLayout.getEndState()) {
                        slideToConfirm.setInteractionEnabled(false);
                        tv_lable.setText("Verifying");
                        if ((isFaceLock || isTouchId) && Utils.checkAuthentication(GiftCardDetails.this)) {
                            if (isBiometric && ((isTouchId && Utils.isFingerPrint(GiftCardDetails.this)) || (isFaceLock))) {
                                prevDialog.dismiss();
                                Utils.checkAuthentication(GiftCardDetails.this, CODE_AUTHENTICATION_VERIFICATION);
                            } else {
                                prevDialog.dismiss();
                                startActivityForResult(new Intent(GiftCardDetails.this, PINActivity.class)
                                        .putExtra("TYPE", "ENTER")
                                        .putExtra("screen", "GiftCard"), FOR_RESULT);
                            }
                        } else {
                            prevDialog.dismiss();
                            startActivityForResult(new Intent(GiftCardDetails.this, PINActivity.class)
                                    .putExtra("TYPE", "ENTER")
                                    .putExtra("screen", "GiftCard"), FOR_RESULT);
                        }
                    }
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
            WithdrawRequest request = getGCWithdrawRequest();
            if (Utils.checkInternet(GiftCardDetails.this)) {
                buyTokenViewModel.withdrawTokens(request);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SetFaceLock() {
        try {
            isFaceLock = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsFacePin = mydatabase.rawQuery("Select * from tblFacePinLock", null);
            dsFacePin.moveToFirst();
            if (dsFacePin.getCount() > 0) {
                String value = dsFacePin.getString(1);
                if (value.equals("true")) {
                    isFaceLock = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isFaceLock = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SetTouchId() {
        try {
            isTouchId = false;
            mydatabase = openOrCreateDatabase("Coyni", MODE_PRIVATE, null);
            dsTouchID = mydatabase.rawQuery("Select * from tblThumbPinLock", null);
            dsTouchID.moveToFirst();
            if (dsTouchID.getCount() > 0) {
                String value = dsTouchID.getString(1);
                if (value.equals("true")) {
                    isTouchId = true;
                    objMyApplication.setLocalBiometric(true);
                } else {
                    isTouchId = false;
                    objMyApplication.setLocalBiometric(false);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK: {
                withdrawGiftCard();
            }
            break;
            case 235: {
                withdrawGiftCard();
            }
            break;
        }
    }

    public WithdrawRequest getGCWithdrawRequest() {
        WithdrawRequest request = new WithdrawRequest();
        GiftCardWithDrawInfo giftCardWithDrawInfo = new GiftCardWithDrawInfo();
        giftCardWithDrawInfo.setGiftCardName(brandsResponseObj.getData().getBrands().get(0).getBrandName());
        giftCardWithDrawInfo.setGiftCardCurrency(brandsResponseObj.getData().getBrands().get(0).getItems().get(0).getCurrencyCode());
        giftCardWithDrawInfo.setTotalAmount(Double.parseDouble(Utils.convertBigDecimalUSDC(amountET.getText().toString())));
        giftCardWithDrawInfo.setUtid(brandsResponseObj.getData().getBrands().get(0).getItems().get(0).getUtid());

        List<RecipientDetail> recipientDetailList = new ArrayList<>();
        RecipientDetail recipientDetail = new RecipientDetail();
        recipientDetail.setAmount(Double.parseDouble(Utils.convertBigDecimalUSDC(amountET.getText().toString())));
        recipientDetail.setFirstName(firstNameET.getText().toString());
        recipientDetail.setLastName(lastNameET.getText().toString());
        recipientDetail.setEmail(emailET.getText().toString());
        recipientDetailList.add(recipientDetail);

        giftCardWithDrawInfo.setRecipientDetails(recipientDetailList);

        request.setBankId(0L);
        request.setCardId(0L);
        request.setGiftCardWithDrawInfo(giftCardWithDrawInfo);
        request.setTokens(Double.parseDouble(Utils.convertBigDecimalUSDC(amountET.getText().toString())));
        request.setRemarks("");
        request.setWithdrawType(Utils.giftcardType);

        objMyApplication.setGcWithdrawRequest(request);
        return request;
    }
}