package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class RegionsByStateIdResponseModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("regionsByStateId")
    var regionsByStateId: Array<String>


)