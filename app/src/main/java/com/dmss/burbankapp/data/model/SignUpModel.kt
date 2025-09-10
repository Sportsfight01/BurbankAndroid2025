package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class SignUpModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var Message: String = "",
    @SerializedName("Userinfo")
    var Userinfo: Int

)