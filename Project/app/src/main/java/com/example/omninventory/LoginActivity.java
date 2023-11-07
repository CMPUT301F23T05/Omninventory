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
    private ActivityResultLauncher<Intent> signupActivityResultLauncher;

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

        // === add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_main, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AtomicBoolean isValid = new AtomicBoolean(true);
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // check for empty input
                if (username.isEmpty()) {
                    isValid.set(false);
                    usernameEditText.setError("Please fill out this field");
                }
                if (password.isEmpty()) {
                    isValid.set(false);
                    passwordEditText.setError("Please fill out this field");
                }

                // === check if username is in database
                DocumentReference userDocRef = db.collection("users").document(username);
                userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                isValid.set(false);
                            }
                            else if (document.get("Password") != HashPassword.sha256(password)) {
                                isValid.set(false);
                            }
                        }
                        else {
                            Log.d(TAG, "Failed with: ", task.getException());
                        }
                    }
                });

                // log in and return to MainActivity
                if (isValid.get()) {
                    Intent data = new Intent();
                    data.putExtra(MainActivity.EXTRA_LOGIN_USERNAME, username);
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });
    }
    private void startSignupActivity() {
        signupActivityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Get the username from SignupActivity
                        String username = result.getData().getStringExtra(MainActivity.EXTRA_LOGIN_USERNAME);
                        Intent data = new Intent();
                        data.putExtra(MainActivity.EXTRA_LOGIN_USERNAME, username);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
        Intent intent = new Intent(this, SignupActivity.class);
        signupActivityResultLauncher.launch(intent);
    }
}
