package lab02.hunter.group4.a1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

public class JsonManagementTest {

    private static JsonManagement jm;
    private String expOutputJsonPath = "src/test/resources/expectedOutput.json";
    private String actualOutputJsonPath = "src/test/resources/actualOutput.json";
    
    @BeforeAll
    public static void setUp() {
        jm = new JsonManagement();
    }

    // test if the admin info is correctly read from the json file
    @Test
    public void testGetAdminInfoValid() {
        // write a simulated admin username and pwd into the expectedOutput and compare to the actual output
        String username1 = "testAdminUsername";
        String password1 = "testAdminPassword";
        JSONObject admin1 = new JSONObject();
        String username2 = "testAdminUsername2";
        String password2 = "testAdminPassword2";
        JSONObject admin2 = new JSONObject();
        admin1.put("username", username1);
        admin1.put("password", password1);
        admin2.put("username", username2);
        admin2.put("password", password2);
        JSONArray adminArray = new JSONArray();
        adminArray.add(admin1);
        adminArray.add(admin2);
        try {
            FileWriter file = new FileWriter(expOutputJsonPath);
            file.write(adminArray.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the actual output
        HashMap<String, String> actualOutput = jm.getAdminInfo(expOutputJsonPath);
        
        // check if the actual output contains the expected username and pwd
        Assertions.assertEquals(actualOutput.size(), 2);
        Assertions.assertNotNull(actualOutput.get(username1));
        Assertions.assertNotNull(actualOutput.get(username2));
        Assertions.assertEquals(actualOutput.get(username1), password1);
        Assertions.assertEquals(actualOutput.get(username2), password2);
    }

    // test if the correct orderhistory is returned when new order is added
    @Test
    public void validGetOrderHistory() {
        // simulate an order json file
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
        ArrayList<Order> actualOutput = jm.getOrderHistory(expOutputJsonPath);
        Order o1 = actualOutput.get(0);

        // check if the actual output contains the expected order history
        Assertions.assertEquals(actualOutput.size(), 1);
        Assertions.assertEquals(o1.getOrderNo(), "ORD20230831105043");
        Assertions.assertEquals(o1.getOrderDate(), LocalDate.parse("2023-08-31", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        Assertions.assertEquals(o1.getOrderItems().size(), 2);
        Assertions.assertEquals(o1.getOrderItems().get("testItem1"), 1);
        Assertions.assertEquals(o1.getOrderItems().get("testItem2"), 2);
        Assertions.assertEquals(o1.getTotalPrice(), 99.0);
    }

    // test if the correct menu info is returned
    @Test
    public void validGetMenuInfo() {
        // simulate a menu json file
        JSONObject menuTest = new JSONObject();
        JSONArray testCategory1 = new JSONArray();
        JSONArray testCategory2 = new JSONArray();
        JSONObject cat1Item1 = new JSONObject();
        JSONObject cat1Item2 = new JSONObject();
        JSONObject cat2Item1 = new JSONObject();
        
        cat1Item1.put("name", "cat1Item1");
        cat1Item1.put("price", 1.0);
        cat1Item1.put("description", "cat1Item1 for testing");

        cat1Item2.put("name", "cat1Item2");
        cat1Item2.put("price", 2.0);
        cat1Item2.put("description", "cat1Item2 for testing");

        cat2Item1.put("name", "cat2Item1");
        cat2Item1.put("price", 3.0);
        cat2Item1.put("description", "cat2Item1 for testing");

        testCategory1.add(cat1Item1);
        testCategory1.add(cat1Item2);
        testCategory2.add(cat2Item1);

        menuTest.put("testCategory1", testCategory1);
        menuTest.put("testCategory2", testCategory2);

        // write the simulated menu json file
        try {
            FileWriter file = new FileWriter(expOutputJsonPath);
            file.write(menuTest.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // get the actual output
        HashMap<String, JSONArray> actualOutput = jm.getMenuInfo(expOutputJsonPath);
        
        // check if the actual output contains the expected menu info
        Assertions.assertEquals(actualOutput.size(), 2);
        Assertions.assertNotNull(actualOutput.get("testCategory1"));
        Assertions.assertNotNull(actualOutput.get("testCategory2"));
        Assertions.assertEquals(actualOutput.get("testCategory1").size(), 2);
        Assertions.assertEquals(actualOutput.get("testCategory2").size(), 1);
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory1").get(0)).get("name"), "cat1Item1");
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory1").get(0)).get("price"), 1.0);
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory1").get(0)).get("description"), "cat1Item1 for testing");
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory1").get(1)).get("name"), "cat1Item2");
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory1").get(1)).get("price"), 2.0);
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory1").get(1)).get("description"), "cat1Item2 for testing");
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory2").get(0)).get("name"), "cat2Item1");
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory2").get(0)).get("price"), 3.0);
        Assertions.assertEquals(((JSONObject) actualOutput.get("testCategory2").get(0)).get("description"), "cat2Item1 for testing");
    }

    @Test
    public void testAddNewOrderValid() {
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
        
        // add a new order
        Order newOrder = new Order("ORD20230911123456", 
        LocalDate.parse("2023-09-11", DateTimeFormatter.ofPattern("yyyy-MM-dd")), 
        new HashMap<String, Integer>() {
            {
                put("testItem1", 1);
                put("testItem2", 3);
            }
        }, 
        199);
        jm.addNewOrderToJson(newOrder, expOutputJsonPath);

        // get the actual output
        ArrayList<Order> actualOutput = jm.getOrderHistory(expOutputJsonPath);

        // check if the actual output contains the expected order history
        Assertions.assertEquals(actualOutput.size(), 2);        
    }

}
