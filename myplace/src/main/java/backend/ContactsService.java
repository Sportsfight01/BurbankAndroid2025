package backend;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import common.AppController;
import common.Common;
import common.Utils;
import interfaces.ContactsInterface;

public class ContactsService extends Service {
    AppController controller;
    Context context;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ContactsService(Context context) {
        this.context = context;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        controller = (AppController) getApplicationContext();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void getContactsDetails() {
        if (controller.getMy_Place_Details() != null) {
            if (Utils.isNetworkAvailable(getApplicationContext())) {
                /*Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {*/

                        String result = controller.webApiCall().postData_to_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceUserCheckUrl, getMyPlaceLoginJson());
                        if (result.equalsIgnoreCase("true")) {
                            String result2 = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlQldOrSa + "" + Common.MyPlaceGetUserDetailsUrl);
                            if (result2 != null && !result2.equalsIgnoreCase("null")) {
                                final String contactDetails = controller.webApiCall().getData_From_MyPlace(Common.myPlaceBaseUrlVic + "" + Common.MyPlaceGetContactDetails + "" + controller.getJobNumber());
                                if (contactDetails != null) {
                                    if (!contactDetails.contains("Error")) {
                                        if (contactDetails != null && contactDetails.length() > 0) {
                                            ContactsInterface anInterface = (ContactsInterface) context;
                                            anInterface.contactsServiceSuccess(contactDetails);
                                        }
                                        /*new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (contactDetails.contains("timeout")) {
                                                    Toast.makeText(ContactsActivity.this, "Error: Network is slow,\nPlease try again later", Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(ContactsActivity.this, contactDetails, Toast.LENGTH_LONG).show();
                                                }
                                                dialog.cancel();
                                            }
                                        });*/
                                    }
                                }
                            }
                        }
                    /*}*/
                /*});
                t.start();*/
            } else {
                //apiCallStarted = false;
                //controller.setContactsModelDetails(null);
            }
        } else {
            //apiCallStarted = false;
            //controller.setContactsModelDetails(null);
        }

    }

    public String getMyPlaceLoginJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Region", controller.getMy_Place_Details().getRegion());
            jsonObject.put("JobNumber", controller.getMy_Place_Details().getJobNumber());
            jsonObject.put("UserName", controller.getMy_Place_Details().getUsername());
            jsonObject.put("Password", controller.getMy_Place_Details().getPassword());
        } catch (JSONException ex) {
            ex.fillInStackTrace();
        }
        return jsonObject.toString();
    }

    private static void sendMessageToActivity(String msg) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        Bundle b = new Bundle();
        //LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}
