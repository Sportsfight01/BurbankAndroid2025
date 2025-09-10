package models

data class LoginRequestBody(
    var JobNumber: String,
    var UserName: String,
    var Region: String,
    var Password: String
)