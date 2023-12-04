package com.example.omninventory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static java.lang.Thread.sleep;

import android.widget.DatePicker;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
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

    TestItems testItems = new TestItems();

    /**
     * PLEASE ONLY USE "AddItemTest" as the name for testing item.
     */
    @Before
    public void setup() {
        testItems.sleepProblemsAway(500);


        onView(withId(R.id.login_username_edit_text)).perform(typeText("Tester"));
        onView(withId(R.id.login_password_edit_text))
                .perform(typeText("Hahaha123!"), closeSoftKeyboard());
        onView(withId(R.id.login_btn)).perform(click());

        //allow app to load in time
        testItems.sleepProblemsAway(2000);
    }


    /**
     * Clean up for the item added during test
     */
    @After
    public void cleanup(){
        onView(withText("AddItemTest"))
                .perform(scrollTo());

        onView(withText("AddItemTest")).perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        //stall is required to update the database properly
        testItems.sleepProblemsAway(1000);
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
        onView(withText("AddItemTest"))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

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
        onView(withId(R.id.item_model_edittext))
                .perform(ViewActions.
                typeText("Test Model"));
        onView(withId(R.id.item_value_edittext)).perform(ViewActions.
                typeText("9.99"));
        onView(withId(R.id.item_serial_edittext)).perform(ViewActions.
                typeText("2222"));

        // Click the button to open the DatePickerDialog
        onView(withId(R.id.item_date_button))
                .perform(scrollTo())
                .perform(click());

        // Set the date on the DatePicker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2002, 2, 22));

        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());

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
        onView(withText("2002-02-22")).check(matches(isDisplayed()));
        onView(withText("AddItemTest description test")).check(matches(isDisplayed()));
        onView(withText("AddItemTest Comment test")).check(matches(isDisplayed()));
        onView(withText("Make Test")).check(matches(isDisplayed()));
        onView(withText("$9.99")).check(matches(isDisplayed()));
        onView(withText("Test Model")).check(matches(isDisplayed()));

        testItems.sleepProblemsAway(2000);

        onView(withId(R.id.back_button)).perform(click());

    }

    //@Test
    public void testAddItemWithTag(){
        //Start on inventory screen, click on the add button

        // start on the inventory screen click on the add button
        onView(allOf(withId(R.id.add_item_button), isDisplayed()))
                .perform(click());

        //in the add item screen fill in the minimum required information for an item
        onView(withId(R.id.item_name_edittext)).perform(ViewActions.
                typeText("AddItemTest"));
        onView(withId(R.id.item_description_edittext)).perform(ViewActions.
                typeText("AddItemTest description test"));

        //add tag to item
        onView(withId(R.id.item_tags_button))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.item_tags_button)).perform(click());

        onView(withText("important"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.confirm_tags_button)).perform(click());

        onView(withId(R.id.save_button)).perform(click());

        onView(withText("AddItemTest"))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        //validate in the inventory screen that the item was added
        //may crash if the database is not reset before hand.
        onView(withText("AddItemTest description test")).check(matches(isDisplayed()));

        onView(withText("AddItemTest")).perform(click());

        testItems.sleepProblemsAway(1000);

        // Now check if the TextView contains the expected tag
        onView(withId(R.id.item_tags_text)).check(matches(withText(containsString("#important"))));

        testItems.sleepProblemsAway(1000);

        onView(withId(R.id.back_button)).perform(click());

    }

}