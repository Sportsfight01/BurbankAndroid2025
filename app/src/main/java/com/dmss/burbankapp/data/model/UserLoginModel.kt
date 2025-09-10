package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class UserLoginModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var Message: String,
    @SerializedName("Token")
    var token: String,
    @SerializedName("Userid")
    var Userid: String
)
data class DeviceResponseModel (
    @SerializedName("Code")
    val Code: Int,
    @SerializedName("Message")
    var Message: String

)