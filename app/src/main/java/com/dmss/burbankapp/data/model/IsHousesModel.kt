package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class IsHousesModel(
    @SerializedName("FacadeLargeImageUrls")
    var facadeLargeImageUrls: ArrayList<String>,
    @SerializedName("HomePlan")
    var homePlanModel: HousePlanModel,
    @SerializedName("Facade")
    var facade: String,
    @SerializedName("ValidFacades")
    var validFacades: String? = null,

    @SerializedName("HouseName")
    var HouseName: String,
    @SerializedName("HouseSize")
    var HouseSize: String,


    @SerializedName("StateId")
    var StateId: String,
    @SerializedName("CarSpace")
    var CarSpace: String,
    @SerializedName("Price")
    var Price: Int ,

    @SerializedName("BedRooms")
    var BedRooms: String,
    @SerializedName("MinLotWidth")
    var MinLotWidth: Double,
    @SerializedName("BathRooms")
    var BathRooms: String ,
    @SerializedName("FacadePermanentUrl")
    var FacadePermanentUrl: String ,
    @SerializedName("IsFavourite")
    var IsFavourite: String ,
    @SerializedName("Visualisation")
    var Visualisation: String ,
    @SerializedName("id")
    var id: String ,


)