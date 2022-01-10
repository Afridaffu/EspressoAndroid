package com.greenbox.coyni.utils.verticalcalendar

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ListAdapter
import com.greenbox.coyni.R


internal class CalendarAdapter() :
    ListAdapter<CalendarEntity, CalendarViewHolder>(CalendarDiffCallback()), Parcelable {

    var onActionListener: (CalendarEntity, Int) -> Unit = { _, _ -> }

    constructor(parcel: Parcel) : this() {

    }

    override fun submitList(list: MutableList<CalendarEntity>?) {
        super.submitList(list?.toMutableList())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return when (viewType) {
            CalendarType.MONTH.ordinal -> MonthViewHolder(
                parent.inflate(R.layout.calendar_month_view)
            )
            CalendarType.WEEK.ordinal -> WeekViewHolder(parent.inflate(R.layout.calendar_week_view))
            CalendarType.DAY.ordinal -> DayViewHolder(parent.inflate(R.layout.calendar_day_view))
            else -> EmptyViewHolder(parent.inflate(R.layout.calendar_empty_view))
        }
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.onBind(getItem(position), onActionListener)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).calendarType
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CalendarAdapter> {
        override fun createFromParcel(parcel: Parcel): CalendarAdapter {
            return CalendarAdapter(parcel)
        }

        override fun newArray(size: Int): Array<CalendarAdapter?> {
            return arrayOfNulls(size)
        }
    }
}

fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachedToRoot: Boolean = false): View =
    from(context).inflate(layoutId, this, attachedToRoot)