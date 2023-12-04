package com.example.omninventory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.ListenerRegistration;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


// TODO:
//  Input validation and testing
//  Clean up layout file UI
//  Documentation

/**
 * Activity used for user to select how to sort/filter items. Selections for sorting (dropdown)
 * are: None, Date, Description, Make, and Value. Options for filtering are make, date, description,
 * and tags. Selections are passed back to MainActivity via Intent. Previous selections
 * are passed back to MainActivity and will be restored when coming back to this Activity.
 * Static methods are provided for the actual sorting/filtering so they can be accessed from
 * anywhere. Sorting is done with the help of Comparator class.
 * @author Zachary
 */
public class SortFilterActivity extends AppCompatActivity {
    private String dropdownSelection;
    private ItemDate startDate;
    private ItemDate endDate;
    private ArrayList<Tag> tagFilter;
    private String sortOrder;
    private String makeText;
    private String descriptionText;
    private Dialog tagFilterDialog;
    private boolean makePressed;
    private boolean datePressed;
    private boolean descriptionPressed;
    private boolean tagsPressed;
    private User currentUser;

    @ColorInt int colorFilterApplied;
    @ColorInt int colorFilterNotApplied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_filter);

        // get all buttons/editTexts to be accessed later
        final EditText makeFilterEditText = findViewById(R.id.make_filter_edit_text);
        final Button makeFilterButton = findViewById(R.id.add_make_filter_button);
        final Button ascDescButton = findViewById(R.id.asc_desc_button);
        sortOrder = getResources().getString(R.string.ascending);

        final Button dateFilterButton = findViewById(R.id.add_date_filter_button);
        final EditText descriptionFilterEditText = findViewById(R.id.description_filter_edit_text);
        final Button descriptionFilterButton = findViewById(R.id.add_description_filter_button);
        final Button filterByTagsButton = findViewById(R.id.filter_by_tags_button);

        tagFilter = new ArrayList<>();
        tagFilterDialog = new Dialog(this);

        final ImageButton backButton = findViewById(R.id.back_button);

        final TextView titleText = findViewById(R.id.title_text);
        titleText.setText(getString(R.string.sort_filter_title_text));

        final Spinner sortDropdown = findViewById(R.id.sort_dropdown_spinner);

        // ArrayAdapter for dropdown choices. Choices stored in strings.xml
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_dropdown_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortDropdown.setAdapter(adapter);

        TextView startDateText = findViewById(R.id.start_date_text);
        ImageButton startDateBtn = findViewById(R.id.start_date_button);

        TextView endDateText = findViewById(R.id.end_date_text);
        ImageButton endDateBtn = findViewById(R.id.end_date_button);

        // ==== get theme colours for setting button colours on selection
        Resources.Theme theme = this.getTheme();
        TypedValue typedValue = new TypedValue();

        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        colorFilterApplied = typedValue.data;

        theme.resolveAttribute(com.google.android.material.R.attr.colorSecondary, typedValue, true);
        colorFilterNotApplied = typedValue.data;

        // to restore previous selections, get the data from intent
        // if restoring value, set button background color to R.color.clicked_filter_button
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
                makeFilterButton.setBackgroundColor(colorFilterApplied);
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
                descriptionFilterButton.setBackgroundColor(colorFilterApplied);
                descriptionPressed = true;
            }
            if (intent.getSerializableExtra("filterTags") != null) {
                setTagFilter((ArrayList<Tag>) intent.getSerializableExtra("filterTags"));

            }
            if (intent.getSerializableExtra("login") != null) {
                currentUser = (User) intent.getSerializableExtra("login");
            }
        }

        if (startDate != null && endDate != null) {
            dateFilterButton.setBackgroundColor(colorFilterApplied);
        }

        // store dropdown selection (how we will sort the items)
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

        // store ascending/descending selection. Serves as a toggle button. Default is ascending.
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

        // use Calendar widget to prompt user for start/endDate
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

        // apply filter buttons act like toggle buttons (apply or don't apply)
        makeFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (makePressed) {
                    makeFilterButton.setBackgroundColor(colorFilterNotApplied);
                    makePressed = false;
                }
                else {
                    makeText = makeFilterEditText.getText().toString();
                    makeFilterButton.setBackgroundColor(colorFilterApplied);
                    makePressed = true;
                }
            }
        });

        dateFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (datePressed) {
                    dateFilterButton.setBackgroundColor(colorFilterNotApplied);
                    datePressed = false;
                }
                else {
                    dateFilterButton.setBackgroundColor(colorFilterApplied);
                    datePressed = true;
                }
            }
        });

        descriptionFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (descriptionPressed) {
                    descriptionFilterButton.setBackgroundColor(colorFilterNotApplied);
                    descriptionPressed = false;
                }
                else {
                    descriptionText = descriptionFilterEditText.getText().toString();
                    descriptionFilterButton.setBackgroundColor(colorFilterApplied);
                    descriptionPressed = true;
                }
            }
        });

        filterByTagsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagFilter.isEmpty()) {
                    tagFilterDialog();
                } else {
                    setTagFilter(new ArrayList<>());
                }
            }
        });

        // when going back, pass all relevant data to intent
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(SortFilterActivity.this, MainActivity.class);
                makeText = makeFilterEditText.getText().toString();
                descriptionText = descriptionFilterEditText.getText().toString();
                putFieldsIntent(myIntent, makePressed, datePressed, descriptionPressed, tagsPressed);
                SortFilterActivity.this.startActivity(myIntent);
                finish();
            }
        });
    }

    /**
     * Put all fields where filter has been applied in intent.
     * @param myIntent - intent to pass to MainActivity
     * @param makePressed - true if "apply make filter" is toggled to on
     * @param datePressed - true if "apply date filter" is toggled to on
     * @param descriptionPressed - true if "apply description filter" is toggled to on
     */
    private void putFieldsIntent(Intent myIntent, boolean makePressed,
                                 boolean datePressed, boolean descriptionPressed, boolean tagsPressed) {
        myIntent.putExtra("sortBy", dropdownSelection);
        myIntent.putExtra("sortOrder", sortOrder);
        myIntent.putExtra("login", currentUser);
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
        if (tagsPressed) {
            myIntent.putExtra("filterTags", tagFilter);
        }
    }

    /**
     * Perform sorting on the ArrayAdapter of Inventory items. Can be done in ascending
     * or descending order. Implemented with the help of Comparator class.
     * Changes are applied directly to the passed adapter.
     * @param sortBy - user selection to sort the items by. Can be one of: "None", "Date",
     *               "Description", "Make", "Estimated Value", "Tags"
     * @param sortOrder - either "ascending" or "descending"
     * @param adapter - ArrayAdapter of InventoryItems to sort
     * @param descendingText - the string "descending". Values in strings.xml cannot be
     *                       accessed in static methods so it must be passed in
     */
    public static void applySorting(String sortBy, String sortOrder, ArrayAdapter<InventoryItem> adapter,
                                    String descendingText) {
        boolean descending = sortOrder.equals(descendingText);
        switch (sortBy) {
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
            case "Tags":
                if (descending) {
                    adapter.sort(new SortByTags().reversed());
                }
                else {
                    adapter.sort(new SortByTags());
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Filter the adapter of InventoryItems by make. Will remove all InventoryItems that have a
     * different make from the one supplied.
     * Changes are applied directly to the passed adapter.
     * don't have the corresponding make.
     * @param make - make to filter the adpater of items by
     * @param adapter - the adapter to filter
     */
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

    /**
     * Filter the adapter of InventoryItems by date. Will remove all InventoryItems that don't have
     * date between startDate and endDate. Changes are applied directly to the passed adapter.
     * @param startDate - starting bound for accepted dates. InventoryItems with date less than
     *                  this are removed
     * @param endDate - end bound for accepted dates. InventoryItems with date greater than
     *      *                  this are removed
     * @param adapter - the adapter to filter
     */
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

    /**
     * Filter the adapter of InventoryItems by description keywords. Will remove InventoryItems
     * from the adapter if their description doesn't contain at least one keyword.
     * Changes are applied directly to the passed adapter.
     * @param descriptionKeywords - string of space-separated keywords
     * @param adapter - the adapter to filter
     */
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

    /**
     * Filter the adapter of InventoryItems by applied tags. Will remove InventoryItems from the
     * adapter if they do not have all tags.
     * Changes are applied directly to the passed adapter.
     * @param tags    the list of tag objects by which to filter
     * @param adapter the adapter to filter
     */
    public static void applyTagsFilter(ArrayList<Tag> tags, ArrayAdapter<InventoryItem> adapter) {
        ArrayList<InventoryItem> itemsToRemove = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            InventoryItem item = adapter.getItem(i);
            for (int j = 0; j < tags.size(); j++) {
                if (!tags.get(j).getItemIds().contains(item.getFirebaseId())) {
                    itemsToRemove.add(item);
                    break;
                }
            }
        }
        for (InventoryItem item : itemsToRemove) {
            adapter.remove(item);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Sets the current list of tags by which to filter and updates display.
     * @param tagFilter the list of tags by which to filter.
     */
    private void setTagFilter(ArrayList<Tag> tagFilter) {
        this.tagFilter = tagFilter;

        Button tagFilterButton = findViewById(R.id.filter_by_tags_button);
        TextView tagFilterText = findViewById(R.id.tag_filter_text_content);

        if (this.tagFilter.isEmpty()) {
            tagFilterButton.setBackgroundColor(colorFilterNotApplied);
            tagFilterText.setVisibility(View.GONE);
            tagsPressed = false;
        }
        else {
            String tagString = "";
            for (int i = 0; i < tagFilter.size(); i++) {
                tagString = String.join(" ", tagString, String.format("#%s", tagFilter.get(i).getName()));
            }
            tagFilterButton.setBackgroundColor(colorFilterApplied);
            tagFilterText.setText(tagString);
            tagFilterText.setVisibility(View.VISIBLE);
            tagsPressed = true;
        }
    }

    /**
     * Displays a dialog to let the user select one or more tags by which to filter.
     */
    private void tagFilterDialog() {

        tagFilterDialog.setCancelable(false);
        tagFilterDialog.setContentView(R.layout.tag_filter_dialog);

        InventoryRepository repo = new InventoryRepository();
        ArrayList<Tag> selectedTags = new ArrayList<>();

        // UI Elements
        ListView tagList = tagFilterDialog.findViewById(R.id.tag_filter_list);
        Button filterButton = tagFilterDialog.findViewById(R.id.tag_filter_dialog_button);

        ArrayList<Tag> tagListData = new ArrayList<>();

        TagAdapter tagListAdapter = new TagAdapter(this, tagListData);
        tagList.setAdapter(tagListAdapter);
        ListenerRegistration registration = repo.setupTagList(tagListAdapter, currentUser);

        // make background transparent for our rounded corners
        tagFilterDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tag tag = tagListData.get(i);
                if (tag.isSelected()) {
                    tag.setSelected(false);
                    selectedTags.remove(tag);
                } else {
                    tag.setSelected(true);
                    selectedTags.add(tag);
                }
                // colours are set to reflect selection in TagAdapter
                tagListAdapter.notifyDataSetChanged();
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTagFilter(selectedTags);
                tagFilterDialog.dismiss();
            }
        });

        tagFilterDialog.show();
    }
}