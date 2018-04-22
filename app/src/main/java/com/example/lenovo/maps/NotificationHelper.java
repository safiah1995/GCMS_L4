package com.example.lenovo.maps;


import android.annotation.TargetApi;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

/**
 * Created by haneenbahgat on 23/03/2018.
 */

public class NotificationHelper extends ContextWrapper {
    public static final String channel1Id ="This is ID of channel 1";
    public static final String channel1Name ="channel 1";

    public static final String channel2Id ="This is ID of channel 2";
    public static final String channel2Name ="channel 2";

    private NotificationManager nm;

    public NotificationHelper(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CreateChannels();
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void CreateChannels() {
        NotificationChannel Channel1 = new NotificationChannel(channel1Id, channel1Name, NotificationManager.IMPORTANCE_HIGH);
        Channel1.enableLights(true);
        Channel1.enableVibration(true);
        Channel1.setLightColor(R.color.colorPrimary);
        Channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(Channel1);
///////////
        NotificationChannel Channel2 = new NotificationChannel(channel2Id, channel2Name, NotificationManager.IMPORTANCE_DEFAULT);
        Channel2.enableLights(true);
        Channel2.enableVibration(true);
        Channel2.setLightColor(R.color.colorPrimary);
        Channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(Channel2);

    }

    public NotificationManager getManager(){
        if(nm == null){
            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return nm;

    }

    public NotificationCompat.Builder getChannel1Notification (String title, String body){
        return new NotificationCompat.Builder(getApplicationContext(), channel1Id)
                .setContentTitle(title)
                .setContentText(body)
                .setOnlyAlertOnce(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setDefaults(Notification.FLAG_AUTO_CANCEL)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.logo);

    }

    public NotificationCompat.Builder getChannel2Notification (String title, String body){
        return new NotificationCompat.Builder(getApplicationContext(), channel1Id)
                .setContentTitle(title)
                .setContentText(body)
                .setOnlyAlertOnce(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setDefaults(Notification.FLAG_AUTO_CANCEL)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setSmallIcon(R.drawable.logo);

    }

}