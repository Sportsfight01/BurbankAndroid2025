package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NewHomesNextFeaturesModel(
    @SerializedName("HouseCount")
    var HouseCount: Int = 0,
    @SerializedName("NextFeature")
    var nextFeature: String? = null,
    @SerializedName("NextFeatureQuestion")
    var nextFeatureQuestion: String? = null,
    @SerializedName("NextFeatureAnswers")
    var nextFeatureAnswers: ArrayList<String>? = null,
    @SerializedName("NewHomesList")
    var newHomesList: ArrayList<NewHomeListModel>? = null


) : Parcelable {

}