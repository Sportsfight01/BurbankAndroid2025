package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class FetchListSearchModel(
    @SerializedName("ItemId")
    var itemmField: Int,
    @SerializedName("SearchTypeText")
    var SearchTypeText: String,
    @SerializedName("TypeId")
    var typeId: Int

)