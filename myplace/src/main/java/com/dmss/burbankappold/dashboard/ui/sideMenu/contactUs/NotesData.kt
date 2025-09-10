package com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotesData(val noteId: String?= null,
                     val noteType: String?= null,
                     val unknownAuthor: String?= null,
                     val author: authorData?= null,
                     val subject: String?= null,
                     val body: String?= null,
                     val activityDate: String? = null,
                     var replies: List<NotesData>? =null,
                     val conversations: list? = null,
                     val createdInMyHome:Boolean=true,
                     val contract:Contract?=null,
                     val replyTo:ReplyToData? = null ): Parcelable{
    val isValid get() = replyTo != null && noteId != null && noteType != null && unknownAuthor != null  && author != null
            && subject != null  && body != null  && activityDate != null
}

@Parcelize
data class  ReplyToData(val noteId:String):Parcelable

@Parcelize
data class  list(val list:List<NotesData>):Parcelable

@Parcelize
data class  authorData(val fullName:String):Parcelable

@Parcelize
data class Contract(val contractId:String):Parcelable