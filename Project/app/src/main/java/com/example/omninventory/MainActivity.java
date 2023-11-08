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
    private InventoryItemAdapter itemListAdapter;
    SharedPreferences sp;
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
                    // TODO: this needs to remove the item from the database as well
                    itemListData.remove(selectedItem);
                    itemListAdapter.notifyDataSetChanged();
                }
                calcValue();
                deleteDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: this is testing code, replace when merged with Rose's code
        currentUser = new User("erika", "erikausername", "password", new ArrayList<String>());

        // === set up database
        repo = new InventoryRepository();

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


        // === this will store user's login state to keep them logged in
        sp = getSharedPreferences("login", MODE_PRIVATE);

        // get taskbar buttons
        final ImageButton profileBtn = findViewById(R.id.profile_button);

        // check if user just logged in
        if (getIntent().getExtras() != null)  {
            // user just logged in
            String user = getIntent().getExtras().getString("loggedInUser");
            sp.edit().putBoolean("logged",true).apply();
            sp.edit().putString("username",user).apply();
            Log.d("login", "Logged in as: " + user);
            // todo: for testing purposes only, will remove later
            Toast.makeText(getApplicationContext(), "Logged in as , " + user, Toast.LENGTH_LONG).show();
        }

        // === set up itemList owned by logged in user
        // TODO: this is a string array for now, fix
        itemListData = new ArrayList<InventoryItem>();
        // retrieve data passed from SortFilterActivity: itemListData and sortBy

        Intent intent = getIntent();
        if (intent != null) {
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
    
        // Setup delete items dialog
        deleteDialog = new Dialog(this);

        calcValue(); // Get total estimated value

        // === Set up onClick actions
        //itemListData.add(new InventoryItem("Cat"));

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("MainActivity", "click");
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                detailsIntent.putExtra("item", itemListData.get(position));
                detailsIntent.putExtra("user", currentUser);
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
                if (selectedItems.size() > 0) { deleteDialog();}
            }
        });
        
        profileBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // todo: this logs out the current user, for testing only, will remove this later
                sp.edit().putBoolean("logged",false).apply();
                // check if user is logged in
                if (!sp.getBoolean("logged",false)) {
                    startLoginActivity();
                }
                else {
                    // start ProfileActivity
                }
            }
        });

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                InventoryItem item = itemListData.get(position);
                if (item.isSelected()) {
                    item.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                    selectedItems.remove(item);
                } else {
                    item.setSelected(true);
                    view.setBackgroundColor(Color.LTGRAY);
                    selectedItems.add(item);
                }
                itemListAdapter.notifyDataSetChanged();
                return true;
            }
        });
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
            