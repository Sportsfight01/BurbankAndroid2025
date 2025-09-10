package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class HouseAppointmentDataModelObject(
    @SerializedName("DisplayId")
    var displayId: Int,
    @SerializedName("StateId")
    var stateId: Int,
    @SerializedName("UserId")
    var userId: Int,
    @SerializedName("Date")
    var date: String,
    @SerializedName("Comments")
    var comments: String,
    @SerializedName("Time")
    var Time: String,
    @SerializedName("FirstName")
    var firstName: String,
    @SerializedName("LastName")
    var lastName: String,
    @SerializedName("Phone")
    var phone: String,
    @SerializedName("Email")
    var email: String,
    @SerializedName("Where would you like to live")
    var where_would_you_like_to_live: String,
    @SerializedName("IsPrivacyConsent")
    var isPrivacyConsent: Boolean,

)