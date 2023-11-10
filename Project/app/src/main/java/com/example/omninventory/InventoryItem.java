package com.example.omninventory;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Holds all data stored in fields for each inventory item.
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
    private ArrayList<String> tags;
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
     * Placeholder constructor for testing that initializes fields with just a name and description.
     * @param name
     * @param description
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
        this.tags = new ArrayList<>();
        // TODO: images

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
                         String make, String model, String serialNo, ItemValue value, ItemDate date, ArrayList<String> tags) {
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
        this.tags = tags;
        // TODO: images
        this.isSelected = false;
    }

    /**
     * Convert fields of an InventoryItem into a HashMap for writing to Firebase.
     * Note that item.firebaseId is not stored in the HashMap.
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
        itemData.put("tags", this.getTags());
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

    public void addTag(String tagName) { tags.add(tagName); }
    public void setTags(ArrayList<String> tags) { this.tags = tags; }

    public ArrayList<String> getTags() { return tags; }

    /**
     * A method to generate a space-separated list of #-preceded tags as a single string for display
     * @return A String in the form "#[tag_1_name] #[tag_2_name] etc
     */
    public String getTagsString() {
        String tagString = "";
        for (int i = 0; i < tags.size(); i++) {
            tagString = String.join(" ", tagString, String.format("#%s", tags.get(i)));
        }
        return tagString;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
