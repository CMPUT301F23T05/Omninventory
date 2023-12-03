package com.example.omninventory;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Activity for editing an item's fields. It has a flag which determines whether we treat the item
 * as an existing item or a new item (changes how we call database methods).
 *
 * The changing functionality could have been implemented with inheritance, but there would be a large
 * amount of functionality that would have to be implemented as overridable methods; in this case
 * it is much more simple and understandable to implement with a simple toggle flag.
 *
 * @author Castor
 */
public class EditActivity extends AppCompatActivity implements ImageDownloadHandler {

    private InventoryRepository repo;

    private InventoryItem currentItem;
    private User currentUser;

    // references to Views put here because they are used in multiple methods
    private TextInputEditText itemNameEditText;
    private TextInputEditText itemDescriptionEditText;
    private TextInputEditText itemCommentEditText;
    private TextInputEditText itemMakeEditText;
    private TextInputEditText itemModelEditText;
    private TextInputEditText itemSerialEditText;
    private TextInputEditText itemValueEditText;
    private TextView itemDateText;
    private TextView itemTagsText;
    private RecyclerView imageList;

    private EditableItemImageAdapter imageAdapter;

    // ActivityResultLauncher to launch the ApplyTagsActivity for result, to define tags for the edited item
    ActivityResultLauncher<Intent> mDefineTags = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                // Get the tagged item back from ApplyTagsActivity and repopulate fields
                currentItem = (InventoryItem) result.getData().getExtras().get("taggedItem");
                setFields(currentItem);
            }
        });

    // ActivityResultLauncher to launch the BarcodeActivity for results getting product information from the scanned barcode
    ActivityResultLauncher<Intent> barcodeActivityLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Retrieve product information
                Intent data = result.getData();
                if (data != null) {
                    String productDescription = data.getStringExtra("productDescription");
                    String productPrice = data.getStringExtra("productPrice");
                    itemDescriptionEditText.setText(productDescription);
                    itemValueEditText.setText(productPrice);
                }
            }
        }
    });

    /**
     * Method called on Activity creation. Contains most of the logic of this Activity; programmatically
     * modifying UI elements, creating Intents to move to other Activites, and setting up connection
     * to the database.
     * @param savedInstanceState Information about this Activity's saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // === set up database
        repo = new InventoryRepository();

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);
        itemDateText = findViewById(R.id.item_date_text);
        itemTagsText = findViewById(R.id.item_tags_text);

        itemNameEditText = findViewById(R.id.item_name_edittext);
        itemDescriptionEditText = findViewById(R.id.item_description_edittext);
        itemCommentEditText = findViewById(R.id.item_comment_edittext);
        itemMakeEditText = findViewById(R.id.item_make_edittext);
        itemModelEditText = findViewById(R.id.item_model_edittext);
        itemSerialEditText = findViewById(R.id.item_serial_edittext);
        itemValueEditText = findViewById(R.id.item_value_edittext);
        imageList = findViewById(R.id.item_images_list);

        final ImageButton backButton = findViewById(R.id.back_button);
        final ImageButton itemDateButton = findViewById(R.id.item_date_button);
        final ImageButton saveButton = findViewById(R.id.save_button);
        final ImageButton descriptionCameraButton = findViewById(R.id.description_camera_button);
        final ImageButton imageTakeButton = findViewById(R.id.image_take_button);
        final ImageButton imageUploadButton = findViewById(R.id.image_upload_button);

        // ============== RETRIEVE DATA ================

        final boolean newItemFlag; // true if we are creating a new item, false if editing existing item

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getSerializable("item") == null) {
                // creating a new item; initialize with no fields
                Log.d("EditActivity", "EditActivity opened without an InventoryItem");
                currentItem = new InventoryItem();
                newItemFlag = true;
            }
            else {
                Log.d("EditActivity", "EditActivity opened with an InventoryItem");
                currentItem = (InventoryItem) getIntent().getExtras().getSerializable("item");
                newItemFlag = false;
            }

            if (getIntent().getExtras().getSerializable("user") == null) {
                Log.d("EditActivity", "EditActivity opened without a User; possibly concerning");
            }
            else {
                currentUser = (User) getIntent().getExtras().getSerializable("user");
            }
        }
        else {
            // this shouldn't happen
            throw new RuntimeException("EditActivity opened without any extra data");
        }

        // ============== UI SETUP ================

        // set title text
        if (newItemFlag) {
            titleText.setText(getString(R.string.add_title_text));
        }
        else {
            titleText.setText(getString(R.string.edit_title_text));
        }

        // set default values for fields
        Log.d("EditActivity", "setFields called from onCreate");
        setFields(currentItem);

        // set up TextWatchers for dynamic input formatting
        Log.d("EditActivity", "set up TextWatcher");
        // initial text modified by TextWatcher will be from currentItem, as EditText contents were just set by setFields
        itemValueEditText.addTextChangedListener(new ValueTextWatcher(itemValueEditText));

        // set up list adapter for images
        imageAdapter = new EditableItemImageAdapter(new ArrayList<ItemImage>());
        imageAdapter.resetData(currentItem.getImages().size());
        imageList.setAdapter(imageAdapter);
        imageList.setLayoutManager(new LinearLayoutManager(this));

        // ============== CLICK ACTIONS ================

        // back button should take us back to DetailsActivity without saving any item data on click
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // display an exit message
                CharSequence toastText;
                if (newItemFlag) {
                    toastText = "Item was discarded.";
                }
                else {
                    toastText = "Edits were discarded.";
                }
                Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                toast.show();

                // return without changing any item fields
                finish();
            }
        });

        // tagButton should launch the ApplyTagsActivity for result
        final ImageButton tagButton = findViewById(R.id.item_tags_button);
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create an item from the current fields without data validation (just to repopulate them after)
                currentItem = makeInventoryItem();
                Intent applyTagsIntent = new Intent(EditActivity.this, ApplyTagsActivity.class);
                ArrayList<InventoryItem> itemList = new ArrayList<>();
                itemList.add(currentItem);

                // pass the current item to ApplyTagsActivity in "return" mode
                // this makes sure we get it back instead of writing it to the db
                applyTagsIntent.putExtra("selectedItems", itemList);
                applyTagsIntent.putExtra("action", "return");
                mDefineTags.launch(applyTagsIntent);
            }
        });

        // descriptionCameraButton takes user to Camera to scan product barcode
        descriptionCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent barcodeIntent = new Intent(EditActivity.this, BarcodeActivity.class);
                barcodeActivityLauncher.launch(barcodeIntent);
            }
        });

        // itemDateButton should open a DatePickerDialog to choose date
        itemDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get InventoryItem's current date for default calendar value
                final Calendar c = Calendar.getInstance();
                int currentYear = currentItem.getDate().toCalendar().get(Calendar.YEAR);
                int currentMonth = currentItem.getDate().toCalendar().get(Calendar.MONTH);
                int currentDay = currentItem.getDate().toCalendar().get(Calendar.DAY_OF_MONTH);

                // create a calendar dialog to get date input
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EditActivity.this, // pass context
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // set formatted date in itemDateText TextView
                            // use monthOfYear + 1 because Calendar zero-indexes month
                            itemDateText.setText(ItemDate.ymdToString(year, monthOfYear + 1, dayOfMonth));
                        }
                    },
                    currentYear, currentMonth, currentDay
                );

                // display dialog
                datePickerDialog.show();
            }
        });

        // imageTakeButton takes user to Camera to take a photo
        imageTakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO
            }
        });

        // Create a photo picker activity launcher to get an image and then store it in the current InventoryItem.
        // See https://developer.android.com/training/data-storage/shared/photopicker
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // callback when photo chosen or user closes menu
                if (uri != null) {
                    Log.d("EditActivity", "PhotoPicker, user selected photo URI: " + uri);
                    imageAdapter.add(new ItemImage(uri)); // add the uri to the current images, has no path yet and a local URI
                } else {
                    Log.d("EditActivity", "PhotoPicker, user closed menu with no photo selected");
                }
            });

        // imageUploadButton takes user to gallery to upload a photo
        imageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            }
        });

        // saveButton should send data to Firebase and return to DetailsActivity
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (validateFields()) {
                    Log.d("EditActivity", "validation success, updating database");
                    InventoryItem newItem = makeInventoryItem();
                    CharSequence toastText;

                    if (newItemFlag) {
                        repo.addInventoryItem(currentUser, newItem);
                        toastText = String.format("%s was added.", newItem.getName());
                    } else {
                        repo.updateInventoryItem(newItem); // save changes to item fields
                        toastText = String.format("%s was edited.", newItem.getName());
                    }

                    // display success message
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();

                    finish(); // return after changing item fields
                }
                else {
                    // prompt user to fix input issues
                    CharSequence toastText = "Please fix invalid input fields.";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    /**
     * Set fields in this activity's layout to the field values of an InventoryItem.
     * @param item The InventoryItem to display.
     */
    private void setFields(InventoryItem item) {
        // add item details to each field
        itemNameEditText.setText(item.getName());
        itemDescriptionEditText.setText(item.getDescription());
        itemCommentEditText.setText(item.getComment());
        itemMakeEditText.setText(item.getMake());
        itemModelEditText.setText(item.getModel());
        itemSerialEditText.setText(item.getSerialNo());
        itemValueEditText.setText(item.getValue().toString()); // convert ItemValue to String
        itemDateText.setText(item.getDate().toString()); // convert ItemDate to String. note this is a TextView, not EditText
        itemTagsText.setText(item.getTagsString());

//        imageListData = currentItem.getImages(); // ensure this item's images are displayed
        repo.attemptDownloadImages(item, this); // attempt image download into this activity, calling this.onImageDownload on success
    }

    /**
     * Handles input validation for fields on this screen. Currently only imposes restrictions on
     * the item name (that is, it must exist), but in the future might be necessary to add more
     * complex restrictions on more fields.
     * @return A Boolean; 'true' if validation succeeded, otherwise 'false'.
     */
    private boolean validateFields() {
        // === get references to Views
        final TextInputEditText itemNameEditText = findViewById(R.id.item_name_edittext);
        boolean val_result = true;

        // item name
        if (itemNameEditText.getText() == null || itemNameEditText.getText().toString().length() == 0) {
            itemNameEditText.setError("Item name is required.");
            val_result = false;
        }

        // ValueTextWatcher effectively handles validation for the item value
        // DatePickerDialog and call to ItemDate.ymdToString effectively handles date validation

        return val_result;
    }

    /**
     * Creates an InventoryItem from the fields on screen.
     * @return The new InventoryItem.
     */
    private InventoryItem makeInventoryItem() {
        // assume validateFields has already been run and inputs are OK
        // all fields are new except for firebaseId & tags (which is set in a separate activity)
        return new InventoryItem(
            currentItem.getFirebaseId(),
            itemNameEditText.getText().toString(),
            itemDescriptionEditText.getText().toString(),
            itemCommentEditText.getText().toString(),
            itemMakeEditText.getText().toString(),
            itemModelEditText.getText().toString(),
            itemSerialEditText.getText().toString(),
            new ItemValue(itemValueEditText.getText().toString()),
            new ItemDate(itemDateText.getText().toString()),
            currentItem.getTags(),
            imageAdapter.getImageList(), // the current images displayed onscreen
            currentItem.getImages() // original image paths before edit
        );
    }

    public void onImageDownload(int pos, ItemImage image) {
        Log.d("EditActivity", "onimagedownload called");
        imageAdapter.set(pos, image);
    }
}
