package models.profile

data class MyPlaceEmail(
    val ContactId: Any,
    val Email: String,
    val FirstName: String,
    val FullName: String,
    val IsPrimaryUser: Boolean,
    val LastName: String,
    val Password: String,
    val UserName: String
)