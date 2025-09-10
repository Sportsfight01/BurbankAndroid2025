package com.dmss.burbankapp.data.model

data class ResponseModel (
    val id: Int,
    val vote_average: Double,
    val title: String,
    val overview: String,
    val adult: Boolean
)
data class ResponseListModel(
    val results: List<ResponseModel>
)