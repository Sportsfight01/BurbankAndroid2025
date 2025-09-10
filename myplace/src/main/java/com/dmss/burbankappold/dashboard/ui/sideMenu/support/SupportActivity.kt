package com.dmss.burbankappold.dashboard.ui.sideMenu.support

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyNotificationActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.support.faq.FAQsActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.support.infoCentre.InfoCentreActivity
import com.dmss.burbankappold.databinding.ActivitySupportBinding
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.loadUrlUsingGlide
import com.dmss.burbankappold.utils.show

class SupportActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivitySupportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViews()
    }

    private fun initViews() {
        initToolBar()
        mBinding.mySupportHeader.tvHeading.text = getString(R.string.mysupport)
        mBinding.mySupportHeader.tvSubHeading.text = getString(R.string.my_support_heading_desc)
        mBinding.toolbar.line.visibility=View.VISIBLE
        mBinding.toolbar.ivNavMenu.visibility=View.GONE
        mBinding.faqcardview.setOnClickListener {
            startActivity(Intent(this, FAQsActivity::class.java))
        }
        mBinding.contactuscarview.setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }
        mBinding.infoseecardview.setOnClickListener {
            startActivity(Intent(this, InfoCentreActivity::class.java))
        }
    }

    private fun initToolBar(){
        mBinding.mySupportHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivChat.hide()
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.constraint.setBackgroundColor(Color.TRANSPARENT)
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.mySupportHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
//        mBinding.mySupportHeader.tvNotificationsCount.text = PrefsHelper.notificationCount
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount!="" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            mBinding.mySupportHeader.tvNotificationsCount.text = notificationCount
        }
        mBinding.mySupportHeader.ivPhoto.setOnClickListener {
            startActivity(Intent(this, MyNotificationActivity::class.java))
        }
    }
}