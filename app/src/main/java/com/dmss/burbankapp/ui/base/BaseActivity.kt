package com.dmss.burbankapp.ui.base

import android.app.Dialog
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.dmss.burbankapp.R
import com.dmss.burbankapp.ui.view.CustomProgressDialog
import com.dmss.burbankapp.ui.view.CustomUnAutharizedDialog
import com.dmss.burbankapp.ui.view.FavoritesNotAvailable
import com.dmss.burbankapp.utils.NetworkConnection
import timber.log.Timber
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


open class BaseActivity : AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog;
    var customUnAutharizedDialog: CustomUnAutharizedDialog? = null
    var favoritesNotAvailable: FavoritesNotAvailable? = null
    var customProgressDialog: CustomProgressDialog? = null
    lateinit var dialog: Dialog;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = ProgressDialog(this);
        progressDialog.setMessage("Loading..")
        progressDialog.setCanceledOnTouchOutside(false)
        customUnAutharizedDialog = CustomUnAutharizedDialog(this)
        favoritesNotAvailable = FavoritesNotAvailable(this)
        customUnAutharizedDialog?.setCanceledOnTouchOutside(false)
        customProgressDialog = CustomProgressDialog(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            generateKeyHash()
        }
        dialog = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.layout_no_internet)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        val networkConnectionLiveData = NetworkConnection(applicationContext)
        networkConnectionLiveData.observe(this, Observer { isConnected ->
            if (isConnected) {
                Timber.e("Connected")
                dismissNoInternetDialog()
            } else {
                Timber.e("DisConnected")
                showNoInterNetDialog()
            }
        })

        /*val connectionLiveData = ConnectionLiveData(this)
        connectionLiveData.observe(this, Observer {
            if (it != null) {
                if (it.isConnected) {
                    Timber.e("Connected")
                    dismissNoInternetDialog()
                } else {
                    Timber.e("DisConnected")
                    showNoInterNetDialog()
                }
            }
        })*/

    }

    open fun showNoInterNetDialog() {
        if (!dialog.isShowing)
            dialog.show()
    }

    open fun dismissNoInternetDialog() {
        if (dialog.isShowing)
            dialog.dismiss()
    }

    fun showProgressDialog() {
        customProgressDialog?.showProgress()
    }

    fun dismissProgressDialog() {
        customProgressDialog?.dismissProgress()

    }

    @RequiresApi(Build.VERSION_CODES.P)
    open fun generateKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                "com.dmss.burbankapp",
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            for (signature in info.signingInfo.apkContentsSigners) {
                val md =
                    MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Timber.e(
                    "KeyHash  ${
                        Base64.encodeToString(
                            md.digest(),
                            Base64.DEFAULT
                        )
                    }"
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    }

    open fun changeStatusBarColor(color: Int) {
        val window: Window = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            window.setStatusBarColor(color)
        }
    }


    open fun makeFullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    fun showCustomDialog() {
        Timber.e("CustomDialog")
        customUnAutharizedDialog?.let {
            if (!it.isShowing) {
                it.show()
            }
        }

    }

    fun showNoFavoriteDialog() {
        Timber.e("CustomDialog")
        favoritesNotAvailable?.let {
            if (!it.isShowing) {
                it.show()
            }
        }

    }

    open fun changeStatusBarColor(color: Color) {
        changeStatusBarColor(color)
    }

    companion object {
        fun changeStatusBarColor(baseActivity: BaseActivity, color: Int) {
            val window = baseActivity.window
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = color
            }
        }
    }


}