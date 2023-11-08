package com.example.omninventory;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


// TODO:
//  Input validation and testing
//  Clean up layout file UI
//  Documentation
public class SortFilterActivity extends AppCompatActivity {
    private String dropdownSelection;
    private ItemDate startDate;
    private ItemDate endDate;
    private String sortOrder;
    private String makeText;
    private String descriptionText;
    private boolean makePressed;
    private boolean datePressed;
    private boolean descriptionPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_filter);

        final EditText makeFilterEditText = findViewById(R.id.make_filter_edit_text);
        final Button makeFilterButton = findViewById(R.id.add_make_filter_button);
        final Button ascDescButton = findViewById(R.id.asc_desc_button);
        sortOrder = getResources().getString(R.string.ascending);

        final Button dateFilterButton = findViewById(R.id.add_date_filter_button);
        final EditText descriptionFilterEditText = findViewById(R.id.description_filter_edit_text);
        final Button descriptionFilterButton = findViewById(R.id.add_description_filter_button);
        final Button filterByTagsButton = findViewById(R.id.filter_by_tags_button);

        final ImageButton backButton = findViewById(R.id.back_button);

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

        TextView startDateText = findViewById(R.id.start_date_text);
        Button startDateBtn = findViewById(R.id.start_date_button);

        TextView endDateText = findViewById(R.id.end_date_text);
        Button endDateBtn = findViewById(R.id.end_date_button);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("sortBy") != null) {
                dropdownSelection = intent.getStringExtra("sortBy");
                List<String> list= Arrays.asList(getResources().getStringArray(R.array.sort_dropdown_options));
                sortDropdown.setSelection(list.indexOf(dropdownSelection));
            }
            if (intent.getStringExtra("sortOrder") != null) {
                sortOrder = intent.getStringExtra("sortOrder");
                if (sortOrder.equals(getResources().getString(R.string.descending))) {
                    ascDescButton.setText(R.string.descending);
                }
            }
            if (intent.getStringExtra("filterMake") != null) {
                makeText = intent.getStringExtra("filterMake");
                makeFilterEditText.setText(makeText);
                makeFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.clicked_filter_button));
                makePressed = true;
            }
            if (intent.getSerializableExtra("filterStartDate") != null) {
                startDate = (ItemDate) intent.getSerializableExtra("filterStartDate");
                startDateText.setText(startDate.toString());
                datePressed = true;
            }
            if (intent.getSerializableExtra("filterEndDate") != null) {
                endDate = (ItemDate) intent.getSerializableExtra("filterEndDate");
                endDateText.setText(endDate.toString());
            }
            if (intent.getStringExtra("filterDescription") != null) {
                descriptionText = intent.getStringExtra("filterDescription");
                descriptionFilterEditText.setText(descriptionText);
                descriptionFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.clicked_filter_button));
                descriptionPressed = true;
            }
        }

        if (startDate != null && endDate != null) {
            dateFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.clicked_filter_button));
        }

        sortDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                dropdownSelection = (String) parentView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                dropdownSelection = "None";
            }

        });

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

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c;
                if (startDate != null) {
                    // restore date from previously selected
                    c = startDate.toCalendar();
                }
                else {
                    c = Calendar.getInstance();
                }

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SortFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // month starts at 0
                                monthOfYear++;
                                String dateStr = ItemDate.ymdToString(year, monthOfYear, dayOfMonth);
                                startDateText.setText(dateStr);
                                startDate = new ItemDate(dateStr);
                            }
                        },
                        year, month, day);
                if (endDate != null) {
                    // startDate can't be after endDate
                    datePickerDialog.getDatePicker().setMaxDate(endDate.toCalendar().getTimeInMillis());
                }
                datePickerDialog.show();
            }
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c;
                if (endDate != null) {
                    // restore date from previously selected
                    c = endDate.toCalendar();
                }
                else {
                    c = Calendar.getInstance();
                }

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SortFilterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // months start at 0
                                monthOfYear++;
                                String dateStr = ItemDate.ymdToString(year, monthOfYear, dayOfMonth);
                                endDateText.setText(dateStr);
                                endDate = new ItemDate(dateStr);
                            }
                        },
                        year, month, day);
                if (startDate != null) {
                    // endDate can't be after startDate
                    datePickerDialog.getDatePicker().setMinDate(startDate.toCalendar().getTimeInMillis());
                }
                datePickerDialog.show();
            }
        });

        makeFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (makePressed) {
                    makeFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.unclicked_filter_button));
                    makePressed = false;
                }
                else {
                    makeText = makeFilterEditText.getText().toString();
                    makeFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.clicked_filter_button));
                    makePressed = true;
                }
            }
        });

        dateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (datePressed) {
                    dateFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.unclicked_filter_button));
                    datePressed = false;
                }
                else {
                    dateFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.clicked_filter_button));
                    datePressed = true;
                }
            }
        });

        descriptionFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (descriptionPressed) {
                    descriptionFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.unclicked_filter_button));
                    descriptionPressed = false;
                }
                else {
                    descriptionText = descriptionFilterEditText.getText().toString();
                    descriptionFilterButton.setBackgroundColor(ContextCompat.getColor(SortFilterActivity.this, R.color.clicked_filter_button));
                    descriptionPressed = true;
                }
            }
        });

        filterByTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //applyTagsFilter(tags, adapter);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SortFilterActivity.this, MainActivity.class);
                putFieldsIntent(myIntent, makePressed, datePressed, descriptionPressed);
                SortFilterActivity.this.startActivity(myIntent);
            }
        });
    }

    private void putFieldsIntent(Intent myIntent, boolean makePressed,
                                 boolean datePressed, boolean descriptionPressed) {
        myIntent.putExtra("sortBy", dropdownSelection);
        myIntent.putExtra("sortOrder", sortOrder);
        if (makePressed) {
            myIntent.putExtra("filterMake", makeText);
        }
        if (datePressed) {
            myIntent.putExtra("filterStartDate", startDate);
            myIntent.putExtra("filterEndDate", endDate);
        }
        if (descriptionPressed) {
            myIntent.putExtra("filterDescription", descriptionText);
        }
    }

    public static void applySorting(String selection, String sortOrder, ArrayAdapter<InventoryItem> adapter,
                                    String descendingText) {
        boolean descending = sortOrder.equals(descendingText);
        switch (selection) {
            case "None":
                break;
            case "Date":
                if (descending) {
                    adapter.sort(new SortByDate().reversed());
                }
                else {
                    adapter.sort(new SortByDate());
                }
                break;
            case "Description":
                if (descending) {
                    adapter.sort(new SortByDescription().reversed());
                }
                else {
                    adapter.sort(new SortByDescription());
                }
                break;
            case "Make":
                if (descending) {
                    adapter.sort(new SortByMake().reversed());
                }
                else {
                    adapter.sort(new SortByMake());
                }
                break;
            case "Estimated Value":
                if (descending) {
                    adapter.sort(new SortByValue().reversed());
                }
                else {
                    adapter.sort(new SortByValue());
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    public static void applyMakeFilter(String make, ArrayAdapter<InventoryItem> adapter) {
        ArrayList<InventoryItem> itemsToRemove = new ArrayList<InventoryItem>();
        for (int i = 0; i < adapter.getCount(); i++) {
            InventoryItem item = adapter.getItem(i);
            if (!item.getMake().equals(make)) {
                itemsToRemove.add(item);
            }
        }
        for (InventoryItem item : itemsToRemove) {
            adapter.remove(item);
        }
        adapter.notifyDataSetChanged();
    }

    public static void applyDateFilter(ItemDate startDate, ItemDate endDate, ArrayAdapter<InventoryItem> adapter) {
        // remove item if before startDate or after endDate
        ArrayList<InventoryItem> itemsToRemove = new ArrayList<InventoryItem>();
        for (int i = 0; i < adapter.getCount(); i++) {
            InventoryItem item = adapter.getItem(i);
            if (item.getDate().toCalendar().compareTo(startDate.toCalendar()) < 0 || item.getDate().toCalendar().compareTo(endDate.toCalendar()) > 0) {
                itemsToRemove.add(item);
            }
        }
        for (InventoryItem item : itemsToRemove) {
            adapter.remove(item);
        }
        adapter.notifyDataSetChanged();
    }

    public static void applyDescriptionFilter(String descriptionKeywords, ArrayAdapter<InventoryItem> adapter) {
        ArrayList<InventoryItem> itemsToRemove = new ArrayList<InventoryItem>();
        String[] keywords = descriptionKeywords.split(" ");
        for (int i = 0; i < adapter.getCount(); i++) {
            InventoryItem item = adapter.getItem(i);
            boolean foundKeyWord = false;
            for (String keyword : keywords) {
                if (item.getDescription().contains(keyword)) {
                    foundKeyWord = true;
                    break;  // short-circuit search
                }
            }
            if (!foundKeyWord) {
                itemsToRemove.add(item);
            }
        }
        for (InventoryItem item : itemsToRemove) {
            adapter.remove(item);
        }
        adapter.notifyDataSetChanged();
    }

    public static void applyTagsFilter(Tag[] tags, ArrayAdapter<InventoryItem> adapter) {
        // do nothing for now, implemented in part 4
    }
}