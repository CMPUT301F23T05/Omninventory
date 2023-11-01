package com.example.omninventory;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    /*

    // setting rule for isolating the login screen
    @Rule
    public ActivityScenarioRule<any> scenario = new
            ActivityScenarioRule<any>(any.class);

     */

    @Test
    public void testLogin(){
        //set up a default user to test login
        //Test both fail and success case
    }

    @Test
    public void testSignup(){
        // Test for user signup
    }

    @Test
    public void invalidSignup(){
        //Test to reject sign up when
    }
}
