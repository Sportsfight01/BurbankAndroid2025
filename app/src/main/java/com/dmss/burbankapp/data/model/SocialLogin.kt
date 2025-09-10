package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName
 class SocialLogin (
     @SerializedName("status")
    var Status: Boolean,
    @SerializedName("message")
    var Message: String = "",
    @SerializedName("UserId")
    var UserId: String = "",
    var LoginType:Boolean,

    var loginStatus: LoginStatus

)