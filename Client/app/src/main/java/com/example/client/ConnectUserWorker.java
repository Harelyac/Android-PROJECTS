package com.example.client;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Response;

public class ConnectUserWorker extends Worker {

    public ConnectUserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PostPcServerInterface serverInterface = ServerHolder.getInstance().serverInterface;
        String user_name = getInputData().getString("key_user_name");
        try {
            Response<TokenResponse> response = serverInterface.connectUser(user_name).execute();

            // convert json to gson using the auto convert of retrofit
            TokenResponse token = response.body();

            // convert back to json for further use
            String token_as_jason = new Gson().toJson(token);
            Log.d("two", "doWork: token is: " + token_as_jason);
            Data output_data = new Data.Builder()
                    .putString("key_token", token_as_jason)
                    .build();
            return Result.success(output_data);
        }

        catch (IOException e){
            e.printStackTrace();
            return Result.failure();
        }
    }
}
