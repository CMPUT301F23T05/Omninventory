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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
/**
 * Activity for creating a new account
 *
 * @author Rose Nguyen
 */
public class SignupActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private FirebaseFirestore db;

    /**
     * Method called on Activity creation. Contains most of the logic of this Activity; programmatically
     * modifying UI elements, creating Intents to move to other Activites, and setting up connection
     * to the database.
     * @param savedInstanceState Information about this Activity's saved state.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // set up database
        db = FirebaseFirestore.getInstance();

        // get references to button and edittext fields
        final Button signupButton = findViewById(R.id.signup_btn);
        nameEditText = findViewById(R.id.signup_name_edit_text);
        usernameEditText = findViewById(R.id.signup_username_edit_text);
        passwordEditText = findViewById(R.id.signup_password_edit_text);
        confirmPasswordEditText = findViewById(R.id.signup_confirm_password_edit_text);
        final TextView titleText = findViewById(R.id.title_text);

        // === UI setup
        // set title text
        titleText.setText(getString(R.string.signup_title_text));

        // === signup button onclick function
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                validateUserInput(name, username, password, confirmPassword, new ValidationResultCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String message, User user) {
                        // === add user to database if their input passes all validation tests
                        if (isValid) {
                            InventoryRepository repo = new InventoryRepository();
                            repo.addUser(user);
                            Log.d("SignUpActivity", user.getName());
                            // have user logged in and go back to main activity
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            intent.putExtra("login", user);
                            startActivity(intent);
                            Log.d("SignUpActivity", "Signed up successfully");
                            finish();
                        } else if (message == "emptyName") {
                            nameEditText.setError("Please fill out this field");
                        } else if (message == "emptyUsername") {
                            usernameEditText.setError("Please fill out this field");
                        } else if (message == "emptyPassword") {
                            passwordEditText.setError("Please fill out this field");
                        } else if (message == "emptyConfirmPassword") {
                            confirmPasswordEditText.setError("Please fill out this field");
                        } else if (message == "unmatchedPasswords") {
                            confirmPasswordEditText.setError("Passwords do not match");
                        } else if (message == "usernameTaken") {
                            usernameEditText.setError("Username already exists");
                        } else if (message == "weakPassword") {
                            passwordEditText.setError("Password must be between 8-20 characters, have at least one uppercase letter (A-Z), one lowercase letter (a-z), one digit (0-9), and one symbol");
                        } else if (message == "error") {
                            Toast.makeText(getApplicationContext(), "Error occurred. Please try again.", Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });
    }

    /**
     * Go to the login screen if user clicks on the login link
     */
    public void onClickLogInLink(View v) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Validate and authenticate user's signup credentials
     * @param name              Contents of 'Name' field.
     * @param username          Contents of 'Username' field.
     * @param password          Contents of 'Password' field.
     * @param confirmPassword   Contents of 'confirm password' field.
     * @param callback          A callback handler to run when validation is complete.
     */
    private void validateUserInput(String name, String username, String password, String confirmPassword, ValidationResultCallback callback) {
        // check for empty input
        if (name.isEmpty()) {
            callback.onValidationResult(false, "emptyName", null);
        }
        else if (username.isEmpty()) {
            callback.onValidationResult(false, "emptyUsername", null);
        }
        else if (password.isEmpty()) {
            callback.onValidationResult(false, "password", null);
        }
        else if (confirmPassword.isEmpty()) {
            callback.onValidationResult(false, "emptyConfirmPassword", null);
        }
//        else if (!Utils.validatePassword(password)) {
//            callback.onValidationResult(false, "weakPassword", null);
//        }
        else if (!password.equals(confirmPassword)) {
            callback.onValidationResult(false, "unmatchedPasswords", null);
        }
        else {
            Log.d("SignUpActivity", "checking if username exists");
            DocumentReference userDocRef = db.collection("users").document(username);
            userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("SignUpActivity", "username taken");
                            callback.onValidationResult(false, "usernameTaken", null);
                        }
                        else {
                            Log.d("SignUpActivity", "success");
                            callback.onValidationResult(true, "valid", new User(name, username, Utils.sha256(password), new ArrayList<String>()));
                            Log.d("SignUpActivity", name);
                        }
                    }
                    else {
                        Log.d(TAG, "Failed with: ", task.getException());
                        callback.onValidationResult(false, "error", null);
                    }
                }
            });
        }
    }
}
