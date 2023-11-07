package com.example.omninventory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter that works with InventoryItem objects. Uses item_list_content.xml
 * for layout display of InventoryItems in a ListView.
 */
public class InventoryItemAdapter extends ArrayAdapter<InventoryItem> {

    private ArrayList<InventoryItem> itemListData;
    private Context context;

    public InventoryItemAdapter(Context context, ArrayList<InventoryItem> items) {
        super(context, 0, items);
        this.itemListData = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // === setup view
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list_content, parent, false);
        }

        // === find views
        TextView itemNameText = view.findViewById(R.id.item_name_text);
        TextView itemDescriptionText = view.findViewById(R.id.item_description_text);

        // === UI setup
        InventoryItem item = itemListData.get(position); // get item at this position

        // set fields
        itemNameText.setText(item.getName());
        itemDescriptionText.setText(item.getDescription());

        return view;
    }
}
