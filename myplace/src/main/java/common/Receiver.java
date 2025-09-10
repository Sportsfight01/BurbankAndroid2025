package common;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Ashish.Kumar on 07-06-2017.
 */

public class Receiver extends BroadcastReceiver {
    AlertDialog alertDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                Log.d("Network", "Internet YAY");
                if (alertDialog != null) {
                    alertDialog.cancel();
                }
            } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
//                Log.d("Network", "No internet :(");
                Toast.makeText(context, "Internet not available, Please connect to internet.", Toast.LENGTH_SHORT).show();
//                showInternetErrorAlert(context);

            }
        }
    }

    public void showInternetErrorAlert(Context context) {
        if (alertDialog != null) {
            alertDialog.cancel();
        }
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setCancelable(true);
            alertDialog.setTitle("Info");
            alertDialog.setMessage("Internet not available, Please connect to internet.");
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            try {
                alertDialog.show();
            } catch (Exception ex) {
                ex.fillInStackTrace();
                Log.d("exception: ", "" + ex.fillInStackTrace());
            }
        }
    }

