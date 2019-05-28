package com.example.ex1;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class IntroActivity extends AppCompatActivity {


    private Executor executor = Executors.newCachedThreadPool(); // first bg thread
    private int progressCounter = 0;

    // used to jump from bg thread back to ui tread
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        final ProgressBar pbar = findViewById(R.id.progressbar);
        final TextView tView =  findViewById(R.id.LoadingCompleteTextView);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        progressCounter = 100;
                        executor.execute(new Runnable() {
                            @Override
                            public void run() {
                                android.os.SystemClock.sleep(750);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbar.setProgress(progressCounter);
                                        tView.setVisibility(View.VISIBLE);
                                    }
                                });
                                if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                    android.os.SystemClock.sleep(250);
                                    Intent inte = new Intent(IntroActivity.this, MainActivity.class);
                                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                    inte.putExtra("value", document.get("name").toString());
                                    startActivity(inte);
                                }
                                else {
                                    Intent inte = new Intent(IntroActivity.this,ConfigureName.class);
                                    startActivity(inte);
                                }
                            }
                        });
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
