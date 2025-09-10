package models.progress

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserJobProgressItem(
    val comment: String? = null,
    val dateactual: String?="",
    val datedescription: String? =null,
    val forclient: Boolean? = null,
    val name:  String?="",
    val phasecode: String? = null,
    val resourcename: String? = null,
    val sequence: Int? = null,
    val stageId: Int? = null,
    val stageName: String?="",
    val status: String?="",
    val taskid: Int
): Parcelable

@Parcelize
data class UserJobProgressNewItem(
    val dateactual: String?="",
    val name:  String?="",
    val stageName: String?="",
    val status: String?="",
    val taskid: String
): Parcelable