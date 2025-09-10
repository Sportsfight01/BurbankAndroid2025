package models.profile

data class NotificationType(
    val Description: String,
    val IsActive: Boolean,
    val IsUserOpted: Boolean,
    val Name: String,
    val NotificationTypeId: Int
)