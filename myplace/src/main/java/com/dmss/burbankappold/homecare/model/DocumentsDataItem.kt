package com.digitalminds.homecare.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DocumentsDataItem(
    val authorname: String,
    val byclient: Boolean,
    val current: Boolean,
    val docdate: String,
    val title: String,
    val type: String,
    val url: String,
): Parcelable

@Parcelize
data class DocumentItem(
    var url : String,
    var date : String,
    var photoTitle : String,
    var photoType: String
): Parcelable
