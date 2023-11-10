package com.example.omninventory;

/**
 * Interface for a class that can receive InventoryItem data from Firebase. Was necessary because
 * of InventoryRepository.getInventoryItem(), which calls the onGetInventoryItem() method of
 * classes that implement this interface.
 * @author Castor
 */
public interface GetInventoryItemHandler {
    void onGetInventoryItem(InventoryItem item);
}
