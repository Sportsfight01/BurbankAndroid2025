package com.dmss.burbankapp.utils

import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.dmss.burbankapp.data.model.*
import java.lang.reflect.Executable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

object AppConstants {
//    const val WEB_BASE_URL = "https://www.burbank.com.au"
    const val WEB_BASE_URL = "http://10.6.45.14:8085"
    const val ENQUIRY_BASE_URL = "https://share.hsforms.com/"
    val PREVIOUS_QUESTION: String = "PREVIOUS_QUESTION"
    const val GOOGLE_MAPS_APP = "com.google.android.apps.maps"
    const val HUAWEI_MAPS_APP = "com.huawei.maps.app"
    var newHomesNextFeatures: NewHomesNextFeaturesModel? = null
    var BACKSTACK_COUNT: Int = 0
    var dummyLatitude = "-37.811929"
    var dummyLongitude = "144.718279"
    var currentLatitude = "0.0"
    var currentLongitude = "0.0"
    var displayHomeSubHeader = ""
    var intialId=0
    var MyFavouriteFlow = "MyFavouriteFlow"

    var totalminimumPrice = 0
    var totalmaximumPrice = 0

    var minimumPriceForPriceRange: Double? = null
    var maximumPriceForPriceRange: Double? = null

    var priceRangeMinimum: Float? = 0f
    var priceRangeMaxmimu: Float? = 0f
    var homeLandregionsData: HnLPackagesModel? = null

    var selectedminimumPriceForPriceRange: Double? = 0.0
    var selectedmaximumPriceForPriceRange: Double? = 0.0

    var PUSH_NOTIFICATION: String = "PUSH_NOTIFICATION"
    val USER_CREATED: String? = "USER_CREATED"

    //const val PROFILEPIC_BASE = "https://www.burbank.com.au"
    const val MYPLACETHREED_BASE = WEB_BASE_URL

    //http://10.6.45.14:8081/api/api/"
    const val PROFILEPIC_BASE = WEB_BASE_URL
    const val USER_FAVORITE_DISPLAY_MODEL: String = "UserFavoritesDisplayModel"

    var SELECTED_REGION_DISPLAY = "SELECTED_REGION_DISPLAY"


    const val QUIZLISTMODEL: String = "QUIZLISTMODEL"
    const val NEWHOMELISTMODEL: String = "NEWHOMELISTMODEL"
    const val NEWHOMELISTMODELLIST: String = "NEWHOMELISTMODELLIST"

    const val NEWHOMENEXTQUESTIONMODEL: String = "NEWHOMENEXTQUESTIONMODEL"
     var newHomeNextFeatureResponseModel: NewHomeNextFeatureResponseModel? =null

    internal val EMPTY_EMAIL_ERROR = 1001
    internal val INVALID_EMAIL_ERROR = 1002
    internal val EMPTY_PASSWORD_ERROR = 1003
    internal val LOGIN_FAILURE = 1004
    internal val NULL_INDEX = -1L
    internal val HOME_LAND_PACKAGE_OBJECT = "HOME_LAND_PACKAGE_OBJECT"
    internal val HNLPACKAGESMODEL = "HNLPACKAGESMODEL"
    internal val PACKAGESLIST = "PACKAGESLIST"

    internal val MYCOLLECTION_RECENT_SEARCH_KEY = "MYCOLLECTION_RECENT_SEARCH_KEY"


    internal var MIN_PRICE = ""
    internal var MIN_PRICE_SELECTED = ""
    internal var MAX_PRICE_SELECTED = ""

    internal var MAX_PRICE = ""
    internal var SELECTED_MIN_PRICE = "0.0"
    internal var SELECTED_MAX_PRICE = "0.0"
    internal var headerTextStr = ""
    internal var TotalMyFavs = 0

    internal var updateDisplayHomesFav = false

    internal var Bathrooms: Array<Int>? = null
    internal var BedRooms: Array<Int>? = null
    internal var CarSpaces: Array<Int>? = null
    internal var BEDROOM_COUNT = 0
    internal var SELECTED_BEDROOM_COUNT = 0

    var HOMEANDLAND_TAP: Int = 0
    // internal val BedRooms = arrayOf(3, 4, 5, 6)

    var REGION_HEADER = ""
    var STOREYS_HEADER = ""
    var BEDROOMS_HEADER = ""
    var PRICE_RANGE_HEADER = ""
    internal var PACKAGES_COUNT = 0
    internal var ISFROM_SKIP = false

    //PREVIOUS SELECTED
    var SELECTED_STOREY = -1
    var SELECTED_REGION = ""
    var SELECTED_STOREY_PREVIOUS = 0

    internal var SELECT_FAVORITE = "SELECT_FAVORITE"


    //HOME DESIGN
    internal var NEWHOMESHOUSECOUNT = 0
    internal var newHomeQuizList: ArrayList<NewHomeQuizListModel> = ArrayList()
    internal var newHomeJsonObjectsList: ArrayList<NewHomeJsonObject> = ArrayList()

    internal var newHomeHashMap: HashMap<String, ArrayList<NewHomeJsonObject>> = LinkedHashMap()
    internal var newHomeHashMapDashboard: HashMap<String, ArrayList<NewHomeJsonObject>> = LinkedHashMap()

    internal var homeAndLandMap: HashMap<String, String> = LinkedHashMap()
    internal var newHomeHashMapNextFeature: HashMap<String, NewHomeJsonObject> = HashMap()

    internal var toolheaderHashMap: HashMap<String, String> = LinkedHashMap()
    internal var breadCrumbMyCollection: MutableMap<String, BreadCrumbHashMapModel> = LinkedHashMap()

    //HEADERS
    internal var MY_COLLECTION_STOREYS = ""
    internal var MY_COLLECTION_COLLECTIONS = 0
    internal var QUESTION_ORDER = 0

    internal var I_MUST_HAVE_THIS = "I must have this"
    internal var I_DONT_MIND = "I don't mind"
    internal var I_DONT_WANT_THIS = "I do not want this"

    internal var toolheaderHashMapHomeLand: HashMap<String, String> = LinkedHashMap()

    internal var headerTextViewMap: HashMap<String, TextView> = LinkedHashMap()

    const val REGION: String = "REGION"
    const val STOREYS: String = "STOREYS"
    const val BEDROOMS: String = "BEDROOMS"
    const val PRICERANGE: String = "PRICERANGE"

    var isFirstMyCollection: Boolean = false
    var isPackagesFromProfile: Boolean = false

    var isHomeLandProfile: Boolean = true

    //Profile Notification Count
    var SHARE_ACCOUNT_NOTIFICATION: String = "0"
    var MY_COLLECTION_NOTIFICATION: String = "0"
    var HOME_AND_LAND_NOTIFICATION: String = "0"
    var HOME_DESIGN_NOTIFICATION: String = "0"
    var MY_SAVED_DISPLAY: String = "0"


    //HOME AND LAND FILTER
    internal var FILTER_SELECTED_STOREY = -1
    internal var FILTER_MIN_PRICE = ""
    internal var FILTER_MAX_PRICE = ""

    internal var FILTER_Bathrooms: Array<Int>? = null
    internal var FILTER_BedRooms: Array<Int>? = null
    internal var FILTER_CarSpaces: Array<Int>? = null
    internal var FILTER_BEDROOM_COUNT = 0
    internal var FILTER_PRICE_RANGE_HEADER = ""
    internal var FILTER_toolHeaderHashMapHomeLand: HashMap<String, String> = LinkedHashMap()


    internal var previouslySelectedMinPrice: Double? = null
    internal var previouslySelectedMaxPrice: Double? = null

    internal var HouseCount = 0
    internal var isRecentDialogShowed = false
    internal var MAINHEADER = ""
    internal var FRAGMENTTAG = ""

    internal var storedFragments  = HashMap<String, Fragment>()

    fun setRecentSearchData(searchJsonModel: SearchJsonModel) {
        var regionName = ""
        var bedroomString = ""
        var storeyString = ""
        var minRangeValue: Int?
        var maxRangeValue: Int?
        ISFROM_SKIP = false

        if (searchJsonModel.regionsList.size > 0) {
            regionName = searchJsonModel.regionsList.joinToString(","){it.regionName}
        }

        searchJsonModel.minPrice.let { it1 ->
            minRangeValue = it1.toInt() / 1000
        }
        searchJsonModel.maxPrice.let { it1 ->
            maxRangeValue = it1.toInt() / 1000
        }

        PRICE_RANGE_HEADER =
            "$" + minRangeValue + "K-" + "$" + maxRangeValue + "K"
        toolheaderHashMapHomeLand[REGION] =
            regionName
        toolheaderHashMapHomeLand[PRICERANGE] =
            PRICE_RANGE_HEADER
        REGION_HEADER = regionName
        MIN_PRICE = searchJsonModel.minPrice.toString()
        MAX_PRICE = searchJsonModel.maxPrice.toString()

        val storeyFilter = searchJsonModel.StoreyFilters.filter { it.IsChecked }
        var count = -1
        if (storeyFilter.size>1){
            storeyString = "All"
        }else if (storeyFilter.size == 1) {
            storeyFilter.forEach {
                if (it.IsChecked) {
                    count = it.value
                    storeyString = it.displayName
                    return@forEach
                }

            }
        }
        toolheaderHashMapHomeLand[STOREYS] = storeyString
        STOREYS_HEADER = storeyString
        SELECTED_STOREY = count
        val bedRooms = searchJsonModel.BedRoomFilters.filter { it.IsChecked }
        when(bedRooms.size){
            1 ->{
                bedRooms.forEach {
                    BedRooms = arrayOf(it.value)
                    BEDROOM_COUNT = it.value
                    bedroomString = "${it.value} Bed"
                }
            }
            2 ->{
                BedRooms = arrayOf(5,6)
                BEDROOM_COUNT = 5
                bedroomString = "5+ Bed"
            }
            else ->{
                BedRooms = arrayOf(3,4,5,6)
                BEDROOM_COUNT = -1
                bedroomString = "All"
            }
        }
        toolheaderHashMapHomeLand[BEDROOMS] =
            bedroomString
        BEDROOMS_HEADER = bedroomString

        val bathroomFilter = searchJsonModel.BathRoomFilters.filter { it.IsChecked }
        if(bathroomFilter.size>1)
            Bathrooms = arrayOf(2,3)
        else {
            bathroomFilter.forEach{
                when (it.value) {
                    2 -> if (it.IsChecked) Bathrooms = arrayOf(2)
                    3 -> if (it.IsChecked) Bathrooms = arrayOf(3)
                }
            }
        }

        searchJsonModel.CarSpaces.forEach {
            when(it.value){
                1 -> if (it.IsChecked) CarSpaces = arrayOf(1)
                2 -> if (it.IsChecked) CarSpaces = arrayOf(2)
                else -> CarSpaces = arrayOf(1,2)
            }
        }
    }
   fun clearCarspaceBathroomsdata(){
       FILTER_CarSpaces=null
       FILTER_Bathrooms=null
       CarSpaces= arrayOf(1,2)
       Bathrooms= arrayOf(1,2,3,4,5,6)
   }
    fun removeNextBreadCrumbItem(breadCrumbKey: String) {
        val newHashMap: HashMap<String, BreadCrumbHashMapModel> = LinkedHashMap()
        if (breadCrumbMyCollection.keys.size > 0) {
            val breadCrumbIterator: Iterator<*> =
                breadCrumbMyCollection.keys.iterator()
            while (breadCrumbIterator.hasNext()) {
                val key = breadCrumbIterator.next() as String
                var value = breadCrumbMyCollection[key]
                if (key == breadCrumbKey) {
                    if (value != null) {
                        newHashMap[key] = value
                    }
                }
            }

            if (newHashMap.size > 0) {
                breadCrumbMyCollection.clear()
                val newBreadCrumbIterator: Iterator<*> = newHashMap.keys.iterator()
                while (newBreadCrumbIterator.hasNext()) {
                    val key = newBreadCrumbIterator.next() as String
                    val value = newHashMap[key]
                    if (value != null) {
                        breadCrumbMyCollection[key] = value
                    }
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun removeCurrentElement(breadCrumbKey : String) {
        try {
            var removeAllItems = false
            val it: MutableIterator<Map.Entry<String, BreadCrumbHashMapModel>> = breadCrumbMyCollection.entries.iterator()
            while (it.hasNext()) {
                if (it.next().key == breadCrumbKey) {
                    removeAllItems = true
                }
                if (removeAllItems) it.remove()
            }

            var bredCrump = -1
            if (newHomeJsonObjectsList.size > 0) {
                newHomeJsonObjectsList.forEachIndexed { index, newHomeJsonObject ->
                    if (newHomeJsonObject.feature == breadCrumbKey){
                        bredCrump = index
                    }
                }
                if (bredCrump != -1)
                    newHomeJsonObjectsList.subList(bredCrump, newHomeJsonObjectsList.size).clear()
            }

            var removeAllFrag = false
            val fragList: MutableIterator<Map.Entry<String, Fragment>> = storedFragments.entries.iterator()
            while (fragList.hasNext()) {
                if (fragList.next().key == breadCrumbKey) {
                    removeAllFrag = true
                }
                if (removeAllFrag) fragList.remove()
            }

            var removeHashMap = false
            val hashMapList: MutableIterator<Map.Entry<String, ArrayList<NewHomeJsonObject>>> = newHomeHashMap.entries.iterator()
            while (hashMapList.hasNext()) {
                if (hashMapList.next().key == breadCrumbKey) {
                    removeHashMap = true
                }
                if (removeHashMap) hashMapList.remove()
            }

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    fun removeNextBredCrumpItem(feature: String, list: ArrayList<NewHomeJsonObject>): ArrayList<NewHomeJsonObject>{
        var pos = -1
        list.forEachIndexed { index, newHomeJsonObject ->
            if (newHomeJsonObject.feature == feature){
                pos = index
            }
        }
        if(pos != -1)
            list.subList(pos+1, list.size).clear()
        return list
    }

    fun removeLastElementOfHashMap(key : String) {
        val listKeys: LinkedList<String> = LinkedList()
        val listKeysToolHeader: LinkedList<String> = LinkedList()
        listKeys.addAll(newHomeHashMap.keys)
        listKeysToolHeader.addAll(toolheaderHashMap.keys)
        if (listKeys.size > 0) {
            newHomeHashMap.remove(key)
        }
        if (listKeysToolHeader.size > 0) {
            toolheaderHashMap.remove(key)
        }
    }

}

