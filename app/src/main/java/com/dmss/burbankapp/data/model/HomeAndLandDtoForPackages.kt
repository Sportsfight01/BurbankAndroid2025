package com.dmss.burbankapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeAndLandDtoForPackages(
    var region: String? = null,
    var storeys: ArrayList<Int>? = null,
    var bedRooms: ArrayList<Int>? = null,
    var selectedMinimumPrice: String? = null,
    var selectedMaxPrice: String? = null
) : Parcelable