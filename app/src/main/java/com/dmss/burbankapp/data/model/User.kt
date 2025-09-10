package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    val name: String = "",
    @SerializedName("email")
    val email: String = "",
    @SerializedName("avatar")
    var avatar: String = ""

)