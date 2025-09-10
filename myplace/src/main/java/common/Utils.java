package common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


import com.dmss.burbankappold.MyPlaceSettingActivity;
import com.dmss.burbankappold.R;
import com.google.gson.JsonObject;

/**
 * Created by Ashish.Kumar on 15-12-2016.
 */

public class Utils {
    public static AlertDialog alertDialog;
    public static String getCamelCase(final String init) {
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }
    public static void hideSwipeRefresh(SwipeRefreshLayout swipeRefreshLayout){
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }

    }
    public static boolean isNetworkAvailable(Activity act) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetworkInfo != null) && (activeNetworkInfo.isConnected())) {
            return true;
        } else {
            showToast(act, "Check your internet and pull to refresh again", Common.internet_Error);
            return false;
        }
    }
    public static boolean isNetworkAvailableWithError(Activity act) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetworkInfo != null) && (activeNetworkInfo.isConnected())) {
            return true;
        } else {
            showToast(act, "Internet not available, Please connect to internet.", Common.internet_Error);
            return false;
        }
    }
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ((activeNetworkInfo != null) && (activeNetworkInfo.isConnected())) {
            return true;
        } else {
            //showToast(context, "Internet Unavailable", Common.internet_Error);
            return false;
        }
    }
    public static JsonObject getLoginRequest(String jobNo,String userName,String passowrd){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("contractNumber",jobNo);
        jsonObject.addProperty("userName",userName);
        jsonObject.addProperty("password",passowrd);

        return jsonObject;
    }
    /*************************
     * camera module popup
     *************************************/
    public static void selectImageDialog(final Activity act) {
        final CharSequence[] items = {"Take Photo", "Choose from Library(Image)", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle("Add File(Photo/Video)");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if (isDeviceSupportCamera(act)) {
                        captureImage(act);
                    } else {
                        Toast.makeText(act, "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
                    }
                } else if (items[item].equals("Choose from Library(Image)")) {
                    @SuppressLint("IntentReset") Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    act.startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            MyPlaceSettingActivity.SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    public static void captureImage(Activity act) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Common.imageUri = getOutputMediaFileUri(Common.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Common.imageUri);

        // start the image capture Intent
        try {
            act.startActivityForResult(intent, Common.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }catch (Exception ex)
        {
            ex.fillInStackTrace();
        }
    }

    public static Uri getOutputMediaFileUri(int type) {
        File tempFile = getOutputMediaFile(type);
        Uri uri = Uri.fromFile(tempFile);
        return uri;
    }
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(Common.sdCardPath);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == Common.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
        File files = mediaFile;
        return mediaFile;
    }

    /* * Checking device has camera hardware or not
     */
    private static boolean isDeviceSupportCamera(Activity act) {
        if (act.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }

    }

    public static AlertDialog showAlertdialog(final Activity act, final String message) {

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog = new AlertDialog.Builder(act).create();

                // Setting Dialog Title
                alertDialog.setTitle("Error");

                // Setting Dialog Message
                alertDialog.setMessage(message);

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.unsucess);

                // Setting OK Button
                alertDialog.setButton("exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed

                        act.onBackPressed();

                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        });
        return alertDialog;
    }




    public static void showToast(final Activity act, final String Message, final int type) {

       act.runOnUiThread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                Toast.makeText(act,Message,Toast.LENGTH_SHORT).show();

               /* AppController controller = (AppController) act.getApplicationContext();
                LayoutInflater inflater = act.getLayoutInflater();

                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup) (act).findViewById(R.id.toast_layout_root));
                ImageView image = (ImageView) layout.findViewById(R.id.toast_image);
                //put your image in the drawable folder
                TextView text = (TextView) layout.findViewById(R.id.toast_text);
                text.setText(Message);
                text.setTypeface(controller.getTypefaceRegular());
                Drawable res;
                switch (type) {
                    case 1:
                        res = act.getResources().getDrawable(R.drawable.unsucess);
                        image.setImageDrawable(res);
                        layout.setBackground(act.getResources().getDrawable(R.drawable.red_border_bg));

                        break;
                    case 2:
                        res = act.getResources().getDrawable(R.drawable.sucess);
                        image.setImageDrawable(res);
                        layout.setBackground(act.getResources().getDrawable(R.drawable.cyan_border_bg));
                        break;
                    case 3:
                        res = act.getResources().getDrawable(R.drawable.internet_error);
                        image.setImageDrawable(res);
                        layout.setBackground(act.getResources().getDrawable(R.drawable.red_border_bg));
                        break;
                }

                Toast toast = new Toast(act);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 10, 40);

                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();*/
            }
        });


    }

    public static TransparentProgressDialog getProgress(Activity act) {
        TransparentProgressDialog dialog = new TransparentProgressDialog(act, R.drawable.ic_loading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        return dialog;
    }

    public static String getBase64(String val) {

        byte[] message = val.getBytes();
        return Base64.encodeToString(message, Base64.NO_WRAP);
    }

    public static void updateUI(final TextView tv, final String s) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    tv.setText(s);
                } catch (Exception ex) {
                    ex.fillInStackTrace();
                }
            }
        });
    }



    public static String getTwoDecimalValue(double value) {
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(value);
    }
    public static String getTwoDecimalValueForWidth(double value) {
        DecimalFormat df = new DecimalFormat("###.00");
        return df.format(value);
    }





    public static void setListViewHeightBasedOnChildren(final ListView listView) {
        listView.post(new Runnable() {
            @Override
            public void run() {
                ListAdapter listAdapter = listView.getAdapter();
                if (listAdapter == null) {
                    return;
                }
                int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
                int listWidth = listView.getMeasuredWidth();
                for (int i = 0; i < listAdapter.getCount(); i++) {
                    View listItem = listAdapter.getView(i, null, listView);
                    listItem.measure(
                            View.MeasureSpec.makeMeasureSpec(listWidth, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));


                    totalHeight += listItem.getMeasuredHeight();
                    Log.d("listItemHeight" + listItem.getMeasuredHeight(), "___________");
                }
                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = (int) ((totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))));
                listView.setLayoutParams(params);
                listView.requestLayout();

            }
        });
    }

    public static ArrayList<String> getFilteredValues(HashMap map, ArrayList<String> list) {
        ArrayList<String> tempList = new ArrayList<>();
        tempList.addAll(list);
        if (map != null) {
            for (Object key : map.keySet()) {
                String lKey = (String) key;
                if (tempList.contains(lKey)) {
                    tempList.remove(lKey);
                }
            }
        }
        return tempList;
    }

    public static String protectedEmailAddress(String emailAddress) {
        int pos = emailAddress.indexOf("@");
        StringBuilder newString = new StringBuilder(emailAddress);
        for (int i = pos + 1; i < emailAddress.length() - 4; i++) {
            newString.setCharAt(i, 'x');
        }


        return newString.toString();
    }
    public static void setMutlicolorTextView(Activity act, TextView tv, String text)
    {
        Spannable wordtoSpan = new SpannableString(text);
      //  wordtoSpan.setSpan(new ForegroundColorSpan(act.getResources().getColor(R.color.fontColorOrange)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(wordtoSpan);
    }
}
