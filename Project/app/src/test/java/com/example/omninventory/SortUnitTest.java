package com.example.omninventory;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Unit tests for sorting.
 * @author Kevin
 */
public class SortUnitTest {

    private InventoryItem item1;
    private InventoryItem item2;
    private InventoryItem item3;

    /**
     * Set up unit test for sort
     * @author Kevin
     */
    @Before
    public void setUp(){

        Calendar c1 = Calendar.getInstance();
        c1.set(2022,02,21);
        item1 = new InventoryItem(
                "id1",
                "item1",
                "description1",
                "comment1",
                "make1",
                "model1",
                "serialno1",
                new ItemValue((long) 1.11),
                new ItemDate(c1),
                null,
                null
        );

        Calendar c2 = Calendar.getInstance();
        c2.set(2022,02,22);
        item2 = new InventoryItem(
                "id2",
                "item2",
                "description2",
                "comment2",
                "make2",
                "model2",
                "serialno2",
                new ItemValue((long) 2.22),
                new ItemDate(c2),
                null,
                null
        );

        Calendar c3 = Calendar.getInstance();
        c3.set(2022,02,23);
        item3 = new InventoryItem(
                "id3",
                "item3",
                "description3",
                "comment3",
                "make3",
                "model3",
                "serialno3",
                new ItemValue((long) 3.33),
                new ItemDate(c3),
                null,
                null
        );
    }

    /**
     * Unit test sort date
     * @author Kevin
     */
    @Test
    public void testSortDate(){
        SortByDate sorter = new SortByDate();
        assertEquals(1,sorter.compare(item2,item1));
        assertEquals(-1,sorter.compare(item2,item3));
        assertEquals(0,sorter.compare(item2,item2));
    }

    /**
     * unit test for sorting description
     * @author Kevin
     */
    @Test
    public void testSortDescription(){
        SortByDescription sorter = new SortByDescription();
        assertEquals(1,sorter.compare(item2,item1));
        assertEquals(-1,sorter.compare(item2,item3));
        assertEquals(0,sorter.compare(item2,item2));
    }

    /**
     * unit test for sorting Make
     * @author Kevin
     */
    @Test
    public void testSortMake(){
        SortByMake sorter = new SortByMake();
        assertEquals(1,sorter.compare(item2,item1));
        assertEquals(-1,sorter.compare(item2,item3));
        assertEquals(0,sorter.compare(item2,item2));
    }

    /**
     * unit test for sorting Value
     * @author Kevin
     */
    @Test
    public void testSortValue(){
        SortByValue sorter = new SortByValue();
        assertEquals(1,sorter.compare(item2,item1));
        assertEquals(-1,sorter.compare(item2,item3));
        assertEquals(0,sorter.compare(item2,item2));
    }

}
