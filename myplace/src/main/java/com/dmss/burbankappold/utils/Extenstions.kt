package com.dmss.burbankappold.utils

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.dmss.burbankappold.R
import com.squareup.picasso.Picasso
import common.AppController
import models.photos.PhotosDataItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


fun Context.showErrorToast(){
    Toast.makeText(this,this.getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
}
fun Context.showToast(message: String){
    Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
}

fun View.show(){
    this.visibility = View.VISIBLE
}
fun View.hide(){
    this.visibility = View.GONE
}

fun EditText.setDisable(){
    this.isEnabled = false
    this.isClickable = false
    this.isFocusable = false
    this.isCursorVisible = false
}

fun String.convertUsCurrency(): String{
    var amount = 0f
    try {
        amount = this.toFloat()
    }catch (nfe: Exception){
        nfe.printStackTrace()
    }
    return NumberFormat.getCurrencyInstance(Locale.US).format(amount)
}

const val photosBaseUrl = "https://nationalclickhome.burbankgroup.com.au/clickhome3webservice"
fun ImageView.loadUrl(url: String?, type: String?){
    url?.let {
        Picasso.get()
            .load("$photosBaseUrl/${it.replace("=0","")}.$type")
            .into(this)
    }
}

fun ImageView.loadUrlUsingGlide(url: String?){
    if (url?.isNotEmpty() == true){
        Glide.with(this.context)
            .load(url)
//            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .placeholder(R.drawable.profile_place_holder)
            .into(this)
    }
}

const val SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
const val DATE_FORMAT = "dd/MM/yyyy"
const val DATE_FORMAT1 = "dd/MMMM/yyyy"

const val PHOTOS_DATE_FORMAT = "EEEE, dd/MM/yy"
const val PHOTOS_DATE_FORMAT_TIME = "EEEE, dd/MM/yy hh:mm:ss"
const val DOCUMENTS_FORMAT = "dd MMM, yyyy, hh:mma"
const val APPOINTMENTS_DATE_FORMAT = "dd MMMM, yyyy"
//const val ACTUAL_DATE_FORMAT = "dd/MM/yyyy hh:mm:ss a"
const val LOCAL_DB_DATE_FORMAT = "dd-MMMM-yyyy"
const val ACTUAL_DATE_FORMAT = "dd MMM yyyy, hh:mm a"

const val CONTACT_US_SERVER_DATE_FORMAT = "dd/MM/yyyy hh:mm a"
fun String.convertDateFormat(dateFormat : String = DATE_FORMAT, actualFormat : String = SERVER_DATE_FORMAT): String{
    val sdf = SimpleDateFormat(actualFormat, Locale.getDefault())
    val convertSdf = SimpleDateFormat(dateFormat, Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val dateTime = sdf.parse(this)
    val cal  = Calendar.getInstance()
    cal.time = dateTime
   /* if (dateFormat == DOCUMENTS_FORMAT)
        cal.add(Calendar.HOUR, -11)*/
    return convertSdf.format(cal.time)
}
fun String.convertDateFormatCompare(dateFormat : String = DATE_FORMAT, actualFormat : String = SERVER_DATE_FORMAT): Date{
    val sdf = SimpleDateFormat(actualFormat, Locale.getDefault())
    val convertSdf = SimpleDateFormat(dateFormat, Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val dateTime = sdf.parse(this)
    val cal  = Calendar.getInstance()
    cal.time = dateTime
    /* if (dateFormat == DOCUMENTS_FORMAT)
         cal.add(Calendar.HOUR, -11)*/
    return dateTime
}

fun String.convertDateFormatNotification(dateFormat : String = DATE_FORMAT1, actualFormat : String = SERVER_DATE_FORMAT): String{
    val sdf = SimpleDateFormat(actualFormat, Locale.getDefault())
    val convertSdf = SimpleDateFormat(dateFormat, Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val dateTime = sdf.parse(this)
    val cal  = Calendar.getInstance()
    cal.time = dateTime
    /* if (dateFormat == DOCUMENTS_FORMAT)
         cal.add(Calendar.HOUR, -11)*/
    return convertSdf.format(cal.time)
}
fun String.convertDateFormatContacus(dateFormat : String = DATE_FORMAT, actualFormat : String = SERVER_DATE_FORMAT): String{
    val sdf = SimpleDateFormat(actualFormat, Locale.getDefault())
    val convertSdf = SimpleDateFormat(dateFormat, Locale.getDefault())
//    sdf.timeZone = TimeZone.getTimeZone("UTC")
    val dateTime = sdf.parse(this)
    val cal  = Calendar.getInstance()
    cal.time = dateTime
    /* if (dateFormat == DOCUMENTS_FORMAT)
         cal.add(Calendar.HOUR, -11)*/
    return convertSdf.format(cal.time)
}
fun String.convertDateFormatFinc(dateFormat : String = DATE_FORMAT, actualFormat : String = SERVER_DATE_FORMAT): String{
    val sdf = SimpleDateFormat("dd/M/yyyy")
    val currentDate = sdf.format(Date())
    val date: Date = sdf.parse(currentDate)
    val outFormat = SimpleDateFormat("EEEE")
    val goal = outFormat.format(date)

     val day= "$goal, $currentDate"
    return day
}
 fun String.sortBydateList(documentsList : List<PhotosDataItem>):List<PhotosDataItem>{
    val sortedList = documentsList.sortedByDescending {  it.docdate }
    return sortedList
}
fun String.convertDateFormatFincDetaild(dateFormat : String = DATE_FORMAT, actualFormat : String = SERVER_DATE_FORMAT): String{
    val sdf = SimpleDateFormat("dd/MM/yyyy")
    val currentDate = sdf.format(Date())
    val date: Date = sdf.parse(currentDate)
    val outFormat = SimpleDateFormat("EEEE")
    val goal = outFormat.format(date)

    val day= "$goal, $currentDate"
    return day
}
fun String.sentenceFirstLetterCaps(str: String):String {
    val words = str.split(" ").toMutableList()
    var output = ""
    for(word in words){
        output += word.capitalize() +" "
    }
    output = output.trim()
//    print(output)

    return output
}
object BundleKey{
    const val PROGRESS_DATA = "progressData"
    const val IMAGE_URL = "ImageBaseUrl"
    const val IMAGE_FORMAT_TYPE = "imageFormat"
    const val DATE = "date"
    const val DOCUMENT_URL = "documentUrl"
    const val DOCUMENT_TYPE = "documentType"
    const val FINANCE_DATA = "financeData"
    const val FINANCE_TYPE = "finance_type"
    const val CONTRACT_VALUE = "contract_value"
    const val TITLE = "title"
    const val URL = "url"
    const val PHOTOS_LIST = "photos_list"
    const val PHOTOS_ITEM = "photos_item"
    const val PHOTOS_COUNT = "photos_count"
    const val PHOTO = "photo"
    const val DATA_OBJECT = "data_object"
}

fun hideKeyboardFrom(context: Context, view: View) {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun ImageView.changeIconColor(context: Context?, colorID: Int = R.color.white){
    context?.let {
        this.drawable?.setColorFilter(
            ContextCompat.getColor(it, colorID),
            PorterDuff.Mode.SRC_IN
        )
        this.setImageDrawable(this.drawable)
    }
}

fun TextView.changeTextColor(context: Context?, colorID: Int = R.color.white){
    context?.let {
        val color = ContextCompat.getColor(it, colorID)
        this.setTextColor(color)
    }
}
fun View.changeBackgroundColor(context: Context?, colorID: Int = R.color.new_gray_color){
    context?.let {
        val color = ContextCompat.getColor(it, colorID)
        this.setBackgroundColor(color)
    }
}

fun ImageView.setVideoThumbnail(url: String?){
    url?.also {
        Glide.with(context).load(url)
            .thumbnail(0.1f).into(this)
    }
}
fun ImageView.setVideoThumbnail1(url: String?){
    url?.also {
        Glide.with(context).load(url)
            .thumbnail(0.1f).into(this)
    }
}
@Throws(Throwable::class)
fun retriveVideoFrameFromVideo(context: Context, videoPath: String?): Bitmap? {
    var bitmap: Bitmap? = null
    var mediaMetadataRetriever: MediaMetadataRetriever? = null
    try {
        mediaMetadataRetriever = MediaMetadataRetriever()
        /*mediaMetadataRetriever.setDataSource(
            videoPath,
            HashMap()
        )*/
        mediaMetadataRetriever.setDataSource(context, Uri.parse(videoPath));
        bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
        throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
    } finally {
        mediaMetadataRetriever?.release()
    }
    return bitmap
}

fun getStateName():String{
    val region = if (AppController.controller.userProfile.userDetailses.isNotEmpty()){
        AppController.controller.userProfile.userDetailses[0].myPlaceJobDetailses[0].region.lowercase()
    }else ""
     return when(region){
        "vic" -> "Victoria"
        "qld" -> "Queensland"
        "nsw" -> "NSW"
        "sa" -> "South-Australia"
        else -> ""
    }
}





