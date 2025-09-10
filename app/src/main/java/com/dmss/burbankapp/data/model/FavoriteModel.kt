package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class FavoriteModel(
    @SerializedName("status")
    var status: String,
    @SerializedName("message")
    var message: String
)