package com.example.omninventory;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

/**
 * Unit tests for ItemDate.
 * @author Kevin
 */
public class ItemDateUnitTest {

    Calendar temp = Calendar.getInstance();

    /**
     * setup for item date
     * @author Kevin
     */
    @Before
    public void setUp(){
        temp.set(2022,02,22);
    }

    /**
     * UnitTest for Item date
     * @author Kevin
     */
    @Test
    public void test(){
        ItemDate tester = new ItemDate(temp);

        //To string
        assertEquals("2022-03-22", tester.toString());
        assertEquals("2023-12-04", tester.ymdToString(2023,12,04));
        assertEquals("2022-03-22",tester.calToString(tester.toCalendar()));

        //cannot test methods with logd junit nnot support.o
    }

}
