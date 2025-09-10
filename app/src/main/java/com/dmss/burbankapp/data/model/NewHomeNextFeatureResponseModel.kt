package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class NewHomeNextFeatureResponseModel(
    @SerializedName("status")
    var status: Boolean = false,
    @SerializedName("message")
    var message: String?=null,
    @SerializedName("newHomesNextFeatures")
    var newHomesNextFeatures: NewHomesNextFeaturesModel? = null

)