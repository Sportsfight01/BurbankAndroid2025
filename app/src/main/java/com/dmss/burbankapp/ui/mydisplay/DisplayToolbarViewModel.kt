package com.dmss.burbankapp.ui.mydisplay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DisplayToolbarViewModel: ViewModel() {
    val favouritesCount = MutableLiveData<Int>()
    val updateFavouriteDisplayHomes = MutableLiveData<Int>()
    val updateMainHeader= MutableLiveData<String>()
    val updateMainHeaderForDisplayHomes= MutableLiveData<String>()
    val updateSubheaderForDisplayHomes= MutableLiveData<String>()

    val updateToolBar = MutableLiveData<String>()
//    val selectedItem: LiveData<String> get() = updateToolBar
    fun updateToolbarValue(text: String){
        updateToolBar.postValue(text)
    }
    fun setFavouritesCount(text: Int) {
        favouritesCount.postValue(text)
    }
    fun setFavouriteDisplayHomes(text: Int) {
        updateFavouriteDisplayHomes.postValue(text)
    }
    fun setMainHeader(text: String) {
        updateMainHeader.postValue(text)
    }
    fun setMainHeaderForDisplayHomes(text: String) {
        updateMainHeaderForDisplayHomes.postValue(text)
    }
    fun setUpdateSubheaderForDisplayHomes(text: String) {
        updateSubheaderForDisplayHomes.postValue(text)
    }
}