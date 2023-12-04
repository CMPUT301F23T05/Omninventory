package com.example.omninventory;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;

/**
 * Unit tests for InventoryItem.
 * @author Kevin
 */
public class InventoryItemUnitTest {

    public InventoryItem item;

    /**
     * setup for inventory item tests
     * @author Kevin
     */
    @Before
    public void setUp(){
        Calendar c1 = Calendar.getInstance();
        c1.set(2022,02,21);
        item = new InventoryItem(
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
    }

    /**
     * Testing getters and setters for inventory item
     * @author Kevin
     */
    @Test
    public void testGetterSetter(){

        //firebaseId
        assertEquals("id1",item.getFirebaseId());

        //name
        assertEquals(item.getName(),"item1");
        item.setName("Item");
        assertEquals(item.getName(),"Item");

        //description
        assertEquals(item.getDescription(),"description1");
        item.setDescription("d1");
        assertEquals(item.getDescription(),"d1");

        //comment
        assertEquals(item.getComment(),"comment1");
        item.setComment("c1");
        assertEquals(item.getComment(),"c1");

        //Make
        assertEquals(item.getMake(), "make1");
        item.setMake("m1");
        assertEquals(item.getMake(),"m1");

        //model
        assertEquals(item.getModel(),"model1");
        item.setModel("mod1");
        assertEquals(item.getModel(),"mod1");

        //Date
        Calendar t = Calendar.getInstance();
        t.set(2022,02,21);
        assertEquals(item.getDate().toString(), "2022-03-21");
        t.set(2022,02,22);
        item.setDate(new ItemDate(t));
        assertEquals(item.getDate().toString(), "2022-03-22");

        //value
        assertEquals(item.getValue().toPrimitiveLong(), (long) 1.11);
        ItemValue newVal = new ItemValue((long)2.22);
        item.setValue(newVal);
        assertEquals(item.getValue().toPrimitiveLong(),(long)2.22);

        //serial
        assertEquals(item.getSerialNo(), "serialno1");
        item.setSerialNo("s1");
        assertEquals(item.getSerialNo(), "s1");

        //selected
        assertEquals(item.isSelected(), false);
        item.setSelected(true);
        assertEquals(item.isSelected(), true);

    }
}
