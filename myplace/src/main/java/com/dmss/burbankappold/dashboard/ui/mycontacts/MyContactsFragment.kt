package com.dmss.burbankappold.dashboard.ui.mycontacts

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.dmss.burbankappold.BaseFragment
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyContactsAdapter
import com.dmss.burbankappold.dashboard.ui.sideMenu.MyNotificationActivity
import com.dmss.burbankappold.databinding.MycontactsFragmentBinding
import com.dmss.burbankappold.utils.changeTextColor
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
import models.contacts.MyContactsResultDataData
import org.json.JSONException
import org.json.JSONObject

class MyContactsFragment : BaseFragment() {
    private var _binding: MycontactsFragmentBinding? = null
    private lateinit var controller: AppController
    private lateinit var mMyContactsAdapter: MyContactsAdapter

    private val binding get() = _binding!!
    private var pd: TransparentProgressDialog? = null
    private var dialog: TransparentProgressDialog? = null
//    private lateinit var skeletonScreen: SkeletonScreen
val list = arrayListOf<MyContactItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MycontactsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileHeader.tvHeading.text =getString(R.string.mycontacts)
        binding.profileHeader.tvSubHeading.text = getString(R.string.mycontact_desc)
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        binding.profileHeader.tvHeading.changeTextColor(context)
        binding.profileHeader.ivPhoto.loadUrlUsingGlide(PrefsHelper.profileUrl)
        initViews()
        getContactsDetails()

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun initViews() {
        controller = activity!!.applicationContext as AppController
        mMyContactsAdapter = MyContactsAdapter { type, contactItem ->
            when(type) {
                0 -> sendEmail(contactItem.email)
                else -> call(contactItem.phone)
            }
        }
        binding.swiperefresh.setOnRefreshListener {
            if(list.isEmpty() && Utils.isNetworkAvailable(activity)) {
                Utils.hideSwipeRefresh(binding.swiperefresh)
                getContactsDetails()
            }else{
                Utils.hideSwipeRefresh(binding.swiperefresh)
            }
        }
        binding.rvContacts.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mMyContactsAdapter
        }
       /* binding.profileHeader.tvNotificationsCount.visibility = if(PrefsHelper.notificationCount == "0" || PrefsHelper.notificationCount == "")
            View.GONE else View.VISIBLE
        binding.profileHeader.tvNotificationsCount.text = PrefsHelper.notificationCount*/
        if(PrefsHelper.notificationCount != "0" && PrefsHelper.notificationCount != "") {
            var notificationCount = PrefsHelper.notificationCount
            if (notificationCount != "" && notificationCount.toInt() > 100) {
                notificationCount = "99+"
            }
            binding.profileHeader.tvNotificationsCount.text = notificationCount
            binding.profileHeader.tvNotificationsCount.visibility = View.VISIBLE
        }
        binding.profileHeader.ivPhoto.setOnClickListener {
            startActivity(Intent(activity, MyNotificationActivity::class.java))
        }
    }

    private fun getContactsDetails() {
        if (controller.my_Place_Details != null) {
            if (Utils.isNetworkAvailable(activity)) {
                dialog = Utils.getProgress(activity)
                val t = Thread {
                   /* val result: String = controller.webApiCall().postData_to_MyPlace(
                        Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl,
                        getMyPlaceLoginJson()
                    )*/
//                    http://10.6.45.14:8085/myplace/api/survey/GetClientInfoForContractNumber?jobNumber=200902
                    val contactDetails: String = controller.webApiCall()
                        .getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetContactDetails + "" + AppController.controller.my_Place_Details.jobNumber)
                    if (contactDetails != null) {
                        if (contactDetails.contains("Error")) {
                            Handler(Looper.getMainLooper()).post {
                                if (contactDetails.contains("timeout")) {
                                    Toast.makeText(
                                        activity,
                                        "Error: Network is slow,\nPlease try again later",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        contactDetails,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                dialog?.cancel()
//                                        skeletonScreen.hide()
                            }
                        } else {
                            Handler(Looper.getMainLooper()).post {
                                dialog?.cancel()
//                                        skeletonScreen.hide()

                                onSuccessResult(contactDetails)
                            }
                        }
                    }
                 /*   if (result.equals("true", ignoreCase = true)) {
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
                                                activity,
                                                "Error: Network is slow,\nPlease try again later",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                contactDetails,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        dialog?.cancel()
//                                        skeletonScreen.hide()
                                    }
                                } else {
                                    Handler(Looper.getMainLooper()).post {
                                        dialog?.cancel()
//                                        skeletonScreen.hide()

                                        onSuccessResult(contactDetails)
                                    }
                                }
                            } else {
                                Handler(Looper.getMainLooper()).post {
                                    dialog?.cancel()
//                                    skeletonScreen.hide()

                                    Utils.showToast(
                                        activity,
                                        "My place details not valid for this job number",
                                        Common.errorCase
                                    )
                                }
                            }
                        }
                    } else if (result.equals("false", ignoreCase = true)) {
                        Handler(Looper.getMainLooper()).post {
                            dialog?.cancel()
//                            skeletonScreen.hide()

                            Utils.showToast(
                                activity,
                                "My place details not valid for this job number",
                                Common.errorCase
                            )
                        }
                    }
                    else {
                        Handler(Looper.getMainLooper()).post {
//                            skeletonScreen.hide()
//
                            dialog?.cancel()
                        }
                    }*/
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
        val myResContactsData = Gson().fromJson(contactDetails, MyContactsResultDataData::class.java)
        val myContactsData = myResContactsData.surveyDetails
        if(myContactsData!=null && myResContactsData.status) {
            val siteSuperVisor = MyContactItem(
                designation = "Site Manager",
                name = myContactsData.siteSupervisor,
                email = myContactsData.siteSupervisorEmail,
                phone = myContactsData.siteSupervisorPhone
            )
            val newHomeCoordinator = MyContactItem(
                designation = "Customer Care Coordinator",
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