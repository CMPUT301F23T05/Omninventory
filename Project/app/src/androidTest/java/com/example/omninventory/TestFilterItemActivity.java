package com.example.omninventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.widget.DatePicker;

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
 * Tests for filtering list of items.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestFilterItemActivity {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    TestItems testItems = new TestItems();

    /**
     * Setup for Filter item
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
        testItems.sleepProblemsAway(2000);

        testItems.generateTestItems();
    }

    /**
     * Cleanup for Filter item
     * @author Kevin
     */
    @After
    public void cleanup(){
        testItems.sleepProblemsAway(500);
        testItems.wipeTestItems();
    }

    /**
     * test Filter by make
     * @author Kevin
     */
    @Test
    public void testFilterByMake(){

        //add data to test items
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.item_make_edittext)).perform(typeText("FilterTestA"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.item_make_edittext)).perform(typeText("FilterTestA"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.item_make_edittext)).perform(typeText("FilterTestB"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        //From the inventory screen, select the sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());

        //In the InputBox above the filter by make button enter the make to be filtered by
        onView(withId(R.id.make_filter_edit_text))
                .perform(typeText("FilterTestA"), closeSoftKeyboard());

        onView(withId(R.id.add_make_filter_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        testItems.sleepProblemsAway(500);

        //validate the items are filtered by make.
        onView(withText("TestItem1")).check(matches(isDisplayed()));
        onView(withText("TestItem2")).check(matches(isDisplayed()));
        onView(withText("TestItem3")).check(doesNotExist());

        //From the inventory screen, select the sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());
        onView(withId(R.id.add_make_filter_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

    }

    /**
     * test Filter by date
     * @author Kevin
     */
    @Test
    public void testFilterByDate(){

        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        // Click the button to open the DatePickerDialog
        onView(withId(R.id.item_date_button)).perform(click());

        // Set the date on the DatePicker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2023, 2, 22));

        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());

        //add the item
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        // Click the button to open the DatePickerDialog
        onView(withId(R.id.item_date_button)).perform(click());

        // Set the date on the DatePicker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2023, 2, 23));

        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        // Click the button to open the DatePickerDialog
        onView(withId(R.id.item_date_button)).perform(click());

        // Set the date on the DatePicker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2023, 2, 10));

        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        //From the inventory screen, select the sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());

        onView(withId(R.id.start_date_button)).perform(click());
        // Set the date on the DatePicker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2023, 2, 20));
        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.end_date_button)).perform(click());
        // Set the date on the DatePicker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2023, 2, 25));
        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.add_date_filter_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        testItems.sleepProblemsAway(500);

        //validate the items are filtered by make.
        onView(withText("TestItem1")).check(matches(isDisplayed()));
        onView(withText("TestItem2")).check(matches(isDisplayed()));
        onView(withText("TestItem3")).check(doesNotExist());

        //From the inventory screen, select the sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());
        onView(withId(R.id.add_date_filter_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
    }

    /**
     * test Filter by description
     * @author Kevin
     */
    @Test
    public void testFilterByDescription(){
        //add data to test items
        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.item_description_edittext)).perform(typeText("FilterTestA"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.item_description_edittext)).perform(typeText("FilterTestA"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.item_description_edittext)).perform(typeText("FilterTestB"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        //From the inventory screen, select the sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());

        //In the InputBox above the filter by make button enter the make to be filtered by
        onView(withId(R.id.description_filter_edit_text))
                .perform(typeText("FilterTestA"), closeSoftKeyboard());

        onView(withId(R.id.add_description_filter_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        testItems.sleepProblemsAway(500);

        //validate the items are filtered by make.
        onView(withText("TestItem1")).check(matches(isDisplayed()));
        onView(withText("TestItem2")).check(matches(isDisplayed()));
        onView(withText("TestItem3")).check(doesNotExist());

        //From the inventory screen, select the sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());
        onView(withId(R.id.add_description_filter_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());
    }

}
