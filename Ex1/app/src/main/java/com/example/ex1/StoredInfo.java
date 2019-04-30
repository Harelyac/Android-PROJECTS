package com.example.ex1;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class StoredInfo {

    public ArrayList<ChatBox> chatBoxes;


    public  StoredInfo (Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String chatBoxesAsJson = sp.getString("my_key",null);
        chatBoxes = gson.fromJson(chatBoxesAsJson, new TypeToken<ArrayList<ChatBox>>(){}.getType());
    }

    public void saveAll(ArrayList<ChatBox> chatBoxes, SharedPreferences sp){
        Gson gson = new Gson();
        String chatBoxesAsJson = gson.toJson(chatBoxes);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("my_key", chatBoxesAsJson);
        editor.apply();
    }

}
