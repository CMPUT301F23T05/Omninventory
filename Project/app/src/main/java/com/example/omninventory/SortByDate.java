package com.example.omninventory;

import java.util.Comparator;

public class SortByDate implements Comparator<InventoryItem>  {
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        return ie1.getDate().toCalendar().compareTo(ie2.getDate().toCalendar());
    }
}
