package com.example.ex1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class StoredInfoSharedPreferences {

    public ArrayList<ChatBox> chatBoxes;


    public StoredInfoSharedPreferences() {
    }

    // read just id because read of chat boxes array is from remote only
    public void read(SharedPreferences sp){
        Gson gson = new Gson();
        String idCounterAsJson = sp.getString("my_id",null);
        ChatBox.idCounter = gson.fromJson(idCounterAsJson, int.class);
        String chatBoxesAsJson = sp.getString("my_key",null);
        chatBoxes = gson.fromJson(chatBoxesAsJson, new TypeToken<ArrayList<ChatBox>>(){}.getType());
    }

    // saves both chat boxes array and id
    public void saveAll(ArrayList<ChatBox> chatBoxes, SharedPreferences sp){
        Gson gson = new Gson();
        String chatBoxesAsJson = gson.toJson(chatBoxes);
        String idCounterAsJson = gson.toJson(ChatBox.idCounter);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("my_key", chatBoxesAsJson);
        editor.putString("my_id", idCounterAsJson);
        editor.apply();
    }

}
