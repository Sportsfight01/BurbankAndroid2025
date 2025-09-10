package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class UserDetailModel(
    @SerializedName("FirstName")
    var FirstName: String = "",
    @SerializedName("LastName")
    var LastName: String = "",
    @SerializedName("Name")
    var name: String = "",
    @SerializedName("Email")
    var Email: String = "",
    @SerializedName("PhoneNumber")
    var PhoneNumber: String = "",
    @SerializedName("ShareAccountWith")
    var ShareAccountWith: String = "",
    @SerializedName("ProfileImage")
    var ProfileImage: String = ""
)