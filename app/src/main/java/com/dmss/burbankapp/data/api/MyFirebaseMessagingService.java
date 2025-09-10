package com.dmss.burbankapp.data.api;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dmss.burbankapp.R;
import com.dmss.burbankapp.data.local.CustomSharedPreferences;
import com.dmss.burbankapp.data.model.HnLQuizPackageModel;
import com.dmss.burbankapp.ui.designs.MyCollectionDetailsActivity;
import com.dmss.burbankapp.ui.homeandland.HomeLandFullScreenActivity;
import com.dmss.burbankapp.ui.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.println("remoteMessage:: "+remoteMessage);
  /*      if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            String ImageUrl = String.valueOf(remoteMessage.getNotification().getImageUrl());
            HashMap<String,String> map= new HashMap<String,String>();
            map.put("title",title);
            map.put("message",message);
            map.put("ImageUrl",ImageUrl);

            showCustomNotification(map);
        } else*/
        if (remoteMessage.getData() != null) {
            // Optional: Fallback to data


            showCustomNotification(remoteMessage.getData());

        }
    }

    public void showCustomNotification(Map<String,String> map) {
        System.out.println("showCustomNotification:: "+map);
        String title = map.get("title");
        String message = map.get("body");
//        String body_message = map.get("body");

        String url = map.get("ImageUrl");
        String PakageIdLandBank = map.get("HandLPackageId");
        String HouseSize = map.get("HouseSize");
        String HouseName = map.get("HouseName");
        String stateId = map.get("StateId");
        String stateName = map.get("State");

//        String action_type = map.get("action_type");
        String State = map.get("State");
        String ModuleType = map.get("ModuleType");
        String isMultiple = map.get("IsMultiple");

        String channelId = getString(R.string.default_notification_channel_id);
//        int notificationId = 1001;
        int notificationId = (int) System.currentTimeMillis();
        Intent intent=null;
        System.out.println("ImageUrl::"+url);
        int SELECTED_ITEM=0;

     /*   if (ModuleType.equalsIgnoreCase( "NewHomes_Main")) {
            SELECTED_ITEM=0;
        }
        else if(ModuleType.equalsIgnoreCase( "HomeAndLand_Main")){
            SELECTED_ITEM=1;

        }else{
            SELECTED_ITEM=2;

        }*/
        if(isMultiple.equalsIgnoreCase("true")){

            intent = new Intent(this, MainActivity.class);

        }else {
            if (Objects.equals(ModuleType, "NewHomes") || Objects.equals(ModuleType, "DisplayHomes")) {
                intent = new Intent(this, MyCollectionDetailsActivity.class);
            } else {
                intent = new Intent(this, HomeLandFullScreenActivity.class);

            }
            System.out.println("ModuleType:"+ModuleType);
        }
//        intent = new Intent(this, MyCollectionDetailsActivity.class);
//        intent = new Intent(this, HomeLandFullScreenActivity.class);

        intent.putExtra("from_notification", PakageIdLandBank);
        intent.putExtra("SELECTED ITEM", SELECTED_ITEM); // Warning: Parse carefully
        intent.putExtra("HandLPackageId", PakageIdLandBank);
        intent.putExtra("HouseSize", HouseSize);
        intent.putExtra("HouseName", HouseName);
        intent.putExtra("StateId", stateId);
        intent.putExtra("ModuleType", ModuleType);
        CustomSharedPreferences myPreference = new CustomSharedPreferences(this);

         myPreference.setStateID(Integer.parseInt(stateId));
        myPreference.selectedState(stateName);


      /*  intent.putExtra("PakageIdLandBank", "PakageIdLandBank");
        intent.putExtra("HouseSize", 120);
        intent.putExtra("HouseName", "HouseName");
        intent.putExtra("StateId", 2);
        intent.putExtra("ModuleType", ModuleType);
*/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        RemoteViews customView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        customView.setTextViewText(R.id.title, title);
        customView.setTextViewText(R.id.message, message);

        if(url!=null && !url.equals("")) {
            Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            customView.setImageViewBitmap(R.id.right_icon, resource);
//                        AppWidgetManager.getInstance(c).updateAppWidget(wi,customView);
                        }
                    });
        }
//        customView.setImageViewResource(R.id.right_icon, R.drawable.notify_img);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Custom Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.myplace_notification)
                .setContentIntent(pendingIntent)
                .setCustomContentView(customView)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setColor(Color.parseColor("#000000"))
                .setAutoCancel(true)
                .build();

        notificationManager.notify(notificationId, notification);
    }
}