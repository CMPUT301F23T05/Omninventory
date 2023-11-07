package com.example.omninventory;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

/**
 * Activity for editing an item's fields.
 */
public class EditActivity extends AppCompatActivity  {

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
    // TODO: tags, images

    private ValueTextWatcher itemValueTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // === set up database
        repo = new InventoryRepository();

        // === get references to Views
        final TextView titleText = findViewById(R.id.title_text);
        itemDateText = findViewById(R.id.item_date_text);

        itemNameEditText = findViewById(R.id.item_name_edittext);
        itemDescriptionEditText = findViewById(R.id.item_description_edittext);
        itemCommentEditText = findViewById(R.id.item_comment_edittext);
        itemMakeEditText = findViewById(R.id.item_make_edittext);
        itemModelEditText = findViewById(R.id.item_model_edittext);
        itemSerialEditText = findViewById(R.id.item_serial_edittext);
        itemValueEditText = findViewById(R.id.item_value_edittext);

        // === load info passed from DetailsActivity (hopefully)
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

        // === UI setup

        // set title text
        if (newItemFlag) {
            titleText.setText(getString(R.string.add_title_text));
        }
        else {
            titleText.setText(getString(R.string.edit_title_text));
        }

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

        // back button should take us back to DetailsActivity without saving any item data on click
        final ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // display an exit message
                CharSequence toastText = "Edits were discarded.";
                Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
                toast.show();

                // return without changing any item fields
                finish();
            }
        });

        // itemDateButton should open a DatePickerDialog to choose date
        final ImageButton itemDateButton = findViewById(R.id.item_date_button);
        itemDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get current date for default calendar value
                final Calendar c = Calendar.getInstance();
                int currentYear = c.get(Calendar.YEAR);
                int currentMonth = c.get(Calendar.MONTH);
                int currentDay = c.get(Calendar.DAY_OF_MONTH);

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

        // saveButton should send data to Firebase and return to DetailsActivity
        final ImageButton saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: implement this

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
        itemValueEditText.setText(item.getValue().toString()); // convert ItemValue to String
        itemDateText.setText(item.getDate().toString()); // convert ItemDate to String. note this is a TextView, not EditText
//        itemTagsEditText.setText(item.getTagsString());
    }

    /**
     * Handles input validation for fields on this screen.
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
     */
    private InventoryItem makeInventoryItem() {
        // assume validateFields has already been run and inputs are OK

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
            new ItemValue(itemValueEditText.getText().toString()),
            new ItemDate(itemDateText.getText().toString())
        );
    }
}
