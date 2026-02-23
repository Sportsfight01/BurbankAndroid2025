package com.dmss.burbankapp.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.databinding.ActivityEnquireNowBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.enguirenow.EnquireViewModel
import com.dmss.burbankapp.utils.AppConstants
import common.AppController

class DashboardEnquiryNow : BaseActivity() {
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


    @SuppressLint("SuspiciousIndentation")
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
//            var stateName = "victoria"
            binding.webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    return false
                }
            }

            binding.webView.settings.javaScriptEnabled = true

            // Handle URL loading inside the WebView
            binding.webView.webViewClient = WebViewClient()
            clearWebViewData(binding.webView)
            // PRODUCTION
            val VICTORIA = "1LzPnJZ1mSIOTU5qoeLnfXAr78x7?"
            val QLD = "1gs51ik2dTDW_5LmNHMKJHAqcx0d?"
            val SA = "1tVwIL7lISIigrybQm7kN9wqcx0d?"
            val NSW = "1ewRKuUVHSDG1nbIfAUZrQgqcx0d?"

            // DEVELOPMENT
//            val VICTORIA ="16EIGnKc6ReGd8wXef5sASQr78x7?"
//            val QLD ="1xNYzAlZxSpqcpWbgAES5oAr78x7?"
//            val SA ="1wk-EWPRcQairXIbos5oM7Qr78x7?"
//            val NSW ="1ncdi4qLFQBm_5tVINWrJEwr78x7?"
            // Load a URL
            if (!TextUtils.isEmpty(stateName)) {
                var loadUrl = ""
                if (stateName.contentEquals("victoria", true)) {
                    loadUrl =
                        AppConstants.ENQUIRY_BASE_URL + VICTORIA + "firstname=$mFirstName&lastname=$mLastName&email=$mEmail&message=&phone=$mPhoneNumber&where_would_you_like_to_live_nsw_=&housename=$mHouseName $mHouseSize&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mADDRESS"

                } else if (stateName.contentEquals("queensland", true)) {
                    loadUrl =
                        AppConstants.ENQUIRY_BASE_URL + QLD + "firstname=$mFirstName&lastname=$mLastName&email=$mEmail&message=&phone=$mPhoneNumber&where_would_you_like_to_live_nsw_=&housename=$mHouseName $mHouseSize&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mADDRESS"

                } else if (stateName.contentEquals("south-australia", true)) {
                    loadUrl =
                        AppConstants.ENQUIRY_BASE_URL + SA + "firstname=$mFirstName&lastname=$mLastName&email=$mEmail&message=&phone=$mPhoneNumber&where_would_you_like_to_live_nsw_=&housename=$mHouseName $mHouseSize&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mADDRESS"

                } else if (stateName.contentEquals(
                        "NSW",
                        true
                    ) || stateName.contentEquals("NSW & ACT", true)
                ) {

                    loadUrl =
                        AppConstants.ENQUIRY_BASE_URL + NSW + "firstname=$mFirstName&lastname=$mLastName&email=$mEmail&message=&phone=$mPhoneNumber&where_would_you_like_to_live_nsw_=&housename=$mHouseName $mHouseSize&i_accept_burbank_s_privacy_policy_and_collection_statement_=&original_marketing_activity=MyPlace App&build_address=$mADDRESS"

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
}