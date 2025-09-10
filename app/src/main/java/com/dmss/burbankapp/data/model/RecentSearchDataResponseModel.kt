package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class
RecentSearchDataResponseModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("UserFavourites")
    var userfavourites: Int,
    @SerializedName("SearchJson")
    var searchJsonModel: SearchJsonModel?=null,
    @SerializedName("FetchData")
    var FetchData: String


)