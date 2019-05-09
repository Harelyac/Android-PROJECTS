package com.example.ex1;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class RunnableWork1 implements Runnable {

    public SharedPreferences sp;
    public SelfChatApp app;
    public ArrayList<ChatBox> chatBoxes;

    public RunnableWork1(SharedPreferences new_sp, SelfChatApp new_app){
        sp = new_sp;
        app = new_app;
    }

    @Override
    public void run() {
        app.storedInfoSharedPreferences.read(sp);
        chatBoxes = app.storedInfoSharedPreferences.chatBoxes;
    }
}
