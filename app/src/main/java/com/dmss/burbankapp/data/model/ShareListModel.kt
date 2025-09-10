package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class ShareListModel(
    @SerializedName("FullName")
    var fullName: String,
    @SerializedName("Email")
    var email: String,
    @SerializedName("FavouriteAdded")
    var favouriteAdded: Boolean,
    @SerializedName("AcceptStatus")
    var acceptStatus: Int

)