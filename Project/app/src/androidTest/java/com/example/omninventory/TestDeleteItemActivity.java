package com.example.omninventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.Arrays;

/**
 * Tests for deleting an item.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestDeleteItemActivity {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    TestItems testItems = new TestItems();

    /**
     * Setup for delete test
     * @author Kevin
     */
    @Before
    public void setup() {

        testItems.sleepProblemsAway(1000);

        onView(withId(R.id.login_username_edit_text)).perform(typeText("Tester"));
        onView(withId(R.id.login_password_edit_text))
                .perform(typeText("Hahaha123!"), closeSoftKeyboard());
        onView(withId(R.id.login_btn)).perform(click());

        //allow app to load in time
        testItems.sleepProblemsAway(1000);
    }

    /**
     * Base test case for deleting item from database
     * @author Kevin
     */
    @Test
    public void testDeleteItem(){
        //add an item to the inventory screen
        onView(allOf(withId(R.id.add_item_button), isDisplayed())).perform(click());
        //Item1 information
        onView(withId(R.id.item_name_edittext)).perform(ViewActions.
                typeText("DeleteTest"));

        onView(withId(R.id.save_button)).perform(click());

        //In the inventory screen, select an item
        onView(withText("DeleteTest")).perform(longClick());

        //Select delete item
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        //validate the item is deleted
        onView(withText("DeleteText")).check(doesNotExist());

    }

    /**
     * Test cases for deleting multiple items at once
     * @author Kevin
     */
    @Test
    public void testDeleteMultipleItem(){
        testItems.generateTestItems();
        //In the inventory screen select multiple items, test items generated from testItem class
        onView(withText("TestItem1")).perform(longClick());
        onView(withText("TestItem2")).perform(longClick());
        onView(withText("TestItem3")).perform(longClick());

        //select delete items
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        //stall is required to update the database properly
        testItems.sleepProblemsAway(2000);

        //validate the items are deleted.
        //todo: change to check for not displayed
        onView(withText("TestItem1")).check(doesNotExist());
        onView(withText("TestItem2")).check(doesNotExist());
        onView(withText("TestItem3")).check(doesNotExist());

    }


}
