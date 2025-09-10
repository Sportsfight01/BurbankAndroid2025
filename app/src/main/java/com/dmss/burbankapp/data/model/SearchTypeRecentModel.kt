package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class SearchTypeRecentModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("FetchListSearchText")
    var fetchsearchList: ArrayList<FetchListSearchModel>
)