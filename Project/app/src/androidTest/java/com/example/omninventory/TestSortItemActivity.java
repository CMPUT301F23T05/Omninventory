package com.example.omninventory;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for sorting the list of items.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSortItemActivity {

    TestItems testItems;

    @Test
    public void testSortItemByDate(){
        testItems.generateTestItems();
        //From inventory screen select sort/filter button
        //Select the sort option dropdown
        //Select Date
        //Select go back button
        //validate that items are sorted by date ASC.
        testItems.wipeTestItems();
    }

    @Test
    public void testSortItemDESCByDate(){
        testItems.generateTestItems();
        //From inventory screen select sort/filter button
        //Select the sort option dropdown
        //Select Date
        //Select the ASC button to change sort order
        //Select go back button
        //validate that items are sorted by date DESC.
        testItems.wipeTestItems();
    }

    @Test
    public void testSortItemByDescription(){
        testItems.generateTestItems();
        //From inventory screen select sort/filter button
        //Select the sort option dropdown
        //Select Description
        //Select go back button
        //validate that items are sorted by description ASC.
        testItems.wipeTestItems();
    }

    @Test
    public void testSortItemByMake(){
        testItems.generateTestItems();
        //From inventory screen select sort/filter button
        //Select the sort option dropdown
        //Select Make
        //Select go back button
        //validate that items are sorted by Make ASC.
        testItems.wipeTestItems();
    }

    @Test
    public void testSortItemByEstValue(){
        testItems.generateTestItems();
        //From inventory screen select sort/filter button
        //Select the sort option dropdown
        //Select Estimated value
        //Select go back button
        //validate that items are sorted by Estimated value ASC.
        testItems.wipeTestItems();
    }

    @Test
    public void testSortItemByTags(){

        //This test might need to be done manually
        testItems.generateTestItems();
        //From inventory screen select sort/filter button
        //Select the sort option dropdown
        //Select Tag
        //Select go back button
        //validate that items are sorted by Tag based on the determined tag sort order.
        testItems.wipeTestItems();
    }
}
