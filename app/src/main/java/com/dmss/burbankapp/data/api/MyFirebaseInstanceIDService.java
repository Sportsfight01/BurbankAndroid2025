package com.dmss.burbankapp.data.api;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Timber.e("New notification %s", remoteMessage.getData().toString());
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Timber.e("onNewToken: %s", s);
    }
}