package com.example.ex1;
import android.app.Application;


public class SelfChatApp extends Application {

    public StoredInfoSharedPreferences storedInfoSharedPreferences;
    public StoredInfoFireStore storedInfoFireStore;

    public void onCreate() {
     super.onCreate();
     storedInfoSharedPreferences = new StoredInfoSharedPreferences();
     storedInfoFireStore = new StoredInfoFireStore();
    }
}
