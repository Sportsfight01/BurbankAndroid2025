package com.dmss.burbankapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.CheckEmailModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class CheckUserEmailViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {
    private val emailLiveData = MutableLiveData<Resource<CheckEmailModel>>()

    fun getEmailExistOrNot(): LiveData<Resource<CheckEmailModel>> {
        return emailLiveData
    }

    fun isEmailExist(email: String) {
        viewModelScope.launch {
            emailLiveData.postValue(Resource.loading(null))
            try {
                val usersFromApi = apiHelper.getEmailExist(email)
                emailLiveData.postValue(Resource.success(usersFromApi))
            } catch (e: TimeoutCancellationException) {
                emailLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                emailLiveData.postValue(Resource.error("Something went wrong!! Please try again later", null))
            }
        }
    }

}