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

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for viewing an item's detailed fields.
 * Will reuse (or extend?) with different behaviour for both viewing & editing items.
 */
public class DetailsActivity extends AppCompatActivity implements GetInventoryItemHandler {

    private InventoryRepository repo;
    private InventoryItem currentItem;

    TextView itemNameText;
    TextView itemDescriptionText;
    TextView itemCommentText;
    TextView itemMakeText;
    TextView itemModelText;
    TextView itemSerialText;
    TextView itemValueText;
    TextView itemDateText;
    TextView itemTagsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // === set up database
        repo = new InventoryRepository();

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);
        itemNameText = findViewById(R.id.item_name_text);
        itemDescriptionText = findViewById(R.id.item_description_text);
        itemCommentText = findViewById(R.id.item_comment_text);
        itemMakeText = findViewById(R.id.item_make_text);
        itemModelText = findViewById(R.id.item_model_text);
        itemSerialText = findViewById(R.id.item_serial_text);
        itemValueText = findViewById(R.id.item_value_text);
        itemDateText = findViewById(R.id.item_date_text);
        itemTagsText = findViewById(R.id.item_tags_text);

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
        // set item fields
        setFields(currentItem);

        // set title text
        titleText.setText(getString(R.string.details_title_text));

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
                startActivity(intent); // use startActivityForResult so that onActivityResullt is called
            }
        });
    }

    /**
     * Set fields in this activity's layout to the field values of an InventoryItem.
     * @param item
     */
    private void setFields(InventoryItem item) {
        // add item details to each field
        itemNameText.setText(item.getName());
        itemDescriptionText.setText(item.getDescription());
        itemCommentText.setText(item.getComment());
        itemMakeText.setText(item.getMake());
        itemModelText.setText(item.getModel());
        itemSerialText.setText(item.getSerialno());
        itemValueText.setText(item.getValue().toString());
        itemDateText.setText(item.getDate().toString());
        itemTagsText.setText(item.getTagsString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("DetailsActivity", "onResume called");

        // repo and currentItem may be null if we are entering DetailActivity from MainActivity,
        // as opposed to returning from EditActivity
        if (repo != null && currentItem != null) {
            Log.i("DetailsActivity", "refreshing currentItem");
            repo.getInventoryItemInto(currentItem.getFirebaseId(), this); // refresh our item
        }
    }

    /**
     * Implement behaviour when InventoryItem is read.
     * I'm not sold that this is the best way to do this (update the fields in this Activity on return
     * from EditActivity, I'll come back to it later.
     * TODO: come back to this later
     * @param item
     */
    public void onGetInventoryItem(InventoryItem item) {
        // refresh fields with the updated item
        setFields(item);
    }
}
