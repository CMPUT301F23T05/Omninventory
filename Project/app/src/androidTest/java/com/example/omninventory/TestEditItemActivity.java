package com.example.omninventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.allOf;

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

    TestItems testItems = new TestItems(); //util for tests
    boolean singleItem = false; //bool for clean up

    /**
     * Setup for edit item
     * @Author kevin
     */
    @Before
    public void setup() {

        testItems.sleepProblemsAway(1000);

        onView(withId(R.id.login_username_edit_text)).perform(typeText("Tester"));
        onView(withId(R.id.login_password_edit_text))
                .perform(typeText("Hahaha123!"), closeSoftKeyboard());
        onView(withId(R.id.login_btn)).perform(click());

        //allow app to load in time
        testItems.sleepProblemsAway(2000);
    }

    /**
     * Clean up for edititem
     * @author Kevin
     */
    @After
    public void cleanup(){
        if(singleItem){
            testItems.cleanOneTestItem();
        }else{
            testItems.wipeTestItems();
        }
    }

    /**
     * Test case for base level edit item (only description)
     * @author Kevin
     */
    @Test
    public void testEditItemBase(){
        testItems.generateOneTestItems();
        singleItem = true;
        // start on the inventory screen click on an inventory item
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        testItems.sleepProblemsAway(200);

        //click the edit button
        onView(allOf(withId(R.id.edit_button), isDisplayed()))
                .perform(click());

        testItems.sleepProblemsAway(200);

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

    }

    /**
     * Test case for editing multiple fields of item
     * @author Kevin
     */
    @Test
    public void testEditItemMultipleField(){
        testItems.generateOneTestItems();
        singleItem =true;

        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        testItems.sleepProblemsAway(200);

        //click the edit button
        onView(allOf(withId(R.id.edit_button), isDisplayed()))
                .perform(click());

        testItems.sleepProblemsAway(200);

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

        //DATES ARE A PAINNN
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

        //check in item info
        // Optionally, verify that the date is set correctly
        onView(withId(R.id.item_date_text)).check(matches(withText("2002-02-22")));
        onView(withText("EditItemTest description test")).check(matches(isDisplayed()));
        onView(withText("EditItemTest Comment test")).check(matches(isDisplayed()));
        onView(withText("Make Test")).check(matches(isDisplayed()));
        onView(withId(R.id.item_value_text)).check(matches(withText("$8.88")));
        onView(withText("Test Model")).check(matches(isDisplayed()));

        testItems.sleepProblemsAway(2000);

        onView(withId(R.id.back_button)).perform(click());

    }


    /**
     * Edit item with adding and removing tags
     * @author Kevin
     */
    @Test
    public void testEditItemWithTag(){
        testItems.generateOneTestItems();
        singleItem = true;
        //Start on inventory screen, click on an inventory item
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        //click the edit button
        onView(allOf(withId(R.id.edit_button), isDisplayed()))
                .perform(click());

        testItems.sleepProblemsAway(100);

        //in the edit item screen, click on the edit tag
        onView(withId(R.id.item_tags_button)).perform(click());

        onView(withText("important"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        testItems.sleepProblemsAway(100);

        onView(withId(R.id.confirm_tags_button)).perform(click());

        onView(withId(R.id.save_button)).perform(click());

        testItems.sleepProblemsAway(1000);

        // Now check if the TextView contains the expected tag
        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#important"))));

        //---- tag exists, we can test remove tags-----
        testItems.sleepProblemsAway(1000);

        //click the edit button
        onView(allOf(withId(R.id.edit_button), isDisplayed()))
                .perform(click());

        testItems.sleepProblemsAway(100);

        //in the edit item screen, click on the edit tag
        onView(withId(R.id.item_tags_button)).perform(click());

        onView(withText("important"))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .perform(click());

        testItems.sleepProblemsAway(100);

        onView(withId(R.id.confirm_tags_button)).perform(click());

        onView(withId(R.id.save_button)).perform(click());

        testItems.sleepProblemsAway(1000);

        // Now check if the TextView contains the expected tag
        onView(withId(R.id.item_tags_text))
                .check(matches(not(withText(containsString("#important")))));

        onView(withId(R.id.back_button)).perform(click());

    }


    /**
     * Test case for adding tags to multiple items
     * @author Kevin
     */
    @Test
    public void testEditMultipleItemWithTags(){
        testItems.generateTestItems();
        singleItem = false;
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

        testItems.sleepProblemsAway(1000);

        //validate tags exist
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#important"))));
        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#shared"))));

        testItems.sleepProblemsAway(1000);

        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#important"))));
        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#shared"))));

        testItems.sleepProblemsAway(1000);

        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#important"))));
        onView(withId(R.id.item_tags_text))
                .check(matches(withText(containsString("#shared"))));

        testItems.sleepProblemsAway(1000);

        onView(withId(R.id.back_button)).perform(click());

    }

}


