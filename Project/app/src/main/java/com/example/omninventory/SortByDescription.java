package com.example.omninventory;

import java.util.Comparator;

/**
 * Implements comparing an InventoryItem by Description (a String).
 * @author Zachary
 */
public class SortByDescription implements Comparator<InventoryItem> {
    /**
     * Overrides compare method to compare two InventoryItems by description.
     * @param ie1 One of two InventoryItems.
     * @param ie2 One of two InventoryItems.
     * @return A negative integer if the first InventoryItem's Description is earlier in lexicographical
     * order than the second, zero if they are equal, and a positive integer if the first is later in
     * order than the second.
     */
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        return ie1.getDescription().compareTo(ie2.getDescription());
    }
}
