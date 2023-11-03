package com.example.omninventory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import java.beans.Transient;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestEditItemActivity {

    //Rule to have activity start in the inventory screen
    public void addTestItems() {
        //adds a set of default items to be tested on
        //these test items should have all necessary info different tags, photos, barcodes, and serial numbers
    }


    @Test
    public void testEditItemBase(){
        addTestItems();
        // start on the inventory screen click on an inventory item
        //in the Edit item screen change the information about a certain item
        //edit the item
        //validate in the inventory screen that the item was edited
    }

    @Test
    public void testEditItemWithTag(){
        addTestItems();
        //Start on inventory screen, click on an inventory item
        //in the edit item screen, click on the edit tag
        //edit the tags on the item
        //apply the changes
        //validate from the inventory screen that the item's tags are changed
    }

    @Test
    public void testEditItemWithPhotos(){
        addTestItems();
        //Start on inventory screen, click on an inventory item
        //in the edit item screen, click on edit photos
        //change photos of the item
        //apply the changes
        //validate from the inventory screen that the item has different photos
    }

    @Test
    public void testEditItemWithBarcode(){
        addTestItems();
        //Start on inventory screen, click on an inventory item
        //in the edit item screen, click on change barcode
        //edit the barcode of the item
        //apply the changes
        //validate from the inventory screen that the barcode has changed
    }

    @Test
    public void testEditItemWithSerialNumber(){
        addTestItems();
        //Start on inventory screen, click on an inventory item
        //in the edit item screen, click on change serial number
        //edit the serial number of the item
        //apply the changes
        //validate from the inventory screen that the item has serial number has changed
    }

    @Test
    public void testEditMultipleItemWithTags(){
        addTestItems();
        //Start on inventory screen, Select multiple inventory items
        //in the edit item screen, click on edit tags
        //add new tags to the item and remove common tags from item
        //apply the changes
        //validate from the inventory screen that the item's tags are changed.
    }
}


