package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class DisplaysByStateIdModel(
    @SerializedName("MostPopularHomesDTOs")
    var mostPopularHomesDTOs: ArrayList<MostPopularHomesDTOModel>? = null,
    @SerializedName("SuggestedHomesDTOs")
    var suggestedHomesDTOs: ArrayList<SuggestedHomesDtoModel>? = null
)

