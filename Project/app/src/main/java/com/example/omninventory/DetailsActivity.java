package com.example.omninventory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.security.InvalidParameterException;

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
        final TextView itemNameText = findViewById(R.id.item_name_text);
        final TextView itemDescriptionText = findViewById(R.id.item_description_text);

        // === load info passed from MainActivity (hopefully)
        InventoryItem item;
        if (savedInstanceState != null) {
            // TODO: this will probably be used later when we go from this activity to others; for now, error
            throw new RuntimeException("DetailsActivity opened with a savedInstanceState");
        }
        else if (getIntent().getExtras() == null) {
            throw new RuntimeException("DetailsActivity opened without an InventoryItem");
        }
        else {
            item = (InventoryItem) getIntent().getExtras().getSerializable("item");
        }

        // === UI setup

        // set title text
        titleText.setText(getString(R.string.details_title_text));

        // add item details
        itemNameText.setText(item.getName());
        itemDescriptionText.setText(item.getDescription());

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.view_item_taskbar, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

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
