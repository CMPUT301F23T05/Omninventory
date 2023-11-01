package com.example.omninventory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestAddItemActivity {

    //Rule to have activity start in the inventory screen

    @Test
    public void testAddItemBase(){
        // start on the inventory screen click on the add button
        //in the add item screen fill in the minimum required information for an item
        //add the item
        //validate in the inventory screen that the item was added
    }

    @Test
    public void testAddItemWithTag(){
        //Start on inventory screen, click on the add button
        //in the add item screen, fill in the minimum required information for an item
        //add tags to the item
        //add the item
        //validate from the inventory screen that the item has tags
    }

    @Test
    public void testAddItemWithPhotos(){
        //Start on inventory screen, click on the add button
        //in the add item screen, fill in the minimum required information for an item
        //add photos to the item
        //add the item
        //validate from the inventory screen that the item has photos
    }

    @Test
    public void testAddItemWithBarcode(){
        //Start on inventory screen, click on the add button
        //in the add item screen, fill in the minimum required information for an item
        //add barcode to the item
        //add the item
        //validate from the inventory screen that the item has barcode attached
    }

    @Test
    public void testAddItemWithSerialNumber(){
        //Start on inventory screen, click on the add button
        //in the add item screen, fill in the minimum required information for an item
        //add serial number to the item
        //add the item
        //validate from the inventory screen that the item has serial number attached
    }
}
