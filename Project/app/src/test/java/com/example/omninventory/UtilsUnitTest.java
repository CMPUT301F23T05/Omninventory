package com.example.omninventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Unit tests for Utils.
 * @author Castor
 */
public class UtilsUnitTest {

    /**
     * Unit test for password validation
     * @author Castor
     */
    @Test
    public void testUtilPassword(){

        //Invalid passwords
        assertEquals(Utils.validatePassword("password"), false);
        assertEquals(Utils.validatePassword("password123"), false);
        assertEquals(Utils.validatePassword("123456789"), false);
        assertEquals(Utils.validatePassword("password!"), false);
        assertEquals(Utils.validatePassword("PASSWORD!"), false);
        assertEquals(Utils.validatePassword("A123!"), false);

        assertEquals(Utils.validatePassword("Password123!"), true);
        assertEquals(Utils.validatePassword("ABCDEf10-"), true);
    }

    /**
     * Unit test for Sha256
     * @author castor
     */
    @Test
    public void testUtilSha256(){
        // same hash
        assertEquals(Utils.sha256("password"), Utils.sha256("password"));
        assertEquals(Utils.sha256("ABCDEf10-"), Utils.sha256("ABCDEf10-"));

        // different hash
        assertNotEquals(Utils.sha256("password"), Utils.sha256("Password"));
        assertNotEquals(Utils.sha256("ABCDEf10-"), Utils.sha256("ABCDEf10!"));
    }
}
