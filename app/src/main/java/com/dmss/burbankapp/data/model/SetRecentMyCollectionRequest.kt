package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class SetRecentMyCollectionRequest(
    @field:SerializedName("StateId")
    var stateId: Int,
    @field:SerializedName("TypeId") var typeId: Int,
    @field:SerializedName("UserId") var userId: Int,
    @SerializedName("SearchTextJson")
    var newHomeJsonList: ArrayList<NewHomeJsonObject>?
)

