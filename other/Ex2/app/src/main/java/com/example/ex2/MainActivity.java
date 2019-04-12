package com.example.ex2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ChatBoxRecyclerUtils.ChatBoxClickCallback {


    private ChatBoxRecyclerUtils.ChatBoxAdapter adapter
            = new ChatBoxRecyclerUtils.ChatBoxAdapter();


    private ArrayList<ChatBox> chatBoxes = new ArrayList<>(ChatBox.getAll());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.chatBox_Recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
        adapter.callback = this;

        adapter.submitList(chatBoxes);
    }

    @Override
    public void onChatBoxClick(ChatBox cb) {
        ArrayList<ChatBox> chatBoxesCopy = new ArrayList<>(this.chatBoxes);
        chatBoxesCopy.remove(cb);
        this.chatBoxes = chatBoxesCopy;
        this.adapter.submitList(this.chatBoxes);
    }

    public void onSendButtonClick(ChatBox cb) {

    }
}
