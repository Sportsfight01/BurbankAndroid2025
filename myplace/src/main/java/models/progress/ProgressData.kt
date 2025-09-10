package models.progress

import android.os.Parcelable
import com.dmss.burbankappold.dashboard.ui.home.ProgressStage
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProgressData(
    var name: String,
    var date: String? ="",
    var progress: Int,
    var desc : String ="",
    var tasksCompleted : Int = 0,
    var totalTasks : Int = 0,
    var progressStage: ProgressStage,
    var list: List<UserJobProgressItem>
) : Parcelable
