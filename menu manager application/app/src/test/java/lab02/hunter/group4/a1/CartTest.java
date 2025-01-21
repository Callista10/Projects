package lab02.hunter.group4.a1;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.mockito.MockitoAnnotations;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class CartTest {
    private Cart cart;
    private ByteArrayInputStream testInput;
    private Scanner mockScanner;
    private ByteArrayOutputStream systemOutContent = new ByteArrayOutputStream();
    private PrintStream originalSystemOut = System.out;

    @BeforeEach
    public void setUp() {
        cart = new Cart();
        MockitoAnnotations.openMocks(this);


        System.setOut(new PrintStream(systemOutContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalSystemOut);
    }
    @Test
    public void testAddItemWithValidInput() {
        // Set up user input using the provideInput method
        provideInput("Dish1\n10\nno\n");

        // Mock menu items
        HashMap<String, JSONArray> menu = new HashMap<>();
        JSONArray items = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("name", "Dish1");
        item1.put("description", "Description1");
        item1.put("price", "10.0");
        items.add(item1);
        menu.put("Category1", items);

        // Mock JSON manager behavior
        cart.jsonManager = Mockito.mock(JsonManagement.class);
        when(cart.jsonManager.getMenuInfo(cart.jsonManager.MENU_FILE)).thenReturn(menu);

        // Execute the add_item() method
        cart.add_item();

        // Verify that the cartItems were updated as expected
        assertEquals(10, cart.cartItems.get("Dish1"));
    }

    @Test
    public void testAddItemWithInvalidInput() {
        // Set up user input using the provideInput method
        provideInput("InvalidDish\nDish1\n10\nno\n");

        // Mock menu items
        HashMap<String, JSONArray> menu = new HashMap<>();
        JSONArray items = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("name", "Dish1");
        item1.put("description", "Description1");
        item1.put("price", "10.0");
        items.add(item1);
        menu.put("Category1", items);

        // Mock JSON manager behavior
        cart.jsonManager = Mockito.mock(JsonManagement.class);
        when(cart.jsonManager.getMenuInfo(cart.jsonManager.MENU_FILE)).thenReturn(menu);

        // Execute the add_item() method
        cart.add_item();

        // Verify that an error message was printed
        assertTrue(systemOutContent.toString().contains("Item not found in the menu. Please enter a valid item name."));
    }

    @Test
    public void testAdjustQuantity() {
        // Prepare the cart with an item
        cart.cartItems.put("Dish1", 5);

        // Set up user input using the provideInput method
        provideInput("Dish1\n20\n");

        // Execute the adjust_quantity() method
        cart.adjust_quantity();

        // Verify that the quantity was adjusted as expected
        assertEquals(20, cart.cartItems.get("Dish1"));
    }

    @Test
    public void testRemoveItemsFromCart() {
        // Prepare the cart with an item
        cart.cartItems.put("Dish1", 5);

        // Set up user input using the provideInput method
        provideInput("Dish1\n");

        // Execute the remove_items_cart() method
        cart.remove_items_cart();

        // Verify that the item was removed from the cart
        assertFalse(cart.cartItems.containsKey("Dish1"));
    }

    @Test
    public void testConfirmOrder() {
        // Prepare the cart with items
        cart.cartItems.put("Dish1", 2);
        cart.cartItems.put("Dish2", 3);

        // Set up user input using the provideInput method
        provideInput("Pickup\n");

        // Mock menu items
        HashMap<String, JSONArray> menu = new HashMap<>();
        JSONArray items = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("name", "Dish1");
        item1.put("description", "Description1");
        item1.put("price", "10.0");
        JSONObject item2 = new JSONObject();
        item2.put("name", "Dish2");
        item2.put("description", "Description2");
        item2.put("price", "15.0");
        items.add(item1);
        items.add(item2);
        menu.put("Category1", items);

        // Mock JSON manager behavior
        cart.jsonManager = Mockito.mock(JsonManagement.class);
        when(cart.jsonManager.getMenuInfo(cart.jsonManager.MENU_FILE)).thenReturn(menu);

        // Execute the confirm_order() method
        cart.confirm_order();

        // Verify that the order details were printed as expected
        assertTrue(systemOutContent.toString().contains("Order Number: ORD"));

    }


    private void provideInput(String input) {
        testInput = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);
        mockScanner = new Scanner(testInput);
        cart.scanner = mockScanner; // Replace the scanner with the mockScanner
    }
}
