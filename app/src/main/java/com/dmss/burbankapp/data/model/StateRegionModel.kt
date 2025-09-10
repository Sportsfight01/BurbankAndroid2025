package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class StateRegionModel (
    @SerializedName("RegionId")
    var regionId:Int,
    @SerializedName("StateId")
    var stateId:Int,
    @SerializedName("Latitudefield")
    var latitudefield:Float,
    @SerializedName("Longitudefield")
    var longitudefield:Float,
    @SerializedName("RegionName")
    var RegionName:String,
    @SerializedName("isSelected")
    var isSelected: Boolean = false



)