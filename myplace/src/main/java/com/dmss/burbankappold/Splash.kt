package com.dmss.burbankappold

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.dmss.burbankappold.dashboard.DashboardNewActivity
import com.dmss.burbankappold.dashboard.PrefsHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import common.AppController
import common.Common
import common.Utils
import interfaces.WebApiResponseCallback
import kotlinx.coroutines.*
import models.MyPlaceCredentials
import models.profile.UserJobProfile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by Ashish.Kumar on 16-12-2016.
 */
class Splash : BaseActivity() {
    var isPermissionRequested = false
    var isAlertShown = false
    var requestPermission = 1
    var isIntentCalled = false
    var logo: ImageView? = null
    var dialog: AlertDialog? = null
    var controller: AppController = AppController.controller
    var updateDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PrefsHelper.init(this)
        //line for removing top title bar from screen////////////
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        ///////line for opening screen in full mode///////////////
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        setContentView(R.layout.splash)
        initializeUi()
    }

    /*************
     * method for activity switch
     */
    private fun switchToNextActivity(callback: (Boolean) -> Unit) {
        if (!controller.isUserLoggedIn) {
            startActivity(Intent(this, FirstClass::class.java))
        } else {
            if (controller.userProfile.userDetailses == null || !controller.userProfile.userDetails[0].isMyPlaceAccessible) {
                startActivity(Intent(this, DashboardNewActivity::class.java))
            } else {
                callback.invoke(true)
            }
        }
        finish()
    }

    private fun updateUI(result1: String?) {
        onSuccessResult(result1){
            if (it){
                startActivity(Intent(this, DashboardNewActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            callAPi()
        }
    }

    private fun initializeUi() {
        logo = findViewById<View>(R.id.imageView) as ImageView
        logo!!.startAnimation(controller.fadeInAnimationInstance)
    }

    private suspend fun callAPi(){
        val result1 = withContext(Dispatchers.IO){
            getAPI()
        }
        updateUI(result1)
        /*lifecycleScope.launch {
            val result = async { getAPI()}
            async { callProfileAPi()}
            onSuccessResult(result.toString())
        }*/
    }

  /*  private fun parseAPIResponse(result: String?, callback: (Boolean) -> Unit){
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.getBoolean(Common.Status_Key)) {
                val job = jsonObject.getJSONObject(Common.Result_Key)
                if (job.getBoolean(Common.Sucess_Key)) {
                    controller.setProfileInfo(job.toString())
                    val gson = GsonBuilder().create()
                    val profile: UserJobProfile = gson.fromJson(job.toString(), UserJobProfile::class.java)
                    AppController.controller.setUserJobProfile(profile)
                    AppController.controller.setUserJobDetail(profile.UserDetails)
                    AppController.controller.myPlaceDetail = profile.UserDetails[0].MyPlaceDetails
                    val myPlaceDetails = profile.UserDetails[0].MyPlaceDetails[0]
                    val myPlaceCredentials = MyPlaceCredentials(
                        myPlaceDetails.Region,
                        myPlaceDetails.JobNo,
                        myPlaceDetails.UserName,
                        myPlaceDetails.Password
                    )
                    AppController.controller.my_Place_Details = myPlaceCredentials
                    callback.invoke(true)
                } else {
                    Utils.showToast(
                        this@Splash,
                        jsonObject.getString(Common.Message),
                        Common.errorCase
                    )
                }
            } else {
                Utils.showToast(
                    this@Splash,
                    jsonObject.getString(Common.Message),
                    Common.errorCase
                )
            }
        } catch (jsonException: JSONException) {
            jsonException.printStackTrace()
        }
    }*/

    private suspend fun getAPI() = withContext(Dispatchers.IO){
        AppController.controller.webApiCall().getData_From_MyPlace(Common.maintenanceUrl)
    }

    val json: String
        get() {
            val job = JSONObject()
            try {
                job.put("Email", controller.userProfile.userDetails[0].email)
            } catch (ex: Exception) {
                ex.fillInStackTrace()
            }
            return job.toString()
        }

    fun onSuccessResult(result: String?, callback: (Boolean) -> Unit) {
        Handler(Looper.getMainLooper()).post {
            try {
                Log.e("This maintenance test", result)
                val jsonArray = JSONArray(result)
                val maintenanceObject = JSONObject()
                var displayText = ""
                var updateCheck = false
                var maintenance = false
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val groupName =
                        if (jsonObject.isNull("GroupName")) "" else jsonObject.getString("GroupName")
                    val contentName =
                        if (jsonObject.isNull("ContentName")) "" else jsonObject.getString("ContentName")
                    val contentValue =
                        if (jsonObject.isNull("ContentValue")) "" else jsonObject.getString("ContentValue")
                    val isActive =
                        if (jsonObject.isNull("isActive")) false else jsonObject.getBoolean("isActive")
                    when (groupName.lowercase()) {
                        "notice" -> when (contentName.lowercase()) {
                            "available" -> if (contentValue.equals("true", ignoreCase = true)) {
                                updateCheck = true
                            }
                            "message" -> displayText = contentValue
                        }
                        "maintenance" -> when (contentName.lowercase()) {
                            "undermaintenance" -> if (contentValue.equals(
                                    "true",
                                    ignoreCase = true
                                )
                            ) {
                                maintenance = true
                            }
                            "title" -> maintenanceObject.put("Title", contentValue)
                            "message" -> maintenanceObject.put("Message", contentValue)
                            "startdatetime" ->                                         //10/31/2019 15:30:00 this is the format given by server.
                                maintenanceObject.put("StartDateTime", contentValue)
                            "enddatetime" ->                                         //10/31/2019 15:30:00 this is the format given by server.
                                maintenanceObject.put("EndDateTime", contentValue)
                        }
                        "pcinspection" -> if (contentName.equals("Available", ignoreCase = true)
                            && contentValue.equals("true", ignoreCase = true)
                        ) {
                            controller!!.isShowPcInspection = true
                        }
                    }
                }
                if (updateCheck) {
                    if (displayText.length > 0) {
                        displayProfileDialog(displayText, callback)
                    } else if (maintenance) {
                        controller!!.maintenanceObject = maintenanceObject
                        startActivity(Intent(this@Splash, MaintenanceActivity::class.java))
                        overridePendingTransition(0, 0)
                        finish()
                    } else {
                        switchToNextActivity(callback)
                    }
                } else if (maintenance) {
                    controller!!.maintenanceObject = maintenanceObject
                    startActivity(Intent(this@Splash, MaintenanceActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                } else {
                    switchToNextActivity(callback)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                switchToNextActivity(callback)
            }
        }
    }

    private fun displayProfileDialog(display: String?, callback: (Boolean) -> Unit) {
        updateDialog = Dialog(this@Splash)
        updateDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        updateDialog?.let { it ->
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            updateDialog!!.setCanceledOnTouchOutside(false)
            updateDialog!!.setContentView(R.layout.app_update_dialog)
            val updateMessageTextView =
                updateDialog!!.findViewById<View>(R.id.updateMessageTextView) as TextView
            updateMessageTextView.text = display
            val notNowButton = updateDialog!!.findViewById<View>(R.id.notNowButton) as Button
            notNowButton.typeface = controller!!.typeface
            val updateAppButton = updateDialog!!.findViewById<View>(R.id.updateAppButton) as Button
            updateAppButton.typeface = controller!!.typeface
            val fingerPrintCheckBox =
                updateDialog!!.findViewById<View>(R.id.fingerPrintCheckBox) as CheckBox
            notNowButton.setOnClickListener {
                updateDialog!!.dismiss()
                switchToNextActivity(callback)
            }
            updateAppButton.setOnClickListener {
                val appPackageName = packageName // getPackageName() from Context or Activity object
                try {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "market://details?id=$appPackageName"
                            )
                        )
                    )
                } catch (anfe: ActivityNotFoundException) {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "https://play.google.com/store/apps/details?id=$appPackageName"
                            )
                        )
                    )
                }
            }
            updateDialog!!.show()
        }
    }

}