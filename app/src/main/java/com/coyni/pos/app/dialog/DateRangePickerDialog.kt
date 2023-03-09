package com.coyni.pos.app.dialog

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.CalendarDialogBinding
import com.coyni.pos.app.model.RangeDates
import com.coyni.pos.app.model.TransactionFilter.TransactionsSubTypeData
import com.coyni.pos.app.model.TransactionFilter.TransactionsTypeData
import com.coyni.pos.app.utils.LogUtils
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.utils.verticalcalendar.CalendarPicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateRangePickerDialog(context: Context, rangeDates: RangeDates) : BaseDialog(context) {
    val TAG = javaClass.name
    private var startDateD: Date? = null
    private var endDateD: Date? = null
    private var strFromDate = ""
    private var strToDate: String? = ""
    private var strSelectedDate: String? = ""
    private lateinit var rangeDates: RangeDates
    private lateinit var calendarPicker: CalendarPicker
    var displayFormat = "MM-dd-yyyy"
    private lateinit var displayFormatter: SimpleDateFormat
    private lateinit var mContext:Context


    private lateinit var dialogBinding: CalendarDialogBinding
    override fun getLayoutId() = R.layout.calendar_dialog

    override fun initViews() {
        dialogBinding = CalendarDialogBinding.bind(findViewById(R.id.root))

        initFields()
        setSelectedDate()
    }

    private fun setSelectedDate() {
        calendarPicker?.showDayOfWeekTitle(true)
        calendarPicker?.setMode(CalendarPicker.SelectionMode.RANGE)
        val startDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())
        val backwardDate = Date(startDate.time.time - 31556952000L)
        val endDate = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault())
        calendarPicker?.setRangeDate(backwardDate, endDate.time)
        calendarPicker?.scrollToDate(endDate.time)
        if (rangeDates != null) {
            strToDate = rangeDates?.updatedToDate
            strFromDate = rangeDates?.updatedFromDate!!
            strSelectedDate = rangeDates?.fullDate
            try {
                startDateD = displayFormatter?.parse(strFromDate)
                endDateD = displayFormatter?.parse(strToDate)
                showSelectedDate()
                calendarPicker?.setSelectionDate(startDateD!!, endDateD)
                dialogBinding.doneTV?.setTextColor(context.getResources().getColor(R.color.primary_black))
            } catch (e: Exception) {
                LogUtils.e(TAG, "Date Parse exception")
            }
        }
    }

    private fun initFields() {

        dialogBinding.closeIV?.setOnClickListener(View.OnClickListener { dismiss() })

        dialogBinding.doneTV?.setOnClickListener(View.OnClickListener {
            dismiss()
            try {
                rangeDates = RangeDates()
                rangeDates?.updatedToDate = (displayFormatter?.format(endDateD))
                rangeDates?.updatedFromDate = (displayFormatter?.format(startDateD))
                rangeDates?.fullDate = (strSelectedDate)
                getOnDialogClickListener()?.onDialogClicked(Utils.datePicker, rangeDates)
                dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

        calendarPicker?.setOnRangeSelectedListener { date, date2, s, s2 ->
            startDateD = date
            endDateD = date2
            showSelectedDate()
            null
        }

        calendarPicker?.setOnStartSelectedListener { date: Date?, s: String? ->
            val f = SimpleDateFormat("dd MMM yyyy")
            try {
                startDateD = f.parse(s)
                endDateD = f.parse(s)
                showSelectedDate()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            null
        }
    }

    private fun showSelectedDate() {
        val formatToDisplay = "MMM dd, yyyy"
        val simpleDateFormat = SimpleDateFormat(formatToDisplay)
        strSelectedDate =
            simpleDateFormat.format(startDateD) + " - " + simpleDateFormat.format(endDateD)
        dialogBinding.rangeDateTV?.setText(strSelectedDate)
//        doneTV.setTextColor(context.getResources().getColor(R.color.primary_black));
    }
}