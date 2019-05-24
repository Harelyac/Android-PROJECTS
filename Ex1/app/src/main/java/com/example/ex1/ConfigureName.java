package com.example.ex1;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConfigureName extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_name);
        // create object for widget
        final EditText editText = findViewById(R.id.editText2);
        final Button nameButton = findViewById(R.id.button2);
        final Button skipButton = findViewById(R.id.button3);
        nameButton.setVisibility(View.INVISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count != 0)
                {
                    nameButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        // set listener on button
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usnername = editText.getText().toString();

                // second add to fire store
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String,Object> map = new HashMap<>();
                map.put("name", usnername);
                db.collection("users").document(usnername).set(map);

                Intent intent = new Intent(ConfigureName.this, MainActivity.class);
                intent.putExtra("value", usnername);
                startActivity(intent);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfigureName.this, MainActivity.class);
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
