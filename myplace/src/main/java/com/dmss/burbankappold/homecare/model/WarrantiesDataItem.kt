package com.digitalminds.homecare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WarrantiesDataItem(
    val authorname: String,
    val byclient: Boolean,
    val current: Boolean,
    val docdate: String,
    val title: String,
    val type: String,
    val url: String,
): Parcelable

@Parcelize
data class WarrantiesItem(
    var url : String,
    var date : String,
    var photoTitle : String,
    var photoType: String
): Parcelable
