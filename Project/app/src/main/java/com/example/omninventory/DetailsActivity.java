package com.example.omninventory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Activity for viewing the contents of all of an InventoryItem's fields.
 * @author Castor
 */
public class DetailsActivity extends AppCompatActivity implements GetInventoryItemHandler, ImageDownloadHandler {

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
    private RecyclerView imageList;

    private ItemImageAdapter imageAdapter;

    /**
     * Method called on Activity creation. Contains most of the logic of this Activity; programmatically
     * modifying UI elements, creating Intents to move to other Activites, and setting up connection
     * to the database.
     * @param savedInstanceState Information about this Activity's saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // === set up database
        repo = new InventoryRepository();

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);
        final ImageButton backButton = findViewById(R.id.back_button);
        final ImageButton editButton = findViewById(R.id.edit_button);
        itemNameText = findViewById(R.id.item_name_text);
        itemDescriptionText = findViewById  (R.id.item_description_text);
        itemCommentText = findViewById(R.id.item_comment_text);
        itemMakeText = findViewById(R.id.item_make_text);
        itemModelText = findViewById(R.id.item_model_text);
        itemSerialText = findViewById(R.id.item_serial_text);
        itemValueText = findViewById(R.id.item_value_text);
        itemDateText = findViewById(R.id.item_date_text);
        itemTagsText = findViewById(R.id.item_tags_text);
        imageList = findViewById(R.id.item_images_list);

        // ============== RETRIEVE DATA ================

        if (getIntent().getExtras() != null) {
            // get InventoryItem
            if (getIntent().getExtras().getSerializable("item") == null) {
                // creating a new item; initialize with no fields
                Log.d("DetailsActivity", "DetailsActivity opened without an InventoryItem; concerning");
                currentItem = new InventoryItem();
            }
            else {
                Log.d("DetailsActivity", "DetailsActivity opened with an InventoryItem");
                currentItem = (InventoryItem) getIntent().getExtras().getSerializable("item");
            }

            // get User
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

        // ============== UI SETUP ================

        titleText.setText(getString(R.string.details_title_text)); // set title text
        setFields(); // set item fields to contain data from InventoryItem passed in

        // set up list adapter for images
        imageAdapter = new ItemImageAdapter(new ArrayList<ItemImage>());
        imageAdapter.resetData(currentItem.getImages().size());
        imageList.setAdapter(imageAdapter);
        imageList.setLayoutManager(new LinearLayoutManager(this));

        // ============== ONCLICK ACTIONS ================

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to MainActivity
                finish();
            }
        });

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
     * Set fields in this activity's layout to display the field values of the current InventoryItem.
     */
    private void setFields() {
        itemNameText.setText(currentItem.getName());
        itemDescriptionText.setText(currentItem.getDescription());
        itemCommentText.setText(currentItem.getComment());
        itemMakeText.setText(currentItem.getMake());
        itemModelText.setText(currentItem.getModel());
        itemSerialText.setText(currentItem.getSerialNo());
        itemValueText.setText(currentItem.getValue().toString()); // convert ItemValue to String
        itemDateText.setText(currentItem.getDate().toString()); // convert ItemDate to String
        itemTagsText.setText(currentItem.getTagsString());

        // attempt image download into this activity, calling this.onImageDownload on success
        repo.attemptDownloadImages(currentItem, this);
    }

    /**
     * Called when Activity is resumed from another Activity. Necessary because we use this to update
     * the fields of an item; if an item is modified in EditActivity, fields must be updated after
     * EditActivity calls finish() and DetailsActivity is resumed. This makes a call to a method in
     * InventoryRepository, which then calls DetailsActivity.onGetInventoryItem to actually
     * update the fields.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // repo and currentItem may be null if we are entering DetailActivity from MainActivity
        if (repo != null && currentItem != null) {
            // but, in case we are entering from EditActivity, we need to refresh the item fields
            // as they may have been edited
            Log.d("DetailsActivity", "refreshing currentItem");
            repo.getInventoryItemInto(currentItem.getFirebaseId(), this);
            // need to redownload images in onGetInventoryItem because size of array may change
        }
    }

    /**
     * Implement behaviour when InventoryItem is read by InventoryRepository. This is a callback to
     * receive Firebase data from repo.getInventoryItemInto(), which is called in
     * DetailsActivity.onResume().
     * TODO: come back to this later, consider making method of InventoryItem instead
     * @param item InventoryItem received.
     */
    public void onGetInventoryItem(InventoryItem item) {
        // update fields for the new item
        currentItem = item;

        // clear adapter in case we had anything in there (otherwise downloading new images will cause duplicates)
        imageAdapter.resetData(currentItem.getImages().size());

        setFields(); // finally can set fields for display (and attempt to download images)
    }

    public void onImageDownload(int pos, ItemImage image) {
        Log.d("DetailsActivity", "onImageDownload called");
        imageAdapter.set(pos, image);
    }

    public void onImageDownloadFailed(int pos) {
        // just keep trying to download the image
        Log.d("DetailsActivity", String.format("onImageDownloadFailed called for pos %d, trying again...", pos));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                repo.attemptDownloadImage(currentItem, pos, DetailsActivity.this);
            }
        }, 1500); // try again after 1.5s
    }
}
