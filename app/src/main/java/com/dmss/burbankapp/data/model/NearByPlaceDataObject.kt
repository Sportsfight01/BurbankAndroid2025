package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class NearByPlaceDataObject(
    @SerializedName("stateId")
    var stateId: Int,
    @SerializedName("latitude")
    var latitude: String,
    @SerializedName("longitude")
    var longitude: String,
    @SerializedName("userId")
    var userId: Int
)