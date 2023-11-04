package com.example.omninventory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestFilterItemActivity {

    TestItems testItems;

    @Test
    public void testFilterByMake(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by make button enter the make to be filtered by
        //Select the filter by make button.
        //validate the items are filtered by make.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByDate(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by date button enter the two dates to be filtered by
        //Select the filter by date button.
        //validate the items are filtered between the dates.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByDateThroughDateButton(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //Use the date buttons beside the input box above the input by date button
            //select the date to be filter by
        //validate that the dates filled to InputBox are same as the dates selected
        //Select the filter by date button.
        //validate the items are filtered between the dates.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByOnlyStartDate(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by date button enter the start dates to be filtered by
        //Select the filter by date button.
        //validate the items are filtered between the start date and today.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByOnlyEndDate(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by date button enter the two dates to be filtered by
        //Select the filter by date button.
        //validate the items are filtered between start of universe and end date.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByDescription(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by description button enter the description to be filtered by
        //Select the filter by description button.
        //validate the items are filtered by the description.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByTags(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //Select the filter by tags button
        //Select a set of tags to be filtered by
        //validate the items are filtered by tags selected.
        testItems.wipeTestItems();
    }

}
