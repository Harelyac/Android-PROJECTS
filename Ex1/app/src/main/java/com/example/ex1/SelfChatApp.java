package com.example.ex1;
import android.app.Application;


public class SelfChatApp extends Application {

    public StoredInfo storedInfo;

    public void onCreate() {
     super.onCreate();
     storedInfo = new StoredInfo(this);
    }
}
