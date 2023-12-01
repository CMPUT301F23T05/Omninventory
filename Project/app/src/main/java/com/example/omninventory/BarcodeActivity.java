package com.example.omninventory;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

/**
 * Activity for scanning barcodes to specify an item description using the associated product
 * information.
 *
 * @author Aron
 * @reference https://github.com/yuriy-budiyev/code-scanner
 */
public class BarcodeActivity extends AppCompatActivity {

    private CodeScanner mCodeScanner;
    private final String apiKey = "8518E9E60AAD145CF70AD433EB26DFFE";

    /**
     * Method to dynamically request camera permissions for the barcode scanner
     * @param context Context of barcode activity
     * @reference https://stackoverflow.com/questions/67553067/cannot-open-camera-0-without-camera-permission
     */
    public static void checkCameraPermissions(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            // Permission is not granted
            Log.d("checkCameraPermissions", "No Camera Permissions");
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] { Manifest.permission.CAMERA },
                    100);
        }
    }

    /**
     * Method called on Activity creation. Contains most of the logic of this Activity; programmatically
     * modifying UI elements, creating Intents to move to other Activites, and setting up connection
     * to the database.
     * @param savedInstanceState Information about this Activity's saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner);

        // Dynamically request camera permissions
        checkCameraPermissions(this);

        // Setup UI Elements
        CodeScannerView scannerView = findViewById(R.id.barcode_scanner);
        Button backButton = findViewById(R.id.barcode_back_button);
        mCodeScanner = new CodeScanner(this, scannerView, CodeScanner.CAMERA_BACK);

        // Callback function for when the barcode is scanned
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {

                    // Once barcode is decoded from scanner, fetch the associated product information
                    @Override
                    public void run() {
                        fetchProductInfo(result.getText());
                    }
                });
            }
        });

        // Enables the barcode scanning process using the camera
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });


        // Back button that takes user back to EditActivity if no barcode is scanned
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * Resumes the camera preview for the barcode scanner when the activity is resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    /**
     * Initiates network request to fetch product information from UPC database based on provided
     * barcode. The method executes the request in a background thread and updates the UI with the
     * fetched product description.
     * @param barcode The barcode value to fetch product information for.
     */
    private void fetchProductInfo(final String barcode) {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                String productDescription = null;
                String productPrice = null;
                try {
                    // Request product information with given barcode from UPC database website
                    URL url = new URL("https://api.upcdatabase.org/product/" + barcode + "?apikey=" + apiKey);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        // Read JSON file of product information
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }

                        // Get JSON object of product information
                        JSONObject jsonObject = new JSONObject(result.toString());

                        // Product description and price extracted from JSON object
                        productDescription = jsonObject.getString("description");
                        productPrice = jsonObject.getString("msrp");
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final String finalProductDescription = productDescription;
                final String finalProductPrice = productPrice;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Update the UI
                        if (finalProductDescription != null && finalProductPrice != null) {
                            // Transfer product information to EditActivity
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("productDescription", finalProductDescription);
                            resultIntent.putExtra("productPrice", finalProductPrice);
                            setResult(Activity.RESULT_OK, resultIntent);

                            // Close BarcodeActivity and return to EditActivity
                            finish();
                        } else {
                            Toast.makeText(BarcodeActivity.this,
                                    "Product description or price " +
                                    "not found", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}
