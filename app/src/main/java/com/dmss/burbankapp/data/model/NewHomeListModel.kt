package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewHomeListModel(
    @SerializedName("FacadePermanentUrl")
    var facadePermantUrl: String? = null,
    @SerializedName("HouseName")
    var HouseName: String? = null,
    @SerializedName("HouseSize")
    var HouseSize: Int? = null,
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
    var MinLotWidth: Float? = null,
    @SerializedName("IsFav")
    var IsFav: Boolean = false,
    var message: String? = null,
    @SerializedName("Id")
    var Id: Int? = null,
    @SerializedName("HouseId_LandBank")
    var houseIdLandBank: Int? = null,
    var currentUserName: String? = ""

) : Parcelable
