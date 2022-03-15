package com.greenbox.coyni.utils.MaskEditText.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.greenbox.coyni.R
import com.greenbox.coyni.utils.MaskEditText.Mask
import com.greenbox.coyni.utils.MaskEditText.MaskChangedListener
import com.greenbox.coyni.utils.MaskEditText.MaskStyle
import com.greenbox.coyni.utils.MaskEditText.mostOccurred


@SuppressLint("Recycle")
class MaskEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var maskChangedListener: MaskChangedListener? = null

    val masked: String
        get() = maskChangedListener?.masked.orEmpty()

    val unMasked: String
        get() = maskChangedListener?.unMasked.orEmpty()

    val isDone: Boolean
        get() = maskChangedListener?.isDone ?: false

    companion object {
        val isOther = true;
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.MaskEditText).apply {
            val style = getInteger(R.styleable.MaskEditText_maskStyle, 0)
            val value = getString(R.styleable.MaskEditText_mask).orEmpty()

            val character = getString(R.styleable.MaskEditText_maskCharacter).orEmpty()

            if (value.isNotEmpty()) {
                val maskChar = if (character.isEmpty()) value.mostOccurred() else character.single()
                val mask = Mask(value, maskChar, MaskStyle.valueOf(style))
                maskChangedListener = MaskChangedListener(mask)
            }

            recycle()
        }
    }

    /**
     * Let only one [maskChangedListener] allowed at a time
     */
    override fun addTextChangedListener(watcher: TextWatcher?) {
        if (watcher is MaskChangedListener) {
            removeTextChangedListener(maskChangedListener)
            maskChangedListener = watcher
        }
        super.addTextChangedListener(watcher)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        addTextChangedListener(maskChangedListener)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeTextChangedListener(maskChangedListener)
    }
}
