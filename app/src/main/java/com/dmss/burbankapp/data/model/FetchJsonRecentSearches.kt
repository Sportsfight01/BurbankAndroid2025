package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class FetchJsonRecentSearches(

    @SerializedName("Message")
    var message: String,
    @SerializedName("FullName")
    var FullName :String,
    @SerializedName("Count")
    var Count:Int,
    @SerializedName("homeAndLandDTOs")
    var homeAndLandDTOsList: ArrayList<HnLQuizPackageModel>
)