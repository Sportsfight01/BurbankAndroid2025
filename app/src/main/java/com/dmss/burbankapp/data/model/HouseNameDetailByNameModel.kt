package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class HouseNameDetailByNameModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("message")
    var message: String,
    @SerializedName("lsthouses")
    var isHousesModel: IsHousesModel


)