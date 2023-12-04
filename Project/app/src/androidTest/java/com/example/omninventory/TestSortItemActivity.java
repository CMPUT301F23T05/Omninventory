package com.example.omninventory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import android.widget.DatePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
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
 * Tests for sorting the list of items.
 * NOTE, FILTERS MUST WORK FOR THESE TESTS TO WORK, TEST FILTERS FIRST
 *
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSortItemActivity {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    TestItems testItems = new TestItems();

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

    @After
    public void cleanup(){
        testItems.wipeTestItems();
    }

    @Test
    public void testSortItemByDate(){
        testItems.generateTestItems();
        //add dates to each test item

        testItems.sleepProblemsAway(100);

        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.edit_button)).perform(click());

        // Click the button to open the DatePickerDialog
        onView(withId(R.id.item_date_button)).perform(click());

        // Set the date on the DatePicker
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2002, 2, 22));
        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.item_description_edittext)).perform(typeText("TestItem"));
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
                .perform(PickerActions.setDate(2003, 2, 22));
        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.item_description_edittext)).perform(typeText("TestItem"));
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
                .perform(PickerActions.setDate(2001, 2, 22));
        // Click the OK button on the dialog
        onView(withId(android.R.id.button1)).perform(click());

        onView(withId(R.id.item_description_edittext)).perform(typeText("TestItem"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        //Expected order would be testitem3, testitem1, testitem2
        //From inventory screen select sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());

        //Select the sort option dropdown
        onView(withId(R.id.sort_dropdown_spinner)).perform(click());

        //Select Date
        // Select the "Date" sort option from the dropdown
        onData(allOf(is(instanceOf(String.class)), is("Date")))
                .inRoot(isPlatformPopup()).perform(click());

        //add a description filter to test sort for only certain items
        onView(withId(R.id.description_filter_edit_text))
                .perform(typeText("TestItem"), closeSoftKeyboard());
        onView(withId(R.id.add_description_filter_button)).perform(click());

        //Select go back button
        onView(withId(R.id.back_button)).perform(click());

        //validate that items are sorted by date ASC.
        // Scroll to the first expected item and verify its position
        onData(anything())
                .inAdapterView(withId(R.id.item_list)) // Replace with the actual ID of your ListView
                .atPosition(0)
                .onChildView(withId(R.id.item_name_text)) // Replace with the actual ID of the TextView within the list item
                .check(matches(withText("TestItem3")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(1)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem1")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem2")));

        //Test DESC
        onView(withId(R.id.sort_filter_button)).perform(click());
        onView(withId(R.id.asc_desc_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onData(anything())
                .inAdapterView(withId(R.id.item_list)) // Replace with the actual ID of your ListView
                .atPosition(0)
                .onChildView(withId(R.id.item_name_text)) // Replace with the actual ID of the TextView within the list item
                .check(matches(withText("TestItem2")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(1)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem1")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem3")));
    }

    @Test
    public void testSortItemByDescription(){
        testItems.generateTestItems();

        testItems.sleepProblemsAway(100);

        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.edit_button)).perform(click());

        onView(withId(R.id.item_description_edittext)).perform(typeText("Sort C"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());

        onView(withId(R.id.item_description_edittext)).perform(typeText("Sort B"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        // Click the button to open the DatePickerDialog
        onView(withId(R.id.item_description_edittext)).perform(typeText("Sort A"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        //Expected order would be testitem3, testitem2, testitem1
        //From inventory screen select sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());

        //Select the sort option dropdown
        onView(withId(R.id.sort_dropdown_spinner)).perform(click());

        //Select Date
        // Select the "Date" sort option from the dropdown
        onData(allOf(is(instanceOf(String.class)), is("Description")))
                .inRoot(isPlatformPopup()).perform(click());

        //add a description filter to test sort for only certain items
        onView(withId(R.id.description_filter_edit_text))
                .perform(typeText("Sort"), closeSoftKeyboard());
        onView(withId(R.id.add_description_filter_button)).perform(click());

        //Select go back button
        onView(withId(R.id.back_button)).perform(click());

        //validate that items are sorted by date ASC.
        // Scroll to the first expected item and verify its position
        onData(anything())
                .inAdapterView(withId(R.id.item_list)) // Replace with the actual ID of your ListView
                .atPosition(0)
                .onChildView(withId(R.id.item_name_text)) // Replace with the actual ID of the TextView within the list item
                .check(matches(withText("TestItem3")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(1)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem2")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem1")));

        //Test DESC
        onView(withId(R.id.sort_filter_button)).perform(click());
        onView(withId(R.id.asc_desc_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onData(anything())
                .inAdapterView(withId(R.id.item_list)) // Replace with the actual ID of your ListView
                .atPosition(0)
                .onChildView(withId(R.id.item_name_text)) // Replace with the actual ID of the TextView within the list item
                .check(matches(withText("TestItem1")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(1)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem2")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem3")));
    }

    @Test
    public void testSortItemByMake(){
        testItems.generateTestItems();

        testItems.sleepProblemsAway(100);

        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.item_description_edittext)).perform(typeText("SortTest"));
        onView(withId(R.id.item_make_edittext)).perform(typeText("A"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.item_description_edittext)).perform(typeText("SortTest"));
        onView(withId(R.id.item_make_edittext)).perform(typeText("C"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(click());
        onView(withId(R.id.edit_button)).perform(click());
        // Click the button to open the DatePickerDialog
        onView(withId(R.id.item_description_edittext)).perform(typeText("SortTest"));
        onView(withId(R.id.item_make_edittext)).perform(typeText("B"));
        onView(withId(R.id.save_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        //Expected order would be testitem3, testitem2, testitem1
        //From inventory screen select sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());

        //Select the sort option dropdown
        onView(withId(R.id.sort_dropdown_spinner)).perform(click());

        //Select Date
        // Select the "Date" sort option from the dropdown
        onData(allOf(is(instanceOf(String.class)), is("Make")))
                .inRoot(isPlatformPopup()).perform(click());

        //add a description filter to test sort for only certain items
        onView(withId(R.id.description_filter_edit_text))
                .perform(typeText("SortTest"), closeSoftKeyboard());
        onView(withId(R.id.add_description_filter_button)).perform(click());

        //Select go back button
        onView(withId(R.id.back_button)).perform(click());

        //validate that items are sorted by date ASC.
        // Scroll to the first expected item and verify its position
        onData(anything())
                .inAdapterView(withId(R.id.item_list)) // Replace with the actual ID of your ListView
                .atPosition(0)
                .onChildView(withId(R.id.item_name_text)) // Replace with the actual ID of the TextView within the list item
                .check(matches(withText("TestItem1")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(1)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem3")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem2")));

        //Test DESC
        onView(withId(R.id.sort_filter_button)).perform(click());
        onView(withId(R.id.asc_desc_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        onData(anything())
                .inAdapterView(withId(R.id.item_list)) // Replace with the actual ID of your ListView
                .atPosition(0)
                .onChildView(withId(R.id.item_name_text)) // Replace with the actual ID of the TextView within the list item
                .check(matches(withText("TestItem2")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(1)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem3")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem1")));
    }

    @Test
    public void testSortItemByEstValue(){
        testItems.generateTestItems();
        testItems.sleepProblemsAway(100);

        onView(withText("TestItem1"))
                .perform(scrollTo())
                .perform(click());

        testItems.sleepProblemsAway(100);

        onView(withId(R.id.edit_button)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.item_description_edittext)).perform(typeText("SortTest"));
        onView(withId(R.id.item_value_edittext)).perform(scrollTo()).perform(typeText("3.22"));
        onView(withId(R.id.save_button)).perform(click());
        testItems.sleepProblemsAway(500);
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem2"))
                .perform(scrollTo())
                .perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.edit_button)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.item_description_edittext)).perform(typeText("SortTest"));
        onView(withId(R.id.item_value_edittext)).perform(scrollTo()).perform(typeText("2.22"));
        onView(withId(R.id.save_button)).perform(click());
        testItems.sleepProblemsAway(500);
        onView(withId(R.id.back_button)).perform(click());

        onView(withText("TestItem3"))
                .perform(scrollTo())
                .perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.edit_button)).perform(click());
        testItems.sleepProblemsAway(100);
        // Click the button to open the DatePickerDialog
        onView(withId(R.id.item_description_edittext)).perform(typeText("SortTest"));
        onView(withId(R.id.item_value_edittext)).perform(scrollTo()).perform(typeText("1.11"));
        onView(withId(R.id.save_button)).perform(click());
        testItems.sleepProblemsAway(500);
        onView(withId(R.id.back_button)).perform(click());

        testItems.sleepProblemsAway(100);

        //Expected order would be testitem3, testitem2, testitem1
        //From inventory screen select sort/filter button
        onView(withId(R.id.sort_filter_button)).perform(click());
        testItems.sleepProblemsAway(100);
        //Select the sort option dropdown
        onView(withId(R.id.sort_dropdown_spinner)).perform(click());
        testItems.sleepProblemsAway(100);
        //Select Date
        // Select the "Date" sort option from the dropdown
        onData(allOf(is(instanceOf(String.class)), is("Estimated Value")))
                .inRoot(isPlatformPopup()).perform(click());

        testItems.sleepProblemsAway(100);
        //add a description filter to test sort for only certain items
        onView(withId(R.id.description_filter_edit_text))
                .perform(typeText("SortTest"), closeSoftKeyboard());
        onView(withId(R.id.add_description_filter_button)).perform(click());

        //Select go back button
        onView(withId(R.id.back_button)).perform(click());
        testItems.sleepProblemsAway(500);

        //validate that items are sorted by date ASC.
        // Scroll to the first expected item and verify its position
        onData(anything())
                .inAdapterView(withId(R.id.item_list)) // Replace with the actual ID of your ListView
                .atPosition(0)
                .onChildView(withId(R.id.item_name_text)) // Replace with the actual ID of the TextView within the list item
                .check(matches(withText("TestItem1")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(1)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem2")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem3")));

        //Test DESC
        onView(withId(R.id.sort_filter_button)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.asc_desc_button)).perform(click());
        onView(withId(R.id.back_button)).perform(click());

        testItems.sleepProblemsAway(100);
        onData(anything())
                .inAdapterView(withId(R.id.item_list)) // Replace with the actual ID of your ListView
                .atPosition(0)
                .onChildView(withId(R.id.item_name_text)) // Replace with the actual ID of the TextView within the list item
                .check(matches(withText("TestItem3")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(1)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem2")));

        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(2)
                .onChildView(withId(R.id.item_name_text))
                .check(matches(withText("TestItem1")));
    }


    public void testSortItemByTags(){

        //This test might need to be done manually
        testItems.generateTestItems();
        //From inventory screen select sort/filter button
        //Select the sort option dropdown
        //Select Tag
        //Select go back button
        //validate that items are sorted by Tag based on the determined tag sort order.
    }

}
