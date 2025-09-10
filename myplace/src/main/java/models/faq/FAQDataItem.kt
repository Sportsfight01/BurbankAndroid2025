package models.faq

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FAQDataItem(
    val State: String,
    val Category: String,
    val Answer: String?,
    val Question: String?,
    val VideoUrl: String?,
    val Image: String?,
    val Heading: String?,
    val Description: String?,
    var isExpanded: Boolean  = false
): Parcelable