package com.dmss.burbankapp.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dmss.burbankapp.ApplicationClass
import com.dmss.burbankapp.data.model.UserInfoModel
import com.google.gson.Gson

class CustomSharedPreferences(context: Context) {
    val PREFERENCE_NAME = "myplcae_shared_preference"
    val PREFERNCE_STATE_ID = "PREFERNCE_STATE_ID"
    val USER_INFO_MODEL = "USER_INFO_MODEL"
    val USER_ID = "USER_ID"
    val USER_LOGGED_IN = "USER_LOGGED_IN"
    val SELECTED_STATE = "SELECTED_STATE"
    val SELECTED_STATE_ID = "SELECTED_STATE_ID"
    val SELECTED_REGION = "SELECTED_REGION"
    val REGION_ID = "REGION_ID"
    val PACKAGES_COUNT = "PACKAGES_COUNT"
    val STOREY_COUNT = "STOREY_COUNT"
    val BED_ROOM_COUNT = "BED_ROOM_COUNT"
    val USER_EMAIL = "USER_EMAIL"
    val LOGIN_TYPE = "LOGIN_TYPE"
    val MIN_PRICE = "MIN_PRICE"
    val MAX_PRICE = "MAX_PRICE"
    val TOKEN = "TOKEN"
    val SHARE_ACCOUNT_NOTIFICATION = "SHARE_ACCOUNT_NOTIFICATION"
    val RECENT_SEARCH_MYCOLLECTION_NOTIFICATION = "RECENT_SEARCH_MYCOLLECTION_NOTIFICATION"
    val RECENT_SEARCH_HOME_LAND_NOTIFICATION = "RECENT_SEARCH_HOME_LAND_NOTIFICATION"
    var FIRST_TIME_USER_LOGGEDIN = "FIRST_TIME_USER_LOGGEDIN"
    val HOME_DESIGN_NOTIFICATION = "HOME_DESIGN_NOTIFICATION"
    val CHOOSEOPTION = "CHOOSEOPTION"

    val SAVE_PRICE_RANGE = "SAVE_PRICE_RANGE"


    val USER_WITHOUT_LOGIN = "USER_WITHOUT_LOGIN"
    val USER_LOGIN_PASSWORD = "USER_LOGIN_PASSWORD"

    val DISPLAY_ID = "DISPLAY_ID"


    companion object {
        private var singleTonInstance: CustomSharedPreferences? = null
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor
        private const val PREF_NAME = "app_prefs"
        private const val PRIVATE_MODE = 0
        private const val KEY_AUTH_TOKEN = "auth_token"
        val instance: CustomSharedPreferences
            get() {
                if (singleTonInstance == null)
                    singleTonInstance =
                        CustomSharedPreferences(ApplicationClass.applicationContext())
                else
                    singleTonInstance

                return singleTonInstance as CustomSharedPreferences
            }
    }

    private val prefs: Lazy<SharedPreferences> = lazy { // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(
            PREF_NAME, Context.MODE_PRIVATE
        )
    }


    init {
        sharedPreferences = context.getSharedPreferences(
            PREF_NAME,
            PRIVATE_MODE
        )
        editor = sharedPreferences.edit()
        editor.apply()
    }

    fun saveLoginPassword(password: String) {
        val edit = preference.edit()
        edit.putString(USER_LOGIN_PASSWORD, password)
        edit.apply()
    }
    fun saveselectedoption(selectedoption: Int) {
        val edit = preference.edit()
        edit.putInt(CHOOSEOPTION, selectedoption)
        edit.apply()
    }
    fun getLoginPassword(): String? {
        return preference.getString(USER_LOGIN_PASSWORD, "")
    }
    fun getselectedoption(): Int? {
        return preference.getInt(CHOOSEOPTION, 0)
    }

    private val preference: SharedPreferences =
        context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)


    fun setUserLogin(isUserLogin: Boolean) {
        val edit = preference.edit()
        edit.putBoolean(USER_WITHOUT_LOGIN, isUserLogin)
        edit.apply()
    }

    fun getUserLogin(): Boolean {
        return preference.getBoolean(USER_WITHOUT_LOGIN, true)
    }

    fun setStateID(stateID: Int) {
        val edit = preference.edit()
        edit.putInt(PREFERNCE_STATE_ID, stateID)
        edit.apply()
    }

    fun getStateID(): Int {
        return preference.getInt(PREFERNCE_STATE_ID, -1)
    }

    fun saveUserInfoModel(userInfoModel: UserInfoModel) {
        val edit = preference.edit()
        val gson = Gson()
        val json: String = gson.toJson(userInfoModel)
        edit.putString(USER_INFO_MODEL, json)
        edit.apply()
    }

    fun getUserInfoModel(): UserInfoModel {
        var userInfoModel=UserInfoModel()
        val gson = Gson()
        val json: String? = preference.getString(USER_INFO_MODEL, "")
        if(json!=null && json!="") {
            userInfoModel = gson.fromJson(json, UserInfoModel::class.java)
        }
        return userInfoModel
    }

    fun saveUserId(userId: Int) {
        val edit = preference.edit()
        edit.putInt(USER_ID, userId)
        edit.apply()
    }

    fun getUserId(): Int {
        return preference.getInt(USER_ID, 0)
    }

    fun setFirstTimeAppLogin(isFirstTimeUserLoggedIn: Boolean) {
        val edit = preference.edit()
        edit.putBoolean(FIRST_TIME_USER_LOGGEDIN, isFirstTimeUserLoggedIn)
        edit.apply()
    }

    fun getIsFirstTimeLogin(): Boolean {
        return preference.getBoolean(FIRST_TIME_USER_LOGGEDIN, false)
    }

    fun setUserLoggedIn(isUserLoggedIn: Boolean) {
        val edit = preference.edit()
        edit.putBoolean(USER_LOGGED_IN, isUserLoggedIn)
        edit.apply()
    }

    fun getUserLoggedIn(): Boolean {
        return preference.getBoolean(USER_LOGGED_IN, false)
    }

    fun selectedState(state: String) {
        val edit = preference.edit()
        edit.putString(SELECTED_STATE, state)
        edit.apply()
    }

    fun getSelectedState(): String? {
        return preference.getString(SELECTED_STATE, "")
    }

    fun selectedStateID(state: String) {
        val edit = preference.edit()
        edit.putString(SELECTED_STATE_ID, state)
        edit.apply()
    }

    fun getSelectedStateID(): String? {
        return preference.getString(SELECTED_STATE_ID, "")
    }

    fun selectedRegion(region: String) {
        val edit = preference.edit()
        edit.putString(SELECTED_REGION, region)
        edit.apply()
    }

    fun getSelectedRegion(): String? {
        return preference.getString(SELECTED_REGION, "");
    }

    fun setRegionId(stateID: Int) {
        val edit = preference.edit()
        edit.putInt(REGION_ID, stateID)
        edit.apply()
    }

    fun getRegionID(): Int {
        return preference.getInt(REGION_ID, 0)
    }

    fun setPackagesCount(packagesCount: Int) {
        val edit = preference.edit()
        edit.putInt(PACKAGES_COUNT, packagesCount)
        edit.apply()
    }

    fun getPackagesCount(): Int {
        return preference.getInt(PACKAGES_COUNT, 0)
    }

    fun setStoreyCount(storeyCount: Int) {
        val edit = preference.edit()
        edit.putInt(STOREY_COUNT, storeyCount)
        edit.apply()

    }

    fun getStoreyCount(): Int {
        return preference.getInt(STOREY_COUNT, 0)
    }

    fun setBedRoomCount(storeyCount: Int) {
        val edit = preference.edit()
        edit.putInt(BED_ROOM_COUNT, storeyCount)
        edit.apply()

    }

    fun getDisplayId(): Int {
        return preference.getInt(DISPLAY_ID, 0)
    }

    fun saveDisplayId(displayId: Int) {
        val edit = preference.edit()
        edit.putInt(DISPLAY_ID, displayId)
        edit.apply()
    }

    fun getBedRoomCount(): Int {
        return preference.getInt(BED_ROOM_COUNT, 0)
    }

    fun saveEmail(emailId: String) {
        val edit = preference.edit()
        edit.putString(USER_EMAIL, emailId)
        edit.apply()
    }

    fun getEmailId(): String? {
        return preference.getString(USER_EMAIL, "")
    }

    fun setLoginType(loginType: String?) {
        val edit = preference.edit()
        edit.putString(LOGIN_TYPE, loginType)
        edit.apply()
    }

    fun getLoginType(): String? {
        return preference.getString(LOGIN_TYPE, "")
    }

    fun saveMinPrice(minPrice: String) {
        val edit = preference.edit()
        edit.putString(MIN_PRICE, minPrice)
        edit.apply()
    }

    fun saveMaxPrice(maxPrice: String) {
        val edit = preference.edit()
        edit.putString(MAX_PRICE, maxPrice)
        edit.apply()
    }

    fun getMinPrice(): String? {
        return preference.getString(MIN_PRICE, "")
    }

    fun getMaxPrice(): String? {
        return preference.getString(MAX_PRICE, "")
    }

    fun saveToken(token: String) {
        val edit = preference.edit()
        edit.putString(TOKEN, token)
        edit.apply()
    }

    fun getToken(): String? {
        return preference.getString(TOKEN, "")
    }

    fun saveShareAccountNotificationNumber(shareNotification: Int) {
        val edit = preference.edit()
        edit.putInt(SHARE_ACCOUNT_NOTIFICATION, shareNotification)
        edit.apply()
    }

    fun getShareAccountNotification(): Int? {
        return preference.getInt(SHARE_ACCOUNT_NOTIFICATION, 0)
    }

    fun saveMyCollectionNotificationNumber(myCollection: Int) {
        val edit = preference.edit()
        edit.putInt(RECENT_SEARCH_MYCOLLECTION_NOTIFICATION, myCollection)
        edit.apply()
    }


    fun getMyCollectionNotification(): Int {
        return preference.getInt(RECENT_SEARCH_MYCOLLECTION_NOTIFICATION, 0)
    }

    fun saveHomeDesignNotificationNumber(myCollection: Int) {
        val edit = preference.edit()
        edit.putInt(HOME_DESIGN_NOTIFICATION, myCollection)
        edit.apply()
    }

    fun getHomeDesignNotificationNumber(): Int {
        return preference.getInt(HOME_DESIGN_NOTIFICATION, 0)
    }

    fun saveHomeAndLandNotificationNumber(homeAndLand: Int) {
        val edit = preference.edit()
        edit.putInt(RECENT_SEARCH_HOME_LAND_NOTIFICATION, homeAndLand)
        edit.apply()
    }

    fun getHomeAndLandnNotification(): Int {
        return preference.getInt(RECENT_SEARCH_HOME_LAND_NOTIFICATION, 0)
    }


    fun clearSession() {
        preference.edit {
            clear()
            commit()
        }

    }

    fun savePriceRange(priceRange: String) {
        val edit = preference.edit()
        edit.putString(SAVE_PRICE_RANGE, priceRange)
        edit.apply()
    }

    fun getPriceRange(): String? {
        return preference.getString(SAVE_PRICE_RANGE, "")
    }

}