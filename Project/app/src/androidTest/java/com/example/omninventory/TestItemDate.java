package com.example.omninventory;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

/**
 * Get rid of this later, it's a demonstration that type conversion in ItemDate works as intended.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestItemDate {

    @Test
    public void testItemDate(){
        ItemDate x = new ItemDate("2002-11-23");
        Log.d("test1", x.toString());
        Log.d("test1", x.toDate().toString());
        Log.d("test1", x.toCalendar().toString());

        ItemDate y = new ItemDate(Calendar.getInstance());
        Log.d("test2", y.toString());
        Log.d("test2", y.toDate().toString());
        Log.d("test2", y.toCalendar().toString());

        ItemDate z = new ItemDate(new Date());
        Log.d("test3", z.toString());
        Log.d("test3", z.toDate().toString());
        Log.d("test3", z.toCalendar().toString());
    }

}


