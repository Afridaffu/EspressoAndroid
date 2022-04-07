package com.greenbox.coyni.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;
import com.greenbox.coyni.model.RangeDates;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.util.ArrayList;

public class MerchantTransactionsFilterDialog extends BaseDialog {
    private Boolean isFilters = false;
    private ArrayList<Integer> transactionType = new ArrayList<Integer>();
    private ArrayList<Integer> transactionSubType = new ArrayList<Integer>();
    private ArrayList<Integer> txnStatus = new ArrayList<Integer>();
    private String strStartAmount = "", strEndAmount = "", strFromDate = "", strToDate = "", strSelectedDate = "", tempStrSelectedDate = "";

    private Long mLastClickTimeFilters = 0L;
    private String dateSelected = null;
    private MyApplication objMyApplication;
    private Context context;
    private TransactionListRequest filterTransactionListRequest = null;
    private EditText getDateFromPickerET;
    private RangeDates rangeDates;
    private Activity activity;
    private DateRangePickerDialog dateRangePickerDialog;

    public MerchantTransactionsFilterDialog(Context context, TransactionListRequest filterTransactionListRequest) {
        super(context);
        this.context = context;
        this.filterTransactionListRequest = filterTransactionListRequest;
        activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_transactions_filter);
        initFields();

    }

    private void initFields() {

        objMyApplication = (MyApplication) context.getApplicationContext();
        Chip transTypeSalesOrderToken = findViewById(R.id.transTypeSalesOrderToken);
        Chip transTypeRefund = findViewById(R.id.transTypeRefund);
        Chip transTypeMerchantPayout = findViewById(R.id.transTypeMerchantPayout);
        Chip transTypeMonthlyServiceFee = findViewById(R.id.transTypeMonthlyServiceFee);

        Chip transStatusCompleted = findViewById(R.id.transStatusCompleted);
        Chip transStatusRefund = findViewById(R.id.transStatusRefund);
        Chip transStatusPartialRefund = findViewById(R.id.transStatusPartialRefund);

        EditText transAmountStartET = findViewById(R.id.transAmountStartET);
        EditText transAmountEndET = findViewById(R.id.transAmountEndET);

        CardView applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        LinearLayout dateRangePickerLL = findViewById(R.id.dateRangePickerLL);
        getDateFromPickerET = findViewById(R.id.datePickET);
        TextView resetFiltersTV = findViewById(R.id.resetFiltersTV);

        if (filterTransactionListRequest != null) {
            isFilters = true;
            transactionType = filterTransactionListRequest.getTransactionType();
            if (transactionType == null) {
                transactionType = new ArrayList<>();
            }
            if (transactionType.size() > 0) {
                for (int i = 0; i < transactionType.size(); i++) {
                    switch (transactionType.get(i)) {
                        case Utils.saleOrder:
                            transTypeSalesOrderToken.setChecked(true);
                            break;
                        case Utils.refund:
                            transTypeRefund.setChecked(true);
                            break;
                        case Utils.merchantPayout:
                            transTypeMerchantPayout.setChecked(true);
                            break;
                        case Utils.monthlyServiceFee:
                            transTypeMonthlyServiceFee.setChecked(true);
                            break;
                    }
                }
            }
            transactionSubType = filterTransactionListRequest.getTransactionSubType();
            if (transactionSubType == null) {
                transactionSubType = new ArrayList<>();
            }
            txnStatus = filterTransactionListRequest.getTxnStatus();
            if (txnStatus == null) {
                txnStatus = new ArrayList<>();
            }
            if (txnStatus.size() > 0) {
                for (int i = 0; i < txnStatus.size(); i++) {
                    switch (txnStatus.get(i)) {
                        case Utils.completed:
                            transStatusCompleted.setChecked(true);
                            break;
                        case Utils.refund:
                            transStatusRefund.setChecked(true);
                            break;
                        case Utils.cancelled:
                            transStatusPartialRefund.setChecked(true);
                            break;
                    }
                }
            }
            strStartAmount = filterTransactionListRequest.getFromAmount();
            if (strStartAmount != null && !strStartAmount.trim().equals("")) {
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(String.valueOf(R.string.maxlendecimal)));
                transAmountStartET.setFilters(FilterArray);
                transAmountStartET.setText(strStartAmount);
            }

            strEndAmount = filterTransactionListRequest.getToAmount();
            if (strEndAmount != null && !strEndAmount.trim().equals("")) {
                InputFilter[] FilterArray = new InputFilter[1];
                FilterArray[0] = new InputFilter.LengthFilter(Integer.parseInt(String.valueOf(R.string.maxlendecimal)));
                transAmountEndET.setFilters(FilterArray);
                transAmountEndET.setText(strEndAmount);
            }

            if (!strSelectedDate.equals("")) {
                getDateFromPickerET.setText(strSelectedDate);
            }
        } else {
            if(transactionType != null) {
                transactionType.clear();
            }
            if(transactionSubType != null) {
                transactionSubType.clear();
            }
            if(txnStatus != null) {
                txnStatus.clear();
            }
            strFromDate = "";
            strToDate = "";
            strStartAmount = "";
            strEndAmount = "";
            isFilters = false;
            strSelectedDate = "";
        }

        resetFiltersTV.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                return;
            }
            mLastClickTimeFilters = SystemClock.elapsedRealtime();
            if(transactionType != null) {
                transactionType.clear();
            }
            if(transactionSubType != null) {
                transactionSubType.clear();
            }
            if(txnStatus != null) {
                txnStatus.clear();
            }
            strFromDate = "";
            strToDate = "";
            strStartAmount = "";
            strEndAmount = "";
            isFilters = false;
            strSelectedDate = "";
            transAmountStartET.setText("");
            transAmountEndET.setText("");
            getDateFromPickerET.setText("");

            transAmountStartET.clearFocus();
            transAmountEndET.clearFocus();
            getDateFromPickerET.clearFocus();

            transTypeSalesOrderToken.setChecked(false);
            transTypeRefund.setChecked(false);
            transTypeMerchantPayout.setChecked(false);
            transTypeMonthlyServiceFee.setChecked(false);
            transTypeRefund.setChecked(false);

            transStatusCompleted.setChecked(false);
            transStatusRefund.setChecked(false);
            transStatusPartialRefund.setChecked(false);

            transAmountStartET.setText("");
            transAmountEndET.setText("");
            getDateFromPickerET.setText("");
            getOnDialogClickListener().onDialogClicked(Utils.resetFilter, null);
            dismiss();

        });

        transTypeSalesOrderToken.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.saleOrder);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.saleOrder) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transTypeRefund.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.refund);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.refund) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transTypeMerchantPayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.merchantPayout);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.merchantPayout) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transTypeMonthlyServiceFee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.monthlyServiceFee);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.monthlyServiceFee) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transStatusCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txnStatus.add(Utils.completed);
                } else {
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.completed) {
                            txnStatus.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transStatusRefund.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txnStatus.add(Utils.refund);
//                    transStatusCanceled.setChecked(false);
                } else {
//                    transStatusCanceled.setChecked(false);
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.refund) {
                            txnStatus.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transStatusPartialRefund.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txnStatus.add(Utils.inProgress);
                } else {
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.inProgress) {
                            txnStatus.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        transAmountStartET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(".")) {
                    transAmountStartET.setText("");
                }
            }
        });

        transAmountEndET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(".")) {
                    transAmountEndET.setText("");
                }
            }
        });

        transAmountStartET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    transAmountStartET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(String.valueOf(R.string.maxlendecimal)))});
                    USFormat(transAmountStartET, "START");

                    try {

                        if (!transAmountStartET.getText().toString().equals("") && !transAmountStartET.getText().toString().equals("")) {

                            Double startAmount = Double.parseDouble(transAmountStartET.getText().toString().replace(",", "").trim());
                            Double endAmount = Double.parseDouble(transAmountEndET.getText().toString().replace(",", "").trim());
                            if (endAmount < startAmount) {
                                Utils.displayAlert(context.getString(R.string.transAmountAlert), activity, "", "");

                                transAmountStartET.setText("");
                                strStartAmount = "";
                                transAmountEndET.setText("");
                                strEndAmount = "";
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    transAmountStartET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(String.valueOf(R.string.maxlength)))});
                }
            }
        });

        transAmountEndET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (transAmountStartET.getText().toString().equals("")) {
                    transAmountStartET.setText("0.00");
                }

                if (!hasFocus) {
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(String.valueOf(R.string.maxlendecimal)))});
                    USFormat(transAmountEndET, "END");
                    try {

                        if (!transAmountEndET.getText().toString().equals("") && !transAmountEndET.getText().toString().equals("")) {

                            Double startAmount = Double.parseDouble(transAmountStartET.getText().toString().replace(",", "").trim());
                            Double endAmount = Double.parseDouble(transAmountEndET.getText().toString().replace(",", "").trim());
                            if (endAmount < startAmount) {
                                Utils.displayAlert(context.getString(R.string.transAmountAlert), activity, "", "");
                                transAmountStartET.setText("");
                                strStartAmount = "";
                                transAmountEndET.setText("");
                                strEndAmount = "";
                            }
                        }
                        if (transAmountStartET.getText().toString().equals("")) {
                            transAmountStartET.setText("0.00");
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(String.valueOf(R.string.maxlength)))});
                }
            }
        });

        transAmountStartET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    transAmountStartET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(String.valueOf(R.string.maxlendecimal)))});
                    USFormat(transAmountStartET, "START");
                    transAmountStartET.clearFocus();
                }

                return false;
            }
        });

        transAmountEndET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(String.valueOf(R.string.maxlendecimal)))});
                    USFormat(transAmountEndET, "END");
                    transAmountEndET.clearFocus();
                    if (transAmountStartET.getText().toString().equals("")) {
                        transAmountStartET.setText("0.00");
                    }
                }
                return false;
            }
        });

        applyFilterBtnCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterTransactionListRequest = new TransactionListRequest();
                isFilters = false;
                transAmountStartET.clearFocus();
                transAmountEndET.clearFocus();

                if (transactionType.size() > 0) {
                    isFilters = true;
                    filterTransactionListRequest.setTransactionType(transactionType);
                }
                if (transactionSubType.size() > 0) {
                    isFilters = true;
                    filterTransactionListRequest.setTransactionSubType(transactionSubType);
                }
                if (txnStatus.size() > 0) {
                    isFilters = true;
                    filterTransactionListRequest.setTxnStatus(txnStatus);
                }
                if (!transAmountStartET.getText().toString().trim().equals("")) {
                    isFilters = true;
                    filterTransactionListRequest.setFromAmount(transAmountStartET.getText().toString().replace(",", ""));
                    filterTransactionListRequest.setFromAmountOperator(">=");
                } else {
                    strStartAmount = "";
                }
                if (!transAmountEndET.getText().toString().trim().equals("")) {
                    isFilters = true;
                    filterTransactionListRequest.setToAmount(transAmountEndET.getText().toString().replace(",", ""));
                    filterTransactionListRequest.setToAmountOperator("<=");

                    if (transAmountStartET.getText().toString().trim().equals("") || transAmountStartET.getText().toString().trim().equals("0.00")) {
                        filterTransactionListRequest.setFromAmount("0.00");
                        filterTransactionListRequest.setFromAmountOperator(">=");
                        strStartAmount = "0.00";
                    }
                } else {
                    strEndAmount = "";
                }
                if (!strFromDate.equals("")) {
                    isFilters = true;
                    filterTransactionListRequest.setUpdatedFromDate(objMyApplication.exportDate(strFromDate));
                }
                if (!strToDate.equals("")) {
                    isFilters = true;
                    filterTransactionListRequest.setUpdatedToDate(objMyApplication.exportDate(strToDate));
                }

                if (!transAmountStartET.getText().toString().equals("") && !transAmountEndET.getText().toString().equals("")) {
                    Double startAmount = Double.parseDouble(transAmountStartET.getText().toString().replace(",", "").trim());
                    Double endAmount = Double.parseDouble(transAmountEndET.getText().toString().replace(",", "").trim());
                    if (endAmount < startAmount) {
                        Utils.displayAlert(context.getString(R.string.transAmountAlert), activity, "", "");
                        transAmountStartET.setText("");
                        strStartAmount = "";
                        transAmountEndET.setText("");
                        strEndAmount = "";
                    }
                }
                getOnDialogClickListener().onDialogClicked(Utils.applyFilter, filterTransactionListRequest);
                dismiss();
            }
        });

        dateRangePickerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                    return;
                }
                mLastClickTimeFilters = SystemClock.elapsedRealtime();
                if (dateRangePickerDialog != null && dateRangePickerDialog.isShowing()) {
                    return;
                }
                showCalendar();
            }
        });

        getDateFromPickerET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                    return;
                }
                mLastClickTimeFilters = SystemClock.elapsedRealtime();
                if (dateRangePickerDialog != null && dateRangePickerDialog.isShowing()) {
                    return;
                }
                showCalendar();
            }
        });
    }

    public void showCalendar() {
        // custom dialog
        dateRangePickerDialog = new DateRangePickerDialog(context);
        dateRangePickerDialog.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogClicked(String action, Object value) {
                if (action.equals(Utils.datePicker)) {
                    rangeDates = (RangeDates) value;
                    strFromDate = rangeDates.getUpdatedFromDate();
                    strToDate = rangeDates.getUpdatedToDate();
                    tempStrSelectedDate = rangeDates.getFullDate();
                    getDateFromPickerET.setText(tempStrSelectedDate);
                }
            }
        });
        dateRangePickerDialog.show();

    }

    private void USFormat(EditText etAmount, String mode) {
        try {
            String strAmount = "";
            strAmount = Utils.convertBigDecimalUSDC(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Double.parseDouble(strAmount)));
            etAmount.setSelection(etAmount.getText().length());
            if (mode.equals("START")) {
                strStartAmount = Utils.USNumberFormat(Double.parseDouble(strAmount));
            } else {
                strEndAmount = Utils.USNumberFormat(Double.parseDouble(strAmount));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
