package com.ideyalabs.wheelpicker

import android.content.Context
import com.ideyalabs.wheelpicker.core.WheelPickerActionSheet

class WeekdayTimePicker(context: Context) : WheelPickerActionSheet<WeekdayTimePickerView>(context) {
    init {
        setPickerView(WeekdayTimePickerView(context))
    }
}