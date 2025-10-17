package com.dmss.burbankapp.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.Html
import android.text.Layout
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AlignmentSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import android.widget.LinearLayout.LayoutParams
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.dmss.burbankapp.ApplicationClass
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.ui.loginhome.LoginHomeActivity
import com.facebook.login.LoginManager
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow


class AppUtils {
    companion object {
        private fun getRoundedPrice(value: String): String? {
            val price: Spanned? = fromHtml(value)
            return String.format("%.1f", value.toFloat())
        }

        fun roundRemainingString(value: String): String? {
            val price: String? = getRoundedPrice(value)
            if (price != null) {
                if (price.contains(".")) {
                    val arrOfStr: List<String> = price.split(".")
                    if (arrOfStr[1] == "0") {
                        return arrOfStr[0]
                    }
                    return price

                }
            }
            return price
        }

        private fun fromHtml(html: String?): Spanned? {
            return when {
                html == null -> {
                    // return an empty spannable if the html is null
                    SpannableString("")
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                    // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
                    // we are using this flag to give a consistent behaviour
                    Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
                }
                else -> {
                    Html.fromHtml(html)
                }
            }
        }

        fun getCommasForPriceValue(price: Int): String {
            val formatter: DecimalFormat = DecimalFormat("#,###,###")
            return formatter.format(price)
        }

        fun showCustomCenterToast(context: Context, message: String) {
          /*  val toast = ToastHandler.getToastInstance(
                ApplicationClass.applicationContext(),
                message,
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()*/
            Toast.makeText(ApplicationClass.applicationContext(),
                message,
                Toast.LENGTH_SHORT).show()
        }

        fun showCustomCenterToast(message: String) {
           /* val toast = ToastHandler.getToastInstance(
                ApplicationClass.applicationContext(),
                message,
                Toast.LENGTH_SHORT
            )
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()*/
            Toast.makeText(ApplicationClass.applicationContext(),
                message,
                Toast.LENGTH_SHORT).show()
        }
        public fun showSuccessAlert(context: Context,signUpIntent:Intent, message: String){
            val builder = AlertDialog.Builder(context)
//           builder.setTitle(context.getString(R.string.app_name))
            var myView =  TextView(context);
            myView.text = context.getString(R.string.app_name)
            myView.textSize = 25F;
            myView.gravity = Gravity.CENTER;
            myView.setPadding(0, 20, 0, 20);

            myView.setTextColor(context.getColor(R.color.black_bg_3_1))
            builder.setCustomTitle(myView);
            builder.setMessage(message)
            builder.setPositiveButton(Html.fromHtml("<font color='${context.resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { _, _ ->
                builder.create().dismiss()
                context.startActivity(signUpIntent)
                val activity = context as Activity
                activity.finish()
            }
            val  dialog = builder.create();
            dialog.show();
            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1.0f
            layoutParams.gravity = Gravity.CENTER
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize=18f
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).layoutParams = layoutParams;
            dialog!!.window!!.attributes = dialog!!.window!!.attributes.apply { dimAmount = 0F }
            val messageView = dialog.findViewById<View>(android.R.id.message) as TextView
            messageView.gravity = Gravity.CENTER
        }
        fun showLocationSettingsAlert(context: Context){
            val builder = AlertDialog.Builder(context)
//           builder.setTitle(context.getString(R.string.app_name))
            var myView =  TextView(context);
            myView.text = context.getString(R.string.app_name)
            myView.textSize = 25F;
            myView.gravity = Gravity.CENTER;
            myView.setPadding(0, 20, 0, 20);

            myView.setTextColor(context.getColor(R.color.black_bg_3_1))
            builder.setCustomTitle(myView);
            builder.setMessage("")
            builder.setPositiveButton(Html.fromHtml("<font color='${context.resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { _, _ ->
                builder.create().dismiss()
                val intent: Intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            }
            builder.setNegativeButton(Html.fromHtml("<font color='${context.resources.getColor(R.color.black_bg_3_1)}'>Cancel</font>")) { _, _ ->
                builder.create().dismiss()
            }
            val  dialog = builder.create();
            dialog.show();
            /* val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
             val positiveButtonLL = positiveButton.layoutParams as LinearLayout.LayoutParams
             positiveButtonLL.gravity = Gravity.RIGHT
             positiveButton.layoutParams = positiveButtonLL*/

            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1.0f
            layoutParams.gravity = Gravity.CENTER
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize=18f
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).layoutParams = layoutParams;
            dialog!!.window!!.attributes = dialog!!.window!!.attributes.apply { dimAmount = 0F }
            val messageView = dialog.findViewById<View>(android.R.id.message) as TextView
            messageView.gravity = Gravity.CENTER
        }
       fun showValidationAlert(context: Context, message: String){
           val builder = AlertDialog.Builder(context)
//           builder.setTitle(context.getString(R.string.app_name))
             var myView =  TextView(context);
           myView.text = context.getString(R.string.app_name)
           myView.textSize = 25F;
           myView.gravity = Gravity.CENTER;
           myView.setPadding(0, 20, 0, 20);

           myView.setTextColor(context.getColor(R.color.black_bg_3_1))
           builder.setCustomTitle(myView);
           builder.setMessage(message)
           builder.setPositiveButton(Html.fromHtml("<font color='${context.resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { _, _ ->
               builder.create().dismiss()
           }
           val  dialog = builder.create();
           dialog.show();
          /* val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
           val positiveButtonLL = positiveButton.layoutParams as LinearLayout.LayoutParams
           positiveButtonLL.gravity = Gravity.RIGHT
           positiveButton.layoutParams = positiveButtonLL*/

           val layoutParams: LinearLayout.LayoutParams = LayoutParams(
               LinearLayout.LayoutParams.MATCH_PARENT,
               LinearLayout.LayoutParams.WRAP_CONTENT
           )
           layoutParams.weight = 1.0f
           layoutParams.gravity = Gravity.CENTER
           dialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize=18f
           dialog.getButton(AlertDialog.BUTTON_POSITIVE).layoutParams = layoutParams;
           dialog!!.window!!.attributes = dialog!!.window!!.attributes.apply { dimAmount = 0F }
           val messageView = dialog.findViewById<View>(android.R.id.message) as TextView
           messageView.gravity = Gravity.CENTER
       }

        fun isLocationEnabled(context: Context): Boolean {
            var locationMode = 0
            val locationProviders: String
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                locationMode = try {
                    Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                } catch (e: SettingNotFoundException) {
                    e.printStackTrace()
                    return false
                }
                locationMode != Settings.Secure.LOCATION_MODE_OFF
            } else {
                locationProviders = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED
                )
                !TextUtils.isEmpty(locationProviders)
            }
        }

        fun showEnquireNowSuccessAlert(context: Activity, message: String){
            val builder = AlertDialog.Builder(context)
//           builder.setTitle(context.getString(R.string.app_name))
            var myView =  TextView(context);
            myView.text = context.getString(R.string.app_name)
            myView.textSize = 25F;
            myView.gravity = Gravity.CENTER;
            myView.setPadding(0, 20, 0, 20);

            myView.setTextColor(context.getColor(R.color.black_bg_3_1))
            builder.setCustomTitle(myView);
            builder.setMessage(message)
            builder.setPositiveButton(Html.fromHtml("<font color='${context.resources.getColor(R.color.orange_bg_3_1)}'>OK</font>")) { _, _ ->
                builder.create().dismiss()
                context.finish()
            }
            val  dialog = builder.create();
            dialog.show();
            /* val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
             val positiveButtonLL = positiveButton.layoutParams as LinearLayout.LayoutParams
             positiveButtonLL.gravity = Gravity.RIGHT
             positiveButton.layoutParams = positiveButtonLL*/

            val layoutParams: LinearLayout.LayoutParams = LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.weight = 1.0f
            layoutParams.gravity = Gravity.CENTER
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize=18f
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).layoutParams = layoutParams;
            dialog!!.window!!.attributes = dialog!!.window!!.attributes.apply { dimAmount = 0F }
            val messageView = dialog.findViewById<View>(android.R.id.message) as TextView
            messageView.gravity = Gravity.CENTER
        }
        fun showPleaseLoginDialog(context: Context, activity: Activity, message: String) {
            val dialogBuilder = AlertDialog.Builder(context)
            val title = SpannableString("MyPlace")
            title.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0,
                title.length,
                0
            )
            dialogBuilder.setTitle(title)//Should be in center
            dialogBuilder.setMessage(message)
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton(Html.fromHtml("<font color='${activity!!.resources.getColor(R.color.orange_bg_3_1)}'>LOGIN</font>")) { dialog, _ ->
                    dialog.cancel()

                    LoginManager.getInstance().logOut()
                    //Removing All Saved Local Data
                    val myPreference = CustomSharedPreferences(activity)
                    myPreference.setUserLoggedIn(false)
                    myPreference.clearSession()
                    val intent = Intent(activity, LoginHomeActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    activity.startActivity(intent)
                    dialog.dismiss()
//                    activity.finish()
                }
                .setNegativeButton(Html.fromHtml("<font color='#000000'>OK</font>")) { dialog, _ ->
                    dialog.cancel()
                }

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            //alert.setTitle("Select ${state.name}?")
            // show alert dialog
            alert.show()
            val negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            val positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)

            negativeButton.isAllCaps = false
            positiveButton.isAllCaps = false

            negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
            positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
            val layoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10F
            positiveButton.layoutParams = layoutParams
            negativeButton.layoutParams = layoutParams
        }
        fun showUpdateVewVersionAppDialog( activity: Context, updatedVersion:String) {
            var packageName = activity.packageName

            val dialogBuilder = AlertDialog.Builder(activity)
            val title = SpannableString("MyPlace")
            title.setSpan(
                AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                0,
                title.length,
                0
            )
            dialogBuilder.setTitle(title)//Should be in center
            dialogBuilder.setMessage("Version $updatedVersion is available. Please update app")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton(Html.fromHtml("<font color='${activity!!.resources.getColor(R.color.orange_bg_3_1)}'>Update</font>")) { dialog, _ ->
                    dialog.cancel()

                    LoginManager.getInstance().logOut()
                    //Removing All Saved Local Data
                    val myPreference = CustomSharedPreferences(activity)
                    myPreference.setUserLoggedIn(false)
                    myPreference.clearSession()
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
            if(!alert.isShowing) {
                alert.show()
            }
            val negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            val positiveButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)

            negativeButton.isAllCaps = false
            positiveButton.isAllCaps = false

            negativeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
            positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18F);
            val layoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
            layoutParams.weight = 10F
            positiveButton.layoutParams = layoutParams
            negativeButton.layoutParams = layoutParams
        }
        fun getPackageTextBasedOnCount(count: Int): String {

            var packageName = ""
            packageName = when (count) {
                0 -> {
                    "NO PACKAGES"
                }
                1 -> {
                    "SKIP TO 1 PACKAGE"
                }
                else -> "SKIP TO $count PACKAGES"
            }

            return packageName


        }

        fun profileCountStatus(tvProfileCount:TextView){
            if(AppConstants.TotalMyFavs==0){
                tvProfileCount.visibility=View.GONE
            }
            tvProfileCount.text=AppConstants.TotalMyFavs.toString()
        }

        fun noPackagesAndDisableClick(
            packageTextView: Button,
            nextRelativeView: RelativeLayout,
            nextButton: Button,
            isHomeAndLand: Boolean
        ): Boolean {
            if (isHomeAndLand) {
                packageTextView.text = "NO PACKAGES"
            } else {
                packageTextView.text = "NO DESIGNS"
            }
            packageTextView.setTextColor(
                ContextCompat.getColor(
                    packageTextView.context,
                    R.color.orange_bg_3_1
                )
            )
            packageTextView.isEnabled = false
            packageTextView.isEnabled = false

            nextRelativeView.background = ContextCompat.getDrawable(
                nextButton.context,
                R.drawable.disable_next_background
            )

            return false


        }

        fun disableAndEnableView(
            count: Int,
            packageTextView: Button,
            nextRelativeView: RelativeLayout,
            nextButton: Button
        ): Boolean {
            if (count == 0) {
                nextButton.isEnabled = false
                packageTextView.isEnabled = false
                packageTextView.setTextColor(
                    ContextCompat.getColor(
                        packageTextView.context,
                        R.color.light_grey_disabled_bg_3_1
                    )
                )
                nextRelativeView.background = ContextCompat.getDrawable(
                    nextButton.context,
                    R.drawable.disable_next_background
                )
                return false
            } else {
                nextButton.isEnabled = true
                packageTextView.isEnabled = true
                packageTextView.setTextColor(
                    ContextCompat.getColor(
                        packageTextView.context,
                        R.color.orange_bg_3_1
                    )
                )
                nextRelativeView.background = ContextCompat.getDrawable(
                    nextButton.context,
                    R.drawable.enable_orange_bg
                )
                return true
            }
        }

        fun getDesignsTextBasedOnCount(count: Int): String {

            var packageName = ""
            packageName = when (count) {
                0 -> {
                    "NO DESIGNS"
                }
                1 -> {
                    "SKIP TO 1 DESIGN"
                }
                else -> "SKIP TO $count DESIGNS"
            }

            return packageName


        }

        fun removeNextBreadCrumbItem(breadCrumbKey: String) {
            AppConstants.removeCurrentElement(breadCrumbKey)
            /*var currentBreadCrumb: Boolean = false
            val newHashMap: HashMap<String, BreadCrumbHashMapModel> = LinkedHashMap()

            if (AppConstants.breadCrumbMyCollection.keys.size > 0) {
                val breadCrumbIterator: Iterator<*> =
                    AppConstants.breadCrumbMyCollection.keys.iterator()
                while (breadCrumbIterator.hasNext()) {
                    val key = breadCrumbIterator.next() as String
                    var value = AppConstants.breadCrumbMyCollection[key]

                    if (key == breadCrumbKey) {
                        if (value != null) {
                            currentBreadCrumb = true
                            newHashMap[key] = value
                        }

                    }

                    if (!currentBreadCrumb) {
                        if (value != null) {
                            newHashMap[key] = value
                        }
                    }

                }
                if (newHashMap.size > 0) {
                    AppConstants.breadCrumbMyCollection.clear()
                    val newBreadCrumbIterator: Iterator<*> = newHashMap.keys.iterator()
                    while (newBreadCrumbIterator.hasNext()) {
                        val key = newBreadCrumbIterator.next() as String
                        val value = newHashMap[key]
                        if (value != null) {
                            AppConstants.breadCrumbMyCollection[key] = value
                        }
                    }

                }
            }*/

        }

//https://stackoverflow.com/questions/41859525/how-to-go-about-formatting-1200-to-1-2k-in-android-studio

        /*String numberString = "";
if (Math.abs(number / 1000000) > 1) {
   numberString = (number / 1000000).toString() + "m";

} else if (Math.abs(number / 1000) > 1) {
   numberString = (number / 1000).toString() + "k";

} else {
   numberString = number.toString();

}*/
        fun formattingKConverter(number: Int): Float {
            var numberString = ""
            numberString = when {
                abs(number / 1000000) > 1 -> {
                    (number / 1000000).toString()
                }
                abs(number / 1000) > 1 -> {
                    (number / 1000).toString()
                }
                else -> {
                    number.toString()
                }
            }
            return numberString.toFloat()
        }

        fun removeLastIndexOfString(str: String?): String {
            var str = str
            if (str != null && str.isNotEmpty() && str[str.length - 1] == '|') {
                str = str.substring(0, str.length - 1)
            }
            return str.toString()
        }

        fun removeLastIndexComma(str: String?): String {
            var str = str?.trim()
            if (str != null && str.isNotEmpty() && str[str.length - 1] == ',') {
                str = str.substring(0, str.length - 1)
            }
            return str.toString()
        }

        fun navigateToGoogleMaps(lat: Double, lon: Double, context: Context) {
            try {
                val gmmIntentUri = Uri.parse("geo:$lat,$lon?q=$lat,$lon")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
       fun navigateDirections(lat: Double, lon: Double, context: Context){
           try {
               val intent = Intent(
                   Intent.ACTION_VIEW,
                   Uri.parse("http://maps.google.com/maps?daddr=$lat,$lon")
               )
               context.startActivity(intent)
           }catch (e:Exception){
               e.printStackTrace()
           }
       }

        fun getFormattedNumber(value: Int): String {
            val count = value * 1000
            if (count < 1000) return "" + count
            val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
            var countval=count / 1000.0.pow(exp.toDouble())
            var formattedValue =
                String.format("%.1f%c", countval, "KMGTPE"[exp - 1])
            try {
                if (formattedValue.contains("K")) {
                    val value = formattedValue.replace("K", "").toFloat().toInt()
                    if (value < 1000) {
                        formattedValue = "${value}K"
                    }
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            return formattedValue
        }
    }
}
