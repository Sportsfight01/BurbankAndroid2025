package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class HouseDetailByEstateObject(
    @SerializedName("stateId")
    var stateId: Int,
    @SerializedName("estateName")
    var estateName: String,
    @SerializedName("userID")
    var userID: Int = 0
)