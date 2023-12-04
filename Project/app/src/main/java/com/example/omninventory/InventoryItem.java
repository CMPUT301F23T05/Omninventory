package com.example.omninventory;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Class representing an item stored in the inventory. Holds all data stored in fields for each
 * inventory item.
 * @author Aron
 * @author Castor
 * @author Rose
 */
public class InventoryItem implements Serializable {

    private String firebaseId;
    private String name;
    private String description;
    private String comment;
    private String make;
    private String model;
    private String serialNo;
    private ItemValue value;
    private ItemDate date;
    private ArrayList<Tag> tags;
    private ArrayList<Object> images; // placeholder

    private boolean isSelected;

    /**
     * Empty constructor that initializes all fields to default values.
     */
    public InventoryItem() {
        this.firebaseId = null;
        this.name = "";
        this.description = "";
        this.comment = "";
        this.make = "";
        this.model = "";
        this.serialNo = "";
        this.value = new ItemValue(0);
        this.date = new ItemDate(new Date());
        this.tags = new ArrayList<>();
        // TODO: images

        this.isSelected = false;
    }

    /**
     * Full constructor that initializes all fields of item.
     * @param firebaseId   ID of item to create.
     * @param name         Name of item to create.
     * @param description  Description of item to create.
     * @param comment      Comment of item to create.
     * @param make         Make of item to create.
     * @param model        Model of item to create.
     * @param serialNo     Serial number of item to create.
     * @param value        Estimated value of item to create (an ItemValue).
     * @param date         Date of purchase of item to create (an ItemDate).
     */
    public InventoryItem(String firebaseId, String name, String description, String comment,
                         String make, String model, String serialNo, ItemValue value, ItemDate date, ArrayList<Tag> tags) {
        // full constructor
        this.firebaseId = firebaseId;
        this.name = name;
        this.description = description;
        this.comment = comment;
        this.make = make;
        this.model = model;
        this.serialNo = serialNo;
        this.value = value;
        this.date = date;
        this.tags = (ArrayList<Tag>) tags.clone();
        // TODO: images
        this.isSelected = false;
    }

    /**
     * Convert fields of an InventoryItem into a HashMap for writing to Firebase.
     * Note that item.firebaseId is not stored in the HashMap, as it is not written with the item
     * data.
     * @return A HashMap representing this InventoryItem that may be written to Firebase.
     */
    public HashMap<String, Object> convertToHashMap() {
        HashMap<String, Object> itemData = new HashMap<>();
        itemData.put("name", this.getName());
        itemData.put("description", this.getDescription());
        itemData.put("comment", this.getComment());
        itemData.put("make", this.getMake());
        itemData.put("model", this.getModel());
        itemData.put("serialno", this.getSerialNo());
        itemData.put("value", this.getValue().toPrimitiveLong()); // convert ItemValue to long
        itemData.put("date", this.getDate().toDate()); // convert ItemDate to Date
        itemData.put("tags", this.getTagIds());
        // TODO: tags and images
        return itemData;
    }

    /**
     * A method to generate a space-separated list of #-preceded tags as a single string for display
     * @return A String in the form "#[tag_1_name] #[tag_2_name] etc
     */
    public String getTagsString() {
        String tagString = "";
        for (int i = 0; i < tags.size(); i++) {
            tagString = String.join(" ", tagString, String.format("#%s", tags.get(i).getName()));
        }
        return tagString;
    }

    /**
     * Adds a new tag to this InventoryItem's ArrayList of tags.
     * @param tag The tag to add.
     */
    public void addTag(Tag tag) { tags.add(tag); }

    /**
     * For InventoryItems in the MainActivity item list, this returns a flag describing whether or
     * not the item is currently selected (on a long press from the user).
     * @return A Boolean, 'true' if the item is selected, 'false' if not.
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Sets the flag describing whether or not the item is currently selected in the list.
     * @param isSelected A Boolean, 'true' if the item is selected, 'false' if not.
     */
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    // ============== getters and setters ================

    /**
     * Getter for the InventoryItem's ID in firebase (a randomly-generated string).
     * @return InventoryItem's ID.
     */
    public String getFirebaseId() {
        return firebaseId;
    }

    /**
     * Getter for the InventoryItem's name.
     * @return InventoryItem's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the InventoryItem's name.
     * @param name New name to use.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the InventoryItem's description.
     * @return InventoryItem's description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter for the InventoryItem's description.
     * @param description New description to use.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter for the InventoryItem's comment.
     * @return InventoryItem's comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for the InventoryItem's comment.
     * @param comment New comment to use.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Getter for the InventoryItem's make.
     * @return InventoryItem's make.
     */
    public String getMake() {
        return make;
    }

    /**
     * Setter for the InventoryItem's make.
     * @param make New make to use.
     */
    public void setMake(String make) {
        this.make = make;
    }

    /**
     * Getter for the InventoryItem's model.
     * @return InventoryItem's model.
     */
    public String getModel() {
        return model;
    }

    /**
     * Setter for the InventoryItem's model.
     * @param model New model to use.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Getter for the InventoryItem's serial number.
     * @return InventoryItem's serial number.
     */
    public String getSerialNo() {
        return serialNo;
    }

    /**
     * Setter for the InventoryItem's serial number.
     * @param serialNo New serial number to use.
     */
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    /**
     * Getter for the InventoryItem's estimated value.
     * @return InventoryItem's estimated value (an ItemValue).
     */
    public ItemValue getValue() {
        return value;
    }

    /**
     * Setter for the InventoryItem's value.
     * @param value New value to use (an ItemValue).
     */
    public void setValue(ItemValue value) {
        this.value = value;
    }

    /**
     * Getter for the InventoryItem's date of purchase.
     * @return InventoryItem's date of purchase (an ItemDate).
     */
    public ItemDate getDate() {
        return date;
    }

    /**
     * Setter for the InventoryItem's date.
     * @param date New date to use (an ItemDate).
     */
    public void setDate(ItemDate date) {
        this.date = date;
    }

    /**
     * Setter for the InventoryItem's tags.
     * @param tags New tags.
     */
    public void setTags(ArrayList<Tag> tags) { this.tags = tags; }

    /**
     * Getter for the InventoryItem's tags.
     * @return InventoryItem's tags (an ArrayList of String objects)
     */
    public ArrayList<Tag> getTags() { return tags; }

    public ArrayList<String> getTagNames() {
        ArrayList<String> tagNames = new ArrayList<>();
        this.tags.forEach(tag -> {
            tagNames.add(tag.getName());
        });
        return tagNames;
    }

    public ArrayList<String> getTagIds() {
        ArrayList<String> tagIds = new ArrayList<>();
        this.tags.forEach(tag -> {
            tagIds.add(tag.getId());
        });
        return tagIds;
    }
}
