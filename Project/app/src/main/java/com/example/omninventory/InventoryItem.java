package com.example.omninventory;

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
    private ArrayList<Object> tags; // placeholder
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
        // TODO: tags & images

        this.isSelected = false;
    }

    /**
     * Placeholder constructor for testing that initializes fields with just a name and description.
     * @param name The name of the item.
     * @param description A short description of the item.
     */
    public InventoryItem(String FirebaseId, String name, String description, String comment,
                         String make, String model, String serialno, Integer value, Date date) {
        // placeholder constructor for testing, just has name
        this.name = name;
        this.description = description;
        this.comment = "comment";
        this.make = "make";
        this.model = "model";
        this.serialNo = "123";
        this.value = new ItemValue(0);
        this.date = new ItemDate(new Date());
        // TODO: tags & images

        this.isSelected = false;
    }

    /**
     * Full constructor.
     * @param firebaseId
     * @param name
     * @param description
     * @param comment
     * @param make
     * @param model
     * @param serialNo
     * @param value
     * @param date
     */
    public InventoryItem(String firebaseId, String name, String description, String comment,
                         String make, String model, String serialNo, ItemValue value, ItemDate date) {
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
        // TODO: tags & images

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
        // TODO: tags and images
        return itemData;
    }

    // ============== getters and setters ================
    public String getFirebaseId() {
        return firebaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public ItemValue getValue() {
        return value;
    }

    public void setValue(ItemValue value) {
        this.value = value;
    }

    public ItemDate getDate() {
        return date;
    }

    public void setDate(ItemDate date) {
        this.date = date;
    }

    public String getTagsString() {
        return "#placeholder #tags";
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
