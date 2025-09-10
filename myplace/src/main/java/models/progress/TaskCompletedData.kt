package models.progress

data class TaskCompletedData(

    val taskId : String?,
    val taskName : String?,
    val completedDate : String?,
    val stageName : String?,
    val hidden : Boolean?
)
data class TaskCompletedInfoData(
    val stageName : String?,
    val noOfTasks : Int?,
    val noOfCompletedTasks : Double?,
    val percentage : Int?,

    val taskDetails:ArrayList<TasksData>,
    var userJobProgressItem: List<UserJobProgressItem>

)
data class TasksData(
    val completedDate : String,
    val taskId : String?,
    val taskName : String?,
)