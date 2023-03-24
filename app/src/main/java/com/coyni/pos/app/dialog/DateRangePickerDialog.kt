package com.coyni.pos.app.dialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.model.RangeDates
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.utils.verticalcalendar.CalendarPicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateRangePickerDialog : BaseDialog {
    val TAG = javaClass.name
    private var context: Context
    private var startDateD: Date? = null
    private var endDateD: Date? = null
    private var strFromDate: String? = ""
    private var strToDate: String? = ""
    private var strSelectedDate: String? = ""
    private var rangeDates: RangeDates? = null
    private var calendarPicker: CalendarPicker? = null
    private var closeIV: ImageView? = null
    private var doneTV: TextView? = null
    private var rangeDateTV: TextView? = null
    private var displayFormatter: SimpleDateFormat? = null

    constructor(context: Context) : super(context) {
        this.context = context
    }

    constructor(context: Context, rangeDates: RangeDates?) : super(context) {
        this.context = context
        this.rangeDates = rangeDates
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calendar_dialog)
        try {
            val height = (context.resources.displayMetrics.heightPixels * 0.90).toInt()
            window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, height)
            displayFormatter = SimpleDateFormat(displayFormat)
            initFields()
            setSelectedDate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setSelectedDate() {
        calendarPicker!!.showDayOfWeekTitle(true)
        calendarPicker!!.setMode(CalendarPicker.SelectionMode.RANGE)
//        val startDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
//        val backwardDate = Date(startDate.getTime().getTime() - 31556952000L);
        val endDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())
//        calendarPicker!!.setRangeDate(backwardDate, endDate.getTime());
        calendarPicker!!.scrollToDate(endDate.time)
        if (rangeDates != null) {
            strToDate = rangeDates!!.updatedToDate
            strFromDate = rangeDates!!.updatedFromDate
            strSelectedDate = rangeDates!!.fullDate
            try {
                startDateD = displayFormatter!!.parse(strFromDate)
                endDateD = displayFormatter!!.parse(strToDate)
                showSelectedDate()
                calendarPicker!!.setSelectionDate(startDateD!!, endDateD)
                doneTV!!.setTextColor(context.resources.getColor(R.color.primary_black))
            } catch (e: Exception) {
                Log.e(TAG, "Date Parse exception")
            }
        }
    }

    private fun initFields() {
        closeIV = findViewById(R.id.closeIV)
        doneTV = findViewById(R.id.doneTV)
        rangeDateTV = findViewById(R.id.rangeDateTV)
        calendarPicker = findViewById(R.id.calendar_view)
        closeIV!!.setOnClickListener(View.OnClickListener { dismiss() })
        doneTV!!.setOnClickListener(View.OnClickListener {
            dismiss()
            try {
                rangeDates = RangeDates()
                rangeDates!!.updatedToDate = displayFormatter!!.format(endDateD)
                rangeDates!!.updatedFromDate = displayFormatter!!.format(startDateD)
                rangeDates!!.fullDate = strSelectedDate
                getOnDialogClickListener()!!.onDialogClicked(Utils.datePicker, rangeDates)
                dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        calendarPicker!!.setOnRangeSelectedListener { date: Date?, date2: Date?, s: String?, s2: String? ->
            startDateD = date
            endDateD = date2
            showSelectedDate()
        }
        calendarPicker!!.setOnStartSelectedListener { date: Date?, s: String? ->
            val f = SimpleDateFormat("dd MMM yyyy")
            try {
                startDateD = f.parse(s)
                endDateD = f.parse(s)
                showSelectedDate()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    private fun showSelectedDate() {
        val formatToDisplay = "MMM dd, yyyy"
        val simpleDateFormat = SimpleDateFormat(formatToDisplay)
        strSelectedDate =
            simpleDateFormat.format(startDateD) + " - " + simpleDateFormat.format(endDateD)
        rangeDateTV!!.text = strSelectedDate
        //        doneTV.setTextColor(context.getResources().getColor(R.color.primary_black));
    }

    override fun getLayoutId(): Int {
        return R.layout.calendar_dialog
    }

    override fun initViews() {}

    companion object {
        var displayFormat = "MM-dd-yyyy"
    }
}