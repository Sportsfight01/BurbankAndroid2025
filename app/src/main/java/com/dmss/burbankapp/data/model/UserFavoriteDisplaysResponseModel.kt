package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserFavoriteDisplaysResponseModel(
    @SerializedName("status")
    var status: Boolean = false,
    @SerializedName("userFavDisplays")
    var userFavorites: ArrayList<UserFavoritesDisplayModel>
) : Parcelable

@Parcelize
data class UserFavoritesDisplayModel(
    @SerializedName("Id")
    var id: Int,
    @SerializedName("Facade")
    var facade: String,
    @SerializedName("FacadePermanentUrl")
    var FacadePermanentUrl: String,
    @SerializedName("HouseName")
    var HouseName: String,
    @SerializedName("HouseSize")
    var HouseSize: Int,
    @SerializedName("IsFav")
    var IsFav: Boolean = true,
    @SerializedName("Price")
    var Price: Double? = null,
    @SerializedName("BedRooms")
    var BedRooms: Int? = null,
    @SerializedName("BathRooms")
    var BathRooms: Int? = null,
    @SerializedName("Storey")
    var Storey: Int? = null,
    @SerializedName("CarSpace")
    var CarSpace: Int? = null,
    @SerializedName("MinLotWidth")
    var MinLotWidth: Double? = null,
    var message: String? = null,
    @SerializedName("HouseId_LandBank")
    var currentUserName: String? = "",
    @SerializedName("DisplayEstateName")
    var displayEstateName: String,
    @SerializedName("Suburb")
    var suburb: String,
    @SerializedName("Street")
    var street: String,
    @SerializedName("DisplayDesignCount")
    var displayDesignCount: ArrayList<String>? = null,
    @SerializedName("DisplayId")
    var DisplayId: Int


) : Parcelable
