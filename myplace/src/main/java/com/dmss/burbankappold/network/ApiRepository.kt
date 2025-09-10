package com.dmss.burbankappold.network

import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import common.AppController
import common.Common
import models.LoginRequestBody
import models.LoginUserData
import models.MyPlaceCredentials
import models.contactUs.ContactUsData
import models.contactUs.ContactUsDataItem
import models.contacts.AppVersionModel
import models.contacts.MyContactsData
import models.details.UserContractDetails
import models.faq.FAQData
import models.finance.FinanceData
import models.history.MyHistoryItem
import models.photos.PhotosData
import models.profile.MyPlaceDetail
import models.profile.ProfileDetails
import models.profile.Result
import models.profile.UserJobProfile
import models.progress.UserJobProgressItem
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiRepository {

    fun requestProgressSteps(callBack: (Boolean, List<UserJobProgressItem>?) -> Unit){
        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrl).getProgressDetails().enqueue(object:
            Callback<List<UserJobProgressItem>> {
            override fun onResponse(
                call: Call<List<UserJobProgressItem>>,
                response: Response<List<UserJobProgressItem>>
            ) {
                if (response.isSuccessful) {
                    callBack.invoke(true,response.body())
                }else if (response.code() == 401){
                    requestUserLoginDetails(jsonBody) { isSuccess, response ->
                        if (isSuccess) {
                            parseAPIResponse(response) {
                                PrefsHelper.isProfileApiCalled = true
                                if (it){
                                    requestProgressSteps(callBack)
                                }
                            }
                        }
                    }
                }
                else{
                    callBack.invoke(false,null)
                }
            }

            override fun onFailure(call: Call<List<UserJobProgressItem>>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }

    fun requestDetailsApi(callBack: (Boolean,UserContractDetails?) -> Unit){
        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrl).getUserContractDetails().enqueue(object:
            Callback<UserContractDetails> {
            override fun onResponse(
                call: Call<UserContractDetails>,
                response: Response<UserContractDetails>
            ) {
                println("requestDetailsApi:: "+response.raw().request.url)
                if (response.isSuccessful) {
                    callBack.invoke(true,response.body())
                }else{
                    callBack.invoke(false,null)
                }
            }

            override fun onFailure(call: Call<UserContractDetails>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }

    fun requestPhotosApi(callBack: (Boolean,PhotosData?) -> Unit){
        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrl).getPhotosOrDocuments().enqueue(object:
            Callback<PhotosData> {
            override fun onResponse(
                call: Call<PhotosData>,
                response: Response<PhotosData>
            ) {
                if (response.isSuccessful) {
                    callBack.invoke(true,response.body())
                }else{
                    callBack.invoke(false,null)
                }
            }

            override fun onFailure(call: Call<PhotosData>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }
    fun requestPhotosApi(cookie:String,jsonBody: JsonObject,callBack: (Boolean, String?) -> Unit){
        println("requestPhotosApi request JSON:: "+cookie+" jsonBody:: "+jsonBody)
        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrlLive).getPhotosOrDocuments(cookie,jsonBody).enqueue(object:
            Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                println("requestPhotosApi:: "+Gson().toJson(response.body()))
                if (response.isSuccessful) {
                    callBack.invoke(true,response.body())
                }else{
                    callBack.invoke(false,null)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }

    fun checkUserLogin(body : LoginRequestBody, callBack: (Boolean) -> Unit){
        BaseRetrofit.depositApiService(Common.myPlaceBaseUrlQldOrSa).checkUserLogin(body).enqueue(object:
            Callback<Boolean> {
            override fun onResponse(
                call: Call<Boolean>,
                response: Response<Boolean>
            ) {
                callBack.invoke(response.body()?:false)
            }
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                callBack.invoke(false)
            }

        })
    }

    fun requestFinanceApi(ticketID: String, callBack: (Boolean,FinanceData?) -> Unit){
        BaseRetrofit.depositApiService(Common.myPlaceBaseUrlQldOrSa).getFinanceData(ticketID).enqueue(object:
            Callback<FinanceData> {
            override fun onResponse(
                call: Call<FinanceData>,
                response: Response<FinanceData>
            ) {
                if (response.isSuccessful) {
                    callBack.invoke(true,response.body())
                }else{
                    callBack.invoke(false,null)
                }
            }

            override fun onFailure(call: Call<FinanceData>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }

    fun requestUserLoginDetails(jsonBody: JsonObject, callBack: (Boolean,String) -> Unit){
        BaseRetrofit.depositApiService(Common.BaseUrl).getUserDetails(jsonBody).enqueue(object :
            Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful){
                    response.body()?.let {
                        callBack.invoke(true,it.string())
                    }
                }else callBack.invoke(false,"")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callBack.invoke(false, "")
            }

        })
    }

    fun requestUserHistoryDetails(loginRequestBody: LoginRequestBody, callBack: (Boolean,List<MyHistoryItem>) -> Unit){
        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrl).getMyHistory().enqueue(object :
            Callback<List<MyHistoryItem>>{
            override fun onResponse(call: Call<List<MyHistoryItem>>, response: Response<List<MyHistoryItem>>) {
                if (response.isSuccessful){
                    response.body()?.let {
                        callBack.invoke(true,it)
                    }
                }else callBack.invoke(false, listOf())
            }

            override fun onFailure(call: Call<List<MyHistoryItem>>, t: Throwable) {
                callBack.invoke(false, listOf())
            }

        })
    }

    private val jsonBody: JsonObject
        get() {
            val jsonObject = JsonObject()
            try {
                jsonObject.addProperty("Email", AppController.controller.userProfile.userDetails[0].email)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonObject
        }

    fun requestContactDetails(jobNumber: String, callBack: (Boolean,MyContactsData?) -> Unit){
        BaseRetrofit.depositApiService(Common.myPlaceBaseUrlVic).getMyContacts(jobNumber).enqueue(object :
            Callback<MyContactsData>{
            override fun onResponse(call: Call<MyContactsData>, response: Response<MyContactsData>) {
                if (response.isSuccessful && response.body() != null){
                    response.body()?.let {
                        callBack.invoke(true,it)
                    }
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<MyContactsData>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }

    fun requestProfileDetails(jsonBody: JsonObject, callBack: (Boolean, Result?) -> Unit){
        BaseRetrofit.depositApiService(Common.BaseUrl).getProfileDetails(jsonBody).enqueue(object :
            Callback<ProfileDetails>{
            override fun onResponse(call: Call<ProfileDetails>, response: Response<ProfileDetails>) {
                if (response.isSuccessful && response.body() != null){
                    response.body()?.let {
                        callBack.invoke(true, it.Result)
                    }
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<ProfileDetails>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }

    fun requestFaqDetails(callBack: (Boolean, FAQData?) -> Unit){
        BaseRetrofit.depositApiService(Common.LOCAL_BASE_URL).getFaqDetails().enqueue(object :
            Callback<FAQData>{
            override fun onResponse(call: Call<FAQData>, response: Response<FAQData>) {
                if (response.isSuccessful && response.body() != null){
                    response.body()?.let {
                        callBack.invoke(true, it)
                    }
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<FAQData>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }

    fun requestInfoCenterDetails(callBack: (Boolean, FAQData?) -> Unit){

        println()
        BaseRetrofit.depositApiService(Common.LOCAL_BASE_URL).getInfoCenter().enqueue(object :
            Callback<FAQData>{
            override fun onResponse(call: Call<FAQData>, response: Response<FAQData>) {
                if (response.isSuccessful && response.body() != null){
                    response.body()?.let {
                        callBack.invoke(true, it)
                    }
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<FAQData>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }

    fun requestContactUsDetails(callBack: (Boolean, MutableList<ContactUsDataItem>?) -> Unit){
        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrl).getContactUsData().enqueue(object :
            Callback<ContactUsData>{
            override fun onResponse(call: Call<ContactUsData>, response: Response<ContactUsData>) {
                if (response.isSuccessful && response.body() != null){
                    response.body()?.let {
                        callBack.invoke(true, it)
                    }
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<ContactUsData>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }
    fun requestContactUsLogin(jsonBody: JsonObject,callBack: (Boolean, String?, String?) -> Unit){
        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrlLive).contactUsAccountsLogin(jsonBody).enqueue(object :
            Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null){
                    var headers= response.headers()
                    var cookie = response.headers()["clickhomemyhomeapitoken"];

                    response.body()?.let {
                        println("requestContactUsLogin:: "+it)
                        callBack.invoke(true, cookie,it)
                    }
                }else callBack.invoke(false, null,null)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.invoke(false, null,null)
            }

        })
    }
    fun requestClickhomeViewLogin(jsonBody: RequestBody, callBack: (Boolean, String?, String?) -> Unit){
        println("requestClickhomeViewLogin: $jsonBody")
        BaseRetrofit.depositApiService(Common.ClickHomeV2BaseUrlLive).clickhomeViewLogin(jsonBody).enqueue(object :
            Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null){
                    var headers= response.headers()
                    var cookie = response.headers()["ClickHomeApiToken"];
                    println("cookie:: $cookie")

                    response.body()?.let {
                        println("requestContactUsLogin:: "+it)
                        callBack.invoke(true, cookie,it)
                    }
                }else callBack.invoke(false, null,null)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.invoke(false, null,null)
            }

        })
    }
    fun requestContactMasterContracts(cookie:String,jsonBody: JsonObject,callBack: (Boolean, String?) -> Unit){
        println("requestContactMasterContracts request JSON:: "+cookie+" jsonBody:: "+jsonBody)

        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrlLive).contactUsMasterContracts(cookie,jsonBody).enqueue(object :
            Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null){
                    response.body()?.let {
                        callBack.invoke(true, it)
                    }
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }
    fun requestUpdateVersionCode(callBack: (Boolean, String?) -> Unit){

        BaseRetrofit.depositApiService(Common.BASEURL_api_v2).getCurrentVersion().enqueue(object :
            Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null){
                    response.body()?.let {
                        var versionCode = "0.0"
                        val gson = GsonBuilder().create()
                        val currentVersionData= gson.fromJson(it,Array<AppVersionModel>::class.java).toList()
                        currentVersionData.forEach {
                            if(it.AppType=="Android"){
                                versionCode = it.AppVersion
                            }
                        }
                        callBack.invoke(true, versionCode)
                    }
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }
    fun requestconstructionContractsJobSteps(jobNumber:String,cookie:String,jsonBody: JsonObject,callBack: (Boolean, String?) -> Unit){
        BaseRetrofit.depositApiService(Common.ClickHomeV2BaseUrlLive).constructionContractsJobSteps(jobNumber,cookie,jsonBody).enqueue(object :
            Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful && response.body() != null){
                    response.body()?.let {
                        callBack.invoke(true, it)
                    }
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                callBack.invoke(false, null)
            }

        })
    }
    fun requestNewMessage(cookie:String,contractNumber:String,jsonBody: JsonObject, callBack: (Boolean, NotesData?) -> Unit){
        println("requestNewMessage:: $jsonBody")
//        BaseRetrofit.depositApiService(Common.ClickHomeBaseUrl).contactUsNewMessage(jsonBody).enqueue(object :
        BaseRetrofit.depositApiService(Common.ClickHomeBaseV3UrlLive).AddNotes(cookie,contractNumber,jsonBody).enqueue(object :

        Callback<NotesData>{
            override fun onResponse(call: Call<NotesData>, response: Response<NotesData>) {
                if (response.isSuccessful){
                    callBack.invoke(true, response.body())
                }else callBack.invoke(false, null)
            }

            override fun onFailure(call: Call<NotesData>, t: Throwable) {
                callBack.invoke(false,null)
            }

        })
    }

    private fun parseAPIResponse(result: String?, callback: (Boolean) -> Unit) {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.getBoolean(Common.Status_Key)) {
                val job = jsonObject.getJSONObject(Common.Result_Key)
                if (job.getBoolean(Common.Sucess_Key)) {
                    AppController.controller?.setProfileInfo(job.toString())
                    val gson = GsonBuilder().create()
                    val profile: UserJobProfile =
                        gson.fromJson(job.toString(), UserJobProfile::class.java)
                    AppController.controller.setUserJobProfile(profile)
                    AppController.controller.setUserJobDetail(profile.UserDetails)
                    AppController.controller.myPlaceDetail = profile.UserDetails[0].MyPlaceDetails
                    var myPlaceDetails: MyPlaceDetail? = null
                    println("  AppController.controller.jobNumber:: "+AppController.controller.jobNumber)
                    if(AppController.controller.jobNumber==null || AppController.controller.jobNumber=="null" || AppController.controller.jobNumber==""){
                        AppController.controller.jobNumber=profile.UserDetails[0].MyPlaceDetails[0].JobNo
                    }
                    for (item in profile.UserDetails[0].MyPlaceDetails){
                        if(item.JobNo==AppController.controller.jobNumber)
                            myPlaceDetails=item
                    }
                    val myPlaceCredentials = MyPlaceCredentials(
                        myPlaceDetails!!.Region,
                        myPlaceDetails!!.JobNo,
                        myPlaceDetails!!.UserName,
                        myPlaceDetails!!.Password
                    )
                    AppController.controller.my_Place_Details = myPlaceCredentials
                    println("User Profile Details $result")
                    callback.invoke(true)
                } else {
                    callback.invoke(false)
                }
            } else {
                callback.invoke(false)
            }
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
            callback.invoke(false)
        }
    }
}