package com.example.omninventory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for viewing an item's detailed fields.
 * Will reuse (or extend?) with different behaviour for both viewing & editing items.
 */
public class DetailsActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);

        // === UI setup
        titleText.setText(getString(R.string.details_title_text));

        // === set up click actions
        final ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to MainActivity
                finish();
            }
        });
    }
}
