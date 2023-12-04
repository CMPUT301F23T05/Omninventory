package com.example.omninventory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the Tags functionality.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestTagActivity {
    //Note that editing adding and editing tags are handled in
    // testAddItemActivity and testEditItemActivity.
    TestItems testItems;

    @Test
    public void testAddTag(){
        //From inventory screen select tags button
        //Select add tag
        //Enter in dialog box new tag name
        //click add button
        //validate new tag added
    }

    @Test
    public void testDeleteTag(){
        testItems.generateTestItems(); //Test items generated should have tags attached
        //From inventory screen select tags button
        //Select a tag and select the delete button
        //Click on delete
        //Validate the tag is deleted
        //validate items associated with the tag also has the tag removed.
    }
}
