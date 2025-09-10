package com.dmss.burbankapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.SignUpModel
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class SignUpViewModel(private val apiHelper: ApiHelper,
                      private val dbHelper: DatabaseHelper) : ViewModel() {
    private val signUpLiveData = MutableLiveData<Resource<SignUpModel>>()

    fun login(): LiveData<Resource<SignUpModel>> {
        return signUpLiveData
    }

    fun createUser(jsonObject: JsonObject) {
        viewModelScope.launch {
            signUpLiveData.postValue(Resource.loading(null))
            try {
                val signUp = apiHelper.signUpApi(jsonObject)
                signUpLiveData.postValue(Resource.success(signUp))
            } catch (e: TimeoutCancellationException) {
                signUpLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                signUpLiveData.postValue(Resource.error("Something went wrong!! Please try again later", null))
            }
        }
    }

}