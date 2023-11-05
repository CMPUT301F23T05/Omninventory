package com.example.omninventory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestEditItemActivity {

    //Rule to have activity start in the inventory screen
    TestItems testItems;

    @Test
    public void testEditItemBase(){
        testItems.generateTestItems();
        // start on the inventory screen click on an inventory item
        //in the Edit item screen change the information about a certain item
        //edit the item
        //validate in the inventory screen that the item was edited
        testItems.wipeTestItems();
    }

    @Test
    public void testEditItemWithTag(){
        testItems.generateTestItems();
        //Start on inventory screen, click on an inventory item
        //in the edit item screen, click on the edit tag
        //edit the tags on the item
        //apply the changes
        //validate from the inventory screen that the item's tags are changed
        testItems.wipeTestItems();
    }

    @Test
    public void testEditItemWithPhotos(){
        testItems.generateTestItems();
        //Start on inventory screen, click on an inventory item
        //in the edit item screen, click on edit photos
        //change photos of the item
        //apply the changes
        //validate from the inventory screen that the item has different photos
        testItems.wipeTestItems();
    }

    @Test
    public void testEditItemWithBarcode(){
        testItems.generateTestItems();
        //Start on inventory screen, click on an inventory item
        //in the edit item screen, click on change barcode
        //edit the barcode of the item
        //apply the changes
        //validate from the inventory screen that the barcode has changed
        testItems.wipeTestItems();
    }

    @Test
    public void testEditItemWithSerialNumber(){
        testItems.generateTestItems();
        //Start on inventory screen, click on an inventory item
        //in the edit item screen, click on change serial number
        //edit the serial number of the item
        //apply the changes
        //validate from the inventory screen that the item has serial number has changed
        testItems.wipeTestItems();
    }

    @Test
    public void testEditMultipleItemWithTags(){
        testItems.generateTestItems();
        //Start on inventory screen, Select multiple inventory items
        //in the edit item screen, click on edit tags
        //add new tags to the item and remove common tags from item
        //apply the changes
        //validate from the inventory screen that the item's tags are changed.
        testItems.wipeTestItems();
    }

}


