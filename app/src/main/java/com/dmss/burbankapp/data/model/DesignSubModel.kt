package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DesignSubModel(
    @SerializedName("EstateName")
    var EstateName: String,
    @SerializedName("HouseName")
    var HouseName: String,
    @SerializedName("HouseSize")
    var HouseSize: Int,
    @SerializedName("IsFavourite")
    var IsFavourite: Boolean = false,
    @SerializedName("Id")
    var Id: Int,
    @SerializedName("LotStreet1")
    var LotStreet1: String,
    @SerializedName("LotSuburb")
    var LotSuburb: String,
    @SerializedName("CarSpace")
    var CarSpace: Int = 0,
    @SerializedName("BathRooms")
    var BathRooms: Int = 0,
    @SerializedName("BedRooms")
    var BedRooms: Int = 0,
    var price: Double = 0.0,
    @SerializedName("Latitude")
    var latitude: String,
    @SerializedName("Longitude")
    var longitude: String
) : Parcelable
