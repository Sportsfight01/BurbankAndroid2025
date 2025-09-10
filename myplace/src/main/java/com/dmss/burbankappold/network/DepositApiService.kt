package com.dmss.burbankappold.network

import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.google.gson.JsonObject
import models.LoginRequestBody
import models.LoginUserData
import models.contactUs.ContactUsData
import models.contactUs.ContactUsDataItem
import models.contacts.AppVersionModel
import models.contacts.MyContactsData
import models.details.UserContractDetails
import models.faq.FAQData
import models.finance.FinanceData
import models.history.MyHistoryItem
import models.photos.PhotosData
import models.profile.ProfileDetails
import models.progress.UserJobProgressItem
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface DepositApiService {

    @POST("login/getUserDetails")
    fun getUserDetails(@Body body: JsonObject) : Call<ResponseBody>

    @GET("jobsteps")
    fun getProgressDetails(): Call<List<UserJobProgressItem>>

    @GET("job")
    fun getUserContractDetails(): Call<UserContractDetails>

    @GET("documents")
    fun getPhotosOrDocuments(): Call<PhotosData>

    @Headers("Cache-Control: max-age=640000")
    @POST("login/CheckUserlogin")
    fun checkUserLogin(
        @Body body: LoginRequestBody
    ): Call<Boolean>

    @Headers("Cache-Control: max-age=640000")
    @GET("finance/GetFinance")
    fun getFinanceData(@Query("financialTicketId") ticketID: String): Call<FinanceData>

    @GET("notes")
    fun getMyHistory(): Call<List<MyHistoryItem>>

    @GET("survey/GetClientInfoForContractNumber")
    fun getMyContacts(@Query("jobNumber") jobNumber : String): Call<MyContactsData>

    @GET("myplace/GetAppVersion")
    fun getCurrentVersion(): Call<String>

    @GET("login/getLoggedinUser/")
    fun getLoggedInUser() : Call<ResponseBody>

    @POST("userProfile/getUserProfile")
    fun getProfileDetails(
        @Body jsonObject: JsonObject
    ) : Call<ProfileDetails>

    @GET("api/api/myplace/faq")
    fun getFaqDetails() : Call<FAQData>

    @GET("api/api/myplace/infocentre")
    fun getInfoCenter() : Call<FAQData>

    @GET("notes")
    fun getContactUsData(): Call<ContactUsData>

    @POST("notes")
    fun contactUsNewMessage(
        @Body jsonObject: JsonObject
    ): Call<NotesData>



    @POST("Accounts/Login")
    fun contactUsAccountsLogin(
        @Body jsonObject: JsonObject
    ): Call<String>

    @POST("Login")
    fun clickhomeViewLogin(
        @Body jsonObj: RequestBody
    ): Call<String>

        @POST("MasterContracts/Get")
    fun getPhotosOrDocuments(
        @Header("clickhomemyhomeapitoken")  content_type:String, @Body jsonObject: JsonObject
    ): Call<String>


    @POST("MasterContracts/Get")
    fun contactUsMasterContracts(
        @Header("clickhomemyhomeapitoken")  content_type:String, @Body jsonObject: JsonObject
    ): Call<String>

    @POST("MasterContracts/{jobNumber}")
    fun constructionContractsJobSteps(@Path("jobNumber") jobNumber:String,
        @Header("ClickHomeApiToken")  content_type:String, @Body jsonObject: JsonObject
    ): Call<String>

    @POST("Contracts/{contractNumber}/AddNote")
    fun AddNotes(
        @Header("clickhomemyhomeapitoken")  content_type:String,
        @Path("contractNumber") jobNumber:String,
        @Body jsonObject: JsonObject
    ): Call<NotesData>
}