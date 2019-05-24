package com.example.ex1;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import static android.support.constraint.Constraints.TAG;


class DataManager {
    private MutableLiveData<ArrayList<ChatBox>> ChatBoxLiveData = new MutableLiveData<>();
    private Executor executor1 = Executors.newCachedThreadPool(); // first bg thread
    private Executor executor2 = Executors.newCachedThreadPool(); // second bg thread

    // read from remote db
    public DataManager() {
    }

    public void read() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // put empty array list via main thread to start with something
        ChatBoxLiveData.setValue(new ArrayList<ChatBox>());


        db.collection("ChatBoxes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        executor1.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    ArrayList<ChatBox> chatBoxes = new ArrayList<ChatBox>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ChatBox cb = document.toObject(ChatBox.class);
                                        chatBoxes.add(cb);
                                    }
                                    DataManager.this.ChatBoxLiveData.postValue(chatBoxes);
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                    }
                });
    }


    // add to live data & remote db
    public void add(ChatBox cb) {
        // first add to LiveData
        ArrayList<ChatBox> temp = ChatBoxLiveData.getValue();
        temp.add(cb);
        ChatBoxLiveData.postValue(temp);

        // second add to fire store
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ChatBoxes").document(Integer.toString(cb.getId())).set(cb);
    }


    // delete from live data & remote db
    public void delete(ChatBox cb) {
        // first remove from LiveData
        ArrayList<ChatBox> temp = ChatBoxLiveData.getValue();
        temp.remove(cb);
        ChatBoxLiveData.postValue(temp);

        // second remove from fire store
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ChatBoxes").document(Integer.toString(cb.getId())).delete();
    }

    // returns live data
    public LiveData<ArrayList<ChatBox>> getItemsLiveData() {
        return ChatBoxLiveData;
    }


    // update local db from live data
    void updateLocal(SharedPreferences sp) {
        Gson gson = new Gson();
        ArrayList<ChatBox> temp = ChatBoxLiveData.getValue();
        String chatBoxesAsJson = gson.toJson(temp);
        String idCounterAsJson = gson.toJson(ChatBox.idCounter);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("my_key", chatBoxesAsJson);
        editor.putString("my_id", idCounterAsJson);
        editor.apply();
    }

    // read local db to live data
    // also on bg thread
    public void readLocal(final SharedPreferences sp) {
        executor2.execute(new Runnable() {
            @Override
            public void run() {
                Gson gson = new Gson();
                ArrayList<ChatBox> chatBoxes = new ArrayList<ChatBox>();
                String chatBoxesAsJson = sp.getString("my_key", null);
                chatBoxes = gson.fromJson(chatBoxesAsJson, new TypeToken<ArrayList<ChatBox>>() {
                }.getType());
                DataManager.this.ChatBoxLiveData.postValue(chatBoxes);
            }
        });
    }
}


