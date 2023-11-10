package com.example.omninventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for editing an item.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestEditItemActivity {

    //Rule to have activity start in the inventory screen
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    TestItems testItems = new TestItems();

    /**
     * Test case for base level edit item (only description)
     */
    @Test
    public void testEditItemBase(){
        testItems.generateTestItems();
        // start on the inventory screen click on an inventory item
        onView(withText("TestItem1")).perform(click());

        //click the edit button
        onView(allOf(withId(R.id.edit_button), isDisplayed()))
                .perform(click());

        //in the Edit item screen change the information about a certain item
        onView(withId(R.id.item_description_edittext)).perform(typeText("Hello World"));

        //save the item
        onView(withId(R.id.save_button)).perform(click());

        //validate in the inventory screen that the item was edited
        onView(withText("Hello World")).check(matches(isDisplayed()));

        //click the back button and check the list view
        onView(withId(R.id.back_button)).perform(click());
        onView(withText("Hello World")).check(matches(isDisplayed()));

        testItems.wipeTestItems();
    }

    //todo: Add test case that covers all fields of edit item

    /**
     * Not yet implemented
     */
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

    /**
     * Not yet implemented
     */
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

    /**
     * Not yet implemented
     */
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

    /**
     * Not yet implemented
     */
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

    /**
     * Not yet implemented
     */
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


