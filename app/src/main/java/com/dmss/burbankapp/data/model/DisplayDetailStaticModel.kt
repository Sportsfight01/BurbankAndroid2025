package com.dmss.burbankapp.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DisplayDetailStaticModel(
    var HouseName: String,
    var HouseSize: Int,
    var HousePrice: Double,
    var Storey: Int,
    var CarSpace: Int,
    var BathRooms: Int,
    var BedRooms: Int,
    var Id: Int,
    var IsFav: Boolean = true,
    var MinLotWidth: Double? = 0.0

) : Parcelable