package com.dmss.burbankapp.ui.resetpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.*
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {
    private val resetPasswordLiveData = MutableLiveData<Resource<ResetPasswordModel>>()

    fun getResetPasswordData(): LiveData<Resource<ResetPasswordModel>> {
        return resetPasswordLiveData
    }

    fun setResetPassword(jsonObject: JsonObject) {
        viewModelScope.launch {
            resetPasswordLiveData.postValue(Resource.loading(null))
            try {
                val resetPasswordData = apiHelper.setResetPassword(jsonObject)
                resetPasswordLiveData.postValue(Resource.success(resetPasswordData))
            } catch (e: TimeoutCancellationException) {
                resetPasswordLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                resetPasswordLiveData.postValue(Resource.error("Something went wrong!! Please try again later", null))
            }
        }
    }


}