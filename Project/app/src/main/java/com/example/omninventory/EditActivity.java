package com.example.omninventory;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

/**
 * Activity for editing an item's detailed fields.
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
        if (savedInstanceState != null) {
            // TODO: this will probably be used later when we go from this activity to others; for now, error
            throw new RuntimeException("EditActivity opened with a savedInstanceState");
        }
        else if (getIntent().getExtras() == null) {
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

        // if initialized with an item, set default values for fields
        itemNameEditText.setText(currentItem.getName());

        // === set up click actions
        final ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // return to DetailsActivity without changing any item fields
                setResult(RESULT_CANCELED, null); // RESULT_CANCELED signifies edits cancelled
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

                    setResult(RESULT_OK, null); // RESULT_OK signifies edits were made
                    finish(); // return to DetailsActivity
                }
                else {
                    // show Toast prompting user to fix input issues
                    CharSequence toastText = "Please fix invalid input fields.";
                    Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                    toast.show();

                }
            }
        });
    }

    /**
     * Handles input validation for fields on this screen.
     */
    private boolean validateInputs() {
        // === get references to Views
        final TextInputEditText itemNameEditText = findViewById(R.id.item_name_edittext);
        boolean val_result = true;

        // === item name
        if (itemNameEditText.getText().toString().length() == 0) {
            itemNameEditText.setError("Item name is required.");
            val_result = false;
        }

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
            0,
            new Date()
        );
    }
}
