package com.example.omninventory;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for editing an item's detailed fields.
 */
public class EditActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);
        final TextView itemNameEditText = findViewById(R.id.item_name_edittext);

        // === load info passed from MainActivity (hopefully)
        InventoryItem item;
        if (savedInstanceState != null) {
            // TODO: this will probably be used later when we go from this activity to others; for now, error
            throw new RuntimeException("EditActivity opened with a savedInstanceState");
        }
        else if (getIntent().getExtras() == null) {
            throw new RuntimeException("EditActivity opened without an InventoryItem");
        }
        else {
            item = (InventoryItem) getIntent().getExtras().getSerializable("item");
        }

        // === UI setup

        // set title text
        titleText.setText(getString(R.string.edit_title_text));

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_edit, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

        // if initialized with an item, set default values for fields
        itemNameEditText.setText(item.getName());

        // === set up click actions
        final ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to DetailsActivity without changing any item fields
                finish();
            }
        });

        final ImageButton saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: implement this
                // return to DetailsActivity and save changes to item fields
                finish();
            }
        });
    }
}
