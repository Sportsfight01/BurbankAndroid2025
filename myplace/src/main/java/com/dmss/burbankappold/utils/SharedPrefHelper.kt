package com.dmss.burbankappold.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import models.LoginUserData


object SharedPrefHelper {
    // save data in sharedPrefences
    fun setSharedOBJECT(
        context: Activity, key: String?,
        value: String?
    ) {
        /*val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            context.packageName, Context.MODE_PRIVATE
        )*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()

        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    // get data from sharedPrefences
    fun getSharedOBJECT(context: Context, key: String?): String {
//        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
//            context.getPackageName(), Context.MODE_PRIVATE
//        )
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

//        val gson = Gson()
        val json: String = sharedPreferences.getString(key, "")
//        val obj = gson.fromJson(json, Any::class.java)
        println("LoginUserData11:: "+json)
        return json
    }
}