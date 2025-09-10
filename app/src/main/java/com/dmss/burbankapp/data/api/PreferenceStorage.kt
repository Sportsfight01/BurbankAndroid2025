package com.dmss.burbankapp.data.api

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Storage for app and user preferences.
 */
interface PreferenceStorage {
    var isLoggedIn: Boolean
    var isStepsSubscribed: Boolean
    var token: String
    fun clear()
}

/**
 * [PreferenceStorage] impl backed by [android.content.SharedPreferences].
 */
@ExperimentalCoroutinesApi
@Singleton
class SharedPreferenceStorage @Inject constructor(
    @ApplicationContext context: Context
) : PreferenceStorage {

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREFS_NAME, MODE_PRIVATE
        )
    }
    override var isLoggedIn by BooleanPreference(prefs, PREF_IS_REGISTERED, false)
    override var isStepsSubscribed by BooleanPreference(prefs, PREF_IS_GOOGLE_FIT_SUBSCRIBED, false)
    override var token by StringPreference(prefs, PREF_TOKEN, "")

    override fun clear() {
        prefs.value.edit {
            clear()
            commit()
        }
    }

    companion object {
        const val PREFS_NAME = "mysaheli-prefs"
        const val PREF_IS_REGISTERED = "pref_is_registered"
        const val PREF_IS_GOOGLE_FIT_SUBSCRIBED = "pref_is_google_subscribed"
        const val PREF_TOKEN = "pref_token"
        const val LANGUAGE_PREF = "languagePreference"
        const val SYMPTOMS_IDs = "symptomids"
    }
}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit { putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return preferences.value.getString(name, defaultValue) ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit { putString(name, value) }
    }
}
