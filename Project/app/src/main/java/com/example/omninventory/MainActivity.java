package com.example.omninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Main screen of the app; holds list of inventory items and buttons
 * that take user to other screens.
 */
public class MainActivity extends AppCompatActivity {

    private ListView itemList;
    private ArrayList<InventoryItem> itemListData;
    private InventoryItemAdapter itemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // === get references to Views
        itemList = findViewById(R.id.item_list);

        // === set up itemList
        // TODO: this is a string array for now, fix
        itemListData = new ArrayList<InventoryItem>();
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);

        itemListData.add(new InventoryItem("Cat"));

        // === set up onClick actions
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("MainActivity", "click");

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

    }
}