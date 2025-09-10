package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PriceRangeModel(
    @SerializedName("Price")
    var price: Int,
    @SerializedName("MinPrice")
    var MinPrice: Double? = null,
    @SerializedName("MaxPrice")
    var MaxPrice: Double? = null
) : Serializable {
    override fun toString(): String {
        return "PriceRangeModel(MinPrice=$MinPrice, MaxPrice=$MaxPrice)"
    }
}
