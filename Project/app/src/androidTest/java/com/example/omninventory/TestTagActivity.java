package com.example.omninventory;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
    //Note that editing adding and editing tags are handled in
    // testAddItemActivity and testEditItemActivity.
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testCreateAndDeleteTag(){
        //From inventory screen select tags button
        onView(withId(R.id.tag_button)).perform(click());

        //Select add tag
        onView(withId(R.id.add_tag_button)).perform(click());

        //Enter in dialog box new tag name
        onView(withId(R.id.new_tag_name_editText)).perform(typeText("TagTest"));

        //click add button
        onView(withId(R.id.add_tag_dialog_button)).perform(click());

        //validate new tag added
        onView(withText("TestTag")).check(matches(isDisplayed()));

        //todo not yet implemented
        //delete said tag

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

}
