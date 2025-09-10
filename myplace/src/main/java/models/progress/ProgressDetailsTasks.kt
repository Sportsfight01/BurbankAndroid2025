package models.progress

import com.dmss.burbankappold.dashboard.ui.home.ProgressStage

data class ProgressDetailsTasks(
    val name: String,
    val date: String,
    var status: String,
    var stage: ProgressStage?,
    var stageName: String?

)