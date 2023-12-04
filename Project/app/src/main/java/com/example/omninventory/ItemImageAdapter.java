package com.example.omninventory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A custom RecyclerView.Adapter that works with ItemImage objects. Uses item_list_content.xml
 * for layout display of ItemImages in a RecyclerView. Has methods that handle adding, setting,
 * and removing images along with updating the contents of the ArrayAdapter.
 * @author Castor
 * @reference https://developer.android.com/develop/ui/views/layout/recyclerview
 */
public class ItemImageAdapter extends RecyclerView.Adapter<ItemImageAdapter.ViewHolder> {

    protected ArrayList<ItemImage> listData;

    /**
     * Constructor that takes in necessary parameters for an ArrayAdapter.
     * @param listData List of ItemImages to display in this Adapter.
     */
    public ItemImageAdapter(ArrayList<ItemImage> listData) {
        this.listData = listData;
    }

    /**
     * Sets this adapter's data to a default array of nulls, which are displayed as some placeholder
     * until overwritten with an ItemImage.
     * @param n Length of array.
     */
    public void resetData(int n) {
        listData = new ArrayList<ItemImage>();
        for (int i = 0; i < n; i++) {
            // by default, we have a list of placeholder images
            listData.add(null);
        }
        this.notifyDataSetChanged(); // using this because whole dataset may have been changed
    }


    /**
     * Provides a reference to View used.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageContent;

        /**
         * Constructor that gets the image content ImageView.
         * @param view
         */
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imageContent = view.findViewById(R.id.image_content);
        }

        /**
         * Getter for image content ImageView.
         * @return image content ImageView
         */
        public ImageView getImageView() {
            return imageContent;
        }
    }

    /**
     * Inflates layout for the content Views, creates a ViewHolder that can be used to access
     * its child Views.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return The ViewHolder created.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_content, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Function run to set up content Views. Displays an ItemImage.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // === find views
        ImageView imageContent = holder.getImageView();

        ItemImage image = listData.get(position); // get item at this position

        if (image != null && image.getUri() != null) {
            Log.d("ItemImageAdapter", String.format("Image %d can be displayed, uri is %s", position, image.getUri()));
            // set content field (image display) for this image from its URI
            // this works for both local URIs and internet firebase storage URIs
            Picasso.get()
                    .load(image.getUri().toString())
//                    .rotate(image.getNeededRotation())
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageContent);
        }
        else {
            Log.d("ItemImageAdapter", String.format("Image %d not loaded yet", position));
            // image not loaded yet; use placeholder
            // seemingly images usually load fast enough that this is never visible. but hey
            Picasso.get()
                    .load(R.drawable.image_placeholder)
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageContent);
        }
    }

    /**
     * Mimic set method of ArrayList to set item in adapter and update
     * @param pos Position of ItemImage in list.
     * @param image The ItemImage to set.
     */
    public void set(int pos, ItemImage image) {
        listData.set(pos, image);
        this.notifyItemChanged(pos);
    }

    /**
     * Wraps ArrayList.add to add an image to the RecyclerView and also update the proper content row.
     * @param image The ItemImage to add.
     */
    public void add(ItemImage image) {
        listData.add(image);
        this.notifyItemInserted(listData.size() - 1);
    }

    /**
     * Wraps ArrayList.remove to remove an image from the RecyclerView and also update the proper content row.
     * @param pos The position of the row to remove.
     */
    public void remove(int pos) {
        listData.remove(pos); // this position's image will be deleted
        this.notifyItemRemoved(pos); // update this ItemImageAdapter
    }

    /**
     * Getter for listData.
     * @return This ItemImageAdapter's list of ItemImages used to display content rows.
     */
    public ArrayList<ItemImage> getImageList() {
        return listData;
    }

    /**
     * Returns the count of rows displayed by this ItemImageAdapter.
     * @return The count of rows displayed.
     */
    @Override
    public int getItemCount() {
        return listData.size();
    }


}
