package com.example.omninventory;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stores tag name and list of items tagged with that tag.
 * @author Patrick
 */
public class Tag implements Serializable {
    private String name;
    private ArrayList<String> itemIds;

    /**
     * Basic constructor to initialize a new tag that's not applied to anything yet.
     * @param name Name of new Tag.
     */
    public Tag(String name) {
        this.name = name;
        this.itemIds = new ArrayList<>();
    }

    /**
     * Getter for name of Tag.
     * @return Name of Tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name of Tag.
     * @param name New name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for IDs of items associated with Tag.
     * @return ArrayList of IDs of items associated with Tag.
     */
    public ArrayList<String> getItemIds() {
        return itemIds;
    }

    /**
     * Gets the number of items associated with this Tag.
     * @return The number of items.
     */
    public int getItemCount() {
        return itemIds.size();
    }

    /**
     * Adds an InventoryItem (by ID) to this Tag's list of associated items.
     * @param itemId The ID of the item to add.
     */
    public void addItem(String itemId) {
        itemIds.add(itemId);
    }



}
