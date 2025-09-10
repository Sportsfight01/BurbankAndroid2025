package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class DisplyByStateIdBodyModel(
    @SerializedName("stateId")
    var stateId: Int,
    @SerializedName("latitude")
    var latitude: String,
    @SerializedName("longitude")
    var longitude: String,
    @SerializedName("userId")
    var userId: Int,
    @SerializedName("storey")
    var storey: String,
    @SerializedName("houseName")
    var houseName: String = "",
    @SerializedName("houseSize")
    var houseSize: String = ""


)