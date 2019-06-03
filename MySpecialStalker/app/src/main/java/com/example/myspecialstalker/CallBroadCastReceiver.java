package com.example.myspecialstalker;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.MutableLiveData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;


public class CallBroadCastReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "100";
    public static final int NOTIFICATION_ID = 1;
    private final MutableLiveData<String> CallHasBeenMade = new MutableLiveData<>();
    String textTitle = "SMS STATUS:";
    String textContent = "sending message...";
    Notification ntfc;


    // get the needed context
    CallBroadCastReceiver(){
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // create nofitication channel
        createNotificationChannel(context);

        // fast create notification
        createNotification(context);

        // notify the notification manager about the newly notification
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID,ntfc);

        // notify observer to send sms about the outgoing call
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            CallHasBeenMade.postValue(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
        }
    }

    public MutableLiveData<String> CallStatus(){
        return CallHasBeenMade;
    }



    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


   private void createNotification(Context context) {
       ntfc = new NotificationCompat.Builder(context, CHANNEL_ID)
               .setSmallIcon(R.drawable.sms0)
               .setContentTitle(textTitle)
               .setContentText(textContent)
               .setPriority(NotificationCompat.PRIORITY_DEFAULT)
               .build();
   }

}
