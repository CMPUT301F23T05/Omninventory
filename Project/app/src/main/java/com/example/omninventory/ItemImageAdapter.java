package com.example.omninventory;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A custom ArrayAdapter that works with InventoryItem objects. Uses item_list_content.xml
 * for layout display of InventoryItems in a ListView.
 * @author Castor
 */
public class ItemImageAdapter extends RecyclerView.Adapter<ItemImageAdapter.ViewHolder> {

    protected ArrayList<ItemImage> listData;

    /**
     * Constructor that takes in necessary parameters for an ArrayAdapter.
     */
    public ItemImageAdapter(ArrayList<ItemImage> listData) {
        this.listData = listData;
    }


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageContent;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imageContent = view.findViewById(R.id.image_content);
        }

        public ImageView getImageView() {
            return imageContent;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // === find views
        ImageView imageContent = holder.getImageView();

        ItemImage image = listData.get(position); // get item at this position

        if (image == null) {
            // image not loaded yet; use placeholder
            // seemingly images usually load fast enough that this never gets drawn but hey
            Picasso.get()
                    .load(R.drawable.image_placeholder)
                    .into(imageContent);
        }
        else if (image.getUri() != null) {
            // set content field (image display) for this image from its URI
            // this works for both local URIs and internet firebase storage URIs
//        imageContent.setImageURI(Uri.parse(image.getUri().toString()));
            Picasso.get()
                    .load(image.getUri().toString())
                    .into(imageContent);
        }
        else {
            // placeholder again
            Picasso.get()
                    .load(R.drawable.image_placeholder)
                    .into(imageContent);
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
