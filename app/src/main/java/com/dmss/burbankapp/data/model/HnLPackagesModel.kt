package com.dmss.burbankapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class HnLPackagesModel(
    @SerializedName("status")
    var status: Boolean =false,
    @SerializedName("message")
    var message: String?=null,
    @SerializedName("HnLQuizResults")
    var HnLQuizResults: Int?=null,
    @SerializedName("PriceRange")
    var priceRangeModel: PriceRangeModel? = null,
    @SerializedName("CountDistribution")
    var countDistribution: Array<Int>?=null,
    @SerializedName("Storeys")
    var storeys: Array<Int>?=null,
    @SerializedName("Bedrooms")
    var bedrooms: Array<Int>?=null,
    @SerializedName("carspaces")
    var carspaces: Array<Int>?=null,
    @SerializedName("Bathrooms")
    var Bathrooms: Array<Int>?=null,
    @SerializedName("HnLQuizPackages")
    var hnlQuizList: ArrayList<HnLQuizPackageModel>?=null,
    @SerializedName("PriceListRange")
    var priceRangeList: ArrayList<PriceRangeListModel>? = null


) : Parcelable
@Parcelize
class PriceRangeListModel(
    @SerializedName("Price")
    var price: Int,
    @SerializedName("MinPrice")
    var minPrice: Double,
    @SerializedName("MaxPrice")
    var maxPrice: Double
):Parcelable

