package com.example.ex1;
import android.app.Application;


public class SelfChatApp extends Application {

    public DataManager dataManager;

    public void onCreate() {
     super.onCreate();
     dataManager = new DataManager();
    }
}
