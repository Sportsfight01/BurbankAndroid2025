package com.dmss.burbankapp.ui.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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
import com.dmss.burbankapp.data.model.ForgetPasswordModel
import com.dmss.burbankapp.data.model.ResetPasswordModel
import com.dmss.burbankapp.databinding.ActivityResetPasswordBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.resetpassword.ResetPasswordViewModel
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.viewmodel.ForgetPasswordViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import common.AppController
import common.Utils


class ResetPasswordActivity : BaseActivity() {
    lateinit var binding: ActivityResetPasswordBinding
    lateinit var resetPasswordViewModel: ResetPasswordViewModel
    lateinit var customSharedPreferences: CustomSharedPreferences
    lateinit var forgetPasswordViewModel: ForgetPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.white))
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // StatusBarUtil.setTransparent(this)
        customSharedPreferences = CustomSharedPreferences(this)
        setViewModel()

        initViews()
    }

    private fun setViewModel() {
        resetPasswordViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(AppController.getInstance()))
            )
        ).get(ResetPasswordViewModel::class.java)

        forgetPasswordViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(ForgetPasswordViewModel::class.java)

        setUpObserver()
    }
  /*  private fun showSuccessAlert(context: Context,activity:Activity, message: String){
        val builder = AlertDialog.Builder(context)
//           builder.setTitle(context.getString(R.string.app_name))
        var myView =  TextView(context);
        myView.text = context.getString(R.string.app_name)
        myView.textSize = 25F;
        myView.gravity = Gravity.CENTER;
        myView.setPadding(0, 20, 0, 20);

        myView.setTextColor(context.getColor(R.color.black_bg_3_1))
        builder.setCustomTitle(myView);
        builder.setMessage(message)
        builder.setPositiveButton(Html.fromHtml("<font color='${context.resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { _, _ ->
            builder.create().dismiss()
            startActivity(Intent(this, EnterPasswordActivity::class.java))
            finish()
        }
        val  dialog = builder.create();
        dialog.show();
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.weight = 1.0f
        layoutParams.gravity = Gravity.CENTER
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize=18f
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).layoutParams = layoutParams;
        dialog!!.window!!.attributes = dialog!!.window!!.attributes.apply { dimAmount = 0F }
        val messageView = dialog.findViewById<View>(android.R.id.message) as TextView
        messageView.gravity = Gravity.CENTER
    }*/
    private fun setUpObserver() {
        resetPasswordViewModel.getResetPasswordData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                   dismissProgressDialog()
                    val resetPassWordModel: ResetPasswordModel? = it.data
                    if (resetPassWordModel != null) {

                        if (resetPassWordModel.status) {
                            var intent=Intent(this, EnterPasswordActivity::class.java)
                           AppUtils.showSuccessAlert(this,intent,resetPassWordModel.message)
                            //showSnakbar(resetPassWordModel.message)

                        } else {
                            AppUtils.showCustomCenterToast(this,resetPassWordModel.message)
                           // showSnakbar(resetPassWordModel.message)
                        }
                    }
                }
                Status.LOADING -> {
                  showProgressDialog()
                }
                Status.ERROR -> {
                   dismissProgressDialog()
                }
            }
        })

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

    fun otpDialog(message:String) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { dialog, id ->
                dialog.dismiss()
            }


        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("My Place")
        // show alert dialog
        alert.show()

    }

    private fun initViews() {
        binding.ivBack.setOnClickListener{
            onBackPressed()
        }

        binding.resendPasscode.setOnClickListener {
            forgetPasswordViewModel.fetchForgetPassword(AppController.getLoginEmailID())
        }

        binding.tvSubmit.setOnClickListener {

            if (isEmpty(binding.passcode)) {
                if (isEmpty(binding.enternewpassword)) {
                    if (isPasswordLength(binding.enternewpassword)) {
                        if (isEmpty(binding.enterconfirmpassword)) {

//                            if (isPasswordLength(binding.enterconfirmpassword)) {
                                if (validateTwoPasswords() && Utils.isNetworkAvailableWithError(this)) {
                                    resetPasswordApi()
                                } else {
                                    //AppUtils.showCustomCenterToast(this,"New password & Confirm Passwords are not matching"e)
                                    showSnakbar("New password & Confirm Passwords should be same")
                                }

//                            } else {
//                                showSnakbar("Please enter minimum 6 characters for confirm password")
//                            }

                        } else {
                            showSnakbar("Please enter Confirm Password")
                        }
                    } else {
                        showSnakbar("Please enter minimum 6 characters for new password")
                    }
                } else {
                    showSnakbar("Please enter New Password")
                }


            } else {
                showSnakbar("Please enter Passcode")
            }
        }
    }

    private fun resetPasswordApi() {
        var jsonObject = JsonObject()

        var emailId: String? = customSharedPreferences.getEmailId()
        if (emailId != null) {
            if (emailId.isNotEmpty()) {
                jsonObject.addProperty("EmailId", emailId)
                jsonObject.addProperty("Passcode", binding.passcode.text.toString().trim())
                jsonObject.addProperty(
                    "NewPassword",
                    binding.enterconfirmpassword.text.toString().trim()
                )
                resetPasswordViewModel.setResetPassword(jsonObject)
            }
        }


    }


    fun validateTwoPasswords(): Boolean {
        val password = binding.enternewpassword.text.toString().trim()
        val confirmPassword = binding.enterconfirmpassword.text.toString().trim()
        return password == confirmPassword
    }

    private fun isPasswordLength(editText: EditText): Boolean {
        val str: CharSequence = editText.text.toString()
        return str.length >= 6

    }

    private fun isEmpty(text: EditText): Boolean {
        val str: CharSequence = text.text.toString()
        return str.isNotEmpty()
    }

    private fun showSnakbar(message: String) {
       /* val snack = Snackbar.make(binding.tvSubmit, message, Snackbar.LENGTH_LONG)
        snack.show()*/
//        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        AppUtils.showValidationAlert(this@ResetPasswordActivity,message)

    }


}
