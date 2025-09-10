package com.dmss.burbankappold.dashboard.ui.documents

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.dmss.burbankappold.databinding.ActivityWebViewBinding
import com.dmss.burbankappold.utils.*
import common.TransparentProgressDialog
import common.Utils

class WebViewActivity : AppCompatActivity() {
    private val url: String? by lazy {
        intent.getStringExtra(BundleKey.URL)
    }
    private lateinit var mBinding : ActivityWebViewBinding
    var pd : TransparentProgressDialog? = null
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolBar()
        val urlWebView = mBinding.webView
        urlWebView.settings.javaScriptEnabled = true
        urlWebView.settings.builtInZoomControls = true;
        urlWebView.settings.mediaPlaybackRequiresUserGesture = false;
        urlWebView.loadUrl(url)
        pd = Utils.getProgress(this@WebViewActivity)
        urlWebView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if(view.contentHeight == 0){
                    view.reload()
                    return
                }else{
                    super.onPageFinished(view, url)
                    pd?.dismiss()
                }
            }
            @Deprecated("Deprecated in Java")
            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                pd?.dismiss()
                showToast("Error:$description")
            }
        }
    }

    private fun initToolBar(){
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivToolBarBack.changeIconColor(this)
        mBinding.toolbar.constraint.changeBackgroundColor(this)
        mBinding.toolbar.tvToolbarTitle.changeIconColor(this)
        mBinding.toolbar.ivChat.hide()
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
    }
}