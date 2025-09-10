package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class CheckEmailStatusModel(
    @SerializedName("IsEmailExists")
    var IsEmailExists: Boolean,
    @SerializedName("Message")
    var message: String,
    @SerializedName("FullName")
    var fullName: String,
    @SerializedName("Email")
    var email: String,
    @SerializedName("ProfilePic")
    var profilePic: String?

)