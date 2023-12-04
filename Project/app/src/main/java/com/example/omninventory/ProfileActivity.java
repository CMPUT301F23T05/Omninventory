package com.example.omninventory;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
public class ProfileActivity extends AppCompatActivity implements UpdateUsernameHandler {
    private TextView titleText;
    private TextView profileName;
    private TextView profileUsername;
    private User currentUser;
    private Dialog changeUsernameDialog;
    private Dialog changeNameDialog;
    private Dialog changePasswordDialog;
    private InventoryRepository repo;
    int canUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // === set up database
        repo = new InventoryRepository();

        // === UI setup
        titleText = findViewById(R.id.title_text);
        titleText.setText(getString(R.string.profile_title_text)); // set title text
        changeUsernameDialog = new Dialog(this);
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
                Intent userIntent = new Intent();
                userIntent.putExtra("user", currentUser);
                setResult(Activity.RESULT_OK, userIntent);
                finish();
            }
        });

        TextView changeUsernameBtn = findViewById(R.id.change_username_btn);
        TextView changeNameBtn = findViewById(R.id.change_name_btn);
        TextView changePasswordBtn = findViewById(R.id.change_password_btn);
        TextView logoutBtn = findViewById(R.id.logout_btn);

        changeUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsernameDialog();
            }
        });

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

    private void changeUsernameDialog() {
        changeUsernameDialog.setCancelable(false);
        changeUsernameDialog.setContentView(R.layout.change_name_dialog);
        Log.d("ProfileActivity", "creating dialog for changing username");

        // UI Elements
        TextView dialogTitle = changeUsernameDialog.findViewById(R.id.change_name_title);
        EditText newUsernameEditText = changeUsernameDialog.findViewById(R.id.change_name_editText);
        Button okDialogButton = changeUsernameDialog.findViewById(R.id.ok_dialog_button);
        Button cancelDialogButton = changeUsernameDialog.findViewById(R.id.cancel_dialog_button);

        dialogTitle.setText("CHANGE USERNAME");
        // Update user's username or cancel dialog
        okDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = newUsernameEditText.getText().toString();
                if (newName.isEmpty()) {
                    newUsernameEditText.setError("Please fill out this field");
                    return;
                }
                canUpdateUsername(newName);
            }
        });
        cancelDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUsernameDialog.dismiss();
            }
        });

        changeUsernameDialog.show();
    }

    private void changeNameDialog() {
        changeNameDialog.setCancelable(false);
        changeNameDialog.setContentView(R.layout.change_name_dialog);
        Log.d("ProfileActivity", "creating dialog for name changing");

        // UI Elements
        EditText newNameEditText = changeNameDialog.findViewById(R.id.change_name_editText);
        Button okDialogButton = changeNameDialog.findViewById(R.id.ok_dialog_button);
        Button cancelDialogButton = changeNameDialog.findViewById(R.id.cancel_dialog_button);

        // make background transparent for our rounded corners
        changeNameDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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

        // make background transparent for our rounded corners
        changePasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

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
                if (!Utils.validatePassword(newPassword)) {
                    confirmPasswordEditText.setError("Password must be between 8-20 characters, have at least one uppercase letter (A-Z), one lowercase letter (a-z), one digit (0-9), and one symbol");
                    return;
                }
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

    private void canUpdateUsername(String username) {
        repo.isUsernameUnique(username, this);
    }

    public void onUsernameValidation(boolean isUnique, boolean isError, String newUsername) {
        if (isError) {
            Log.d("onUsernameValidation", "error");
            canUpdate = -1; // error occurred
        }
        else if (!isUnique) {
            Log.d("onUsernameValidation", "cant update");
            canUpdate = 1; // username is not unique, cannot update
        }
        else {
            Log.d("onUsernameValidation", "can update");
            canUpdate = 0; // user is  available, can update
        }
        EditText newUsernameEditText = changeUsernameDialog.findViewById(R.id.change_name_editText);
        if (canUpdate == 1) {
            newUsernameEditText.setError("Username already exists");
            return;
        }
        if (canUpdate == -1) {
            newUsernameEditText.setError("An error occurred. Please try again");
            return;
        }
        String oldUsername = currentUser.getUsername();
        currentUser.setUsername(newUsername);
        profileUsername.setText("@" + currentUser.getUsername());
        repo.updateUsername(currentUser, oldUsername);
        changeUsernameDialog.dismiss();
    }
}
