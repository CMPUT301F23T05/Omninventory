package com.example.omninventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

public class Tag implements Serializable {
    private String name;
    private ArrayList<String> items;

    public Tag(String name) {
        this.name = name;
        this.items = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getItemsRefs() {
        return items;
    }

    public int getItemCount() {
        return items.size();
    }

    public void addItem(String item) {
        items.add(item);
    }



}
