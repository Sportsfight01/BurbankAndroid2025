package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class HousePlanModel(
    @SerializedName("FloorPlanImageURL_Mobile")
    var FloorPlanImageURL_Mobile: String,
    @SerializedName("Visualisation")
    var Visualisation: Boolean,
    @SerializedName("MinLotWidth")
    var MinLotWidth: Float = 0f,



    )