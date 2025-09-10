package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.dmss.burbankappold.databinding.ActivityMyAppointments2Binding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import common.AppController
import common.MyPlaceDataBase
import common.TransparentProgressDialog
import common.Utils
import models.progress.UserJobProgressItem
import models.progress.UserJobProgressNewItem

class MyAppointmentsActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityMyAppointments2Binding
    var pd: TransparentProgressDialog? = null
    private var mList = listOf<UserJobProgressItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMyAppointments2Binding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViews()
//        requestApiCall()


        println("UserJobProgressNewItem:: "+AppConstants.UserJobProgressNewItem)
        updateNewUI(AppConstants.UserJobProgressNewItem)
    }

    private fun initViews() {
        initToolBar()
        mBinding.headerLayout.tvHeading.text = getString(R.string.myappointments)
        mBinding.headerLayout.tvSubHeading.text = getString(R.string.my_appointments_details)
        mBinding.toolbar.line.visibility=View.VISIBLE
        mBinding.toolbar.ivChat.show()
        /*if(AppConstants.contacts_total_items==AppConstants.contacts_allreadItems){
            mBinding.toolbar.tvNotifications.hide()
        }else{
            mBinding.toolbar.tvNotifications.show()

        }*/
        mBinding.toolbar.ivChat.setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }
        mBinding.headerLayout.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        mBinding.headerLayout.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
//        mBinding.headerLayout.tvNotificationsCount.text = PrefsHelper.notificationCount
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount!="" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            mBinding.headerLayout.tvNotificationsCount.text  = notificationCount
        }
        mBinding.swiperefresh.setOnRefreshListener {
            if( mList.isEmpty()) {
                Utils.hideSwipeRefresh(mBinding.swiperefresh)

                requestApiCall()
            }else{
                Utils.hideSwipeRefresh(mBinding.swiperefresh)
            }

        }
    }

    private fun initToolBar(){
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.toolbar.ivChat.show()
    }

    private fun requestApiCall(){
        if(Utils.isNetworkAvailable(this)) {
            pd = Utils.getProgress(this)
            ApiRepository.requestProgressSteps { isSuccess, progressData ->
                pd?.dismiss()
                if (isSuccess) {
                    mList = progressData!!
                    updateUI(progressData)
                }
            }
        }
    }
    private fun updateNewUI(myAppointments: ArrayList<UserJobProgressNewItem>?) {
        myAppointments?.forEach {
            println("UserJobProgressNewItem:: "+it.name)
            when(it.name){
                "Edge Appointment", "Colour Selection" ->{
                    if (it.status =="unplanned")
                        mBinding.colourSelectionDateTextView.text = "--"
                    else mBinding.colourSelectionDateTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                }
                "Electrical Selection" ->{
                    mBinding.electricalSelectionDateTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                }
                "Sign Building Contract" ->{
                    if (it.status =="Completed")
                        mBinding.buildingSelectionDateTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                    else mBinding.buildingSelectionDateTextView.text = "--"
                }
                "Tender Presentation" ->{
                    mBinding.tenderSelectionDateTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                }
                "PC INSPECTION" ->{
                    mBinding.pcInspectionTimeTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)

                    /* if (AppController.controller.isShowPcInspection){
                             mBinding.pcInspectionTimeTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                         }
                         else mBinding.pcInspectionTimeTextView.text = "--"*/

                }

            }
        }
    }
    private fun updateUI(myAppointments: List<UserJobProgressItem>?) {
        myAppointments?.forEach {
            when(it.name){
                "Edge Appointment", "Colour Selection" ->{
                    if (it.status =="unplanned")
                        mBinding.colourSelectionDateTextView.text = "--"
                    else mBinding.colourSelectionDateTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                }
                "Electrical Selection" ->{
                    mBinding.electricalSelectionDateTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                }
                "Sign Building Contract" ->{
                    if (it.status =="Completed")
                        mBinding.buildingSelectionDateTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                    else mBinding.buildingSelectionDateTextView.text = "--"
                }
                "Tender Presentation" ->{
                    mBinding.tenderSelectionDateTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                }
                "PC INSPECTION" ->{
                    if (AppController.controller.isShowPcInspection){
                        mBinding.pcInspectionTimeTextView.text = it.dateactual!!.convertDateFormat(APPOINTMENTS_DATE_FORMAT)
                    }
                    else mBinding.pcInspectionTimeTextView.text = "--"
                }

            }
        }
    }
}