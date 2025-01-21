package lab02.hunter.group4.a1;
import java.time.LocalDate;
import java.util.HashMap;

import org.junit.jupiter.api.*;

public class OrderTest {

    private static Order order;

    @BeforeAll
    public static void setUp() {
        HashMap<String, Integer> orderItems = new HashMap<>();
        orderItems.put("Salmon", 3);
        orderItems.put("Tiramisu", 1);
        LocalDate orderDate = LocalDate.parse("2023-09-05");
        order = new Order("ORD20230905101430", orderDate, orderItems, 68.97);
    }

    @Test
    public void testGetOrderNoInvalid() {
        Assertions.assertNotEquals("2", order.getOrderNo());
    }

    @Test
    public void testGetOrderNoValid() {
        Assertions.assertEquals("ORD20230905101430", order.getOrderNo());
    }

    @Test
    public void testGetOrderDateInvalid() {
        Assertions.assertNotEquals("2023.09.05", order.getOrderDate().toString());
    }

    @Test
    public void testGetOrderDateValid() {
        Assertions.assertEquals("2023-09-05", order.getOrderDate().toString());
    }

    @Test
    public void testGetOrderItemsInvalid() {
        HashMap<String, Integer> orderItems = new HashMap<>();
        orderItems.put("Salmon", 3);
        orderItems.put("Tiramisu", 2);
        Assertions.assertNotEquals(orderItems, order.getOrderItems());
    }

    @Test
    public void testGetOrderItemsValid() {
        HashMap<String, Integer> orderItems = new HashMap<>();
        orderItems.put("Salmon", 3);
        orderItems.put("Tiramisu", 1);
        Assertions.assertEquals(orderItems, order.getOrderItems());
    }

    @Test
    public void testGetTotalPriceInvalid() {
        Assertions.assertNotEquals(69.00, order.getTotalPrice());
    }

    @Test
    public void testGetTotalPriceValid() {
        Assertions.assertEquals(68.97, order.getTotalPrice());
    }
}
