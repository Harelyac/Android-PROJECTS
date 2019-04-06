package com.example.ex1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.text);
        final EditText editText = (EditText) findViewById(R.id.editText);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = editText.getText().toString();
                textView.setText(result);
                editText.setText("");
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("my_name", textView.getText().toString());
        super.onSaveInstanceState(outState); // calling override method from parent for additional implementation
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        textView.setText(savedInstanceState.getString("my_name"));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
