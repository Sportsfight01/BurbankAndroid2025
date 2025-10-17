package com.dmss.burbankapp.ui.enquireNow

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.databinding.ActivityEnquireNowBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.ui.enguirenow.EnquireViewModel
import common.AppController


class EnquireNowActivity : BaseActivity() {
    lateinit var enquireViewModel: EnquireViewModel
    lateinit var preferences: CustomSharedPreferences
    var isChecked: Boolean = false
    lateinit var binding: ActivityEnquireNowBinding
    var localPlaces: List<String> = arrayListOf()
    lateinit var localPlace: String
    lateinit var cameFrom: String
    lateinit var packageID: String
    lateinit var packageLandBankId: String
    lateinit var estateName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnquireNowBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        changeStatusBarColor(Color.WHITE)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.app_bg))

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivProfile.setOnClickListener {
            val signUpIntent = Intent(this, DashboardActivity::class.java)
            signUpIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(signUpIntent)
        }
    }


    override fun onStart() {
        super.onStart()
        preferences = CustomSharedPreferences(AppController.getInstance())

        var intent = intent

        if (intent != null) {
            var mHouseName = intent.getStringExtra("HOSUENAME");
            var mHouseSize = intent.getIntExtra("HOUSESIZE", 0);
            var mADDRESS = intent.getStringExtra("ADDRESS");

            cameFrom = intent.getStringExtra("FROM").toString()
            if (cameFrom.contentEquals("homeland")) {
                packageID = intent.getStringExtra("PACKAGEID").toString()
                packageLandBankId = intent.getStringExtra("PACKAGE_LANDBANK_ID").toString()
            } else if (cameFrom.contentEquals("display")) {
                estateName = (intent.getStringExtra("ESTATE")).toString()
                if (estateName != null) {
                    estateName = estateName.trim()
                }
            }


            var userInfoModel = preferences.getUserInfoModel()
            var mFirstName = userInfoModel.FirstName
            var mLastName = userInfoModel.LastName
            var mEmail = userInfoModel.Email
            var mPhoneNumber = userInfoModel.PhoneNumber

            var stateName = preferences.getSelectedState()
            binding.webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }
            }

            binding.webView.settings.javaScriptEnabled = true

            // Handle URL loading inside the WebView
            binding.webView.webViewClient = WebViewClient()
        clearWebViewData(binding.webView)
            // Load a URL
            if (!TextUtils.isEmpty(stateName)) {
                var loadUrl = ""
                if (stateName.contentEquals("victoria", true)) {
                    loadUrl = "https://share.hsforms.com/1pTObYsRpSLa10d4MGWJBQgqcx0d?firstname=$mFirstName&lastname=$mLastName&email=$mEmail&message=&phone=$mPhoneNumber&where_would_you_like_to_live_nsw_=&housename=$mHouseName $mHouseSize&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mADDRESS"

                } else if (stateName.contentEquals("queensland", true)) {
                    loadUrl = "https://share.hsforms.com/1gs51ik2dTDW_5LmNHMKJHAqcx0d?firstname=$mFirstName&lastname=$mLastName&email=$mEmail&message=&phone=$mPhoneNumber&where_would_you_like_to_live_nsw_=&housename=$mHouseName $mHouseSize&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mADDRESS"

                } else if (stateName.contentEquals("south-australia", true)) {
                    loadUrl = "https://share.hsforms.com/1tVwIL7lISIigrybQm7kN9wqcx0d?firstname=$mFirstName&lastname=$mLastName&email=$mEmail&message=&phone=$mPhoneNumber&where_would_you_like_to_live_nsw_=&housename=$mHouseName $mHouseSize&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mADDRESS"

                } else if (stateName.contentEquals("NSW", true) || stateName.contentEquals("NSW & ACT", true)) {

                    loadUrl = "https://share.hsforms.com/1ewRKuUVHSDG1nbIfAUZrQgqcx0d?firstname=$mFirstName&lastname=$mLastName&email=$mEmail&message=&phone=$mPhoneNumber&where_would_you_like_to_live_nsw_=&housename=$mHouseName $mHouseSize&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mADDRESS"
                }
                println("loadUrl:: $loadUrl")
                binding.webView.loadUrl(loadUrl)

            }
        }
    }
    fun clearWebViewData(webView: WebView) {
        webView.clearCache(true)
        webView.clearHistory()
        webView.clearFormData()
        webView.clearSslPreferences()
        webView.loadUrl("about:blank")

        // Clear cookies
       val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }
  /*  @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()

        var intent = intent

        if (intent != null) {
            var houseName = intent.getStringExtra("HOSUENAME");
            var houseSize = intent.getIntExtra("HOUSESIZE", 0);
            cameFrom = intent.getStringExtra("FROM").toString()
            if (cameFrom.contentEquals("homeland")) {
                packageID = intent.getStringExtra("PACKAGEID").toString()
                packageLandBankId = intent.getStringExtra("PACKAGE_LANDBANK_ID").toString()
            } else if (cameFrom.contentEquals("display")) {
                estateName = (intent.getStringExtra("ESTATE")).toString()
                if(estateName !=null){
                    estateName = estateName.trim()
                }
            }
            binding.etHud.text = (houseName + " " + houseSize.toString())

        }
        preferences = CustomSharedPreferences(AppController.getInstance())
        binding.ivProfile.setOnClickListener {
            val signUpIntent = Intent(this, DashboardActivity::class.java)
            signUpIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(signUpIntent)
        }
        setUpViewModel()
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        var userInfoModel = preferences.getUserInfoModel()

        binding.ivCheck.setOnClickListener {
            if (isChecked) {
                isChecked = false
                binding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.uncheck_checkbox
                    )
                )
            } else {
                isChecked = true
                binding.ivCheck.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.enq_checj
                    )
                )
            }


        }
        userInfoModel.FirstName?.let {
            if (it.isNotEmpty()) {
                binding.etFirstName.setText(it.capitalize())
                binding.etFirstName.background =
                    ContextCompat.getDrawable(
                        binding.etFirstName.context,
                        R.drawable.disable_gery
                    )
                binding.etFirstName.isEnabled = false
                binding.etFirstName.isFocusable = false
            } else {
                binding.etFirstName.background = ContextCompat.getDrawable(
                    binding.etFirstName.context,
                    R.drawable.rectangel_shape
                )
                binding.etFirstName.isEnabled = true
                binding.etFirstName.isFocusableInTouchMode = true


            }


        }
        userInfoModel.Email?.let {
            if (it.isNotEmpty()) {
                binding.etEmail.setText(it)
                binding.etEmail.background =
                    ContextCompat.getDrawable(binding.etEmail.context, R.drawable.disable_gery)
                binding.etEmail.isEnabled = false
                binding.etEmail.isFocusable = false

            } else {
                binding.etEmail.background = ContextCompat.getDrawable(
                    binding.etEmail.context,
                    R.drawable.rectangel_shape
                )
                binding.etEmail.isEnabled = true
                binding.etEmail.isFocusableInTouchMode = true
            }
        }

        userInfoModel.LastName?.let {
            if (it.isNotEmpty()) {
                binding.etLastName.setText(it)
                binding.etLastName.background =
                    ContextCompat.getDrawable(binding.etLastName.context, R.drawable.disable_gery)
                binding.etLastName.isFocusable = false
                binding.etLastName.isFocusableInTouchMode = false
            } else {
                binding.etLastName.background = ContextCompat.getDrawable(
                    binding.etLastName.context,
                    R.drawable.rectangel_shape
                )
                binding.etLastName.isFocusable = true
                binding.etLastName.isFocusableInTouchMode = true
            }
        }

        userInfoModel.PhoneNumber?.let {
            binding.etPhone.setText(it)
        }

        binding.btnEnq.setOnClickListener {
            var firstName: String = binding.etFirstName.text.toString().trim()
            var lastName: String = binding.etLastName.text.toString().trim()
            var phoneNumber: String = binding.etPhone.text.toString().trim()
            var etMessage: String = binding.etMessage.text.toString().trim()
            var etEmail = binding.etEmail.text.toString().trim()
            *//*if (binding.etEmail.text.toString().isNotEmpty()) {
                etEmail = binding.etEmail.text.toString().trim()
            }*//*


            if (firstName.isNotEmpty()) {
                if (lastName.isNotEmpty()) {

                        if (!TextUtils.isEmpty(etEmail)
                        ) {
                            if (validEmailName()){
                                if (!TextUtils.isEmpty(phoneNumber)) {
                                if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber) && phoneNumber.length >= 8) {
                                    *//*if (!TextUtils.isEmpty(etMessage)) {*//*

                                    if (!TextUtils.isEmpty(localPlace)) {
                                        if (isChecked) {
                                            if (intent != null) {
                                                var houseName = intent.getStringExtra("HOSUENAME")
                                                var houseSize = intent.getIntExtra("HOUSESIZE", 0);
                                                fetchEnquiry(
                                                    etMessage,
                                                    houseName,
                                                    houseSize.toString(),
                                                    firstName,
                                                    lastName,
                                                    phoneNumber,
                                                    etEmail,
                                                    localPlace
                                                )
                                            }
                                        } else {
                                            AppUtils.showCustomCenterToast(
                                                this,
                                                "Please accept Burbank's privacy policy"
                                            )
                                        }
                                    } else {
                                        AppUtils.showValidationAlert(
                                            this,
                                            "Please select where would you like to live?"
                                        )
                                    }

                                } else {
                                    AppUtils.showValidationAlert(
                                        this,
                                        "Please enter valid phone number"
                                    )


                                }
                        }
                            else {
                                    AppUtils.showValidationAlert(this, "Please enter phone number")

                                }

                        } else {
                                AppUtils.showValidationAlert(this, "Please enter valid email id")

                            }


                    } else {
//                        AppUtils.showValidationAlert(this, "Please enter phone number")
                        AppUtils.showValidationAlert(
                            this,
                            "Please enter email"
                        )
                    }

                } else {
                    AppUtils.showValidationAlert(this, "Please enter last name")
                }

            } else {
                AppUtils.showValidationAlert(this, "Please enter first name")
            }
        }

        var stateName = preferences.getSelectedState()
        if (!TextUtils.isEmpty(stateName)) {
            if (stateName.contentEquals("victoria", true)) {
                localPlaces = resources.getStringArray(R.array.victoria).toList()
            } else if (stateName.contentEquals("queensland", true)) {
                localPlaces = resources.getStringArray(R.array.queensland).toList()
            } else if (stateName.contentEquals("south-australia", true)) {
                localPlaces = resources.getStringArray(R.array.south_australia).toList()
            } else if (stateName.contentEquals("NSW & ACT", true)) {
                localPlaces = resources.getStringArray(R.array.nsw).toList()
            }
        }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, localPlaces
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.localPlaceSpinner.adapter = adapter

        binding.localPlaceSpinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                localPlace = if (position == 0) {
                    ""
                } else {
                    localPlaces[position]
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }
    private fun validEmailName(): Boolean {
        val emailString = binding.etEmail.text.toString().trim()
        return Patterns.EMAIL_ADDRESS.matcher(emailString).matches()
    }
    private fun setUpViewModel() {
        enquireViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(EnquireViewModel::class.java)
        setupObserver()
    }

    private fun setupObserver() {
        enquireViewModel.getEnquireLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var regionsData: EnquireModel? = it.data
                    if (regionsData != null) {
                        if (regionsData.status) {
                            showSuccessDialog(regionsData.message)
                        }
                    }
                }
                Status.LOADING -> {
                    showProgressDialog()
                }
                Status.ERROR -> {
                    dismissProgressDialog()
                }
            }
        })
    }


    //Change to Dialog with OK Action
    private fun showSuccessDialog(message: String) {
        AppUtils.showEnquireNowSuccessAlert(this,message)
    }


    private fun fetchEnquiry(
        message: String?,
        houseName: String?,
        houseSize: String?,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?,
        email: String?,
        localPlace: String?
    ) {
        val enquiryObj = JsonObject()
        val bodyObj = JsonObject()
        var stateName: String = preferences.getSelectedState().toString()
        var stateId: String = preferences.getSelectedStateID().toString()


        //var notes = "DisplayLocation-${estateName!!.trim()}~AppointmentTime-${time}~Date-${date}"
        if (stateName.lowercase().contains("nsw & act")) {
            stateName = "nsw"
        } else if (stateName.lowercase().contains("south-australia")) {
            stateName = "south-australia"
        } else if (stateName.lowercase().contains("queensland")) {
            stateName = "queensland"
        } else {
            stateName = "victoria"
        }

        var subject = ""
        var formName = ""
        var notes = ""
        if (cameFrom.contentEquals("display")) {
            notes = "DisplayLocation-${estateName!!.trim()}"
            var estate = ""
            if(estateName != null){
                estate = estateName.replace(" ", "-")
            }
            formName = "DisplayHomesEnquiry"
            //https://www.burbank.com.au/south-australia/display-location/miravale-estate/
            subject =
                "MyPlace app Enquiry - ${AppConstants.WEB_BASE_URL}/${stateName}/display-location/${estate}/"
        } else if (cameFrom.contentEquals("homeland")) {
            formName = "HomeandLandEnquiry"
            subject =
                "MyPlace app Enquiry - ${AppConstants.WEB_BASE_URL}/${stateName}/home-details/housename.${houseName};housesize.${houseSize};packageId.${packageLandBankId}/"
            notes = "PackageId-${packageLandBankId}"
            //subject = "$subject;packageId.$packageID/
        } else {
            subject =
                "MyPlace app Enquiry - ${AppConstants.WEB_BASE_URL}/${stateName}/house-details/housename.${houseName};housesize.${houseSize}/"
            notes = ""
            formName = "NewHomesEnquiry"
        }
        //https://www.burbank.com.au/south-australia/house-details/housename.Madigan;housesize.142/

        bodyObj.addProperty("Message", message)
        bodyObj.addProperty("I'm interested in", "$houseName $houseSize")
        bodyObj.addProperty("Where would you like to live", localPlace)
        bodyObj.addProperty(
            "I accept Burbank’s privacy policy and wish to receive Burbank’s latest news and offers on my email.",
            if (isChecked) "true" else "false"
        )

        bodyObj.addProperty("Name", firstName + " " + lastName)
        bodyObj.addProperty("PhoneNumber", phoneNumber)
        bodyObj.addProperty("Email", email)
        bodyObj.addProperty("Where would you like to live", localPlace)

        enquiryObj.addProperty("Subject", subject)
        enquiryObj.addProperty("Email", email)
        enquiryObj.addProperty("Message", message)
        enquiryObj.addProperty("Build", localPlace)
        enquiryObj.addProperty("Where would you like to live", localPlace)
        enquiryObj.addProperty("State", stateName)
        enquiryObj.addProperty("Header", "")
        enquiryObj.addProperty("IsConsent", true)
        enquiryObj.addProperty("ReferralSource", "")
        enquiryObj.addProperty("EnquiryType", "Android - App")
        enquiryObj.addProperty("FirstName", firstName)
        enquiryObj.addProperty("HouseName", houseName)
        enquiryObj.addProperty("Hearaboutus", "")

        enquiryObj.addProperty("FormName", formName)
        enquiryObj.addProperty("PhoneNumber", phoneNumber)
        enquiryObj.addProperty("LastName", lastName)
        enquiryObj.addProperty("Notes", notes)
        enquiryObj.add("Body", bodyObj)

        enquireViewModel.fetchEnquire(enquiryObj)
    }*/

}