package com.example.omninventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class InventoryItemAdapter extends ArrayAdapter<InventoryItem> {

    /**
     * A custom ArrayAdapter that works with InventoryItem objects. Uses item_list_content.xml
     * for layout display of InventoryItem.
     */

    private ArrayList<InventoryItem> inventoryItemList;
    private Context context;

    public InventoryItemAdapter(Context context, ArrayList<InventoryItem> items) {
        super(context, 0, items);
        this.inventoryItemList = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // === setup
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list_content, parent, false);
        }

        return view;
    }
}
