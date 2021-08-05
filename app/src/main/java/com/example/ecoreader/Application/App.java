package com.example.ecoreader.Application;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID_1 = "channel1";
    public static final String GROUP_1 = "group_1";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannelGroup(new NotificationChannelGroup(GROUP_1, "First Group"));

            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID_1, "Notification Channel", NotificationManager.IMPORTANCE_LOW);
            channel1.setDescription("This channel is responsible for handling off-app retrieving of Australian Economic Data.");
            channel1.setGroup(GROUP_1);

            manager.createNotificationChannel(channel1);
        }
    }
}
