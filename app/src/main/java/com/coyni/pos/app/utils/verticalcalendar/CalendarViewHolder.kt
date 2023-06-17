package com.coyni.pos.app.utils.verticalcalendar

import com.coyni.pos.app.R
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.coyni.pos.app.utils.MyApplication
import java.util.*

internal abstract class CalendarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun onBind(item: CalendarEntity, actionListener: (CalendarEntity, Int) -> Unit)
}

internal class MonthViewHolder(private val view: View) :
    CalendarViewHolder(view) {
    private val name = view.findViewById(R.id.vMonthName) as TextView

    override fun onBind(item: CalendarEntity, actionListener: (CalendarEntity, Int) -> Unit) {
        if (item is CalendarEntity.Month) {
            name.setText(item.label)
            Log.e("name", item.label)
        }
    }
}

internal class WeekViewHolder(private val view: View) :
    CalendarViewHolder(view) {

    private val parentContainer = view.findViewById(R.id.parent_container) as LinearLayout

    override fun onBind(item: CalendarEntity, actionListener: (CalendarEntity, Int) -> Unit) {
//        val dateFormat = DateFormatSymbols().shortWeekdays
//        (1 until dateFormat.size).forEach {
//            (parentContainer.getChildAt(it - 1) as TextView).text = dateFormat[it]
//        }
    }
}

internal class DayViewHolder(view: View) : CalendarViewHolder(view) {
    private val name = view.findViewById(R.id.vDayName) as TextView
    private val halfLeftBg = view.findViewById(R.id.vHalfLeftBg) as View
    private val halfRightBg = view.findViewById(R.id.vHalfRightBg) as View

    override fun onBind(item: CalendarEntity, actionListener: (CalendarEntity, Int) -> Unit) {
        if (item is CalendarEntity.Day) {
            name.setText(item.label)
            name.setTextColor(getFontColor(item))
            Log.e("DayViewHolder", item.label + " " + item.date.day)
            when (item.selection) {
                SelectionType.START -> {
//                    Log.e("Start", item.label)
                    name.select()
                    halfLeftBg.dehighlight()
                    if (item.isRange) halfRightBg.highlight()
                    else halfRightBg.dehighlight()

                    if (item.date.day == 6 ) {
                        halfRightBg.background = ContextCompat.getDrawable(MyApplication.context, R.drawable.calendar_range_deselected_bg)
                    }
////                    halfLeftBg.background = ContextCompat.getDrawable(MyApplication.getContext(),R.drawable.calendar_range_selected_bg)
//                    if (item.isRange) halfRightBg.background = ContextCompat.getDrawable(MyApplication.getContext(),R.drawable.calendar_range_selected_bg)
//                    else halfRightBg.background = ContextCompat.getDrawable(MyApplication.getContext(),R.drawable.calendar_range_selected_bg)


                }
                SelectionType.END -> {
//                    Log.e("End", item.label)
                    name.select()
//                    halfLeftBg.background = ContextCompat.getDrawable(
//                        MyApplication.getContext(),
//                        R.drawable.calendar_range_selected_bg
//                    )
//                    halfRightBg.background = ContextCompat.getDrawable(
//                        MyApplication.getContext(),
//                        R.drawable.calendar_range_deselected_bg
//                    )

                    halfLeftBg.highlight()
                    halfRightBg.dehighlight()
                    if (item.date.day == 0 ) {
                        halfLeftBg.background = ContextCompat.getDrawable(MyApplication.context, R.drawable.calendar_range_deselected_bg)
                    }
                }
                SelectionType.BETWEEN -> {
//                    Log.e("Between", item.label)
                    name.deselect()
                    halfRightBg.highlight()
                    halfLeftBg.highlight()
                    if (item.date.day == 0 || item.label == "1") {
                        halfLeftBg.background = ContextCompat.getDrawable(MyApplication.context, R.drawable.calendar_range_left_selected_bg)
                    }
                    if (item.date.day == 6 || isLastDayOfMonth(item.date)) {
                        halfRightBg.background = ContextCompat.getDrawable(MyApplication.context, R.drawable.calendar_range_right_selected_bg)
                    }
                }

                SelectionType.NONE -> {
//                    Log.e("None", item.label)
                    halfLeftBg.dehighlight()
                    halfRightBg.dehighlight()
                    name.deselect()
                }
            }

            if (item.state != DateState.DISABLED) {
//                Log.e("if", "if")
                itemView.setOnClickListener {
                    actionListener.invoke(
                        item,
                        adapterPosition
                    )
                }
            } else {
//                Log.e("else", "else")
                itemView.setOnClickListener(null)
            }
        }
    }

    private fun getFontColor(item: CalendarEntity.Day): Int {
        return if (item.selection == SelectionType.START || item.selection == SelectionType.END) {
            ContextCompat.getColor(itemView.context, R.color.white)
        } else {
            val color = when (item.state) {
                DateState.DISABLED -> R.color.light_gray
                DateState.WEEKEND -> R.color.primary_green
                else -> R.color.calendar_week_font
            }
            ContextCompat.getColor(itemView.context, color)
        }
    }

    private fun View.select() {
        val drawable = ContextCompat.getDrawable(context, R.drawable.selected_day_bg)
        background = drawable
    }

    private fun View.deselect() {
        background = null
    }

    private fun View.dehighlight() {
        val color = ContextCompat.getColor(context, R.color.calendar_day_unselected_bg)
        setBackgroundColor(color)
    }

    private fun View.highlight() {
        val color = ContextCompat.getColor(context, R.color.calendar_day_range_selected_bg)
        setBackgroundColor(color)
    }

    fun isLastDayOfMonth(date: Date): Boolean {
        val calendar = Calendar.getInstance()
        calendar.time = date

        val currentMonth = calendar.get(Calendar.MONTH)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val nextMonth = calendar.get(Calendar.MONTH)

        return currentMonth != nextMonth
    }
}

internal class EmptyViewHolder(view: View) : CalendarViewHolder(view) {
    override fun onBind(item: CalendarEntity, actionListener: (CalendarEntity, Int) -> Unit) {
    }
}