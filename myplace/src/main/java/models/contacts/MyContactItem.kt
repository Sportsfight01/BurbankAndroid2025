package models.contacts

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import models.finance.FinanceData

data class MyContactItem(
    var designation: String,
    var name : String,
    var email: String,
    var phone: String
)
data class MyContactsResultDataData(
    val status: Boolean,
    val surveyDetails: MyContactsData,

    )