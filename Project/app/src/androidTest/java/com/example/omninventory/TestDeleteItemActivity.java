package com.example.omninventory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestDeleteItemActivity {

    TestItems testItems;


    public void testDeleteItem(){
        testItems.generateTestItems();
        //In the inventory screen, select an item
        //Select delete item
        //validate the item is deleted
        testItems.wipeTestItems();
    }

    public void testDeleteMultipleItem(){
        testItems.generateTestItems();
        //In the inventory screen select multiple items
        //select delete items
        //validate the items are deleted.
        testItems.wipeTestItems();
    }


}
