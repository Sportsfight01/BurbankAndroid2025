package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dmss.burbankappold.R
import com.dmss.burbankappold.databinding.ActivityVideoWebViewBinding
import com.dmss.burbankappold.utils.BundleKey
import com.dmss.burbankappold.utils.showToast
import common.TransparentProgressDialog

class VideoWebViewActivity : AppCompatActivity() {
    private val url: String? by lazy {
        intent.getStringExtra(BundleKey.URL)
    }
    private lateinit var mBinding : ActivityVideoWebViewBinding
    var pd : TransparentProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityVideoWebViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val urlWebView = mBinding.webView
        urlWebView.settings.javaScriptEnabled = true
        urlWebView.settings.mediaPlaybackRequiresUserGesture = false
        urlWebView.loadUrl(url)
    }
}