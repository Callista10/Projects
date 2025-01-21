package lab02.hunter.group4.a1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;

public class MenuAdminTest {
    private Menu menu;
    private InputStream testInput;
    private Scanner mockScanner;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private String menuPath = "src/test/resources/testMenu.json";


    @BeforeEach
    public void setUp() {
        menu = new Menu();
        // Redirect System.out and System.err to capture output
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void tearDown() {

        if (mockScanner != null) {
            mockScanner.close();
        }
    }

    // Helper method to provide input to the scanner
    private void provideInput(String input) {
        testInput = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);
        mockScanner = new Scanner(testInput);
        menu.scanner = mockScanner; // Replace the scanner with the mockScanner
    }

    @Test
    public void testAddMenuItem() {
        provideInput("1\nCategory1,Item1,Description1,10.99\n5\n");
        menu.Astart(menuPath);

        // Replace with assertions based on your expected behavior
        // For example, check if the item was added correctly to the menu
        // You can access the menu state and assert on it
        HashMap<String, JSONArray> menuInfo = menu.jsonManager.getMenuInfo(menuPath);
        JSONArray itemsInCategory1 = menuInfo.get("Category1");
        assertEquals(1, itemsInCategory1.size());
    }

    @Test
    public void testAstartInvalidChoice() {
        // Simulate invalid input (choice 42)
        provideInput("42\n5\n");
        menu.Astart(menuPath);

        // Check if the output contains the expected "Invalid choice" message
        String consoleOutput = outContent.toString().trim();
        assertTrue(consoleOutput.contains("Invalid choice. Please try again."));
    }
    @Test
    public void testUpdateExistingItem() {
        // Add a menu item first
        provideInput("1\nCategory1,Item1,Description1,10.99\n5\n");
        menu.Astart(menuPath);

        // Now, test updating the menu item
        provideInput("2\nCategory1,Item1,NewDescription,12.99\n5\n");
        menu.Astart(menuPath);

        // Replace with assertions based on your expected behavior
        // For example, check if the item was updated correctly in the menu
        HashMap<String, JSONArray> menuInfo = menu.jsonManager.getMenuInfo(menuPath);
        JSONArray itemsInCategory1 = menuInfo.get("Category1");
        assertEquals("NewDescription", ((JSONObject) itemsInCategory1.get(0)).get("description"));
        assertEquals("12.99", ((JSONObject) itemsInCategory1.get(0)).get("price"));
    }

    @Test
    public void testRemoveOutdatedItem() {
        // Add a menu item first
        provideInput("1\nCategory1,Item1,Description1,10.99\n5\n");
        menu.Astart(menuPath);

        // Now, test removing the menu item
        provideInput("3\nCategory1,Item1\n5\n");
        menu.Astart(menuPath);

        // Replace with assertions based on your expected behavior
        // For example, check if the item was removed correctly from the menu
        HashMap<String, JSONArray> menuInfo = menu.jsonManager.getMenuInfo(menuPath);
        JSONArray itemsInCategory1 = menuInfo.get("Category1");
        assertEquals(0, itemsInCategory1.size());
    }

    @Test
    public void testEditCategory() {
        // Add a menu item first
        provideInput("1\nCategory1,Item1,Description1,10.99\n5\n");
        menu.Astart(menuPath);

        // Now, test editing the category name
        provideInput("4\nCategory1,NewCategory\n5\n");
        menu.Astart(menuPath);

        // Replace with assertions based on your expected behavior
        // For example, check if the category name was updated correctly in the menu
        HashMap<String, JSONArray> menuInfo = menu.jsonManager.getMenuInfo(menuPath);
        assertEquals(false, menuInfo.containsKey("Category1"));
        assertEquals(true, menuInfo.containsKey("NewCategory"));
    }
}
