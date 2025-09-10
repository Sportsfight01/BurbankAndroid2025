package models.progress

data class ConstructionContract(
    val tasks:TaskData
)
data class TaskData(
    val list:List<ListData>
)
data class ListData(
    val taskName: String,
    val taskId: String,
    val completedDate: String?,
    val stage: StageData,
    val hidden: Boolean,
)
data class StageData(
    val stageName: String,
)