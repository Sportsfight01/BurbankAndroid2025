package models.profile

data class Result(
    val Email: String,
    val ImageContent: Any,
    val NotificationTypes: List<NotificationType>,
    val ProfilePicPath: String,
    val UpdatedDate: String,
    val UserId: Int,
    val UserName: String
)