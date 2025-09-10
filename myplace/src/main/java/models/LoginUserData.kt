package models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginUserData(
    val myHomeUsername: String? = null,
    var masterContractId: String? = null,
    val clientId: String?=null,
    val clientTitle: String?=null,
    val letterTitle: String?=null,
    val letterSalutation: String?=null,
    val homeAddress: HomeAddressData? = null,
    val contactDetails: ContactDetails? = null,
    val letterCasual: String?=null,
    val taxNumber: String? = null
): Parcelable

@Parcelize
data class HomeAddressData(
    val address: String? = null,
    var street1: String? = null,
    val suburb: String?=null,
    val state: String?=null,
    val postCode:String?=null
):Parcelable

@Parcelize
data class ContactDetails(
    val contactMethod: String? = null,
    var homePhone: String? = null,
    val mobilePhone: String?=null,
    val emailAddress:String?=null
):Parcelable