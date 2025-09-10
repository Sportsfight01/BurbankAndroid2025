package com.dmss.burbankapp.data.model


import com.google.gson.annotations.SerializedName

data class DisplayHomeRegionModel(
    @SerializedName("regionsByStateId")
    var regionsByStateId: List<String>,
    @SerializedName("status")
    var status: Boolean
)