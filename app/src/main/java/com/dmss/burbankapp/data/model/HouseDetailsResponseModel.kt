package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class HouseDetailsResponseModel(
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("houseDetailsByHouseType")
    var houseDetailsList: ArrayList<HouseDetailsByHouseType>

)