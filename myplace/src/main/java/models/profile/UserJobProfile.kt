package models.profile

data class UserJobProfile(
    val CentralLoginPassword: String,
    val Email: Any,
    val InvitedUser: Boolean,
    val IsCentralLoginUser: Boolean,
    val IsEmailNotMapped: Boolean,
    val IsMultipleEmails: Boolean,
    val IsMultipleJobs: Boolean,
    val IsNewUser: Boolean,
    val JobNumber: Any,
    val Message: String,
    val MyPlacePassword: Any,
    val PassCode: Any,
    val PassCodeAlreadySent: Boolean,
    val PassCodeExpired: Boolean,
    val Success: Boolean,
    val UserDetails: List<UserJobDetail>
)