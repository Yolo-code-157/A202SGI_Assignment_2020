package com.example.cheeh.findmyfriend.Utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.cheeh.findmyfriend.R;

public class NotificationHelper extends ContextWrapper{
    private static final String FMF_CHANNEL_ID = "com.example.cheeh.findmyfriend";
    private static final String FMF_CHANNEL_NAME = "FindMyFriend";

    private NotificationManager manager;


    public NotificationHelper(Context base) {
        super(base);
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel(); //see line 30
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel FMFChannel = new NotificationChannel(FMF_CHANNEL_ID,
                FMF_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        FMFChannel.enableLights(false);
        FMFChannel.enableVibration(true);
        FMFChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        
        getManager().createNotificationChannel(FMFChannel);
    }

    public NotificationManager getManager() {
        if(manager == null){
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getRealtimeTrackingNotification(String title, String content, Uri defaultSound) {
        return new Notification.Builder(getApplicationContext(), FMF_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(defaultSound)
                .setAutoCancel(false);
    }
}
