package com.example.omninventory;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ItemValueUnitTest {

    /**
     * Unit test for Item value
     * @author Castor Shem
     */
    @Test
    public void test(){
        ItemValue tester = new ItemValue("4500");

        // Test conversion
        assertEquals("$45.00", tester.toString());
        assertEquals(4500L, tester.toPrimitiveLong());

        // Test different formats
        assertEquals("$45.01", ItemValue.makeValidString("4501"));
        assertEquals("$45.01", ItemValue.makeValidString("$45.01"));
        assertEquals("$45.01", ItemValue.makeValidString("4---=-/.501=-"));
    }

}
