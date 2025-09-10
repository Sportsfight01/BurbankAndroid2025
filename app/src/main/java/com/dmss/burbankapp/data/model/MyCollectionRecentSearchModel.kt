package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName
class MyCollectionRecentSearchModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("UserFavourites")
    var userfavourites: Int,
    @SerializedName("SearchJson")
    var searchJsonList: ArrayList<MyCollectionRecentSearchAnswerModel>
)