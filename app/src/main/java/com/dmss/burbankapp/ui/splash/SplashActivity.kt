package com.dmss.burbankapp.ui.splash

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.databinding.ActivitySplashBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.ui.login.ChooseOptionsActivity
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.NetworkConnection
import com.dmss.burbankapp.utils.customviews.AppEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber


class SplashActivity : BaseActivity() {
    private val SPLASH_TIME_OUT: Long = 3000
    lateinit var binding: ActivitySplashBinding
    lateinit var customSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.app_bg))
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customSharedPreferences = CustomSharedPreferences(this)
        val fromNotification = intent.getBooleanExtra("from_notification", false)
        Log.d("NotificationClick", ""+fromNotification)

        if (fromNotification) {
            // ✅ This was triggered from notification click
            Log.d("NotificationClick", "Opened from notification")

            // You can show a toast, navigate, or log analytics
            Toast.makeText(this, "Opened from notification", Toast.LENGTH_SHORT).show()
        }
        val networkConnectionLiveData = NetworkConnection(applicationContext)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            generateKeyHash()
        }
        networkConnectionLiveData.observe(this, androidx.lifecycle.Observer { isConnected ->
            if (isConnected) {
                Timber.e("Connected")
                dismissNoInternetDialog()
            } else {
                Timber.e("DisConnected")
                showNoInterNetDialog()
            }


        })
        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity
            // startActivity(Intent(this, LoginHomeActivity::class.java))

            if (customSharedPreferences.getUserLoggedIn()) {

                /*val appEvent = AppEvent(AppEvent.SIGNUP, null)
                EventBus.getDefault().post(appEvent)*/

                val loginSuccess = Intent("LoginSuccess")
                LocalBroadcastManager.getInstance(this).sendBroadcast(loginSuccess)

                val signUpIntent = Intent(this, DashboardActivity::class.java)
                signUpIntent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                signUpIntent.putExtra(AppConstants.USER_CREATED, false)
                startActivity(signUpIntent)
            } else {
                val intent = Intent(this, ChooseOptionsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }


            // close this activity

        }, SPLASH_TIME_OUT)

    }

    /*private fun checkIfAlreadyHavePermission(): Boolean {
        val result =
            ContextCompat.checkSelfPermission(this, Manifest.permission.INTERACT_ACROSS_USERS_FULL)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForSpecificPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.INTERACT_ACROSS_USERS_FULL),
            101
        )
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            101 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (customSharedPreferences.getUserLoggedIn()) {

                    *//*val appEvent = AppEvent(AppEvent.SIGNUP, null)
                    EventBus.getDefault().post(appEvent)*//*

                    val loginSuccess = Intent("LoginSuccess")
                    LocalBroadcastManager.getInstance(this).sendBroadcast(loginSuccess)

                    val signUpIntent = Intent(this, DashboardActivity::class.java)
                    signUpIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    signUpIntent.putExtra(AppConstants.USER_CREATED, false)
                    startActivity(signUpIntent)


                } else {
                    val intent = Intent(this, ChooseOptionsActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            } else {
                checkIfAlreadyHavePermission()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAppEvent(event: AppEvent) {


    }

    override fun getTheme(): Resources.Theme {
        return super.getTheme()
    }


}
