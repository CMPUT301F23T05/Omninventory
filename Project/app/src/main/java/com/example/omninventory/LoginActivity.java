package com.example.omninventory;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicBoolean;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private FirebaseFirestore db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // === set up database
        db = FirebaseFirestore.getInstance();

        // === get references to button and edittext fields
        final Button loginButton = findViewById(R.id.login_btn);
        usernameEditText = findViewById(R.id.login_username_edit_text);
        passwordEditText = findViewById(R.id.login_password_edit_text);
        final TextView titleText = findViewById(R.id.title_text);

        // === UI setup
        // set title text
        titleText.setText(getString(R.string.login_title_text));

        // === todo: may move this somewhere later
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                validateUserInput(username, password, new ValidationResultCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String message) {
                        if (isValid) {
                            // User is valid, log in and return to MainActivity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("loggedInUser", username);
                            startActivity(intent);
                            finish();
                        } else if (message == "invalidInput") {
                            passwordEditText.setError("Incorrect username or password");
                        } else if (message == "emptyPassword") {
                            passwordEditText.setError("Please fill out this field");
                        } else if (message == "emptyPassword") {
                            usernameEditText.setError("Please fill out this field");
                        } else if (message == "error") {
                            Toast.makeText(getApplicationContext(), "Error occurred. Please try again.", Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });
    }
    public void onClickSignUpLink(View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void validateUserInput(String username, String password, ValidationResultCallback callback) {
        // checks for empty fields
        if (username.isEmpty()) {
            usernameEditText.setError("Please fill out this field");
            callback.onValidationResult(false, "emptyUsername");
        }
        else if (password.isEmpty()) {
            passwordEditText.setError("Please fill out this field");
            callback.onValidationResult(false, "emptyPassword");
        }
        else {
            // authenticate username and password against database
            DocumentReference userDocRef = db.collection("users").document(username);
            userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            callback.onValidationResult(false, "invalidInput");
                            Log.d(TAG, "invalid username", task.getException());
                        } else if (!document.get("password").equals(Utils.sha256(password))) {
                            callback.onValidationResult(false, "invalidInput");
                            Log.d(TAG, "invalid password", task.getException());
                        } else {
                            callback.onValidationResult(true, "valid");
                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.getException());
                        callback.onValidationResult(false, "error");
                    }
                }
            });
        }
    }
}
