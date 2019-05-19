package com.example.smalldemoapp;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Screen1 extends AppCompatActivity {

    private static final String TAG = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen1);

        // create widget objects
        Button button = findViewById(R.id.button);
        TextView textView = findViewById(R.id.textView);

        // saved data from screen 3 activity
        Bundle extras = getIntent().getExtras();

        Log.d(TAG, "onCreate: " + extras);

        if(extras != null) {
            String str = extras.getString("value");
            if (str != null) {
                textView.setText(str);
            }
        }


        // set listener on button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Screen1.this, Screen2.class);
                startActivity(intent);
            }
        });


        // set listener on text view
        textView.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Screen1.this, Screen3.class);
                startActivity(intent);
            }
        });
    }


    // when another activity comes to foreground and this activity is no longer visible
    @Override
    protected void onPause() {
        super.onPause();
        finish(); // finish the activity avoid chains of activity
    }
}
