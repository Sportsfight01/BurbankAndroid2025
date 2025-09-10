package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class UserInfoModel(
    @SerializedName("LoginType")
    var LoginType: String? = null,
    @SerializedName("FirstName")
    var FirstName: String? = "",
    @SerializedName("LastName")
    var LastName: String? = "",
    @SerializedName("Email")
    var Email: String? = "",
    @SerializedName("ProfileImage")
    var ProfileImage: String? = null,
    @SerializedName("Password")
    var password: String? = null,

    @SerializedName("PhoneNumber")
    var PhoneNumber: String? = ""


)


