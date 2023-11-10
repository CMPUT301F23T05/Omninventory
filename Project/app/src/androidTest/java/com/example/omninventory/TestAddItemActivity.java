package com.example.omninventory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static java.lang.Thread.sleep;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for adding an item.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestAddItemActivity {

    //Rule to have activity start in the inventory screen
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * PLEASE ONLY USE "AddItemTest" as the name for testing item.
     */

    /**
     * Clean up for the item added during test
     */
    private void cleanup(){
        onView(withText("AddItemTest")).perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());
    }

    /**
     * Test for the base case of adding an item
     */
    @Test
    public void testAddItemBase() throws InterruptedException {
        // start on the inventory screen click on the add button
        onView(allOf(withId(R.id.add_item_button), isDisplayed()))
                .perform(click());

        //in the add item screen fill in the minimum required information for an item
        onView(withId(R.id.item_name_edittext)).perform(ViewActions.
                typeText("AddItemTest"));

        //add the item
        onView(withId(R.id.save_button)).perform(click());

        //validate in the inventory screen that the item was added
        //may crash if the database is not reset before hand.
        //todo: update to check with onData instead
        onView(withText("AddItemTest")).check(matches(isDisplayed()));

        cleanup();
    }

    //todo: add test case for more detailed item description to cover all fields (outside of ones listed below)

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