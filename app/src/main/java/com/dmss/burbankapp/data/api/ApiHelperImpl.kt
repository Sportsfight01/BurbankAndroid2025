package com.dmss.burbankapp.data.api

import com.dmss.burbankapp.data.model.*
import com.google.gson.JsonObject

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    override suspend fun getEmailExist(email: String) = apiService.cheEmailExist(email)
    override suspend fun getMoreUsers() = apiService.getMoreUsers()
    override suspend fun getUsersWithError() = apiService.getUsersWithError()
    override suspend fun getLoginApi(email: String, pass: String, body: JsonObject) =
        apiService.getUserLogin(body)

    override suspend fun signUpApi(body: JsonObject) = apiService.signUpApi(body)
    override suspend fun updateProfileImage(body: JsonObject) = apiService.updateProfileImage(body)
    override suspend fun forgetPassword(body: String) = apiService.forgetPassword(body)

    /*ALL PACKAGES*/
    override suspend fun getAllPackages(StateId: String) = apiService.getAllPackages(StateId)
    override suspend fun getAllStates() = apiService.getStates()
    override suspend fun getAllRegions(StateId: Int) = apiService.getRegions(StateId)
    override suspend fun getRecentSearch(
        userId: String,
        typeId: String,
        stateId: String
    ) = apiService.fetchRecentSearch(userId, typeId, stateId)

    override suspend fun SaveOrUpdateDeviceDetails(
        jsonObject: JsonObject
    )= apiService.SaveOrUpdateDeviceDetails(jsonObject)

    override suspend fun getPromotionsetails(stateId: String)=apiService.getPromotionsDetails(stateId)

    override suspend fun getHomeAndLandQuizDesign(
        page: String,
        body: JsonObject
    ) = apiService.getHomeAndLandQuizDesigns(page, body)

    override suspend fun setFavorite(body: JsonObject) = apiService.setFavorite(body)
    override suspend fun getHnLPackages(userId: Int, body: JsonObject) =
        apiService.getHnLPackages(userId, body)

    override suspend fun getMinAndMaxPrice(body: JsonObject) = apiService.getMinAndMaxPrice(body)
    override suspend fun getAllFavorites(body: JsonObject) = apiService.getAllFavorites(body)
    override suspend fun submitEnquireForm(body: JsonObject) = apiService.getEnquireForm(body)
    override suspend fun getUserDetails(userId: Int) = apiService.getUserDetails(userId)
    override suspend fun setShareAccount(userId: Int) = apiService.setShareAccount(userId)

    override suspend fun checkEmailForSharing(emailId: String) =
        apiService.checkEmailForSharing(emailId)

    override suspend fun setShareAccountWithEmail(
        userId: Int,
        jsonObject: JsonObject
    ) = apiService.shareAccountWithEmail(userId, jsonObject)

    override suspend fun getSearchType() = apiService.getSearchType()
    override suspend fun getRecentSearchData(
        userId: Int,
        typeId: Int,
        stateId: Int
    ) = apiService.getRecentSearchData(userId, typeId, stateId)

    override suspend fun setResetPassword(jsonObject: JsonObject) =
        apiService.resetPassword(jsonObject)

    override suspend fun setRecentSearch(
        jsonObject: RecentSearchObjectRequest
    ) = apiService.setRecentSearchData(jsonObject)

    override suspend fun setRecentSearchMyCollectionData(jsonObject: SetRecentMyCollectionRequest) =
        apiService.setRecentSearchMyCollectionData(jsonObject)


    override suspend fun fetchNewHomeQuiz(stateId: Int) = apiService.fetchNewHomesQuiz(stateId)
    override suspend fun fetchNewHomesNextFeature(jsonObject: MyCollectionQuizQuestionRequest) =
        apiService.fetchNewHomesNextFeature(jsonObject)

    override suspend fun fetchNewHomesNextFeatureMinLotWidth(
        jsonObject: MyCollectionQuizQuestionRequest,
        minLotWidth: String
    ) = apiService.fetchNewHomesNextFeatureMinLotWidth(jsonObject, minLotWidth)

    override suspend fun fetchHouseCount(jsonObject: JsonObject) =
        apiService.fetchHouseCountApi(jsonObject)

    override suspend fun updateUserInfo(jsonObject: JsonObject) =
        apiService.updateUserApi(jsonObject)

    override suspend fun userLogin(jsonObject: JsonObject) = apiService.getUser(jsonObject)
    override suspend fun getHouseNameDetailByName(
        HouseSize: Int,
        houseName: String,
        stateId: Int
    ) = apiService.getHouseDetailsByName(HouseSize, houseName, stateId)

    override suspend fun getRecentSearchDataMyCollection(
        userId: Int,
        typeId: Int,
        stateId: Int
    ) = apiService.getRecentSearchDataMyCollection(userId, typeId, stateId)

    override suspend fun getAllFavoritesMyCollection(body: JsonObject): AllFavoritesDesignModel =
        apiService.getAllFavoritesCollections(body)

    override suspend fun getHomeAndLandPackageDetailPageApi(packageId: Int): HomeAndLandPackageDetailModel =
        apiService.getHomeAndLandPackageDetails(packageId)

    override suspend fun getMyCollectionHnlDetailByName(
        stateId: Int,
        houseName: String
    ): MyCollectionHnlModel = apiService.getMyCollectionHnlDetailByName(stateId, houseName)

    override suspend fun fetchNewHomesNextFeatureNext(jsonObject: MyCollectionQuizQuestionRequest) =
        apiService.fetchNewHomesNextFeatureNext(jsonObject)

    override suspend fun getDisplaysByStateId(jsonObject: DisplyByStateIdBodyModel) =
        apiService.getDisplaysByStateId(
            jsonObject.stateId,
            jsonObject.latitude,
            jsonObject.longitude,
            jsonObject.userId,
            jsonObject.storey,
            jsonObject.houseName,
            jsonObject.houseSize
        )

    override suspend fun getNearByDisplaysByRegion(jsonObject: NearByPlaceDataObject) =
        apiService.getNearbyDisplays(
            jsonObject.stateId,
            jsonObject.latitude,
            jsonObject.longitude,
            jsonObject.userId
        )

    override suspend fun getHouseDetailsByEstate(jsonObject: HouseDetailByEstateObject) =
        apiService.getHouseDetailsByEstate(
            jsonObject.stateId,
            jsonObject.estateName,
            jsonObject.userID
        )

    override suspend fun fetchDisplayForRegionAndMap(jsonObject: JsonObject) =
        apiService.fetchDisplayForRegionAndMap(jsonObject)

    override suspend fun fetchRegionsByStateId(stateId: Int) =
        apiService.getRegionsByStateId(stateId)

    override suspend fun addHouseAppointment(obj: HouseAppointmentDataModelObject) =
        apiService.addHouseAppointment(
            obj
        )

    override suspend fun getUserFavoriteDisplays(
        userId: Int,
        stateId: Int
    ) = apiService.getUserFavoriteDisplays(userId, stateId)

    override suspend fun getDisplayHomesRegionByStateId(stateId: Int) =
        apiService.getDisplayHomesRegionByStateId(stateId)

}