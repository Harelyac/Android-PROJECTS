package com.example.ex1;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements ChatBoxRecyclerUtils.ChatBoxClickCallback {

    public ChatBoxRecyclerUtils.ChatBoxAdapter adapter;
    public SelfChatApp app;
    public ArrayList<ChatBox> chatBoxes;
    String TextEmpty = "you can't send an empty message, oh silly!";
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initializer
        adapter = new ChatBoxRecyclerUtils.ChatBoxAdapter();
        app = (SelfChatApp) getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);


        // deal with recycle object
        RecyclerView recyclerView = findViewById(R.id.chatBox_Recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        chatBoxes = app.storedInfo.chatBoxes; // retrieve saved chat boxes from sharedPreference
        adapter.submitList(chatBoxes);
        Log.i("Messages array size", String.valueOf(chatBoxes.size())); // show arraylist size

        adapter.callback = this;

        // deal with other objects
        Button button = findViewById(R.id.button);
        final EditText editText = findViewById(R.id.editText);


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
                    app.storedInfo.saveAll(chatBoxes,sp);
                }
            }
        });
    }


    @Override
    public void onChatBoxClick(final ChatBox cb) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder((MainActivity)this);
        alertDialog.setTitle("popup message");
        alertDialog.setMessage("are you sure?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chatBoxes.remove(cb);
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
//        alertDialog.show();
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("array", chatBoxes);
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        chatBoxes = savedInstanceState.getParcelableArrayList("array");
        adapter.submitList(chatBoxes);
    }

}
