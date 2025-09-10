package com.dmss.burbankappold.dashboard.ui.sideMenu.support.infoCentre

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dmss.burbankappold.dashboard.ui.sideMenu.VideoWebViewActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.databinding.ActivityInfoCentreDetailsBinding
import com.dmss.burbankappold.utils.BundleKey
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.setVideoThumbnail
import com.dmss.burbankappold.utils.show
import common.AppController
import common.Common
import models.faq.FAQDataItem

class InfoCentreDetailsActivity : AppCompatActivity() {
    private val infoData: FAQDataItem? by lazy {
        intent.getParcelableExtra(BundleKey.DATA_OBJECT)
    }
    private lateinit var mBinding : ActivityInfoCentreDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInfoCentreDetailsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolBar()
        initUI()
    }

    private fun initUI() {
        infoData?.also { infoItem ->
            mBinding.tvTitle.text = infoItem.Heading
//            mBinding.videoImage.setVideoThumbnail(infoItem.VideoUrl)
            mBinding.videoImage.settings.javaScriptEnabled = true
/*                    binding.videoImage.settings.setPluginState(PluginState.ON);
                    binding.videoImage.setWebChromeClient(WebChromeClient())*/
            if(infoItem.VideoUrl==""){
                mBinding.videoImage.visibility=View.GONE
                mBinding.Imageview.visibility=View.VISIBLE
                Glide.with(AppController.getInstance())
                    .load("${Common.INFO_IMAGE_BASE_URL}${infoItem.Image}")
                    .into(mBinding.Imageview)

            }else {
                mBinding.videoImage.visibility=View.VISIBLE
                mBinding.Imageview.visibility=View.GONE
                mBinding.videoImage.loadUrl(infoItem.VideoUrl)
            }
            mBinding.tvDesc.text =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(
                        infoItem.Description?.trim { it <= ' ' },
                        Html.FROM_HTML_MODE_LEGACY
                    )
                } else {
                    Html.fromHtml(infoItem.Description?.trim { it <= ' ' })
                }

            mBinding.imgPlay.setOnClickListener {
                startActivity(Intent(this, VideoWebViewActivity::class.java).putExtra(BundleKey.URL, infoItem.VideoUrl ))
            }
            mBinding.toolbar.ivChat.setOnClickListener {
                startActivity(Intent(this, ContactUsActivity::class.java))
            }
        }
    }

    private fun initToolBar(){
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivChat.show()
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
    }
}