package com.dmss.burbankapp.data.apiUtils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.ui.dashboard.DashboardViewModel
import com.dmss.burbankapp.ui.designs.NewHomeQuizViewModel
import com.dmss.burbankapp.ui.enguirenow.EnquireViewModel
import com.dmss.burbankapp.ui.homeandlandregions.HomeAndLandViewModel
import com.dmss.burbankapp.ui.loginhome.LoginHomeViewModel
import com.dmss.burbankapp.ui.mydisplay.DisplayHomesViewModel
import com.dmss.burbankapp.ui.resetpassword.ResetPasswordViewModel
import com.dmss.burbankapp.ui.splash.SpalashViewModel
import com.dmss.burbankapp.viewmodel.*


class ViewModelFactory(private val apiHelper: ApiHelper, private val dbHelper: DatabaseHelper) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfilePicViewModel::class.java)) {
            return ProfilePicViewModel() as T
        }
        if (modelClass.isAssignableFrom(CheckUserEmailViewModel::class.java)) {
            return CheckUserEmailViewModel(apiHelper, dbHelper) as T
        }
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(apiHelper, dbHelper) as T
        }
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(apiHelper, dbHelper) as T
        }
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)) {
            return ForgetPasswordViewModel(apiHelper, dbHelper) as T
        }

        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                apiHelper,
                dbHelper
            ) as T
        }
        if (modelClass.isAssignableFrom(LoginHomeViewModel::class.java)) {
            return LoginHomeViewModel(
                apiHelper,
                dbHelper
            ) as T
        }
        if (modelClass.isAssignableFrom(HomeAndLandViewModel::class.java)) {
            return HomeAndLandViewModel(
                apiHelper,
                dbHelper
            ) as T
        }
        if (modelClass.isAssignableFrom(EnquireViewModel::class.java)) {
            return EnquireViewModel(
                apiHelper, dbHelper
            ) as T
        }
        if (modelClass.isAssignableFrom(ResetPasswordViewModel::class.java)) {
            return ResetPasswordViewModel(
                apiHelper, dbHelper
            ) as T
        }

        if (modelClass.isAssignableFrom(HnLbackHandlingViewModel::class.java)) {
            return HnLbackHandlingViewModel() as T
        }
        if (modelClass.isAssignableFrom(NewHomeQuizViewModel::class.java)) {
            return NewHomeQuizViewModel(apiHelper, dbHelper) as T
        }
        if (modelClass.isAssignableFrom(SpalashViewModel::class.java)) {
            return SpalashViewModel(apiHelper, dbHelper) as T
        }

        if (modelClass.isAssignableFrom(DisplayHomesViewModel::class.java)) {
            return DisplayHomesViewModel(apiHelper, dbHelper) as T
        }

        throw IllegalArgumentException("Unknown class name")
    }

}