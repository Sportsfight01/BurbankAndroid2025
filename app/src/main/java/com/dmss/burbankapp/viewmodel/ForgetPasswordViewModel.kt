package com.dmss.burbankapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.apiUtils.Resource.Companion.error
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.ForgetPasswordModel
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class ForgetPasswordViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    var forgetPasswordLiveData = MutableLiveData<Resource<ForgetPasswordModel>>()

    fun getForgetPasswordLiveData(): LiveData<Resource<ForgetPasswordModel>> {
        return forgetPasswordLiveData
    }

    fun fetchForgetPassword(emailString: String){
        viewModelScope.launch {
            forgetPasswordLiveData.postValue(Resource.loading(null))
            try {
                val user = JsonObject()
                user.addProperty("EmailId", emailString)
                val loginApi = apiHelper.forgetPassword(emailString)
                forgetPasswordLiveData.postValue(Resource.success(loginApi))
            } catch (e: TimeoutCancellationException) {
                forgetPasswordLiveData.postValue(error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                forgetPasswordLiveData.postValue(error("Something went wrong!! Please try again later", null))
            }
        }
    }


}