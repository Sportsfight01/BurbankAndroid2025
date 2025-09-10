package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class SearchJsonModel(
    @SerializedName("Regions")
    var regionsList: ArrayList<RegionsModel>,
    @SerializedName("MinPrice")
    var minPrice: Double,
    @SerializedName("MaxPrice")
    var maxPrice: Double,
    @SerializedName("UserFavourites")
    var userFavorites: Int,
    @SerializedName("SortBy")
    var SortBy: String,
    @SerializedName("MinHomeSize")
    var MinHomeSize: String,
    @SerializedName("MaxHomeSize")
    var MaxHomeSize: String,
    @SerializedName("BathRoomFilters")
    var BathRoomFilters: ArrayList<SearchSubModel>,
    @SerializedName("BedRoomFilters")
    var BedRoomFilters: ArrayList<SearchSubModel>,
    @SerializedName("CarSpaces")
    var CarSpaces: ArrayList<SearchSubModel>,
    @SerializedName("StoreyFilters")
    var StoreyFilters: ArrayList<SearchSubModel>


)