package com.example.omninventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hold all data stored in fields for each inventory item.
 */

public class InventoryItem implements Serializable {

    private String firebaseId;
    private String name;
    private String description;
    private String comment;
    private String make;
    private String model;
    private String serialno;
    private Integer value; // TODO: firestore only stores numeric values as longs; should we just use longs for value?
    private Date date;
    private ArrayList<Object> tags; // placeholder
    private ArrayList<Object> images; // placeholder

    public InventoryItem() {
        // empty constructor, initialize everything with default values
        this.firebaseId = null;
        this.name = "";
        this.description = "";
        this.comment = "";
        this.make = "";
        this.model = "";
        this.serialno = "";
        this.value = 0;
        this.date = new Date();
    }

    public InventoryItem(String name, String description) {
        // placeholder constructor for testing, just has name
        this.name = name;
        this.description = description;
        this.comment = "comment";
        this.make = "make";
        this.model = "model";
        this.serialno = "123";
        this.value = 0;
        this.date = new Date();
    }

    public InventoryItem(String firebaseId, String name, String description, String comment,
                         String make, String model, String serialno, Integer value, Date date) {
        // full constructor
        this.firebaseId = firebaseId;
        this.name = name;
        this.description = description;
        this.comment = comment;
        this.make = make;
        this.model = model;
        this.serialno = serialno;
        this.value = value;
        this.date = date;
        // TODO: tags & images
    }

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

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTagsString() {
        return "#placeholder #tags";
    }
}
