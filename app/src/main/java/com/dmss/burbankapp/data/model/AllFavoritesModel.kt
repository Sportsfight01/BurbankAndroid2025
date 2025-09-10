package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class AllFavoritesModel (
    @SerializedName("status")
    var status:Boolean,
    @SerializedName("HouseListData")
    var houseListData:ArrayList<HouseListModel>

)