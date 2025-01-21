package lab02.hunter.group4.a1;
import org.json.simple.JSONArray;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import java.util.Scanner;

public class MenuGeneralTest {
    @Mock
    private JsonManagement jsonManager;
    private Menu menu;
    private static String testMenuJsonPath;
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private ByteArrayInputStream testInput;
    private Scanner mockScanner;
    private InputStream inputStream;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        menu = new Menu();
        menu.jsonManager = jsonManager;
        testMenuJsonPath = "src/test/resources/testMenu.json";

        // Redirect System.out and System.err to capture output
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void tearDown() {
        // Restore original System.out and System.err
        System.setOut(originalOut);
        System.setErr(originalErr);
    }



    @Test
    public void testOnlyDisplayCategories() {
        Map<String, JSONArray> menuInfo = new HashMap<>();
        menuInfo.put("Appetizers", new JSONArray());
        menuInfo.put("Main", new JSONArray());
        menuInfo.put("Dessert", new JSONArray());

        menu.jsonManager = jsonManager;

        menu.only_display_categories(testMenuJsonPath);

        String expectedOutput = "The Menu Available Categories:\n";
        Assertions.assertEquals(expectedOutput, outContent.toString());
    }

    private void provideInput(String input) {
        testInput = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);
        mockScanner = new Scanner(testInput);
        menu.scanner = mockScanner; // Replace the scanner with the mockScanner
    }

    @Test
    public void testDisplayCategories() {
        // Mock the JSON data retrieval
        HashMap<String, JSONArray> mockMenuInfo = new HashMap<>();
        JSONArray mockItems = new JSONArray();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "TestItem");
        item1.put("description", "Test Description");
        item1.put("price", "9.99");
        mockItems.add(item1);
        mockMenuInfo.put("TestCategory", mockItems);

        // Provide input to simulate user entering "TestCategory"
        provideInput("TestCategory\n");

        // Set up your mock jsonManager to return the mock data
        when(jsonManager.getMenuInfo(jsonManager.MENU_FILE)).thenReturn(mockMenuInfo);

        // Redirect the System.out to capture the printed output
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        // Call the method you want to test
        menu.display_categories(jsonManager.MENU_FILE);

        // Verify the output
        String expectedOutput = "----------------------------\n" +
                "Available Categories:\n" +
                "TestCategory\n" +
                "Enter the category you want to see: " +
                "Items in the selected category:\n" +
                "Name: TestItem\n" +
                "Description: Test Description\n" +
                "Price: 9.99\n" +
                "----------------------------\n";
        assertEquals(expectedOutput, outputStream.toString());
    }

    @Test
    public void testDisplayDetail() {
        Map<String, Object> item1 = new HashMap<>();
        item1.put("name", "Calamari");
        item1.put("description", "Fried calamari with a side of marinara sauce.");
        item1.put("price", "9.99");

        Map<String, Object> item2 = new HashMap<>();
        item2.put("name", "Bruschetta");
        item2.put("description", "Grilled bread with garlic, tomatoes, olive oil, and basil.");
        item2.put("price", "7.99");

        List<Map<String, Object>> itemsList = List.of(item1, item2);

        menu.display_detail(itemsList);

        String expectedOutput = "Items in the selected category:\n" +
                "Name: Calamari\nDescription: Fried calamari with a side of marinara sauce.\nPrice: 9.99\n"+"----------------------------"+
                "\nName: Bruschetta\nDescription: Grilled bread with garlic, tomatoes, olive oil, and basil.\nPrice: 7.99\n"+"----------------------------\n";
        Assertions.assertEquals(expectedOutput, outContent.toString());
    }
    
    @Test
    public void testGstartOption1() {

        // Provide input for option 1 (Only view Menu Categories)
        provideInput("1\n3\n");

        // Execute Gstart method
        menu.Gstart();

        // Verify that the output contains the expected text
        String output = outContent.toString();
        Assertions.assertTrue(output.contains("Only view Menu Categories"));
        Assertions.assertTrue(output.contains("Exiting..."));
    }

}
