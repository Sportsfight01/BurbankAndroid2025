package com.dmss.burbankapp.data.api

import android.content.Context
import android.content.SharedPreferences
import common.AppController

class AppPref private constructor(context: Context) {
    fun saveAuthToken(authToken: String?) {
        editor.putString(KEY_AUTH_TOKEN, authToken)
        editor.commit()
    }

    val authToken: String?
        get() = sharedPreferences.getString(KEY_AUTH_TOKEN, null)

    fun clearData() {
        editor.clear().commit()
    }

    companion object {
        private var singleTonInstance: AppPref? = null
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private const val PREF_NAME = "app_prefs"
        private const val PRIVATE_MODE = 0
        private const val KEY_AUTH_TOKEN = "auth_token"
        val instance: AppPref?
            get() {
                if (singleTonInstance == null) {
                    singleTonInstance =
                        AppPref(AppController.getInstance().applicationContext)
                }
                return singleTonInstance
            }
    }

    init {
        sharedPreferences = context.getSharedPreferences(
            PREF_NAME,
            PRIVATE_MODE
        )
        editor = sharedPreferences.edit()
        editor.apply()
    }


}