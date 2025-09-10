package models.progress

data class UserV2LoginData (
    val username:String = "",
    val password:String = "",
    val rememberMe:Boolean = false,
)