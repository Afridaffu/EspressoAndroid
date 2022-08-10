package com.greenbox.coyni.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.greenbox.coyni.utils.LogUtils;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.greenbox.coyni.R;
import com.greenbox.coyni.model.RangeDates;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;
import com.greenbox.coyni.utils.verticalcalendar.CalendarPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class DateRangePickerDialog extends BaseDialog {

    public final String TAG = getClass().getName();
    private Context context;
    private Date startDateD = null;
    private Date endDateD = null;
    private String strFromDate = "", strToDate = "", strSelectedDate = "";
    private RangeDates rangeDates;
    private CalendarPicker calendarPicker;
    private ImageView closeIV;
    private TextView doneTV;
    private TextView rangeDateTV;
    public static String displayFormat = "MM-dd-yyyy";
    private SimpleDateFormat displayFormatter;


    public DateRangePickerDialog(Context context) {
        super(context);
        this.context = context;
    }

    public DateRangePickerDialog(Context context, RangeDates rangeDates) {
        super(context);
        this.context = context;
        this.rangeDates = rangeDates;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_dialog);
        try {
            int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.90);
            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, height);
            displayFormatter = new SimpleDateFormat(displayFormat);

            initFields();

            setSelectedDate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedDate() {
        calendarPicker.showDayOfWeekTitle(true);
        calendarPicker.setMode(CalendarPicker.SelectionMode.RANGE);
//        Calendar startDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
//        Date backwardDate = new Date(startDate.getTime().getTime() - 31556952000L);
        Calendar endDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
//        calendarPicker.setRangeDate(backwardDate, endDate.getTime());
        calendarPicker.scrollToDate(endDate.getTime());


        if (rangeDates != null) {
            strToDate = rangeDates.getUpdatedToDate();
            strFromDate = rangeDates.getUpdatedFromDate();
            strSelectedDate = rangeDates.getFullDate();
            try {
                startDateD = displayFormatter.parse(strFromDate);
                endDateD = displayFormatter.parse(strToDate);
                showSelectedDate();
                calendarPicker.setSelectionDate(startDateD, endDateD);
                doneTV.setTextColor(context.getResources().getColor(R.color.primary_black));
            } catch (Exception e) {
                LogUtils.e(TAG, "Date Parse exception");
            }

        }
    }

    private void initFields() {

        closeIV = findViewById(R.id.closeIV);
        doneTV = findViewById(R.id.doneTV);
        rangeDateTV = findViewById(R.id.rangeDateTV);
        calendarPicker = findViewById(R.id.calendar_view);

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        doneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                try {
                    rangeDates = new RangeDates();
                    rangeDates.setUpdatedToDate(displayFormatter.format(endDateD));
                    rangeDates.setUpdatedFromDate(displayFormatter.format(startDateD));
                    rangeDates.setFullDate(strSelectedDate);
                    getOnDialogClickListener().onDialogClicked(Utils.datePicker, rangeDates);
                    dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        calendarPicker.setOnRangeSelectedListener((date, date2, s, s2) -> {
            startDateD = date;
            endDateD = date2;
            showSelectedDate();

            return null;
        });

        calendarPicker.setOnStartSelectedListener(new Function2<Date, String, Unit>() {
            @Override
            public Unit invoke(Date date, String s) {
                SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy");
                try {
                    startDateD = f.parse(s);
                    endDateD = f.parse(s);
                    showSelectedDate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                return null;
            }
        });

    }

    private void showSelectedDate() {
        String formatToDisplay = "MMM dd, yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatToDisplay);
        strSelectedDate = simpleDateFormat.format(startDateD) + " - " + simpleDateFormat.format(endDateD);
        rangeDateTV.setText(strSelectedDate);
//        doneTV.setTextColor(context.getResources().getColor(R.color.primary_black));
    }
}