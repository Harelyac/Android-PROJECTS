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
import java.sql.Timestamp;
import android.widget.Toast;
import java.util.Date;



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

        // get time
        Date date = new Date();
        final Timestamp ts = new Timestamp(date.getTime());


        // Initializer
        adapter = new ChatBoxRecyclerUtils.ChatBoxAdapter();
        app = (SelfChatApp) getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);


        // deal with recycle object
        RecyclerView recyclerView = findViewById(R.id.chatBox_Recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);


        // only execute on first launch
        if (savedInstanceState == null){
            // read and retrieve data from remote db -> add it to runnable and run it via bg thread
            RunnableWork runnable = new RunnableWork(app);
            new Thread(runnable).start();
            try {
                Thread.sleep(3000); // freeze ui thread until bg thread complete its work
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chatBoxes = runnable.chatBoxes;
        }
        // after change configuration for ex. change orientation
        else{
            // read from sp -> local db
            RunnableWork1 runnable1 = new RunnableWork1(sp,app);
            new Thread(runnable1).start();
            try {
                Thread.sleep(1000); // freeze ui thread until bg thread complete its work
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            chatBoxes = runnable1.chatBoxes;
        }

        // save that data to local db
        app.storedInfoSharedPreferences.saveAll(chatBoxes,sp);

        // notify adapter for changes
        adapter.submitList(chatBoxes);
        Log.i("Messages array size", String.valueOf(chatBoxes.size())); // show arraylist size

        // set callback from  ChatBoxAdapter object back to MainActivity object (this way one class [ChatBoxAdapter] calls function of another class[MainActivity]
        // usually after it completed some work and notify the other class about it .
        adapter.callback = this;

        // deal with other objects
        Button button = findViewById(R.id.button);
        final EditText editText = findViewById(R.id.editText);


        // adding objects
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = editText.getText().toString();
                if (result.equals("")) {
                    Toast.makeText(getApplicationContext(),TextEmpty,Toast.LENGTH_LONG).show();
                }
                else{
                    ChatBox cb = new ChatBox(result,ts);
                    // add object to local db
                    chatBoxes.add(cb);
                    app.storedInfoSharedPreferences.saveAll(chatBoxes,sp);

                    // notify adapter
                    adapter.notifyDataSetChanged();
                    editText.setText("");

                    // add object to remote db
                    app.storedInfoFireStore.add(cb);
                }
            }
        });
    }

    // deleting objects
    @Override
    public void onChatBoxClick(final ChatBox cb) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder((MainActivity)this);
        alertDialog.setTitle("popup message");
        alertDialog.setMessage("are you sure?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // delete from local db
                chatBoxes.remove(cb);
                adapter.notifyDataSetChanged();
                dialog.cancel();
                app.storedInfoSharedPreferences.saveAll(chatBoxes,sp);

                // delete from remote db
                app.storedInfoFireStore.delete(cb);
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
