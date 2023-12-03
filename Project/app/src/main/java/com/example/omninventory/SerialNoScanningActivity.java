package com.example.omninventory;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;

import android.widget.TextView;
import android.widget.Toast;

import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import androidx.camera.core.ImageAnalysis;
import androidx.lifecycle.LifecycleOwner;

/**
 * Activity for scanning serial numbers from an image taken from the camera.
 *
 * @author Zachary
 */
public class SerialNoScanningActivity extends AppCompatActivity {
    private ExecutorService cameraExecutor;
    private TextView parsedSerialNoText;
    private String scannedText;

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
        setContentView(R.layout.activity_serial_no_scanning);

        PreviewView previewView = findViewById(R.id.preview_view);

        // Dynamically request camera permissions
        checkCameraPermissions(this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        Button cancelButton = findViewById(R.id.serialno_cancel_button);
        // Back button that takes user back to EditActivity if no barcode is scanned
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button confirmButton = findViewById(R.id.serialno_confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("serialno", scannedText);
                setResult(Activity.RESULT_OK, resultIntent);

                // Close BarcodeActivity and return to EditActivity
                finish();
            }
        });

        parsedSerialNoText = findViewById(R.id.parsed_serialno_text);

        startCamera(previewView);
    }

    private void startCamera(PreviewView previewView) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

                imageAnalysis.setAnalyzer(cameraExecutor, new TextAnalyzer());
                cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis);

                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private class TextAnalyzer implements ImageAnalysis.Analyzer {
        private final TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        @OptIn(markerClass = ExperimentalGetImage.class) @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                textRecognizer.process(image)
                        .addOnSuccessListener(result -> {
                            List<Text.TextBlock> blocks = result.getTextBlocks();
                            for (Text.TextBlock block : blocks) {
                                String text = block.getText();
                                handleScan(text);
                            }
                        })
                        .addOnFailureListener(e -> {
                            parsedSerialNoText.setText("N/A");
                        })
                        .addOnCompleteListener(task -> imageProxy.close());
            }
        }
    }

    private void handleScan(String text) {
        try{
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String displayText = "Parsed Serial Number: " + text;
                    parsedSerialNoText.setText(displayText);
                    scannedText = text;
                }
            });
        }
        catch (Exception e){
            Toast.makeText(SerialNoScanningActivity.this,
                    "Could not parse serial number", Toast.LENGTH_LONG).show();
        }
    }
}
