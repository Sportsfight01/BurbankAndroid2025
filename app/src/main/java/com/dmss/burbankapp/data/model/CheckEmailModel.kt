package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

data class CheckEmailModel(
    @SerializedName("status")
    val status: Boolean,
    /*   @SerializedName("EmailExists")
       val EmailExists: S,*/
    @SerializedName("message")
    var Message: String = ""


)
