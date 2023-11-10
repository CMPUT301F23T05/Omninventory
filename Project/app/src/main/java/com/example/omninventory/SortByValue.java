package com.example.omninventory;

import java.util.Comparator;

/**
 * Implements comparing an InventoryItem by estimated value (an ItemValue).
 * @author Zachary
 */
public class SortByValue implements Comparator<InventoryItem> {
    /**
     * Overrides compare method to compare two InventoryItems by value.
     * @param ie1 One of two InventoryItems.
     * @param ie2 One of two InventoryItems.
     * @return A negative integer if the first InventoryItem has a lower value than the second, zero
     *         if the two are of equal value, and a positive integer if the first is greater than the
     *         second.
     */
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        return ie1.getValue().compare(ie2.getValue());
    }
}
