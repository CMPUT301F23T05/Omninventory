package com.example.omninventory;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * Tests for the Tags functionality.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestTagActivity {

    TestItems testItems = new TestItems();

    //Note that editing adding and editing tags are handled in
    // testAddItemActivity and testEditItemActivity.
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

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

    @Test
    public void testCreateAndDeleteTag(){
        //From inventory screen select tags button
        onView(withId(R.id.tag_button)).perform(click());

        //Select add tag
        onView(withId(R.id.add_tag_button)).perform(click());

        //Enter in dialog box new tag name
        onView(withId(R.id.new_tag_name_editText)).perform(typeText("TagTest"));
        onView(withId(R.id.new_tag_priority_editText)).perform(typeText("22"));

        //click add button
        onView(withId(R.id.add_tag_dialog_button)).perform(click());

        //validate new tag added
        onView(withText("TagTest")).check(matches(isDisplayed()));

        onView(withText("TagTest")).perform(longClick());
        onView(withId(R.id.delete_tag_button)).perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());
        testItems.sleepProblemsAway(50);//process the delete
        onView(withText("TestTag")).check(doesNotExist());

    }

    @Test
    public void testAddTagCancel(){
        //From inventory screen select tags button
        onView(withId(R.id.tag_button)).perform(click());

        //Select add tag
        onView(withId(R.id.add_tag_button)).perform(click());

        //Enter in dialog box new tag name
        onView(withId(R.id.new_tag_name_editText)).perform(typeText("CancelTag"));

        //click cancel button
        onView(withId(R.id.cancel_dialog_button)).perform(click());

        //validate cancel isnt on screen
        onView(withText("CancelTag")).check(doesNotExist());
    }

    @Test
    public void testDeleteMultipleTags(){
        //From inventory screen select tags button
        onView(withId(R.id.tag_button)).perform(click());

        //Select add tag
        onView(withId(R.id.add_tag_button)).perform(click());

        //Enter in dialog box new tag name
        onView(withId(R.id.new_tag_name_editText)).perform(typeText("TagTest1"));
        onView(withId(R.id.new_tag_priority_editText)).perform(typeText("22"));

        //click add button
        onView(withId(R.id.add_tag_dialog_button)).perform(click());

        //validate new tag added
        onView(withText("TagTest1")).check(matches(isDisplayed()));

        //Select add tag
        onView(withId(R.id.add_tag_button)).perform(click());

        //Enter in dialog box new tag name
        onView(withId(R.id.new_tag_name_editText)).perform(typeText("TagTest2"));
        onView(withId(R.id.new_tag_priority_editText)).perform(typeText("22"));

        //click add button
        onView(withId(R.id.add_tag_dialog_button)).perform(click());

        //validate new tag added
        onView(withText("TagTest2")).check(matches(isDisplayed()));

        //select the 2 tags
        onView(withText("TagTest1")).perform(longClick());
        onView(withText("TagTest2")).perform(longClick());
        onView(withId(R.id.delete_tag_button)).perform(click());
        onView(withId(R.id.delete_dialog_button)).perform(click());
        testItems.sleepProblemsAway(50);//process the delete
        onView(withText("TestTag1")).check(doesNotExist());
        onView(withText("TestTag2")).check(doesNotExist());
    }

}
