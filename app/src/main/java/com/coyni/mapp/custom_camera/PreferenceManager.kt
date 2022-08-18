package dev.pharmacy.zai.helper

import android.content.Context
import android.content.SharedPreferences


class PreferenceManager(internal var context: Context) {
    internal var sharedPreferences: SharedPreferences
    internal var spEditor: SharedPreferences.Editor
    internal var MODE = 0

    init {
        sharedPreferences = context.getSharedPreferences(PREFERENCE, MODE)
        spEditor = sharedPreferences.edit()
    }

    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        spEditor.putBoolean(FIRST_LAUNCH, isFirstTime)
        spEditor.commit()
    }

    fun FirstLaunch(): Boolean {
        return sharedPreferences.getBoolean(FIRST_LAUNCH, true)
    }

    companion object {
        private val FIRST_LAUNCH = "firstLaunch"
        private val PREFERENCE = "Javapapers"
    }
}