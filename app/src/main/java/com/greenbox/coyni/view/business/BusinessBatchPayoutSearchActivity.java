package com.greenbox.coyni.view.business;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.greenbox.coyni.R;
import com.greenbox.coyni.databinding.PayoutTransactionsFilterBinding;
import com.greenbox.coyni.dialogs.MerchantTransactionsFilterDialog;
import com.greenbox.coyni.dialogs.PayoutTransactionsDetailsFiltersDialog;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.view.BusinessUserDetailsPreviewActivity;
import com.greenbox.coyni.view.TransactionListActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class BusinessBatchPayoutSearchActivity extends AppCompatActivity {

    ImageView filterIconIV, datePickIV;
    TextView applyFilterBtnCV;
    EditText filterdatePickET;
    LinearLayout dateRangePickerLL;
    public String strStartAmount = "", strEndAmount = "", strFromDate = "", strToDate = "", strSelectedDate = "", tempStrSelectedDate = "";
    Date startDateD = null;
    Date endDateD = null;
    public long startDateLong = 0L, endDateLong = 0L, tempStartDateLong = 0L, tempEndDateLong = 0L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_batch_payout_search);
        initFields();
    }

    private void initFields() {
        filterIconIV = findViewById(R.id.filterIconIV);
        applyFilterBtnCV = findViewById(R.id.applyFilterBtnCV);
        filterdatePickET = findViewById(R.id.filterdatePickET);
        dateRangePickerLL = findViewById(R.id.dateRangePickerLL);
        datePickIV = findViewById(R.id.datePickIV);


        filterIconIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFiltersPopup();
            }
        });
    }

    public void showFiltersPopup() {
        PayoutTransactionsDetailsFiltersDialog dialog = new PayoutTransactionsDetailsFiltersDialog(BusinessBatchPayoutSearchActivity.this);
        dialog.show();
    }

}