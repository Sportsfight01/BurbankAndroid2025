package com.dmss.burbankapp.ui.login

import android.content.Intent
import android.os.Bundle
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
import com.dmss.burbankapp.data.model.LoginModel
import com.dmss.burbankapp.data.model.LoginStatus
import com.dmss.burbankapp.databinding.ActivityEnterPasswordBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.utils.customviews.AppEvent
import com.dmss.burbankapp.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import common.AppController
import org.greenrobot.eventbus.EventBus

class EnterPasswordActivity : BaseActivity() {
    lateinit var binding: ActivityEnterPasswordBinding

    lateinit var loginViewModel: LoginViewModel
    lateinit var customSharedPreferences: CustomSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.app_bg))
        binding = ActivityEnterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customSharedPreferences = CustomSharedPreferences(this)
        //StatusBarUtil.setTransparent(this)
        initUI()
        setupViewModel()
        setupObserver()


    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(LoginViewModel::class.java)
    }

    private fun initUI() {
        binding.ivBack.setOnClickListener {
            onBackPressed()

        }
//        binding.etPassword.setText("Digital@123")

        binding.tvUsername.text = AppController.getLoginEmailID()

        binding.tvLogint.setOnClickListener {

            if (binding.etPassword.text.toString().isNotEmpty() && binding.tvUsername.text.toString()
                    .isNotEmpty()
            ) {

                loginViewModel.loginApi(binding.tvUsername.text.toString(), binding.etPassword.text.toString())


            } else {
                AppUtils.showValidationAlert(this, "Please enter password")
                // showSnakbar("Please enter Password")
            }

        }
        binding.llForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun setupObserver() {
        loginViewModel.login().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    val loginModel: LoginModel? = it.data
                    if (loginModel != null) {
                        if (loginModel.Status) {
                            if (binding.etPassword.text.toString().isNotEmpty()) {
                                customSharedPreferences.saveLoginPassword(
                                    binding.etPassword.text.toString().trim()
                                )
                            }
                            customSharedPreferences.setLoginType("email")
                            customSharedPreferences.setUserLoggedIn(true)
                            customSharedPreferences.saveUserId(loginModel.UserId)
                            AppUtils.showCustomCenterToast(this, loginModel.Message)

                            //showSnakbar(loginModel.Message)
                            loginModel.loginStatus = LoginStatus.EMAIL_LOGIN
                            val signUpIntent = Intent(this, DashboardActivity::class.java)
                            signUpIntent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            signUpIntent.putExtra(AppConstants.USER_CREATED, true)
                            startActivity(signUpIntent)
                            val appEvent = AppEvent(AppEvent.SIGNUP, null)
                            EventBus.getDefault().post(appEvent)
                        } else {
                            AppUtils.showValidationAlert(this, loginModel.Message)
                            // showSnakbar(loginModel.Message)
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

    fun showSnakbar(message: String) {
        val snack = Snackbar.make(binding.tvLogint, message, Snackbar.LENGTH_LONG)
        snack.show()
    }
}
