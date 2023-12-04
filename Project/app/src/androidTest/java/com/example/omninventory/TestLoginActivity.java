package com.example.omninventory;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressMenuKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for logging into the app.
 * @author Kevin
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestLoginActivity {

    TestItems testItems = new TestItems(); //sleep user

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    /**
     * Base test case for signing up as a user
     * IMPORTANT: User needs to be deleted from database before running this test case
     * Delete the user after running this test case to ensure no duplicates?
     *
     * @author Kevin
     */
    @Test
    public void testSignup(){
        //From login screen, sign up for a new account
        onView(withId(R.id.signup_link)).perform(click());

        onView(withId(R.id.signup_name_edit_text)).perform(typeText("John Doe"));
        onView(withId(R.id.signup_username_edit_text)).perform(typeText("TheRealJohnDoe"));
        onView(withId(R.id.signup_password_edit_text)).perform(typeText("TheRealJohnDoe123!"),closeSoftKeyboard());
        onView(withId(R.id.signup_confirm_password_edit_text)).perform(typeText("TheRealJohnDoe123!"),closeSoftKeyboard());
        onView(withId(R.id.signup_btn)).perform(click());
        testItems.sleepProblemsAway(200);
        //check profile
        onView(allOf(withId(R.id.profile_button), isDisplayed())).perform(click());
        onView(withText("John Doe")).check(matches(isDisplayed()));
        onView(withText("@TheRealJohnDoe")).check(matches(isDisplayed()));

        //log out and log in
        onView(withId(R.id.logout_btn)).perform(click());
        testItems.sleepProblemsAway(200);
        onView(withId(R.id.login_username_edit_text)).perform(typeText("TheRealJohnDoe"));
        onView(withId(R.id.login_password_edit_text)).perform(typeText("TheRealJohnDoe123!"),closeSoftKeyboard());
        onView(withId(R.id.login_btn)).perform(click());
        //check profile again
        onView(allOf(withId(R.id.profile_button), isDisplayed())).perform(click());
        onView(withText("John Doe")).check(matches(isDisplayed()));
        onView(withText("@TheRealJohnDoe")).check(matches(isDisplayed()));

    }

}
