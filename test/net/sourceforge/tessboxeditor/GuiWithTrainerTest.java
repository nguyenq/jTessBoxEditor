package net.sourceforge.tessboxeditor;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;



public class GuiWithTrainerTest {
    
    public GuiWithTrainerTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
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
