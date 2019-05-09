package com.example.ex1;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;


class StoredInfoFireStore {
    public ArrayList<ChatBox> chatBoxes;

    // read from remote db
    public StoredInfoFireStore() {
    }

    public void read(){
        chatBoxes = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ChatBoxes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ChatBox cb = document.toObject(ChatBox.class);
                                chatBoxes.add(cb);
                            }
                        }
                        else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void add(ChatBox cb){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("ChatBoxes").document(Integer.toString(cb.getId())).set(cb);
    }

    public void delete(ChatBox cb){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("ChatBoxes").document(Integer.toString(cb.getId())).delete();
        }

    }


