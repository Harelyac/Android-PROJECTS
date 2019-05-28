package com.example.ex1;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
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

        // set user name label according to data on extras
        TextView user_name = findViewById(R.id.textView4);
        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            String str = extras.getString("value");
            if (str != null) {
                user_name.setText("hello " + str + "!");
            }
        }



        // Initializer
        adapter = new ChatBoxRecyclerUtils.ChatBoxAdapter();
        app = (SelfChatApp) getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // deal with recycle object
        RecyclerView recyclerView = findViewById(R.id.chatBox_Recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        // check if it's just orientation change or first launch
        if (savedInstanceState == null)
        {
            // read from remote db into live data
            app.dataManager.read();
        }
        else
        {
            // read from local db into live data
            app.dataManager.readLocal(sp);
        }

        // get the live data object
        LiveData<ArrayList<ChatBox>> livedata = app.dataManager.getItemsLiveData();

        // observe the live data
        livedata.observe(this, new Observer<ArrayList<ChatBox>>() {
            @Override
            public void onChanged(@Nullable ArrayList<ChatBox> chatBoxes) {
                // update local db --> shared preferences
                app.dataManager.updateLocal(sp);


                adapter.submitList(chatBoxes);
                adapter.notifyDataSetChanged();

                // show array list size
                Log.i("Messages array size", String.valueOf(chatBoxes.size()));
            }
        });


        // set call back from adapter and message details back to main activity
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
                else {

                    Gson gson = new Gson();
                    String idCounterAsJson = sp.getString("my_id", null);
                    ChatBox.idCounter = gson.fromJson(idCounterAsJson, int.class);

                    ChatBox cb = new ChatBox(result,ts,android.os.Build.MODEL,android.os.Build.MANUFACTURER);
                    app.dataManager.add(cb);

                    // notify adapter
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
            }
        });
    }

    // deleting objects
    @Override
    public void onChatBoxClick(final ChatBox cb) {
        Intent intent = new Intent(MainActivity.this, MessageDetails.class);
        Gson gson = new Gson();
        intent.putExtra("ChatBox",gson.toJson(cb));
        startActivity(intent);
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
