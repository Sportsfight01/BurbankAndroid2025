package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewHomeQuizListModel(
    @field:SerializedName("Question")
    var question: String? = null,
    @field:SerializedName("Feature")
    var feature: String? = null,
    @field:SerializedName("QuestionOrder") var questionOrder: Int? = null,
    @field:SerializedName("MaxValue") var MaxValue: Double? = null,
    @field:SerializedName("MinValue") var MinValue: Double? = null,
    @field:SerializedName("HousesCount") var HousesCount: Int? = null,
    @field:SerializedName("Options") var Options: ArrayList<String>? = null
) : Parcelable
