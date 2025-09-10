package com.dmss.burbankapp.data.model

data class CurrentVersionData(

    val appVersionData:ArrayList<AppVersionModel>
)
data class AppVersionModel (
    val Id: String,
    val AppType: String,
    val AppVersion: String
)
