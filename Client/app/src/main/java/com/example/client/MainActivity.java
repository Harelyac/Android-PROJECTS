package com.example.client;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.google.gson.Gson;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "one";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ask for user name from user
        final EditText editText = findViewById(R.id.editText);

        ImageButton Ibutton = findViewById(R.id.imageButton5);

        Ibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = editText.getText().toString();
                Log.d(TAG, "onClick: user_name:" + user_name);
                connectUser(user_name);
            }
        });


    }

    private void connectUser(String user_name) {

        // create the work
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest connectUserWork = new OneTimeWorkRequest.Builder(ConnectUserWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_user_name", user_name).build())
                .addTag(workTagUniqueId.toString())
                .build();

        // add it to work queue
        WorkManager.getInstance().enqueue(connectUserWork);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe((LifecycleOwner) this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                // there might be some time until this worker is finished to work (in the meantime we will get an empty list
                // so check for that
                if (workInfos == null || workInfos.isEmpty())
                    return ;

                WorkInfo info = workInfos.get(0);

                // now we can use it
                String token_as_json = info.getOutputData().getString("key_token");
                TokenResponse token = new Gson().fromJson(token_as_json, TokenResponse.class);

                if (token != null){
                    getUserInfo(token.data);
                }

//                TextView textView = findViewById(R.id.textView3);
//                if (token != null) {
//                    textView.setText(token.data);
//                    textView.setVisibility(VISIBLE);
//                }

            }
        });
    }

    private void getUserInfo(final String token){
        // create the work
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest getUserInfoWork = new OneTimeWorkRequest.Builder(GetUserInfoWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putString("key_token", token).build())
                .addTag(workTagUniqueId.toString())
                .build();

        // add it to work queue
        WorkManager.getInstance().enqueue(getUserInfoWork);

        WorkManager.getInstance().getWorkInfosByTagLiveData(workTagUniqueId.toString()).observe((LifecycleOwner) this, new Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                // we know there will be only 1 work info in this list - the 1 work with that specific tag!
                // there might be some time until this worker is finished to work (in the meantime we will get an empty list
                // so check for that
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                }

                WorkInfo info = workInfos.get(0);

                // now we can use it
                String user_as_json = info.getOutputData().getString("key_user_info");

                if (user_as_json != null){
                    // intent to new screen (activity) containing info
                    Intent i = new Intent(MainActivity.this, UserProfile.class);
                    i.putExtra("key_user_info", user_as_json);
                    i.putExtra("key_token", token);
                    startActivity(i);
                }

            }
        });
    }
}

