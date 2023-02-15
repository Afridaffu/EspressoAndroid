package com.coyni.pos.app.model

import java.util.ArrayList

class Error {
    var errorCode: String? = null
    var errorDescription: String? = null
    private var fieldErrors: MutableList<String> = ArrayList()
    fun getFieldErrors(): List<String> {
        if (fieldErrors == null) {
            fieldErrors = ArrayList()
            fieldErrors.add("")
        }
        return fieldErrors
    }

    fun setFieldErrors(fieldErrors: MutableList<String>) {
        this.fieldErrors = fieldErrors
    }
}