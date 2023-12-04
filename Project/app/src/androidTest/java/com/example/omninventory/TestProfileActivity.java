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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.matchesPattern;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for the user Profile screen (not yet implemented).
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestProfileActivity {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    TestItems testItems = new TestItems();

    @Before
    public void setUp(){
        testItems.sleepProblemsAway(1000);

        onView(withId(R.id.login_username_edit_text)).perform(typeText("Tester"));
        onView(withId(R.id.login_password_edit_text))
                .perform(typeText("Hahaha123!"), closeSoftKeyboard());
        onView(withId(R.id.login_btn)).perform(click());

        //allow app to load in time
        testItems.sleepProblemsAway(2000);
    }

    @Test
    public void TestProfile(){
        //From inventory screen, click on the profile button.
        onView(allOf(withId(R.id.profile_button), isDisplayed()))
                .perform(click());
        //Validate profile screen contains profile stuff
        onView(withText("Tester Test")).check(matches(isDisplayed()));
        onView(withText("@Tester")).check(matches(isDisplayed()));
        //Click the Change name button, and validate it works properly
        onView(withId(R.id.change_name_btn)).perform(click());
        testItems.sleepProblemsAway(200);

        onView(withId(R.id.change_name_editText)).perform(typeText("Tester newTest"));
        onView(withId(R.id.ok_dialog_button)).perform(click());
        testItems.sleepProblemsAway(50);
        onView(withText("Tester newTest")).check(matches(isDisplayed()));

        onView(withId(R.id.change_name_btn)).perform(click());
        testItems.sleepProblemsAway(200);
        onView(withId(R.id.cancel_dialog_button)).perform(click());
        testItems.sleepProblemsAway(50);
        onView(withText("Tester newTest")).check(matches(isDisplayed()));

        onView(withId(R.id.change_name_btn)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.change_name_editText)).perform(typeText("Tester Test"));
        onView(withId(R.id.ok_dialog_button)).perform(click());
        testItems.sleepProblemsAway(50);
        onView(withText("Tester Test")).check(matches(isDisplayed()));


        //----Change user name tests ------
        onView(withId(R.id.change_username_btn)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.change_name_editText)).perform(typeText("Tester1"));
        onView(withId(R.id.ok_dialog_button)).perform(click());
        testItems.sleepProblemsAway(50);
        onView(withText("@Tester1")).check(matches(isDisplayed()));

        onView(withId(R.id.logout_btn)).perform(click());
        onView(withId(R.id.login_username_edit_text)).perform(typeText("Tester1"));
        onView(withId(R.id.login_password_edit_text))
                .perform(typeText("Hahaha123!"), closeSoftKeyboard());
        onView(withId(R.id.login_btn)).perform(click());
        onView(withId(R.id.profile_button)).perform(click());

        onView(withId(R.id.change_username_btn)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.change_name_editText)).perform(typeText("Tester"));
        onView(withId(R.id.ok_dialog_button)).perform(click());
        testItems.sleepProblemsAway(50);
        onView(withText("@Tester")).check(matches(isDisplayed()));


        //---- PASSWORD TESTS (PASSED) VERY RISKY!! ---
        onView(withId(R.id.change_password_btn)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.cancel_dialog_button)).perform(click());
        onView(withId(R.id.old_password_editText)).check(doesNotExist());

        onView(withId(R.id.change_password_btn)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.old_password_editText)).perform(typeText("Hahaha123!"));
        onView(withId(R.id.new_password_editText)).perform(typeText("Hahaha321!"));
        onView(withId(R.id.confirm_password_editText)).perform(typeText("Hahaha321!"));
        onView(withId(R.id.ok_dialog_button)).perform(click());

        testItems.sleepProblemsAway(100);
        onView(withId(R.id.logout_btn)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.login_username_edit_text)).perform(typeText("Tester"));
        onView(withId(R.id.login_password_edit_text))
                .perform(typeText("Hahaha321!"), closeSoftKeyboard());
        onView(withId(R.id.login_btn)).perform(click());

        onView(allOf(withId(R.id.profile_button), isDisplayed()))
                .perform(click());
        //Validate profile screen contains profile stuff
        onView(withText("Tester Test")).check(matches(isDisplayed()));
        onView(withText("@Tester")).check(matches(isDisplayed()));

        onView(withId(R.id.change_password_btn)).perform(click());
        testItems.sleepProblemsAway(100);
        onView(withId(R.id.old_password_editText)).perform(typeText("Hahaha321!"));
        onView(withId(R.id.new_password_editText)).perform(typeText("Hahaha123!"));
        onView(withId(R.id.confirm_password_editText)).perform(typeText("Hahaha123!"));
        onView(withId(R.id.ok_dialog_button)).perform(click());
        onView(withId(R.id.logout_btn)).perform(click());

    }


}
