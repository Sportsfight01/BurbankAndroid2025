package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class DisplaysByStateIdResponseModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("getdisplaysbyId")
    var getDisplaysById: DisplaysByStateIdModel?=null

)