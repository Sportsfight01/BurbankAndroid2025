package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class MostPopularHomesDTOModel(
    @SerializedName("HouseName")
    var houseName: String,
    @SerializedName("HouseSize")
    var HouseSize: String = "0",
    @SerializedName("ImageUrl")
    var imageUrl: String,
    @SerializedName("HousePrice")
    var HousePrice: Double,
    @SerializedName("Storey")
    var Storey: Int,
    @SerializedName("CarSpace")
    var CarSpace: Int = 0,
    @SerializedName("BathRooms")
    var BathRooms: Int = 0,
    @SerializedName("BedRooms")
    var BedRooms: Int = 0,
    @SerializedName("Id")
    var Id: Int,
    @SerializedName("LocationCount")
    var locationCount: Int = 0,
    @SerializedName("DesignLocation")
    var locations: Array<DesignSubModel>,
    @SerializedName("DisplayId")
    var DisplayId:Int,
    @SerializedName("IsFavourite")
    var IsFavourite:Boolean


)