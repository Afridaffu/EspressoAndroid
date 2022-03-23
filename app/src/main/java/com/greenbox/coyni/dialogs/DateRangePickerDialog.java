package com.greenbox.coyni.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenbox.coyni.R;
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

    private Context context;

    public DateRangePickerDialog(Context context) {
        super(context);
    }

    public long startDateLong = 0L, endDateLong = 0L, tempStartDateLong = 0L, tempEndDateLong = 0L;
    Date startDateD = null;
    Date endDateD = null;
    public String strStartAmount = "", strEndAmount = "", strFromDate = "", strToDate = "", strSelectedDate = "", tempStrSelectedDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_dialog);

        initFields();
    }

    public void initFields() {
        ImageView closeIV = findViewById(R.id.closeIV);
        TextView doneTV = findViewById(R.id.doneTV);
        TextView rangeDateTV = findViewById(R.id.rangeDateTV);
        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        doneTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dismiss();

                    new Date(startDateLong).getYear();
                    Calendar c = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    String sDate = formatter.format(startDateLong);
                    String eDate = formatter.format(endDateLong);

                    strFromDate = sDate.split("-")[2] + "-" + Utils.changeFormat(Integer.parseInt(sDate.split("-")[1])) + "-" + Utils.changeFormat(Integer.parseInt(sDate.split("-")[0])) + "";

                    Log.e("myear", mYear + " " + mMonth + " " + mDay + " " + strFromDate);

                    if (Integer.parseInt(Utils.changeFormat(Integer.parseInt(eDate.split("-")[0]))) == mDay
                            && Integer.parseInt(Utils.changeFormat(Integer.parseInt(eDate.split("-")[1]))) == (mMonth + 1)
                            && Integer.parseInt(Utils.changeFormat(Integer.parseInt(eDate.split("-")[2]))) == mYear) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
                        String str = sdf.format(new Date());
                        strToDate = eDate.split("-")[2] + "-" + Utils.changeFormat(Integer.parseInt(eDate.split("-")[1])) + "-" + Utils.changeFormat(Integer.parseInt(eDate.split("-")[0])) + " ";
                    } else {
                        strToDate = eDate.split("-")[2] + "-" + Utils.changeFormat(Integer.parseInt(eDate.split("-")[1])) + "-" + Utils.changeFormat(Integer.parseInt(eDate.split("-")[0])) + " ";
                    }

                    Log.e("strFromDate", strFromDate);
                    Log.e("strToDate", strToDate);

                    getOnDialogClickListener().onDialogClicked("Done", strFromDate + " - " + strToDate);
                    dismiss();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });


        CalendarPicker calendarPicker = findViewById(R.id.calendar_view);
        Calendar startDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        Date backwardDate = new Date(startDate.getTime().getTime() - 31556952000L);
        Calendar endDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
//        endDate.add(Calendar.MONTH, 12); // Add 6 months ahead from current date
        calendarPicker.setRangeDate(backwardDate, endDate.getTime());
        calendarPicker.showDayOfWeekTitle(true);
        calendarPicker.setMode(CalendarPicker.SelectionMode.RANGE);
        calendarPicker.scrollToDate(endDate.getTime());


        try {
            if (!strSelectedDate.equals("")) {
                rangeDateTV.setText(strSelectedDate);
                calendarPicker.setSelectionDate(new Date(startDateLong), new Date(endDateLong));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        calendarPicker.setOnRangeSelectedListener((date, date2, s, s2) -> {

            SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy");

            try {
                startDateD = f.parse(s);
                endDateD = f.parse(s2);
                startDateLong = startDateD.getTime();
                endDateLong = endDateD.getTime();
                Log.e("startDate long", startDateLong + "  " + endDateLong);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            strSelectedDate = simpleDateFormat.format(startDateLong) + " - " + simpleDateFormat.format(endDateLong);
            rangeDateTV.setText(strSelectedDate);

            return null;
        });

        calendarPicker.setOnStartSelectedListener(new Function2<Date, String, Unit>() {
            @Override
            public Unit invoke(Date date, String s) {
                SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy");

                try {
                    startDateD = f.parse(s);
                    endDateD = f.parse(s);
                    startDateLong = startDateD.getTime();
                    endDateLong = endDateD.getTime();
                    Log.e("startDate long", startDateLong + "  " + endDateLong);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
                strSelectedDate = simpleDateFormat.format(startDateLong) + " - " + simpleDateFormat.format(endDateLong);
                rangeDateTV.setText(strSelectedDate);

                return null;
            }
        });
    }
}
