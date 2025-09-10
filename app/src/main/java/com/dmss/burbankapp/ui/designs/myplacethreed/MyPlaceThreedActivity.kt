package com.dmss.burbankapp.ui.designs.myplacethreed

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.databinding.ActivityMyPlaceThreedBinding
import com.dmss.burbankapp.utils.AppConstants
import timber.log.Timber
import kotlin.properties.Delegates

class MyPlaceThreedActivity : AppCompatActivity() {
    lateinit var customSharedPreferences: CustomSharedPreferences
    lateinit var stateName: String
    var isOrientationChange = false

    lateinit var binding: ActivityMyPlaceThreedBinding
    lateinit var houseName: String
    var houseSize by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPlaceThreedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    override fun onBackPressed() {
        if (!isOrientationChange) {
            super.onBackPressed()
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            isOrientationChange = false
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
        super.onConfigurationChanged(newConfig)
    }

    private fun initView() {
        binding.webView.webViewClient = MyWebViewClient(this)
        binding.webView.settings.domStorageEnabled = true

        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true;
        houseName = intent.getStringExtra("HouseName").toString()
        houseSize = intent.getIntExtra("HouseSize", 0)
        customSharedPreferences = CustomSharedPreferences(this)
        stateName = customSharedPreferences.getSelectedState().toString()
        if (stateName.isNotEmpty()) {
            if (stateName.contains(" ")) {
                stateName = stateName.replace(" ", "-")

            }


        }
        binding.webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
//        binding.webView.settings.setAppCacheEnabled(true);
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isOrientationChange = true
            binding.constantVertical.visibility = View.GONE
            binding.constantHorizontal.visibility = View.VISIBLE
            showWebView()
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            isOrientationChange = false
            binding.constantVertical.visibility = View.VISIBLE
            binding.constantHorizontal.visibility = View.GONE

        }

    }

    private fun showWebView() {
        if(stateName.contains("Wales")){
            stateName = "NSW"
        }else if(stateName.contains("South")){
            stateName = "South-Australia"
        }
        val url =
            AppConstants.MYPLACETHREED_BASE + "/${stateName}/myplace3dmobile/housename.${houseName};housesize.${houseSize}/"

        /* val url =
             "https://www.burbank.com.au/${stateName.toLowerCase(Locale.ROOT)}/house-details/${houseName}-${houseSize}"*/
        binding.webView.loadUrl(url)


        Timber.e("MyPlace3d URL ::----->${url}")
    }

    override fun onResume() {
        //  initView()
        super.onResume()

    }

    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url: String = request?.url.toString();
            view?.loadUrl(url)
            return true
        }

        override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
            webView.loadUrl(url)
            return true
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            Toast.makeText(activity, "Got Error! $error", Toast.LENGTH_SHORT).show()
        }
    }

}