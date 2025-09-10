package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class NearByDisplaysResponseModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("DisplaysByRegion")
    var regionsList: ArrayList<DisplayRegionModel>
)