package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.dmss.burbankappold.databinding.ActivityMyHistoryBinding
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.*
import com.google.gson.Gson
import com.google.gson.JsonParser
import common.AppController
import common.TransparentProgressDialog
import common.Utils
import models.LoginRequestBody

class MyHistoryActivity : AppCompatActivity() {
    private var pd: TransparentProgressDialog? = null
    private lateinit var mBinding: ActivityMyHistoryBinding
    private lateinit var mMyHistoryAdapter: MyHistoryAdapter
    private var mList = listOf<NotesData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMyHistoryBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initViews()
        if (Utils.isNetworkAvailable(this)) {
            AppConstants.validateVersionCode(this) {
                if (!it) {
                    requestApiClickHomeLoginBaseUrl()
                }
            }
        }
    }
    private fun sortBydateList(documentsList : List<NotesData>): List<NotesData> {
        val sortedList = documentsList.sortedByDescending {  it.activityDate }
        return sortedList
    }
    private fun initViews() {
        initToolBar()
        mBinding.headerLayout.tvHeading.text = getString(R.string.myhistory)
        mBinding.headerLayout.tvSubHeading.text = getString(R.string.myhistory_desc)
        mBinding.toolbar.line.visibility=View.VISIBLE
        mBinding.headerLayout.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        mMyHistoryAdapter = MyHistoryAdapter {  }
        mBinding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@MyHistoryActivity)
            adapter = mMyHistoryAdapter
        }
        mBinding.swiperefresh.setOnRefreshListener {
            if(mList.isEmpty() && Utils.isNetworkAvailable(this)) {
                Utils.hideSwipeRefresh(mBinding.swiperefresh)

                requestApiClickHomeLoginBaseUrl()
            }else{
                Utils.hideSwipeRefresh(mBinding.swiperefresh)
            }

        }
        /*if(AppConstants.contacts_total_items==AppConstants.contacts_allreadItems){
            mBinding.toolbar.tvNotifications.hide()
        }else{
            mBinding.toolbar.tvNotifications.show()

        }*/
    }

    private fun initToolBar(){
        mBinding.toolbar.ivNavMenu.hide()
        mBinding.toolbar.ivToolBarBack.show()
        mBinding.toolbar.ivChat.show()
        mBinding.toolbar.ivToolBarBack.setOnClickListener {
            onBackPressed()
        }
        mBinding.toolbar.ivChat.setOnClickListener {
            startActivity(Intent(this, ContactUsActivity::class.java))
        }
        mBinding.headerLayout.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0")
            View.GONE else View.VISIBLE
//        mBinding.headerLayout.tvNotificationsCount.text = PrefsHelper.notificationCount
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount!="" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            mBinding.headerLayout.tvNotificationsCount.text =notificationCount
        }
        mBinding.headerLayout.ivPhoto.setOnClickListener {
            startActivity(Intent(this, MyNotificationActivity::class.java))
        }
    }

    private fun requestApiCall() {
        pd = Utils.getProgress(this)
        ApiRepository.requestUserHistoryDetails(getLoginRequestBody()){ isSuccess, data ->
            pd?.dismiss()
            if (isSuccess){
//                mMyHistoryAdapter.setMyHistoryData(data)
            }
        }
    }
    private fun requestApiClickHomeLoginBaseUrl(){
        /*  var Password= AppController.controller.myPlaceDetail[0].Password
          var JobNo= AppController.controller.myPlaceDetail[0].JobNo
          var UserName= AppController.controller.myPlaceDetail[0].UserName*/
        if(Utils.isNetworkAvailable(this)) {
            var Password = AppController.controller?.my_Place_Details?.password
            var JobNo = AppController.controller?.my_Place_Details?.jobNumber
            var UserName = AppController.controller?.my_Place_Details?.username
         /*   val jsonParser = JsonParser()
            val jsonString = "{contractNumber:$JobNo,userName:$UserName,password:$Password}"
            val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject*/
            val jsonObjectObj = Utils.getLoginRequest(JobNo,UserName,Password)
            pd = Utils.getProgress(this)
            ApiRepository.requestContactUsLogin(jsonObjectObj) { isSuccess, contactData,loginuserData ->
                pd?.dismiss()
                if (isSuccess) {

                    requestApiClickHomeMasterContracts(contactData!!)
                }
            }
        }
    }
    private fun requestApiClickHomeMasterContracts(cookie:String){
        if(Utils.isNetworkAvailable(this)) {
            val jsonParser = JsonParser()
            val jsonString = AppConstants.jsonNotesString
            val jsonObjectObj = jsonParser.parse(jsonString).asJsonObject
            pd = Utils.getProgress(this)
            ApiRepository.requestContactMasterContracts(
                cookie,
                jsonObjectObj
            ) { isSuccess, contactData ->
                pd?.dismiss()
                if (isSuccess) {
//                println("Authorization isSuccess:: $isSuccess contactData::$contactData")
                    val resjsonObjectObj = jsonParser.parse(contactData).asJsonObject
                    var notesList = resjsonObjectObj.getAsJsonObject(getString(R.string.notes))
                        .getAsJsonArray(getString(R.string.list))
//            var mNotesData = listOf<NotesData>()
                    var mNotesData = Gson().fromJson(notesList, Array<NotesData>::class.java)
                    var NoReplayToData = mNotesData.filter { it.replyTo == null }
                    mList = NoReplayToData
//                var filterReplayToData= mNotesData.filter { it.replyTo!=null }
                    if (NoReplayToData.isNotEmpty()) {
                        mMyHistoryAdapter.setMyHistoryData(sortBydateList(NoReplayToData))
                        mBinding.rvHistory.show()
                        mBinding.noRecordFound.hide()
                    } else {
                        mBinding.rvHistory.hide()
                        mBinding.noRecordFound.show()
                    }
                }
            }
        }
    }
    private fun getLoginRequestBody(): LoginRequestBody {
        return LoginRequestBody(
            AppController.controller.my_Place_Details.jobNumber,
            AppController.controller.my_Place_Details.username,
            AppController.controller.my_Place_Details.region,
            AppController.controller.my_Place_Details.password
        )
    }
}