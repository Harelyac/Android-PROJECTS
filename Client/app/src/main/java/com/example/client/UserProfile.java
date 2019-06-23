package com.example.client;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserProfile extends AppCompatActivity   {

    String edit_image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        // get the user info
        String user_info_as_json = getIntent().getExtras().get("key_user_info").toString();
        final String token = getIntent().getExtras().get("key_token").toString();
        Gson gson  = new Gson();
        UserResponse user = gson.fromJson(user_info_as_json, UserResponse.class);

        // set pretty name
        final EditText editText = findViewById(R.id.editText2);
        editText.setText(user.data.pretty_name);

        // substring the image name from the url
        String url = user.data.image_url;
        String image = url.substring(url.lastIndexOf('/')+1, url.lastIndexOf('.'));

        // set profile image
        ImageView imageView = findViewById(R.id.imageView);
        Picasso.get().load("http://hujipostpc2019.pythonanywhere.com" + url).into(imageView);

        // create reference to spinner
        Spinner spinner = findViewById(R.id.Spinner1);

        // create adapter to array resource
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.images, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // combine adapter with spinner
        spinner.setAdapter(adapter);

        // set current profile image as "default"
        spinner.setSelection(adapter.getPosition(image));

        // set listener on selected item
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String image = parent.getItemAtPosition(position).toString();
                Toast.makeText(UserProfile.this, image, Toast.LENGTH_SHORT).show();
                edit_image = "/images/" + image + ".png";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // update pretty name
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditUserName(token,editText.getText().toString());

            }
        });

        // update profile image
        Button button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               EditUserProfileImage(token,edit_image);
            }
        });

    }

    private void EditUserName(String token, String pretty_name) {

        HashMap hm = new HashMap();
        hm.put("key_token", token);
        hm.put("key_pretty_name", pretty_name);

        // create the work
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest EditUserNameWork = new OneTimeWorkRequest.Builder(EditUserNameWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putAll(hm).build())
                .addTag(workTagUniqueId.toString())
                .build();

        // add it to work queue
        WorkManager.getInstance().enqueue(EditUserNameWork);

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
                String user_as_json = info.getOutputData().getString("key_user_info");

                if (user_as_json != null){
                    Gson gson = new Gson();
                    UserResponse user = gson.fromJson(user_as_json, UserResponse.class);

                    // set pretty name
                    final EditText editText = findViewById(R.id.editText2);
                    editText.setText(user.data.pretty_name);

                    // set image
                    // substring the image name from the url
                    String url = user.data.image_url;
                    String image = url.substring(url.lastIndexOf('/')+1, url.lastIndexOf('.'));
                    // create reference to spinner
                    Spinner spinner = findViewById(R.id.Spinner1);

                    // create adapter to array resource
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.images, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // combine adapter with spinner
                    spinner.setAdapter(adapter);

                    // set current profile image as "default"
                    spinner.setSelection(adapter.getPosition(image));

                    // set profile image
                    ImageView imageView = findViewById(R.id.imageView);
                    Picasso.get().load("http://hujipostpc2019.pythonanywhere.com" + url).into(imageView);
                }
            }
        });
    }

    private void EditUserProfileImage(String token, String image) {

        HashMap hm = new HashMap();
        hm.put("key_token", token);
        hm.put("key_image_url", image);

        // create the work
        UUID workTagUniqueId = UUID.randomUUID();
        OneTimeWorkRequest EditUserProfileImageWork = new OneTimeWorkRequest.Builder(EditUserProfileImageWorker.class)
                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .setInputData(new Data.Builder().putAll(hm).build())
                .addTag(workTagUniqueId.toString())
                .build();

        // add it to work queue
        WorkManager.getInstance().enqueue(EditUserProfileImageWork);

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
                String user_as_json = info.getOutputData().getString("key_user_info");

                if (user_as_json != null){
                    Gson gson = new Gson();
                    UserResponse user = gson.fromJson(user_as_json, UserResponse.class);

                    // set pretty name
                    final EditText editText = findViewById(R.id.editText2);
                    editText.setText(user.data.pretty_name);

                    // set image
                    // substring the image name from the url
                    String url = user.data.image_url;
                    String image = url.substring(url.lastIndexOf('/')+1, url.lastIndexOf('.'));
                    // create reference to spinner
                    Spinner spinner = findViewById(R.id.Spinner1);

                    // create adapter to array resource
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),R.array.images, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // combine adapter with spinner
                    spinner.setAdapter(adapter);

                    // set current profile image as "default"
                    spinner.setSelection(adapter.getPosition(image));

                    // set profile image
                    ImageView imageView = findViewById(R.id.imageView);
                    Picasso.get().load("http://hujipostpc2019.pythonanywhere.com" + url).into(imageView);
                }
            }
        });
    }
}
