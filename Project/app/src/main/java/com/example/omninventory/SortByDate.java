package com.example.omninventory;

import java.util.Comparator;

/**
 * Implements comparing an InventoryItem by Date (an ItemDate).
 * @author Zachary
 */
public class SortByDate implements Comparator<InventoryItem>  {
    /**
     * Overrides compare method to compare two InventoryItems by date.
     * @param ie1 One of two InventoryItems.
     * @param ie2 One of two InventoryItems.
     * @return A negative integer if the first InventoryItem's ItemDate is earlier than the second,
     * zero if they are equal, and a positive integer if the first is later in order than the second.
     */
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        return ie1.getDate().toCalendar().compareTo(ie2.getDate().toCalendar());
    }
}
