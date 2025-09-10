package com.dmss.burbankapp.ui.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
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
import com.dmss.burbankapp.data.model.SignUpModel
import com.dmss.burbankapp.data.model.UserInfoModel
import com.dmss.burbankapp.databinding.ActivitySignUpBinding
import com.dmss.burbankapp.ui.base.BaseActivity
import com.dmss.burbankapp.ui.dashboard.DashboardActivity
import com.dmss.burbankapp.utils.AppConstants
import com.dmss.burbankapp.utils.AppUtils
import com.dmss.burbankapp.viewmodel.SignUpViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import common.AppController


class SignUpActivity : BaseActivity() {
    lateinit var dialogBuilder: AlertDialog.Builder
    lateinit var binding: ActivitySignUpBinding
    lateinit var customSharedPreferences: CustomSharedPreferences

    lateinit var signUpViewModel: SignUpViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeStatusBarColor(ContextCompat.getColor(this, R.color.white))
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customSharedPreferences = CustomSharedPreferences(this)
        initUI()
        setupViewModel()
        setupObserver()


    }

    private fun showSnakbar(message: String) {
       /* val snack = Snackbar.make(binding.tvSignup, message, Snackbar.LENGTH_LONG)
        snack.show()*/
//        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        AppUtils.showValidationAlert(this@SignUpActivity,message)

    }

    private fun initUI() {
        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, EnterEmailOrJobActivity::class.java))
        }
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvSignup.setOnClickListener {

            if (isEmpty(binding.etFirstName)) {
                if (isEmpty(binding.etLastName)) {
                    if (isEmpty(binding.etEmail)) {
                        if (validEmailName()) {
                            if (isEmpty(binding.etNewPassword)) {
                                if (isPasswordLength(binding.etNewPassword)) {
                                    if (isEmpty(binding.etConfirmPassword)) {
//                                        if (isPasswordLength(binding.etConfirmPassword)) {
                                            if (validateTwoPasswords()) {
                                                signUpApi()
                                            } else {
                                                showSnakbar("New password & Confirm Password should be same")
                                            }

//                                        }
//                                        else {
//                                            showSnakbar("Please enter minimum 6 characters for confirm password")
//                                        }

                                    } else {
                                        showSnakbar("Please enter confirm password")

                                    }
                                } else {
                                    showSnakbar("Please enter minimum 6 characters for new password")
                                }
                            } else {
                                showSnakbar("Please enter new password")
                            }
                        } else {
                            showSnakbar("Please enter valid email id")
                        }
                    } else {
                        showSnakbar("Please enter email")
                    }
                } else {
                    showSnakbar("Please enter last name")
                }

            } else {
                showSnakbar("Please enter first name")

            }

        }

    }

    private fun signUpApi() {
        val user = JsonObject()
        user.addProperty("LoginType", "email")
        user.addProperty("FirstName", binding.etFirstName.text.toString().trim())
        user.addProperty("LastName", binding.etLastName.text.toString().trim())
        user.addProperty("Email", binding.etEmail.text.toString().trim())
        user.addProperty("Password", binding.etConfirmPassword.text.toString().trim())
        user.addProperty("ImageContent", "")
        var userInfoModel = UserInfoModel(
            "email",
            binding.etFirstName.text.toString().trim(),
            binding.etLastName.text.toString().trim(),
            binding.etEmail.text.toString().trim(),
            "",
            binding.etConfirmPassword.text.toString().trim(), ""
        )
        customSharedPreferences.saveUserInfoModel(userInfoModel)
        signUpViewModel.createUser(user)
    }

    private fun isPasswordLength(editText: EditText): Boolean {
        val str: CharSequence = editText.text.toString()
        return str.length >= 6

    }

    private fun isEmpty(text: EditText): Boolean {
        val str: CharSequence = text.text.toString()
        return str.isNotEmpty()
    }

    private fun setupObserver() {
        signUpViewModel.login().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    dismissProgressDialog()
                    var loginModel: SignUpModel? = it.data
                    if (loginModel != null) {
                        if (loginModel.status) {
                            customSharedPreferences.setLoginType("email")
//                            showSnakbar(loginModel.Message)
//                            Toast.makeText(this,loginModel.Message,Toast.LENGTH_SHORT).show()
                            customSharedPreferences.saveUserId(loginModel.Userinfo)
                            customSharedPreferences.setUserLoggedIn(true)
                            AppController.setLoginEmailID(binding.etEmail.text.toString())
                           val signUpIntent = Intent(this, EnterPasswordActivity::class.java)
//                            signUpIntent.putExtra(AppConstants.USER_CREATED, true)
//                            startActivity(signUpIntent)
                            AppUtils.showSuccessAlert(this,signUpIntent,loginModel.Message)


                        } else {
                            showSnakbar(loginModel.Message)
                        }
                    }
                }
                Status.LOADING -> {
                    showProgressDialog()

                }
                Status.ERROR -> {
                    dismissProgressDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun setupViewModel() {
        signUpViewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiHelperImpl(RetrofitBuilder.apiService),
                DatabaseHelperImpl(DatabaseBuilder.getInstance(this))
            )
        ).get(SignUpViewModel::class.java)
    }


    private fun validEmailName(): Boolean {
        val emailString = binding.etEmail.text.toString().trim()
        return Patterns.EMAIL_ADDRESS.matcher(emailString).matches()
    }

    private fun validateTwoPasswords(): Boolean {
        val password = binding.etNewPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        return password == confirmPassword
    }


}
