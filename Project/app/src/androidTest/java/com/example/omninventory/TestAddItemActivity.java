package com.example.omninventory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.allOf;
import static java.lang.Thread.sleep;

import android.widget.DatePicker;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
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
        onView(withText("AddItemTest"))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withText("AddItemTest")).perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        //stall is required to update the database properly
        try {
            Thread.sleep(2000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

    /**
     * Test for the base case of adding an item
     */
    @Test
    public void testAddItemBase(){
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

    @Test
    public void testAddItemWithMostInfo(){
        // start on the inventory screen click on the add button
        onView(allOf(withId(R.id.add_item_button), isDisplayed()))
                .perform(click());

        //in the add item screen fill in the minimum required information for an item
        onView(withId(R.id.item_name_edittext)).perform(ViewActions.
                typeText("AddItemTest"));
        onView(withId(R.id.item_description_edittext)).perform(ViewActions.
                typeText("AddItemTest description test"));
        onView(withId(R.id.item_comment_edittext)).perform(ViewActions.
                typeText("AddItemTest Comment test"));

        //scroll
        onView(withId(R.id.item_serial_edittext))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.item_make_edittext)).perform(ViewActions.
                typeText("Make Test"));
        onView(withId(R.id.item_model_edittext)).perform(ViewActions.
                typeText("Test Model"));
        onView(withId(R.id.item_value_edittext)).perform(ViewActions.
                typeText("9.99"));
        onView(withId(R.id.item_serial_edittext)).perform(ViewActions.
                typeText("2222"));

        //add the item
        onView(withId(R.id.save_button)).perform(click());

        onView(withText("AddItemTest"))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        //validate in the inventory screen that the item was added
        //may crash if the database is not reset before hand.
        onView(withText("AddItemTest description test")).check(matches(isDisplayed()));

        onView(withText("AddItemTest")).perform(click());

        //check in item info
        onView(withText("AddItemTest description test")).check(matches(isDisplayed()));
        onView(withText("AddItemTest Comment test")).check(matches(isDisplayed()));
        onView(withText("Make Test")).check(matches(isDisplayed()));
        onView(withText("$9.99")).check(matches(isDisplayed()));
        onView(withText("Test Model")).check(matches(isDisplayed()));

        try {
            Thread.sleep(2000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        onView(withId(R.id.back_button)).perform(click());

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
    public void testTemp() {
    }

}