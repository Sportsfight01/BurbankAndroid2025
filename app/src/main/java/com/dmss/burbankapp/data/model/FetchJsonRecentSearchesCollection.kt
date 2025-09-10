package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class FetchJsonRecentSearchesCollection(
    @SerializedName("Message")
    var message: String? = null,
    @SerializedName("FullName")
    var FullName: String? = null,
    @SerializedName("EmailId")
    var emailId: String,
    @SerializedName("Count")
    var Count: Int? = null,
    @SerializedName("newHomesDTOs")
    var homeAndLandDTOsList: ArrayList<NewHomeListModel>? = null
)