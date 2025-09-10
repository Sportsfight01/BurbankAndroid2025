package com.dmss.burbankappold.dashboard.ui.details

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyNotificationActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.dmss.burbankappold.databinding.FragmentMyDetailsBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.dmss.burbankappold.utils.SharedPrefHelper.getSharedOBJECT
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import common.AppController
import common.Common
import common.TransparentProgressDialog
import common.Utils
import models.LoginUserData
import models.details.UserContractDetails
import models.profile.UserJobProfile
import org.json.JSONException
import org.json.JSONObject


class DetailsFragment : AppCompatActivity() {

    private lateinit var binding: FragmentMyDetailsBinding
//    private val binding get() = _binding!!
    var pd: TransparentProgressDialog? = null
    private var userContractDetails: UserContractDetails? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMyDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.profileHeader.tvHeading.text=getString(R.string.mydetails)
        binding.toolbar.ivToolBarBack.visibility=View.VISIBLE
        binding.toolbar.line.visibility=View.VISIBLE
        binding.toolbar.ivNavMenu.visibility=View.GONE
        binding.toolbar.ivChat.visibility=View.GONE
        binding.toolbar.ivToolBarBack.setOnClickListener { finish() }
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        updateUI()
        if (userContractDetails == null){
//            requestApiCall()
            callfacadeDetails()
        }else{
//            updateUI()
        }
        binding.swiperefresh.setOnRefreshListener {
            if( userContractDetails==null) {
                Utils.hideSwipeRefresh(binding.swiperefresh)
//                requestApiCall()
//                callfacadeDetails()

            }else{
                Utils.hideSwipeRefresh(binding.swiperefresh)
            }

        }
    }
   /* override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileHeader.tvHeading.text = getString(R.string.my_details)
        binding.profileHeader.tvHeading.changeTextColor(context)
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        if (userContractDetails == null){
            requestApiCall()
        }else{
            updateUI()
        }
    }*/
//   @RequiresApi(Build.VERSION_CODES.N)
   private fun updateUI(){
       val jsonString: String = getSharedOBJECT(this, getString(R.string.login_user_data))
       val loginUserData = Gson().fromJson(jsonString, LoginUserData::class.java)
       var mobileNumber = loginUserData.contactDetails?.mobilePhone
       var email =  loginUserData.contactDetails?.emailAddress
       if(mobileNumber == null || mobileNumber == "null" || mobileNumber==""){
           mobileNumber = ""
       }
       if(email == "null" || email==null){
           email = ""
       }
       val userDetails = AppController.controller.userJobDetail
       binding.profileHeader.tvSubHeading.text = loginUserData.clientTitle
       println("mobilePhone::"+loginUserData.contactDetails?.mobilePhone+" emailAddress::  "+loginUserData.contactDetails?.emailAddress)

       binding.profileHeader.tvSubHeading.changeTextColor(this)
       binding.profileHeader.tvPhone.text = Html.fromHtml(getString(
           R.string.phone_s,
           "<font color='#ffffff'>${mobileNumber}</font>"
       ),HtmlCompat.FROM_HTML_MODE_LEGACY)
       binding.profileHeader.tvEmail.text = Html.fromHtml(getString(
           R.string.email_s,
           "<font color='#ffffff'>${email}</font>"
       ), HtmlCompat.FROM_HTML_MODE_LEGACY)
       binding.profileHeader.llUserDetails.show()
       binding.tvJobNumber.text = userContractDetails?.job

       binding.tvJobNumber.text = userContractDetails?.job
//       val result: String = loginUserData.homeAddress?.address.toString().replace("\n", "")
//       binding.tvJobAddress.text = result?:"--"

       binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
           View.GONE else View.VISIBLE
//       binding.profileHeader.tvNotificationsCount.text = PrefsHelper.notificationCount
       if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
           var notificationCount = PrefsHelper.notificationCount
           if (notificationCount!="" && notificationCount.toInt() > 100) {
               notificationCount = "99+"
           }
           binding.profileHeader.tvNotificationsCount.text =  notificationCount
       }
       binding.toolbar.ivChat.show()
       binding.toolbar.ivChat.setOnClickListener {
           startActivity(Intent(this, ContactUsActivity::class.java))
       }
       binding.profileHeader.ivPhoto.setOnClickListener {
           startActivity(Intent(this, MyNotificationActivity::class.java))
       }
   }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateUI1(){
       val jsonString: String = getSharedOBJECT(this, getString(R.string.login_user_data))
       val loginUserData = Gson().fromJson(jsonString, LoginUserData::class.java)
       var mobileNumber = loginUserData.contactDetails?.mobilePhone
       var email =  loginUserData.contactDetails?.emailAddress
        if(mobileNumber == null || mobileNumber == "null" || mobileNumber==""){
            mobileNumber = loginUserData.contactDetails?.homePhone
        }
        if(email == "null" || email==""){
            email = ""
        }
       val userDetails = AppController.controller.userJobDetail
        if (userDetails != null && userDetails.isNotEmpty()) {
            userDetails[0]?.let {
//                binding.profileHeader.tvSubHeading.text = AppController.controller.userProfile.userDetailses[0].fullName
                binding.profileHeader.tvSubHeading.text = loginUserData.clientTitle

                binding.profileHeader.tvSubHeading.changeTextColor(this)
                binding.profileHeader.tvPhone.text = Html.fromHtml(getString(
                    R.string.phone_s,
                    "<font color='#ffffff'>${mobileNumber}</font>"
                ),HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding.profileHeader.tvEmail.text = Html.fromHtml(getString(
                    R.string.email_s,
                    "<font color='#ffffff'>${email}</font>"
                ), HtmlCompat.FROM_HTML_MODE_LEGACY)
              /*  binding.profileHeader.tvPhone.text = Html.fromHtml(getString(
                    R.string.phone_s,
                    "<font color='#ffffff'>${it.Mobile}</font>"
                ),HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding.profileHeader.tvEmail.text = Html.fromHtml(getString(
                    R.string.email_s,
                    "<font color='#ffffff'>${it.Email}</font>"
                ), HtmlCompat.FROM_HTML_MODE_LEGACY)*/
            }
        }
        binding.profileHeader.llUserDetails.show()
        binding.tvJobNumber.text = userContractDetails?.job
//        val result: String = loginUserData.homeAddress?.address.toString().replace("\n", "")
//        binding.tvJobAddress.text = result?:"--"
      /*  binding.tvHomeDesign.text = userContractDetails?.housetype?:"--"
        val contractValue = userContractDetails?.contractvalue?:"0"
        binding.tvContactValue.text = contractValue.convertUsCurrency()
        binding.tvSupervisor.text = userContractDetails?.supervisor?:"--"
        binding.tvHomeConsultant.text = userContractDetails?.clientliaison?:"--"
        binding.tvFacade.text = userContractDetails?.facade?:"--"*/

        binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
        binding.profileHeader.tvNotificationsCount.text = PrefsHelper.notificationCount
       binding.toolbar.ivChat.show()
       binding.toolbar.ivChat.setOnClickListener {
           startActivity(Intent(this, ContactUsActivity::class.java))
       }
        binding.profileHeader.ivPhoto.setOnClickListener {
            startActivity(Intent(this, MyNotificationActivity::class.java))
        }
    }
    private val jsonBody: JsonObject
        get() {
            val jsonObject = JsonObject()
            try {
                jsonObject.addProperty("Email",AppController.controller!!.userProfile.userDetails[0].email)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jsonObject
        }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun requestApiCall(){
        if(Utils.isNetworkAvailable(this)) {
            pd = Utils.getProgress(this)
          /*  ApiRepository.requestUserLoginDetails(jsonBody) { isSuccess, response ->
                if (isSuccess) {
                    parseAPIResponse(response) {
                        ApiRepository.requestDetailsApi { isSuccess, userContractDetails ->
                            pd?.dismiss()
                            if (isSuccess) {
                                this.userContractDetails = userContractDetails
                            } else this.showErrorToast()
                            updateUI()
                        }
                    }
                }
            }*/

        }

    }
    fun callfacadeDetails(){
        if(Utils.isNetworkAvailable(this)) {
            pd = Utils.getProgress(this)
            val jsonParser = JsonParser()
            val jsonString =AppConstants.homeDesignString+","+AppConstants.jsonFacadeString
            val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject

                ApiRepository.requestContactMasterContracts(AppConstants.AppCookieContactUs,jsonObjectObj) { isSuccess, contactData ->
                    pd?.dismiss()
                    if (isSuccess) {
                        var packageName ="";
                        val resjsonObjectObj = jsonParser.parse(contactData).asJsonObject
                        println("callfacadeDetails:: $resjsonObjectObj")
                        if(resjsonObjectObj.has("facade")) {
                            val facadeObj = resjsonObjectObj.getAsJsonObject("facade")
                            if (facadeObj.has("packageName")) {
                                packageName = facadeObj.getAsJsonPrimitive("packageName").asString
                            }
                        }
                        val contractNumber = resjsonObjectObj.getAsJsonPrimitive("contractNumber").asString

                        val lotAddressObj = resjsonObjectObj.getAsJsonObject("lotAddress")
                        val address = lotAddressObj.getAsJsonPrimitive("address").asString

                        val houseTypeObj = resjsonObjectObj.getAsJsonObject("houseType")
                        val houseName = houseTypeObj.getAsJsonPrimitive("houseName").asString

                        binding.tvFacade.text = packageName ?: "--"
                        binding.tvHomeDesign.text = houseName ?: "--"
                        binding.tvJobNumber.text = contractNumber ?: "--"
                        val result: String = address.toString().replace("\n", "")
                        binding.tvJobAddress.text = result?:"--"
                    } else this.showErrorToast()

                }

        }
    }
    private fun parseAPIResponse(result: String?, callback: () -> Unit) {
        try {
            val jsonObject = JSONObject(result)
//            println("jsonBody:: $jsonObject")

            if (jsonObject.getBoolean(Common.Status_Key)) {
                val job = jsonObject.getJSONObject(Common.Result_Key)
                if (job.getBoolean(Common.Sucess_Key)) {
//                    controller?.setProfileInfo(job.toString())
                    val gson = GsonBuilder().create()
                    val profile: UserJobProfile =
                        gson.fromJson(job.toString(), UserJobProfile::class.java)
                    AppController.controller.setUserJobProfile(profile)
                    AppController.controller.setUserJobDetail(profile.UserDetails)
//                    AppController.controller.myPlaceDetail = profile.UserDetails[0].MyPlaceDetails
//                    val myPlaceDetails = profile.UserDetails[0].MyPlaceDetails[0]
                  /*  var myPlaceDetails: MyPlaceDetail? = null

                    if(AppController.controller.jobNumber==null || AppController.controller.jobNumber=="null" || AppController.controller.jobNumber==""){
                        AppController.controller.jobNumber=profile.UserDetails[0].MyPlaceDetails[0].JobNo
                    }
//                    println("AppController.controller.jobNumber:: "+AppController.controller.jobNumber)
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
                    AppController.controller.my_Place_Details = myPlaceCredentials*/
                    callback.invoke()
                } else {
                    Utils.showToast(
                        this,
                        jsonObject.getString(Common.Message),
                        Common.errorCase
                    )
                }
            } else {
                Utils.showToast(
                    this,
                    jsonObject.getString(Common.Message),
                    Common.errorCase
                )
            }
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
        }
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/
}