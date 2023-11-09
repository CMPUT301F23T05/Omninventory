package com.example.omninventory;

import java.util.Comparator;

public class SortByDescription implements Comparator<InventoryItem> {
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        return ie1.getDescription().compareTo(ie2.getDescription());
    }
}
