package com.example.omninventory;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

/**
 * Activity for editing an item's fields.
 */
public class EditActivity extends AppCompatActivity  {

    private InventoryRepository repo;

    private InventoryItem currentItem;

    // references to Views put here because they are used in multiple methods
    private TextInputEditText itemNameEditText;
    private TextInputEditText itemDescriptionEditText;
    private TextInputEditText itemCommentEditText;
    private TextInputEditText itemMakeEditText;
    private TextInputEditText itemModelEditText;
    private TextInputEditText itemSerialEditText;
    private TextInputEditText itemValueEditText;
    // TODO: date, tags, images

    private ValueTextWatcher itemValueTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // === set up database
        repo = new InventoryRepository();

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);

        itemNameEditText = findViewById(R.id.item_name_edittext);
        itemDescriptionEditText = findViewById(R.id.item_description_edittext);
        itemCommentEditText = findViewById(R.id.item_comment_edittext);
        itemMakeEditText = findViewById(R.id.item_make_edittext);
        itemModelEditText = findViewById(R.id.item_model_edittext);
        itemSerialEditText = findViewById(R.id.item_serial_edittext);
        itemValueEditText = findViewById(R.id.item_value_edittext);

        // === load info passed from DetailsActivity (hopefully)
        if (getIntent().getExtras() == null) {
            throw new RuntimeException("EditActivity opened without an InventoryItem");
        }
        else {
            currentItem = (InventoryItem) getIntent().getExtras().getSerializable("item");
        }

        // === UI setup

        // set title text
        titleText.setText(getString(R.string.edit_title_text));

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_edit, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

        // set default values for fields
        Log.d("EditActivity", "setFields called from onCreate");
        setFields(currentItem);

        // set up TextWatchers for dynamic input formatting
        Log.d("EditActivity", "set up TextWatcher");
        // initial text modified by TextWatcher will be from currentItem, as EditText contents were just set by setFields
        itemValueEditText.addTextChangedListener(new ValueTextWatcher(itemValueEditText));

        // === set up click actions
        final ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // display an exit message
                CharSequence toastText = "Edits were discarded.";
                Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                toast.show();

                // return to DetailsActivity without changing any item fields
                finish();
            }
        });

        final ImageButton saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: implement this

                if (validateInputs()) {
                    Log.d("EditActivity", "validation success, updating database");
                    InventoryItem updatedItem = makeInventoryItem();
                    repo.updateInventoryItem(updatedItem); // save changes to item fields

                    // display a success message
                    CharSequence toastText = String.format("%s was edited.", updatedItem.getName());
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();

                    finish(); // return to DetailsActivity
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
     * @param item
     */
    private void setFields(InventoryItem item) {
        // add item details to each field
        itemNameEditText.setText(item.getName());
        itemDescriptionEditText.setText(item.getDescription());
        itemCommentEditText.setText(item.getComment());
        itemMakeEditText.setText(item.getMake());
        itemModelEditText.setText(item.getModel());
        itemSerialEditText.setText(item.getSerialno());
        itemValueEditText.setText(item.getValue().toString());

//        itemDateEditText.setText(item.getDate().toString());
//        itemTagsEditText.setText(item.getTagsString());
    }

    /**
     * Handles input validation for fields on this screen.
     */
    private boolean validateInputs() {
        // === get references to Views
        final TextInputEditText itemNameEditText = findViewById(R.id.item_name_edittext);
        boolean val_result = true;

        // item name
        if (itemNameEditText.getText() == null || itemNameEditText.getText().toString().length() == 0) {
            itemNameEditText.setError("Item name is required.");
            val_result = false;
        }

        // ValueTextWatcher effectively handles validation for the item value

        return val_result;
    }

    /**
     * Creates an InventoryItem from the fields on screen.
     */
    private InventoryItem makeInventoryItem() {
        // assume validateInputs has already been run and inputs are OK

        // TODO: this is not complete, need Date & others
        // all fields are new except for firebaseId
        return new InventoryItem(
            currentItem.getFirebaseId(),
            itemNameEditText.getText().toString(),
            itemDescriptionEditText.getText().toString(),
            itemCommentEditText.getText().toString(),
            itemMakeEditText.getText().toString(),
            itemModelEditText.getText().toString(),
            itemSerialEditText.getText().toString(),
            new InventoryItemValue(itemValueEditText.getText().toString()),
            new Date()
        );
    }
}
