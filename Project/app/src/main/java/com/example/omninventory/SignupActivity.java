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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class SignupActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private FirebaseFirestore db;
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

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_main, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AtomicBoolean isValid = new AtomicBoolean(true);
                String name = nameEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();

                // check for empty input
                if (name.isEmpty()) {
                    isValid.set(false);
                    nameEditText.setError("Please fill out this field");
                }
                if (username.isEmpty()) {
                    isValid.set(false);
                    usernameEditText.setError("Please fill out this field");
                }
                if (password.isEmpty()) {
                    isValid.set(false);
                    passwordEditText.setError("Please fill out this field");
                }
                if (confirmPassword.isEmpty()) {
                    isValid.set(false);
                    confirmPasswordEditText.setError("Please fill out this field");
                }

                // === check if username is unique
                DocumentReference userDocRef = db.collection("users").document(name);
                userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                usernameEditText.setError("Username already exists");
                                isValid.set(false);
                            }
                        }
                        else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });

                // TODO: more input validation: password, confirmPassword

                // === add user to database if their input passes all validation tests
                if (isValid.get()) {
                    String hashedPass = HashPassword.sha256(password);
                    InventoryRepository repo = new InventoryRepository();
                    repo.addUser(new User(username, hashedPass, new ArrayList<String>()));
                    // have user logged in and go back to main activity
                    Intent data = new Intent();
                    data.putExtra(MainActivity.EXTRA_LOGIN_USERNAME, username);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
    }
}
