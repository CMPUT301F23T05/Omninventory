package com.example.omninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Main screen of the app; holds list of inventory items and buttons
 * that take user to other screens.
 */
public class MainActivity extends AppCompatActivity {

    private ArrayList<InventoryItem> itemListData;
    private InventoryItemAdapter itemListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // === get references to Views
        final ListView itemList = findViewById(R.id.item_list);
        final TextView titleText = findViewById(R.id.title_text);

        // === UI setup
        titleText.setText(getString(R.string.main_title_text));

        // === set up itemList
        itemListData = new ArrayList<InventoryItem>();
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);

        itemListData.add(new InventoryItem("Cat", "beloved family pet"));
        itemListData.add(new InventoryItem("Laptop", "for developing android apps <3"));
        itemListData.add(new InventoryItem("301 Group Members", "their names are Castor, Patrick, Kevin, Aron, Rose, and Zachary. this item has a long name and description so we can see what that looks like"));

        // === set up onClick actions
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("MainActivity", "click");
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("item", itemListData.get(position));
                startActivity(intent);
            }
        });


        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                InventoryItem item = itemListData.get(position);
                if (item.isSelected()) {
                    item.setSelected(false);
                } else {
                    item.setSelected(true);
                }
                itemListAdapter.notifyDataSetChanged();
                return true;
            }
        });

    }
}