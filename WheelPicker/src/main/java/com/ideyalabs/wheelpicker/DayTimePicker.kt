package com.ideyalabs.wheelpicker

import android.content.Context
import com.ideyalabs.wheelpicker.core.WheelPickerActionSheet

class DayTimePicker(context: Context) : WheelPickerActionSheet<DayTimePickerView>(context) {
    init {
        setPickerView(DayTimePickerView(context))
    }
}