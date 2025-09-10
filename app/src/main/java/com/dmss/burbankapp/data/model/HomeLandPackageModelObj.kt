package com.dmss.burbankapp.data.model

import java.io.Serializable

data class HomeLandPackageModelObj(
    val region:String,
    val storey: Int,
    val bedRooms: Int,
    val carSpaces: Int,
    val bathrooms: Int,
    val selectedMinValue: String,
    val selectedMaxValue: String,
    val includePackages: Int,
    val sortByPrice: Int
):Serializable