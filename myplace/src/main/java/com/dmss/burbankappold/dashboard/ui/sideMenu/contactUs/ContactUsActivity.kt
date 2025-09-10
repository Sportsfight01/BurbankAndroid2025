package com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.databinding.ActivityContactUs2Binding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.AppConstants
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.hideKeyboardFrom
import com.dmss.burbankappold.utils.show
import common.TransparentProgressDialog
import common.Utils

class ContactUsActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityContactUs2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityContactUs2Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initToolBar()
    }

    /* fun sendMail(from: String ="", to: String="", subject : String =""){
         val email_intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", to, null))
         email_intent.putExtra(Intent.EXTRA_SUBJECT, subject)
         startActivity(Intent.createChooser(email_intent, "Send email..."))
     }*/

    private fun initToolBar(){
        mBinding.toolbar.line.visibility= View.VISIBLE
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivChat.hide()
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
            AppConstants.backFromContcatcus=true

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppConstants.liastOfNotesData= emptyList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppConstants.backFromContcatcus=true

    }
}