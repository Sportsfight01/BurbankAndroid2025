package com.dmss.burbankapp.ui.mydisplay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.ApplicationClass
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.api.NoConnectivityException
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.*
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch

class DisplayHomesViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {

    private val displaysByStateIdLiveData =
        MutableLiveData<Resource<DisplaysByStateIdResponseModel>>()
    private val fetchNearByPlacesLiveData = MutableLiveData<Resource<NearByDisplaysResponseModel>>()
    private val getHouseDetailsByEstateData = MutableLiveData<Resource<HouseDetailsResponseModel>>()
    private val getDisplayForRegionAndMap = MutableLiveData<Resource<DisplayRegionResponseModel>>()
    private val favoriteLiveData = MutableLiveData<Resource<FavoriteResponseModel>>()
    private val getHouseAppointmentLiveData =
        MutableLiveData<Resource<HouseAppointmentResponseModel>>()
    private val regionsByStateIdLiveData =
        MutableLiveData<Resource<RegionsByStateIdResponseModel>>()

    private val regionsLiveData = MutableLiveData<Resource<DisplayHomeRegionModel>>()

    fun getRegionsLiveData(): LiveData<Resource<DisplayHomeRegionModel>> {
        return regionsLiveData
    }

    private val _userFavoriteDisplaysLiveData =
        MutableLiveData<Resource<UserFavoriteDisplaysResponseModel>>()

    var stateId: Int = 0
    var userId: Int = 0

    val getUserFavoritesDisplaysLiveData: LiveData<Resource<UserFavoriteDisplaysResponseModel>>
        get() = _userFavoriteDisplaysLiveData


    init {
        var preferences = CustomSharedPreferences(ApplicationClass.applicationContext())
        stateId = preferences.getStateID()
        userId = preferences.getUserId()
    }

    fun getDisplaysByStateId(): LiveData<Resource<DisplaysByStateIdResponseModel>> {
        return displaysByStateIdLiveData
    }

    fun getHouseAppointmentLiveData(): LiveData<Resource<HouseAppointmentResponseModel>> {
        return getHouseAppointmentLiveData
    }

    fun getRegionsByStateIdLiveData(): LiveData<Resource<RegionsByStateIdResponseModel>> {
        return regionsByStateIdLiveData
    }

    fun getNearByPlacesLiveData(): LiveData<Resource<NearByDisplaysResponseModel>> {
        return fetchNearByPlacesLiveData
    }

    fun getHouseDetailsEstateData(): LiveData<Resource<HouseDetailsResponseModel>> {
        return getHouseDetailsByEstateData
    }

    fun getDisplayForRegionAndMap(): LiveData<Resource<DisplayRegionResponseModel>> {
        return getDisplayForRegionAndMap
    }

    fun getFavorite(): LiveData<Resource<FavoriteResponseModel>> {
        return favoriteLiveData
    }

    fun fetchDisplaysByStateId(jsonObject: DisplyByStateIdBodyModel) {
        viewModelScope.launch {
            displaysByStateIdLiveData.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.getDisplaysByStateId(jsonObject)
                displaysByStateIdLiveData.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                displaysByStateIdLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                displaysByStateIdLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )

            } catch (e: NoConnectivityException) {
                displaysByStateIdLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun fetchNearByPlacesApi(jsonObject: NearByPlaceDataObject) {
        viewModelScope.launch {
            fetchNearByPlacesLiveData.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.getNearByDisplaysByRegion(jsonObject)
                fetchNearByPlacesLiveData.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                fetchNearByPlacesLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                fetchNearByPlacesLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )

            } catch (e: NoConnectivityException) {
                fetchNearByPlacesLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }


    fun fetchHouseDetailsByEstate(jsonObject: HouseDetailByEstateObject) {
        viewModelScope.launch {
            getHouseDetailsByEstateData.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.getHouseDetailsByEstate(jsonObject)
                getHouseDetailsByEstateData.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                getHouseDetailsByEstateData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                getHouseDetailsByEstateData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )

            } catch (e: NoConnectivityException) {
                getHouseDetailsByEstateData.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun getDisplayForRegionAndMap(jsonObject: JsonObject) {
        viewModelScope.launch {
            getDisplayForRegionAndMap.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.fetchDisplayForRegionAndMap(jsonObject)
                getDisplayForRegionAndMap.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                getDisplayForRegionAndMap.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                getDisplayForRegionAndMap.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )

            } catch (e: NoConnectivityException) {
                getDisplayForRegionAndMap.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun fetchRegionsByStateId(stateId: Int) {
        viewModelScope.launch {
            displaysByStateIdLiveData.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.fetchRegionsByStateId(stateId)
                regionsByStateIdLiveData.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                regionsByStateIdLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                regionsByStateIdLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )

            } catch (e: NoConnectivityException) {
                regionsByStateIdLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun fetchHouseAppointment(obj: HouseAppointmentDataModelObject) {
        viewModelScope.launch {
            getHouseAppointmentLiveData.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.addHouseAppointment(obj)
                getHouseAppointmentLiveData.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                getHouseAppointmentLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                getHouseAppointmentLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )

            } catch (e: NoConnectivityException) {
                getHouseAppointmentLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun setFavorite(jsonObject: JsonObject) {
        viewModelScope.launch {
            favoriteLiveData.postValue(Resource.loading(null))
            try {
                val favoriteData = apiHelper.setFavorite(jsonObject)
                favoriteLiveData.postValue(Resource.success(favoriteData))
            } catch (e: TimeoutCancellationException) {
                favoriteLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                favoriteLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun getUserFavoritesDisplays() {
        viewModelScope.launch {
            _userFavoriteDisplaysLiveData.postValue(Resource.loading(null))
            try {
                val favoriteData = apiHelper.getUserFavoriteDisplays(userId, stateId)
                _userFavoriteDisplaysLiveData.postValue(Resource.success(favoriteData))
            } catch (e: TimeoutCancellationException) {
                _userFavoriteDisplaysLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                _userFavoriteDisplaysLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }
    fun getUserFavoritesDisplays(userId: Int,  stateId: Int) {
        viewModelScope.launch {
            _userFavoriteDisplaysLiveData.postValue(Resource.loading(null))
            try {
                val favoriteData = apiHelper.getUserFavoriteDisplays(userId, stateId)
                _userFavoriteDisplaysLiveData.postValue(Resource.success(favoriteData))
            } catch (e: TimeoutCancellationException) {
                _userFavoriteDisplaysLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                _userFavoriteDisplaysLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }
    fun fetchRegions(stateId: Int) {
        viewModelScope.launch {
            regionsLiveData.postValue(Resource.loading(null))
            try {
                val regionsData = apiHelper.getDisplayHomesRegionByStateId(stateId)
                regionsLiveData.postValue(Resource.success(regionsData))
            } catch (e: TimeoutCancellationException) {
                regionsLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                regionsLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchEnquire(jsonObject: JsonObject) {
        viewModelScope.launch {
            try {
                val enquireData = apiHelper.submitEnquireForm(jsonObject)
            } catch (e: TimeoutCancellationException) {
            } catch (e: Exception) {
            }
        }
    }

}