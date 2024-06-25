package com.huseyin.chatappp.util

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class CustomSharedPreferences {
    companion object {
        private const val PREFERENCES_TIME = ""
        private var sharedPreferences: SharedPreferences? = null
        @Volatile private var instance: CustomSharedPreferences? = null

        private val lock = Any()

        operator fun invoke(context: Context): CustomSharedPreferences = instance ?: synchronized(lock) {
            instance ?: makeCustomSharedPreferences(context).also {
                instance = it
            }
        }

        private fun makeCustomSharedPreferences(context: Context): CustomSharedPreferences {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            return CustomSharedPreferences()
        }
    }

    fun getTime() = sharedPreferences?.getLong(PREFERENCES_TIME, 0)

    fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences?.getString(key, defaultValue)
    }

    fun saveString(key: String, value: String) {
        sharedPreferences?.edit()?.putString(key, value)?.apply()
    }

    fun remove(key: String) {
        sharedPreferences?.edit()?.remove(key)?.apply()
    }
}