package com.example.omninventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.action.ViewActions;
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
        testItems.generateOneTestItems();
        // start on the inventory screen click on an inventory item
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        //click the edit button
        onView(allOf(withId(R.id.edit_button), isDisplayed()))
                .perform(click());

        //in the Edit item screen change the information about a certain item
        onView(allOf(withId(R.id.item_description_edittext),isDisplayed()))
                .perform(typeText("Hello World"));

        //save the item
        onView(withId(R.id.save_button)).perform(click());

        //validate in the inventory screen that the item was edited
        onView(withText("Hello World")).check(matches(isDisplayed()));

        //click the back button and check the list view
        onView(withId(R.id.back_button)).perform(click());
        onView(withText("Hello World")).check(matches(isDisplayed()));

        testItems.cleanOneTestItem();
    }

    //todo: Add test case that covers all fields of edit item

    @Test
    public void testEditItemMultipleField(){
        testItems.generateOneTestItems();

        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        //click the edit button
        onView(allOf(withId(R.id.edit_button), isDisplayed()))
                .perform(click());

        //in the add item screen fill in the minimum required information for an item
        onView(withId(R.id.item_description_edittext)).perform(ViewActions.
                typeText("EditItemTest description test"));
        onView(withId(R.id.item_comment_edittext)).perform(ViewActions.
                typeText("EditItemTest Comment test"));

        //scroll
        onView(withId(R.id.item_serial_edittext))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        onView(withId(R.id.item_make_edittext)).perform(ViewActions.
                typeText("Make Test"));
        onView(withId(R.id.item_model_edittext)).perform(ViewActions.
                typeText("Test Model"));
        onView(withId(R.id.item_value_edittext)).perform(ViewActions.
                typeText("8.88"));
        onView(withId(R.id.item_serial_edittext)).perform(ViewActions.
                typeText("2222"));

        //add the item
        onView(withId(R.id.save_button)).perform(click());

        //check in item info
        onView(withText("EditItemTest description test")).check(matches(isDisplayed()));
        onView(withText("EditItemTest Comment test")).check(matches(isDisplayed()));
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

        testItems.cleanOneTestItem();
    }


    /**
     * Can edit an item with tag now
     */
    @Test
    public void testEditItemWithTag(){
        testItems.generateOneTestItems();
        //Start on inventory screen, click on an inventory item
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        //click the edit button
        onView(allOf(withId(R.id.edit_button), isDisplayed()))
                .perform(click());

        //in the edit item screen, click on the edit tag
        onView(withId(R.id.item_tags_button)).perform(click());

        onView(withText("important"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.confirm_tags_button)).perform(click());

        onView(withId(R.id.save_button)).perform(click());

        try {
            Thread.sleep(2000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        // Now check if the TextView contains the expected tag
        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#important"))));

        try {
            Thread.sleep(2000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        onView(withId(R.id.back_button)).perform(click());

        //apply the changes
        //validate from the inventory screen that the item's tags are changed
        testItems.cleanOneTestItem();
    }


    /**
     * Not yet implemented
     */
    @Test
    public void testEditMultipleItemWithTags(){
        testItems.generateTestItems();
        //Start on inventory screen, Select multiple inventory items
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(longClick());
        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(longClick());
        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(longClick());

        //click tags
        onView(withId(R.id.tag_button)).perform(click());

        //add new tags to the item
        onView(withText("important"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("shared"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.confirm_tags_button)).perform(click());

        try {
            Thread.sleep(2000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        //validate tags exist
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#important"))));
        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#shared"))));

        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#important"))));
        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#shared"))));

        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#important"))));
        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#shared"))));

        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        onView(withId(R.id.back_button)).perform(click());

        //remove common tags from item

        /**
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(longClick());
        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(longClick());
        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(longClick());

        //click tags
        onView(withId(R.id.tag_button)).perform(click());
         */


        //apply the changes
        //validate from the inventory screen that the item's tags are changed.
        testItems.wipeTestItems();
    }

}


