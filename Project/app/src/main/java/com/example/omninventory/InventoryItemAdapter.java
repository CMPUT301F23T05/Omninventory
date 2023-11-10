package com.example.omninventory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter that works with InventoryItem objects. Uses item_list_content.xml
 * for layout display of InventoryItems in a ListView.
 *
 * @author Castor
 */
public class InventoryItemAdapter extends ArrayAdapter<InventoryItem> {

    private ArrayList<InventoryItem> itemListData;
    private Context context;

    /**
     * Constructor that takes in necessary parameters for an ArrayAdapter.
     * @param context Context for the ArrayAdapter.
     * @param items   ArrayList to use to set up the ArrayAdapter.
     */
    public InventoryItemAdapter(Context context, ArrayList<InventoryItem> items) {
        super(context, 0, items);
        this.itemListData = items;
        this.context = context;
    }

    /**
     * Sets up the UI for a list element in the ArrayAdapter.
     * @param position
     * @param convertView
     * @param parent
     * @return The View for this list element.
     */
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

        // === set up colours

        // get colours so selection works with theme
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();

        theme.resolveAttribute(com.google.android.material.R.attr.colorSurface, typedValue, true);
        @ColorInt int colorNotSelected = typedValue.data;

        theme.resolveAttribute(com.google.android.material.R.attr.colorSurfaceVariant, typedValue, true);
        @ColorInt int colorSelected = typedValue.data;

        // set colour to reflect selection
        if (item.isSelected()) {
            view.setBackgroundColor(colorSelected);
        } else {
            view.setBackgroundColor(colorNotSelected);
        }

        // === set fields for this item
        itemNameText.setText(item.getName());
        itemDescriptionText.setText(item.getDescription());

        return view;
    }
}
