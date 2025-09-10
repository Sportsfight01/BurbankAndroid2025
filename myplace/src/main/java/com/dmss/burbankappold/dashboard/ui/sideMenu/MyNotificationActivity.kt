package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.ContactUsActivity
import com.dmss.burbankappold.databinding.ActivityMyNotificationBinding
import com.dmss.burbankappold.utils.AppConstants
import com.dmss.burbankappold.utils.BundleKey
import com.dmss.burbankappold.utils.SharedViewModel
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.show
import common.AppController
import common.MyPlaceDataBase
import models.NotificationModel

class MyNotificationActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMyNotificationBinding
    var menuNotificationModels = ArrayList<NotificationModel>()
    private val controller = AppController.controller
    lateinit var myPlaceDataBase: MyPlaceDataBase
    private lateinit var noNotificationTextView: AppCompatTextView
    private lateinit var menuListView: RecyclerView
    private lateinit var notificationListAdapter: NotificationsAdapter
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMyNotificationBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        myPlaceDataBase = MyPlaceDataBase(this)
        noNotificationTextView = mBinding.tvNotifications
        mBinding.toolbar.line.visibility=View.VISIBLE
        menuListView = mBinding.rvNotifications
        mBinding.tvJobNumber.text = controller.my_Place_Details.jobNumber
        viewModel = run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        notificationListAdapter = NotificationsAdapter {
            val bundle = Bundle()
            println("NotificationsAdapter:: "+it.isPhoto+" "+it.isPhoto+"  "+it.date)
            if (it.isPhoto){
                bundle.putBoolean(BundleKey.PHOTO, true)
                if (!it.isRead) {
                    myPlaceDataBase.updatePhotoNotifications(
                        controller?.my_Place_Details?.jobNumber,
                        controller?.my_Place_Details?.username,
                        it.date
                    )
                }
            }else bundle.putBoolean(BundleKey.PHOTO, false)
            controller.analytics.notificationsScreenNotificationsTouchEvent()
            if (!it.isRead){
                myPlaceDataBase.updateNotificationProgressReadStatus(
                    controller?.my_Place_Details?.jobNumber,
                    controller?.my_Place_Details?.username,
                    it.taskId
                )
            }
            viewModel.financeItem(AppConstants.financeItemStatus)
            val intent = Intent(this, DashboardNewActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            finishAffinity()
        }
        menuListView.apply {
            layoutManager = LinearLayoutManager(this@MyNotificationActivity)
            adapter = notificationListAdapter
        }
        initToolBar()
        updateNotifications()
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
    }

    private fun updateNotifications() {
        menuNotificationModels.clear()
        val photo: Boolean = controller.isPhotoNotifications
        val stage: Boolean = controller.isProgressNotifications
        val progress: Boolean = controller.isStageNotifications
        if (photo && progress && stage) {
            menuNotificationModels.addAll(
                myPlaceDataBase.getPhotoNotifications(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username
                )
            )
            menuNotificationModels.addAll(
                myPlaceDataBase.getProgress(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username,
                    false
                )
            )
            menuNotificationModels.addAll(
                myPlaceDataBase.getProgress(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username,
                    true
                )
            )
            if (menuNotificationModels.size > 0) {
                noNotificationTextView.visibility = View.GONE
                menuListView.visibility = View.VISIBLE
                sortNotifications()
                menuListView.bringToFront()
            } else {
                noNotificationTextView.visibility = View.VISIBLE
                noNotificationTextView.text = getString(R.string.no_notification_found)
                menuListView.visibility = View.GONE
            }
        } else if (photo && progress && !stage) {
            menuNotificationModels.addAll(
                myPlaceDataBase.getPhotoNotifications(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username
                )
            )
            menuNotificationModels.addAll(
                myPlaceDataBase.getProgress(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username,
                    false
                )
            )
            if (menuNotificationModels.size > 0) {
                noNotificationTextView.setVisibility(View.GONE)
                menuListView.setVisibility(View.VISIBLE)
                sortNotifications()
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE)
                noNotificationTextView.bringToFront()
                noNotificationTextView.setText("No Notifications to display")
                menuListView.setVisibility(View.GONE)
            }
        } else if (photo && !progress && stage) {
            menuNotificationModels.addAll(
                myPlaceDataBase.getPhotoNotifications(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username
                )
            )
            menuNotificationModels.addAll(
                myPlaceDataBase.getProgress(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username,
                    true
                )
            )
            if (menuNotificationModels.size > 0) {
                noNotificationTextView.setVisibility(View.GONE)
                menuListView.setVisibility(View.VISIBLE)
                sortNotifications()
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE)
                noNotificationTextView.bringToFront()
                noNotificationTextView.setText("No Notifications to display")
                menuListView.setVisibility(View.GONE)
            }
        } else if (!photo && progress && stage) {
            menuNotificationModels.addAll(
                myPlaceDataBase.getProgress(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username,
                    false
                )
            )
            menuNotificationModels.addAll(
                myPlaceDataBase.getProgress(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username,
                    true
                )
            )
            if (menuNotificationModels.size > 0) {
                noNotificationTextView.setVisibility(View.GONE)
                menuListView.setVisibility(View.VISIBLE)
                sortNotifications()
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE)
                noNotificationTextView.bringToFront()
                noNotificationTextView.setText("No Notifications to display")
                menuListView.setVisibility(View.GONE)
            }
        } else if (photo && !progress && !stage) {
            menuNotificationModels.addAll(
                myPlaceDataBase.getPhotoNotifications(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username
                )
            )
            if (menuNotificationModels.size > 0) {
                noNotificationTextView.setVisibility(View.GONE)
                menuListView.setVisibility(View.VISIBLE)
                sortNotifications()
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE)
                noNotificationTextView.bringToFront()
                noNotificationTextView.setText("No photo Notifications to display")
                menuListView.setVisibility(View.GONE)
            }
        } else if (!photo && progress && !stage) {
            menuNotificationModels.addAll(
                myPlaceDataBase.getProgress(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username,
                    false
                )
            )
            if (menuNotificationModels.size > 0) {
                menuListView.setVisibility(View.VISIBLE)
                noNotificationTextView.setVisibility(View.GONE)
                sortNotifications()
            } else {
                noNotificationTextView.setVisibility(View.VISIBLE)
                noNotificationTextView.bringToFront()
                noNotificationTextView.setText("No progress Notifications to display")
                menuListView.setVisibility(View.GONE)
            }
        } else if (!photo && !progress && stage) {
            menuNotificationModels.addAll(
                myPlaceDataBase.getProgress(
                    controller.my_Place_Details.jobNumber,
                    controller.my_Place_Details.username,
                    true
                )
            )
            if (menuNotificationModels.size > 0) {
                sortNotifications()
            } else {
                noNotificationTextView.setText("No Stage change Notifications to display")
                menuListView.setVisibility(View.GONE)
            }
        } else {
            noNotificationTextView.setVisibility(View.VISIBLE)
            noNotificationTextView.setText("Notifications settings are not enabled, please enable preferred notifications from settings.")
            menuListView.setVisibility(View.GONE)
        }
        notificationListAdapter.setNotifications(menuNotificationModels)
        var readExist = false
        for (i in menuNotificationModels.indices) {
            if (!menuNotificationModels[i].isRead) {
                readExist = true
                break
            }
        }
    }

    private fun sortNotifications() {
        val minutesModels = ArrayList<NotificationModel>()
        val daysModels = ArrayList<NotificationModel>()
        val basicModels = ArrayList(menuNotificationModels)
        for (i in basicModels.indices) {
            val model1 = basicModels[i]
            if (model1.days == 0) {
                minutesModels.add(model1)
            } else {
                daysModels.add(model1)
            }
        }
        daysModels.sortBy { it.days }
        minutesModels.sortBy { it.minutes }
        menuNotificationModels.clear()
        menuNotificationModels.addAll(minutesModels)
        menuNotificationModels.addAll(daysModels)
    }

}