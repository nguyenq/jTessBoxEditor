package net.sourceforge.tessboxeditor;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class GuiWithTrainerTest {
    
    public GuiWithTrainerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class GuiWithTrainer.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        GuiWithTrainer.main(args);
    }

    /**
     * Test of getDisplayTime method, of class GuiWithTrainer.
     */
    @Test
    public void testGetDisplayTime() {
        System.out.println("getDisplayTime");
        long millis = 8735000L;
        String expResult = "02:25:35";
        String result = GuiWithTrainer.getDisplayTime(millis);
        assertEquals(expResult, result);
    }
    
}
