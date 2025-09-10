package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

data class HomeAndLandPackageDetailModel(
    @SerializedName("staus")
    var staus: Boolean? = null,
    @SerializedName("getpackagebyId")
    var getpackagebyId: GetHomeAndLandPackageByIdModel,



)