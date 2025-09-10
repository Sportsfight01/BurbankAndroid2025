package com.dmss.burbankappold.dashboard.ui.sideMenu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.databinding.ActivityMyContactsBinding
import com.dmss.burbankappold.utils.hide
import com.dmss.burbankappold.utils.loadUrlUsingGlide
import com.dmss.burbankappold.utils.show
import com.google.gson.Gson
import common.AppController
import common.Common
import common.TransparentProgressDialog
import common.Utils
import models.contacts.MyContactItem
import models.contacts.MyContactsData
import org.json.JSONException
import org.json.JSONObject

class MyContactsActivity : AppCompatActivity() {
    private lateinit var mBinding : ActivityMyContactsBinding
    private lateinit var mMyContactsAdapter: MyContactsAdapter
    private var pd: TransparentProgressDialog? = null
    private var dialog: TransparentProgressDialog? = null
    private lateinit var controller: AppController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMyContactsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        controller = applicationContext as AppController
        initViews()
       // requestApiCall()
        getContactsDetails()
    }

    private fun initViews() {
        initToolBar()
        mBinding.headerLayout.tvHeading.text = getString(R.string.mycontacts)
        mBinding.headerLayout.tvSubHeading.text = getString(R.string.mycontact_desc)
        mBinding.headerLayout.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        mMyContactsAdapter = MyContactsAdapter { type, contactItem ->
            when(type) {
                0 -> sendEmail(contactItem.email)
                else -> call(contactItem.phone)
            }
        }
        mBinding.rvContacts.apply {
            layoutManager = LinearLayoutManager(this@MyContactsActivity)
            adapter = mMyContactsAdapter
        }
        mBinding.headerLayout.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
        mBinding.headerLayout.tvNotificationsCount.text = PrefsHelper.notificationCount
        mBinding.headerLayout.ivPhoto.setOnClickListener {
            startActivity(Intent(this, MyNotificationActivity::class.java))
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

    private fun getContactsDetails() {
        if (controller.my_Place_Details != null) {
            if (Utils.isNetworkAvailable(this)) {
                dialog = Utils.getProgress(this)
                val t = Thread {
                    val result: String = controller.webApiCall().postData_to_MyPlace(
                        Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl,
                        getMyPlaceLoginJson()
                    )
                    if (result.equals("true", ignoreCase = true)) {
                        val result2: String = controller.webApiCall()
                            .getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetUserDetailsUrl)
                        if (result2 != null && !result2.equals("null", ignoreCase = true)) {
                            val contactDetails: String = controller.webApiCall()
                                .getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetContactDetails + "" + AppController.controller.my_Place_Details.jobNumber)
                            if (contactDetails != null) {
                                if (contactDetails.contains("Error")) {
                                    Handler(Looper.getMainLooper()).post {
                                        if (contactDetails.contains("timeout")) {
                                            Toast.makeText(
                                                this,
                                                "Error: Network is slow,\nPlease try again later",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                contactDetails,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        dialog?.cancel()
                                    }
                                } else {
                                    Handler(Looper.getMainLooper()).post {
                                        dialog?.cancel()
                                        onSuccessResult(contactDetails)
                                    }
                                }
                            } else {
                                Handler(Looper.getMainLooper()).post {
                                    dialog?.cancel()
                                    Utils.showToast(
                                        this,
                                        "My place details not valid for this job number",
                                        Common.errorCase
                                    )
                                }
                            }
                        }
                    } else if (result.equals("false", ignoreCase = true)) {
                        Handler(Looper.getMainLooper()).post {
                            dialog?.cancel()
                            Utils.showToast(
                                this,
                                "My place details not valid for this job number",
                                Common.errorCase
                            )
                        }
                    } else {
                        Handler(Looper.getMainLooper()).post { dialog?.cancel() }
                    }
                }
                t.start()
            } else {
                controller.contactsModelDetails = null
            }
        } else {
            controller.contactsModelDetails = null
        }
    }

    private fun onSuccessResult(contactDetails: String) {
        val myContactsData = Gson().fromJson(contactDetails, MyContactsData::class.java)
        val list = arrayListOf<MyContactItem>()
        val siteSuperVisor = MyContactItem(
            designation = "Site Supervisor",
            name = myContactsData.siteSupervisor,
            email = myContactsData.siteSupervisorEmail,
            phone = myContactsData.siteSupervisorPhone
        )
        val newHomeCoordinator = MyContactItem(
            designation = "New Home Coordinator",
            name = myContactsData.cro,
            email = myContactsData.croEmail,
            phone = myContactsData.croPhone
        )
        val interiorDesigner = MyContactItem(
            designation = "Interior Designer",
            name = myContactsData.interiorDesigner,
            email = myContactsData.interiorDesignerEmail,
            phone = myContactsData.interiorDesignerPhone
        )
        val electricalEngineer = MyContactItem(
            designation = "Electrical Designer",
            name = myContactsData.electricalConsultant,
            email = myContactsData.electricalConsultantEmail,
            phone = myContactsData.electricalConsultantPhone
        )
        val newHomeConsultant = MyContactItem(
            designation = "New Home Consultant",
            name = myContactsData.newHomeConsultant,
            email = myContactsData.newHomeConsultantEmail,
            phone = myContactsData.newHomeConsultantPhone
        )
        list.add(siteSuperVisor)
        list.add(newHomeCoordinator)
        list.add(interiorDesigner)
        list.add(electricalEngineer)
        list.add(newHomeConsultant)
        mMyContactsAdapter.setMyContactsData(list)
    }

    fun getMyPlaceLoginJson(): String? {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("Region", controller.my_Place_Details.region)
            jsonObject.put("JobNumber", controller.my_Place_Details.jobNumber)
            jsonObject.put("UserName", controller.my_Place_Details.username)
            jsonObject.put("Password", controller.my_Place_Details.password)
        } catch (ex: JSONException) {
            ex.fillInStackTrace()
        }
        return jsonObject.toString()
    }

    private fun sendEmail(mail: String?) {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mail, null
            )
        )
        emailIntent.data = Uri.parse("mailto:"); // only email apps should handle this

        var name = ""
        name =
            if (controller.userProfile.userDetails.size > 0 && controller.userProfile.userDetails[0].fullName != null) {
                " - " + controller.userProfile.userDetails[0].fullName
            } else {
                ""
            }
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            "MyPlace – " + controller.my_Place_Details.jobNumber + name
        )
        emailIntent.putExtra(
            Intent.EXTRA_TEXT,
            "\n\n\n\n* Kindly do not change the subject to track your queries."
        )
        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }

    private fun call(mobile: String?) {
        if (mobile != null && !mobile.equals("null", ignoreCase = true) && mobile.isNotEmpty()) {
            val test2: String = if (mobile.length > 10) {
                val test = mobile.substring(1)
                "+61$test"
            } else {
                "+61$mobile"
            }
            startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", test2, null)))
        }
    }

}