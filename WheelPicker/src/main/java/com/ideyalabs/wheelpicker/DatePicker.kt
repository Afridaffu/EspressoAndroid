package com.ideyalabs.wheelpicker

import android.content.Context
import com.ideyalabs.wheelpicker.core.WheelPickerActionSheet

class DatePicker(context: Context) : WheelPickerActionSheet<DatePickerView>(context) {
    init {
        setPickerView(DatePickerView(context))
    }

}