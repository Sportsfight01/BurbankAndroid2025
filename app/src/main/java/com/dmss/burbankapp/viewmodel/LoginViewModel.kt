package com.dmss.burbankapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.LoginModel
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {
    private val loginLiveData = MutableLiveData<Resource<LoginModel>>()

    fun login(): LiveData<Resource<LoginModel>> {
        return loginLiveData
    }

    fun loginApi(email: String,password:String) {
        viewModelScope.launch {
            loginLiveData.postValue(Resource.loading(null))
            try {
                val user = JsonObject()
                user.addProperty("Username", email)
                user.addProperty("Password", password)

                val loginApi = apiHelper.getLoginApi(email,password,user)
                loginLiveData.postValue(Resource.success(loginApi))
            } catch (e: TimeoutCancellationException) {
                loginLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                loginLiveData.postValue(Resource.error("Something went wrong!! Please try again later", null))
            }
        }
    }

}