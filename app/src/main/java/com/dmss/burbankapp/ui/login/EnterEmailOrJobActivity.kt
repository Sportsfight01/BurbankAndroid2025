package com.dmss.burbankapp.ui.login

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.CheckEmailModel
import com.dmss.burbankapp.databinding.ActivityEnterEmailOrJobBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.splash.SpalashViewModel
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.viewmodel.CheckUserEmailViewModel
import com.dmss.burbankappold.network.ApiRepository
import com.dmss.burbankappold.utils.AppConstants
import com.google.android.material.snackbar.Snackbar
import common.AppController
import common.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EnterEmailOrJobActivity : BaseActivity() {


    lateinit var checkUserEmailViewModel: CheckUserEmailViewModel
    lateinit var binding: ActivityEnterEmailOrJobBinding
    lateinit var spalashViewModel: SpalashViewModel

    lateinit var customSharedPreferences: CustomSharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.white))
        binding = ActivityEnterEmailOrJobBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        binding.etJobNumber.setText("testdmss4445@gmail.com")

        // StatusBarUtil.setTransparent(this)
        customSharedPreferences = CustomSharedPreferences(this)

        val token: String? = CustomSharedPreferences.instance.getToken()
        val token2: String? = customSharedPreferences.getToken()


        setupViewModel()
        setupObserver()
        binding.ivBack.setOnClickListener{
            onBackPressed()
        }


        binding.tvNext.setOnClickListener {
            if (binding.etJobNumber.text.isNotEmpty()) {
                checkUserEmailViewModel.isEmailExist(binding.etJobNumber.text.toString())
            } else {
                showSnakbar(binding.tvNext, "Please enter email")
            }

        }

        binding.tvCreate.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))

        }


    }
    override fun onResume() {
        super.onResume()

        AppConstants.validateVersionCode(this) {

        }

        /* GlobalScope.launch (Dispatchers.IO){
             var currentVersionCode=spalashViewModel.checkVersionCodeAPi()
             val pInfo = packageManager.getPackageInfo(packageName, 0)
             val version = pInfo.versionName.toDouble()
             println("currentVersionCode:: $currentVersionCode version:: $version")

             if(version<currentVersionCode){
                 runOnUiThread( Runnable() {
                     AppUtils.showUpdateVewVersionAppDialog( this,"New version Available")
                 })
             }

         }*/
    }

    private fun setupViewModel() {
        checkUserEmailViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
            )
        ).get(CheckUserEmailViewModel::class.java)
        spalashViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        )[SpalashViewModel::class.java]

    }

    private fun showSnakbar(view: View, message: String) {
       /* val builder = AlertDialog.Builder(this@EnterEmailOrJobActivity)
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage(message)
        builder.setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { _, _ ->
            dialog.dismiss()
        }
        builder.show()*/
        AppUtils.showValidationAlert(this@EnterEmailOrJobActivity,message)
    }

    private fun setupObserver() {
        checkUserEmailViewModel.getEmailExistOrNot().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                   dismissProgressDialog()
                    var checkEmailModel: CheckEmailModel? = it.data
                    if (checkEmailModel != null) {
                        if (checkEmailModel.status) {
                            customSharedPreferences.saveEmail(
                                binding.etJobNumber.text.toString().trim()
                            )
                            startActivity(Intent(this, EnterPasswordActivity::class.java))
                            AppController.setLoginEmailID(binding.etJobNumber.text.toString())
                        } else {
                            showSnakbar(binding.tvNext, checkEmailModel.Message)
                        }

                    }
                }
                Status.LOADING -> {
                  showProgressDialog()

                }
                Status.ERROR -> {
                    //Handle Error
                   dismissProgressDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })


    }
}
