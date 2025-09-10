package com.dmss.burbankapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HnLbackHandlingViewModel : ViewModel() {


    private val backStackString = MutableLiveData<String>()
    private val updateRegionLiveData = MutableLiveData<Boolean>()
    private val updateStoreysLiveData = MutableLiveData<Boolean>()
    private val updateBedroomsLiveData = MutableLiveData<Boolean>()
    private val updatePriceTrackLiveData = MutableLiveData<Boolean>()

    val _backPressLiveData = MutableLiveData<Boolean>()
    val bacPressLiveData:LiveData<Boolean> = _backPressLiveData

    fun getBackStackString(): LiveData<String> {
        return backStackString
    }

    fun setBackStackString(name: String) {
        backStackString.postValue(name)
    }

    //HomeAndLand Regions
    fun getUpdateRegionLiveData(): LiveData<Boolean> {
        return updateRegionLiveData
    }

    fun setUpdateRegionLiveData(isUpdate: Boolean) {
        updateRegionLiveData.postValue(isUpdate)
    }

    //HomeAndLand Storeys
    fun getUpdateStoreysLiveData(): LiveData<Boolean> {
        return updateStoreysLiveData
    }

    fun setUpdateStoreysLiveData(isUpdate: Boolean) {
        updateStoreysLiveData.postValue(isUpdate)
    }

    //HomeAndLand Bedrooms
    fun getUpdateBedroomsLiveData(): LiveData<Boolean> {
        return updateBedroomsLiveData
    }

    fun setUpdateBedroomsLiveData(isUpdate: Boolean) {
        updateBedroomsLiveData.postValue(isUpdate)
    }

    //HomeAndLand PriceTrack
    fun getUpdatePriceTrackLiveData(): LiveData<Boolean> {
        return updatePriceTrackLiveData
    }

    fun setUpdatePriceTrackLiveData(isUpdate: Boolean) {
        updatePriceTrackLiveData.postValue(isUpdate)
    }


}