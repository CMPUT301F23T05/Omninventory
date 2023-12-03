package com.example.omninventory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
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
     * Set this adapter's data to a default array of nulls.
     * @param n
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

        if (image != null && image.getUri() != null) {
            // set content field (image display) for this image from its URI
            // this works for both local URIs and internet firebase storage URIs
//        imageContent.setImageURI(Uri.parse(image.getUri().toString()));
            Picasso.get()
                    .load(image.getUri().toString())
                    .rotate(image.getNeededRotation())
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageContent);

//            Context context = holder.getImageView().getContext();
//            imageContent.setImageBitmap(image.getBitmap(context));
        }
        else {
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
     * @param pos
     * @param image
     */
    public void set(int pos, ItemImage image) {
        listData.set(pos, image);
        this.notifyItemChanged(pos);
    }

    public void add(ItemImage image) {
        listData.add(image);
        this.notifyItemInserted(listData.size() - 1);
    }

    public void remove(int pos) {
        listData.remove(pos); // this position's image will be deleted
        this.notifyItemRemoved(pos); // update this ItemImageAdapter
    }

    public ArrayList<ItemImage> getImageList() {
        return listData;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


}
