package com.example.myspecialstalker;
import android.Manifest;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;


import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MainActivity extends AppCompatActivity {
    private static final int ALL_PERMISSION_CODE = 1546;
    private static final String TAG = "PERMISSION";
    public SharedPreferences sp;
    EditText Target;
    EditText content;
    TextView MissingInfo;
    final CallBroadCastReceiver CallReceiver = new CallBroadCastReceiver();
    String sTarget;
    String sContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MainActivity activity = this;

        // register the receiver to specific intent filter
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(CallReceiver, filter);


        // checks if we got permission
        boolean hasSmsPermission =
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS)
                        == PERMISSION_GRANTED;

        boolean hasCallPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.PROCESS_OUTGOING_CALLS)
                == PERMISSION_GRANTED;

        boolean hasPhonePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                == PERMISSION_GRANTED;


        if (hasSmsPermission && hasCallPermission && hasPhonePermission) {
            PrepareSettings();
        }
        else
        {
            final String[] permissions = new String[]{Manifest.permission.SEND_SMS, Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.READ_PHONE_STATE};
            ActivityCompat.requestPermissions(activity, permissions, ALL_PERMISSION_CODE);

        }


        CallReceiver.CallStatus().observe(this, new Observer<String>() {

            @Override
            public void onChanged(@Nullable String s) {
                if (!s.isEmpty()){
                    sendSms(s);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        int granted = PackageManager.PERMISSION_GRANTED;
        if(grantResults.length == 3){
            if (grantResults[0] == granted && grantResults[1] == granted && grantResults[2] == granted) {
                PrepareSettings();
            }
            else {
                ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSION_CODE);
            }
        }

    }

    public  void PrepareSettings() {
        Target = findViewById(R.id.Target);
        content = findViewById(R.id.Content);
        MissingInfo = findViewById(R.id.MissingInfo);

        // add same watcher to the two edit texts
        Target.addTextChangedListener(SettingsWatcher);
        content.addTextChangedListener(SettingsWatcher);
    }

    private final TextWatcher SettingsWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            sTarget = Target.getText().toString().trim();
            sContent = content.getText().toString().trim();

            if (!sTarget.isEmpty() && !sContent.isEmpty()) {
                MissingInfo.setTextColor(Color.parseColor("#26EE17"));
                MissingInfo.setText("ready to send SMS messages!");
            }
            else {
                MissingInfo.setTextColor(Color.parseColor("#F10D1F"));
                MissingInfo.setText("Missing Info");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("content",content.getText().toString());
        editor.putString("target",Target.getText().toString());
        editor.apply();
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Target.setText(sp.getString("target", null));
        content.setText(sp.getString("content", null));
    }



    public void sendSms (String OutgoingPhoneNumber) {

        // sends two pending intents to two custome services by giving them the permission to update
        // push notification bar
        Intent sentIntent = new Intent(MainActivity.this, SentCustomeService.class);
        sentIntent.putExtra("Channel",CallBroadCastReceiver.CHANNEL_ID);
        sentIntent.putExtra("notification", CallBroadCastReceiver.NOTIFICATION_ID);
        PendingIntent sentService =  PendingIntent.getService(MainActivity.this,1,sentIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent deliveryIntent = new Intent(MainActivity.this, DeliveryCustomeService.class);
        deliveryIntent.putExtra("Channel",CallBroadCastReceiver.CHANNEL_ID);
        deliveryIntent.putExtra("notification", CallBroadCastReceiver.NOTIFICATION_ID);
        PendingIntent deliveryService = PendingIntent.getService(MainActivity.this,2,deliveryIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        // cool! , the pending intent will be broadcast when sms status changes accordingly :)
        SmsManager.getDefault().sendTextMessage(
                sTarget,
                null,
                sContent + " " + OutgoingPhoneNumber,
                sentService,
                deliveryService);

    }
}

