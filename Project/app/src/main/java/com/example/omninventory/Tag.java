package com.example.omninventory;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stores tag name and list of items tagged with that tag
 */
public class Tag implements Serializable {
    private String name;
    private ArrayList<String> itemIds;

    /**
     * Basic constructor to initialize a new tag that's not applied to anything yet.
     * @param name
     */
    public Tag(String name) {
        this.name = name;
        this.itemIds = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getItemIds() {
        return itemIds;
    }

    public int getItemCount() {
        return itemIds.size();
    }

    public void addItem(String itemId) {
        itemIds.add(itemId);
    }



}
