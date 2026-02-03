package com.dmss.burbankapp.ui.splash

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.api.NoConnectivityException
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.AppVersionModel
import com.dmss.burbankapp.data.model.DeviceResponseModel
import com.dmss.burbankapp.data.model.PromotionsResponse
import com.dmss.burbankapp.data.model.UserLoginModel
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import common.AppController
import common.Common
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpalashViewModel(var apiHelper: ApiHelper, var dbHelper: DatabaseHelper) : ViewModel() {

    var userLoginData = MutableLiveData<Resource<UserLoginModel>>()
    var deviceDetailsData = MutableLiveData<Resource<DeviceResponseModel>>()
    var promotionsResponseData = MutableLiveData<Resource<PromotionsResponse>>()

    fun getUserLoginData(): LiveData<Resource<UserLoginModel>> {
        return userLoginData

    }
    fun getDeviceDetailsData(): LiveData<Resource<DeviceResponseModel>> {
        return deviceDetailsData

    }
    fun getPromotionsResponseData(): LiveData<Resource<PromotionsResponse>> {
        return promotionsResponseData

    }
    public suspend fun checkVersionCodeAPi():Double{
        val result1 = withContext(Dispatchers.IO){
            checkVersionCodeRequest()
        }
        var versionCode = 0.0
        val gson = GsonBuilder().create()
        val currentVersionData= gson.fromJson(result1,Array<AppVersionModel>::class.java).toList()
        currentVersionData.forEach {
            if(it.AppType=="Android"){
                versionCode = it.AppVersion.toDouble()
            }
        }

        /*lifecycleScope.launch {
            val result = async { getAPI()}
            async { callProfileAPi()}
            onSuccessResult(result.toString())
        }*/
        return versionCode
    }

    private suspend fun checkVersionCodeRequest() = withContext(Dispatchers.IO){
        AppController.controller.webApiCall().getData_From_MyPlace(RetrofitBuilder.BASE_URL+Common.GetAppVersion)
    }
    fun fetchLoginData() {
        viewModelScope.launch {
            userLoginData.postValue(Resource.loading(null))
            try {
                val jsonObject = JsonObject()
                /*jsonObject.addProperty("Username", "mobileuser@gmail.com")
                jsonObject.addProperty("Password", "MobileUser@123")*/
                jsonObject.addProperty("Username", "burbank_minad")
                jsonObject.addProperty("Password", "401b09eab3c013d4ca54922bb802bec8fd5318192b0a75f201d8b3727429090fb337591abd3e44453b954555b7a0812e1081c39b740293f765eae731f5a65ed1")
                val loginData = apiHelper.userLogin(jsonObject)
                userLoginData.postValue(Resource.success(loginData))
            } catch (e: TimeoutCancellationException) {
                userLoginData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: NoConnectivityException) {
                userLoginData.postValue(
                    Resource.error(e.message, null)
                )
            } catch (e: Exception) {
                userLoginData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }

        }
    }
    fun getPromotionData(stateId:String){
        viewModelScope.launch {
            promotionsResponseData.postValue(Resource.loading(null))
            try{
                val loginData = apiHelper.getPromotionsetails(stateId)
                promotionsResponseData.postValue(Resource.success(loginData))
            }catch (e:Exception){
                promotionsResponseData.postValue(
                    e.message?.let { Resource.error(it, null) }
                )
            }
        }
    }
    fun sendDeviceDetailsData(deviceToke:String,deviceId:String,Latitude:String,Longitude:String,State:String) {
        viewModelScope.launch {
            deviceDetailsData.postValue(Resource.loading(null))
            try {
                val jsonObject = JsonObject()
                /*jsonObject.addProperty("Username", "mobileuser@gmail.com")
                jsonObject.addProperty("Password", "MobileUser@123")*/
                jsonObject.addProperty("DeviceToken", deviceToke)
                jsonObject.addProperty("IMEINumber", deviceId)
                jsonObject.addProperty("Latitude", Latitude)
                jsonObject.addProperty("Longitude", Longitude)
                jsonObject.addProperty("DeviceType", "Android")
                jsonObject.addProperty("State", State)

                println("jsonObject:: $jsonObject")
                val loginData = apiHelper.SaveOrUpdateDeviceDetails(jsonObject)
                deviceDetailsData.postValue(Resource.success(loginData))
            } catch (e: TimeoutCancellationException) {
                deviceDetailsData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: NoConnectivityException) {
                deviceDetailsData.postValue(
                    Resource.error(e.message, null)
                )
            } catch (e: Exception) {
                deviceDetailsData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }

        }
    }

}