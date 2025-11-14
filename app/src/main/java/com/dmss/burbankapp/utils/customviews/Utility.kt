package com.dmss.burbankapp.utils.customviews

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.text.Html
import android.util.Base64
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.dmss.burbankapp.R
import com.dmss.burbankapp.data.local.CustomSharedPreferences
import com.dmss.burbankapp.databinding.CustomDialogViewBinding
import com.dmss.burbankapp.ui.login.ChooseOptionsActivity
import com.dmss.burbankapp.utility.ImagePickerActivity
import com.dmss.burbankapp.utility.ImagePickerActivity.PickerOptionListener
import com.dmss.burbankapp.viewmodel.ProfilePicViewModel
import com.dmss.burbankapp.viewmodel.SingletonNameViewModelFactory
import com.facebook.login.LoginManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import common.AppController
import java.io.ByteArrayOutputStream


object Utility {
    lateinit var dialogBuilder: AlertDialog.Builder

    lateinit var viewItemsList: ArrayList<LinearLayout>
    lateinit var arrowList: ArrayList<ImageView>
    lateinit var map: HashMap<LinearLayout, ImageView>

    lateinit var profilePicViewModel: ProfilePicViewModel;
    lateinit var singletonNameViewModelFactory: SingletonNameViewModelFactory

    fun loadFragment(fragment: Fragment, activity: FragmentActivity) {
        // load fragment
        val transaction: FragmentTransaction = activity.supportFragmentManager.beginTransaction()
        if (fragment.isAdded) {
            return
        }
        transaction.add(R.id.rl_content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /*fun loadFragment(fragment: MyCollectionFirstFragment) {
        TODO("Not yet implemented")
    }*/
    fun showProfileDialog(activity: Activity) {
        singletonNameViewModelFactory = SingletonNameViewModelFactory();

        viewItemsList = ArrayList();
        arrowList = ArrayList();
        map = HashMap();
        dialogBuilder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.profile_layout, null)

        dialogBuilder.setView(dialogView)
        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

        val window: Window = alertDialog.getWindow()!!
        val currentDialogAttributes: WindowManager.LayoutParams = window.getAttributes()
        val newDialogAttributes = WindowManager.LayoutParams()
        newDialogAttributes.copyFrom(currentDialogAttributes)
        newDialogAttributes.width = WindowManager.LayoutParams.MATCH_PARENT
        newDialogAttributes.height = WindowManager.LayoutParams.WRAP_CONTENT
        newDialogAttributes.gravity = Gravity.TOP
        window.setAttributes(newDialogAttributes)

        var iv_profile = alertDialog.findViewById<ImageView>(R.id.iv_profile)

        if (AppController.getProfiepic() != null) {
            iv_profile.setImageBitmap(AppController.getProfiepic())
        }
        iv_profile.setOnClickListener {
            alertDialog.dismiss()
        }

        var rl_my_details = alertDialog.findViewById<RelativeLayout>(R.id.rl_my_details)
        var ll_my_details = alertDialog.findViewById<LinearLayout>(R.id.ll_my_details)
        var rl_my_collection = alertDialog.findViewById<RelativeLayout>(R.id.rl_my_collection)
        var ll_my_collection = alertDialog.findViewById<LinearLayout>(R.id.ll_my_collection)
        // var rl_myday = alertDialog.findViewById<RelativeLayout>(R.id.rl_myday)
        var rl_home_and_land = alertDialog.findViewById<RelativeLayout>(R.id.rl_home_and_land)
        var ll_my_home_and_land = alertDialog.findViewById<LinearLayout>(R.id.ll_my_home_and_land)
        var rl_home_and_design = alertDialog.findViewById<RelativeLayout>(R.id.rl_home_and_design)
        var ll_my_home_and_design =
            alertDialog.findViewById<LinearLayout>(R.id.ll_my_home_and_design)
        var rl_app_setting = alertDialog.findViewById<RelativeLayout>(R.id.rl_app_setting)
        var ll_app_setting = alertDialog.findViewById<LinearLayout>(R.id.ll_app_setting)


        var iv_arrow_mycollection = alertDialog.findViewById<ImageView>(R.id.iv_arrow_mycollection)
        //  var iv_arrow_myday = alertDialog.findViewById<ImageView>(R.id.iv_arrow_myday)
        var iv_arrow_homeandland = alertDialog.findViewById<ImageView>(R.id.iv_arrow_homeandland)
        var iv_arrow_homeanddesign =
            alertDialog.findViewById<ImageView>(R.id.iv_arrow_homeanddesign)
        var iv_arrow_appsettings = alertDialog.findViewById<ImageView>(R.id.iv_arrow_appsettings)
        var iv_arrow_mydetails = alertDialog.findViewById<ImageView>(R.id.iv_arrow_mydetails)

        var rl_logout = alertDialog.findViewById<RelativeLayout>(R.id.rl_logout)
        var tv_select = alertDialog.findViewById<TextView>(R.id.tv_select)
        tv_select.setOnClickListener {
            ImagePickerActivity.clearCache(activity);
            onProfileImageClick(activity)
        }




        viewItemsList.add(ll_my_details)
        viewItemsList.add(ll_my_collection)
        //  viewItemsList.add(ll_myday)
        viewItemsList.add(ll_my_home_and_land)
        viewItemsList.add(ll_my_home_and_design)
        viewItemsList.add(ll_app_setting)


        arrowList.add(iv_arrow_mydetails)
        arrowList.add(iv_arrow_mycollection)
        //arrowList.add(iv_arrow_myday)
        arrowList.add(iv_arrow_homeandland)
        arrowList.add(iv_arrow_homeanddesign)
        arrowList.add(iv_arrow_appsettings)

        map.put(ll_my_details, iv_arrow_mydetails)
        map.put(ll_my_collection, iv_arrow_mycollection)
        // map.put(ll_myday, iv_arrow_myday)
        map.put(ll_my_home_and_land, iv_arrow_homeandland)
        map.put(ll_my_home_and_design, iv_arrow_homeanddesign)
        map.put(ll_app_setting, iv_arrow_appsettings)

        rl_logout.setOnClickListener {
            logoutDialog(activity)
        }

        rl_my_details.setOnClickListener {
            //iv_arrow_mydetails.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.down_arrow_icon))
            // hideandShowingHashmap(ll_my_details, iv_arrow_mydetails, context = activity)
            showHide(ll_my_details, ll_my_details, iv_arrow_mydetails, context = activity)


        }
        rl_my_collection.setOnClickListener {
            // hideandShowingHashmap(ll_my_collection, iv_arrow_mycollection, context = activity)
            showHide(ll_my_collection, ll_my_collection, iv_arrow_mycollection, context = activity)


        }
        /* rl_myday.setOnClickListener {
            // hideandShowingHashmap(ll_myday, iv_arrow_myday, context = activity)
             showHide(ll_myday,ll_myday,iv_arrow_myday,context = activity)


         }*/
        rl_home_and_land.setOnClickListener {
            //hideandShowingHashmap(ll_my_home_and_land, iv_arrow_homeandland, context = activity)
            showHide(
                ll_my_home_and_land,
                ll_my_home_and_land,
                iv_arrow_homeandland,
                context = activity
            )


        }
        rl_home_and_design.setOnClickListener {
            // hideandShowingHashmap(ll_my_home_and_design, iv_arrow_homeanddesign, context = activity)
            showHide(
                ll_my_home_and_design,
                ll_my_home_and_design,
                iv_arrow_homeanddesign,
                context = activity
            )

        }
        rl_app_setting.setOnClickListener {
            //hideandShowingHashmap(ll_app_setting, iv_arrow_appsettings, context = activity)
            showHide(ll_app_setting, ll_app_setting, iv_arrow_appsettings, context = activity)
        }


    }

    fun showHide(view: View, ll: LinearLayout, image: ImageView, context: Context) {
        hideandShowingHashmap(ll, image, context)
        /*if (view.visibility == View.VISIBLE) {
            View.GONE
            image.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.down_arrow_icon
                )
            )
        } else {
            hideandShowingHashmap(ll, image, context)
        }*/
    }

    fun showingHiding(ll: LinearLayout) {

        for (items in viewItemsList) {
            if (items == ll) {
                items.visibility = View.VISIBLE
            } else {
                items.visibility = View.GONE
            }
        }

    }

    fun arrowIconChange(image: ImageView, context: Context) {

        for (items in arrowList) {
            if (items == image) {
                items.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.down_arrow_icon
                    )
                )
            } else {
                items.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.side_arrow))
            }
        }

    }

    fun logoutDialog(activity: Activity) {
        val dialogBuilder = AlertDialog.Builder(activity)

        dialogBuilder.setMessage("Are you sure, you want to Logout?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton(Html.fromHtml("<font color='${activity.resources.getColor(R.color.orange_bg_3_1)}'>YES</font>")) { dialog, _ ->


//                LoginManager.getInstance().logOut()
                //Removing All Saved Local Data
                val myPreference = CustomSharedPreferences(activity)
                myPreference.setUserLoggedIn(false)
                myPreference.setUserLogin(false)

                myPreference.clearSession()

                val intent = Intent(activity, ChooseOptionsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                activity.startActivity(intent)
                dialog.dismiss()
                activity.finish()

            }
            // negative button text and action
            .setNegativeButton(Html.fromHtml("<font color='${activity.resources.getColor(R.color.black_bg_3_1)}'>NO</font>"), DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })

        // create dialog box
        val alert = dialogBuilder.create()

        // set title for alert dialog box
        alert.setTitle("My Place")
        // show alert dialog
        alert.show()
        val BUTTON_NEGATIVE: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        BUTTON_NEGATIVE.setTextColor(Color.BLACK)

        val BUTTON_POSITIVE: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        BUTTON_POSITIVE.setTextColor(Color.BLACK)
    }


    private fun hideandShowingHashmap(ll: LinearLayout, image: ImageView, context: Context) {
        for (item in map.keys) {
            if (item == ll) {
                item.visibility = View.VISIBLE
                map[item]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.down_arrow_icon
                    )
                )
            } else {
                item.visibility = View.GONE
                map[item]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.side_arrow
                    )
                )
            }
        }

    }

    private fun showImagePickerOptions(activity: Activity) {
        ImagePickerActivity.showImagePickerOptions(activity, object : PickerOptionListener {
            override fun onTakeCameraSelected() {
                launchCameraIntent(activity)
            }

            override fun onChooseGallerySelected() {
                launchGalleryIntent(activity)
            }
        })
    }

    fun onProfileImageClick(activity: Activity) {
        Dexter.withActivity(activity)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions(activity);
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog(activity)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private fun showSettingsDialog(activity: Activity) {
        val builder =
            AlertDialog.Builder(activity)
        builder.setTitle(activity.getString(R.string.app_name))
        builder.setMessage(activity.getString(R.string.dialog_permission_title))
        builder.setPositiveButton(activity.getString(R.string.go_to_settings)) { dialog, which ->
            dialog.cancel()
            openSettings(activity)
        }
        builder.setNegativeButton(
            activity.getString(android.R.string.cancel)
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings(activity: Activity) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", activity.getPackageName(), null)
        intent.data = uri
        activity.startActivityForResult(intent, 101)
    }

    private fun launchCameraIntent(activity: Activity) {
        val intent = Intent(activity, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_IMAGE_CAPTURE
        )

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000)
        activity.startActivityForResult(intent, 100)
    }

    private fun launchGalleryIntent(activity: Activity) {
        val intent = Intent(activity, ImagePickerActivity::class.java)
        intent.putExtra(
            ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION,
            ImagePickerActivity.REQUEST_GALLERY_IMAGE
        )

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1)
        activity.startActivityForResult(intent, 100)
    }

    public fun getBase64String(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        var photo: String = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return photo
    }

    fun ByteArray.encodeBase64(): ByteArray {
        val table = (CharRange('A', 'Z') + CharRange('a', 'z') + CharRange(
            '0',
            '9'
        ) + '+' + '/').toCharArray()
        val output = ByteArrayOutputStream()
        var padding = 0
        var position = 0
        while (position < this.size) {
            var b = this[position].toInt() and 0xFF shl 16 and 0xFFFFFF
            if (position + 1 < this.size) b =
                b or (this[position + 1].toInt() and 0xFF shl 8) else padding++
            if (position + 2 < this.size) b =
                b or (this[position + 2].toInt() and 0xFF) else padding++
            for (i in 0 until 4 - padding) {
                val c = b and 0xFC0000 shr 18
                output.write(table[c].toInt())
                b = b shl 6
            }
            position += 3
        }
        for (i in 0 until padding) {
            output.write('='.toInt())
        }
        return output.toByteArray()
    }

    fun showCustomDialog(activity: Activity) {

        var binding = CustomDialogViewBinding.inflate(activity.layoutInflater)
        dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder.setView(binding.root)

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()

    }


}

