package models.progress

import android.os.Parcelable
import com.dmss.burbankappold.dashboard.ui.home.ProgressStage
import kotlinx.parcelize.Parcelize

data class ProgressNewData(
    var stageName: String?,
    var taskName: String?,

    var desc : String ="",
//    var tasksCompleted : Int = 0,
//    var totalTasks : Int = 0,
//    var progressStage: ProgressStage,
    var completedStages: List<TaskCompletedData>,
//    var constructionContractData:ConstructionContract,
//    var preconstructionContractData:PreconstructionContract

//    var list: List<UserJobProgressItem>

)
