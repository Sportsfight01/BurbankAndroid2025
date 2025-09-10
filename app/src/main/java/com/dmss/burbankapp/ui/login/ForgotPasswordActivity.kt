package com.dmss.burbankapp.ui.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.api.ApiHelperImpl
import com.dmss.burbankapp.data.api.RetrofitBuilder
import com.dmss.burbankapp.data.apiUtils.Status
import com.dmss.burbankapp.data.apiUtils.ViewModelFactory
import com.dmss.burbankapp.data.local.entity.DatabaseBuilder
import com.dmss.burbankapp.data.local.entity.DatabaseHelperImpl
import com.dmss.burbankapp.data.model.ForgetPasswordModel
import com.dmss.burbankapp.databinding.ActivityForgotPasswordBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.viewmodel.ForgetPasswordViewModel
import com.google.gson.Gson
import common.AppController

class ForgotPasswordActivity : BaseActivity() {

    lateinit var forgetPasswordViewModel: ForgetPasswordViewModel
    lateinit var binding:ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.white))
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //StatusBarUtil.setTransparent(this)
        binding.tvNext.setOnClickListener {
            forgetPasswordViewModel.fetchForgetPassword(AppController.getLoginEmailID())
        }
        binding.tvEmail.text = AppController.getLoginEmailID()
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }


        setUpViewModel()
        setupObserver()
    }

    private fun setUpViewModel() {
        forgetPasswordViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(ForgetPasswordViewModel::class.java)
    }

    fun otpDialog(message:String) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { dialog, id ->
                startActivity(Intent(this, ResetPasswordActivity::class.java))
                dialog.dismiss()

            }


        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("My Place")
        // show alert dialog
        alert.show()

        val buttonbackground: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonbackground.setTextColor(Color.BLACK)
    }

    private fun setupObserver() {
        forgetPasswordViewModel.getForgetPasswordLiveData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                   dismissProgressDialog()
                    var forgetPasswordModel: ForgetPasswordModel? = it.data
                    if (forgetPasswordModel != null) {
                        if (forgetPasswordModel.status) {
                            otpDialog(forgetPasswordModel.Info.Message)
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
