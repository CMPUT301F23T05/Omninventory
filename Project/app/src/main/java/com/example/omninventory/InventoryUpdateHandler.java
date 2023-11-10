package com.example.omninventory;

/**
 * Interface for a class that should run some method when the whole contents of a User's inventory
 * may be updated (specifically, MainActivity, which holds a ListView showing all items). Was
 * necessary because of InventoryRepository.setupItemList(), which calls the onItemListUpdate()
 * method of classes that implement this interface.
 * @author Castor
 */
public interface InventoryUpdateHandler {
    /**
     * Interface function to be called when the contents of the inventory may have been updated, by
     * InventoryRepository.setupItemList().
     */
    void onItemListUpdate();
}
