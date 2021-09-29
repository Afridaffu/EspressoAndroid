package com.coyni.android.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coyni.android.adapters.GiftAmountsAdapter;
import com.coyni.android.model.GlideApp;
import com.coyni.android.model.giftcard.Brand;
import com.coyni.android.model.giftcard.Items;
import com.coyni.android.model.transactions.LimitResponseData;
import com.coyni.android.model.transactions.TransactionLimitRequest;
import com.coyni.android.model.transactions.TransactionLimitResponse;
import com.coyni.android.model.transferfee.TransferFeeRequest;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.utils.MyApplication;
import com.coyni.android.utils.Utils;
import com.coyni.android.viewmodel.BuyViewModel;
import com.coyni.android.viewmodel.SendViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.coyni.android.R;

import java.util.ArrayList;
import java.util.List;

public class GiftCardDetailsActivity extends AppCompatActivity implements TextWatcher {
    MyApplication objMyApplication;
    SendViewModel sendViewModel;
    BuyViewModel buyViewModel;
    Brand objBrand;
    Double fee = 0.0, min = 0.0, max = 0.0, maxValue = 0.0, minValue = 0.0;
    TextInputEditText etAmount, etFirstName, etLastName, etEmail, etAmount1;
    TextInputLayout etlAmount1;
    TextView tvDesription;
    List<Items> listAmounts;
    RelativeLayout lyVariableAmt, lyFixedAmt;
    TextView tvMinMax, tvLimit;
    RecyclerView rvAmounts;
    ImageView imgArrow;
    CardView cvList;
    String amount = "", strLimit = "";
    Items selectedItem;
    TransactionLimitResponse objResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_gift_card_details);
            initialization();
            initObserver();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
            objMyApplication.userInactive(GiftCardDetailsActivity.this, this, false);
            objMyApplication.getAppHandler().removeCallbacks(objMyApplication.getAppRunnable());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(GiftCardDetailsActivity.this, this, true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        objMyApplication.getHandler().removeCallbacks(objMyApplication.getMyRunnable());
        objMyApplication.userInactive(GiftCardDetailsActivity.this, this, false);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s == etAmount.getEditableText()) {
            try {
                if (s.length() > 0 && !s.toString().equals(".") && !s.toString().equals(".00")) {
                    calculateFee(s.toString());
                    amount = s.toString();
//                    USFormat(etAmount);
                } else if (s.toString().equals(".")) {
                    etAmount.setText("");
                    amount = "";
                } else {
                    etAmount.removeTextChangedListener(GiftCardDetailsActivity.this);
                    etAmount.setText("");
                    etAmount.addTextChangedListener(GiftCardDetailsActivity.this);
                    amount = "";
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initialization() {
        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sent);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            tvDesription = (TextView) findViewById(R.id.tvDesription);
            etAmount = (TextInputEditText) findViewById(R.id.etAmount);
            etAmount1 = (TextInputEditText) findViewById(R.id.etAmount1);
            etFirstName = (TextInputEditText) findViewById(R.id.etFirstName);
            etLastName = (TextInputEditText) findViewById(R.id.etLastName);
            etEmail = (TextInputEditText) findViewById(R.id.etEmail);
            tvMinMax = (TextView) findViewById(R.id.tvMinMax);
            tvLimit = (TextView) findViewById(R.id.tvLimit);
            etlAmount1 = (TextInputLayout) findViewById(R.id.etlAmount1);
            rvAmounts = (RecyclerView) findViewById(R.id.rvAmounts);
            LinearLayout layoutProceed = (LinearLayout) findViewById(R.id.layoutProceed);
            ImageView imgBrand = (ImageView) findViewById(R.id.imgBrand);
            imgArrow = (ImageView) findViewById(R.id.imgArrow);
            lyVariableAmt = (RelativeLayout) findViewById(R.id.lyVariableAmt);
            lyFixedAmt = (RelativeLayout) findViewById(R.id.lyFixedAmt);
            cvList = (CardView) findViewById(R.id.cvList);

            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor("#FFFFFF"));
            }
            objMyApplication = (MyApplication) getApplicationContext();
            sendViewModel = new ViewModelProvider(this).get(SendViewModel.class);
            buyViewModel = new ViewModelProvider(this).get(BuyViewModel.class);
            if (Utils.checkInternet(GiftCardDetailsActivity.this)) {
                TransactionLimitRequest obj = new TransactionLimitRequest();
                obj.setTransactionType(Integer.parseInt(Utils.withdrawType));
                obj.setTransactionSubType(Integer.parseInt(Utils.giftcardType));
                buyViewModel.transactionLimits(obj);
            } else {
                //Toast.makeText(GiftCardDetailsActivity.this, getString(R.string.internet), Toast.LENGTH_LONG).show();
                Utils.displayAlert(getString(R.string.internet), GiftCardDetailsActivity.this);
            }
            if (getIntent().getSerializableExtra("brand") != null) {
                objBrand = (Brand) getIntent().getSerializableExtra("brand");
                if (objBrand != null) {
                    tvTitle.setText(objBrand.getBrandName() + " Gift Card");
                    String description = "", htmlString = "";
                    if (objBrand.getDescription().length() > 200) {
                        description = objBrand.getDescription().substring(0, 200).replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\s+", " ").trim() + " ...View More";
                        //tvDesription.setText(Html.fromHtml(description));
                        htmlString = Html.fromHtml(description).toString();
                        getDescription(htmlString, "...View More");
                    } else {
                        tvDesription.setText(Html.fromHtml(objBrand.getDescription()));
                    }
                    if (objBrand.getImageUrls().get_1200w326ppi() != null && !objBrand.getImageUrls().get_1200w326ppi().equals("")) {
                        GlideApp.with(GiftCardDetailsActivity.this)
                                .load(objBrand.getImageUrls().get_1200w326ppi())
                                .into(imgBrand);
                    }
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
                            lyFixedAmt.setVisibility(View.VISIBLE);
                            lyVariableAmt.setVisibility(View.GONE);
                            bindAmounts();
                        } else {
                            lyFixedAmt.setVisibility(View.GONE);
                            lyVariableAmt.setVisibility(View.VISIBLE);
                            tvMinMax.setText("$" + Utils.convertBigDecimalUSDC(String.valueOf(min)) + " - $" + Utils.convertBigDecimalUSDC(String.valueOf(max)));
                            selectedItem = objBrand.getItems().get(0);
                        }
                    }
                }
            }
            layoutProceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.hideKeypad(GiftCardDetailsActivity.this, v);
                    if (Utils.checkInternet(GiftCardDetailsActivity.this)) {
                        if (validation()) {
                            Intent i = new Intent(GiftCardDetailsActivity.this, WithdrawTokenPreviewActivity.class);
                            i.putExtra("screen", "giftcard");
                            i.putExtra("email", etEmail.getText().toString().trim());
                            i.putExtra("brand", objBrand);
                            i.putExtra("amount", amount);
                            i.putExtra("fee", String.valueOf(fee));
                            i.putExtra("fName", etFirstName.getText().toString().trim());
                            i.putExtra("lName", etLastName.getText().toString().trim());
                            i.putExtra("selectedItem", selectedItem);
                            startActivity(i);
                        }
                    } else {
                        Utils.displayAlert(getString(R.string.internet), GiftCardDetailsActivity.this);
                    }
                }
            });
            etAmount.addTextChangedListener(this);

            etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        USFormat(etAmount);
                    } else if (hasFocus) {
                        InputFilter[] FilterArray = new InputFilter[1];
                        FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
                        etAmount.setFilters(FilterArray);
                    }
                }
            });

            imgArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cvList.getVisibility() == View.VISIBLE) {
                        cvList.setVisibility(View.GONE);
                        imgArrow.setImageResource(R.drawable.ic_down_arrow);
                    } else {
                        cvList.setVisibility(View.VISIBLE);
                        imgArrow.setImageResource(R.drawable.ic_up_arrow);
                    }
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
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
                            strDesc = Html.fromHtml(objBrand.getDescription().replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\s+", " ").trim() + " ...View Less").toString();
                            getDescription(strDesc, "...View Less");
                        } else {
                            String description = objBrand.getDescription().substring(0, 200).replaceAll("[\\t\\n\\r]+", " ").replaceAll("\\s+", " ").trim() + " ...View More";
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
            tvDesription.setText(ss);
            tvDesription.setMovementMethod(LinkMovementMethod.getInstance());
            tvDesription.setHighlightColor(Color.TRANSPARENT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initObserver() {
        sendViewModel.getTransferFeeMutableLiveData().observe(this, new Observer<TransferFeeResponse>() {
            @Override
            public void onChanged(TransferFeeResponse transferFeeResponse) {
                if (transferFeeResponse != null) {
                    fee = transferFeeResponse.getData().getFee();
                }
            }
        });

        buyViewModel.getLimitMutableLiveData().observe(this, new Observer<TransactionLimitResponse>() {
            @Override
            public void onChanged(TransactionLimitResponse transactionLimitResponse) {
                if (transactionLimitResponse != null) {
                    objResponse = transactionLimitResponse;
                    setDailyWeekLimit(transactionLimitResponse.getData());
                }
            }
        });
    }

    private void calculateFee(String strAmount) {
        try {
            if (!strAmount.trim().equals("")) {
                TransferFeeRequest request = new TransferFeeRequest();
                request.setTokens(strAmount.trim());
                request.setTxnType(Utils.withdrawType);
                request.setTxnSubType(Utils.giftcardType);
                if (Utils.checkInternet(GiftCardDetailsActivity.this)) {
                    sendViewModel.transferFee(request);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Boolean validation() {
        Boolean value = true;
        try {
            if (amount.equals("") || Double.parseDouble(amount.replace(",", "")) == 0.0) {
                Utils.displayAlert("Amount is required", GiftCardDetailsActivity.this);
                return value = false;
            } else if (lyVariableAmt.getVisibility() == View.VISIBLE && !amount.equals("") && Double.parseDouble(amount.replace(",", "")) < min) {
                Utils.displayAlert("Amount should be greater than or equal to $" + Utils.convertBigDecimalUSDC(String.valueOf(min)), GiftCardDetailsActivity.this);
                etAmount.requestFocus();
                return value = false;
            } else if (lyVariableAmt.getVisibility() == View.VISIBLE && !amount.equals("") && Double.parseDouble(amount.replace(",", "")) < minValue) {
                Utils.displayAlert("Amount should be greater than or equal to $" + Utils.convertBigDecimalUSDC(String.valueOf(minValue)), GiftCardDetailsActivity.this);
                etAmount.requestFocus();
                return value = false;
            } else if (lyVariableAmt.getVisibility() == View.VISIBLE && !amount.equals("") && Double.parseDouble(amount.replace(",", "")) > max) {
                Utils.displayAlert("Amount should be less than or equal to $" + Utils.convertBigDecimalUSDC(String.valueOf(max)), GiftCardDetailsActivity.this);
                etAmount.requestFocus();
                return value = false;
            } else if (objResponse.getData().getTokenLimitFlag() && lyVariableAmt.getVisibility() == View.VISIBLE && !amount.equals("") && Double.parseDouble(amount.replace(",", "")) > maxValue) {
                //Utils.displayAlert("Amount should be less than or equal to $" + Utils.convertBigDecimalUSDC(String.valueOf(maxValue)), GiftCardDetailsActivity.this);
                if (strLimit.equals("daily")) {
                    Utils.displayAlert("Amount exceeds daily limit", GiftCardDetailsActivity.this);
                } else if (strLimit.equals("week")) {
                    Utils.displayAlert("Amount exceeds weekly limit", GiftCardDetailsActivity.this);
                }
                etAmount.requestFocus();
                return value = false;
            } else if (lyVariableAmt.getVisibility() == View.VISIBLE && objResponse.getData().getTokenLimitFlag() && !strLimit.equals("unlimited") && Double.parseDouble(amount.replace(",", "")) > maxValue) {
                if (strLimit.equals("daily")) {
                    Utils.displayAlert("Amount exceeds daily limit", GiftCardDetailsActivity.this);
                } else if (strLimit.equals("week")) {
                    Utils.displayAlert("Amount exceeds weekly limit", GiftCardDetailsActivity.this);
                }
                return value = false;
            } else if (lyVariableAmt.getVisibility() == View.GONE && objResponse.getData().getTokenLimitFlag() && !strLimit.equals("unlimited") && Double.parseDouble(amount.replace(",", "")) > max) {
                if (strLimit.equals("daily")) {
                    Utils.displayAlert("Amount exceeds daily limit", GiftCardDetailsActivity.this);
                } else if (strLimit.equals("week")) {
                    Utils.displayAlert("Amount exceeds weekly limit", GiftCardDetailsActivity.this);
                }
                return value = false;
            } else if (etFirstName.getText().toString().equals("")) {
                Utils.displayAlert("First Name is required", GiftCardDetailsActivity.this);
                etFirstName.requestFocus();
                return value = false;
            } else if (etLastName.getText().toString().equals("")) {
                Utils.displayAlert("Last Name is required", GiftCardDetailsActivity.this);
                etLastName.requestFocus();
                return value = false;
            } else if (etEmail.getText().toString().equals("")) {
                Utils.displayAlert("Please enter your email", GiftCardDetailsActivity.this);
                etEmail.requestFocus();
                return value = false;
            } else if (!isEmailValid(etEmail.getText().toString().trim())) {
                Utils.displayAlert("Please enter valid Email", GiftCardDetailsActivity.this);
                etEmail.requestFocus();
                return value = false;
            } else if (Double.parseDouble(amount.replace(",", "")) > objMyApplication.getGBTBalance()) {
                Utils.displayAlert("Amount should be less than your wallet balance.", GiftCardDetailsActivity.this);
                return value = false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void bindAmounts() {
        try {
            if (listAmounts != null && listAmounts.size() > 0) {
                GiftAmountsAdapter giftAmountsAdapter = new GiftAmountsAdapter(listAmounts, GiftCardDetailsActivity.this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(GiftCardDetailsActivity.this);
                rvAmounts.setLayoutManager(mLayoutManager);
                rvAmounts.setItemAnimator(new DefaultItemAnimator());
                rvAmounts.setAdapter(giftAmountsAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getAmount(Items objItem) {
        try {
            etAmount1.setText("$" + Utils.convertBigDecimalUSDC(String.valueOf(objItem.getFaceValue())));
            cvList.setVisibility(View.GONE);
            imgArrow.setImageResource(R.drawable.ic_down_arrow);
            calculateFee(String.valueOf(objItem.getFaceValue()));
            amount = String.valueOf(objItem.getFaceValue());
            selectedItem = objItem;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void USFormat(TextInputEditText etAmount) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlendecimal)));
            etAmount.setFilters(FilterArray);
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.removeTextChangedListener(GiftCardDetailsActivity.this);
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.setSelection(etAmount.getText().length() - 3);
            etAmount.addTextChangedListener(GiftCardDetailsActivity.this);
            FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(getString(R.string.maxlength)));
            etAmount.setFilters(FilterArray);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setDailyWeekLimit(LimitResponseData objLimit) {
        try {
            if (objLimit.getTokenLimitFlag()) {
                tvLimit.setVisibility(View.VISIBLE);
                Double week = 0.0, daily = 0.0;
                String strCurrency = "", strAmount = "";
                if (objLimit.getWeeklyAccountLimit() != null && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("NA") && !objLimit.getWeeklyAccountLimit().toLowerCase().equals("unlimited")) {
                    week = Double.parseDouble(objLimit.getWeeklyAccountLimit());
                }
                if (objLimit.getDailyAccountLimit() != null && !objLimit.getDailyAccountLimit().toLowerCase().equals("NA") && !objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    daily = Double.parseDouble(objLimit.getDailyAccountLimit());
                }
                strCurrency = " USD";
                minValue = objLimit.getMinimumLimit();
                if ((week == 0 || week < 0) && daily > 0) {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                } else if ((daily == 0 || daily < 0) && week > 0) {
                    strLimit = "week";
                    maxValue = week;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(week));
                    tvLimit.setText("Your weekly limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                } else if (objLimit.getDailyAccountLimit().toLowerCase().equals("unlimited")) {
                    strLimit = "unlimited";
                    tvLimit.setText("Your daily limit is " + objLimit.getDailyAccountLimit() + " USD");
                } else {
                    strLimit = "daily";
                    maxValue = daily;
                    strAmount = Utils.convertBigDecimalUSDC(String.valueOf(daily));
                    tvLimit.setText("Your daily limit is " + Utils.USNumberFormat(Double.parseDouble(strAmount)) + strCurrency);
                }
            } else {
                tvLimit.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}