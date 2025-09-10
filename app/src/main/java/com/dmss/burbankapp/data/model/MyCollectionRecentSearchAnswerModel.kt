package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class MyCollectionRecentSearchAnswerModel(
    @SerializedName("feature")
    var feature: String,
    @SerializedName("question")
    var question: String,
    @SerializedName("Answer")
    var answer: String,
    @SerializedName("MaxValue")
    var maxValue: String,
    @SerializedName("MinValue")
    var minValue: String
)