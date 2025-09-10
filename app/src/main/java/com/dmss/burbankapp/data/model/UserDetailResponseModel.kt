package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

data class UserDetailResponseModel (
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("UserInfo")
    var userInfo: UserDetailModel

)