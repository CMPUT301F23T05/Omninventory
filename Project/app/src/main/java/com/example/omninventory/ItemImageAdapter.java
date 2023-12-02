package com.example.omninventory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import com.squareup.picasso.Picasso;

/**
 * A custom ArrayAdapter that works with InventoryItem objects. Uses item_list_content.xml
 * for layout display of InventoryItems in a ListView.
 * @author Castor
 */
public class ItemImageAdapter extends RecyclerView.Adapter<ItemImageAdapter.ViewHolder> {

    private ArrayList<ItemImage> listData;

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
        ImageButton imageDeleteButton;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imageContent = view.findViewById(R.id.image_content);
            imageDeleteButton = view.findViewById(R.id.image_delete_button);
        }

        public ImageView getImageView() {
            return imageContent;
        }
        public ImageButton getImageDeleteButton() {
            return imageDeleteButton;
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
        ImageButton imageDeleteButton = holder.getImageDeleteButton();

        ItemImage image = listData.get(position); // get item at this position

        // set content field (image display) for this image from its URI
        // this works for both local URIs and internet firebase storage URIs
//        imageContent.setImageURI(Uri.parse(image.getUri().toString()));
        Picasso.get()
                .load(image.getUri().toString())
                .into(imageContent);

        // set up onclick listener for deleting this image
        imageDeleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                Log.d("ItemImageAdapter", String.format("Deleting image at position %d", pos));
                listData.remove(pos); // this position's image will be deleted
                ItemImageAdapter.this.notifyItemRemoved(pos); // update this ItemImageAdapter
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
