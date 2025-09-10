package com.dmss.burbankappold.dashboard.ui.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ProgressStage(val value: String) : Parcelable {
    HomStage("Your New Home"),
    AdminStage("Admin Stage"),
    Administration("Administration"),
    BaseStage("Base Stage"),
    FrameStage("Frame Stage"),
    LockupStage("Lockup Stage"),
    FixingStage("Fixing Stage"),
    FinishingStage("Finishing Stage")
}