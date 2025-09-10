package models.contactUs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactUsDataItem(
    val authorname: String? = null,
    var body: String? = null,
    val byclient: Boolean,
    val documents: List<String>,
    val notedate: String,
    val noteid: Int,
    val replies: ArrayList<ContactUsDataItem>? = null,
    val replytoid: Int?,
    val subject: String? = null
): Parcelable