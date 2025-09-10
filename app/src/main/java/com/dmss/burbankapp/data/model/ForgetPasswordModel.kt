package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class ForgetPasswordModel(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("EmailExists")
    val EmailExists: String = "",
    @SerializedName("Info")
    val Info:InfoData
)

class InfoData(
    @SerializedName("UserId")
    val UserId : String = "",
    @SerializedName("Message")
    val Message: String = ""
)