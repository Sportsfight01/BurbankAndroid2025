package com.dmss.burbankapp.data.model

class RecentSearchObjectRequest(
    var UserId: String,
    var StateId: String,
    var TypeId: Int,
    var SearchTextJson: SearchTextJsonObjRequest

)