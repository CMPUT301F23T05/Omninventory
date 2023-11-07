package com.example.omninventory;

import java.util.Comparator;

public class SortByValue implements Comparator<InventoryItem> {
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        return ie1.getValue().compare(ie2.getValue());
    }
}
