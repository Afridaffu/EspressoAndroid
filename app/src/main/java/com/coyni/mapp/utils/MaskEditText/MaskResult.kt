package com.coyni.mapp.utils.MaskEditText

import android.text.Editable

internal data class MaskResult(
    val selection: Int,
    val masked: String,
    val unMasked: String,
    val isDone: Boolean
)

/**
 * Applies mask result to given text without getting affected by any input filters
 */
internal fun MaskResult.apply(text: Editable) {
    // suspend filters to allow masking work for all input types
    val filters = text.filters
    text.filters = emptyArray()

    text.replace(0, text.length, masked)
//    if (text.length < 4)
//        Selection.setSelection(text, selection)
//    else
//    Selection.setSelection(text, text.length)
    println(selection)
    println(text)
    // resume filters
    text.filters = filters
}
