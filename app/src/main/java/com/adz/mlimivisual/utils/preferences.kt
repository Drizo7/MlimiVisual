package com.adz.mlimivisual.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object preferences {

    private const val DATA_LOGIN = "status_login"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun setDataLogin(context: Context, status: Boolean) {
        val editor = getSharedPreferences(context).edit()
        editor.putBoolean(DATA_LOGIN, status)
        editor.apply()
    }

    fun getDataLogin(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(DATA_LOGIN, false)
    }

    fun clearData(context: Context) {
        val editor = getSharedPreferences(context).edit()
        editor.remove(DATA_LOGIN)
        editor.apply()
    }
}