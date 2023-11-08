package com.example.omninventory;

import java.util.Comparator;

public class SortByMake implements Comparator<InventoryItem> {
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        return ie1.getMake().compareTo(ie2.getMake());
    }
}
