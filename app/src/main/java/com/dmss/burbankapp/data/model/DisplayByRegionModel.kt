package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class DisplayByRegionModel(
    @SerializedName("Latitude")
    var Latitude: String,
    @SerializedName("Longitude")
    var Longitude: String,
    @SerializedName("EstateName")
    var EstateName: String,
    @SerializedName("FacadePermanentUrl")
    var FacadePermanentUrl: String,
    @SerializedName("Display")
    var Display: String


)