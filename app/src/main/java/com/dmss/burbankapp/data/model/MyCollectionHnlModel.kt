package com.dmss.burbankapp.data.model

import com.google.gson.annotations.SerializedName

class MyCollectionHnlModel(
    @SerializedName("getpackagebyName")
    var hnlQuizList: ArrayList<HnLQuizPackageModel>
)