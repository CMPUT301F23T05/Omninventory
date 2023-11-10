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

public class TestItems {

    /**
     * Run the add item and delete item test before running this class
     */
    public void generateTestItems(){
        //todo: add more information to items being tested

        //adds a set of default items to be tested on to the database
        //these test items should have all necessary info different tags, photos, barcodes, and serial numbers

        //----- item 1 -----
        onView(allOf(withId(R.id.add_item_button), isDisplayed())).perform(click());
        //Item1 information
        onView(withId(R.id.item_name_edittext)).perform(ViewActions.
                typeText("TestItem1"));

        onView(withId(R.id.save_button)).perform(click());

        //----- item 2 -----
        onView(allOf(withId(R.id.add_item_button), isDisplayed())).perform(click());
        //Item1 information
        onView(withId(R.id.item_name_edittext)).perform(ViewActions.
                typeText("TestItem2"));

        onView(withId(R.id.save_button)).perform(click());

        //----- item 3 -----
        onView(allOf(withId(R.id.add_item_button), isDisplayed())).perform(click());
        //Item1 information
        onView(withId(R.id.item_name_edittext)).perform(ViewActions.
                typeText("TestItem2"));

        onView(withId(R.id.save_button)).perform(click());
    }

    /**
     * Deletes test items generated for test cases
     * @author Kevin
     */
    public void wipeTestItems(){
        //todo: change to delete item if it exists

        //Clear generated test items from the database
        onView(withText("TestItem1")).perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        onView(withText("TestItem2")).perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        onView(withText("TestItem3")).perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());
    }

}
