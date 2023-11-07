package com.example.omninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
        currentUser = new User("erika", "password");

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
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

        // connect itemList to Firestore database
        itemListData = new ArrayList<InventoryItem>(); // TODO: unsure if this does anything
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);
        repo.setupInventoryItemList(itemListAdapter, this); // set up listener for getting Firestore data

        // Setup delete items dialog
        deleteDialog = new Dialog(this);

        calcValue(); // Get total estimated value

        // === Set up onClick actions

        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("MainActivity", "click");
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("item", itemListData.get(position));
                intent.putExtra("user", currentUser);
                startActivity(intent);
            }
        });

        ImageButton sortFilterBtn = findViewById(R.id.sort_filter_button);
        sortFilterBtn.setOnClickListener((v) -> {
            Intent myIntent = new Intent(MainActivity.this, SortFilterActivity.class);
            myIntent.putExtra("itemListData", itemListData);
            MainActivity.this.startActivity(myIntent);
        });

        ImageButton addItemButton = findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            // intent launched without an InventoryItem
            intent.putExtra("user", currentUser);
            startActivity(intent);
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
    }
}