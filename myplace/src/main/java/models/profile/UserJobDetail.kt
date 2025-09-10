package models.profile

data class UserJobDetail(
    val CreatedOn: Any,
    val Email: String,
    val FirstName: String,
    val FullName: String,
    val Id: Int,
    val Image: String? = null,
    val IsActive: Boolean,
    val LastName: String,
    val MiddleName: String? = null,
    val Mobile: String,
    val MyPlaceDetails: List<MyPlaceDetail>,
    val Password: String? = null,
    val Region: String,
    val UpdatedOn: String? = null,
    val UserGuid: String,
    val isMyPlaceAccessible: Boolean
)