package com.example.omninventory;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter that works with InventoryItem objects. Uses item_list_content.xml
 * for layout display of InventoryItems in a ListView.
 * @author Castor
 */
public class ItemImageAdapter extends ArrayAdapter<ItemImage> {

    private ArrayList<ItemImage> listData;
    private Context context;

    /**
     * Constructor that takes in necessary parameters for an ArrayAdapter.
     * @param context Context for the ArrayAdapter.
     * @param images  ArrayList to use to set up the ArrayAdapter.
     */
    public ItemImageAdapter(Context context, ArrayList<ItemImage> images) {
        super(context, 0, images);
        this.listData = images;
        this.context = context;
    }

    /**
     * Sets up the UI for a list element in the ArrayAdapter.
     * @param position     Position of list element.
     * @param convertView  View to inflate.
     * @param parent       Parent ViewGroup of this view.
     * @return The View for this list element.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // === setup view
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.image_list_content, parent, false);
        }

        // === find views
        ImageView imageContent = view.findViewById(R.id.image_content);
        ImageButton imageDeleteButton = view.findViewById(R.id.image_delete_button);

        // === UI setup
        ItemImage image = listData.get(position); // get item at this position

        // set content field (image display) for this image from its URI
        imageContent.setImageURI(Uri.parse(image.getUri().toString()));

        // set up onclick listener for deleting this image
        imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("ItemImageAdapter", String.format("Deleting image at position %d", position));
                listData.remove(position); // this position's image will be deleted
                ItemImageAdapter.this.notifyDataSetChanged(); // update this ItemImageAdapter
            }
        });

        return view;
    }
}
