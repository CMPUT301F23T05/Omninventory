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

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
/**
 * Activity for logging into the app
 *
 * @author Rose Nguyen
 */
public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private FirebaseFirestore db;
    public InventoryRepository repo;

    /**
     * Method called on Activity creation. Contains most of the logic of this Activity; programmatically
     * modifying UI elements, creating Intents to move to other Activites, and setting up connection
     * to the database.
     * @param savedInstanceState Information about this Activity's saved state.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // === set up database
        db = FirebaseFirestore.getInstance();
        repo = new InventoryRepository();

        // === get references to button and edittext fields
        final Button loginButton = findViewById(R.id.login_btn);
        usernameEditText = findViewById(R.id.login_username_edit_text);
        passwordEditText = findViewById(R.id.login_password_edit_text);
        final TextView titleText = findViewById(R.id.title_text);

        // === UI setup
        // set title text
        titleText.setText(getString(R.string.login_title_text));
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                validateUserInput(username, password, new ValidationResultCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String message, User user) {
                        if (isValid) {
                            // User is valid, log in and return to MainActivity
                            Log.d("login", "logging in");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("login", user);
                            Log.d("login", "starting main: " + user.getName());
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
    /**
     * Go to the signup screen if user clicks on the signup link
     */
    public void onClickSignUpLink(View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Convert a document from the "users" collection in the database to a User object
     */
    public User convertDocumentToUser(DocumentSnapshot doc) {
        Log.d("Login", "(convertDocumentToUser) converting to User");
        User user = new User(
                doc.getString("name"),
                doc.getId(),
                doc.getString("password"),
                (ArrayList<String>) doc.get("ownedItems")
        );
        Log.d("Login", "(convertDocumentToUser) done, name: " + user.getUsername());
        return user;
    }

    /**
     * Validates the data in input fields on the login screen. Requires querying the database and
     * returning validation result asynchronously through a callback, using the
     * ValidationResultCallback interface.
     * @param username The contents of the Username field.
     * @param password The contents of the Password field.
     * @param callback A callback handler to run when validation is complete.
     */
    private void validateUserInput(String username, String password, ValidationResultCallback callback) {
        // checks for empty fields
        if (username.isEmpty()) {
            usernameEditText.setError("Please fill out this field");
            callback.onValidationResult(false, "emptyUsername", null);
        }
        else if (password.isEmpty()) {
            passwordEditText.setError("Please fill out this field");
            callback.onValidationResult(false, "emptyPassword", null);
        }
        else {
            // authenticate username and password against database
            DocumentReference userDocRef = db.collection("users").document(username);
            Log.d("Login", "id=" + userDocRef.getPath());
            userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (!document.exists()) {
                            callback.onValidationResult(false, "invalidInput", null);
                            Log.d(TAG, "invalid username", task.getException());
                        } else if (!document.get("password").equals(Utils.sha256(password))) {
                            callback.onValidationResult(false, "invalidInput", null);
                            Log.d(TAG, "invalid password", task.getException());
                        } else {
                            User user = repo.convertDocumentToUser(document);
                            callback.onValidationResult(true, "valid", user);
                        }
                    } else {
                        Log.d(TAG, "Failed with: ", task.getException());
                        callback.onValidationResult(false, "error", null);
                    }
                }
            });
        }
    }
}
