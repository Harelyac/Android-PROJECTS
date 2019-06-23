package com.example.pricey;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.pricey.common.CameraSource;
import com.example.pricey.common.CameraSourcePreview;
import com.example.pricey.common.GraphicOverlay;
import com.example.pricey.currency.FixerApi;
import com.example.pricey.currency.Response;
import com.example.pricey.textrecognition.TextRecognitionProcessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, AdapterView.OnItemSelectedListener {

    // Fields.
    private static final String TEXT_DETECTION = "Text Detection";
    private static final String TAG = "LivePreviewActivity";
    private static final int PERMISSION_REQUESTS = 1;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private TextView fixerRate;
    private Response fixerResponse;

    private String baseCurrency = "ILS";
    private String targetCurrency = "ILS";
    private float conversionRate = 1.0f;
    private String isConversionInitilizedKey = "Initialized";
    private TextRecognitionProcessor textRecognitionProcessor;
    private String MY_PREFS_NAME = "MY_PREFERENCES";
    // MainActivity main OnCreate method.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.pricey_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Set camera source and overlay.
        Log.d(TAG, "onCreate");
        preview = (CameraSourcePreview) findViewById(R.id.firePreview);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = (GraphicOverlay) findViewById(R.id.fireFaceOverlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // status bar is transparant
//        getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        );



        // Get currency rates.
        fixerRate = (TextView) findViewById(R.id.conversionRate);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://data.fixer.io/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FixerApi fixerApi = retrofit.create(FixerApi.class);
        Call<Response> call = fixerApi.getResponse();
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (!response.isSuccessful()) {
                    fixerRate.setText("Code: " + response.code());
                    return;
                }

                fixerResponse = response.body();
                String[] currencyList = fixerResponse.rates.getCurrencyNames();
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                for (String currency : currencyList)
                {
                    editor.putFloat(currency,fixerResponse.rates.getConversionRate("EUR",currency));
                }
                editor.putString(isConversionInitilizedKey,"True");
                editor.commit();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Error getting currency rates from Fixer.io", Toast.LENGTH_SHORT).show();
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String restoredText = prefs.getString(isConversionInitilizedKey, null);
                if (restoredText != null) {
                    ((TextView) findViewById(R.id.entryText)).setText("Using downloaded conversion rate");
                }else{
                    ((TextView) findViewById(R.id.entryText)).setText("Please enable network and restart the app:)");
                    ((TextView) findViewById(R.id.conversionRate)).setText("");
                }
            }
        });

        // Create spinners for available currencies.
        Spinner spinnerBase = (Spinner) findViewById(R.id.baseSpinner);
        Spinner spinnerTarget = (Spinner) findViewById(R.id.targetSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterBase = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterTarget = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapterBase.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterTarget.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerBase.setAdapter(adapterBase);
        spinnerTarget.setAdapter(adapterTarget);
        spinnerBase.setOnItemSelectedListener(this);
        spinnerTarget.setOnItemSelectedListener(this);


        // Check permissions and start camera.
        if (allPermissionsGranted()) {
            createCameraSource();
        } else {
            getRuntimePermissions();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
             return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.about){
            Intent intent = new Intent(this, AboutTheAppActivity.class);
            this.startActivity(intent);
        }

        if (item.getItemId()==R.id.contact){
            Intent intent = new Intent(this, ContactUsActivity.class);
            this.startActivity(intent);
        }
                return super.onOptionsItemSelected(item);
    }

    // Camera
    private void createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        Log.i(TAG, "Using Text Detector Processor");
        textRecognitionProcessor = new TextRecognitionProcessor();
        cameraSource.setMachineLearningFrameProcessor(textRecognitionProcessor);
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        startCameraSource();
    }

    /** Stops the camera. */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }


    // Permissions
    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        if (fixerResponse != null) {
            switch(parent.getId()) {
                case R.id.baseSpinner:
                    baseCurrency = parent.getItemAtPosition(position).toString();
                    conversionRate = fixerResponse.rates.getConversionRate(baseCurrency, targetCurrency);
                    break;
                case R.id.targetSpinner:
                    targetCurrency = parent.getItemAtPosition(position).toString();
                    conversionRate = fixerResponse.rates.getConversionRate(baseCurrency, targetCurrency);
                    break;
            }

            textRecognitionProcessor.setConversionRate(conversionRate);
            fixerRate.setText(String.valueOf(conversionRate));
        }
        else{
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String restoredText = prefs.getString(isConversionInitilizedKey, null);
            if (restoredText != null){
                switch(parent.getId()) {
                    case R.id.baseSpinner:
                        baseCurrency = parent.getItemAtPosition(position).toString();
                        break;
                    case R.id.targetSpinner:
                        targetCurrency = parent.getItemAtPosition(position).toString();
                        break;
                }
                float baseCurrency_to_EUR = prefs.getFloat(baseCurrency,0);
                float targetCurrency_to_EUR = prefs.getFloat(targetCurrency,0);
                conversionRate = baseCurrency_to_EUR / targetCurrency_to_EUR;
                textRecognitionProcessor.setConversionRate(conversionRate);
                fixerRate.setText(String.valueOf(conversionRate));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }
}
