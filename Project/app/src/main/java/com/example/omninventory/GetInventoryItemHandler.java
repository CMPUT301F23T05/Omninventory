package com.example.omninventory;

/**
 * Interface for a class that can receive InventoryItem data from Firebase.
 * Also see InventoryRepository.getInventoryItem.
 */
public interface GetInventoryItemHandler {
    void onGetInventoryItem(InventoryItem item);
}
