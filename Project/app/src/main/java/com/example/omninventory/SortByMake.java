package com.example.omninventory;

import java.util.Comparator;

/**
 * Implements comparing an InventoryItem by Make (a String).
 * @author Zachary
 */
public class SortByMake implements Comparator<InventoryItem> {
    /**
     * Overrides compare method to compare two InventoryItems by make.
     * @param ie1 One of two InventoryItems.
     * @param ie2 One of two InventoryItems.
     * @return A negative integer if the first InventoryItem's Make is earlier in lexicographical
     * order than the second, zero if they are equal, and a positive integer if the first is later in
     * order than the second.
     */
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        return ie1.getMake().compareTo(ie2.getMake());
    }
}
