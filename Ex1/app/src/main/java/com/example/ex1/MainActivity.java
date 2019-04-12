package com.example.ex1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ChatBoxRecyclerUtils.ChatBoxClickCallback {

    public ChatBoxRecyclerUtils.ChatBoxAdapter adapter = new ChatBoxRecyclerUtils.ChatBoxAdapter();
    public ArrayList<ChatBox> chatBoxes = new ArrayList<>(ChatBox.getAll());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        final EditText editText = (EditText) findViewById(R.id.editText);
        RecyclerView recyclerView = findViewById(R.id.chatBox_Recycler);

        // deal with recycle object
        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        adapter.callback = this;
        adapter.submitList(chatBoxes);

        // set click lister on button object
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = editText.getText().toString();
                ChatBox cb = new ChatBox(1,result,"harel","harel");
                chatBoxes.add(cb);
                adapter.submitList(chatBoxes);
                editText.setText("");
            }
        });


    }


    @Override
    public void onChatBoxClick(ChatBox cb) {
        ArrayList<ChatBox> chatBoxesCopy = new ArrayList<>(this.chatBoxes);
        chatBoxesCopy.remove(cb);
        this.chatBoxes = chatBoxesCopy;
        this.adapter.submitList(this.chatBoxes);
    }

/* @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("my_name", textView.getText().toString());
        super.onSaveInstanceState(outState); // calling override method from parent for additional implementation
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        textView.setText(savedInstanceState.getString("my_name"));
        super.onRestoreInstanceState(savedInstanceState);*/

}
