package models.photos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotosDataItem(
    val authorname: String,
    val byclient: Boolean,
    val current: Boolean,
    val docdate: String,
    val title: String,
    val type: String,
    val url: String,
    val extension: String?="",
    val isMyHome: String,

    val metaData: MetaData,
    ): Parcelable

@Parcelize
data class PhotoItem(
    var url : String,
    var date : String,
    var photoTitle : String,
    var photoType: String
): Parcelable

@Parcelize
data class MetaData(
    var createdOn : String

): Parcelable
