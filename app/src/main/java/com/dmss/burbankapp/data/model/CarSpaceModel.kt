package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class CarSpaceModel(
    @SerializedName("Value")
    var value:Int,
    @SerializedName("IsChecked")
    var isChecked:Boolean,
    @SerializedName("DisplayName")
    var displayName:String
)