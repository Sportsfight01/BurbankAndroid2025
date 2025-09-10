package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class UpdateProfilePicModel {
    @SerializedName("status")
    val Status: Boolean? = null

    @SerializedName("message")
    var Message: String? = null

    @SerializedName("File")

    var file: String? = null
    override fun toString(): String {
        return "UpdateProfilePicModel(Status=$Status, Message=$Message, file=$file)"
    }


}




