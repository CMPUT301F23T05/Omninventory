package com.example.omninventory;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestLoginActivity {


    /*

    //Rule too set starting point on login screen
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);
    */

    @Test
    public void testSignup(){
        //From login screen, sign up for a new account
        //validate the user profile is of the account just signed up for
    }

    @Test
    public void testLogin(){
        //From login screen, login to a already existing user
        //validate in user profile is the account just logged in
    }

    public void testSignupToLogin(){
        //From login screen go to sign up screen, go back to login screen
    }

    @Test
    public void testInvalidLogin(){
        //Try to login with invalid user information
        //Check for some kind of error message display
    }

    @Test
    public void testInvalidSignup(){
        //Try to signup with invalid or not enough information
        //check that some kind of error message should display
    }
}
