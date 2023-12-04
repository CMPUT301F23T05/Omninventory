package com.example.omninventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.fail;
import static org.hamcrest.Matchers.allOf;

import static java.lang.Thread.sleep;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test class that sets up default database InventoryItem data for testing.
 * @author Kevin
 */
public class TestItems {

    /**
     * Run the add item and delete item test before running this class
     * @author Kevin
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
                typeText("TestItem3"));

        onView(withId(R.id.save_button)).perform(click());
        sleepProblemsAway(100);
    }

    /**
     * Deletes test items generated for test cases
     * @author Kevin
     */
    public void wipeTestItems() {
        //todo: change to delete item if it exists

        //Clear generated test items from the database
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        sleepProblemsAway(100);

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        sleepProblemsAway(100);

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        sleepProblemsAway(100);

    }

    /**
     * Generate one test item
     * @author Kevin
     */
    public void generateOneTestItems() {
        //todo: add more information to items being tested

        //adds a set of default items to be tested on to the database
        //these test items should have all necessary info different tags, photos, barcodes, and serial numbers

        //----- item 1 -----
        onView(allOf(withId(R.id.add_item_button), isDisplayed())).perform(click());
        //Item1 information
        onView(withId(R.id.item_name_edittext)).perform(ViewActions.
                typeText("TestItem1"));

        onView(withId(R.id.save_button)).perform(click());
    }

    /**
     * Remove the one test item generated
     * @author Kevin
     */
    public void cleanOneTestItem(){
        //Clear generated test items from the database
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(longClick());
        onView(allOf(withId(R.id.delete_item_button), isDisplayed()))
                .perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());

        sleepProblemsAway(2000);
    }

    /**
     * A lot of tests run too fast. Need to halt it to allow program to load in time.
     * @param sleepTime
     * @author Kevin
     */
    public void sleepProblemsAway(int sleepTime){
        try {
            Thread.sleep(sleepTime); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }

}
