package com.example.myspecialstalker;
import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


public class DeliveryCustomeService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DeliveryCustomeService(String name) {
        super(name);
    }

    public DeliveryCustomeService() {
        super("DeliveryCustomeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extra = intent.getExtras();
        Notification ntfc = new NotificationCompat.Builder(this, extra.getString("Channel"))
                .setSmallIcon(R.drawable.sms0)
                .setContentTitle("Notification No 3.")
                .setContentText("message received successfully!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        // notify the notification manager about the newly notification
        NotificationManagerCompat.from(this).notify(extra.getInt("notification"),ntfc);
    }
}
