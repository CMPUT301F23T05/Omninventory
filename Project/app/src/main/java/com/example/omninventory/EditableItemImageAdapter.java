package com.example.omninventory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;

/**
 * A custom ArrayAdapter that works with InventoryItem objects. Uses item_list_content.xml
 * for layout display of InventoryItems in a ListView.
 * @author Castor
 */
public class EditableItemImageAdapter extends ItemImageAdapter {

    /**
     * Constructor that takes in necessary parameters for an ArrayAdapter.
     * @param listData
     */
    public EditableItemImageAdapter(ArrayList<ItemImage> listData) {
        super(listData);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends ItemImageAdapter.ViewHolder {
        ImageButton imageDeleteButton;

        public ViewHolder(View view) {
            super(view);
            imageDeleteButton = view.findViewById(R.id.image_delete_button);
        }

        public ImageButton getImageDeleteButton() {
            return imageDeleteButton;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_content_editable, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        // === find views
        ImageButton imageDeleteButton = holder.getImageDeleteButton();

        // set up onclick listener for deleting this image
        imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Log.d("ItemImageAdapter", String.format("Deleting image at position %d", pos));
                listData.remove(pos); // this position's image will be deleted
                EditableItemImageAdapter.this.notifyItemRemoved(pos); // update this ItemImageAdapter
            }
        });
    }
}
