package com.example.omninventory;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // === UI setup
        titleText = findViewById(R.id.title_text);
        titleText.setText(getString(R.string.profile_title_text)); // set title text

        // get logged in username
        currentUser = (User) getIntent().getExtras().getSerializable("loggedInUser");
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
    }

    /**
     * Logs user out when they click the logout button
     */
     public void onClickLogOutButton(View v) {
         Log.d("logout", "logging out");
         Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
         startActivity(intent);
    }
}
