package com.example.omninventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for filtering list of items.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestFilterItemActivity {

    TestItems testItems = new TestItems();

    @Before
    public void setup(){
        testItems.generateTestItems();
    }

    @After
    public void cleanup(){
        testItems.wipeTestItems();
    }

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

        //validate the items are filtered by make.
        onView(withText("TestItem1")).check(matches(isDisplayed()));
        onView(withText("TestItem2")).check(matches(isDisplayed()));
        onView(withText("TestItem3")).check(doesNotExist());

    }

    @Test
    public void testFilterByDate(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by date button enter the two dates to be filtered by
        //Select the filter by date button.
        //validate the items are filtered between the dates.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByDateThroughDateButton(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //Use the date buttons beside the input box above the input by date button
            //select the date to be filter by
        //validate that the dates filled to InputBox are same as the dates selected
        //Select the filter by date button.
        //validate the items are filtered between the dates.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByOnlyStartDate(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by date button enter the start dates to be filtered by
        //Select the filter by date button.
        //validate the items are filtered between the start date and today.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByOnlyEndDate(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by date button enter the two dates to be filtered by
        //Select the filter by date button.
        //validate the items are filtered between start of universe and end date.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByDescription(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //In the InputBox above the filter by description button enter the description to be filtered by
        //Select the filter by description button.
        //validate the items are filtered by the description.
        testItems.wipeTestItems();
    }

    @Test
    public void testFilterByTags(){
        testItems.generateTestItems();
        //From the inventory screen, select the sort/filter button
        //Select the filter by tags button
        //Select a set of tags to be filtered by
        //validate the items are filtered by tags selected.
        testItems.wipeTestItems();
    }

}
