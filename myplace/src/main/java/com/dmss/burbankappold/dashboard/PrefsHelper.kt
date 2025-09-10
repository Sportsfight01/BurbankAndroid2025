package com.dmss.burbankappold.dashboard

import android.content.Context
import android.content.SharedPreferences

object PrefsHelper {

    private var prefs: SharedPreferences? = null

    private const val PREFS_NAME = "params"

    const val ID_USER = "id_user"
    const val TOKEN = "token"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getString(key: String): String? {
        return prefs?.getString(key,"")
    }

    fun getBoolean(key: String): Boolean {
        return prefs?.getBoolean(key,false)?:false
    }

    fun getInt(key: String): Int? {
        return prefs?.getInt(key,0)
    }

    fun write(key: String, value: String) {
        val prefsEditor: SharedPreferences.Editor? = prefs?.edit()
        with(prefsEditor) {
            this?.putString(key, value)
            this?.commit()
        }
    }

    fun write(key: String, value: Boolean) {
        val prefsEditor: SharedPreferences.Editor? = prefs?.edit()
        with(prefsEditor) {
            this?.putBoolean(key, value)
            this?.commit()
        }
    }

    fun write(key: String, value: Int) {
        val prefsEditor: SharedPreferences.Editor? = prefs?.edit()
        with(prefsEditor) {
            this?.putInt(key, value)
            this?.commit()
        }
    }


    private const val IS_PROFILE_API_CALLED = "profile_api_called"
    var isProfileApiCalled : Boolean get() =
        getBoolean(IS_PROFILE_API_CALLED)
    set(value) = write(IS_PROFILE_API_CALLED, value)

    private const val PROFILE_URL = "profile_url"
    var profileUrl : String get() =
        getString(PROFILE_URL)?:""
    set(value) = write(PROFILE_URL, value)

    private const val PHOTOS_COUNT = "photos_count"
    var photosCount : Int get() =
        getInt(PHOTOS_COUNT) ?: 0
        set(value) = write(PHOTOS_COUNT, value)

    private const val NOTIFICATIONS_COUNT = "notification_count"
    var notificationCount : String get() =
        getString(NOTIFICATIONS_COUNT) ?: "0"
        set(value) = write(NOTIFICATIONS_COUNT, value)

    fun clearPrefs(){
        prefs?.edit()?.clear()?.apply()
    }
}