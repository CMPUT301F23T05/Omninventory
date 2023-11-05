package com.example.omninventory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class InventoryItemAdapter extends ArrayAdapter<InventoryItem> {

    /**
     * A custom ArrayAdapter that works with InventoryItem objects. Uses item_list_content.xml
     * for layout display of InventoryItem.
     */

    private ArrayList<InventoryItem> itemListData;
    private Context context;

    public InventoryItemAdapter(Context context, ArrayList<InventoryItem> items) {
        super(context, 0, items);
        this.itemListData = items;
        this.context = context;
    }

    public ArrayList<InventoryItem> getList() {
        return itemListData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // === setup
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_list_content, parent, false);
        }

        InventoryItem item = itemListData.get(position);
        TextView itemNameText = view.findViewById(R.id.item_name_text);
        TextView itemDescriptionText = view.findViewById(R.id.item_description_text);

        itemNameText.setText(item.getName());
        itemDescriptionText.setText(item.getDescription());

        return view;
    }
}
