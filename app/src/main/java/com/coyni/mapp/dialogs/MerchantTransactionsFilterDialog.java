package com.coyni.mapp.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.chip.Chip;
import com.coyni.mapp.R;
import com.coyni.mapp.model.RangeDates;
import com.coyni.mapp.model.transaction.TransactionListRequest;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MerchantTransactionsFilterDialog extends BaseDialog {
    private Boolean isFilters = false;
    private ArrayList<Integer> transactionType = new ArrayList<Integer>();
    private ArrayList<Integer> transactionSubType = new ArrayList<Integer>();
    private ArrayList<Integer> txnStatus = new ArrayList<Integer>();
    private String strStartAmount = "", strEndAmount = "", strFromDate = "", strToDate = "", strSelectedDate = "", tempStrSelectedDate = "", strupdated = "", strended = "", strF = "", strT = "";

    private Long mLastClickTimeFilters = 0L;
    private String dateSelected = null, storedSelectDate = "";
    private MyApplication objMyApplication;
    private Context context;
    private TransactionListRequest filterTransactionListRequest = null;
    private EditText getDateFromPickerET;
    private RangeDates rangeDates;
    private Activity activity;
    private DateRangePickerDialog dateRangePickerDialog;
    private String displayFormat = "MM-dd-yyyy";
    private SimpleDateFormat displayFormatter;
    private Date startDateD = null;
    private Date endDateD = null;

    public MerchantTransactionsFilterDialog(Context context, TransactionListRequest filterTransactionListRequest1) {
        super(context);
        this.context = context;
        this.filterTransactionListRequest = filterTransactionListRequest1;
        activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.merchant_transactions_filter);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.90);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, height);
        displayFormatter = new SimpleDateFormat(displayFormat);
        initFields();

    }

    private void initFields() {

        objMyApplication = (MyApplication) context.getApplicationContext();
        Chip transTypeSalesOrderEcomerce = findViewById(R.id.transTypeSalesOrderEcomerce);
        Chip transTypeSalesOrderRetail = findViewById(R.id.transTypeSalesOrderRetail);
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
            if (filterTransactionListRequest.getTransactionType() != null) {
                transactionType.addAll(filterTransactionListRequest.getTransactionType());
            }

            if (transactionType == null) {
                transactionType = new ArrayList<>();
            }
            if (transactionType.size() > 0) {
                for (int i = 0; i < transactionType.size(); i++) {
                    switch (transactionType.get(i)) {
                        case Utils.eComerce:
                            transTypeSalesOrderEcomerce.setChecked(true);
                            break;
                        case Utils.retailMobile:
                            transTypeSalesOrderRetail.setChecked(true);
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
            if (filterTransactionListRequest.getTransactionSubType() != null) {
                transactionSubType.addAll(filterTransactionListRequest.getTransactionSubType());
            }
            if (transactionSubType == null) {
                transactionSubType = new ArrayList<>();
            }

            if (filterTransactionListRequest.getTxnStatus() != null) {
                txnStatus.addAll(filterTransactionListRequest.getTxnStatus());
            }
            if (txnStatus == null) {
                txnStatus = new ArrayList<>();
            }
            if (txnStatus.size() > 0) {
                for (int i = 0; i < txnStatus.size(); i++) {
                    switch (txnStatus.get(i)) {
                        case Utils.completed:
                            transStatusCompleted.setChecked(true);
                            break;
                        case Utils.refunded:
                            transStatusRefund.setChecked(true);
                            break;
                        case Utils.partialRefund:
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

            if (filterTransactionListRequest.getUpdatedFromDate() != null && !filterTransactionListRequest.getUpdatedFromDate().equals("")) {
                strF = filterTransactionListRequest.getUpdatedFromDate();
                if (strF.contains(".")) {
                    strF = strF.substring(0, strF.lastIndexOf("."));
                }
                strF = objMyApplication.convertZoneDateTime(strF, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy");
            }

            if (filterTransactionListRequest.getUpdatedToDate() != null && !filterTransactionListRequest.getUpdatedToDate().equals("")) {
                strT = filterTransactionListRequest.getUpdatedToDate();
                if (strT.contains(".")) {
                    strT = strT.substring(0, strT.lastIndexOf("."));
                }
                strT = objMyApplication.convertZoneDateTime(strT, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy");
            }

            String formatToDisplay = "MMM dd, yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatToDisplay);
            try {
                if (strF != null && strT != null) {
                    startDateD = displayFormatter.parse(strF);
                    endDateD = displayFormatter.parse(strT);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (startDateD != null && endDateD != null) {
                strupdated = simpleDateFormat.format(startDateD);
                strended = simpleDateFormat.format(endDateD);
            }
//                strupdated = objMyApplication.convertZoneDateTime(strF,"MM:dd:yyyy","MMM dd, yyyy");
//                strended = objMyApplication.convertZoneDateTime(strT,"MM:dd:yyyy","MMM dd, yyyy");
            if (strupdated != null && !strupdated.equals("") && strended != null && !strended.equals("")) {
                strSelectedDate = strupdated + "  " + strended;
            }
//                strSelectedDate = strF + " " + strT;
            if (strSelectedDate != null && !strSelectedDate.equals("")) {
                getDateFromPickerET.setText(strSelectedDate);
            } else {
                getDateFromPickerET.setText("");
            }


            try {
                if (startDateD != null && !startDateD.equals("") && endDateD != null && !endDateD.equals("")) {
                    SimpleDateFormat rangeFormat = new SimpleDateFormat(DateRangePickerDialog.displayFormat);
                    rangeDates = new RangeDates();
                    strFromDate = rangeFormat.format(startDateD);
                    rangeDates.setUpdatedFromDate(strFromDate);
                    strToDate = rangeFormat.format(endDateD);
                    rangeDates.setUpdatedToDate(strToDate);
                    rangeDates.setFullDate(strSelectedDate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (transactionType != null) {
                transactionType.clear();
            }
            if (transactionSubType != null) {
                transactionSubType.clear();
            }
            if (txnStatus != null) {
                txnStatus.clear();
            }
            strFromDate = "";
            strToDate = "";
            strStartAmount = "";
            strEndAmount = "";
//            isFilters = false;
            tempStrSelectedDate = "";
            storedSelectDate = "";
        }

        resetFiltersTV.setOnClickListener(view -> {
            if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                return;
            }
            mLastClickTimeFilters = SystemClock.elapsedRealtime();
            if (Utils.isKeyboardVisible)
                Utils.hideKeypad(context);
            if (transactionType != null) {
                transactionType.clear();
            }
            if (transactionSubType != null) {
                transactionSubType.clear();
            }
            if (txnStatus != null) {
                txnStatus.clear();
            }
            strFromDate = "";
            strToDate = "";
            strStartAmount = "";
            strEndAmount = "";
            isFilters = false;
            strSelectedDate = "";
            tempStrSelectedDate = "";
            storedSelectDate = "";
            transAmountStartET.setText("");
            transAmountEndET.setText("");
            getDateFromPickerET.setText("");

            transAmountStartET.clearFocus();
            transAmountEndET.clearFocus();
            getDateFromPickerET.clearFocus();

            rangeDates = new RangeDates();
            rangeDates.setUpdatedFromDate("");
            rangeDates.setUpdatedToDate("");

            transTypeSalesOrderEcomerce.setChecked(false);
            transTypeSalesOrderRetail.setChecked(false);
            transTypeRefund.setChecked(false);
            transTypeMerchantPayout.setChecked(false);
            transTypeMonthlyServiceFee.setChecked(false);
//            transTypeRefund.setChecked(false);

            transStatusCompleted.setChecked(false);
            transStatusRefund.setChecked(false);
            transStatusPartialRefund.setChecked(false);

            transAmountStartET.setText("");
            transAmountEndET.setText("");
            getDateFromPickerET.setText("");
            getOnDialogClickListener().onDialogClicked(Utils.resetFilter, null);
//            dismiss();
        });
//

        transTypeSalesOrderEcomerce.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.eComerce);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.eComerce) {
                            transactionType.remove(i);
                            break;
                        }
                    }
                }
            }
        });



        transTypeSalesOrderRetail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    transactionType.add(Utils.retailMobile);
                } else {
                    for (int i = 0; i < transactionType.size(); i++) {
                        if (transactionType.get(i) == Utils.retailMobile) {
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
                    txnStatus.add(Utils.refunded);
//                    transStatusCanceled.setChecked(false);
                } else {
//                    transStatusCanceled.setChecked(false);
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.refunded) {
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
                    txnStatus.add(Utils.partialRefund);
                } else {
                    for (int i = 0; i < txnStatus.size(); i++) {
                        if (txnStatus.get(i) == Utils.partialRefund) {
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
                    transAmountStartET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
                    USFormat(transAmountStartET, "START");

                    try {

                        if (!transAmountStartET.getText().toString().equals("") && !transAmountStartET.getText().toString().equals("")) {

                            Double startAmount = Utils.doubleParsing(transAmountStartET.getText().toString().replace(",", "").trim());
                            Double endAmount = Utils.doubleParsing(transAmountEndET.getText().toString().replace(",", "").trim());
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
                    transAmountStartET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
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
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
                    USFormat(transAmountEndET, "END");
                    try {

                        if (!transAmountEndET.getText().toString().equals("") && !transAmountEndET.getText().toString().equals("")) {

                            Double startAmount = Utils.doubleParsing(transAmountStartET.getText().toString().replace(",", "").trim());
                            Double endAmount = Utils.doubleParsing(transAmountEndET.getText().toString().replace(",", "").trim());
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
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
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
                    transAmountEndET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(13)});
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
                if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < 2000) {
                    return;
                }
                mLastClickTimeFilters = SystemClock.elapsedRealtime();
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
//                    filterTransactionListRequest.setUpdatedFromDate((objMyApplication.exportDate(strFromDate + " 00:00:00.000")).split("\\ ")[0] + " 00:00:00");
                    filterTransactionListRequest.setUpdatedFromDate(Utils.convertPreferenceZoneToUtcDateTime(strFromDate + " 00:00:00", "MM-dd-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss", objMyApplication.getStrPreference()));
                    filterTransactionListRequest.setUpdatedFromDateOperator(">=");
                }
                if (!strToDate.equals("")) {
                    isFilters = true;
//                    filterTransactionListRequest.setUpdatedToDate((objMyApplication.exportDate(strToDate + "00:00:00.000")).split("\\ ")[0] + " 23:59:59");
                    filterTransactionListRequest.setUpdatedToDate(Utils.convertPreferenceZoneToUtcDateTime(strToDate + " 23:59:59", "MM-dd-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss", objMyApplication.getStrPreference()));
                    filterTransactionListRequest.setUpdatedToDateOperator("<=");
                }

                if (!transAmountStartET.getText().toString().equals("") && !transAmountEndET.getText().toString().equals("")) {
                    Double startAmount = Utils.doubleParsing(transAmountStartET.getText().toString().replace(",", "").trim());
                    Double endAmount = Utils.doubleParsing(transAmountEndET.getText().toString().replace(",", "").trim());
                    if (endAmount < startAmount) {
                        Utils.displayAlert(context.getString(R.string.transAmountAlert), activity, "", "");
                        transAmountStartET.setText("");
                        strStartAmount = "";
                        transAmountEndET.setText("");
                        strEndAmount = "";
                    }
                }
                filterTransactionListRequest.setFilters(isFilters);
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
        dateRangePickerDialog = new DateRangePickerDialog(context, rangeDates);
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
            strAmount = Utils.convertBigDecimalUSD(etAmount.getText().toString().trim().replace(",", ""));
            etAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)));
            etAmount.setSelection(etAmount.getText().length());
            if (mode.equals("START")) {
                strStartAmount = Utils.USNumberFormat(Utils.doubleParsing(strAmount));
            } else {
                strEndAmount = Utils.USNumberFormat(Utils.doubleParsing(strAmount));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
