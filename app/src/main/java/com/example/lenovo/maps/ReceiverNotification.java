package com.example.lenovo.maps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by HP on 03/04/18.
 */

public class ReceiverNotification extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationService.class));

    }
}
