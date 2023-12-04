package com.example.omninventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Stores tag name and list of items tagged with that tag.
 * @author Patrick
 */
public class Tag implements Serializable, Comparable<Tag> {
    private String id;
    private String name;
    private String owner;
    private long priority;
    private ArrayList<String> itemIds;

    private boolean isSelected;

    /**
     * Basic constructor to initialize a new tag that's not applied to anything yet.
     * @param name Name of new Tag.
     */
    public Tag(String name, String owner, long priority) {
        this.id = "";
        this.name = name;
        this.owner = owner;
        this.priority = priority;
        this.itemIds = new ArrayList<>();
        this.isSelected = false;
    }

    /**
     * Basic constructor to initialize a tag object to represent an existing tag
     * @param id Id of tag.
     * @param name Name of tag.
     * @param itemIds List of references to the items to which the tag is applied.
     */
    public Tag(String id, String name, String owner, long priority, ArrayList<String> itemIds) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.priority = priority;
        this.itemIds = itemIds;
        this.isSelected = false;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Getter for name of Tag.
     * @return Name of Tag.
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for id of Tag.
     * @return Id of Tag.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for id of Tag.
     * @param id New id.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Setter for name of Tag.
     * @param name New name.
     */
    public void setName(String name) {
        this.name = name;
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) { this.isSelected = isSelected; }

    public int compareTo(Tag t) {
        return (int) (this.priority - t.getPriority());
    }

}
