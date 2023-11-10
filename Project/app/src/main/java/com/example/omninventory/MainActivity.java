package com.example.omninventory;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.color.MaterialColors;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

/**
 * Main screen of the app. Holds list of inventory items and buttons
 * that take user to other screens.
 */
public class MainActivity extends AppCompatActivity implements InventoryUpdateHandler {

    private InventoryRepository repo;
    private ArrayList<InventoryItem> itemListData;
    private ArrayList<InventoryItem> completeItemList;
    private InventoryItemAdapter itemListAdapter;
    SharedPreferences sharedPrefs;
    private String sortBy;
    private String sortOrder;
    private String filterMake;
    private ItemDate filterStartDate;
    private ItemDate filterEndDate;
    private String filterDescription;
    private User currentUser;
    private ListView itemList;
    private TextView titleText;
    private TextView totalValueText;

    private Long totalValue;

    private ArrayList<InventoryItem> selectedItems;

    private Dialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: this is testing code, replace when merged with Rose's code
        currentUser = new User("erika", "erikausername", "password", new ArrayList<String>());

        selectedItems = new ArrayList<>();

        // === set up database
        repo = new InventoryRepository();

        // Get references to views
        itemList = findViewById(R.id.item_list);
        titleText = findViewById(R.id.title_text);
        totalValueText = findViewById(R.id.total_value_text);

        // === UI setup
        titleText.setText(getString(R.string.main_title_text)); // set title text

        // this will store user's login state to keep them logged in
        sharedPrefs = getSharedPreferences("login", MODE_PRIVATE);

        // get taskbar buttons
        final ImageButton profileBtn = findViewById(R.id.profile_button);

        // check if user just logged in
        if (getIntent().getExtras() != null)  {
            // user just logged in
            String user = getIntent().getExtras().getString("loggedInUser");
            sharedPrefs.edit().putBoolean("logged", true).apply();
            sharedPrefs.edit().putString("username", user).apply();
            Log.d("login", "Logged in as: " + user);
            // todo: for testing purposes only, will remove later
            Toast.makeText(getApplicationContext(), "Logged in as , " + user, Toast.LENGTH_LONG).show();
        }

        // === set up itemList owned by logged in user
        itemListData = new ArrayList<InventoryItem>();

        // retrieve data passed from SortFilterActivity: itemListData and sortBy
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getSerializableExtra("itemListData") != null) {
                itemListData = (ArrayList<InventoryItem>) intent.getSerializableExtra("itemListData");
                completeItemList = (ArrayList<InventoryItem>) itemListData.clone();
            }
            if (intent.getStringExtra("sortBy") != null) {
                sortBy = intent.getStringExtra("sortBy");
            }
            if (intent.getStringExtra("sortOrder") != null) {
                sortOrder = intent.getStringExtra("sortOrder");
            }
            if (intent.getStringExtra("filterMake") != null) {
                filterMake = intent.getStringExtra("filterMake");
            }
            if (intent.getSerializableExtra("filterStartDate") != null) {
                filterStartDate = (ItemDate) intent.getSerializableExtra("filterStartDate");
            }
            if (intent.getSerializableExtra("filterEndDate") != null) {
                filterEndDate = (ItemDate) intent.getSerializableExtra("filterEndDate");
            }
            if (intent.getStringExtra("filterDescription") != null) {
                filterDescription = intent.getStringExtra("filterDescription");
            }
        }

        // === connect itemList to Firestore database
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);
        ListenerRegistration registration = repo.setupInventoryItemList(itemListAdapter, this); // set up listener for getting Firestore data

        // === apply sort and filter
        if (sortBy != null && sortOrder != null) {
            // should always trigger if coming from SortFilterActivity
            registration.remove();
            String ascendingText = getString(R.string.ascending);
            String descendingText = getString(R.string.descending);
            SortFilterActivity.applySorting(sortBy, sortOrder, itemListAdapter, descendingText);
        }
        if (filterMake != null) {
            SortFilterActivity.applyMakeFilter(filterMake, itemListAdapter);
        }
        if (filterStartDate != null && filterEndDate != null) {
            SortFilterActivity.applyDateFilter(filterStartDate, filterEndDate, itemListAdapter);
        }
        if (filterDescription != null) {
            SortFilterActivity.applyDescriptionFilter(filterDescription, itemListAdapter);
        }
    
        // Setup delete items dialog
        deleteDialog = new Dialog(this);

        // Setup selected items of inventory
        selectedItems = new ArrayList<InventoryItem>();
        resetSelectedItems();

        calcValue(); // Get total estimated value

        // === Set up onClick actions

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("MainActivity", "click");
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("item", itemListData.get(position));
                detailsIntent.putExtra("user", currentUser);
                resetSelectedItems();
                startActivity(detailsIntent);
            }
        });

        ImageButton sortFilterBtn = findViewById(R.id.sort_filter_button);
        sortFilterBtn.setOnClickListener((v) -> {
            Intent sortFilterIntent = new Intent(MainActivity.this, SortFilterActivity.class);
            sortFilterIntent.putExtra("sortBy", sortBy);
            sortFilterIntent.putExtra("sortOrder", sortOrder);
            sortFilterIntent.putExtra("filterMake", filterMake);
            sortFilterIntent.putExtra("filterStartDate", filterStartDate);
            sortFilterIntent.putExtra("filterEndDate", filterEndDate);
            sortFilterIntent.putExtra("filterDescription", filterDescription);
            MainActivity.this.startActivity(sortFilterIntent);
        });

        ImageButton addItemButton = findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener((v) -> {
            Intent addIntent = new Intent(MainActivity.this, EditActivity.class);
            // intent launched without an InventoryItem
            addIntent.putExtra("user", currentUser);
            startActivity(addIntent);
        });

        ImageButton deleteItemButton = findViewById(R.id.delete_item_button);
        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.size() > 0) {
                    deleteDialog();
                }
            }
        });
        
        profileBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // todo: this logs out the current user, for testing only, will remove this later
                sharedPrefs.edit().putBoolean("logged",false).apply();
                // check if user is logged in
                if (!sharedPrefs.getBoolean("logged",false)) {
                    startLoginActivity();
                }
                else {
                    // start ProfileActivity
                }
            }
        });

        ImageButton tagButton = findViewById(R.id.tag_button);
        tagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedItems.size() > 0) {
                    // go to apply tags screen
                } else {
                    Intent manageTagsIntent = new Intent(MainActivity.this, ManageTagsActivity.class);
                    startActivity(manageTagsIntent);
                }
            }
        });

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                InventoryItem item = itemListData.get(position);
                if (item.isSelected()) {
                    item.setSelected(false);
                    selectedItems.remove(item);
                } else {
                    item.setSelected(true);
                    selectedItems.add(item);
                }
                // colours are set to reflect selection in InventoryItemAdapter
                itemListAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void deleteDialog() {
        deleteDialog.setCancelable(false);
        deleteDialog.setContentView(R.layout.delete_dialog);

        // UI Elements of Dialog
        TextView deleteItemsText = deleteDialog.findViewById(R.id.delete_message);
        Button deleteButton = deleteDialog.findViewById(R.id.delete_dialog_button);
        Button cancelButton = deleteDialog.findViewById(R.id.cancel_dialog_button);

        // Fill out TextView with information
        String defaultText = "Are you sure you would like to delete the selected items (";
        int index = 0;
        for (InventoryItem selectedItem : selectedItems) {
            defaultText += selectedItem.getName();
            if (index == selectedItems.size() - 1) {
                defaultText += ")";
            } else {
                defaultText += ", ";
            }
            index += 1;
        }
        deleteItemsText.setText(defaultText);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (InventoryItem selectedItem : selectedItems) {
                    if (selectedItem != null) {
                        repo.deleteInventoryItem(currentUser, selectedItem.getFirebaseId());
                    }
                    itemListData.remove(selectedItem);
                    itemListAdapter.notifyDataSetChanged();
                }
                calcValue();
                resetSelectedItems();
                deleteDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSelectedItems();
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }

    private void calcValue() {
        // TODO: this has overflow issues
        Log.d("MainActivity", String.format("calcValue called, number of items in list is %d", itemListData.size()));
        totalValue = 0L;
        for (InventoryItem item : itemListData) {
            totalValue += item.getValue().toPrimitiveLong();
        }
        String formattedValue = ItemValue.numToString(totalValue);
        totalValueText.setText(formattedValue);
    }

    private void resetSelectedItems() {
        for (InventoryItem selectedItem: selectedItems) {
            selectedItem.setSelected(false);
        }
        selectedItems.clear();
        System.out.println("The number of selected items is: " + Integer.toString(selectedItems.size()));
    }

    /**
     * Need to asynchronously call an update routine when items are added to the list, else
     * value will be calculated before items
     */
    public void onItemListUpdate() {

        this.calcValue();
        this.sortAndFilter();
    }

    public void sortAndFilter() {
        if (sortBy != null && sortOrder != null) {
            // should always trigger if coming from SortFilterActivity
            String descendingText = getString(R.string.descending);
            SortFilterActivity.applySorting(sortBy, sortOrder, itemListAdapter, descendingText);
        }
        if (filterMake != null) {
            SortFilterActivity.applyMakeFilter(filterMake, itemListAdapter);
        }
        if (filterStartDate != null && filterEndDate != null) {
            SortFilterActivity.applyDateFilter(filterStartDate, filterEndDate, itemListAdapter);
        }
        if (filterDescription != null) {
            SortFilterActivity.applyDescriptionFilter(filterDescription, itemListAdapter);
        }
    }

    private void startLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
            