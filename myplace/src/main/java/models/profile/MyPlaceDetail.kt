package models.profile

data class MyPlaceDetail(
    val CreatedOn: Any,
    val Id: Int,
    val JobNo: String,
    val JobType: Any,
    val MyPlaceEmails: List<MyPlaceEmail>,
    val Password: String,
    val Region: String,
    val UpdatedOn: Any,
    val UserId: Int,
    val UserName: String
)