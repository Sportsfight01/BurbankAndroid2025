package com.dmss.burbankappold.dashboard.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {


    val JobNumbersDailog= MutableLiveData<String>()

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    fun showJobNumbersDailog(text: String) {
        JobNumbersDailog.postValue(text)
    }
}