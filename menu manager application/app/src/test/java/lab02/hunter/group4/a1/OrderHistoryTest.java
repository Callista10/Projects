package lab02.hunter.group4.a1;

import java.io.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import com.fasterxml.jackson.databind.ObjectMapper;


public class OrderHistoryTest {
    private OrderHistory orderHistory;
    private String jsonFilePath;
    private ObjectMapper objectMapper;
    private String expOutputJsonPath;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();


    @BeforeEach
    public void setUp() {
        jsonFilePath = "src/test/resources/orderHistoryTestSample.json";
        expOutputJsonPath = "src/test/resources/expectedOutput.json";
            JSONObject totalOrder = new JSONObject();
            JSONArray expOrders = new JSONArray();
            JSONObject expOrder1 = new JSONObject();

            expOrder1.put("orderNumber", "ORD20230831105043");
            expOrder1.put("orderDate", "2023-08-31");

            JSONArray expItems = new JSONArray();
            JSONObject expItem1 = new JSONObject();
            expItem1.put("name", "testItem1");
            expItem1.put("quantity", 1);

            JSONObject expItem2 = new JSONObject();
            expItem2.put("name", "testItem2");
            expItem2.put("quantity", 2);

            expItems.add(expItem1);
            expItems.add(expItem2);

            expOrder1.put("items", expItems);
            expOrder1.put("total", 99.0);
            expOrders.add(expOrder1);
            totalOrder.put("orders", expOrders);

            // write the simulated order json file
            try {
                FileWriter file = new FileWriter(expOutputJsonPath);
                file.write(totalOrder.toJSONString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        orderHistory = new OrderHistory(true, expOutputJsonPath);
        objectMapper = new ObjectMapper();
    }


        @Test
        public void testDisplayOrderHistory() throws IOException {
            JSONObject totalOrder = new JSONObject();
            JSONArray expOrders = new JSONArray();
            JSONObject expOrder1 = new JSONObject();

            expOrder1.put("orderNumber", "ORD20230831105043");
            expOrder1.put("orderDate", "2023-08-31");

            JSONArray expItems = new JSONArray();
            JSONObject expItem1 = new JSONObject();
            expItem1.put("name", "testItem1");
            expItem1.put("quantity", 1);

            JSONObject expItem2 = new JSONObject();
            expItem2.put("name", "testItem2");
            expItem2.put("quantity", 2);

            expItems.add(expItem1);
            expItems.add(expItem2);

            expOrder1.put("items", expItems);
            expOrder1.put("total", 99.0);
            expOrders.add(expOrder1);
            totalOrder.put("orders", expOrders);

            // write the simulated order json file
            try {
                FileWriter file = new FileWriter(expOutputJsonPath);
                file.write(totalOrder.toJSONString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // get the actual output
            System.setOut(new PrintStream(outContent));
            System.setErr(new PrintStream(errContent));




//            ArrayList<Order> sampleOrder = jm.getOrderHistory("app/src/main/resources/order.json");

            // Redirect the System.out to capture the printed output
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outputStream));
            orderHistory.display_order_history();
            // Call the method you want to test

            // Verify the output
            String expectedOutput = "Order No: ORD20230831105043\n" +
                    "Order Date: 2023-08-31\n" +
                    "Item: testItem1 Quantity: 1\n" +
                    "Item: testItem2 Quantity: 2\n" +
                    "Total Price: 99.0\n\n";
            Assertions.assertEquals(expectedOutput, outputStream.toString());
        }



    @Test
    public void test_display_total_order() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        orderHistory.display_total_order();
        System.setOut(System.out);
        String actualOutput = outputStream.toString().trim();
        Assertions.assertEquals("Total Order: 1", actualOutput);

    }

    @Test
    public void test_get_targeted_order_exists() {
        String orderNumber = "ORD20230831105043";
        Order foundOrder = orderHistory.get_targeted_order(orderNumber);

        Assertions.assertNotNull(foundOrder);
        Assertions.assertEquals(orderNumber, foundOrder.getOrderNo());
    }

    @Test
    public void test_get_targeted_order_does_not_exists() {
        String orderNumber = "0";
        Order foundOrder = orderHistory.get_targeted_order(orderNumber);

        Assertions.assertNull(foundOrder);

    }
}




