package com.example.omninventory;

/**
 * Interface for a class that can receive InventoryItem data from Firebase. Was necessary because
 * of InventoryRepository.getInventoryItem(), which calls the onGetInventoryItem() method of
 * classes that implement this interface.
 * @author Castor
 */
public interface GetInventoryItemHandler {
    /**
     * Interface function to be called when an InventoryItem is received by
     * InventoryRepository.getInventoryItem().
     * @param item Item to send to implementing class.
     */
    abstract void onGetInventoryItem(InventoryItem item);
}
