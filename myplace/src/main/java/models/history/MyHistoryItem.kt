package models.history

data class MyHistoryItem(
    val authorname: String? = null,
    val body: String? =null,
    val byclient: Boolean,
    val documents: List<Any>,
    val notedate: String,
    val noteid: Int,
    val replies: List<MyHistoryItem>? = null,
    val replytoid: Int? = null,
    val subject: String? = null
)

