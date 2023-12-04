package com.example.omninventory;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
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
 * Activity for scanning serial numbers from an image taken from the camera. Scanning is done
 * using Google's computer vision "mlkit" package (https://developers.google.com/ml-kit/vision/text-recognition/v2).
 *
 * @author Zachary
 */
public class SerialNoScanningActivity extends AppCompatActivity {
    private ExecutorService cameraExecutor;
    private TextView parsedSerialNoText;
    private String scannedText;
    private View scanningBox;
    private PreviewView previewView;
    private Button confirmButton;

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
     * modifying UI elements, creating Intents to move back to MainActivity, connecting to the
     * camera and setting an image analyzer.
     * @param savedInstanceState Information about this Activity's saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_no_scanning);

        previewView = findViewById(R.id.preview_view);

        // Dynamically request camera permissions
        checkCameraPermissions(this);
        cameraExecutor = Executors.newSingleThreadExecutor();

        Button cancelButton = findViewById(R.id.serialno_cancel_button);
        // Back button that takes user back to EditActivity if no serial number is scanned
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        // confirm button sends scanned serial number back to EditActivity
        confirmButton = findViewById(R.id.serialno_confirm_button);
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
        scanningBox = findViewById(R.id.center_box);

        startCamera(previewView);
    }

    /**
     * Connects to the phone's camera and displays output from the camera to the screen's PreviewView.
     * Images from the camera's feed are sent to the analyzer periodically to do the scanning.
     * @param previewView Element on the screen that the camera's video feed is displayed to.
     */
    private void startCamera(PreviewView previewView) {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();

                // connect camera feed to scanner (TextAnalyzer.analyze)
                imageAnalysis.setAnalyzer(cameraExecutor, new TextAnalyzer());
                cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis);

                // send camera feed to PreviewView element
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * Inner class that implements the mlkit Analyzer interface. Images from the camera's feed are
     * periodically sent to the analyze method where they are interpreted and scanned for
     * serial numbers.
     */
    private class TextAnalyzer implements ImageAnalysis.Analyzer {
        private final TextRecognizer textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        /**
         * Images from the camera's feed are periodically sent here to be scanned for serial numbers.
         * Images are automatically scanned for text. If the center of the text is inside the
         * bouding box and is a valid potential serial number, it is accepted as one.
         * @param imageProxy The image to analyze
         */
        @OptIn(markerClass = ExperimentalGetImage.class) @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            @SuppressLint("UnsafeExperimentalUsageError") Image mediaImage = imageProxy.getImage();
            if (mediaImage != null) {
                InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                textRecognizer.process(image)
                        .addOnSuccessListener(result -> {
                            // text is found in the image. For every line of text
                            // in the bounding box, pass it to handleScan()
                            List<Text.TextBlock> blocks = result.getTextBlocks();
                            for (Text.TextBlock block : blocks) {
                                if (textInBox(block.getBoundingBox(), image.getWidth(), image.getHeight())) {
                                    for (String line : block.getText().split("\n")) {
                                        handleScan(line);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            parsedSerialNoText.setText("N/A");
                        })
                        .addOnCompleteListener(task -> imageProxy.close());
            }
        }
    }

    /**
     * Check if the scanned text is a valid potential serial number and pass it to the intent
     * that will be sent back to EditActivity
     * @param text Scanned text from the camera
     */
    private void handleScan(String text) {
        text = text.replace(" ", "");
        // serial numbers are alphanumeric and at least 6 characters long
        if (text.length() >= 6 && text.matches("[a-zA-Z0-9]*")) {
            try {
                Handler handler = new Handler(Looper.getMainLooper());
                String finalText = text;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // serial number is valid. Add it to the intent and make the confirm button visible
                        String displayText = "Parsed Serial Number: " + finalText;
                        parsedSerialNoText.setText(displayText);
                        scannedText = finalText;
                        confirmButton.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(SerialNoScanningActivity.this,
                        "Could not parse serial number", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Check whether a scanned text element's center point is contained in the bounding box.
     * @param textBounds Rectangle that represents the bounds of the scanned text element
     * @param imageWidth Width of the image in pixels
     * @param imageHeight Height of the image in pixels
     * @return True if the text element is in the bounding box, false otherwise.
     */
    private boolean textInBox(Rect textBounds, float imageWidth, float imageHeight) {
        // need to be operating on the same scale. textbounds is based on image's scale,
        // scanningBox is based on phone's scale.
        float widthScale = imageWidth/previewView.getWidth();
        float heightScale = imageHeight/previewView.getHeight();

        int verticalCenter = (textBounds.left + textBounds.right)/2;
        int horizontalCenter = (textBounds.bottom + textBounds.top)/2;

        float scanningBoxLeft = scanningBox.getLeft() * widthScale;
        float scanningBoxRight = scanningBox.getRight() * widthScale;
        float scanningBoxTop = scanningBox.getTop() * heightScale;
        float scanningBoxBottom = scanningBox.getBottom() * heightScale;

        return verticalCenter > scanningBoxLeft && verticalCenter < scanningBoxRight
                && horizontalCenter < scanningBoxBottom && horizontalCenter > scanningBoxTop;
    }
}
