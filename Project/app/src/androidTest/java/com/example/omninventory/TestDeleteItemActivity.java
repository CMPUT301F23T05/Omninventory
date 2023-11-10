package com.example.omninventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestDeleteItemActivity {

    TestItems testItems;

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Base test case for deleting item from database
     */
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
        //todo: update to is not displayed
        onView(withText("DeleteText")).check(matches(isDisplayed()));

    }

    /**
     * Test cases for deleting multiple items at once
     * @author Kevin
     */
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

        //validate the items are deleted.
        //todo: change to check for not displayed
        onView(withText("TestItem1")).check(matches(isDisplayed()));
        onView(withText("TestItem2")).check(matches(isDisplayed()));
        onView(withText("TestItem3")).check(matches(isDisplayed()));

    }


}
