package com.example.ex1;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

public class MessageDetails extends AppCompatActivity {
    public SelfChatApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        app = (SelfChatApp) getApplicationContext();

        // get object to be deleted (probably)
        Gson gson = new Gson();
        String json = getIntent().getExtras().getString("ChatBox");
        final ChatBox cb = gson.fromJson(json, ChatBox.class);

        TextView Date = findViewById(R.id.textView5);
        TextView Phone_Type = findViewById(R.id.textView6);


        Date.append(cb.getTimestamp().toString());
        Phone_Type.append(cb.getManufactor() + ", " + cb.getModel());

        // get object of button
        Button DelButton = findViewById(R.id.button4);

        DelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MessageDetails.this);
                    alertDialog.setTitle("popup message");
                    alertDialog.setMessage("are you sure?");
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            app.dataManager.delete(cb);
                            dialog.cancel();
                            finish();
                        }
                    });

                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = alertDialog.create();
                    alert.show();
                }
        });

    }
}
