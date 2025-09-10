package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class FavoriteResponseModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var message:String
)