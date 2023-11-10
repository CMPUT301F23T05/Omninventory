package com.example.omninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * Main screen of the app. Holds list of inventory items and buttons
 * that take user to other screens.
 */
public class MainActivity extends AppCompatActivity implements ItemListUpdateHandler {

    private InventoryRepository repo;
    private ArrayList<InventoryItem> itemListData;
    private ArrayList<InventoryItem> completeItemList;
    private InventoryItemAdapter itemListAdapter;
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
        Intent intent = getIntent();
        // check if user is logged in or out
        if (intent.getExtras() != null)  {
            if (intent.getStringExtra("login") != null) {
                // user just logged in
                currentUser = (User) getIntent().getSerializableExtra("loggedInUser");
                Log.d("main", "Logged in as: " + currentUser.getName());
            }
        }

        // make sure user is logged in before giving them access to the rest of the app
        if (currentUser == null) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
        else {
            // === set up database
            repo = new InventoryRepository(currentUser.getUsername());


        // Get references to views
        itemList = findViewById(R.id.item_list);
        titleText = findViewById(R.id.title_text);
        totalValueText = findViewById(R.id.total_value_text);

        // === UI setup
        titleText.setText(getString(R.string.main_title_text)); // set title text

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_main, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.task_bar_main);
        taskbarHolder.addView(taskbarLayout);

        // get taskbar buttons
        ImageButton sortFilterBtn = findViewById(R.id.sort_filter_button);
        ImageButton addItemButton = findViewById(R.id.add_item_button);
        ImageButton deleteItemButton = findViewById(R.id.delete_item_button);
        ImageButton profileBtn = findViewById(R.id.profile_button);

        // === set up itemList owned by logged in user
        // TODO: this is a string array for now, fix
        itemListData = new ArrayList<InventoryItem>();
        // retrieve data passed from SortFilterActivity: itemListData and sortBy
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

        // connect itemList to Firestore database
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);
        ListenerRegistration registration = repo.setupInventoryItemList(itemListAdapter, this); // set up listener for getting Firestore data

        if (sortBy != null && sortOrder != null) {
            // should always trigger if coming from SortFilterActivity
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

        calcValue(); // Get total estimated value

        resetSelectedItems();

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

        sortFilterBtn.setOnClickListener((v) -> {
            Intent sortFilterIntent = new Intent(MainActivity.this, SortFilterActivity.class);
            if (completeItemList == null) {
                sortFilterIntent.putExtra("itemListData", itemListData);
            }
            else {
                sortFilterIntent.putExtra("itemListData", completeItemList);
            }
            MainActivity.this.startActivity(sortFilterIntent);
        });


        addItemButton.setOnClickListener((v) -> {
            Intent addIntent = new Intent(MainActivity.this, EditActivity.class);
            // intent launched without an InventoryItem
            addIntent.putExtra("user", currentUser);
            startActivity(addIntent);
        });


        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.size() > 0) {
                    deleteDialog();
                }
            }
        });
        
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("loggedInUser", currentUser);
                startActivity(intent);
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
                itemListAdapter.notifyDataSetChanged();
                return true;
            }
        });}
    }

    /**
     * Need to asynchronously call an update routine when items are added to the list, else
     * value will be calculated before items
     */
    public void onItemListUpdate() {
        this.calcValue();
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
     * Add an inventory item retrieved from the database to the data list
     */
    public void onGetItemListData(InventoryItem item) {
        itemListData.add(item);
    }

}
            