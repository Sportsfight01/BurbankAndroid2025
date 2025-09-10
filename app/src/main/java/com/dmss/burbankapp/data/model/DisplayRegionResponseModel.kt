package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class DisplayRegionResponseModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("DisplaysByRegion")
    var displaysByRegionList: ArrayList<DisplayRegionModel>


) : Parcelable

