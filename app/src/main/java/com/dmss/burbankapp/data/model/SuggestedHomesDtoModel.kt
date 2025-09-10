package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SuggestedHomesDtoModel(
    @SerializedName("HouseName")
    var HouseName: String,
    @SerializedName("FacadePermanentUrl")
    var FacadePermanentUrl: String,
    @SerializedName("EstateName")
    var EstateName: String,
    @SerializedName("LotSuburb")
    var lotSuburb: String,
    @SerializedName("HouseSize")
    var HouseSize: Int,
    @SerializedName("Locations")
    var locations: Array<String> = emptyArray(),
    @SerializedName("Latitude")
    var latitude: String,
    @SerializedName("Longitude")
    var longitude: String,
    @SerializedName("IsFavourite")
    var isFavorite: Boolean = false,
    @SerializedName("Id")
    var id: Int,
    @SerializedName("LotStreet1")
    var LotStreet1: String=null?: ""


) : Parcelable {
    override fun toString(): String {
        return "SuggestedHomesDtoModel(HouseName='$HouseName', FacadePermanentUrl='$FacadePermanentUrl', EstateName='$EstateName', HouseSize=$HouseSize, locations=${locations.contentToString()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SuggestedHomesDtoModel

        if (HouseName != other.HouseName) return false
        if (FacadePermanentUrl != other.FacadePermanentUrl) return false
        if (EstateName != other.EstateName) return false
        if (HouseSize != other.HouseSize) return false
        if (!locations.contentEquals(other.locations)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = HouseName.hashCode()
        result = 31 * result + FacadePermanentUrl.hashCode()
        result = 31 * result + EstateName.hashCode()
        result = 31 * result + HouseSize
        result = 31 * result + locations.contentHashCode()
        return result
    }
}