package vaga.io.wizzchat.services;

import vaga.io.wizzchat.R;
import vaga.io.wizzchat.RoomActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class GcmMessageHandler extends GcmListenerService {

    private static final String TAG = "GcmMessageHandler";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        //String message = data.getString("message");

        Log.d(TAG, data.toString());

        //createNotification(from, message);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RoomActivity.NOTIFY_REFRESH_THREAD);
        broadcastIntent.putExtra("author", data.getString("author"));
        broadcastIntent.putExtra("date", data.getString("date"));
        broadcastIntent.putExtra("message", data.getString("message"));

        sendBroadcast(broadcastIntent);
    }
}