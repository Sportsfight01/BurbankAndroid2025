package com.dmss.burbankapp.data.model

class SearchTextJsonObjRequest(
    var SortBy: String,
    var MaxPrice: String,
    var MinPrice: String,
    var MinHomeSize: String,
    var MaxHomeSize: String,
    var BathRoomFilters: ArrayList<SearchSubModel>,
    var BedRoomFilters: ArrayList<SearchSubModel>,
    var CarSpaces: ArrayList<SearchSubModel>,
    var Regions: ArrayList<RegionsModel>,
    var StoreyFilters: ArrayList<SearchSubModel>


)