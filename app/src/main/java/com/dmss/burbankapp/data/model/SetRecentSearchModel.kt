package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class SetRecentSearchModel (
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var Message: String
)