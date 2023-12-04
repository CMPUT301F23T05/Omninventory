package com.example.omninventory;

import java.util.Comparator;
import java.util.Iterator;

public class SortByTags implements Comparator<InventoryItem> {
    @Override
    public int compare(InventoryItem ie1, InventoryItem ie2) {
        Iterator<Tag> iter1 = ie1.getTags().listIterator();
        Iterator<Tag> iter2 = ie2.getTags().listIterator();

        while (iter1.hasNext() && iter2.hasNext()) {
            Tag tag1 = iter1.next();
            Tag tag2 = iter2.next();
            if (tag1.getPriority() == tag2.getPriority()) {
                continue;
            } else {
                return (int) ((int) tag1.getPriority() - tag2.getPriority());
            }
        }
        if (iter1.hasNext()) {
            return 1;
        } else if (iter2.hasNext()) {
            return -1;
        } else {
            return 0;
        }
    }
}
