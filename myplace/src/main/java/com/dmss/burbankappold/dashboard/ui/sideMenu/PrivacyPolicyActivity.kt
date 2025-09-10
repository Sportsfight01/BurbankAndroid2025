package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.dmss.burbankappold.databinding.ActivityPrivacyPolicyBinding
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show
import common.AppController

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolBar()
    }

    private fun initToolBar() {
        val my_Place_Details=AppController.controller.my_Place_Details
        val region=my_Place_Details.region
        var regionUrl="https://www.burbank.com.au/victoria/terms-conditions#privacypolicy"
        when (region) {
            "QLD" -> {
                regionUrl="https://www.burbank.com.au/queensland/terms-conditions#privacypolicy"

            }
            "SA" -> {
                regionUrl="https://www.burbank.com.au/south-australia/terms-conditions#privacypolicy"

            }
            "NSW" -> {
                regionUrl="https://www.burbank.com.au/nsw/terms-conditions#privacypolicy"

            }
        }
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivChat.hide()
        mBinding.toursWebView.settings.javaScriptEnabled = true
        mBinding.toursWebView.isVerticalScrollBarEnabled = true;
        mBinding.toursWebView.isHorizontalScrollBarEnabled = true;
        mBinding.toursWebView.settings.builtInZoomControls = true;
        mBinding.toursWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        mBinding.toursWebView.loadUrl(regionUrl)

        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
    }

}