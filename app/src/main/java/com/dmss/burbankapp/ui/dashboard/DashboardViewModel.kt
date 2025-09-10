package com.dmss.burbankapp.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dmss.burbankapp.data.api.ApiHelper
import com.dmss.burbankapp.data.api.NoConnectivityException
import com.dmss.burbankapp.data.apiUtils.Resource
import com.dmss.burbankapp.data.local.entity.DatabaseHelper
import com.dmss.burbankapp.data.model.*
import com.google.gson.JsonObject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import timber.log.Timber

class DashboardViewModel(
    private val apiHelper: ApiHelper,
    private val dbHelper: DatabaseHelper
) : ViewModel() {
    private val statesLiveData = MutableLiveData<Resource<ArrayList<StateModel>>>()
    private val regionsLiveData = MutableLiveData<Resource<ArrayList<StateRegionModel>>>()
    private val userDetailLiveData = MutableLiveData<Resource<UserDetailResponseModel>>()
    private val shareAccountLiveData = MutableLiveData<Resource<ShareAccountModel>>()
    private val pendingShareAccountLiveData = MutableLiveData<Resource<ShareAccountModel>>()
    private val checkEmailSharingLiveData = MutableLiveData<Resource<CheckEmailForSharingModel>>()
    private var fetchShareAccWithEmailLiveData = MutableLiveData<Resource<ShareAccountModel>>()
    private var searchTypeLiveData = MutableLiveData<Resource<SearchTypeRecentModel>>()
    private val recentSearchDataLive =
        MutableLiveData<Resource<RecentSearchDataResponseModel>>()
    private val recentSearchMyCollectionDataLive =
        MutableLiveData<Resource<MyCollectionRecentSearchModel>>()
    private val profilePictureLiveData = MutableLiveData<Resource<UpdateProfilePicModel>>()
    private val updateUserInfoLiveData = MutableLiveData<Resource<UserInfo>>()

    fun getUpdateUser(): LiveData<Resource<UserInfo>> {
        return updateUserInfoLiveData
    }

    fun getProfilePicLiveData(): LiveData<Resource<UpdateProfilePicModel>> {
        return profilePictureLiveData
    }

    fun getRecentSearchMyCollectionLiveData(): LiveData<Resource<MyCollectionRecentSearchModel>> {
        return recentSearchMyCollectionDataLive
    }

    fun getRecentSearchLiveData(): LiveData<Resource<RecentSearchDataResponseModel>> {
        return recentSearchDataLive
    }

    fun getSearchTypeLiveData(): LiveData<Resource<SearchTypeRecentModel>> {
        return searchTypeLiveData
    }


    fun getShareAccWithEmailLiveData(): LiveData<Resource<ShareAccountModel>> {
        return fetchShareAccWithEmailLiveData
    }


    fun getCheckEmailSharingLiveData(): LiveData<Resource<CheckEmailForSharingModel>> {
        return checkEmailSharingLiveData
    }

    fun getShareAccountLiveData(): LiveData<Resource<ShareAccountModel>> {
        return shareAccountLiveData
    }

    fun getPendingShareAccountLiveData(): LiveData<Resource<ShareAccountModel>> {
        return pendingShareAccountLiveData
    }


    fun getStatesLiveData(): LiveData<Resource<ArrayList<StateModel>>> {
        return statesLiveData
    }

    fun getUserDetailsLiveData(): LiveData<Resource<UserDetailResponseModel>> {
        return userDetailLiveData
    }

    fun getRegionsLiveData(): LiveData<Resource<ArrayList<StateRegionModel>>> {
        return regionsLiveData
    }

    fun fetchStates() {
        viewModelScope.launch {
            statesLiveData.postValue(Resource.loading(null))
            try {
                val stateLocalData = dbHelper.getAllStates()
                if (stateLocalData.isNotEmpty()) {
                    Timber.e("From Local --->${stateLocalData.size}")
                    statesLiveData.postValue(Resource.success(ArrayList(stateLocalData)))
                } else {
                    val statesData = apiHelper.getAllStates()
                    dbHelper.insertStates(statesData.statesList.toList())
                    Timber.e("API Data --->${statesData.statesList}")
                    statesLiveData.postValue(Resource.success(statesData.statesList))
                }

            } catch (e: TimeoutCancellationException) {
                statesLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: NoConnectivityException) {
                statesLiveData.postValue(Resource.error(e.message, null))
            } catch (e: Exception) {
                statesLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchUserDetails(userId: Int) {
        viewModelScope.launch {
            userDetailLiveData.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.getUserDetails(userId)
                userDetailLiveData.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                userDetailLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                userDetailLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )

            } catch (e: NoConnectivityException) {
                userDetailLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun fetchShareAccountDetails(userId: Int) {
        viewModelScope.launch {
            shareAccountLiveData.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.setShareAccount(userId)
                shareAccountLiveData.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                shareAccountLiveData.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                shareAccountLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            } catch (e: NoConnectivityException) {
                shareAccountLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun pendingShareAccounts(userId: Int) {
        viewModelScope.launch {
            pendingShareAccountLiveData.postValue(Resource.loading(null))
            try {
                val statesData = apiHelper.setShareAccount(userId)
                pendingShareAccountLiveData.postValue(Resource.success(statesData))
            } catch (e: TimeoutCancellationException) {
                pendingShareAccountLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                pendingShareAccountLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            } catch (e: NoConnectivityException) {
                pendingShareAccountLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun checkEmailForSharing(emailId: String) {
        viewModelScope.launch {
            checkEmailSharingLiveData.postValue(Resource.loading(null))
            try {
                val checkEmailData = apiHelper.checkEmailForSharing(emailId)
                checkEmailSharingLiveData.postValue(Resource.success(checkEmailData))
            } catch (e: TimeoutCancellationException) {
                checkEmailSharingLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                checkEmailSharingLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun fetchShareAccountWithEmail(userId: Int, jsonObject: JsonObject) {
        viewModelScope.launch {
            fetchShareAccWithEmailLiveData.postValue(Resource.loading(null))
            try {
                val checkEmailData = apiHelper.setShareAccountWithEmail(userId, jsonObject)
                fetchShareAccWithEmailLiveData.postValue(Resource.success(checkEmailData))
            } catch (e: TimeoutCancellationException) {
                fetchShareAccWithEmailLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                fetchShareAccWithEmailLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun getSearchType() {
        viewModelScope.launch {
            searchTypeLiveData.postValue(Resource.loading(null))
            try {
                val searchTypeData = apiHelper.getSearchType()
                searchTypeLiveData.postValue(Resource.success(searchTypeData))
            } catch (e: TimeoutCancellationException) {
                searchTypeLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                searchTypeLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun getRecentSearchData(userId: Int, typeId: Int, stateId: Int) {
        viewModelScope.launch {
            recentSearchDataLive.postValue(Resource.loading(null))
            try {
                val favoriteListData = apiHelper.getRecentSearchData(userId, typeId, stateId)
                recentSearchDataLive.postValue(Resource.success(favoriteListData))
            } catch (e: TimeoutCancellationException) {
                recentSearchDataLive.postValue(Resource.error("TimeoutCancellationException", null))
            } catch (e: Exception) {
                recentSearchDataLive.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }

    fun getRecentSearchDataMyCollection(userId: Int, typeId: Int, stateId: Int) {
        viewModelScope.launch {
            recentSearchMyCollectionDataLive.postValue(Resource.loading(null))
            try {
                val favoriteListData =
                    apiHelper.getRecentSearchDataMyCollection(userId, typeId, stateId)
                recentSearchMyCollectionDataLive.postValue(Resource.success(favoriteListData))
            } catch (e: TimeoutCancellationException) {
                recentSearchMyCollectionDataLive.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                recentSearchMyCollectionDataLive.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }

    }

    fun updateImageApi(userId: Int, photoString: String) {
        viewModelScope.launch {
            profilePictureLiveData.postValue(Resource.loading(null))
            try {
                val user = JsonObject()
                user.addProperty("UserId", userId)
                user.addProperty("ImageContent", photoString)

                val updateProfilePicApi = apiHelper.updateProfileImage(user)
                profilePictureLiveData.postValue(Resource.success(updateProfilePicApi))
            } catch (e: TimeoutCancellationException) {
                profilePictureLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                profilePictureLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }
    }

    fun updateUserDetailsApi(jsonObject: JsonObject) {
        viewModelScope.launch {
            updateUserInfoLiveData.postValue(Resource.loading(null))
            try {
                val updateProfilePicApi = apiHelper.updateUserInfo(jsonObject)
                updateUserInfoLiveData.postValue(Resource.success(updateProfilePicApi))
            } catch (e: TimeoutCancellationException) {
                updateUserInfoLiveData.postValue(
                    Resource.error(
                        "TimeoutCancellationException",
                        null
                    )
                )
            } catch (e: Exception) {
                updateUserInfoLiveData.postValue(
                    Resource.error(
                        "Something went wrong!! Please try again later",
                        null
                    )
                )
            }
        }


    }

    suspend fun uploadImageApiCall() {


    }

}