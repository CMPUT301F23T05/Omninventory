package com.example.omninventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Hold all data stored in fields for each inventory item.
 */

InventoryItem implements Serializable {
    private String username;
    private String name;
    private String description;
    private String comment;
    private String make;
    private String model;
    private String serialno;
    private Integer value;
    private Date date;
    private ArrayList<Object> tags; // placeholder
    private ArrayList<Object> images; // placeholder

    public InventoryItem(String name, String description) {
        // placeholder constructor for testing, just has name
        this.name = name;
        this.description = description;
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
}
