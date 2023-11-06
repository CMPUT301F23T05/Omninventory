package com.example.omninventory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for viewing an item's detailed fields.
 * Will reuse (or extend?) with different behaviour for both viewing & editing items.
 */
public class DetailsActivity extends AppCompatActivity  {

    private InventoryRepository repo;
    private InventoryItem currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // === set up database
        repo = new InventoryRepository();

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);
        final TextView itemNameText = findViewById(R.id.item_name_text);
        final TextView itemDescriptionText = findViewById(R.id.item_description_text);
        final TextView itemCommentText = findViewById(R.id.item_comment_text);
        final TextView itemMakeText = findViewById(R.id.item_make_text);
        final TextView itemModelText = findViewById(R.id.item_model_text);
        final TextView itemSerialText = findViewById(R.id.item_serial_text);
        final TextView itemValueText = findViewById(R.id.item_value_text);
        final TextView itemDateText = findViewById(R.id.item_date_text);
        final TextView itemTagsText = findViewById(R.id.item_tags_text);

        // === load info passed from MainActivity (hopefully)
        if (savedInstanceState != null) {
            // TODO: this will probably be used later when we go from this activity to others; for now, error
            throw new RuntimeException("DetailsActivity opened with a savedInstanceState");

        }
        if (getIntent().getExtras() == null) {
            throw new RuntimeException("DetailsActivity opened without an InventoryItem");
        }
        else {
            currentItem = (InventoryItem) getIntent().getExtras().getSerializable("item");
        }

        // === UI setup

        // set title text
        titleText.setText(getString(R.string.details_title_text));

        // add item details to each field
        itemNameText.setText(currentItem.getName());
        itemDescriptionText.setText(currentItem.getDescription());
        itemCommentText.setText(currentItem.getComment());
        itemMakeText.setText(currentItem.getMake());
        itemModelText.setText(currentItem.getModel());
        itemSerialText.setText(currentItem.getSerialno());
        itemValueText.setText(currentItem.getValue().toString());
        itemDateText.setText(currentItem.getDate().toString());
        itemTagsText.setText(currentItem.getTagsString());

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_details, null);
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

        final ImageButton editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // start a new EditActivity to edit this item
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.putExtra("item", currentItem);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("EditActivity", "onActivityResult called with resultCode " + resultCode);

        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Edit was successful.", Toast.LENGTH_SHORT).show();
        } else if ( resultCode == RESULT_CANCELED ) {
            Toast.makeText(this, "There was an error saving your edits.", Toast.LENGTH_SHORT).show();
        }

        // either way, refresh our item
        currentItem = repo.refreshInventoryItem(currentItem);
    }
}
