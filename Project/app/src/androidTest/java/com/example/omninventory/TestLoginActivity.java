package com.example.omninventory;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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

    /**
     * DO NOT RUN THIS TEST CASE, IT WILL FAIL BECAUSE FUNCTIONALITY IS INCOMPLETE
     */
    /*

    //Rule too set starting point on login screen
    @Rule
    public ActivityScenarioRule<> scenario = new
            ActivityScenarioRule<>(.class);
    */

    //todo:update test case after change to start on login screen
    //for now app starts on the inventory screen

    /**
     * Base test case for signing up as a user
     * IMPORTANT: User needs to be deleted from database before running this test case
     * IMPORTANT: Incomplete test case (since functionality is not complete yet)
     */
    public void testSignup(){
        //From login screen, sign up for a new account

        //click on profile button
        onView(allOf(withId(R.id.profile_button), isDisplayed())).perform(click());

        onView(withId(R.id.signup_link)).perform(click());

        onView(withId(R.id.signup_name_edit_text)).perform(typeText("John Doe"));
        onView(withId(R.id.signup_username_edit_text)).perform(typeText("TheRealJohnDoe"));
        onView(withId(R.id.signup_password_edit_text)).perform(typeText("TheRealJohnDoe123!"));
        onView(withId(R.id.signup_confirm_password_edit_text)).perform(typeText("TheRealJohnDoe123!"));

        //validate the user profile is of the account just signed up for
        //todo: this is not yet implemented, update when implemented
    }

    public void testLogin(){
        //From login screen, login to a already existing user
        //validate in user profile is the account just logged in
    }

    public void testSignupToLogin(){
        //From login screen go to sign up screen, go back to login screen
    }

    public void testInvalidLogin(){
        //Try to login with invalid user information
        //Check for some kind of error message display
    }


    public void testInvalidSignup(){
        //Try to signup with invalid or not enough information
        //check that some kind of error message should display
    }
}
