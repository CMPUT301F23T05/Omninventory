package com.example.omninventory;

/**
 * Interface for a class that can receive ItemImage data from Firebase Storage. Necessary because
 * we want a DetailsActivity and EditActivity be able to receive images one-at-a-time and
 * asynchronously as they are received from Firebase Storage.
 * @author Castor
 */
public interface ImageDownloadHandler {

    /**
     * Called by InventoryRepository when an image is successfully downloaded.
     * @param pos Position of ItemImage in the InventoryItem's images array.
     * @param image The ItemImage, with downloaded URI.
     */
    void onImageDownload(int pos, ItemImage image);

    /**
     * Called by InventoryRepository when an image download fails, usually because the image is
     * still in the process of being uploaded. The class implementing this interface can
     * just make another call to try to download the image again, or handle it some other way.
     * @param pos Position of ItemImage in the InventoryItem's images array.
     */
    void onImageDownloadFailed(int pos);
}
