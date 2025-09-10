package com.dmss.burbankappold.utils

import android.text.Spanned
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData

open class SharedViewModel : ViewModel(){
    val selected = MutableLiveData<String>()
    val favouritesCount = MutableLiveData<String>()
    val financetab = MutableLiveData<Boolean>()
    val notesData = MutableLiveData<ArrayList<NotesData>>()
    val profileSubHeader = MutableLiveData<Spanned>()

        val data = MutableLiveData<String>()

        fun setdata(item: String) {
            data.value = item
        }
    fun setNotesdata(item: ArrayList<NotesData>) {
        notesData.value = item
    }
    fun financeItem(item: Boolean) {
        financetab.value = item
    }
    fun selectedItem(item: String) {
        selected.value = item
    }
    fun setFavouriteCount(item: String) {
        favouritesCount.value = item
    }
    fun setProfileSubHeader(msg:Spanned){
        profileSubHeader.value = msg
    }
}