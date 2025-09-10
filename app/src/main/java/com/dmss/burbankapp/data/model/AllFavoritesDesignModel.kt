package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class AllFavoritesDesignModel(
    @SerializedName("status")
    var status:Boolean,
    @SerializedName("HouseListData")
    var houseListData:ArrayList<HouseListCollectionModel>
)