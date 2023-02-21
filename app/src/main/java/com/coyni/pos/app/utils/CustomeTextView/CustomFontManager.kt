package com.coyni.pos.app.utils.CustomeTextView

import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import com.coyni.pos.app.R

object CustomFontManager {
    private const val FONT_FILE_NAME = "fonts/"
    fun applyFontFromAttrs(textView: TextView, attrs: AttributeSet?) {
        // Initialize an array containing id of attributes we want to have
        val set = intArrayOf(
            R.attr.customFont
        )
        val typedArray: TypedArray = textView.getContext().obtainStyledAttributes(attrs, set)
        val fontName: String? = typedArray.getString(0)
        typedArray.recycle()
        if (fontName != null) {
            val font: Typeface = Typeface.createFromAsset(
                textView.getContext().getAssets(),
                FONT_FILE_NAME + fontName
            )
            textView.setTypeface(font)
        }
    }
}