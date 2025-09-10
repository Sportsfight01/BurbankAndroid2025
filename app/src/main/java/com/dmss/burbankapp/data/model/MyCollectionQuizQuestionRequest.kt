package com.dmss.burbankapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MyCollectionQuizQuestionRequest(
    @field:SerializedName("StateId") var stateId: Int,
    @field:SerializedName("IncludePackages") var includePackages: Int,
    @field:SerializedName("SortByPrice") var sortByPrice: Int,
    @field:SerializedName("UserId") var userId: Int,
    @field:SerializedName("FeatureAllFilter") var featureAllFilter: String = "8",
    /*@Expose
    @SerializedName("MinLotWidth") var minLotWidth: Float,*/

    @Expose
    @SerializedName("newhomesjson")
    var newHomeJsonList: ArrayList<NewHomeJsonObject>?
)


