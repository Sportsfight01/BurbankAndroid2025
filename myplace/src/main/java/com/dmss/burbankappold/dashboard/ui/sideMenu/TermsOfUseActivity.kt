package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.databinding.ActivityTermsOfUseBinding
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show

class TermsOfUseActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTermsOfUseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTermsOfUseBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolBar()

    }
    private fun initToolBar() {
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivChat.show()
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
    }

}