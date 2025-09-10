package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class DisplayRegionModel(
    @SerializedName("Id")
    var Id: Int?=null,
    @SerializedName("EstateName")
    var estateName: String,
    @SerializedName("HouseName")
    var houseName: String,
    @SerializedName("Latitude")
    var latitude: String?,
    @SerializedName("Longitude")
    var longitude: String?,
    @SerializedName("FacadePermanentUrl")
    var facadePermanentUrl: String,
    @SerializedName("FacadeName")
    var facadeName: String,
    @SerializedName("CarSpace")
    var carSpaces: Int = 0,
    @SerializedName("BathRooms")
    var bathRooms: Int = 0,
    @SerializedName("BedRooms")
    var bedRooms: Int = 0,
    @SerializedName("LotStreet1")
    var LotStreet1: String = null?: "",
    @SerializedName("LotSuburb")
    var lotSuburb: String = "",


    @SerializedName("Locations")
    var locations: ArrayList<String>
) : Parcelable

