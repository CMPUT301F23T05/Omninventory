package com.example.omninventory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


// TODO:
//  Ability to sort by all options
//  Intent passing from this activity back to main (pass itemListData back, then update the adapter in MainActivity)
//  Change Calendar/Dates to use ItemDate objects
//  Input validation and testing
//  Clean up layout file UI
//  Documentation
public class SortFilterActivity extends AppCompatActivity {
    ArrayList<InventoryItem> itemListData;
    String dropdownSelection;
    ItemDate startDate;
    ItemDate endDate;
    private String sortOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_filter);

        final EditText makeFilterEditText = findViewById(R.id.make_filter_edit_text);
        final Button makeFilterButton = findViewById(R.id.add_make_filter_button);
        final Button ascDescButton = findViewById(R.id.asc_desc_button);

        final Button dateFilterButton = findViewById(R.id.add_date_filter_button);
        final EditText descriptionFilterEditText = findViewById(R.id.description_filter_edit_text);
        final Button descriptionFilterButton = findViewById(R.id.add_description_filter_button);
        final Button filterByTagsButton = findViewById(R.id.filter_by_tags_button);

        // retrieve data passed from the main activity: itemListData
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getSerializableExtra("itemListData") != null) {
                itemListData = (ArrayList<InventoryItem>) intent.getSerializableExtra("itemListData");
            }
        }

        final TextView titleText = findViewById(R.id.title_text);
        titleText.setText(getString(R.string.sort_filter_title_text));

        final Spinner sortDropdown = findViewById(R.id.sort_dropdown_spinner);
        // ArrayAdapter for dropdown choices
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_dropdown_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortDropdown.setAdapter(adapter);
        sortDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dropdownSelection = (String) parentView.getItemAtPosition(position);
                itemListData = applySorting(dropdownSelection, itemListData);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                dropdownSelection = "";
            }

        });

        sortOrder = (String) ascDescButton.getText();
        ascDescButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortOrder.equals(getResources().getString(R.string.ascending))) {
                    sortOrder = getResources().getString(R.string.descending);;
                    ascDescButton.setText(R.string.descending);
                } else {
                    sortOrder = getResources().getString(R.string.ascending);
                    ascDescButton.setText(R.string.ascending);
                }
            }
        });

        TextView startDateText = findViewById(R.id.start_date_text);
        Button startDateBtn = findViewById(R.id.start_date_button);
        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        SortFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                startDateText.setText(ItemDate.ymdToString(year, monthOfYear, dayOfMonth));
                                startDate.setDate(year, monthOfYear, dayOfMonth);
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(endDate.toCalendar().getTimeInMillis());
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });


        TextView endDateText = findViewById(R.id.end_date_text);
        Button endDateBtn = findViewById(R.id.end_date_button);
        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        SortFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                endDateText.setText(ItemDate.ymdToString(year, monthOfYear, dayOfMonth));
                                endDate.setDate(year, monthOfYear, dayOfMonth);
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                datePickerDialog.getDatePicker().setMinDate(startDate.toCalendar().getTimeInMillis());
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        makeFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String makeText = makeFilterEditText.getText().toString();
                itemListData = applyMakeFilter(makeText, itemListData);
            }
        });

        dateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemListData = applyDateFilter(startDate, endDate, itemListData);
            }
        });
        descriptionFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descriptionText = descriptionFilterEditText.getText().toString();
                itemListData = applyDescriptionFilter(descriptionText, itemListData);
            }
        });

        filterByTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do nothing for now, implemented in part 4
            }
        });
    }

    public ArrayList<InventoryItem> applySorting(String selection, ArrayList<InventoryItem> itemListData) {
        switch (selection) {
            case "None":
            case "Date":
                Comparator<InventoryItem> sortDate =  new Comparator<InventoryItem>() {
                    public int compare(InventoryItem ie1, InventoryItem ie2) {
                        return ie1.getDate().toCalendar().compareTo(ie2.getDate().toCalendar());
                    }
                };
                itemListData.sort(sortDate);
            case "Description":
                Comparator<InventoryItem> sortDescription =  new Comparator<InventoryItem>() {
                    public int compare(InventoryItem ie1, InventoryItem ie2) {
                        return ie1.getDescription().compareTo(ie2.getDescription());
                    }
                };
                itemListData.sort(sortDescription);
            case "Make":
                Comparator<InventoryItem> sortMake =  new Comparator<InventoryItem>() {
                    public int compare(InventoryItem ie1, InventoryItem ie2) {
                        return ie1.getMake().compareTo(ie2.getMake());
                    }
                };
                itemListData.sort(sortMake);
            case "Estimated Value":
                Comparator<InventoryItem> sortEstimatedValue =  new Comparator<InventoryItem>() {
                    public int compare(InventoryItem ie1, InventoryItem ie2) {
                        return ie1.getValue().compare(ie2.getValue());
                    }
                };
                itemListData.sort(sortEstimatedValue);
        }
        return itemListData;
    }

    public ArrayList<InventoryItem> applyMakeFilter(String make, ArrayList<InventoryItem> itemListData) {
        itemListData.removeIf(item -> !item.getMake().equals(make));
        return itemListData;
    }

    public ArrayList<InventoryItem> applyDateFilter(ItemDate startDate, ItemDate endDate, ArrayList<InventoryItem> itemListData) {
        // remove item if before startDate or after endDate
        itemListData.removeIf(item -> item.getDate().toCalendar().compareTo(startDate.toCalendar()) < 0 || item.getDate().toCalendar().compareTo(endDate.toCalendar()) > 0);
        return itemListData;
    }

    public ArrayList<InventoryItem> applyDescriptionFilter(String descriptionKeywords, ArrayList<InventoryItem> itemListData) {
        String[] keywords = descriptionKeywords.split(" ");
        for (InventoryItem item : itemListData) {
            boolean foundKeyWord = false;
            for (String keyword : keywords) {
                if (item.getDescription().contains(keyword)) {
                    foundKeyWord = true;
                    break;  // short-circuit search
                }
            }
            if (!foundKeyWord) {
                itemListData.remove(item);
            }
        }
        return itemListData;
    }
}