package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

data class TestUserModel(
    /* @SerializedName("Status")
     val status: Boolean = false,

     *//* @SerializedName("EmailExists")
     val EmailExists: String = "",*/
    @SerializedName("message")
    var Message: String = ""
)