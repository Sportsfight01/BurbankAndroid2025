package models.notifications

import java.io.Serializable

/**
 * Created by jaya.krishna on 05-06-2017.
 */
class NotificationModel : Serializable {
    var isStage = false
    var isPhoto = false
    var isRead = false
    var taskId = 0
    var progressPosition = 0
    var date: String? = null
    var daysTag: String? = null
    var heading: String? = null
    var currentStage: String? = null
    var oldStage: String? = null
    var days = 0
    var minutes = 0

    constructor() {}
    constructor(date: String?, read: String, daysTag: String?) {
        isPhoto = true
        this.date = date
        isRead = read.equals("true", ignoreCase = true)
        this.daysTag = daysTag
    }

    constructor(date: String?, read: String, daysTag: String?, days: Int, minutes: Int) {
        isPhoto = true
        this.date = date
        isRead = read.equals("true", ignoreCase = true)
        this.daysTag = daysTag
        this.days = days
        this.minutes = minutes
    }

    constructor(
        heading: String?,
        progressPosition: Int,
        taskId: Int,
        date: String?,
        read: String,
        daysTag: String?
    ) {
        this.heading = heading
        this.progressPosition = progressPosition
        this.taskId = taskId
        this.date = date
        isRead = read.equals("true", ignoreCase = true)
        this.daysTag = daysTag
        isPhoto = false
        isStage = false
    }

    constructor(
        heading: String?,
        progressPosition: Int,
        taskId: Int,
        date: String?,
        read: String,
        daysTag: String?,
        days: Int,
        minutes: Int
    ) {
        this.heading = heading
        this.progressPosition = progressPosition
        this.taskId = taskId
        this.date = date
        isRead = read.equals("true", ignoreCase = true)
        this.daysTag = daysTag
        isPhoto = false
        isStage = false
        this.days = days
        this.minutes = minutes
    }

    constructor(
        heading: String?,
        progressPosition: Int,
        taskId: Int,
        date: String?,
        read: String,
        currentStage: String?,
        oldStage: String?,
        daysTag: String?
    ) {
        this.heading = heading
        this.progressPosition = progressPosition
        this.taskId = taskId
        this.date = date
        isRead = read.equals("true", ignoreCase = true)
        this.currentStage = currentStage
        this.oldStage = oldStage
        this.daysTag = daysTag
        isPhoto = false
        isStage = true
    }

    constructor(
        heading: String?,
        progressPosition: Int,
        taskId: Int,
        date: String?,
        read: String,
        currentStage: String?,
        oldStage: String?,
        daysTag: String?,
        days: Int,
        minutes: Int
    ) {
        this.heading = heading
        this.progressPosition = progressPosition
        this.taskId = taskId
        this.date = date
        isRead = read.equals("true", ignoreCase = true)
        this.currentStage = currentStage
        this.oldStage = oldStage
        this.daysTag = daysTag
        isPhoto = false
        isStage = true
        this.days = days
        this.minutes = minutes
    }
}