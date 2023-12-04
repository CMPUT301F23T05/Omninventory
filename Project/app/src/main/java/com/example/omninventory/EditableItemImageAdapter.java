package com.example.omninventory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * A custom RecyclerView.Adapter that works with ItemImage objects. Uses item_list_content.xml
 * for layout display of ItemImages in a RecyclerView. Has methods that handle adding, setting,
 * and removing images along with updating the contents of the ArrayAdapter.
 * @author Castor
 * @reference https://developer.android.com/develop/ui/views/layout/recyclerview
 */
public class EditableItemImageAdapter extends ItemImageAdapter {

    /**
     * Constructor that takes in necessary parameters for an ArrayAdapter.
     * @param listData List of ItemImages to display in this Adapter.
     */
    public EditableItemImageAdapter(ArrayList<ItemImage> listData) {
        super(listData);
    }

    /**
     * Subclasses ItemImageAdapter.ViewHolder to add a getter for the delete button.
     */
    public static class ViewHolder extends ItemImageAdapter.ViewHolder {
        ImageButton imageDeleteButton;

        /**
         * Constructor that (in addition to parent behaviour) gets the delete button ImageButton.
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            imageDeleteButton = view.findViewById(R.id.image_delete_button);
        }

        /**
         * Getter for delete button ImageButton.
         * @return image delete ImageButton
         */
        public ImageButton getImageDeleteButton() {
            return imageDeleteButton;
        }
    }

    /**
     * Overrides parent function to use image_list_content_editable.xml layout, which contains a
     * delete button.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return The ViewHolder created.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_content_editable, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Overrides parent function to add a button that will delete this item in the Adapter.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ItemImageAdapter.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        // === find views
        ImageButton imageDeleteButton = ((EditableItemImageAdapter.ViewHolder) holder).getImageDeleteButton();

        // set up onclick listener for deleting this image
        imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Log.d("ItemImageAdapter", String.format("Deleting image at position %d", pos));
                EditableItemImageAdapter.this.remove(pos); // this position's image will be deleted
            }
        });
    }
}
