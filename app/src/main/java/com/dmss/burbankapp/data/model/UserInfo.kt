package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class UserInfo(
    @SerializedName("UserId")
    var UserId: String,
    @SerializedName("message")
    var Message: String = ""
)
