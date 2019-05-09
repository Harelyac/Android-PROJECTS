package com.example.ex1;
import java.util.ArrayList;

public class RunnableWork implements Runnable {

    public SelfChatApp app;
    public ArrayList<ChatBox> chatBoxes;

    public RunnableWork(SelfChatApp new_app){
        app = new_app;
    }

    @Override
    public void run() {
        app.storedInfoFireStore.read();
        chatBoxes = app.storedInfoFireStore.chatBoxes;
    }
}
