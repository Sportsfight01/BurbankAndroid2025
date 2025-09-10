package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class SearchSubModel(
    @SerializedName("Value")
    var value: Int,
    @SerializedName("IsChecked")
    var IsChecked: Boolean,
    @SerializedName("DisplayName")
    var displayName: String
)