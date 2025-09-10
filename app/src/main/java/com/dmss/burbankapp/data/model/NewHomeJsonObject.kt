package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class NewHomeJsonObject(
    @field:SerializedName("feature") var feature: String,
    @field:SerializedName("question") var question: String,
    @field:SerializedName("Answer") var answer: String,
    @Expose
    @SerializedName("MinValue")
    var minValue: String = "0.0",
    @Expose
    @SerializedName("MaxValue")
    var maxValue: String = "",
    @Expose
    @SerializedName("MinLotWidth")
    var minLotWidth: String = ""
) : Parcelable