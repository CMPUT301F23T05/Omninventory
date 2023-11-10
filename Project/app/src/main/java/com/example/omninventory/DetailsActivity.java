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
 * Activity for viewing an item's fields in detail.
 */
public class DetailsActivity extends AppCompatActivity implements GetInventoryItemHandler {

    private InventoryRepository repo;
    private InventoryItem currentItem;
    private User currentUser;

    private TextView itemNameText;
    private TextView itemDescriptionText;
    private TextView itemCommentText;
    private TextView itemMakeText;
    private TextView itemModelText;
    private TextView itemSerialText;
    private TextView itemValueText;
    private TextView itemDateText;
    private TextView itemTagsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // === set up database
        repo = new InventoryRepository();

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);
        itemNameText = findViewById(R.id.item_name_text);
        itemDescriptionText = findViewById  (R.id.item_description_text);
        itemCommentText = findViewById(R.id.item_comment_text);
        itemMakeText = findViewById(R.id.item_make_text);
        itemModelText = findViewById(R.id.item_model_text);
        itemSerialText = findViewById(R.id.item_serial_text);
        itemValueText = findViewById(R.id.item_value_text);
        itemDateText = findViewById(R.id.item_date_text);
        itemTagsText = findViewById(R.id.item_tags_text);

        // === load info passed from MainActivity (hopefully)
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getSerializable("item") == null) {
                // creating a new item; initialize with no fields
                Log.d("DetailsActivity", "DetailsActivity opened without an InventoryItem; concerning");
                currentItem = new InventoryItem();
            }
            else {
                Log.d("DetailsActivity", "DetailsActivity opened with an InventoryItem");
                currentItem = (InventoryItem) getIntent().getExtras().getSerializable("item");
            }

            if (getIntent().getExtras().getSerializable("user") == null) {
                Log.d("DetailsActivity", "DetailsActivity opened without a User; concerning");
            }
            else {
                currentUser = (User) getIntent().getExtras().getSerializable("user");
            }
        }
        else {
            // this shouldn't happen
            throw new RuntimeException("DetailsActivity opened without any extra data");
        }

        // === UI setup
        titleText.setText(getString(R.string.details_title_text)); // set title text
        setFields(currentItem); // set item fields

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
                intent.putExtra("item", currentItem); // pass item data to EditActivity
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
        });
    }

    /**
     * Set fields in this activity's layout to display the field values of an InventoryItem.
     * @param item The InventoryItem to display.
     */
    private void setFields(InventoryItem item) {
        itemNameText.setText(item.getName());
        itemDescriptionText.setText(item.getDescription());
        itemCommentText.setText(item.getComment());
        itemMakeText.setText(item.getMake());
        itemModelText.setText(item.getModel());
        itemSerialText.setText(item.getSerialNo());
        itemValueText.setText(item.getValue().toString()); // convert ItemValue to String
        itemDateText.setText(item.getDate().toString()); // convert ItemDate to String
        itemTagsText.setText(item.getTagsString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DetailsActivity", "onResume called");

        // repo and currentItem may be null if we are entering DetailActivity from MainActivity
        if (repo != null && currentItem != null) {
            // but, in case we are entering from EditActivity, we need to refresh the item fields
            // as they may have been edited
            Log.d("DetailsActivity", "refreshing currentItem");
            repo.getInventoryItemInto(currentItem.getFirebaseId(), this);
        }
    }

    /**
     * Implement behaviour when InventoryItem is read by InventoryRepository. This is a callback to
     * receive Firebase data from repo.getInventoryItemInto when onResume is called. It is
     * necessary because if you go to EditActivity, edit item fields, and then save and return to
     * DetailsActivity, the item fields will still display as they were before the edit if not
     * refreshed somehow.
     * TODO: come back to this later, consider making method of InventoryItem instead
     * @param item InventoryItem received
     */
    public void onGetInventoryItem(InventoryItem item) {
        // update fields for the new item
        currentItem = item;
        setFields(currentItem);
    }
}
