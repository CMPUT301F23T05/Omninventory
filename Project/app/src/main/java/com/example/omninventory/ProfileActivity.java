package com.example.omninventory;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for displaying user's profile
 *
 * @author Rose Nguyen
 */
public class ProfileActivity extends AppCompatActivity {
    private TextView titleText;
    private TextView profileName;
    private TextView profileUsername;
    private User currentUser;
    private Dialog changeNameDialog;
    private Dialog changePasswordDialog;
    private InventoryRepository repo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // === set up database
        repo = new InventoryRepository();

        // === UI setup
        titleText = findViewById(R.id.title_text);
        titleText.setText(getString(R.string.profile_title_text)); // set title text
        changeNameDialog = new Dialog(this);
        changePasswordDialog = new Dialog(this);

        // get logged in username
        currentUser = (User) getIntent().getExtras().getSerializable("user");
        profileName = findViewById(R.id.profile_name);
        profileUsername = findViewById(R.id.profile_username);
        profileName.setText(currentUser.getName());
        profileUsername.setText("@" + currentUser.getUsername());

        // back button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView changeNameBtn = findViewById(R.id.change_name_btn);
        TextView changePasswordBtn = findViewById(R.id.change_password_btn);
        TextView logoutBtn = findViewById(R.id.logout_btn);
        changeNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeNameDialog();
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordDialog();
            }
        });

        /**
         * Logs user out when they click the logout button
         */
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("logout", "logging out");
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void changeNameDialog() {
        changeNameDialog.setCancelable(false);
        changeNameDialog.setContentView(R.layout.change_name_dialog);
        Log.d("ProfileActivity", "creating dialog for name changing");

        // UI Elements
        EditText newNameEditText = changeNameDialog.findViewById(R.id.change_name_editText);
        Button okDialogButton = changeNameDialog.findViewById(R.id.ok_dialog_button);
        Button cancelDialogButton = changeNameDialog.findViewById(R.id.cancel_dialog_button);

        // Update user's name or cancel dialog
        okDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = newNameEditText.getText().toString();
                if (newName.isEmpty()) {
                    newNameEditText.setError("Please fill out this field");
                    return;
                }
                currentUser.setName(newName);
                profileName.setText(newName);
                repo.updateUser(currentUser);
                changeNameDialog.dismiss();
            }
        });
        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNameDialog.dismiss();
            }
        });

        changeNameDialog.show();
    }

    private void changePasswordDialog() {
        changePasswordDialog.setCancelable(false);
        changePasswordDialog.setContentView(R.layout.change_password_dialog);
        Log.d("ProfileActivity", "creating dialog for password changing");

        // UI Elements
        EditText oldPasswordEditText = changePasswordDialog.findViewById(R.id.old_password_editText);
        EditText newPasswordEditText = changePasswordDialog.findViewById(R.id.new_password_editText);
        EditText confirmPasswordEditText = changePasswordDialog.findViewById(R.id.confirm_password_editText);
        Button okDialogButton = changePasswordDialog.findViewById(R.id.ok_dialog_button);
        Button cancelDialogButton = changePasswordDialog.findViewById(R.id.cancel_dialog_button);

        // Update user's password or cancel dialog
        okDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                if (oldPassword.isEmpty()) {
                    oldPasswordEditText.setError("Please fill out this field");
                    return;
                }
                if (newPassword.isEmpty()) {
                    newPasswordEditText.setError("Please fill out this field");
                    return;
                }
                if (confirmPassword.isEmpty()) {
                    confirmPasswordEditText.setError("Please fill out this field");
                    return;
                }
                if (!Utils.sha256(oldPassword).equals(currentUser.getPassword())) {
                    oldPasswordEditText.setError("Current password is incorrect");
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Passwords do not match");
                    return;
                }
                // TODO: uncomment this. Commenting this out for testing.
//                if (!Utils.validatePassword(newPassword)) {
//                    confirmPasswordEditText.setError("Password must be between 8-20 characters, have at least one uppercase letter (A-Z), one lowercase letter (a-z), one digit (0-9), and one symbol");
//                    return;
//                }
                currentUser.setPassword(Utils.sha256(newPassword));
                repo.updateUser(currentUser);
                changePasswordDialog.dismiss();
            }
        });
        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePasswordDialog.dismiss();
            }
        });

        changePasswordDialog.show();
    }
}
