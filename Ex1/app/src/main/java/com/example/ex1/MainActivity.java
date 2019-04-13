package com.example.ex1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{ // implements ChatBoxRecyclerUtils.ChatBoxClickCallback

    public ChatBoxRecyclerUtils.ChatBoxAdapter adapter = new ChatBoxRecyclerUtils.ChatBoxAdapter();
    public ArrayList<ChatBox> chatBoxes;

    String TextEmpty = "you can't send an empty message, oh silly!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // deal with recycle object
        RecyclerView recyclerView = findViewById(R.id.chatBox_Recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        if (savedInstanceState == null || !savedInstanceState.containsKey("array")){
            chatBoxes = new ArrayList<ChatBox>();
            chatBoxes.add(new ChatBox("FirstCB"));
            adapter.submitList(chatBoxes);
        }
        else {
            chatBoxes = savedInstanceState.getParcelableArrayList("array");
            adapter.submitList(chatBoxes);
        }

        // adapter.callback = this;


        // deal with other objects
        Button button = (Button) findViewById(R.id.button);
        final EditText editText = (EditText) findViewById(R.id.editText);


        // set click lister on button object
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = editText.getText().toString();
                if (result.equals("")) {
                    Toast.makeText(getApplicationContext(),TextEmpty,Toast.LENGTH_LONG).show();
                }
                else{
                    ChatBox cb = new ChatBox(result);
                    chatBoxes.add(cb);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
            }
        });


    }


  /*  @Override
    public void onChatBoxClick(ChatBox cb) {
        chatBoxes.remove(cb);
        adapter.submitList(chatBoxes);
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("array", chatBoxes);
    }
}
