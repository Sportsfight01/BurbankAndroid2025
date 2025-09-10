package com.dmss.burbankapp.ui.enguirenow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.EnquireModel
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class EnquireViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private var enquireLiveData = MutableLiveData<Resource<EnquireModel>>()

    fun getEnquireLiveData(): LiveData<Resource<EnquireModel>> {
        return enquireLiveData

    }

    fun fetchEnquire(jsonObject: JsonObject) {
        viewModelScope.launch {
            enquireLiveData.postValue(Resource.loading(null))
            try {
                val enquireData = apiHelper.submitEnquireForm(jsonObject)
                enquireLiveData.postValue(Resource.success(enquireData))
            } catch (e: TimeoutCancellationException) {
                enquireLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                enquireLiveData.postValue(Resource.error("Something went wrong!! Please try again later", null))
            }
        }

    }


}