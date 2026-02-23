package com.dmss.burbankappold.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.TypedValue
import android.widget.LinearLayout
import com.dmss.burbankappold.R
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.NotesData
import com.dmss.burbankappold.dashboard.ui.sideMenu.contactUs.list
import com.dmss.burbankappold.network.ApiRepository
import com.google.gson.JsonObject
import common.Utils
import models.photos.PhotosDataItem
import models.progress.ConstructionContract
import models.progress.PreconstructionContract
import models.progress.TaskCompletedInfoData
import models.progress.UserJobProgressNewItem
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

object AppConstants {
    var photosList = mutableListOf<List<PhotosDataItem>>()
    var documentsList = listOf<PhotosDataItem>()

    var filterList = listOf<PhotosDataItem>()
    var terplysData = listOf<NotesData>()
    var selectedTitlte = ""
    var backFromContcatcus = false
    var facade = "facade"
    var homeDesign = "false"
    var progressData: JSONObject? = null
    var financeItemStatus = false
    var constructionContractData: ConstructionContract? = null
    var preconstructionContractData: PreconstructionContract? = null
    var AppCookieContactUs = ""
    var contactusSearchEnable=false
    var liastOfNotesData = listOf<NotesData>()
    var UserJobProgressNewItem= ArrayList<UserJobProgressNewItem>()

    var contactUsGivenReply=false
    var contacts_allreadItems=0
    var contractStatusContractId=""
    var contacts_total_items=0
    var listConstructionStageCondition:ArrayList<TaskCompletedInfoData>?=null
    val Miscellaneous = "Miscellaneous"
    val Fixout_Stage = "Fixout Stage"
    val Completion = "Completion"
    val Handover = "Handover"
    val Finishing_Stage = "Finishing Stage"
    val Fixing_Stage = "Fixing Stage"
    val Base_Stage = "Base Stage"
    val Lockup_Stage = "Lockup Stage"
    val Frame_Stage = "Frame Stage"
    val Admin_Stage = "Admin Stage"
    val Administration = "Administration"

    val Your_New_Home = "Your New Home"

    /*val jsonNotesString="{" +
           "Notes:{" +
           "List:{" +
           "MetaData:{}" +
           "}" +
           "}" +
           "}"*/
    val jsonNotesString="{" +
            "AnyContract:{},"+
            "Notes: {" +
            "List: {" +
            "author : {" +
            "fullUser : {}" +
            "}," +
            "MetaData: {}" +
            "}" +
            "}" +
            "}"
    val jsonPreConstructionContract=
        "{"+"PRECONSTRUCTIONCONTRACT"+": {"+"TASKS"+": { "+"LIST"+ ":{ "+"STAGE"+": { } } }},"
    val jsonConstructionContract="CONSTRUCTIONCONTRACT"+": {"+"TASKS"+": { "+"LIST"+ ":{ "+"STAGE"+": { } } }}}"

    val jsonFacadeString="facade:{}}"
    val homeDesignString="{houseType:{brand:{},facade:{},documents:{list:{url:true}}}"
    val jsonDocumentsString="{" +
            "documents: {" +
            "list: {" +
            "url: true,thumbnailUrl: true,"+
            "MetaData: {}" +
            "}" +
            "}" +
            "}"
    var NoRecentPhotos = ""
    var PhotosSubheader = ""
    var savedphotosList = mutableListOf<List<PhotosDataItem>>()
    private fun sortBydateList(documentsList : List<NotesData>): List<NotesData> {
        val sortedList = documentsList.sortedByDescending {  it.activityDate }
        return sortedList
    }
    fun getContractId(resjsonObjectObj:JsonObject):String{
        var contractStatusContractId=""
        var leadContract="leadContract"
        var preconstructionContract="preconstructionContract"
        var constructionContract="constructionContract"
        var maintainceConstruction="mantainanceConstruction"
        var contractStatus="contractStatus"
        var contractId="contractId"
        var contractStatusVal =""
        var contaractStatusObjKey=""
       /* if(resjsonObjectObj.has(leadContract)) {
            contractStatusVal =
                resjsonObjectObj.getAsJsonObject(leadContract).get(contractStatus).asString
            contaractStatusObjKey=leadContract
        }
       else */if(resjsonObjectObj.has(preconstructionContract)) {
            contractStatusVal =
                resjsonObjectObj.getAsJsonObject(preconstructionContract)
                    .get(contractStatus).asString
            contaractStatusObjKey=preconstructionContract

        }
        else if(resjsonObjectObj.has(constructionContract)) {
            contractStatusVal =
                resjsonObjectObj.getAsJsonObject(constructionContract).get(contractStatus).asString
            contaractStatusObjKey=constructionContract

        }
        else if(resjsonObjectObj.has(maintainceConstruction)) {
            contractStatusVal =
                resjsonObjectObj.getAsJsonObject(maintainceConstruction).get(contractStatus).asString
            contaractStatusObjKey=maintainceConstruction

        }
//                val maintainceConstruction_contractStatus=resjsonObjectObj.getAsJsonObject("maintainceConstruction").get("contractStatus").asString

        if(contractStatusVal!=null && contractStatusVal!="") {
            contractStatusContractId =
                resjsonObjectObj.getAsJsonObject(contaractStatusObjKey).get(contractId).asString
        }
//        }else if(preconstructionContract_contractStatus!=null && preconstructionContract_contractStatus!=""){
//            contractStatusContractId=resjsonObjectObj.getAsJsonObject("preconstructionContract").get("contractId").asString
//        }else if(constructionContract_contractStatus!=null && constructionContract_contractStatus!=""){
//            contractStatusContractId=resjsonObjectObj.getAsJsonObject("constructionContract").get("contractId").asString
//        }
//                else if(maintainceConstruction_contractStatus!=null && maintainceConstruction_contractStatus!=""){
//                    contractStatusContractId=resjsonObjectObj.getAsJsonObject("maintainceConstruction").get("contractId").asString
//                }


        AppConstants.contractStatusContractId=contractStatusContractId
        return contractStatusContractId
    }
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    fun filterAndSortNotesData(NoReplayToData : List<NotesData>,filterReplayToData :List<NotesData>) : List<NotesData>{

        NoReplayToData.forEachIndexed {index, note ->
            var filterReplies=filterReplayToData.filter { it.replyTo!!.noteId==note.noteId }
            NoReplayToData[index].replies = filterReplies as ArrayList<NotesData>
            if(note.conversations?.list?.isNotEmpty() == true){
                NoReplayToData[index].replies = NoReplayToData[index].replies?.plus(note.conversations!!.list)
            }
            NoReplayToData[index].replies= NoReplayToData[index].replies?.let { sortBydateList(it) }
        }
        val NoReplayToDataSort= NoReplayToData.sortedWith <NotesData> (object : Comparator <NotesData> {
            override fun compare (note1: NotesData, note2: NotesData) : Int {
                var noteReply1Date: Date? = null
                if(note1.replies!=null && note1.replies!!.isNotEmpty()) {
                    noteReply1Date=  note1.replies?.first()?.activityDate?.convertDateFormatCompare(
                        ACTUAL_DATE_FORMAT
                    )
                }
                var noteReply2Date: Date? = null
                if(note2.replies!=null && note2.replies!!.isNotEmpty()) {
                    noteReply2Date=  note2.replies?.first()?.activityDate?.convertDateFormatCompare(
                        ACTUAL_DATE_FORMAT
                    )
                }
                var note1Date=note1.activityDate?.convertDateFormatCompare(ACTUAL_DATE_FORMAT)
                var note2Date=note2.activityDate?.convertDateFormatCompare(ACTUAL_DATE_FORMAT)

                if(noteReply1Date!=null && noteReply2Date!=null){
                    return  noteReply1Date!!.compareTo(noteReply2Date)
                }
                else if(noteReply1Date == null && noteReply2Date==null){
                    return  note1Date!!.compareTo(note2Date)
                }
                else if(noteReply1Date == null && noteReply2Date!=null){
                    return  note1Date!!.compareTo(noteReply2Date)
                }
                else if(noteReply1Date != null && noteReply2Date==null){
                    return noteReply1Date!!.compareTo( note2Date)
                }
                return -1
            }
        })

        return NoReplayToDataSort
    }
    fun showUpdateVewVersionAppDialog(activity: Context,updatedVersion:String) {
        var packageName = activity.packageName
        val dialogBuilder = AlertDialog.Builder(activity)
        val title = SpannableString("Update Available")
        title.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
            0,
            title.length,
            0
        )
        dialogBuilder.setTitle(title)//Should be in center
        dialogBuilder.setMessage("A new version ($updatedVersion) of the app is available. Please update to enjoy the latest features.")

            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${activity!!.resources.getColor(R.color.orange_bg_3_1)}'>Update</font>")) { dialog, _ ->
                dialog.cancel()

                //Removing All Saved Local Data

                try {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                } catch (e: ActivityNotFoundException) {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                }
                dialog.dismiss()
//                    activity.finish()
            }
        /*  .setNegativeButton(Html.fromHtml("<font color='#000000'>OK</font>")) { dialog, _ ->
              dialog.cancel()
          }*/

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        //alert.setTitle("Select ${state.name}?")
        // show alert dialog
        alert.show()

        val negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        val positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
         positiveButton.setAllCaps(false)
        negativeButton.setAllCaps(false)


        negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
        positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
        val layoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 10F
        positiveButton.layoutParams = layoutParams
        negativeButton.layoutParams = layoutParams
    }
    fun validateVersionCode(activity:Activity,callBack: (Boolean) -> Unit){
        if (Utils.isNetworkAvailableWithError(activity)) {
            ApiRepository.requestUpdateVersionCode() { isSuccess, currentVersionCode ->
                val pInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
                val version = pInfo.versionName
//                println("currentVersionCode:: " + currentVersionCode + " version:: "+version)
                currentVersionCode?.let {
                    if (version != currentVersionCode) {
//                        showUpdateVewVersionAppDialog(activity, it)
                        // if true
                        callBack.invoke(false)

                    } else {
                        callBack.invoke(false)

                    }
                }
            }
        }
    }
}